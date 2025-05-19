package org.vrk.accounting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.vrk.accounting.domain.ItemEmployee;

import java.util.UUID;

@Repository
public interface ItemEmployeeRepository extends JpaRepository<ItemEmployee, UUID> {
    ItemEmployee findBySnils(String snils);
}
