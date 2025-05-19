package org.vrk.accounting.web;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.vrk.accounting.service.InventoryListService;

@RestController
@RequestMapping("/inventory_list")
@RequiredArgsConstructor
@Tag(name = "Работа с инвентарными описями")
public class InventoryListController {
    private final InventoryListService inventoryListService;
}
