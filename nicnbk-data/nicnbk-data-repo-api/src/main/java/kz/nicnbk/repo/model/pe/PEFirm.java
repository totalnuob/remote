package kz.nicnbk.repo.model.pe;

import kz.nicnbk.repo.model.base.CreateUpdateBaseEntity;
import kz.nicnbk.repo.model.common.Geography;
import kz.nicnbk.repo.model.common.Strategy;

import javax.persistence.*;
import java.util.Set;

/**
 * Created by zhambyl on 15-Sep-16.
 */
@Entity
@Table(name = "pe_firm")
public class PEFirm extends CreateUpdateBaseEntity{

    private String firmName;
    private int foundedYear;
    private float aum;
    private int invTeamSize;
    private int opsTeamSize;
    //    private Set<PEFirm> peers;
    private Set<Strategy> strategy;
    private Set<PEIndustry> industryFocus;
    private Set<Geography> geographyFocus;
//    private Set<PEFund> funds;

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

    @Column(name = "firm_name")
    public String getFirmName() {
        return firmName;
    }

    public void setFirmName(String firmName) {
        this.firmName = firmName;
    }

    @Column(name = "founded_year")
    public int getFoundedYear() {
        return foundedYear;
    }

    public void setFoundedYear(int foundedYear) {
        this.foundedYear = foundedYear;
    }

    @Column(name = "aum")
    public float getAum() {
        return aum;
    }

    public void setAum(float aum) {
        this.aum = aum;
    }

    @Column(name = "investment_team_size")
    public int getInvTeamSize() {
        return invTeamSize;
    }

    public void setInvTeamSize(int invTeamSize) {
        this.invTeamSize = invTeamSize;
    }

    @Column(name = "operations_team_size")
    public int getOpsTeamSize() {
        return opsTeamSize;
    }

    public void setOpsTeamSize(int opsTeamSize) {
        this.opsTeamSize = opsTeamSize;
    }

//    @ManyToMany(fetch = FetchType.LAZY)
//    @JoinTable(
//            name = "firm_peers",
//            joinColumns = @JoinColumn(name = "firm_id", referencedColumnName = "ID"),
//            inverseJoinColumns = @JoinColumn(name = "peers_id", referencedColumnName = "ID")
//    )
//    public Set<PEFirm> getPeers() {
//        return peers;
//    }
//
//    public void setPeers(Set<PEFirm> peers) {
//        this.peers = peers;
//    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "pe_firm_strategies",
            joinColumns = @JoinColumn(name = "firm_id", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "strategy_id", referencedColumnName = "ID")
    )
    public Set<Strategy> getStrategy() {
        return strategy;
    }

    public void setStrategy(Set<Strategy> strategy) {
        this.strategy = strategy;
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "pe_firm_industries",
            joinColumns = @JoinColumn(name = "firm_id", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "pe_industry_id", referencedColumnName = "ID")
    )
    public Set<PEIndustry> getIndustryFocus() {
        return industryFocus;
    }

    public void setIndustryFocus(Set<PEIndustry> industryFocus) {
        this.industryFocus = industryFocus;
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "pe_firm_geography",
            joinColumns = @JoinColumn(name = "firm_id", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "geography_id", referencedColumnName = "ID")
    )
    public Set<Geography> getGeographyFocus() {
        return geographyFocus;
    }

    public void setGeographyFocus(Set<Geography> geographyFocus) {
        this.geographyFocus = geographyFocus;
    }

//    @OneToMany(fetch = FetchType.LAZY, mappedBy = "firm", cascade = CascadeType.ALL)
//    public Set<PEFund> getFunds() {
//        return funds;
//    }
//
//    public void setFunds(Set<PEFund> funds) {
//        this.funds = funds;
//    }

//    @OneToMany(fetch = FetchType.LAZY, mappedBy = "firm")
//    public Set<FirmAddress> getAddress() {
//        return address;
//    }
//
//    public void setAddress(Set<FirmAddress> address) {
//        this.address = address;
//    }
//
//    @OneToMany(fetch = FetchType.LAZY, mappedBy = "firm")
//    public Set<Contacts> getContacts() {
//        return contacts;
//    }
//
//    public void setContacts(Set<Contacts> contacts) {
//        this.contacts = contacts;
//    }

    @Column(name = "locations")
    public String getLocations() {
        return locations;
    }

    public void setLocations(String locations) {
        this.locations = locations;
    }

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