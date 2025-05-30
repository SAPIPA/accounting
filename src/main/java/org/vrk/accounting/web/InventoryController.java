package org.vrk.accounting.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.vrk.accounting.domain.dto.InventoryDTO;
import org.vrk.accounting.domain.dto.InventoryListDTO;
import org.vrk.accounting.domain.dto.ItemDTO;
import org.vrk.accounting.service.InventoryListService;
import org.vrk.accounting.service.InventoryService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/inventories")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;
    private final InventoryListService inventoryListService;

    /**
     * 1) Подготовить данные для инициации (рис. 6):
     *    GET /api/inventories/prepare-init/{initiatorId}
     *    Возвращает InventoryDTO, в котором заполнены commissionMemberIds + inventoryLists (itemId + isPresent=false).
     *    Поля startDate и responsibleEmployeeId frontend заполнит самостоятельно (по выбору пользователя).
     */
    @GetMapping("/prepare-init/{initiatorId}")
    public ResponseEntity<InventoryDTO> prepareInit(@PathVariable("initiatorId") UUID initiatorId) {
        InventoryDTO dto = inventoryService.prepareInit(initiatorId);
        return ResponseEntity.ok(dto);
    }

    /**
     * 2) Создать новый процесс инвентаризации (кнопка «Отправить» на экране 6):
     *    POST /api/inventories/{initiatorId}
     *    Body: InventoryDTO (с полями startDate, responsibleEmployeeId, commissionMemberIds, inventoryLists).
     *    Возвращает: созданный InventoryDTO (с присвоенным id, без endDate).
     */
    @PostMapping("/{initiatorId}")
    public ResponseEntity<InventoryDTO> createInventory(
            @PathVariable("initiatorId") UUID initiatorId,
            @RequestBody InventoryDTO dto) {

        InventoryDTO created = inventoryService.create(initiatorId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * 3) Получить информацию о конкретной инвентаризации (включая уже заполненные списки, если они есть):
     *    GET /api/inventories/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<InventoryDTO> getById(@PathVariable("id") Long id) {
        InventoryDTO dto = inventoryService.getById(id);
        return ResponseEntity.ok(dto);
    }

    /**
     * 4) Получить список всех инвентаризаций (кратко):
     *    GET /api/inventories
     */
    @GetMapping
    public ResponseEntity<List<InventoryDTO>> listAll() {
        List<InventoryDTO> list = inventoryService.list();
        return ResponseEntity.ok(list);
    }

    /**
     * 5) Обновить существующую инвентаризацию (изменить дату, M.O.L., членов комиссии):
     *    PUT /api/inventories/{id}
     *    Body: InventoryDTO (с новыми полями).
     */
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
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        inventoryService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 7) Получить список ItemDTO для проверки (кнопка «Начать инвентаризацию» – рис. 10):
     *    GET /api/inventories/{id}/items-to-check
     */
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
    @PostMapping("/{id}/results")
    public ResponseEntity<InventoryDTO> saveResults(
            @PathVariable("id") Long id,
            @RequestBody List<InventoryListDTO> lists) {

        InventoryDTO result = inventoryService.saveInventoryResults(id, lists);
        return ResponseEntity.ok(result);
    }
}
