package org.vrk.accounting.web;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.vrk.accounting.domain.dto.InventoryDTO;
import org.vrk.accounting.domain.enums.Role;
import org.vrk.accounting.service.InventoryService;
import org.vrk.accounting.util.secure.RoleGuard;

import java.util.UUID;

@RestController
@RequestMapping("/api/inventories")
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryService invService;

    /**
     * Подготовка процесса инвентаризации.
     * Возвращает возможных commission-members и список items.
     */
    @GetMapping("/init")
    public InventoryDTO init(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader("X-User-Role") Role role
    ) {
        RoleGuard.require(role, Role.ROLE_COMMISSION_MEMBER);
        return invService.prepareInit(userId);
    }

    /**
     * Создать новую инвентаризацию по заполненному DTO.
     */
    @PostMapping
    public InventoryDTO create(
            @RequestHeader("X-User-Id")   UUID userId,
            @RequestHeader("X-User-Role") Role role,
            @RequestBody InventoryDTO dto
    ) {
        RoleGuard.require(role, Role.ROLE_COMMISSION_MEMBER);
        return invService.create(userId, dto);
    }
}
