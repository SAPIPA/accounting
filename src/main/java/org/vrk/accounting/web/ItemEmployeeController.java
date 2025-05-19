package org.vrk.accounting.web;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.vrk.accounting.domain.dto.ItemEmployeeDTO;
import org.vrk.accounting.domain.enums.Role;
import org.vrk.accounting.service.EmployeeService;
import org.vrk.accounting.util.secure.RoleGuard;

import java.util.UUID;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class ItemEmployeeController {

    private final EmployeeService service;

    /** Просмотр своего профиля — любой аутентифицированный (роль не проверяем) */
    @GetMapping("/me")
    public ItemEmployeeDTO getMe(
            @RequestHeader("X-User-Id") UUID userId
    ) {
        return service.getById(userId);
    }

    /** Обновление пользователя — только ROLE_MODERATOR */
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
