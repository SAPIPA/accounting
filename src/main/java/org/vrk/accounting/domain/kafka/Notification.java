package org.vrk.accounting.domain.kafka;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * Уведомления в системе
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Notification implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * СНИЛС получателя
     */
    private String destinationSnils;
    /**
     * GUID получателя
     */
    private String destinationId;
    /**
     * Источник
     */
    private String source;
    /**
     * ID источника
     */
    private String sourceUuid;
    /**
     * Сообщение
     */
    private String text;
    /**
     * Статус уведомления
     */
    private String status;
    /**
     * Тип уведомления
     */
    private String type;
    /**
     * Дата создания
     */
    private String creationDate;
}
