package kz.nicnbk.service.dto.corpmeetings;

import kz.nicnbk.common.service.model.BaseDto;
import kz.nicnbk.common.service.util.DateUtils;
import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.service.api.tripmemo.TripMemoService;
import org.springframework.format.annotation.DateTimeFormat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class ICMeetingVoteDto implements BaseDto {
    private Long icMeetingId;
    private List<ICMeetingTopicsVoteDto> votes;

    public Long getIcMeetingId() {
        return icMeetingId;
    }

    public void setIcMeetingId(Long icMeetingId) {
        this.icMeetingId = icMeetingId;
    }

    public List<ICMeetingTopicsVoteDto> getVotes() {
        return votes;
    }

    public void setVotes(List<ICMeetingTopicsVoteDto> votes) {
        this.votes = votes;
    }
}

