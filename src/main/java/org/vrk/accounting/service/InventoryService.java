package org.vrk.accounting.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.vrk.accounting.domain.Inventory;
import org.vrk.accounting.domain.InventoryList;
import org.vrk.accounting.domain.ItemEmployee;
import org.vrk.accounting.domain.dto.InventoryDTO;
import org.vrk.accounting.domain.dto.InventoryListDTO;
import org.vrk.accounting.domain.enums.Role;
import org.vrk.accounting.repository.InventoryRepository;
import org.vrk.accounting.repository.ItemEmployeeRepository;
import org.vrk.accounting.repository.ItemRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository repo;
    private final ItemEmployeeRepository empRepo;
    private final ItemRepository itemRepo;

    /**
     * Подготовка данных для инициации:
     * - все ROLE_COMMISSION_MEMBER с тем же objId factWorkplace
     * - все Item, у которых responsible.factWorkplace.objId тот же
     */
    @Transactional
    public InventoryDTO prepareInit(UUID initiatorId) {
        ItemEmployee me = empRepo.findById(initiatorId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + initiatorId));
        String objId = me.getFactWorkplace().getObjId();

        // commission members
        List<UUID> memberIds = empRepo.findByRoleAndFactWorkplace_ObjId(Role.ROLE_COMMISSION_MEMBER, objId)
                .stream()
                .map(ItemEmployee::getId)
                .distinct()
                .collect(Collectors.toList());
        // make initiator first
        LinkedHashSet<UUID> ordered = new LinkedHashSet<>();
        ordered.add(initiatorId);
        ordered.addAll(memberIds);

        // available items
        List<InventoryListDTO> lists = itemRepo.findByResponsible_FactWorkplace_ObjId(objId)
                .stream()
                .map(item -> InventoryListDTO.builder()
                        .itemId(item.getId())
                        .isPresent(false)
                        .note(null)
                        .build())
                .collect(Collectors.toList());

        return InventoryDTO.builder()
                .commissionMemberIds(ordered)
                .inventoryLists(lists)
                .build();
    }

    private Inventory toEntity(InventoryDTO dto) {
        Set<ItemEmployee> members = new HashSet<>();
        if (dto.getCommissionMemberIds() != null) {
            for (UUID userId : dto.getCommissionMemberIds()) {
                ItemEmployee e = empRepo.findById(userId)
                        .orElseThrow(() -> new IllegalArgumentException(
                                "ItemEmployee not found, id=" + userId));
                members.add(e);
            }
        }
        return Inventory.builder()
                .id(dto.getId())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .commissionMembers(members)
                // inventoryLists загружаются/обрабатываются отдельно при необходимости
                .build();
    }

    private InventoryDTO toDto(Inventory inv) {
        Set<UUID> memberIds = inv.getCommissionMembers().stream()
                .map(ItemEmployee::getId)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        List<InventoryListDTO> listDtos = inv.getInventoryLists().stream()
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
                .commissionMemberIds(memberIds)
                .inventoryLists(listDtos)
                .build();
    }



    /**
     * Создание процесса инвентаризации:
     * - сохраняем Inventory с now() как startDate
     * - привязываем commissionMembers и InventoryList через Cascade.ALL
     */
    @Transactional
    public InventoryDTO create(UUID initiatorId, InventoryDTO dto) {
        Inventory inv = Inventory.builder()
                .startDate(LocalDateTime.now())
                .build();

        // commissionMembers: LinkedHashSet чтобы сохранить порядок
        LinkedHashSet<ItemEmployee> members = new LinkedHashSet<>();
        // initiator first
        members.add(empRepo.findById(initiatorId).orElseThrow());
        // остальные
        for (UUID uid : dto.getCommissionMemberIds()) {
            if (!uid.equals(initiatorId)) {
                members.add(empRepo.findById(uid).orElseThrow());
            }
        }
        inv.setCommissionMembers(members);

        // inventoryLists
        List<InventoryList> invLists = dto.getInventoryLists().stream()
                .map(l -> InventoryList.builder()
                        .inventory(inv)
                        .item(itemRepo.findById(l.getItemId()).orElseThrow(
                                () -> new IllegalArgumentException("Item not found: " + l.getItemId())))
                        .isPresent(l.isPresent())
                        .note(l.getNote())
                        .build())
                .collect(Collectors.toList());
        inv.setInventoryLists(invLists);

        Inventory saved = repo.save(inv);
        return toDto(saved);
    }

    /** Получить инвентаризацию по ID */
    @Transactional
    public InventoryDTO getById(Long id) {
        Inventory inv = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Inventory not found, id=" + id));
        return toDto(inv);
    }

    /** Список всех инвентаризаций */
    @Transactional
    public List<InventoryDTO> list() {
        return repo.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /** Обновить существующую инвентаризацию */
    @Transactional
    public InventoryDTO update(Long id, InventoryDTO dto) {
        Inventory existing = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Inventory not found, id=" + id));
        existing.setStartDate(dto.getStartDate());
        existing.setEndDate(dto.getEndDate());

        Set<ItemEmployee> members = new HashSet<>();
        if (dto.getCommissionMemberIds() != null) {
            for (UUID userId : dto.getCommissionMemberIds()) {
                ItemEmployee e = empRepo.findById(userId)
                        .orElseThrow(() -> new IllegalArgumentException(
                                "ItemEmployee not found, id=" + userId));
                members.add(e);
            }
        }
        existing.getCommissionMembers().clear();
        existing.getCommissionMembers().addAll(members);

        Inventory updated = repo.save(existing);
        return toDto(updated);
    }

    /** Удалить инвентаризацию */
    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new IllegalArgumentException("Inventory not found, id=" + id);
        }
        repo.deleteById(id);
    }
}
