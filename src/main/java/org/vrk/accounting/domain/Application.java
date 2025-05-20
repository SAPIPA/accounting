package org.vrk.accounting.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.vrk.accounting.domain.enums.ApplicationType;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Заявления в системе.
 */
@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "item_application")
public class Application {
    /**
     * ИД акта.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "item_application_id_seq")
    @SequenceGenerator(name = "item_application_id_seq", sequenceName = "item_application_id_seq", allocationSize = 1)
    private Long id;
    /**
     * Тип заявления в системе.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ApplicationType type;
    /**
     * Содержание самого заявления.
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "body", columnDefinition = "jsonb")
    private Map<String, Object> body;
    /**
     * Дата отправки заявления.
     */
    @Column(name = "start_date")
    private LocalDateTime sendDate;
    /**
     * Путь к файлу в файловой системе.
     */
    @Column(name = "file_path", columnDefinition = "text")
    private String filePath;
}
