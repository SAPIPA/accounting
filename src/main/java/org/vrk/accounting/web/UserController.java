package org.vrk.accounting.web;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.vrk.accounting.service.EmployeeService;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "Работа с пользователями")
public class UserController {
    private final EmployeeService employeeService;
}
