package org.vrk.accounting.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlaceDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * ИД места работы (при создании — null)
     * */
    private Long id;
    /**
     * ИД организационной единицы
     * */
    private String objId;
    /**
     * Наименование организационной единицы
     * */
    private String sText;
    /**
     * Аббревиатура организационной единицы
     * */
    private String ztlc;
    /**
     * Регион организационной единицы
     * */
    private String regio;
    /**
     * Город организационной единицы
     * */
    private String ort01;
    /**
     * Улица организационной единицы
     * */
    private String stras;
    /**
     * Номер дома организационной единицы
     * */
    private String hausn;
}
