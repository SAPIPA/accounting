package org.vrk.accounting.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.vrk.accounting.domain.Inventory;
import org.vrk.accounting.domain.ItemEmployee;
import org.vrk.accounting.domain.dto.InventoryDTO;
import org.vrk.accounting.repository.InventoryRepository;
import org.vrk.accounting.repository.ItemEmployeeRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository repo;
    private final ItemEmployeeRepository empRepo;

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

    private InventoryDTO toDto(Inventory entity) {
        Set<UUID> ids = entity.getCommissionMembers().stream()
                .map(ItemEmployee::getId)  // UUID
                .collect(Collectors.toSet());
        return InventoryDTO.builder()
                .id(entity.getId())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .commissionMemberIds(ids)
                .build();
    }

    /** Создать новую инвентаризацию */
    @Transactional
    public InventoryDTO create(InventoryDTO dto) {
        Inventory saved = repo.save(toEntity(dto));
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
