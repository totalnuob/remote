package kz.nicnbk.repo.api.employee;

import kz.nicnbk.repo.model.employee.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by magzumov on 02.08.2016.
 */
public interface EmployeeRepository extends PagingAndSortingRepository<Employee, Long> {

    Employee findByUsername(String username);

    Employee findByEmail(String email);

    @Query("SELECT e FROM Employee e WHERE" +
            " (:status is null OR e.active=:status)" +
            " AND (:firstName is null OR ((e.firstName is null OR e.firstName='') AND :firstName='') OR LOWER(e.firstName) LIKE LOWER(CONCAT('%', :firstName,'%')))" +
            " AND (:lastName is null OR ((e.lastName is null OR e.lastName='') AND :lastName='') OR LOWER(e.lastName) LIKE LOWER(CONCAT('%', :lastName,'%')))"
    )
    Page<Employee> search(@Param("firstName")String firstName, @Param("lastName")String lastName,
                          @Param("status") Boolean status, Pageable pageable);

    @Query("SELECT e FROM  Employee e " )
    Page<Employee> findAll(Pageable pageable);

    @Query("SELECT e FROM  Employee e WHERE e.active=true" )
    List<Employee> findActiveAll(Sort sort);

    @Query("SELECT e FROM  Employee e WHERE e.active=true AND e.failedLoginAttempts<=?3")
    List<Employee> findActiveAndNoFailedLoginAttempts();

    @Query("SELECT e FROM Employee e JOIN FETCH e.position WHERE e.position.department.id=?1 AND (?2 IS NULL OR e.active=?2)")
    List<Employee> findByPositionDepartmentIdAndActive(int departmentId, Boolean active);

    @Query("SELECT e FROM Employee e WHERE e.active=?1" +
            " AND e.position.code IN ?2"
    )
    List<Employee> findByPositionCodesAndActive(boolean active, String[] positionCodes);
}
