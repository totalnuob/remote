package kz.nicnbk.service.dto.hf;

import kz.nicnbk.common.service.model.HistoryBaseEntityDto;
import kz.nicnbk.repo.model.hf.HFManager;

import java.util.List;
import java.util.Set;

/**
 * Created by timur on 19.10.2016.
 */
@Deprecated
public class HFManagerDto extends HistoryBaseEntityDto<HFManager> {
    private String name;
    private String managerType;
    private String strategy;
    private String status;
    private String inception;
    private String legalStructure;
    private String domicileCountry;
    private String AUM;

    private String fundManagers;
    private String headquarters;
    private String contactPerson;
    private String telephone;
    private String fax;
    private String website;
    private String email;

    private Set<HedgeFundDto> funds;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getManagerType() {
        return managerType;
    }

    public void setManagerType(String managerType) {
        this.managerType = managerType;
    }

    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getInception() {
        return inception;
    }

    public void setInception(String inception) {
        this.inception = inception;
    }

    public String getLegalStructure() {
        return legalStructure;
    }

    public void setLegalStructure(String legalStructure) {
        this.legalStructure = legalStructure;
    }

    public String getDomicileCountry() {
        return domicileCountry;
    }

    public void setDomicileCountry(String domicileCountry) {
        this.domicileCountry = domicileCountry;
    }

    public String getAUM() {
        return AUM;
    }

    public void setAUM(String AUM) {
        this.AUM = AUM;
    }

    public String getFundManagers() {
        return fundManagers;
    }

    public void setFundManagers(String fundManagers) {
        this.fundManagers = fundManagers;
    }

    public String getHeadquarters() {
        return headquarters;
    }

    public void setHeadquarters(String headquarters) {
        this.headquarters = headquarters;
    }

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

    public Set<HedgeFundDto> getFunds() {
        return funds;
    }

    public void setFunds(Set<HedgeFundDto> funds) {
        this.funds = funds;
    }
}
