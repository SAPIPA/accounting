package org.vrk.accounting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.vrk.accounting.domain.ItemEmployee;
import org.vrk.accounting.domain.enums.Role;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface ItemEmployeeRepository extends JpaRepository<ItemEmployee, UUID> {
    ItemEmployee findBySnils(String snils);
    List<ItemEmployee> findBySnilsIn(Collection<String> snilsList);
    /**
     * Найти всех сотрудников с данной ролью и factWorkplace.objId = given objId
     */
    List<ItemEmployee> findByRoleAndFactWorkplace_ObjId(Role role, String objId);

    // Поиск ФИО
    @Query("""
        SELECT ie 
        FROM ItemEmployee ie
          JOIN RZDEmployee re 
            ON ie.snils = re.snils
        WHERE LOWER(CONCAT(
            re.lastName, ' ',
            re.firstName, ' ',
            COALESCE(re.midName, '')
        )) LIKE LOWER(CONCAT('%', :fullName, '%'))
        """)
    List<ItemEmployee> findByFullNameContainingIgnoreCase(@Param("fullName") String fullName);
}
