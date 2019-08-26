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

    private String username;
    private String salt;
    private String password;
    private Boolean active;
    private Set<Role> roles;

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

    public void setUsername(String username) {
        this.username = username;
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
}
