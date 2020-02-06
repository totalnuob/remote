package kz.nicnbk.repo.api.employee;

import kz.nicnbk.repo.model.employee.Department;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by pak on 06.02.2020
 */
public interface DepartmentRepository extends PagingAndSortingRepository<Department, Integer> {

    @Query("SELECT e FROM Department e")
    List<Department> getAllDepartments();
}
