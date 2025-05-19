package org.vrk.accounting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.vrk.accounting.domain.Item;
import org.vrk.accounting.domain.ItemEmployee;

import java.util.List;
import java.util.UUID;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByCurrentItemEmployee_Id(UUID currentUserId);
    List<Item> findAllByResponsible(ItemEmployee itemEmployee);
}
