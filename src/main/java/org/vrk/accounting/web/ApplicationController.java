package org.vrk.accounting.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.vrk.accounting.domain.dto.ApplicationDTO;
import org.vrk.accounting.service.ApplicationService;
import org.vrk.accounting.util.file.FileStorage;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
@Tag(name = "Работа с заявлениями")
public class ApplicationController {

    private final ApplicationService applicationService;
    private final FileStorage fileStorage;

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

    /**
     * Эндпоинт для загрузки “несвязанной” фотографии.
     * Клиент отправляет multipart/form-data с полем "file".
     * Возвращаем JSON: { "file": "<имя_файла>" }.
     *
     * После этого клиент может положить полученное <имя_файла> в
     * ApplicationDTO.body.{ "file": "<имя_файла>" } при создании заявления.
     */
    @PostMapping(path = "/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Загрузить несвязанную фотографию",
            description = "Сохраняет файл во временную директорию uploads/items/temp/ и возвращает JSON с именем файла.")
    public ResponseEntity<Map<String, String>> uploadStandalonePhoto(
            @RequestPart("file") MultipartFile file
    ) throws IOException {
        // 1) Сохраняем файл в папку uploads/items/temp/
        String filename = fileStorage.storeTemp(file);

        // 2) Возвращаем JSON с ключом "file" — именем сохранённого файла
        return ResponseEntity.ok(Collections.singletonMap("file", filename));
    }

}
