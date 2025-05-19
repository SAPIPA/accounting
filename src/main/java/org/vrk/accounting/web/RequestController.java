package org.vrk.accounting.web;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.vrk.accounting.service.ApplicationService;

@RestController
@RequestMapping("/request")
@RequiredArgsConstructor
@Tag(name = "Работа с заявлениями")
public class RequestController {
    private final ApplicationService applicationService;
}
