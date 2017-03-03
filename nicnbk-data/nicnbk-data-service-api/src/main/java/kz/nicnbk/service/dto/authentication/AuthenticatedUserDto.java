package kz.nicnbk.service.dto.authentication;

import kz.nicnbk.common.service.model.BaseDto;

import java.util.Set;

/**
 * Created by magzumov on 21.02.2017.
 */
public class AuthenticatedUserDto implements BaseDto {

    private String username;
    private Set<String> roles;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public String[] getRolesAsArray(){
        if(this.roles == null){
            return null;
        }
        return this.roles.toArray(new String[this.roles.size()]);
    }
}
