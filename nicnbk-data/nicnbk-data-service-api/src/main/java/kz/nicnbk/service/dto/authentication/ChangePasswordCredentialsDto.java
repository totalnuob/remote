package kz.nicnbk.service.dto.authentication;

import kz.nicnbk.common.service.model.BaseDto;

/**
 * Created by magzumov on 01.03.2017.
 */
public class ChangePasswordCredentialsDto implements BaseDto {

    private String currentPassword;
    private String newPassword;

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
