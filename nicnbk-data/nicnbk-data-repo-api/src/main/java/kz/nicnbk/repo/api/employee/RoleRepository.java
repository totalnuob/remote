package kz.nicnbk.repo.api.employee;

import kz.nicnbk.repo.model.employee.Role;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by pak on 05.02.2020
 */
public interface RoleRepository extends PagingAndSortingRepository<Role, Integer> {

    @Query("SELECT e FROM Role e")
    List<Role> getAllRoles();
}
