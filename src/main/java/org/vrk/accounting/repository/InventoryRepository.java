package org.vrk.accounting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.vrk.accounting.domain.Inventory;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
}
