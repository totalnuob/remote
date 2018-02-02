package kz.nicnbk.service.dto.common;

/**
 * Created by magzumov on 08.07.2016.
 */
@Deprecated
public class StatusResultDto extends MessageResultDto {

    private StatusResultType status;

    public StatusResultDto(){}

    public StatusResultDto(StatusResultType status, String messageRu, String messageEn, String messageKz){
        super(messageRu, messageEn, messageKz);
        this.status = status;
    }

    public StatusResultType getStatus() {
        return status;
    }

    public void setStatus(StatusResultType status) {
        this.status = status;
    }
}
