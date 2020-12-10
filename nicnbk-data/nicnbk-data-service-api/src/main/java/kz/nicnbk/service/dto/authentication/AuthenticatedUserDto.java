package kz.nicnbk.service.dto.authentication;

import kz.nicnbk.common.service.model.BaseDto;
import kz.nicnbk.service.dto.employee.PositionDto;

import java.util.Set;

/**
 * Created by magzumov.
 */
public class AuthenticatedUserDto implements BaseDto {

    private String username;
    private Set<String> roles;
    private PositionDto position;

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

    public PositionDto getPosition() {
        return position;
    }

    public void setPosition(PositionDto position) {
        this.position = position;
    }

    public String[] getRolesAsArray(){
        if(this.roles == null){
            return null;
        }
        return this.roles.toArray(new String[this.roles.size()]);
    }
}
