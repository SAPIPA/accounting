package org.vrk.accounting.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "rzd_employee",
        indexes = {
                @Index(name = "idx_employee_snils",         columnList = "snils"),
                @Index(name = "idx_employee_internal_guid", columnList = "internal_guid"),
                @Index(name = "idx_employee_pernr",         columnList = "ga_pernr")
        }
)
public class RZDEmployee {
    /**
     * Пол работника.
     */
    @Column(name = "pd_sex")
    private Boolean sex;
    /**
     * Почта работника.
     */
    @Column(name = "con_email")
    private String email;
    /**
     * Рабочий телефон работника.
     */
    @Column(name = "con_work_phone")
    private String workPhone;
    /**
     * Мобильный телефон работника.
     */
    @Column(name = "con_mob_phone")
    private String mobPhone;
    /**
     * Отчество.
     */
    @Column(name = "ga_mid_name")
    private String midName;
    /**
     * Имя.
     */
    @Column(name = "ga_first_name")
    private String firstName;
    /**
     * Идентификатор подразделения. Перечень подразделений ЕКАСУТР предоставляется сервисом по структуре
     */
    @Column(name = "ga_orgeh")
    private String orgeh;
    /**
     * СНИЛС сотрудника
     */
    @Id
    @Column(name = "snils")
    private String snils;
    /**
     * Идентификатор организационной единицы, соотвествующей предприятию работника
     */
    @Column(name = "ga_org_id")
    private String orgId;
    /**
     * Табельный номер работника
     */
    @Column(name = "ga_pernr")
    private String pernr;
    /**
     * Фамилия.
     */
    @Column(name = "ga_last_name")
    private String lastName;
    /**
     * GUID пользователя.
     */
    @Column(name = "internal_guid")
    private UUID internalGuid;
    /**
     * Штатная должность.
     */
    @Column(name = "ga_plans")
    private String plans;
    /**
     * Идентифкатор штатной должности.
     */
    @Column(name = "ga_plans_id")
    private String plansId;

}
