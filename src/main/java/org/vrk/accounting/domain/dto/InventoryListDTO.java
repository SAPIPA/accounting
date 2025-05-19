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
public class InventoryListDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * ИД записи описи (при создании null)
     * */
    private Long id;
    /**
     * Связанный инвентаризационный процесс (inventory.id)
     * */
    private Long inventoryId;
    /**
     * Связанное материальное средство (item.id)
     * */
    private Long itemId;
    /**
     * Признак наличия средства
     * */
    private boolean isPresent;
    /**
     * Дополнительный комментарий
     * */
    private String note;
}
