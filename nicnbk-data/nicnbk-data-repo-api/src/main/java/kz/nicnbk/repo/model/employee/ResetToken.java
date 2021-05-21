package kz.nicnbk.repo.model.employee;

import kz.nicnbk.repo.model.base.BaseEntity;
import kz.nicnbk.repo.model.base.BaseTypeEntityImpl;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "reset_token")
public class ResetToken extends BaseEntity {

    private String token;
    private Date expiryDate;

    public ResetToken() {}

    public ResetToken(Long id) {
        setId(id);
    }

    @Column(name = "reset_token")
    public String getToken() {
        return token;
    }

    @Column(name = "expiry_date")
    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }
}
