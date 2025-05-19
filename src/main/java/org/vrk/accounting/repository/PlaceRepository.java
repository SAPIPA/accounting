package org.vrk.accounting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.vrk.accounting.domain.Place;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {
    Place findByObjId(String id);
}
