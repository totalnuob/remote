package kz.nicnbk.service.dto.authentication;

import kz.nicnbk.common.service.model.BaseDto;

/**
 * Created by pak on 17.02.2020.
 */
public class MfaRegistrationDto implements BaseDto {
    private String secret;
    private String otp;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
