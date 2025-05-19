package org.vrk.accounting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.vrk.accounting.domain.Item;
import org.vrk.accounting.domain.ItemEmployee;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByCurrentItemEmployee(ItemEmployee itemEmployee);
    List<Item> findAllByResponsible(ItemEmployee itemEmployee);
}
