package org.vrk.accounting.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.vrk.accounting.domain.Inventory;
import org.vrk.accounting.domain.InventoryList;
import org.vrk.accounting.domain.Item;
import org.vrk.accounting.domain.ItemEmployee;
import org.vrk.accounting.domain.dto.InventoryDTO;
import org.vrk.accounting.domain.dto.InventoryListDTO;
import org.vrk.accounting.domain.dto.ItemDTO;
import org.vrk.accounting.domain.enums.Role;
import org.vrk.accounting.repository.InventoryRepository;
import org.vrk.accounting.repository.ItemEmployeeRepository;
import org.vrk.accounting.repository.ItemRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepo;
    private final ItemEmployeeRepository empRepo;
    private final ItemRepository itemRepo;

    /**
     * Подготовка данных для экрана «Инициация».
     */
    @Transactional
    public InventoryDTO prepareInit(UUID initiatorId) {
        ItemEmployee me = empRepo.findById(initiatorId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + initiatorId));
        String objId = me.getFactWorkplace().getObjId();

        // Члены комиссии
        LinkedHashSet<UUID> commission = new LinkedHashSet<>();
        commission.add(initiatorId);
        empRepo.findByRoleAndFactWorkplace_ObjId(Role.ROLE_COMMISSION_MEMBER, objId)
                .stream()
                .map(ItemEmployee::getId)
                .forEach(commission::add);

        // Начальный список InventoryListDTO по всем Item на том же factWorkplace
        List<InventoryListDTO> lists = itemRepo.findByResponsible_FactWorkplace_ObjId(objId)
                .stream()
                .map(item -> InventoryListDTO.builder()
                        .itemId(item.getId())
                        .isPresent(false)
                        .note(null)
                        .build())
                .collect(Collectors.toList());

        return InventoryDTO.builder()
                .commissionMemberIds(commission)
                .inventoryLists(lists)
                .build();
    }

    /**
     * Создание нового процесса инвентаризации.
     */
    @Transactional
    public InventoryDTO create(UUID initiatorId, InventoryDTO dto) {
        // валидация
        if (dto.getStartDate() == null)
            throw new IllegalArgumentException("Не указана дата старта");
        if (dto.getResponsibleEmployeeId() == null)
            throw new IllegalArgumentException("Не указан M.O.L.");

        // MOL
        ItemEmployee mol = empRepo.findById(dto.getResponsibleEmployeeId())
                .orElseThrow(() -> new IllegalArgumentException("Responsible not found: " + dto.getResponsibleEmployeeId()));

        // собираем Inventory
        Inventory inv = Inventory.builder()
                .startDate(dto.getStartDate())
                .endDate(null)
                .responsibleEmployee(mol)
                .build();

        // члены комиссии
        LinkedHashSet<ItemEmployee> members = new LinkedHashSet<>();
        ItemEmployee initiator = empRepo.findById(initiatorId)
                .orElseThrow(() -> new IllegalArgumentException("Initiator not found: " + initiatorId));
        members.add(initiator);
        if (dto.getCommissionMemberIds() != null) {
            for (UUID uid : dto.getCommissionMemberIds()) {
                if (!uid.equals(initiatorId)) {
                    ItemEmployee e = empRepo.findById(uid)
                            .orElseThrow(() -> new IllegalArgumentException("Commission member not found: " + uid));
                    members.add(e);
                }
            }
        }
        inv.setCommissionMembers(members);

        // предварительные записи описи
        List<InventoryList> items = new ArrayList<>();
        if (dto.getInventoryLists() != null) {
            for (InventoryListDTO l : dto.getInventoryLists()) {
                Item item = itemRepo.findById(l.getItemId())
                        .orElseThrow(() -> new IllegalArgumentException("Item not found: " + l.getItemId()));
                items.add(InventoryList.builder()
                        .inventory(inv)
                        .item(item)
                        .isPresent(l.isPresent())
                        .note(l.getNote())
                        .build());
            }
        }
        inv.setInventoryLists(items);

        // сохранение
        Inventory saved = inventoryRepo.save(inv);
        return toDto(saved);
    }

    /**
     * Получить одну инвентаризацию.
     */
    @Transactional
    public InventoryDTO getById(Long id) {
        return inventoryRepo.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new IllegalArgumentException("Inventory not found: " + id));
    }

    /**
     * Список всех инвентаризаций.
     */
    @Transactional
    public List<InventoryDTO> list() {
        return inventoryRepo.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Обновить метаданные инвентаризации (дату, M.O.L., комиссию).
     */
    @Transactional
    public InventoryDTO update(Long id, InventoryDTO dto) {
        Inventory inv = inventoryRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Inventory not found: " + id));

        if (dto.getStartDate() != null) inv.setStartDate(dto.getStartDate());
        inv.setEndDate(dto.getEndDate());

        if (dto.getResponsibleEmployeeId() != null) {
            ItemEmployee mol = empRepo.findById(dto.getResponsibleEmployeeId())
                    .orElseThrow(() -> new IllegalArgumentException("Responsible not found: " + dto.getResponsibleEmployeeId()));
            inv.setResponsibleEmployee(mol);
        }

        if (dto.getCommissionMemberIds() != null) {
            LinkedHashSet<ItemEmployee> members = dto.getCommissionMemberIds().stream()
                    .map(uid -> empRepo.findById(uid)
                            .orElseThrow(() -> new IllegalArgumentException("Commission member not found: " + uid)))
                    .collect(Collectors.toCollection(LinkedHashSet::new));
            inv.getCommissionMembers().clear();
            inv.getCommissionMembers().addAll(members);
        }

        return toDto(inventoryRepo.save(inv));
    }

    /**
     * Удалить инвентаризацию.
     */
    @Transactional
    public void delete(Long id) {
        if (!inventoryRepo.existsById(id))
            throw new IllegalArgumentException("Inventory not found: " + id);
        inventoryRepo.deleteById(id);
    }

    /**
     * Получить список ItemDTO для проверки.
     */
    @Transactional
    public List<ItemDTO> getItemsToCheck(Long inventoryId) {
        Inventory inv = inventoryRepo.findById(inventoryId)
                .orElseThrow(() -> new IllegalArgumentException("Inventory not found: " + inventoryId));

        if (LocalDateTime.now().isBefore(inv.getStartDate())) {
            throw new IllegalStateException("Ещё рано, старт: " + inv.getStartDate());
        }

        UUID molId = inv.getResponsibleEmployee().getId();

        // служебные
        List<Item> service = itemRepo.findAllByResponsible_Id(molId);
        // личные (фактические)
        List<Item> personal = itemRepo.findAllByCurrentItemEmployee_Id(molId);

        return Stream.concat(service.stream(), personal.stream())
                .map(this::itemToDto)
                .collect(Collectors.toList());
    }

    /**
     * Сохранить результаты (опись) и закрыть инвентаризацию.
     */
    @Transactional
    public InventoryDTO saveInventoryResults(Long inventoryId, List<InventoryListDTO> results) {
        Inventory inv = inventoryRepo.findById(inventoryId)
                .orElseThrow(() -> new IllegalArgumentException("Inventory not found: " + inventoryId));

        inv.getInventoryLists().clear();
        List<InventoryList> newLists = new ArrayList<>();
        for (InventoryListDTO dto : results) {
            if (!Objects.equals(dto.getInventoryId(), inventoryId)) {
                throw new IllegalArgumentException("Wrong inventoryId in list item: " + dto.getInventoryId());
            }
            Item item = itemRepo.findById(dto.getItemId())
                    .orElseThrow(() -> new IllegalArgumentException("Item not found: " + dto.getItemId()));
            newLists.add(InventoryList.builder()
                    .inventory(inv)
                    .item(item)
                    .isPresent(dto.isPresent())
                    .note(dto.getNote())
                    .build());
        }
        inv.setInventoryLists(newLists);
        inv.setEndDate(LocalDateTime.now());

        return toDto(inventoryRepo.save(inv));
    }

    // --------------------
    // private helpers
    // --------------------

    private InventoryDTO toDto(Inventory inv) {
        Set<UUID> cm = inv.getCommissionMembers().stream()
                .map(ItemEmployee::getId)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        List<InventoryListDTO> lists = inv.getInventoryLists().stream()
                .map(l -> InventoryListDTO.builder()
                        .id(l.getId())
                        .inventoryId(inv.getId())
                        .itemId(l.getItem().getId())
                        .isPresent(l.isPresent())
                        .note(l.getNote())
                        .build())
                .collect(Collectors.toList());

        return InventoryDTO.builder()
                .id(inv.getId())
                .startDate(inv.getStartDate())
                .endDate(inv.getEndDate())
                .responsibleEmployeeId(inv.getResponsibleEmployee().getId())
                .commissionMemberIds(cm)
                .inventoryLists(lists)
                .build();
    }

    private ItemDTO itemToDto(Item item) {
        return ItemDTO.builder()
                .id(item.getId())
                .isPersonal(item.getIsPersonal())
                .name(item.getName())
                .inventoryNumber(item.getInventoryNumber())
                .measuringUnit(item.getMeasuringUnit())
                .count(item.getCount())
                .receiptDate(item.getReceiptDate())
                .serviceNumber(item.getServiceNumber())
                .status(item.getStatus())
                .responsibleUserId(item.getResponsible().getId())
                .currentUserId(item.getCurrentItemEmployee() != null
                        ? item.getCurrentItemEmployee().getId()
                        : null)
                .photoFilename(item.getPhotoFilename())
                .build();
    }
}
