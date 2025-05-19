package org.vrk.accounting.web;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.vrk.accounting.service.InventoryService;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
@Tag(name = "Работа с процессом инвентаризация")
public class InventoryController {
    private final InventoryService inventoryService;
}
