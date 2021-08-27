package kz.nicnbk.repo.model.employee;

import kz.nicnbk.repo.model.base.BaseEntity;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

/**
 * Created by magzumov on 04.07.2016.
 */

@Entity
@Table(name="employees")
public class Employee extends BaseEntity {

    private String lastName;
    private String firstName;
    private String patronymic;
    private Date birthDate;
    private Position position;

    private String lastNameRu;
    private String firstNameRu;
    private String patronymicRu;
    private String lastNameRuPossessive;

    private String username;
    private String salt;
    private String password;
    private Boolean active;
    private Set<Role> roles;

    private Integer failedLoginAttempts;
    private Boolean locked;

    private Boolean mfaEnabled;
    private String secret;
    private String email;

    private Boolean isActing;
    private Long actingEmployee;

    public Employee(){}

    public Employee(Long id){
        setId(id);
    }

    @Column(name="lastname")
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Column(name="firstname")
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Column(name="patronymic")
    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    @Column(name="birthdate")
    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    @Column(name="username", unique = true)
    public String getUsername() {
        return username;
    }

    @Column(name = "email", unique = true)
    public String getEmail() {
        return email;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id")
    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(name="password", length = 512)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Column(name="salt")
    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    @Column(name="active", nullable=false)
    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name="employee_roles",
            joinColumns=
            @JoinColumn(name="employee_id", referencedColumnName="ID"),
            inverseJoinColumns=
            @JoinColumn(name="role_id", referencedColumnName="ID")
    )
    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public Integer getFailedLoginAttempts() {
        return failedLoginAttempts;
    }

    public void setFailedLoginAttempts(Integer failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }

    public Boolean getLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }

    public Boolean getMfaEnabled() {
        return mfaEnabled;
    }

    public void setMfaEnabled(Boolean mfaEnabled) {
        this.mfaEnabled = mfaEnabled;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    @Column(name="lastname_ru")
    public String getLastNameRu() {
        return lastNameRu;
    }

    public void setLastNameRu(String lastNameRu) {
        this.lastNameRu = lastNameRu;
    }

    @Column(name="firstname_ru")
    public String getFirstNameRu() {
        return firstNameRu;
    }

    public void setFirstNameRu(String firstNameRu) {
        this.firstNameRu = firstNameRu;
    }

    @Column(name="patronymic_ru")
    public String getPatronymicRu() {
        return patronymicRu;
    }

    public void setPatronymicRu(String patronymicRu) {
        this.patronymicRu = patronymicRu;
    }

    @Column(name="lastname_ru_possessive")
    public String getLastNameRuPossessive() {
        return lastNameRuPossessive;
    }

    public void setLastNameRuPossessive(String lastNameRuPossessive) {
        this.lastNameRuPossessive = lastNameRuPossessive;
    }

    @Column(name = "is_acting")
    public Boolean getIsActing() {
        return isActing;
    }

    public void setIsActing(Boolean isActing) {
        this.isActing = isActing;
    }

    @Column(name = "acting_employee_id")
    public Long getActingEmployee() {
        return actingEmployee;
    }

    public void setActingEmployee(Long actingEmployee) {
        this.actingEmployee = actingEmployee;
    }
}
