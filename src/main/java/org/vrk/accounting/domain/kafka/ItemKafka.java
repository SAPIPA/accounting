package org.vrk.accounting.domain.kafka;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Данные о материальном средстве
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemKafka implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * Инвентарный номер.
     */
    private String inventoryNumber;
    /**
     * ШК-номер.
     */
    private String serviceNumber;
    /**
     * Наименование основного средства.
     */
    private String name;
    /**
     * Единица измерения.
     */
    private String measuringUnit;
    /**
     * Цена за единицу измерения
     */
    private String cost;
    /**
     * Количество в единицах измерения.
     */
    private Integer count;
    /**
     * Дата поступления.
     */
    private LocalDateTime receiptDate;
    /**
     * СНИЛС материально-ответственного лица.
     */
    private String snils;
}
