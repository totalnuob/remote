package kz.nicnbk.service.dto.common;

import kz.nicnbk.common.service.model.BaseDto;

/**
 * Dto class for response.
 *
 * Created by magzumov.
 */
public class ResponseDto implements BaseDto {

    private ResponseStatusType status;
    private ResponseMessageDto message;

    public ResponseDto(){}

    public ResponseDto(ResponseStatusType status, String messageRu, String messageEn, String messageKz){
        this.status = status;
        if(this.message == null){
            this.message = new ResponseMessageDto();
        }
        this.message.setNameRu(messageRu);
        this.message.setNameEn(messageEn);
        this.message.setNameKz(messageKz);
    }

    public ResponseStatusType getStatus() {
        return status;
    }

    public void setStatus(ResponseStatusType status) {
        this.status = status;
    }

    public ResponseMessageDto getMessage() {
        return message;
    }

    public void setMessage(ResponseMessageDto message) {
        this.message = message;
    }

    public void setMessageEn(String messageEn){
        if(this.message == null){
            this.message = new ResponseMessageDto();
        }
        this.message.setNameEn(messageEn);
    }

    public void setMessageRu(String messageRu){
        if(this.message == null){
            this.message = new ResponseMessageDto();
        }
        this.message.setNameRu(messageRu);
    }

    public void setMessageKz(String messageKz){
        if(this.message == null){
            this.message = new ResponseMessageDto();
        }
        this.message.setNameKz(messageKz);
    }
}
