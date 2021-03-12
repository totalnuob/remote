package kz.nicnbk.service.impl.scheduled;

import kz.nicnbk.common.service.util.DateUtils;
import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.service.api.corpmeetings.CorpMeetingService;
import kz.nicnbk.service.api.employee.EmployeeService;
import kz.nicnbk.service.api.notification.NotificationService;
import kz.nicnbk.service.dto.authentication.UserRoles;
import kz.nicnbk.service.dto.corpmeetings.ICMeetingDto;
import kz.nicnbk.service.dto.corpmeetings.ICMeetingTopicAssignmentDto;
import kz.nicnbk.service.dto.corpmeetings.ICMeetingTopicDto;
import kz.nicnbk.service.dto.employee.DepartmentDto;
import kz.nicnbk.service.dto.employee.EmployeeDto;
import kz.nicnbk.service.dto.notification.NotificationDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class NotificationGenerationServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(NotificationGenerationServiceImpl.class);
    public static final String JOBNAME = "SYSTEM_USER";

    @Autowired
    private CorpMeetingService corpMeetingService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private EmployeeService employeeService;

    @Scheduled(cron="0 30 7 * * *") // Every day at 8 am
    public void createICMeetingDeadlineNotifications() {
        try {
            List<ICMeetingDto> icMeetings = this.corpMeetingService.getICMeetingsWithDeadline(new Date());

            if(icMeetings != null && !icMeetings.isEmpty()){
                for(ICMeetingDto icMeeting: icMeetings){
                    if(icMeeting.getTopics() != null && !icMeeting.getTopics().isEmpty()){
                        for(ICMeetingTopicDto topic: icMeeting.getTopics()){
                            if(icMeeting.getDate() != null){
                                String topicName = StringUtils.isNotEmpty(topic.getName()) ?
                                        ("'" + topic.getName().substring(0, Math.min(30,topic.getName().length())) + (topic.getName().length() > 30 ? "...'": "'")) : "";
                                Date deadlineDate = DateUtils.moveDateByDays(icMeeting.getDate(), -CorpMeetingService.IC_MEETING_DEADLINE_DAYS,true);
                                if(deadlineDate != null) {
                                    Date deadLine = DateUtils.getDateWithTime(deadlineDate, CorpMeetingService.IC_MEETING_DEADLINE_HOURS);
                                    if(deadLine != null){
                                        String notificationMessage = "IC Module: IC Meeting Topic '" + topicName + "' will be closed " +
                                                "for editing on " + DateUtils.getDateFormattedWithTime(deadLine) + " (IC Meeting deadline)";
                                        if(topic.getSpeaker() != null) {
                                            NotificationDto notificationDto = new NotificationDto();
                                            notificationDto.setInAppName(notificationMessage);
                                            notificationDto.setEmployee(topic.getSpeaker());
                                            this.notificationService.save(notificationDto);
                                        }
                                        if(topic.getExecutor() != null) {
                                            NotificationDto notificationDto = new NotificationDto();
                                            notificationDto.setInAppName(notificationMessage);
                                            notificationDto.setEmployee(topic.getExecutor());
                                            this.notificationService.save(notificationDto);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        catch (Exception ex) {
            logger.error("Error creating IC Meeting notifications", ex);
        }
    }

    @Scheduled(cron="0 00 8 * * MON-FRI") // Every day at 8 am
    @Transactional
    public void createICAssignmentsDeadlineNotifications() {
        try {
//            if (DateUtils.getDayOfWeek(new Date()) == Calendar.SUNDAY || DateUtils.getDayOfWeek(new Date()) == Calendar.SATURDAY) {
//                return;
//            }
            List<ICMeetingTopicAssignmentDto> assignments = this.corpMeetingService.getICAssignmentsDueWithinWeek();
            if (assignments != null && !assignments.isEmpty()) {
                for (ICMeetingTopicAssignmentDto assignmentDto: assignments) {
                    if(assignmentDto.getId() != null && assignmentDto.getDepartments() != null && !assignmentDto.getDepartments().isEmpty()) {
                        List<EmployeeDto> icAdmins = this.employeeService.findUsersWithRole(UserRoles.IC_ADMIN.getCode(), true);
                        EmployeeDto icAdmin = icAdmins != null && !icAdmins.isEmpty() ? icAdmins.get(0) : null;
                        for(DepartmentDto departmentDto: assignmentDto.getDepartments()) {
                            if(departmentDto.getId() != null) {
                                List<EmployeeDto> employees = this.employeeService.findByDepartmentAndActive(departmentDto.getId(), true);
                                if(employees != null && !employees.isEmpty()) {
                                    for(EmployeeDto employeeDto: employees) {
                                        if(employeeDto.getPosition().isHead() != null && employeeDto.getPosition().isHead().booleanValue()) {
                                            NotificationDto notificationDto = new NotificationDto();
                                            notificationDto.setInAppName("Reminder: IC Assignment '" + assignmentDto.getName() + "' is due on " + DateUtils.getDateFormatted(assignmentDto.getDateDue()));
                                            notificationDto.setEmailName("Reminder: IC Assignment '" + assignmentDto.getName() + "' is due on " + DateUtils.getDateFormatted(assignmentDto.getDateDue())
                                                    + ". https://unic.nicnbk.kz/#/corpMeetings/assignment/edit/" + assignmentDto.getId().longValue());
                                            notificationDto.setEmployee(employeeDto);
                                            this.notificationService.createInAppAndEmailNotification(notificationDto);
                                            break;
                                        }
                                    }
                                    NotificationDto notificationDto = new NotificationDto();
                                    notificationDto.setInAppName("IC Assignment '" + assignmentDto.getName() + "' is due on " + DateUtils.getDateFormatted(assignmentDto.getDateDue()));
                                    notificationDto.setEmailName("IC Assignment '" + assignmentDto.getName() + "' is due on " + DateUtils.getDateFormatted(assignmentDto.getDateDue())
                                            + ". https://unic.nicnbk.kz/#/corpMeetings/assignment/edit/" + assignmentDto.getId().longValue());
                                    notificationDto.setEmployee(icAdmin);
                                    this.notificationService.createInAppAndEmailNotification(notificationDto);
                                }
                            }
                        }
                    }
                }
            }
        }catch (Exception ex){
            logger.error("Error creating IC Meeting Assignments notifications", ex);
        }
    }
}
