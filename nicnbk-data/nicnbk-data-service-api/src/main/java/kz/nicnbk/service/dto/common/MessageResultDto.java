package kz.nicnbk.service.dto.common;

import kz.nicnbk.common.service.model.BaseDto;

/**
 * Created by magzumov on 08.07.2016.
 */
public class MessageResultDto implements BaseDto {

    private String messageEn;
    private String messageKz;
    private String messageRu;

    public MessageResultDto(){}

    public MessageResultDto(String messageRu, String messageEn, String messageKz){
        this.messageRu = messageRu;
        this.messageEn = messageEn;
        this.messageKz = messageKz;
    }

    public String getMessageEn() {
        return messageEn;
    }

    public void setMessageEn(String messageEn) {
        this.messageEn = messageEn;
    }

    public String getMessageKz() {
        return messageKz;
    }

    public void setMessageKz(String messageKz) {
        this.messageKz = messageKz;
    }

    public String getMessageRu() {
        return messageRu;
    }

    public void setMessageRu(String messageRu) {
        this.messageRu = messageRu;
    }
}
