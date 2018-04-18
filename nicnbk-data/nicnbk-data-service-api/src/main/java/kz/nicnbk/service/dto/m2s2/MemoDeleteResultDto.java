package kz.nicnbk.service.dto.m2s2;

import kz.nicnbk.service.dto.common.StatusResultDto;
import kz.nicnbk.service.dto.common.StatusResultType;

/**
 * Created by Pak on 10/04/2018.
 */
public class MemoDeleteResultDto extends StatusResultDto {

    // It does not make any sense
    private String st;

    public MemoDeleteResultDto (String st, StatusResultType status, String messageRu, String messageEn, String messageKz) {
        super(status, messageRu, messageEn, messageKz);
        this.st = st;
    }

    public String getSt() {
        return st;
    }

    public void setSt(String st) {
        this.st = st;
    }
}
