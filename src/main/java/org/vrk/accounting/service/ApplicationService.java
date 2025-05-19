package org.vrk.accounting.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.vrk.accounting.domain.Application;
import org.vrk.accounting.domain.dto.ApplicationDTO;
import org.vrk.accounting.repository.ApplicationRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationService {
    private final ApplicationRepository repo;

    private Application toEntity(ApplicationDTO dto) {
        return Application.builder()
                .id(dto.getId())
                .type(dto.getType())
                .body(dto.getBody())
                .sendDate(dto.getSendDate())
                .build();
    }

    private ApplicationDTO toDto(Application entity) {
        return ApplicationDTO.builder()
                .id(entity.getId())
                .type(entity.getType())
                .body(entity.getBody())
                .sendDate(entity.getSendDate())
                .build();
    }

    /** Создать новое заявление */
    @Transactional
    public ApplicationDTO create(ApplicationDTO dto) {
        Application saved = repo.save(toEntity(dto));
        return toDto(saved);
    }

    /** Получить заявление по ID */
    @Transactional
    public ApplicationDTO getById(Long id) {
        Application app = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Application not found: id=" + id));
        return toDto(app);
    }

    /** Список всех заявлений */
    @Transactional
    public List<ApplicationDTO> list() {
        return repo.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /** Обновить существующее заявление */
    @Transactional
    public ApplicationDTO update(Long id, ApplicationDTO dto) {
        Application existing = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Application not found: id=" + id));
        existing.setType(dto.getType());
        existing.setBody(dto.getBody());
        existing.setSendDate(dto.getSendDate());
        Application updated = repo.save(existing);
        return toDto(updated);
    }

    /** Удалить заявление */
    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new IllegalArgumentException("Application not found: id=" + id);
        }
        repo.deleteById(id);
    }
}
