package org.vrk.accounting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.vrk.accounting.domain.RZDEmployee;

import java.util.List;

@Repository
public interface RZDEmployeeRepository extends JpaRepository<RZDEmployee, String> {
    List<RZDEmployee> findByOrgeh(String orgeh);
}
