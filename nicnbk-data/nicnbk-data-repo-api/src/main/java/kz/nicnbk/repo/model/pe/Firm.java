package kz.nicnbk.repo.model.pe;

import kz.nicnbk.repo.model.base.CreateUpdateBaseEntity;
import kz.nicnbk.repo.model.common.Geography;
import kz.nicnbk.repo.model.common.Strategy;
import kz.nicnbk.repo.model.pe.common.Industry;

import javax.persistence.*;
import java.util.Set;

/**
 * Created by zhambyl on 15-Sep-16.
 */
@Entity
@Table(name = "firm")
public class Firm extends CreateUpdateBaseEntity{

    private String firmName;
    private int foundedYear;
    private int aum;
    private int invTeamSize;
    private int opsTeamSize;
    private String locations;
    private Set<Firm> peers;
    private Set<Strategy> strategy;
    private Set<Industry> industryFocus;
    private Set<Geography> geographyFocus;
//    private Set<Fund> funds;

    private Set<FirmAddress> address;
    private Set<Contacts> contacts;

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
    public int getAum() {
        return aum;
    }

    public void setAum(int aum) {
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

    @Column(name = "locations")
    public String getLocations() {
        return locations;
    }

    public void setLocations(String locations) {
        this.locations = locations;
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "firm_peers",
            joinColumns = @JoinColumn(name = "firm_id", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "peers_id", referencedColumnName = "ID")
    )
    public Set<Firm> getPeers() {
        return peers;
    }

    public void setPeers(Set<Firm> peers) {
        this.peers = peers;
    }

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
    public Set<Industry> getIndustryFocus() {
        return industryFocus;
    }

    public void setIndustryFocus(Set<Industry> industryFocus) {
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
//    public Set<Fund> getFunds() {
//        return funds;
//    }
//
//    public void setFunds(Set<Fund> funds) {
//        this.funds = funds;
//    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "firm")
    public Set<FirmAddress> getAddress() {
        return address;
    }

    public void setAddress(Set<FirmAddress> address) {
        this.address = address;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "firm")
    public Set<Contacts> getContacts() {
        return contacts;
    }

    public void setContacts(Set<Contacts> contacts) {
        this.contacts = contacts;
    }

}
