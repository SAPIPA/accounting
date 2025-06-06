package org.vrk.accounting.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.vrk.accounting.domain.enums.ItemStatus;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * ИД материального средства (при create — null)
     * */
    private Long id;
    /**
     * Является ли средство личным
     * */
    private Boolean isPersonal;
    /**
     * Наименование основного средства
     * */
    private String name;
    /**
     * Инвентарный номер
     * */
    private String inventoryNumber;
    /**
     * Единица измерения
     * */
    private String measuringUnit;
    /**
     * Количество в единицах измерения
     * */
    private Integer count;
    /**
     * Стоимость
     * */
    private String cost;
    /**
     * Дата поступления
     * */
    private LocalDateTime receiptDate;
    /**
     * ШК-номер
     */
    private String serviceNumber;
    /**
     * Текущий статус
     */
    private ItemStatus status;
    /**
     * ID ответственного сотрудника
     * */
    private UUID responsibleUserId;
    /**
     * ID фактического пользователя (если есть)
     * */
    private UUID currentUserId;
    /**
     * Имя файла (backend) и URL (frontend)
     * */
    private String photoFilename;
    /**
     * Кабинет (office) фактического или ответственного сотрудника.
     */
    private String office;
}
