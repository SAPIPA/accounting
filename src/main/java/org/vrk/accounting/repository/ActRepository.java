package org.vrk.accounting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.vrk.accounting.domain.Act;

@Repository
public interface ActRepository extends JpaRepository<Act, Long> {
}
