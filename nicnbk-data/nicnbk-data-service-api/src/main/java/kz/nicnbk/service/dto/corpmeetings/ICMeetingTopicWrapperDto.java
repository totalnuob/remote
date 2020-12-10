package kz.nicnbk.service.dto.corpmeetings;

import kz.nicnbk.common.service.model.BaseDto;
import org.springframework.web.multipart.MultipartFile;

public class ICMeetingTopicWrapperDto implements BaseDto {
    private MultipartFile[] files;
    private ICMeetingTopicDto icMeetingTopic;

    public MultipartFile[] getFiles() {
        return files;
    }

    public void setFiles(MultipartFile[] files) {
        this.files = files;
    }

    public ICMeetingTopicDto getIcMeetingTopic() {
        return icMeetingTopic;
    }

    public void setIcMeetingTopic(ICMeetingTopicDto icMeetingTopic) {
        this.icMeetingTopic = icMeetingTopic;
    }
}
