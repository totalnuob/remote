package kz.nicnbk.repo.model.hf;

import kz.nicnbk.repo.model.base.CreateUpdateBaseEntity;
import kz.nicnbk.repo.model.base.DataConstraints;
import kz.nicnbk.repo.model.common.Country;
import kz.nicnbk.repo.model.common.Currency;
import kz.nicnbk.repo.model.common.Strategy;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by magzumov on 04.07.2016.
 */

@Entity
@Table(name = "hf_manager")
public class HFManager extends CreateUpdateBaseEntity {

    // TODO: rename table and class

    private String name;
    private ManagerType managerType;
    private String inception;
    private Date inceptionDate;
    private String AUM;
    private String AUMDigit;
    private Currency AUMCurrency;
    private Boolean meetingsInThePast;

    private String fundManagers;
    private String headquarters;
    private String contactPerson;
    private String telephone;
    private String fax;
    private String website;
    private String email;


    //    private Strategy strategy;
//    private HedgeFundStatus status;
//    private String inception;
//    private LegalStructure legalStructure;
//    private Country domicileCountry;

    @Column(name="name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id")
    public ManagerType getManagerType() {
        return managerType;
    }

    public void setManagerType(ManagerType managerType) {
        this.managerType = managerType;
    }

    @Column(name="inception")
    public String getInception() {
        return inception;
    }

    public void setInception(String inception) {
        this.inception = inception;
    }

    @Column(name="inception_date")
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern="dd-MM-yyyy")
    public Date getInceptionDate() {
        return inceptionDate;
    }

    public void setInceptionDate(Date inceptionDate) {
        this.inceptionDate = inceptionDate;
    }

    @Column(name="aum")
    public String getAUM() {
        return AUM;
    }

    public void setAUM(String AUM) {
        this.AUM = AUM;
    }

    @Column(name="aum_digit")
    public String getAUMDigit() {
        return AUMDigit;
    }

    public void setAUMDigit(String AUMDigit) {
        this.AUMDigit = AUMDigit;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "aum_currency_id")
    public Currency getAUMCurrency() {
        return AUMCurrency;
    }

    public void setAUMCurrency(Currency AUMCurrency) {
        this.AUMCurrency = AUMCurrency;
    }


    @Column(name="fund_managers", columnDefinition = "TEXT")
    public String getFundManagers() {
        return fundManagers;
    }

    public void setFundManagers(String fundManagers) {
        this.fundManagers = fundManagers;
    }

    @Column(name="headquarters", columnDefinition = "TEXT")
    public String getHeadquarters() {
        return headquarters;
    }

    public void setHeadquarters(String headquarters) {
        this.headquarters = headquarters;
    }

    @Column(name="contact_person", length = DataConstraints.C_TYPE_ENTITY_DESCRIPTION_SHORT)
    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    @Column(name="telephone", length = DataConstraints.C_TYPE_ENTITY_NAME)
    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    @Column(name="fax", length = DataConstraints.C_TYPE_ENTITY_NAME)
    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    @Column(name="website", length = DataConstraints.C_TYPE_ENTITY_NAME)
    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    @Column(name="email", length = DataConstraints.C_TYPE_ENTITY_NAME)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(name="meetings_in_past")
    public Boolean getMeetingsInThePast() {
        return meetingsInThePast;
    }

    public void setMeetingsInThePast(Boolean meetingsInThePast) {
        this.meetingsInThePast = meetingsInThePast;
    }

    //    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "strategy_id")
//    public Strategy getStrategy() {
//        return strategy;
//    }
//
//    public void setStrategy(Strategy strategy) {
//        this.strategy = strategy;
//    }

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "status_id")
//    public HedgeFundStatus getStatus() {
//        return status;
//    }
//
//    public void setStatus(HedgeFundStatus status) {
//        this.status = status;
//    }

    //    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "legal_structure_id")
//    public LegalStructure getLegalStructure() {
//        return legalStructure;
//    }
//
//    public void setLegalStructure(LegalStructure legalStructure) {
//        this.legalStructure = legalStructure;
//    }

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "domicile_country_id")
//    public Country getDomicileCountry() {
//        return domicileCountry;
//    }
//
//    public void setDomicileCountry(Country domicileCountry) {
//        this.domicileCountry = domicileCountry;
//    }
}
