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
import org.vrk.accounting.domain.dto.ActDTO;
import org.vrk.accounting.service.ActService;

import java.io.IOException;

@RestController
@RequestMapping("/api/acts")
@RequiredArgsConstructor
@Tag(name = "Работа с актами")
public class ActController {

    private final ActService actService;

    /**
     * Создать новый акт и сразу скачать файл.
     */
    @Operation(
            summary = "Создать акт и скачать сгенерированный .docx",
            description = "Принимает JSON с данными заявления, генерирует .docx по шаблону и возвращает файл"
    )
    @PostMapping(produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<FileSystemResource> createAct(@RequestBody @Valid ActDTO actDTO) throws IOException {
        FileSystemResource fileResource = new FileSystemResource(actService.createAct(actDTO));
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=act.docx");
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity.ok()
                .headers(headers)
                .body(fileResource);
    }

}
