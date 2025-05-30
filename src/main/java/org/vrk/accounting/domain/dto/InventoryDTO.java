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
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class InventoryDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * ID инвентаризации (null при создании).
     */
    private Long id;

    /**
     * Дата начала инвентаризации (LocalDateTime), отправляем в формате ISO (yyyy-MM-ddTHH:mm).
     */
    private LocalDateTime startDate;

    /**
     * Дата окончания (проставится, когда все записи описи будут сохранены).
     */
    private LocalDateTime endDate;

    /**
     * ID сотрудника-МОЛа, чей склад/имущество проверяем.
     */
    private UUID responsibleEmployeeId;

    /**
     * Список ID сотрудников (UUID), которые входят в состав комиссии.
     */
    private Set<UUID> commissionMemberIds;

    /**
     * Список строк описи (если перечень уже заполнен).
     * При создании новой инвентаризации обычно null или пустой.
     */
    private List<InventoryListDTO> inventoryLists;
}
