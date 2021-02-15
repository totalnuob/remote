package kz.nicnbk.service.api.notification;

import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.notification.NotificationDto;

import java.util.List;

public interface NotificationService extends BaseService{

    public static final String EMAIL_HEADER = "UNIC: Notification";

    boolean save(NotificationDto notificationDto);

    boolean createInAppAndEmailNotification(NotificationDto notificationDto);

    List<NotificationDto> getUserNotificationsAll(Long employeeId);

    List<NotificationDto> getUserNotificationsOpen(Long employeeId);

    boolean closeUserNotification(Long employeeId, Long notificationId);

    boolean closeUserNotificationsAll(Long employeeId);

}
