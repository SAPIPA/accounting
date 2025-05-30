package org.vrk.accounting.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.vrk.accounting.domain.dto.ItemEmployeeDTO;
import org.vrk.accounting.domain.enums.Role;
import org.vrk.accounting.service.EmployeeService;
import org.vrk.accounting.util.secure.RoleGuard;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@Tag(name = "Работа с пользователями", description = "Включает все c информацией пользователей")
public class ItemEmployeeController {

    private final EmployeeService service;

    /**
     * Поиск сотрудников по ФИО, ограниченный рабочим местом текущего пользователя.
     *
     * @param fullName часть или полный текст ФИО
     * @param userId   UUID текущего пользователя из заголовка
     */
    @GetMapping("/search")
    @Operation(summary = "Поиск сотрудников по ФИО",
            description = "Возвращает список сотрудников с совпадающим ФИО и тем же " +
                    "workplace или factWorkplace, что и у текущего пользователя.")
    public List<ItemEmployeeDTO> searchByFullName(
            @RequestParam(required = false) String fullName,
            @RequestHeader("X-User-Id") UUID userId
    ) {
        return service.findByFullName(fullName, userId);
    }

    /**
     * Просмотр своего профиля — любой аутентифицированный (роль не проверяем)
     * */
    @GetMapping("/employee")
    @Operation(
            summary = "Пользователь по id",
            description = "Позволяет получить пользователя по id"
    )
    public ItemEmployeeDTO getMe(
            @RequestHeader("X-User-Id") UUID userId
    ) {
        return service.getById(userId);
    }

    /**
     * Получить текущего пользователя + всех коллег по orgeh.
     * Доступно только ROLE_MODERATOR.
     */
    @GetMapping("/colleagues")
    public List<ItemEmployeeDTO> getColleagues(
            @RequestHeader("X-User-Id")   UUID currentUserId,
            @RequestHeader("X-User-Role") Role role
    ) {
        RoleGuard.require(role, Role.ROLE_MODERATOR);
        return service.getColleaguesWithSelf(currentUserId);
    }

//    /**
//     * Админ получает всех commission-members, чей factWorkplace.objId совпадает с его.
//     * Заголовки:
//     *   X-User-Id   – UUID администратора
//     *   X-User-Role – ROLE_MODERATOR
//     */
//    @GetMapping("/commission-members")
//    public List<ItemEmployeeDTO> getCommissionMembers(
//            @RequestHeader("X-User-Id")   UUID userId,
//            @RequestHeader("X-User-Role") Role role
//    ) {
//        RoleGuard.require(role, Role.ROLE_MODERATOR);
//        return service.getCommissionMembersByAdmin(userId);
//    }

    /**
     * Обновление пользователя — только ROLE_MODERATOR
     * */
    @PutMapping("/{id}")
    public ItemEmployeeDTO updateUser(
            @PathVariable UUID id,
            @RequestHeader("X-User-Role") Role role,
            @RequestBody ItemEmployeeDTO dto
    ) {
        RoleGuard.require(role, Role.ROLE_MODERATOR);
        return service.update(id, dto);
    }

}
