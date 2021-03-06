package kz.nicnbk.service.impl.notification;

import kz.nicnbk.repo.api.notification.NotificationRepository;
import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.repo.model.notification.Notification;
import kz.nicnbk.service.api.email.EmailService;
import kz.nicnbk.service.api.employee.EmployeeService;
import kz.nicnbk.service.api.notification.NotificationService;
import kz.nicnbk.service.converter.notification.NotificationEntityConverter;
import kz.nicnbk.service.dto.employee.EmployeeDto;
import kz.nicnbk.service.dto.notification.NotificationDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationEntityConverter notificationEntityConverter;

    @Autowired
    private EmailService emailService;

    @Autowired
    private EmployeeService employeeService;

    @Override
    public boolean save(NotificationDto notificationDto) {
        try{
            if(notificationDto != null && notificationDto.getEmployee().getId() != null){
                Notification notification = new Notification();
                notification.setName(notificationDto.getInAppName());
                notification.setEmployee(new Employee(notificationDto.getEmployee().getId()));
                this.notificationRepository.save(notification);
                return true;
            }
        }catch (Exception ex){
            logger.error("Error creating notification", ex);
        }
        return false;
    }

    @Override
    public boolean createInAppAndEmailNotification(NotificationDto notificationDto){
        try {
            boolean saved = save(notificationDto);
            if(saved) {
                if(notificationDto != null && notificationDto.getEmployee() != null && notificationDto.getEmployee().getId() != null){
                    EmployeeDto employeeDto = this.employeeService.getEmployeeById(notificationDto.getEmployee().getId());
                    if (employeeDto.getEmail() != null) {
                        String subject = EMAIL_HEADER;
                        this.emailService.sendMail(employeeDto.getEmail(), subject, notificationDto.getEmailName());
                    }
                    return true;
                }
            }
        }catch (Exception ex){
            logger.error("Notification failed with exception", ex);
        }
        return false;
    }


    @Override
    public List<NotificationDto> getUserNotificationsAll(Long employeeId) {
        List<NotificationDto> notifications = new ArrayList<>();
        if(employeeId != null){
            List<Notification> entities = this.notificationRepository.findByEmployeeId(employeeId);
            if(entities != null){
                for(Notification entity: entities){
                    NotificationDto dto = this.notificationEntityConverter.disassemble(entity);
                    notifications.add(dto);
                }
            }
        }
        return notifications;
    }

    @Override
    public List<NotificationDto> getUserNotificationsOpen(Long employeeId) {
        List<NotificationDto> notifications = new ArrayList<>();
        if(employeeId != null){
            List<Notification> entities = this.notificationRepository.findByEmployeeIdAndClosed(employeeId, false);
            if(entities != null){
                for(Notification entity: entities){
                    NotificationDto dto = this.notificationEntityConverter.disassemble(entity);
                    dto.setInAppName(entity.getName());
                    notifications.add(dto);
                }
            }
        }
        return notifications;
    }

    @Override
    public boolean closeUserNotification(Long employeeId, Long notificationId) {
        if(employeeId != null && notificationId != null){
            Notification entity = this.notificationRepository.findByIdAndEmployeeId(notificationId, employeeId);
            if(entity != null) {
                entity.setClosed(true);
                this.notificationRepository.save(entity);
                return true;
            }
        }

        return false;
    }

    @Transactional
    @Override
    public boolean closeUserNotificationsAll(Long employeeId) {
        if(employeeId != null){
            int closed = this.notificationRepository.closeUserNotifications(employeeId);
            return true;
        }

        return false;
    }
}
