package org.vrk.accounting.web;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.vrk.accounting.domain.dto.ItemDTO;
import org.vrk.accounting.service.ItemService;
import org.vrk.accounting.util.file.FileStorage;

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
@RequestMapping("/api/items")
@RequiredArgsConstructor
@Tag(name = "Работа с материальными средствами")
public class ItemController {

    private final ItemService itemService;
    private final FileStorage fileStorage;

    @PostMapping(path = "/{id}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('COMMISSION_MEMBER','MODERATOR', 'USER')")
    @Operation(
            summary = "Загрузить/обновить фото",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ItemDTO uploadPhoto(
            @PathVariable Long id,
            @RequestPart("file") MultipartFile file) {
        // сохраняем файл и получаем только имя
        String filename = fileStorage.store(file, id);
        // обновляем у Item поле photoFilename
        return itemService.updatePhotoFilename(id, filename);
    }

    /**
     * Скачать фото
     * */
    @GetMapping("/{id}/photo")
    @PreAuthorize("hasAnyRole('COMMISSION_MEMBER','MODERATOR', 'USER')")
    @Operation(
            summary = "Скачать фото",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Resource> servePhoto(@PathVariable Long id) {
        ItemDTO dto = itemService.getItemById(id);
        if (dto.getPhotoFilename() == null) {
            return ResponseEntity.notFound().build();
        }
        Resource file = fileStorage.load(id, dto.getPhotoFilename());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }

    @PreAuthorize("hasAnyRole('COMMISSION_MEMBER','MODERATOR', 'USER')")
    @Operation(
            summary = "Пользователь получает свои материальные средства",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/my")
    public List<ItemDTO> getMyItems(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        return itemService.getItemsByCurrentUser(userId);
    }

    @PreAuthorize("hasAnyRole('COMMISSION_MEMBER','MODERATOR', 'USER')")
    @Operation(
            summary = "Простой поиск",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/search")
    public ResponseEntity<List<ItemDTO>> searchItems(
            @RequestParam("q") String query) {
        List<ItemDTO> result = itemService.searchItems(query);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/all")
    @Operation(summary = "Получить все вещи МОЛу на балансе")
    public List<ItemDTO> getAllItems(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        return itemService.getItemsByAdmin(userId);
    }

    @PutMapping("/{id}")
    public ItemDTO updateItem(
            @PathVariable Long id,
            @RequestBody ItemDTO dto) {
        return itemService.updateItem(id, dto);
    }

}
