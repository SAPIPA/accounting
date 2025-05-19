package org.vrk.accounting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.vrk.accounting.domain.Application;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
}
