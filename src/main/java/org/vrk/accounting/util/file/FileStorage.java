package org.vrk.accounting.util.file;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorage {
    private final Path root = Paths.get("uploads/items");

    /**
     * Сохраняет файл внутри папки uploads/temp/, возвращает уникальное имя.
     */
    public String storeTemp(MultipartFile file) {
        try {
            // Используем отдельную папку “temp” для несвязанных фотографий
            Path folder = root.resolve("temp");
            Files.createDirectories(folder);

            String ext = getExt(file.getOriginalFilename());
            String filename = UUID.randomUUID() + ext;
            Path target = folder.resolve(filename);

            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return filename;
        } catch (IOException e) {
            throw new RuntimeException("Не удалось сохранить временный файл", e);
        }
    }

    public String store(MultipartFile file, Long itemId) {
        try {
            Path folder = root.resolve(itemId.toString());
            Files.createDirectories(folder);
            String ext = getExt(file.getOriginalFilename());
            String filename = UUID.randomUUID() + ext;
            Path target = folder.resolve(filename);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return filename;
        } catch (IOException e) {
            throw new RuntimeException("Не удалось сохранить файл", e);
        }
    }

    public Resource load(Long itemId, String filename) {
        try {
            Path file = root.resolve(itemId.toString()).resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Файл не найден: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Ошибка чтения файла", e);
        }
    }

    private String getExt(String name) {
        int i = name != null ? name.lastIndexOf('.') : -1;
        return (i >= 0 ? name.substring(i) : "");
    }
}
