package org.vrk.accounting.web;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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

    private static final Logger log = LoggerFactory.getLogger(ItemController.class);
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
     * 2) Эндпоинт для автономной загрузки фото (без привязки к существующему заявлению).
     *    Этот метод может использоваться, если нужно сначала сохранить файл,
     *    получить имя и отправить его в JSON при создании нового ApplicationDTO.
     */
    @PostMapping(path = "/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Загрузить фото (не привязано к заявлению)",
            description = "Сохраняет файл во временную папку uploads/items/0/ и возвращает JSON с именем файла."
    )
    public ResponseEntity<Map<String, String>> uploadStandalonePhoto(
            @RequestPart("file") MultipartFile file) throws IOException {
        // Сохраняем файл с itemId = 0 (или любым условным значением)
        String filename = fileStorage.store(file, 0L);

        // Возвращаем JSON: { "photoFilename": "<filename>" }
        return ResponseEntity.ok(Collections.singletonMap("photoFilename", filename));
    }

    /**
     * Скачать фото
     * */
    @GetMapping("/{id}/photo")
    @PreAuthorize("hasAnyRole('COMMISSION_MEMBER','MODERATOR', 'USER')")
    @Operation(
            summary = "Скачать фото",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Фото (binary)",
                            content = @Content(
                                    mediaType = MediaType.IMAGE_JPEG_VALUE,
                                    schema = @Schema(type = "string", format = "binary")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Фото не найдено"
                    )
            }
    )
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
        UUID userId = UUID.fromString(jwt.getClaim("internalGuid"));
        return itemService.getItemsByCurrentUser(userId);
    }

    @PreAuthorize("hasAnyRole('COMMISSION_MEMBER','MODERATOR', 'USER')")
    @Operation(
            summary = "Пользователь получает свои материальные средства",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/colleague/{id}")
    public List<ItemDTO> getColleagueItems(@PathVariable UUID id) {
        return itemService.getItemsByCurrentUser(id);
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
    @PreAuthorize("hasAnyRole('COMMISSION_MEMBER','MODERATOR', 'USER')")
    @Operation(
            summary = "Получить все вещи МОЛу на балансе",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public List<ItemDTO> getAllItems(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getClaim("internalGuid"));
        return itemService.getItemsByAdmin(userId);
    }

    @PutMapping("/{id}")
    public ItemDTO updateItem(
            @PathVariable Long id,
            @RequestBody ItemDTO dto) {
        return itemService.updateItem(id, dto);
    }

}
