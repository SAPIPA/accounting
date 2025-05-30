package org.vrk.accounting.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.vrk.accounting.domain.dto.ActDTO;
import org.vrk.accounting.domain.enums.Role;
import org.vrk.accounting.service.ActService;
import org.vrk.accounting.util.secure.RoleGuard;

@RestController
@RequestMapping("/act")
@RequiredArgsConstructor
@Tag(name = "Работа с актами")
public class ActController {
    private final ActService actService;

    /**
     * Создать новый акт
     * */
    @PostMapping
    @Operation(summary = "Сформировать акт")
    public ActDTO create(
            @RequestHeader("X-User-Role") Role role,
            @RequestBody ActDTO dto
    ) {
        RoleGuard.require(role, Role.ROLE_USER);
        return actService.createAct(dto);
    }
}
