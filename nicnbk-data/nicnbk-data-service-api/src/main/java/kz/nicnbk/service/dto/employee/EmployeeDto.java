package kz.nicnbk.service.dto.employee;

import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.common.service.model.BaseEntityDto;
import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.repo.model.employee.Employee;

import java.util.Date;
import java.util.Set;

/**
 * Created by magzumov on 08.07.2016.
 */
public class EmployeeDto extends BaseEntityDto<Employee> {

    private String lastName;
    private String firstName;
    private String patronymic;
    private Date birthDate;
    private PositionDto position;
    private String email;

    private String lastNameRu;
    private String firstNameRu;
    private String patronymicRu;
    private String lastNameRuPossessive;

    private Boolean active;
    private String username;
    private Set<BaseDictionaryDto> roles;

    private Boolean mfaEnabled;
    private Integer failedLoginAttempts;

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Set<BaseDictionaryDto> getRoles() {
        return roles;
    }

    public void setRoles(Set<BaseDictionaryDto> roles) {
        this.roles = roles;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public PositionDto getPosition() {
        return position;
    }

    public void setPosition(PositionDto position) {
        this.position = position;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getMfaEnabled() {
        return mfaEnabled;
    }

    public void setMfaEnabled(Boolean mfaEnabled) {
        this.mfaEnabled = mfaEnabled;
    }

    public String getLastNameRu() {
        return lastNameRu;
    }

    public void setLastNameRu(String lastNameRu) {
        this.lastNameRu = lastNameRu;
    }

    public String getFirstNameRu() {
        return firstNameRu;
    }

    public void setFirstNameRu(String firstNameRu) {
        this.firstNameRu = firstNameRu;
    }

    public String getPatronymicRu() {
        return patronymicRu;
    }

    public void setPatronymicRu(String patronymicRu) {
        this.patronymicRu = patronymicRu;
    }

    public String getLastNameRuPossessive() {
        return lastNameRuPossessive;
    }

    public void setLastNameRuPossessive(String lastNameRuPossessive) {
        this.lastNameRuPossessive = lastNameRuPossessive;
    }

    public Integer getFailedLoginAttempts() {
        return failedLoginAttempts;
    }

    public void setFailedLoginAttempts(Integer failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }

    public String getFullNamePossessiveInitialsRu(){
        if(StringUtils.isNotEmpty(this.lastNameRuPossessive) && StringUtils.isNotEmpty(this.firstNameRu) && StringUtils.isNotEmpty(this.patronymicRu)){
            return this.lastNameRuPossessive + " " + this.firstNameRu.charAt(0) + "." + this.patronymicRu.charAt(0) + '.';
        }else{
            return getFullNameInitialsRu();
        }
    }

    public String getFullNameInitialsRu(){
        if(StringUtils.isNotEmpty(this.lastNameRu) && StringUtils.isNotEmpty(this.firstNameRu) && StringUtils.isNotEmpty(this.patronymicRu)){
            return this.lastNameRu + " " + this.firstNameRu.charAt(0) + "." + this.patronymicRu.charAt(0) + '.';
        }else if(StringUtils.isNotEmpty(this.lastNameRu) && StringUtils.isNotEmpty(this.firstNameRu)){
            return this.lastNameRu + " " + this.firstNameRu.charAt(0) + ".";
        }else if(StringUtils.isNotEmpty(this.lastNameRu)){
            return this.lastNameRu;
        }
        return null;
    }

    public String getFullPositionRu(){
        if(this.position != null) {
            if (StringUtils.isNotEmpty(this.position.getNameRu()) && this.position.getDepartment() != null &&
                    StringUtils.isNotEmpty(this.position.getDepartment().getNameUsedWithPositionRu())) {
                return this.position.getNameRu() + " " + this.position.getDepartment().getNameUsedWithPositionRu();
            } else if (StringUtils.isNotEmpty(this.position.getNameRu())) {
                return this.position.getNameRu();
            }
        }
        return null;
    }
}
