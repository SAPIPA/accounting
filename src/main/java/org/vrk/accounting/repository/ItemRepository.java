package org.vrk.accounting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.vrk.accounting.domain.Item;

import java.util.List;
import java.util.UUID;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByCurrentItemEmployee_Id(UUID currentUserId);
    List<Item> findAllByResponsible_Id(UUID responsibleUserId);
    /** Все items, у которых responsible.factWorkplace.objId = objId */
    List<Item> findByResponsible_FactWorkplace_ObjId(String objId);

    List<Item> findByNameContainingIgnoreCaseOrInventoryNumberContainingIgnoreCase(
            String namePart,
            String inventoryNumberPart
    );
}
