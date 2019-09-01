package kz.nicnbk.repo.api.employee;

import kz.nicnbk.repo.model.employee.Position;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by magzumov on 02.08.2016.
 */
public interface PositionRepository extends PagingAndSortingRepository<Position, Integer> {

    @Query("SELECT e FROM Position e")
    List<Position> getAllPositions();

    @Query("SELECT e FROM Position e WHERE e.code=?1")
    Position findByCode(String code);
}
