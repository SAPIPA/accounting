package org.vrk.accounting.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.vrk.accounting.domain.Application;
import org.vrk.accounting.domain.dto.ActDTO;
import org.vrk.accounting.domain.dto.ApplicationDTO;
import org.vrk.accounting.domain.kafka.Notification;
import org.vrk.accounting.repository.ApplicationRepository;
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
public class ApplicationService {

    private final ApplicationRepository repo;
    private final FileUtil fileUtil;
    private final NotificationProducer notificationProducer;

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
        dto.setSendDate(LocalDateTime.now());
        Application application = repo.save(toEntity(dto));
        File file = fileUtil.generateApplication(toDto(application));
        application.setFilePath(file.getAbsolutePath());
        application = repo.save(application);
        Notification notification = buildNotificationForApplication(toDto(application));
        notificationProducer.sendNotification(notification);
        return file;
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

    private Notification buildNotificationForApplication(ApplicationDTO dto) {
        String destinationSnils = "01234567899";
        String destinationId = "fc6196df-061c-4553-81a9-6c73e716c5c4";

        String source = "APPLICATION_SERVICE";
        String sourceUuid = dto.getId().toString();

        String text = "Вам поступило заявление на подписание";

        String status = "NEW";
        String type = "INVENTORY_ACT_CREATED";

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

//123


