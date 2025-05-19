package org.vrk.accounting.web;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.vrk.accounting.service.ActService;

@RestController
@RequestMapping("/act")
@RequiredArgsConstructor
@Tag(name = "Работа с актами")
public class ActController {
    private final ActService actService;
}
