package org.vrk.accounting.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class InventoryDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * ИД инвентаризации. При создании оставить null
     * */
    private Long id;
    /**
     * Дата начала инвентаризации
     * */
    private LocalDateTime startDate;
    /**
     * Дата окончания инвентаризации
     * */
    private LocalDateTime endDate;
    /**
     * Список ID сотрудников-членов комиссии.
     * При создании — передаётся список существующих user_id.
     */
    private Set<UUID> commissionMemberIds;
}
