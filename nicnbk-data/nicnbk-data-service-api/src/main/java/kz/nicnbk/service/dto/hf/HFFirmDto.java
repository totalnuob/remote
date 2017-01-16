package kz.nicnbk.service.dto.hf;

import kz.nicnbk.common.service.model.HistoryBaseEntityDto;
import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.repo.model.hf.HFManager;

import java.util.Date;
import java.util.List;

/**
 * Created by timur on 27.10.2016.
 */
public class HFFirmDto extends HistoryBaseEntityDto<HFManager> {
    private String name;
    private String managerType;

    private String inception;
    private Date inceptionDate;

    private String aum;
    private String aumDigit;
    private String aumCurrency;

    private Boolean meetingsInThePast;

    private String fundManagers;
    private String headquarters;
    private String contactPerson;
    private String telephone;
    private String fax;
    private String website;
    private String email;

    private List<HedgeFundDto> funds;

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

    public String getInception() {
        return inception;
    }

    public void setInception(String inception) {
        this.inception = inception;
    }

    public String getAum() {
        return aum;
    }

    public void setAum(String aum) {
        this.aum = aum;
    }

    public String getAumDigit() {
        return aumDigit;
    }

    public void setAumDigit(String aumDigit) {
        this.aumDigit = aumDigit;
    }

    public String getAumCurrency() {
        return aumCurrency;
    }

    public void setAumCurrency(String aumCurrency) {
        this.aumCurrency = aumCurrency;
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

    public List<HedgeFundDto> getFunds() {
        return funds;
    }

    public void setFunds(List<HedgeFundDto> funds) {
        this.funds = funds;
    }

    public Date getInceptionDate() {
        return inceptionDate;
    }

    public void setInceptionDate(Date inceptionDate) {
        this.inceptionDate = inceptionDate;
    }

    public Boolean getMeetingsInThePast() {
        return meetingsInThePast;
    }

    public void setMeetingsInThePast(Boolean meetingsInThePast) {
        this.meetingsInThePast = meetingsInThePast;
    }

    public String getAumText(){
        String AUMText = "";
        if(StringUtils.isNotEmpty(this.aum)){
            AUMText += this.aum;
            if(StringUtils.isNotEmpty(this.aumDigit)){
                AUMText += " " + this.aumDigit;
            }
            if(StringUtils.isNotEmpty(this.aumCurrency)){
                AUMText += " " + this.aumCurrency;
            }
            return AUMText;
        }else{
            return null;
        }
    }
}
