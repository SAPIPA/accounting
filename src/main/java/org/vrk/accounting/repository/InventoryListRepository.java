package org.vrk.accounting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.vrk.accounting.domain.InventoryList;

import java.util.List;

@Repository
public interface InventoryListRepository extends JpaRepository<InventoryList, Long> {
    List<InventoryList> findByInventory_Id(Long inventoryId);
}
