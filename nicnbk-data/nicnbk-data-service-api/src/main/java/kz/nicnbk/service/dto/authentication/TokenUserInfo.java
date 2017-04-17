package kz.nicnbk.service.dto.authentication;

import java.util.Date;
import java.util.Set;

/**
 * Created by magzumov on 21.02.2017.
 */
public class TokenUserInfo {
    private String username;
    private Date issuedAt;
    private String[] roles;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(Date issuedAt) {
        this.issuedAt = issuedAt;
    }

    public String[] getRoles() {
        return roles;
    }

    public void setRoles(String[] roles) {
        this.roles = roles;
    }
}
