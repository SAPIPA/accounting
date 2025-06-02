package org.vrk.accounting.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.vrk.accounting.domain.enums.Role;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemEmployeeDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * GUID пользователя (при создании null)
     * */
    private UUID id;
    /**
     * SNILS пользователя
     * */
    private String snils;
    /**
     * Роль пользователя в системе
     * */
    private Role role;
    /**
     * Табельный номер
     * */
    private String pernr;
    /**
     * Штатная должность.
     */
    private String plans;
    /**
     * Фамилия.
     */
    private String lastName;
    /**
     * Имя.
     */
    private String firstName;
    /**
     * Отчество.
     */
    private String middleName;
    /**
     * ID рабочего места
     * */
    private String workplaceName;
    /**
     * ID фактического рабочего места
     * */
    private String factWorkplaceName;
    /**
     * Кабинет
     * */
    private String office;
}
