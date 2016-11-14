package kz.nicnbk.repo.model.hf;

import kz.nicnbk.repo.model.base.CreateUpdateBaseEntity;
import kz.nicnbk.repo.model.base.DataConstraints;
import kz.nicnbk.repo.model.common.Country;
import kz.nicnbk.repo.model.common.Strategy;

import javax.persistence.*;

/**
 * Created by magzumov on 04.07.2016.
 */

@Entity
@Table(name = "hf_manager")
public class HFManager extends CreateUpdateBaseEntity {
    private String name;
    private ManagerType managerType;
    private Strategy strategy;
//    private FundStatus status;
    private String inception;
//    private LegalStructure legalStructure;
    private Country domicileCountry;
    private String AUM;

    private String fundManagers;
    private String headquarters;
    private String contactPerson;
    private String telephone;
    private String fax;
    private String website;
    private String email;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "strategy_id")
    public Strategy getStrategy() {
        return strategy;
    }

    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "status_id")
//    public FundStatus getStatus() {
//        return status;
//    }
//
//    public void setStatus(FundStatus status) {
//        this.status = status;
//    }

    @Column(name="inception")
    public String getInception() {
        return inception;
    }

    public void setInception(String inception) {
        this.inception = inception;
    }

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "legal_structure_id")
//    public LegalStructure getLegalStructure() {
//        return legalStructure;
//    }
//
//    public void setLegalStructure(LegalStructure legalStructure) {
//        this.legalStructure = legalStructure;
//    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "domicile_country_id")
    public Country getDomicileCountry() {
        return domicileCountry;
    }

    public void setDomicileCountry(Country domicileCountry) {
        this.domicileCountry = domicileCountry;
    }

    @Column(name="aum")
    public String getAUM() {
        return AUM;
    }

    public void setAUM(String AUM) {
        this.AUM = AUM;
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

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
