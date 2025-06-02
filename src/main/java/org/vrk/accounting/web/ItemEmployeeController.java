package org.vrk.accounting.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.vrk.accounting.domain.dto.ItemEmployeeDTO;
import org.vrk.accounting.service.EmployeeService;

import java.util.List;
import java.util.UUID;

@SecurityScheme(
        name = "bearerAuth",                          // идентификатор схемы
        type = SecuritySchemeType.HTTP,               // HTTP схема
        scheme = "bearer",                            // Bearer-токен
        bearerFormat = "JWT",                         // формат токена
        in = SecuritySchemeIn.HEADER                  // передаётся в заголовке Authorization
)
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
     */
    @GetMapping("/search")
    @Operation(summary = "Поиск сотрудников по ФИО",
            description = "Возвращает список сотрудников с совпадающим ФИО и тем же " +
                    "workplace или factWorkplace, что и у текущего пользователя.",
            security = @SecurityRequirement(name = "bearerAuth"))
    public List<ItemEmployeeDTO> searchByFullName(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(required = false) String fullName) {
        UUID userId = UUID.fromString(jwt.getClaim("internalGuid"));
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
    public ItemEmployeeDTO getMe(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getClaim("internalGuid"));
        return service.getById(userId);
    }

    /**
     * Получить текущего пользователя + всех коллег по orgeh.
     * Доступно только ROLE_MODERATOR.
     */
    @GetMapping("/colleagues")
    public List<ItemEmployeeDTO> getColleagues(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getClaim("internalGuid"));
        return service.getColleaguesWithSelf(userId);
    }

    /**
     * Обновление пользователя — только ROLE_MODERATOR
     * */
    @PutMapping("/update")
    @Operation(summary = "Обновление пользователя",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ItemEmployeeDTO updateUser(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody ItemEmployeeDTO dto) {
        return service.update(dto);
    }

}
