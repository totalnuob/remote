package kz.nicnbk.repo.model.pe;

import kz.nicnbk.repo.model.base.BaseTypeEntityImpl;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Created by zhambyl on 10-Nov-16.
 */
@Entity
@Table(name = "contacts")
public class Contacts extends BaseTypeEntityImpl{
    private Firm firm;

    @ManyToOne
    public Firm getFirm() {
        return firm;
    }

    public void setFirm(Firm firm) {
        this.firm = firm;
    }
}
