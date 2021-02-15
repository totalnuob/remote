package kz.nicnbk.service.impl.scheduled;

import kz.nicnbk.common.service.util.DateUtils;
import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.service.api.corpmeetings.CorpMeetingService;
import kz.nicnbk.service.api.notification.NotificationService;
import kz.nicnbk.service.dto.corpmeetings.ICMeetingDto;
import kz.nicnbk.service.dto.corpmeetings.ICMeetingTopicDto;
import kz.nicnbk.service.dto.notification.NotificationDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class NotificationGenerationServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(NotificationGenerationServiceImpl.class);
    public static final String JOBNAME = "SYSTEM_USER";

    @Autowired
    private CorpMeetingService corpMeetingService;

    @Autowired
    private NotificationService notificationService;

    @Scheduled(cron="0 00 8 * * *") // Every day at 8 am
    public void createICMeetingNotifications() {
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
}
