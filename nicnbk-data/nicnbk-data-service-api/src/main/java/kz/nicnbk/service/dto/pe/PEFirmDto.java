package kz.nicnbk.service.dto.pe;

import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.common.service.model.HistoryBaseEntityDto;
import kz.nicnbk.repo.model.pe.PEFirm;

import java.util.List;
import java.util.Set;

/**
 * Created by zhambyl on 15-Nov-16.
 */
public class PEFirmDto extends HistoryBaseEntityDto<PEFirm> {
    private String firmName;
    private int foundedYear;
    private float aum;
    private int invTeamSize;
    private int opsTeamSize;
    //    private Set<PEFirm> peers;
    private Set<BaseDictionaryDto> strategy;
    private Set<BaseDictionaryDto> industryFocus;
    private Set<BaseDictionaryDto> geographyFocus;
    private List<PEFundDto> funds;

//    private Set<FirmAddress> address;
//    private Set<Contacts> contacts;

    private String peers;
    private String locations;
    private String headquarters;
    private String telephone;
    private String fax;
    private String website;
    private String contactPerson;
    private String email;


    public String getFirmName() {
        return firmName;
    }

    public void setFirmName(String firmName) {
        this.firmName = firmName;
    }

    public int getFoundedYear() {
        return foundedYear;
    }

    public void setFoundedYear(int foundedYear) {
        this.foundedYear = foundedYear;
    }

    public float getAum() {
        return aum;
    }

    public void setAum(float aum) {
        this.aum = aum;
    }

    public int getInvTeamSize() {
        return invTeamSize;
    }

    public void setInvTeamSize(int invTeamSize) {
        this.invTeamSize = invTeamSize;
    }

    public int getOpsTeamSize() {
        return opsTeamSize;
    }

    public void setOpsTeamSize(int opsTeamSize) {
        this.opsTeamSize = opsTeamSize;
    }

    public String getLocations() {
        return locations;
    }

    public void setLocations(String locations) {
        this.locations = locations;
    }

//    public Set<PEFirm> getPeers() {
//        return peers;
//    }
//
//    public void setPeers(Set<PEFirm> peers) {
//        this.peers = peers;
//    }

    public Set<BaseDictionaryDto> getStrategy() {
        return strategy;
    }

    public void setStrategy(Set<BaseDictionaryDto> strategy) {
        this.strategy = strategy;
    }

    public Set<BaseDictionaryDto> getIndustryFocus() {
        return industryFocus;
    }

    public void setIndustryFocus(Set<BaseDictionaryDto> industryFocus) {
        this.industryFocus = industryFocus;
    }

    public Set<BaseDictionaryDto> getGeographyFocus() {
        return geographyFocus;
    }

    public void setGeographyFocus(Set<BaseDictionaryDto> geographyFocus) {
        this.geographyFocus = geographyFocus;
    }

    public List<PEFundDto> getFunds() {
        return funds;
    }

    public void setFunds(List<PEFundDto> funds) {
        this.funds = funds;
    }

//    public Set<FirmAddress> getAddress() {
//        return address;
//    }
//
//    public void setAddress(Set<FirmAddress> address) {
//        this.address = address;
//    }
//
//    public Set<Contacts> getContacts() {
//        return contacts;
//    }
//
//    public void setContacts(Set<Contacts> contacts) {
//        this.contacts = contacts;
//    }


    public String getPeers() {
        return peers;
    }

    public void setPeers(String peers) {
        this.peers = peers;
    }

    public String getHeadquarters() {
        return headquarters;
    }

    public void setHeadquarters(String headquarters) {
        this.headquarters = headquarters;
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

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}