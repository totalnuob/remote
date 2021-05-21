package kz.nicnbk.repo.api.employee;

import kz.nicnbk.repo.model.employee.ResetToken;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ResetTokenRepository extends PagingAndSortingRepository<ResetToken, Long> {
}
