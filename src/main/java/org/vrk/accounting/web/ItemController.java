package org.vrk.accounting.web;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.vrk.accounting.domain.dto.ItemDTO;
import org.vrk.accounting.domain.enums.Role;
import org.vrk.accounting.service.ItemService;
import org.vrk.accounting.util.secure.RoleGuard;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/my")
    public List<ItemDTO> getMyItems(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader("X-User-Role") Role role
    ) {
        RoleGuard.require(role, Role.ROLE_USER, Role.ROLE_MODERATOR);
        return itemService.getItemsByCurrentUser(userId);
    }

    @GetMapping("/all")
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
