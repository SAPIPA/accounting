package org.vrk.accounting.domain.kafka;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

/**
 * Заявления в системе
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Application implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * Идентификатор записи
     */
    private UUID id;
    /**
     * uuid заявления
     */
    private UUID externalId;
    /**
     * Тип заявления
     */
    private String template;
    /**
     * Статус заявления
     */
    private String status;
    /**
     * Табельный номер
     */
    private String employeeId;
    /**
     * GUID пользователя
     */
    private String employeeGuid;
    /**
     * GUID подписанта
     */
    private String signatoryId;

}
