package org.vrk.accounting.web;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.vrk.accounting.domain.dto.ItemDTO;
import org.vrk.accounting.domain.enums.Role;
import org.vrk.accounting.service.ItemService;
import org.vrk.accounting.util.file.FileStorage;
import org.vrk.accounting.util.secure.RoleGuard;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
@Tag(name = "Работа с материальными средствами")
public class ItemController {
    private final ItemService itemService;
    private final FileStorage fileStorage;

    /** Загрузить/обновить фото */
    @PostMapping(path = "/{id}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ItemDTO uploadPhoto(
            @RequestHeader("X-User-Role") Role role,
            @PathVariable Long id,
            @RequestPart("file") MultipartFile file
    ) {
        RoleGuard.require(role, Role.ROLE_MODERATOR);
        // сохраняем файл и получаем только имя
        String filename = fileStorage.store(file, id);
        // обновляем у Item поле photoFilename
        return itemService.updatePhotoFilename(id, filename);
    }

    /** Скачать фото */
    @GetMapping("/{id}/photo")
    public ResponseEntity<Resource> servePhoto(
            @PathVariable Long id
    ) {
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

    /** Простой поиск без пагинации */
    @GetMapping("/search")
    public ResponseEntity<List<ItemDTO>> searchItems(
            @RequestHeader("X-User-Role") Role role,
            @RequestParam("q") String query
    ) {
        RoleGuard.require(role, Role.ROLE_USER, Role.ROLE_MODERATOR);
        List<ItemDTO> result = itemService.searchItems(query);
        return ResponseEntity.ok(result);
    }

    /**
     * Пользователь получает свои материальные средства
     * */
    @GetMapping("/my")
    public List<ItemDTO> getMyItems(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader("X-User-Role") Role role
    ) {
        RoleGuard.require(role, Role.ROLE_USER, Role.ROLE_MODERATOR);
        return itemService.getItemsByCurrentUser(userId);
    }

    @GetMapping("/all")
    @Operation(summary = "Получить все вещи МОЛу на балансе")
    public List<ItemDTO> getAllItems(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader("X-User-Role") Role role
    ) {
        RoleGuard.require(role, Role.ROLE_MODERATOR);
        return itemService.getItemsByAdmin(userId);
    }

    @PostMapping
    public ItemDTO createItem(
            @RequestHeader("X-User-Role") Role role,
            @RequestBody ItemDTO dto
    ) {
        RoleGuard.require(role, Role.ROLE_MODERATOR);
        return itemService.createItem(dto);
    }

    @PutMapping("/{id}")
    public ItemDTO updateItem(
            @PathVariable Long id,
            @RequestHeader("X-User-Role") Role role,
            @RequestBody ItemDTO dto
    ) {
        RoleGuard.require(role, Role.ROLE_MODERATOR);
        return itemService.updateItem(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteItem(
            @PathVariable Long id,
            @RequestHeader("X-User-Role") Role role
    ) {
        RoleGuard.require(role, Role.ROLE_MODERATOR);
        itemService.deleteItem(id);
    }
}
