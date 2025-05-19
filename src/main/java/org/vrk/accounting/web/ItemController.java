package org.vrk.accounting.web;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.vrk.accounting.domain.ItemEmployee;
import org.vrk.accounting.domain.dto.ItemDTO;
import org.vrk.accounting.service.ItemService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/item")
@RequiredArgsConstructor
@Tag(name = "Работа с вещами")
public class ItemController {

    private final ItemService itemService;

    /**
     * Получить все материалы, где пользователь с данным UUID — currentUser.
     */
    @GetMapping("/my/{userId}")
    public ResponseEntity<List<ItemDTO>> getMyItems(@PathVariable UUID userId) {
        List<ItemDTO> dtos = itemService.getItemsByCurrentUser(userId);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/get/admin")
    public ResponseEntity<?> getAllItemsByAdmin(@RequestBody ItemEmployee itemEmployee) {
        return ResponseEntity.ok(itemService.getItemsByAdmin(itemEmployee));
    }

}
