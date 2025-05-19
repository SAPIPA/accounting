package org.vrk.accounting.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.vrk.accounting.domain.Inventory;
import org.vrk.accounting.domain.InventoryList;
import org.vrk.accounting.domain.Item;
import org.vrk.accounting.domain.dto.InventoryListDTO;
import org.vrk.accounting.repository.InventoryListRepository;
import org.vrk.accounting.repository.InventoryRepository;
import org.vrk.accounting.repository.ItemRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryListService {

    private final InventoryListRepository repo;
    private final InventoryRepository inventoryRepo;
    private final ItemRepository itemRepo;

    private InventoryList toEntity(InventoryListDTO dto) {
        Inventory inventory = inventoryRepo.findById(dto.getInventoryId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Inventory not found, id=" + dto.getInventoryId()));
        Item item = itemRepo.findById(dto.getItemId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Item not found, id=" + dto.getItemId()));

        return InventoryList.builder()
                .id(dto.getId())
                .inventory(inventory)
                .item(item)
                .isPresent(dto.isPresent())
                .note(dto.getNote())
                .build();
    }

    private InventoryListDTO toDto(InventoryList entity) {
        return InventoryListDTO.builder()
                .id(entity.getId())
                .inventoryId(entity.getInventory().getId())
                .itemId(entity.getItem().getId())
                .isPresent(entity.isPresent())
                .note(entity.getNote())
                .build();
    }

    /** Создать новую запись инвентарной описи */
    @Transactional
    public InventoryListDTO create(InventoryListDTO dto) {
        InventoryList saved = repo.save(toEntity(dto));
        return toDto(saved);
    }

    /** Получить запись по ID */
    @Transactional
    public InventoryListDTO getById(Long id) {
        InventoryList entity = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "InventoryList not found, id=" + id));
        return toDto(entity);
    }

    /** Список всех записей описи */
    @Transactional
    public List<InventoryListDTO> list() {
        return repo.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /** Обновить существующую запись */
    @Transactional
    public InventoryListDTO update(Long id, InventoryListDTO dto) {
        InventoryList existing = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "InventoryList not found, id=" + id));

        // Обновляем поля
        existing.setInventory(
                inventoryRepo.findById(dto.getInventoryId())
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Inventory not found, id=" + dto.getInventoryId()))
        );
        existing.setItem(
                itemRepo.findById(dto.getItemId())
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Item not found, id=" + dto.getItemId()))
        );
        existing.setPresent(dto.isPresent());
        existing.setNote(dto.getNote());

        InventoryList updated = repo.save(existing);
        return toDto(updated);
    }

    /** Удалить запись описи */
    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new IllegalArgumentException("InventoryList not found, id=" + id);
        }
        repo.deleteById(id);
    }
}
