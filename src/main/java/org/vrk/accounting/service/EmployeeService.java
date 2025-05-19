package org.vrk.accounting.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.vrk.accounting.domain.ItemEmployee;
import org.vrk.accounting.domain.Place;
import org.vrk.accounting.domain.dto.ItemEmployeeDTO;
import org.vrk.accounting.repository.ItemEmployeeRepository;
import org.vrk.accounting.repository.PlaceRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final ItemEmployeeRepository empRepo;
    private final PlaceRepository placeRepo;

    private ItemEmployee toEntity(ItemEmployeeDTO dto) {
        // Загружаем места по их ID
        Place wp  = placeRepo.findById(dto.getWorkplaceId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Place not found, id=" + dto.getWorkplaceId()));
        Place fwp = placeRepo.findById(dto.getFactWorkplaceId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Place not found, id=" + dto.getFactWorkplaceId()));

        return ItemEmployee.builder()
                .id(dto.getId())
                .snils(dto.getSnils())
                .role(dto.getRole())
                .pernr(dto.getPernr())
                .workplace(wp)
                .factWorkplace(fwp)
                .office(dto.getOffice())
                .build();
        // inventories не маппим – ведётся из других сервисов
    }

    private ItemEmployeeDTO toDto(ItemEmployee entity) {
        return ItemEmployeeDTO.builder()
                .id(entity.getId())
                .snils(entity.getSnils())
                .role(entity.getRole())
                .pernr(entity.getPernr())
                .workplaceId(entity.getWorkplace().getId())
                .factWorkplaceId(entity.getFactWorkplace().getId())
                .office(entity.getOffice())
                .build();
    }

    /** Создать нового пользователя */
    @Transactional
    public ItemEmployeeDTO create(ItemEmployeeDTO dto) {
        ItemEmployee saved = empRepo.save(toEntity(dto));
        return toDto(saved);
    }

    /** Получить пользователя по GUID */
    @Transactional
    public ItemEmployeeDTO getById(UUID id) {
        ItemEmployee e = empRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "ItemEmployee not found, id=" + id));
        return toDto(e);
    }

    /** Список всех пользователей */
    @Transactional
    public List<ItemEmployeeDTO> list() {
        return empRepo.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /** Обновить существующего пользователя */
    @Transactional
    public ItemEmployeeDTO update(UUID id, ItemEmployeeDTO dto) {
        ItemEmployee existing = empRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "ItemEmployee not found, id=" + id));

        existing.setSnils(dto.getSnils());
        existing.setRole(dto.getRole());
        existing.setPernr(dto.getPernr());
        existing.setOffice(dto.getOffice());

        // Обновляем места
        if (!existing.getWorkplace().getId().equals(dto.getWorkplaceId())) {
            Place wp = placeRepo.findById(dto.getWorkplaceId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Place not found, id=" + dto.getWorkplaceId()));
            existing.setWorkplace(wp);
        }
        if (!existing.getFactWorkplace().getId().equals(dto.getFactWorkplaceId())) {
            Place fwp = placeRepo.findById(dto.getFactWorkplaceId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Place not found, id=" + dto.getFactWorkplaceId()));
            existing.setFactWorkplace(fwp);
        }

        ItemEmployee updated = empRepo.save(existing);
        return toDto(updated);
    }

    /** Удалить пользователя */
    @Transactional
    public void delete(UUID id) {
        if (!empRepo.existsById(id)) {
            throw new IllegalArgumentException(
                    "ItemEmployee not found, id=" + id);
        }
        empRepo.deleteById(id);
    }
}
