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
    private ResponseMessageDto warningMessage;

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

    public boolean isStatusOK(){
        return this.status != null && this.status.getCode().equalsIgnoreCase(ResponseStatusType.SUCCESS.getCode());
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

    public void setWarningMessageEn(String messageEn){
        if(this.warningMessage == null){
            this.warningMessage = new ResponseMessageDto();
        }
        this.warningMessage.setNameEn(messageEn);
    }

    public void setWarningMessageRu(String messageRu){
        if(this.warningMessage == null){
            this.warningMessage = new ResponseMessageDto();
        }
        this.warningMessage.setNameRu(messageRu);
    }

    public void setWarningMessageKz(String messageKz){
        if(this.warningMessage == null){
            this.warningMessage = new ResponseMessageDto();
        }
        this.warningMessage.setNameKz(messageKz);
    }

    public void setErrorMessageEn(String messageEn){
        this.setStatus(ResponseStatusType.FAIL);
        this.setMessageEn(messageEn);
    }

    public void appendErrorMessageEn(String message){
        String existingMessage = getErrorMessageEn() != null ? getErrorMessageEn() : "";
        setErrorMessageEn(existingMessage + " " + message);
        setStatus(ResponseStatusType.FAIL);
    }
    public void appendWarningMessageEn(String message){
        String existingMessage = getWarningMessageEn() != null ? getWarningMessageEn() : "";
        setWarningMessageEn(existingMessage + " " + message);
    }

    public String getWarningMessageEn(){
        if(this.warningMessage != null){
            return this.warningMessage.getNameEn();
        }
        return null;
    }

    public void setSuccessMessageEn(String messageEn){
        this.setStatus(ResponseStatusType.SUCCESS);
        this.setMessageEn(messageEn);
    }

    public String getErrorMessageEn(){
        if(this.status != null && this.status == ResponseStatusType.FAIL && this.message != null){
            return this.message.getNameEn();
        }
        return null;
    }

    public ResponseMessageDto getWarningMessage() {
        return warningMessage;
    }

    public void setWarningMessage(ResponseMessageDto warningMessage) {
        this.warningMessage = warningMessage;
    }
}
