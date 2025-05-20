package org.vrk.accounting.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.vrk.accounting.domain.ItemEmployee;
import org.vrk.accounting.domain.Place;
import org.vrk.accounting.domain.RZDEmployee;
import org.vrk.accounting.domain.dto.ItemEmployeeDTO;
import org.vrk.accounting.repository.ItemEmployeeRepository;
import org.vrk.accounting.repository.PlaceRepository;
import org.vrk.accounting.repository.RZDEmployeeRepository;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final ItemEmployeeRepository empRepo;
    private final RZDEmployeeRepository rzdRepo;
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

    /**
     * Получить текущего пользователя + всех его коллег (same orgeh).
     */
    @Transactional
    public List<ItemEmployeeDTO> getColleaguesWithSelf(UUID currentUserId) {
        // 1) Текущий пользователь
        ItemEmployee me = empRepo.findById(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "User not found: " + currentUserId));
        ItemEmployeeDTO meDto = toDto(me);

        // 2) Вытаскиваем orgeh из вспомогательной таблицы
        String snils = me.getSnils();
        RZDEmployee meta = rzdRepo.findById(snils)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Metadata for SNILS not found: " + snils));
        String orgeh = meta.getOrgeh();

        // 3) Находим всех RZDEmployee с тем же orgeh
        List<RZDEmployee> peersMeta = rzdRepo.findByOrgeh(orgeh);
        Set<String> peerSnils = peersMeta.stream()
                .map(RZDEmployee::getSnils)
                .collect(Collectors.toSet());

        // 4) Достаём из ItemEmployee по snils
        List<ItemEmployee> peers = empRepo.findBySnilsIn(peerSnils);

        // 5) Мапим в DTO
        List<ItemEmployeeDTO> dtos = peers.stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        // 6) Убедимся, что текущий пользователь есть в списке
        boolean containsMe = dtos.stream()
                .anyMatch(d -> d.getId().equals(currentUserId));
        if (!containsMe) {
            dtos.add(meDto);
        }

        return dtos;
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
