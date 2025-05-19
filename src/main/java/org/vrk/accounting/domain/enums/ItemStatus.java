package org.vrk.accounting.domain.enums;
/**
 * Статус материального средства
 */
public enum ItemStatus {
    /**
     * В использовании
     */
    IN_USE,
    /**
     * На обслуживании
     */
    UNDER_SERVICE,
    /**
     * Списано
     */
    WRITTEN_OFF
}
