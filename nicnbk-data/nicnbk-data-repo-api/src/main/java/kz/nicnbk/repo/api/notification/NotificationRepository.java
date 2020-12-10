package kz.nicnbk.repo.api.notification;

import kz.nicnbk.repo.model.notification.Notification;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface NotificationRepository extends PagingAndSortingRepository<Notification, Long> {

    List<Notification> findByEmployeeId(Long employeeId);

    List<Notification> findByEmployeeIdAndClosed(Long employeeId, boolean closed);

    Notification findByIdAndEmployeeId(Long id, Long employeeId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Notification n SET n.closed=true where n.employee.id=?1")
    int closeUserNotifications(Long employeeId);

}
