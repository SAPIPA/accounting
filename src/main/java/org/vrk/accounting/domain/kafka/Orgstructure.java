package org.vrk.accounting.domain.kafka;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * Оргструктура
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Orgstructure implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * ИД организационной единицы.
     */
    private String objId;
    /**
     * Наименование организационной единицы.
     */
    @JsonProperty("sText")
    private String sText;
    /**
     * Аббревиатура организационной единицы.
     */
    private String ztlc;
    /**
     * Регион организационной единицы.
     */
    private String regio;
    /**
     * Город организационной единицы.
     */
    private String ort01;
    /**
     * Улица организационной единицы.
     */
    private String stras;
    /**
     * Номер дома организационной единицы.
     */
    private String hausn;
}
