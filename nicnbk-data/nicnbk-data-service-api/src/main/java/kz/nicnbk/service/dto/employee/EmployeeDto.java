package kz.nicnbk.service.dto.employee;

import kz.nicnbk.common.service.model.BaseEntityDto;
import kz.nicnbk.repo.model.employee.Employee;

import java.util.Date;

/**
 * Created by magzumov on 08.07.2016.
 */
public class EmployeeDto extends BaseEntityDto<Employee> {
    private String lastName;
    private String firstName;
    private String patronymic;
    private Date birthDate;

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
}
