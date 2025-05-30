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

    private final InventoryListRepository listRepo;
    private final InventoryRepository inventoryRepo;
    private final ItemRepository itemRepo;

    @Transactional
    public InventoryListDTO create(InventoryListDTO dto) {
        Inventory inv = inventoryRepo.findById(dto.getInventoryId())
                .orElseThrow(() -> new IllegalArgumentException("Inventory not found: " + dto.getInventoryId()));
        Item item = itemRepo.findById(dto.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("Item not found: " + dto.getItemId()));

        InventoryList ent = InventoryList.builder()
                .inventory(inv)
                .item(item)
                .isPresent(dto.isPresent())
                .note(dto.getNote())
                .build();

        InventoryList saved = listRepo.save(ent);
        return toDto(saved);
    }

    @Transactional
    public InventoryListDTO getById(Long id) {
        return listRepo.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new IllegalArgumentException("InventoryList not found: " + id));
    }

    @Transactional
    public List<InventoryListDTO> list() {
        return listRepo.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public InventoryListDTO update(Long id, InventoryListDTO dto) {
        InventoryList existing = listRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("InventoryList not found: " + id));

        if (dto.getInventoryId() != null) {
            Inventory inv = inventoryRepo.findById(dto.getInventoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Inventory not found: " + dto.getInventoryId()));
            existing.setInventory(inv);
        }
        if (dto.getItemId() != null) {
            Item item = itemRepo.findById(dto.getItemId())
                    .orElseThrow(() -> new IllegalArgumentException("Item not found: " + dto.getItemId()));
            existing.setItem(item);
        }
        existing.setPresent(dto.isPresent());
        existing.setNote(dto.getNote());

        return toDto(listRepo.save(existing));
    }

    @Transactional
    public void delete(Long id) {
        if (!listRepo.existsById(id)) {
            throw new IllegalArgumentException("InventoryList not found: " + id);
        }
        listRepo.deleteById(id);
    }

    private InventoryListDTO toDto(InventoryList ent) {
        return InventoryListDTO.builder()
                .id(ent.getId())
                .inventoryId(ent.getInventory().getId())
                .itemId(ent.getItem().getId())
                .isPresent(ent.isPresent())
                .note(ent.getNote())
                .build();
    }
}
