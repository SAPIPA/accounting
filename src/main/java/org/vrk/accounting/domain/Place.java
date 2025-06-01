package org.vrk.accounting.domain;

import jakarta.persistence.*;
import lombok.*;

/**
 * Фактическое место работы пользователя.
 */
@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "item_place")
public class Place {
    /**
     * ИД фактического места работы.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "place_id_seq")
    @SequenceGenerator(name = "place_id_seq", sequenceName = "place_id_seq", allocationSize = 1)
    private Long id;
    /**
     * ИД организационной единицы.
     */
    @Column(name = "obj_id")
    private String objId;
    /**
     * Наименование организационной единицы.
     */
    @Column(name = "s_text")
    private String sText;
    /**
     * Аббревиатура организационной единицы.
     */
    @Column(name = "ztlc")
    private String ztlc;
    /**
     * Регион организационной единицы.
     */
    @Column(name = "regio")
    private String regio;
    /**
     * Город организационной единицы.
     */
    @Column(name = "ort01")
    private String ort01;
    /**
     * Улица организационной единицы.
     */
    @Column(name = "stras")
    private String stras;
    /**
     * Номер дома организационной единицы.
     */
    @Column(name = "hausn")
    private String hausn;

}
