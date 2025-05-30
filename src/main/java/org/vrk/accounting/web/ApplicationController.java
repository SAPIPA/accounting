package org.vrk.accounting.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.vrk.accounting.domain.dto.ApplicationDTO;
import org.vrk.accounting.service.ApplicationService;

import java.io.IOException;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
@Tag(name = "Работа с заявлениями")
public class ApplicationController {

    private final ApplicationService applicationService;

    /**
     * Создать новое заявление и сразу скачать файл.
     */
    @Operation(
            summary = "Создать заявление и скачать сгенерированный .docx",
            description = "Принимает JSON с данными заявления, генерирует .docx по шаблону и возвращает файл"
    )
    @PostMapping(produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<FileSystemResource> createApplication(@RequestBody @Valid ApplicationDTO applicationDTO) throws IOException {
        FileSystemResource fileResource = new FileSystemResource(applicationService.create(applicationDTO));
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=application.docx");
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity.ok()
                .headers(headers)
                .body(fileResource);
    }

}
