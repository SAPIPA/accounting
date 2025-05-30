package org.vrk.accounting.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.vrk.accounting.domain.Act;
import org.vrk.accounting.domain.dto.ActDTO;
import org.vrk.accounting.repository.ActRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActService {
    private final ActRepository repo;

    // Вспомогательные мапперы DTO ⇄ Entity
    private Act toEntity(ActDTO dto) {
        return Act.builder()
                .id(dto.getId())
                .type(dto.getType())
                .body(dto.getBody())
                .filePath(dto.getFilePath())
                .build();
    }

    private ActDTO toDto(Act entity) {
        return ActDTO.builder()
                .id(entity.getId())
                .type(entity.getType())
                .body(entity.getBody())
                .filePath(entity.getFilePath())
                .build();
    }

    /** Создать новый акт */
    @Transactional
    public ActDTO createAct(ActDTO dto) {
        Act saved = repo.save(toEntity(dto));
        return toDto(saved);
    }

    /** Получить акт по ID */
    @Transactional
    public ActDTO getActById(Long id) {
        Act act = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Act not found: id=" + id));
        return toDto(act);
    }

    /** Список всех актов */
    @Transactional
    public List<ActDTO> listActs() {
        return repo.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /** Обновить существующий акт */
    @Transactional
    public ActDTO updateAct(Long id, ActDTO dto) {
        Act existing = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Act not found: id=" + id));

        if (dto.getType() != null) {
            existing.setType(dto.getType());
        }
        if (dto.getBody() != null) {
            existing.setBody(dto.getBody());
        }
        if (dto.getFilePath() != null) {
            existing.setFilePath(dto.getFilePath());
        }

        Act updated = repo.save(existing);
        return toDto(updated);
    }

    /** Удалить акт */
    @Transactional
    public void deleteAct(Long id) {
        if (!repo.existsById(id)) {
            throw new IllegalArgumentException("Act not found: id=" + id);
        }
        repo.deleteById(id);
    }
}