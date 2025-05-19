package org.vrk.accounting.web;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.vrk.accounting.service.PlaceService;

@RestController
@RequestMapping("/place")
@RequiredArgsConstructor
@Tag(name = "Работа с рабочим местом")
public class PlaceController {
    private final PlaceService placeService;
}
