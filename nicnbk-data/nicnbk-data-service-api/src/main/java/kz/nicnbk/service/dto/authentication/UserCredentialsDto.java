package kz.nicnbk.service.dto.authentication;

import kz.nicnbk.common.service.model.BaseDto;

/**
 * Created by magzumov on 21.02.2017.
 */
public class UserCredentialsDto implements BaseDto {
    private String username;
    private String password;
    private String otp;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
