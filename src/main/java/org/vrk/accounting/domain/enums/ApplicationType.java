package org.vrk.accounting.domain.enums;

/**
 * Типы заявлений.
 */
public enum ApplicationType {
    /**
     * Заявка на списание.
     */
    WRITE_OFF,
    /**
     * Заявка на получение.
     */
    ACQUISITION,
    /**
     * Заявка на внесение.
     */
    ADD,
    /**
     * Заявление на передачу с одного МОЛа на другого
     */
    TRANSFER
}
