package org.vrk.accounting.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.vrk.accounting.domain.enums.ActType;

import java.util.Map;

/**
 * Акты системы.
 */
@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "item_act")
public class Act {
    /**
     * ИД акта.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "item_act_id_seq")
    @SequenceGenerator(name = "item_act_id_seq", sequenceName = "item_act_id_seq", allocationSize = 1)
    private Long id;
    /**
     * Тип акта в системе.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ActType type;
    /**
     * Содержание самого акта.
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "body", columnDefinition = "jsonb")
    private Map<String, Object> body;
    /**
     * Путь к файлу в файловой системе.
     */
    @Column(name = "file_path", columnDefinition = "text")
    private String filePath;
}
