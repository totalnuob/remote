package kz.nicnbk.repo.api.employee;

import kz.nicnbk.repo.model.employee.Employee;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by magzumov on 02.08.2016.
 */
public interface EmployeeRepository extends PagingAndSortingRepository<Employee, Long> {
}
