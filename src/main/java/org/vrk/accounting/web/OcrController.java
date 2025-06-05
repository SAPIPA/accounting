package org.vrk.accounting.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.vrk.accounting.service.OcrService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

@RestController
@RequestMapping("/api/ocr")
@RequiredArgsConstructor
@Tag(name = "Работа с ИИ", description = "Включает все c информацией пользователей")
public class OcrController {

    private final OcrService service;

    @Operation(summary = "Распознавание цифр с изображения")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> recognize(
            @Parameter(
                    description = "Изображение для OCR",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(type = "string", format = "binary")
                    )
            )
            @RequestPart("file") MultipartFile file) {
        try {
            // 1) Считываем в BufferedImage (любой поддерживаемый формат)
            BufferedImage bi = ImageIO.read(file.getInputStream());
            if (bi == null) {
                throw new IOException("Unsupported image format");
            }
            // 2) Передаём в сервис
            String digits = service.extractDigits(bi);

            return ResponseEntity.ok(digits);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("OCR error: " + e.getMessage());
        }
    }
}
