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
 * Пакет по пользователю
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Employee implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * Пол работника.
     */
    private Boolean sex;
    /**
     * Почта работника.
     */
    private String email;
    /**
     * Рабочий телефон работника.
     */
    private String workPhone;
    /**
     * Мобильный телефон работника.
     */
    private String mobPhone;
    /**
     * Отчество.
     */
    private String midName;
    /**
     * Имя.
     */
    private String firstName;
    /**
     * Идентификатор подразделения. Перечень подразделений ЕКАСУТР предоставляется сервисом по структуре
     */
    private String orgeh;
    /**
     * СНИЛС сотрудника
     */
    private String snils;
    /**
     * Идентификатор организационной единицы, соотвествующей предприятию работника
     */
    private String orgId;
    /**
     * Табельный номер работника
     */
    private String pernr;
    /**
     * Фамилия.
     */
    private String lastName;
    /**
     * GUID пользователя.
     */
    private UUID internalGuid;
    /**
     * Штатная должность.
     */
    private String plans;
    /**
     * Идентифкатор штатной должности.
     */
    private String plansId;

}
