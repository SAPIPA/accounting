package org.vrk.accounting.web;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.vrk.accounting.domain.dto.ApplicationDTO;
import org.vrk.accounting.domain.enums.Role;
import org.vrk.accounting.service.ApplicationService;
import org.vrk.accounting.util.secure.RoleGuard;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {
    private final ApplicationService service;

    /** Получить список всех заявлений — только модератор */
    @GetMapping
    public List<ApplicationDTO> listAll(@RequestHeader("X-User-Role") Role role) {
        RoleGuard.require(role, Role.ROLE_MODERATOR);
        return service.listAll();
    }

    /** Получить одно заявление — только модератор */
    @GetMapping("/{id}")
    public ApplicationDTO getOne(
            @RequestHeader("X-User-Role") Role role,
            @PathVariable Long id
    ) {
        RoleGuard.require(role, Role.ROLE_MODERATOR);
        return service.getById(id);
    }

    /** Создать новое заявление — только модератор */
    @PostMapping
    public ApplicationDTO create(
            @RequestHeader("X-User-Role") Role role,
            @RequestBody ApplicationDTO dto
    ) {
        RoleGuard.require(role, Role.ROLE_MODERATOR);
        return service.create(dto);
    }

    /** Обновить заявление — только модератор */
    @PutMapping("/{id}")
    public ApplicationDTO update(
            @RequestHeader("X-User-Role") Role role,
            @PathVariable Long id,
            @RequestBody ApplicationDTO dto
    ) {
        RoleGuard.require(role, Role.ROLE_MODERATOR);
        return service.update(id, dto);
    }

    /** Удалить заявление — только модератор */
    @DeleteMapping("/{id}")
    public void delete(
            @RequestHeader("X-User-Role") Role role,
            @PathVariable Long id
    ) {
        RoleGuard.require(role, Role.ROLE_MODERATOR);
        service.delete(id);
    }
}
