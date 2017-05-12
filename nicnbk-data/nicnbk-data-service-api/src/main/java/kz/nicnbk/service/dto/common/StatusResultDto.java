package kz.nicnbk.service.dto.common;

/**
 * Created by magzumov on 08.07.2016.
 */
public class StatusResultDto extends MessageResultDto {

    private StatusResultType status;

    public StatusResultType getStatus() {
        return status;
    }

    public void setStatus(StatusResultType status) {
        this.status = status;
    }
}
