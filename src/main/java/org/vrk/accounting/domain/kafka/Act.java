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

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Act implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * Идентификатор записи
     */
    private UUID id;
    /**
     * uuid акта
     */
    private UUID externalId;
    /**
     * Тип акта
     */
    private String template;
    /**
     * Статус акта
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
