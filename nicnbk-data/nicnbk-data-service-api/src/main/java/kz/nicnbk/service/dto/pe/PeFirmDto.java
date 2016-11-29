package kz.nicnbk.service.dto.pe;

import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.common.service.model.HistoryBaseEntityDto;
import kz.nicnbk.repo.model.common.Geography;
import kz.nicnbk.repo.model.pe.Contacts;
import kz.nicnbk.repo.model.pe.Firm;
import kz.nicnbk.repo.model.pe.FirmAddress;
import kz.nicnbk.repo.model.pe.common.Industry;

import java.util.Set;

/**
 * Created by zhambyl on 15-Nov-16.
 */
public class PeFirmDto extends HistoryBaseEntityDto<Firm> {
    private String firmName;
    private int foundedYear;
    private int aum;
    private int invTeamSize;
    private int opsTeamSize;
    private String locations;
    private Set<Firm> peers;
    private Set<BaseDictionaryDto> strategy;
    private Set<BaseDictionaryDto> industryFocus;
    private Set<BaseDictionaryDto> geographyFocus;
    private Set<PeFundDto> funds;

    private Set<FirmAddress> address;
    private Set<Contacts> contacts;

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

    public int getAum() {
        return aum;
    }

    public void setAum(int aum) {
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

    public Set<Firm> getPeers() {
        return peers;
    }

    public void setPeers(Set<Firm> peers) {
        this.peers = peers;
    }

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

    public Set<PeFundDto> getFunds() {
        return funds;
    }

    public void setFunds(Set<PeFundDto> funds) {
        this.funds = funds;
    }

    public Set<FirmAddress> getAddress() {
        return address;
    }

    public void setAddress(Set<FirmAddress> address) {
        this.address = address;
    }

    public Set<Contacts> getContacts() {
        return contacts;
    }

    public void setContacts(Set<Contacts> contacts) {
        this.contacts = contacts;
    }
}
