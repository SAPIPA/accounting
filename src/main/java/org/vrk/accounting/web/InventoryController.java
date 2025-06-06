package org.vrk.accounting.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.vrk.accounting.domain.dto.InventoryDTO;
import org.vrk.accounting.domain.dto.InventoryListDTO;
import org.vrk.accounting.domain.dto.ItemDTO;
import org.vrk.accounting.service.InventoryListService;
import org.vrk.accounting.service.InventoryService;
import org.vrk.accounting.util.file.FileUtil;

import java.io.IOException;
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
@RequestMapping("/api/inventories")
@RequiredArgsConstructor
@Tag(name = "Инвентаризация")
public class InventoryController {

    private final InventoryService inventoryService;
    private final FileUtil fileUtil;
    /**
     * 1) Подготовить данные для инициации (рис. 6):
     *    GET /api/inventories/prepare-init/{initiatorId}
     *    Возвращает InventoryDTO, в котором заполнены commissionMemberIds + inventoryLists (itemId + isPresent=false).
     *    Поля startDate и responsibleEmployeeId frontend заполнит самостоятельно (по выбору пользователя).
     */
    @PreAuthorize("hasAnyRole('COMMISSION_MEMBER','MODERATOR', 'USER')")
    @Operation(
            summary = "Загрузить/обновить фото",
            security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/prepare-init")
    public ResponseEntity<InventoryDTO> prepareInit(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getClaim("internalGuid"));
        InventoryDTO dto = inventoryService.prepareInit(userId);
        return ResponseEntity.ok(dto);
    }

    /**
     * 2) Создать новый процесс инвентаризации (кнопка «Отправить» на экране 6):
     *    POST /api/inventories
     *    Body: InventoryDTO (с полями startDate, responsibleEmployeeId, commissionMemberIds, inventoryLists).
     *    Возвращает: созданный InventoryDTO (с присвоенным id, без endDate).
     */
    @PreAuthorize("hasAnyRole('COMMISSION_MEMBER','MODERATOR', 'USER')")
    @Operation(
            security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/create")
    public ResponseEntity<InventoryDTO> createInventory(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody InventoryDTO dto) {
        UUID userId = UUID.fromString(jwt.getClaim("internalGuid"));
        InventoryDTO created = inventoryService.create(userId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * 3) Получить информацию о конкретной инвентаризации (включая уже заполненные списки, если они есть):
     *    GET /api/inventories/{id}
     */
    @PreAuthorize("hasAnyRole('COMMISSION_MEMBER','MODERATOR', 'USER')")
    @Operation(
            security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/{id}")
    public ResponseEntity<InventoryDTO> getById(@PathVariable("id") Long id) {
        InventoryDTO dto = inventoryService.getById(id);
        return ResponseEntity.ok(dto);
    }

    /**
     * 4) Получить список всех инвентаризаций (кратко):
     *    GET /api/inventories
     */
    @PreAuthorize("hasAnyRole('COMMISSION_MEMBER','MODERATOR', 'USER')")
    @Operation(
            security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping
    public ResponseEntity<List<InventoryDTO>> listAll(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getClaim("internalGuid"));
        List<InventoryDTO> list = inventoryService.list(userId);
        return ResponseEntity.ok(list);
    }

    /**
     * 5) Обновить существующую инвентаризацию (изменить дату, M.O.L., членов комиссии):
     *    PUT /api/inventories/{id}
     *    Body: InventoryDTO (с новыми полями).
     */
    @PreAuthorize("hasAnyRole('COMMISSION_MEMBER','MODERATOR', 'USER')")
    @Operation(
            security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/{id}")
    public ResponseEntity<InventoryDTO> update(
            @PathVariable("id") Long id,
            @RequestBody InventoryDTO dto) {

        InventoryDTO updated = inventoryService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    /**
     * 6) Удалить инвентаризацию:
     *    DELETE /api/inventories/{id}
     */
    @PreAuthorize("hasAnyRole('COMMISSION_MEMBER','MODERATOR', 'USER')")
    @Operation(
            security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        inventoryService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 7) Получить список ItemDTO для проверки (кнопка «Начать инвентаризацию» – рис. 10):
     *    GET /api/inventories/{id}/items-to-check
     */
    @PreAuthorize("hasAnyRole('COMMISSION_MEMBER','MODERATOR', 'USER')")
    @Operation(
            security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/{id}/items-to-check")
    public ResponseEntity<List<ItemDTO>> getItemsToCheck(@PathVariable("id") Long id) {
        List<ItemDTO> items = inventoryService.getItemsToCheck(id);
        return ResponseEntity.ok(items);
    }

    /**
     * 8) Сохранить (записать) результаты инвентаризации (кнопка «Сформировать инвентаризационную опись»):
     *    POST /api/inventories/{id}/results
     *    Body: List<InventoryListDTO> (каждый с inventoryId = {id}, itemId, isPresent, note).
     *    Возвращает: InventoryDTO (с уже заполненными inventoryLists и проставленным endDate).
     */
    @PreAuthorize("hasAnyRole('COMMISSION_MEMBER','MODERATOR', 'USER')")
    @Operation(
            security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/{id}/results")
    public ResponseEntity<FileSystemResource> saveResults(
            @PathVariable("id") Long id,
            @RequestBody List<InventoryListDTO> lists
    ) throws IOException {
        InventoryDTO result = inventoryService.saveInventoryResults(id, lists);
        FileSystemResource fileResource = new FileSystemResource(fileUtil.generateInventoryList(result));
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=inventory.docx");
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity.ok()
                .headers(headers)
                .body(fileResource);
    }
}
