package org.vrk.accounting.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.vrk.accounting.domain.dto.InventoryDTO;
import org.vrk.accounting.domain.enums.Role;
import org.vrk.accounting.service.InventoryService;
import org.vrk.accounting.util.secure.RoleGuard;

import java.util.UUID;

@SecurityScheme(
        name = "bearerAuth",                          // идентификатор схемы
        type = SecuritySchemeType.HTTP,               // HTTP схема
        scheme = "bearer",                            // Bearer-токен
        bearerFormat = "JWT",                         // формат токена
        in = SecuritySchemeIn.HEADER                  // передаётся в заголовке Authorization
)
@RestController
@RequestMapping("/api/inventories")
@RequiredArgsConstructor
@Tag(name = "Работы по инвентаризации")
public class InventoryController {
    private final InventoryService invService;

    /**
     * Подготовка процесса инвентаризации.
     * Возвращает возможных commission-members и список items.
     */
    @GetMapping("/init")
    @PreAuthorize("hasAnyRole('COMMISSION_MEMBER','MODERATOR', 'USER')")
    @Operation(
            summary = "Начать инвентаризацию",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public InventoryDTO init(@AuthenticationPrincipal Jwt jwt) {
        // из sub (или любого другого claim) получаем UUID пользователя
        UUID userId = UUID.fromString(jwt.getSubject());
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
