package org.vrk.accounting.web;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.vrk.accounting.domain.ItemEmployee;
import org.vrk.accounting.service.ItemService;

@RestController
@RequestMapping("/item")
@RequiredArgsConstructor
@Tag(name = "Работа с вещами")
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/get/user")
    public ResponseEntity<?> getAllItemsByUser(@RequestBody ItemEmployee itemEmployee) {
        return ResponseEntity.ok(itemService.getAllItemsByUser(itemEmployee));
    }

    @GetMapping("/get/admin")
    public ResponseEntity<?> getAllItemsByAdmin(@RequestBody ItemEmployee itemEmployee) {
        return ResponseEntity.ok(itemService.getItemsByAdmin(itemEmployee));
    }

}
