package org.vrk.accounting.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.vrk.accounting.domain.enums.ActType;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * ИД акта.
     * При создании (POST) оставляется null, БД сама генерирует значение.
     */
    private Long id;
    /**
     * Тип акта в системе.
     * */
    private ActType type;
    /**
     * Содержание акта в виде JSON-структуры.
     * */
    private Map<String, Object> body;
}
