package kz.nicnbk.ws.rest;

import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.service.api.authentication.TokenService;
import kz.nicnbk.service.api.employee.EmployeeService;
import kz.nicnbk.service.api.notification.NotificationService;
import kz.nicnbk.service.dto.employee.EmployeeDto;
import kz.nicnbk.service.dto.notification.NotificationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/notification")
public class NotificationServiceREST extends CommonServiceREST {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private EmployeeService employeeService;

    @RequestMapping(value = "/getUserNotifications", method = RequestMethod.GET)
    public ResponseEntity getUserNotifications() {
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        EmployeeDto employee = this.employeeService.findByUsername(username);

        List<NotificationDto> notifications = this.notificationService.getUserNotificationsOpen(employee.getId());
        return new ResponseEntity<>(notifications, null, HttpStatus.OK);
    }

    @RequestMapping(value = "/closeUserNotification/{id}", method = RequestMethod.POST)
    public ResponseEntity closeUserNotification(@PathVariable Long id) {
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        EmployeeDto employee = this.employeeService.findByUsername(username);

        boolean closed = this.notificationService.closeUserNotification(employee.getId(), id);
        return buildEntitySaveResponseEntity(closed);
    }

    @RequestMapping(value = "/closeUserNotificationsAll", method = RequestMethod.POST)
    public ResponseEntity closeUserNotificationsAll() {
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        EmployeeDto employee = this.employeeService.findByUsername(username);

        boolean closed = this.notificationService.closeUserNotificationsAll(employee.getId());
        return buildEntitySaveResponseEntity(closed);
    }
}
