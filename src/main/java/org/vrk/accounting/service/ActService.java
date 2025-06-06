package org.vrk.accounting.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.vrk.accounting.domain.Act;
import org.vrk.accounting.domain.Inventory;
import org.vrk.accounting.domain.dto.ActDTO;
import org.vrk.accounting.domain.kafka.Notification;
import org.vrk.accounting.repository.ActRepository;
import org.vrk.accounting.service.kafka.Producer.NotificationProducer;
import org.vrk.accounting.util.file.FileUtil;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActService {

    private final ActRepository repo;
    private final FileUtil fileUtil;
    private final NotificationProducer notificationProducer;

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
    public File createAct(ActDTO dto) throws IOException {
        Act act = repo.save(toEntity(dto));
        File file = fileUtil.generateAct(toDto(act));
        act.setFilePath(file.getAbsolutePath());
        act = repo.save(act);
        Notification notification = buildNotificationForAct(toDto(act));
        notificationProducer.sendNotification(notification);
        return file;
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

    private Notification buildNotificationForAct(ActDTO dto) {
        String destinationSnils = "01234567899";
        String destinationId = "fc6196df-061c-4553-81a9-6c73e716c5c4";

        String source = "INVENTORY_SERVICE";
        String sourceUuid = dto.getId().toString();

        String text = "Вам необходимо просмотреть акт";

        String status = "NEW";
        String type   = "ACT_CREATED";

        String creationDate = LocalDateTime.now()
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        return Notification.builder()
                .destinationSnils(destinationSnils)
                .destinationId(destinationId)
                .source(source)
                .sourceUuid(sourceUuid)
                .text(text)
                .status(status)
                .type(type)
                .creationDate(creationDate)
                .build();
    }
}