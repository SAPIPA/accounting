package org.vrk.accounting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.vrk.accounting.domain.Place;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {
    Place findByObjId(String id);
    @Query("SELECT p FROM Place p WHERE p.sText = :text")
    Place findBySText(@Param("text") String text);
}
