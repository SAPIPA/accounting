package org.vrk.accounting.domain.enums;

/**
 * Роль пользователя в системе.
 */
public enum Role {
    /**
     * Обычный пользователь.
     */
    ROLE_USER,
    /**
     * Член комиссии по инвентаризации.
     */
    ROLE_COMMISSION_MEMBER,
    /**
     * Материально-ответсвенное лицо или ответственное лицо.
     */
    ROLE_MODERATOR,
}
