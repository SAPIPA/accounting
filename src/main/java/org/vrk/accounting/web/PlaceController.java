package org.vrk.accounting.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.vrk.accounting.domain.dto.PlaceDTO;
import org.vrk.accounting.service.PlaceService;

import java.util.List;

@RestController
@RequestMapping("/place")
@RequiredArgsConstructor
@Tag(name = "Работа с рабочим местом")
public class PlaceController {

    private final PlaceService placeService;

//    @Operation(summary = "Создать новое место работы")
//    @ApiResponse(responseCode = "201", description = "Место работы успешно создано")
//    @PostMapping
//    public ResponseEntity<PlaceDTO> createPlace(@RequestBody @Valid PlaceDTO dto) {
//        PlaceDTO created = placeService.createPlace(dto);
//        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
//                .path("/{id}")
//                .buildAndExpand(created.getId())
//                .toUri();
//        return ResponseEntity.created(location).body(created);
//    }

    @Operation(summary = "Получить место работы по ID")
    @ApiResponse(responseCode = "200", description = "Место работы найдено")
    @ApiResponse(responseCode = "404", description = "Место работы не найдено")
    @GetMapping("/{id}")
    public ResponseEntity<PlaceDTO> getPlaceById(@PathVariable Long id) {
        PlaceDTO dto = placeService.getPlaceById(id);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Список всех мест работы")
    @ApiResponse(responseCode = "200", description = "Список мест работы")
    @GetMapping
    public ResponseEntity<List<PlaceDTO>> listPlaces() {
        List<PlaceDTO> list = placeService.listPlaces();
        return ResponseEntity.ok(list);
    }

//    @Operation(summary = "Обновить существующее место работы")
//    @ApiResponse(responseCode = "200", description = "Место работы успешно обновлено")
//    @ApiResponse(responseCode = "404", description = "Место работы не найдено")
//    @PutMapping("/{id}")
//    public ResponseEntity<PlaceDTO> updatePlace(
//            @PathVariable Long id,
//            @RequestBody @Valid PlaceDTO dto) {
//        PlaceDTO updated = placeService.updatePlace(id, dto);
//        return ResponseEntity.ok(updated);
//    }

//    @Operation(summary = "Удалить место работы")
//    @ApiResponse(responseCode = "204", description = "Место работы успешно удалено")
//    @ApiResponse(responseCode = "404", description = "Место работы не найдено")
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deletePlace(@PathVariable Long id) {
//        placeService.deletePlace(id);
//        return ResponseEntity.noContent().build();
//    }
}
