package org.vrk.accounting.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.vrk.accounting.domain.enums.ApplicationType;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApplicationDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * ИД заявления. При создании оставляется null
     * */
    private Long id;
    /**
     * Тип заявления в системе
     * */
    private ApplicationType type;
    /**
     * Содержимое заявления (JSON)
     * */
    private Map<String, Object> body;
    /**
     * Дата отправки заявления
     * */
    private LocalDateTime sendDate;
}
