package org.vrk.accounting.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.vrk.accounting.domain.Application;
import org.vrk.accounting.domain.dto.ApplicationDTO;
import org.vrk.accounting.repository.ApplicationRepository;
import org.vrk.accounting.util.file.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository repo;
    private final FileUtil fileUtil;

    // Вспомогательные мапперы DTO ⇄ Entity
    private Application toEntity(ApplicationDTO dto) {
        return Application.builder()
                .id(dto.getId())
                .type(dto.getType())
                .body(dto.getBody())
                .sendDate(dto.getSendDate())
                .filePath(dto.getFilePath())
                .build();
    }

    private ApplicationDTO toDto(Application e) {
        return ApplicationDTO.builder()
                .id(e.getId())
                .type(e.getType())
                .body(e.getBody())
                .sendDate(e.getSendDate())
                .filePath(e.getFilePath())
                .build();
    }


    /** Список всех заявлений */
    @Transactional
    public List<ApplicationDTO> listAll() {
        return repo.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /** Получить конкретное заявление */
    @Transactional
    public ApplicationDTO getById(Long id) {
        Application e = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Заявление не найдено: id=" + id));
        return toDto(e);
    }

    /** Создать новое заявление */
    @Transactional
    public File create(ApplicationDTO dto) throws IOException {
        // TODO: отправить в KAFKA
        Application application = repo.save(toEntity(dto));
        return fileUtil.generateApplication(toDto(application));
    }

    /** Обновить существующее заявление (type и body) */
    @Transactional
    public ApplicationDTO update(Long id, ApplicationDTO dto) {
        Application e = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Заявление не найдено: id=" + id));
        if (dto.getType() != null) {
            e.setType(dto.getType());
        }
        if (dto.getBody() != null) {
            e.setBody(dto.getBody());
        }
        // sendDate не меняем
        Application updated = repo.save(e);
        return toDto(updated);
    }

    /** Удалить заявление */
    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new IllegalArgumentException("Заявление не найдено: id=" + id);
        }
        repo.deleteById(id);
    }
}
