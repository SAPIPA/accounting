package org.vrk.accounting.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.vrk.accounting.domain.Item;
import org.vrk.accounting.domain.ItemEmployee;
import org.vrk.accounting.domain.dto.ItemDTO;
import org.vrk.accounting.repository.ItemEmployeeRepository;
import org.vrk.accounting.repository.ItemRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepo;
    private final ItemEmployeeRepository empRepo;

    private Item toEntity(ItemDTO dto) {
        Item.ItemBuilder builder = Item.builder()
                .id(dto.getId())
                .isPersonal(dto.getIsPersonal())
                .name(dto.getName())
                .inventoryNumber(dto.getInventoryNumber())
                .measuringUnit(dto.getMeasuringUnit())
                .count(dto.getCount())
                .receiptDate(dto.getReceiptDate())
                .serviceNumber(dto.getServiceNumber())
                .status(dto.getStatus())
                // подхватываем имя файла из DTO
                .photoFilename(dto.getPhotoFilename());

        if (dto.getResponsibleUserId() != null) {
            ItemEmployee resp = empRepo.findById(dto.getResponsibleUserId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "ItemEmployee not found, id=" + dto.getResponsibleUserId()));
            builder.responsible(resp);
        }
        if (dto.getCurrentUserId() != null) {
            ItemEmployee cur = empRepo.findById(dto.getCurrentUserId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "ItemEmployee not found, id=" + dto.getCurrentUserId()));
            builder.currentItemEmployee(cur);
        }

        return builder.build();
    }

    private ItemDTO toDto(Item entity) {
        return ItemDTO.builder()
                .id(entity.getId())
                .isPersonal(entity.getIsPersonal())
                .name(entity.getName())
                .inventoryNumber(entity.getInventoryNumber())
                .measuringUnit(entity.getMeasuringUnit())
                .count(entity.getCount())
                .receiptDate(entity.getReceiptDate())
                .serviceNumber(entity.getServiceNumber())
                .status(entity.getStatus())
                .responsibleUserId(
                        entity.getResponsible() != null
                                ? entity.getResponsible().getId()
                                : null)
                .currentUserId(
                        entity.getCurrentItemEmployee() != null
                                ? entity.getCurrentItemEmployee().getId()
                                : null)
                .photoFilename(entity.getPhotoFilename())
                .build();
    }

    /**
     * Поиск всех Item, где name или inventoryNumber содержит подстроку `query`
     */
    @Transactional
    public List<ItemDTO> searchItems(String query) {
        List<Item> list = itemRepo
                .findByNameContainingIgnoreCaseOrInventoryNumberContainingIgnoreCase(query, query);
        return list.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /** Получить все вещи, где данный пользователь — фактический владелец. */
    @Transactional
    public List<ItemDTO> getItemsByCurrentUser(UUID currentUserId) {
        return itemRepo.findAllByCurrentItemEmployee_Id(currentUserId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /** Для модератора: получить все вещи, за которые он отвечает */
    @Transactional
    public List<ItemDTO> getItemsByAdmin(UUID responsibleUserId) {
        ItemEmployee emp = empRepo.findById(responsibleUserId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "ItemEmployee not found, id=" + responsibleUserId));
//        if (emp.getRole() != Role.ROLE_MODERATOR) {
//            throw new IllegalArgumentException("Access denied");
//        }
        return itemRepo.findAllByResponsible_Id(responsibleUserId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /** Создать новый Item */
    @Transactional
    public ItemDTO createItem(ItemDTO dto) {
        Item saved = itemRepo.save(toEntity(dto));
        return toDto(saved);
    }

    /** Получить Item по ID */
    @Transactional
    public ItemDTO getItemById(Long id) {
        Item item = itemRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item not found, id=" + id));
        return toDto(item);
    }

    /** Обновить существующий Item (включая фото) */
    @Transactional
    public ItemDTO updateItem(Long id, ItemDTO dto) {
        Item existing = itemRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item not found, id=" + id));

        existing.setIsPersonal(dto.getIsPersonal());
        existing.setName(dto.getName());
        existing.setInventoryNumber(dto.getInventoryNumber());
        existing.setMeasuringUnit(dto.getMeasuringUnit());
        existing.setCount(dto.getCount());
        existing.setReceiptDate(dto.getReceiptDate());
        existing.setServiceNumber(dto.getServiceNumber());
        existing.setStatus(dto.getStatus());

        // обновляем имя файла, если передано
        if (dto.getPhotoFilename() != null) {
            existing.setPhotoFilename(dto.getPhotoFilename());
        }

        if (dto.getResponsibleUserId() != null) {
            ItemEmployee resp = empRepo.findById(dto.getResponsibleUserId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "ItemEmployee not found, id=" + dto.getResponsibleUserId()));
            existing.setResponsible(resp);
        } else {
            existing.setResponsible(null);
        }
        if (dto.getCurrentUserId() != null) {
            ItemEmployee cur = empRepo.findById(dto.getCurrentUserId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "ItemEmployee not found, id=" + dto.getCurrentUserId()));
            existing.setCurrentItemEmployee(cur);
        } else {
            existing.setCurrentItemEmployee(null);
        }

        Item updated = itemRepo.save(existing);
        return toDto(updated);
    }

    /** Обновить только фото по имени файла */
    @Transactional
    public ItemDTO updatePhotoFilename(Long id, String filename) {
        Item existing = itemRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item not found, id=" + id));
        existing.setPhotoFilename(filename);
        Item updated = itemRepo.save(existing);
        return toDto(updated);
    }

    /** Удалить Item */
    @Transactional
    public void deleteItem(Long id) {
        if (!itemRepo.existsById(id)) {
            throw new IllegalArgumentException("Item not found, id=" + id);
        }
        itemRepo.deleteById(id);
    }
}
