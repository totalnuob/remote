package kz.nicnbk.service.dto.employee;

import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.common.service.model.BaseEntityDto;
import kz.nicnbk.repo.model.employee.ResetToken;

import java.util.Date;

public class ResetTokenDto extends BaseEntityDto<ResetToken> {

    private String token;
    private Date expiryDate;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }
}
