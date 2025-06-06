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
import org.vrk.accounting.repository.InventoryRepository;
import org.vrk.accounting.repository.ItemEmployeeRepository;
import org.vrk.accounting.repository.ItemRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepo;
    private final ItemEmployeeRepository empRepo;
    private final ItemRepository itemRepo;

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    /**
     * Подготовка данных для экрана «Инициация».
     * Теперь возвращаем DTO с единственным председателем комиссии (инициатор).
     */
    @Transactional
    public InventoryDTO prepareInit(UUID initiatorId) {
        ItemEmployee me = empRepo.findById(initiatorId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + initiatorId));

        // По умолчанию председатель комиссии — инициатор
        UUID defaultChairman = initiatorId;

        // Начальный список InventoryListDTO по всем Item на том же factWorkplace
        String objId = me.getFactWorkplace().getObjId();
        List<InventoryListDTO> lists = itemRepo.findByResponsible_FactWorkplace_ObjId(objId)
                .stream()
                .map(item -> InventoryListDTO.builder()
                        .itemId(item.getId())
                        .isPresent(false)
                        .note(null)
                        .build())
                .collect(Collectors.toList());

        return InventoryDTO.builder()
                .commissionChairman(defaultChairman)
                .inventoryLists(lists)
                .build();
    }

    /**
     * Создание нового процесса инвентаризации.
     * Принимаем в DTO только одного commissionChairman.
     */
    @Transactional
    public InventoryDTO create(UUID initiatorId, InventoryDTO dto) {
        dto.setCommissionChairman(initiatorId);
        // Валидация обязательных полей
        if (dto.getStartDate() == null) {
            throw new IllegalArgumentException("Не указана дата старта");
        }
        if (dto.getResponsibleEmployeeId() == null) {
            throw new IllegalArgumentException("Не указан M.O.L.");
        }
        if (dto.getCommissionChairman() == null) {
            throw new IllegalArgumentException("Не указан председатель комиссии");
        }

        // Находим M.O.L.
        ItemEmployee mol = empRepo.findById(dto.getResponsibleEmployeeId())
                .orElseThrow(() -> new IllegalArgumentException("Responsible not found: " + dto.getResponsibleEmployeeId()));

        // Составляем сущность Inventory
        Inventory inv = Inventory.builder()
                .startDate(dto.getStartDate())
                .endDate(null)
                .responsibleEmployee(mol)
                .build();

        // Находим и устанавливаем председателя комиссии
        ItemEmployee chairman = empRepo.findById(dto.getCommissionChairman())
                .orElseThrow(() -> new IllegalArgumentException("Commission chairman not found: " + dto.getCommissionChairman()));
        inv.setCommissionChairman(chairman);

        // Предварительные записи описи (если они пришли в dto)
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

        // Сохраняем и возвращаем DTO
        Inventory saved = inventoryRepo.save(inv);
        return toDto(saved);
    }

    /**
     * Получить одну инвентаризацию по ID.
     */
    @Transactional
    public InventoryDTO getById(Long id) {
        return inventoryRepo.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new IllegalArgumentException("Inventory not found: " + id));
    }

    /**
     * Список всех инвентаризаций пользователя (по ответственному сотруднику).
     */
    @Transactional
    public List<InventoryDTO> list(UUID userId) {
        return inventoryRepo.findAllByCommissionChairman_Id(userId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Обновить метаданные инвентаризации (дату, M.O.L., председателя комиссии).
     */
    @Transactional
    public InventoryDTO update(Long id, InventoryDTO dto) {
        Inventory inv = inventoryRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Inventory not found: " + id));

        // Обновляем дату старта/окончания, если переданы
        if (dto.getStartDate() != null) {
            inv.setStartDate(dto.getStartDate());
        }
        inv.setEndDate(dto.getEndDate());

        // Обновляем M.O.L. (если передали новый)
        if (dto.getResponsibleEmployeeId() != null) {
            ItemEmployee mol = empRepo.findById(dto.getResponsibleEmployeeId())
                    .orElseThrow(() -> new IllegalArgumentException("Responsible not found: " + dto.getResponsibleEmployeeId()));
            inv.setResponsibleEmployee(mol);
        }

        // Обновляем единственного председателя комиссии (если передали новый)
        if (dto.getCommissionChairman() != null) {
            ItemEmployee chairman = empRepo.findById(dto.getCommissionChairman())
                    .orElseThrow(() -> new IllegalArgumentException("Commission chairman not found: " + dto.getCommissionChairman()));
            inv.setCommissionChairman(chairman);
        }

        Inventory updated = inventoryRepo.save(inv);
        return toDto(updated);
    }

    /**
     * Удалить инвентаризацию.
     */
    @Transactional
    public void delete(Long id) {
        if (!inventoryRepo.existsById(id)) {
            throw new IllegalArgumentException("Inventory not found: " + id);
        }
        inventoryRepo.deleteById(id);
    }

    /**
     * Получить список ItemDTO для проверки (описи). Логика не меняется.
     */
    @Transactional
    public List<ItemDTO> getItemsToCheck(Long inventoryId) {
        Inventory inv = inventoryRepo.findById(inventoryId)
                .orElseThrow(() -> new IllegalArgumentException("Inventory not found: " + inventoryId));

        if (LocalDateTime.now().isBefore(inv.getStartDate())) {
            throw new IllegalStateException("Ещё рано, старт: " + inv.getStartDate());
        }

        UUID molId = inv.getResponsibleEmployee().getId();

        List<Item> service = itemRepo.findAllByResponsible_Id(molId).stream()
                .filter(item -> Boolean.FALSE.equals(item.getIsPersonal()))
                .collect(Collectors.toList());

        return service.stream()
                .map(this::itemToDto)
                .collect(Collectors.toList());
    }


    /**
     * Сохранить результаты (опись) и закрыть инвентаризацию.
     * Логика не меняется, просто устанавливается endDate.
     */
    @Transactional
    public InventoryDTO saveInventoryResults(Long inventoryId, List<InventoryListDTO> results) {
        Inventory inv = inventoryRepo.findById(inventoryId)
                .orElseThrow(() -> new IllegalArgumentException("Inventory not found: " + inventoryId));

        // 1) Очищаем существующую коллекцию
        inv.getInventoryLists().clear();

        // 2) Заполняем новой коллекцией
        for (InventoryListDTO dto : results) {
            if (!Objects.equals(dto.getInventoryId(), inventoryId)) {
                throw new IllegalArgumentException(
                        "Wrong inventoryId in list item: " + dto.getInventoryId());
            }
            Item item = itemRepo.findById(dto.getItemId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Item not found: " + dto.getItemId()));
            InventoryList entity = InventoryList.builder()
                    .inventory(inv) // связываем «ребёнок → родитель»
                    .item(item)
                    .isPresent(dto.isPresent())
                    .note(dto.getNote())
                    .build();
            inv.getInventoryLists().add(entity);
        }

        // 3) Устанавливаем дату окончания и сохраняем
        inv.setEndDate(LocalDateTime.now());
        return toDto(inventoryRepo.save(inv));
    }

    // --------------------
    // private helpers
    // --------------------

    /**
     * Преобразуем сущность в DTO, заполняя только одного chairman.
     */
    private InventoryDTO toDto(Inventory inv) {
        UUID chairmanId = inv.getCommissionChairman() != null
                ? inv.getCommissionChairman().getId()
                : null;

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
                .commissionChairman(chairmanId)
                .inventoryLists(lists)
                .build();
    }

    private ItemDTO itemToDto(Item item) {
        // 1) Сначала пробуем взять office из currentItemEmployee
        String officeValue = null;
        if (item.getCurrentItemEmployee() != null) {
            officeValue = item.getCurrentItemEmployee().getOffice();
        }
        // 2) Если currentItemEmployee не задан или у него office == null, берем из responsible
        if (officeValue == null && item.getResponsible() != null) {
            officeValue = item.getResponsible().getOffice();
        }

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
                .responsibleUserId(item.getResponsible() != null
                        ? item.getResponsible().getId()
                        : null)
                .currentUserId(item.getCurrentItemEmployee() != null
                        ? item.getCurrentItemEmployee().getId()
                        : null)
                .photoFilename(item.getPhotoFilename())
                .office(officeValue)   // ← Устанавливаем вычисленный кабинет
                .build();
    }
}

