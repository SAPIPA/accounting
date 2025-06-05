package org.vrk.accounting.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.vrk.accounting.domain.ItemEmployee;
import org.vrk.accounting.domain.Place;
import org.vrk.accounting.domain.RZDEmployee;
import org.vrk.accounting.domain.dto.ItemEmployeeDTO;
import org.vrk.accounting.domain.enums.Role;
import org.vrk.accounting.repository.ItemEmployeeRepository;
import org.vrk.accounting.repository.PlaceRepository;
import org.vrk.accounting.repository.RZDEmployeeRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final ItemEmployeeRepository empRepo;
    private final RZDEmployeeRepository rzdRepo;
    private final PlaceRepository placeRepo;

//    private ItemEmployee toEntity(ItemEmployeeDTO dto) {
//        // Загружаем места по их ID
//        Place wp  = placeRepo.findById(dto.getWorkplaceId())
//                .orElseThrow(() -> new IllegalArgumentException(
//                        "Place not found, id=" + dto.getWorkplaceId()));
//        Place fwp = placeRepo.findById(dto.getFactWorkplaceId())
//                .orElseThrow(() -> new IllegalArgumentException(
//                        "Place not found, id=" + dto.getFactWorkplaceId()));
//
//        return ItemEmployee.builder()
//                .id(dto.getId())
//                .snils(dto.getSnils())
//                .role(dto.getRole())
//                .pernr(dto.getPernr())
//                .workplace(wp)
//                .factWorkplace(fwp)
//                .office(dto.getOffice())
//                .build();
//        // inventories не маппим – ведётся из других сервисов
//    }

    private ItemEmployeeDTO toDto(ItemEmployee entity) {
        String snils = entity.getSnils();

        // 1) Пытаемся загрузить метаданные сотрудника по SNILS
        Optional<RZDEmployee> maybeMeta = rzdRepo.findById(snils);
        if (maybeMeta.isEmpty()) {
            throw new IllegalArgumentException("RZDEmployee metadata not found for SNILS=" + snils);
        }
        RZDEmployee meta = maybeMeta.get();

        // 2) Строим DTO с объединёнными полями
        return ItemEmployeeDTO.builder()
                .id(entity.getId())
                .snils(snils)
                .role(entity.getRole())
                .pernr(entity.getPernr())
                // 2.1) данные из RZDEmployee
                .plans(meta.getPlans())
                .lastName(meta.getLastName())
                .firstName(meta.getFirstName())
                .middleName(meta.getMidName())       // в DTO поле называется middleName, в сущности RZDEmployee – midName
                // 2.2) поля из ItemEmployee
                .workplaceName(entity.getWorkplace().getSText())
                .factWorkplaceName(entity.getFactWorkplace().getSText())
                .office(entity.getOffice())
                .build();
    }


    /**
     * Поиск сотрудников по ФИО, но только тех, у кого workplace или factWorkplace
     * совпадает с текущим пользователем.
     *
     * @param fullName часть или весь текст ФИО
     * @param userId   UUID текущего пользователя из заголовка
     */
    public List<ItemEmployeeDTO> findByFullName(String fullName, UUID userId) {
        // 1. Загружаем текущего пользователя
        ItemEmployee currentUser = empRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "User not found, id=" + userId));

        Long currentWpId  = currentUser.getWorkplace().getId();
        Long currentFwpId = currentUser.getFactWorkplace().getId();

        // 2. Получаем исходный список (все или отфильтрованные по ФИО)
        List<ItemEmployee> list;
        if (fullName == null || fullName.isBlank()) {
            list = empRepo.findAll();
        } else {
            list = empRepo.findByFullNameContainingIgnoreCase(fullName.trim());
        }

        // 3. Отбираем только тех, у кого совпадает workplace или factWorkplace
        return list.stream()
                .filter(e -> {
                    Long wpId  = e.getWorkplace().getId();
                    Long fwpId = e.getFactWorkplace().getId();
                    return currentWpId.equals(wpId) || currentFwpId.equals(fwpId);
                })
                .map(this::toDto)
                .collect(Collectors.toList());
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
     * Для админа: получить всех сотрудников ROLE_COMMISSION_MEMBER,
     * которые работают в той же organizational unit (objId), что и он.
     */
    @Transactional
    public List<ItemEmployeeDTO> getCommissionMembersByAdmin(UUID currentUserId) {
        // 1) Текущий пользователь
        ItemEmployee me = empRepo.findById(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "User not found: " + currentUserId));

        // 2) Берём objId его factWorkplace
        String objId = me.getFactWorkplace().getObjId();

        // 3) Выбираем всех с ролью COMMISSION_MEMBER и тем же objId
        List<ItemEmployee> members = empRepo.findByRoleAndFactWorkplace_ObjId(
                Role.ROLE_COMMISSION_MEMBER, objId);

        // 4) Мапим в DTO
        return members.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
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

    @Transactional
    public List<ItemEmployeeDTO> getMOLS(UUID currentUserId) {
        // 1) Текущий пользователь
        ItemEmployee me = empRepo.findById(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "User not found: " + currentUserId));

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

        // 5) Фильтруем тех, у кого роль ROLE_MODERATOR, и одновременно исключаем текущего по ID
        peers = peers.stream()
                .filter(e -> e.getRole() == Role.ROLE_MODERATOR
                        && !e.getId().equals(currentUserId))
                .collect(Collectors.toList());

        // 6) Мапим в DTO
        List<ItemEmployeeDTO> dtos = peers.stream()
                .map(this::toDto)
                .collect(Collectors.toList());

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
    public ItemEmployeeDTO update(ItemEmployeeDTO dto) {
        ItemEmployee existing = empRepo.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "ItemEmployee not found, id=" + dto.getId()));
        existing.setOffice(dto.getOffice());

        // Обновляем место
        if (!existing.getFactWorkplace().getSText().equals(dto.getFactWorkplaceName())) {
            Place fwp = placeRepo.findBySText(dto.getFactWorkplaceName());
            existing.setFactWorkplace(fwp);
        }
        ItemEmployee updated = empRepo.save(existing);
        return toDto(updated);
    }
}
