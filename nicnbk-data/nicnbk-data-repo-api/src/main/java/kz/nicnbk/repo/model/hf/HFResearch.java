package kz.nicnbk.repo.model.hf;

import kz.nicnbk.repo.model.base.CreateUpdateBaseEntity;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

/**
 * Created by zhambyl on 06/11/2018.
 */
@Entity
@Table(name = "hf_research")
public class HFResearch extends CreateUpdateBaseEntity{

    private HFManager manager;
    private String investmentsDates;
    private Double allocationSize;
    private String contacts;
    private String nicCoverage;
    private String website;
    private String typesOfCommunication;
    private String importantNotes;
    private String keyPeople;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "manager_id")
    public HFManager getManager() {
        return manager;
    }

    public void setManager(HFManager manager) {
        this.manager = manager;
    }

    @Column(name = "investment_dates")
    public String getInvestmentsDates() {
        return investmentsDates;
    }

    public void setInvestmentsDates(String investmentsDates) {
        this.investmentsDates = investmentsDates;
    }

    @Column(name = "allocation_size")
    public Double getAllocationSize() {
        return allocationSize;
    }

    public void setAllocationSize(Double allocation) {
        this.allocationSize = allocationSize;
    }

    @Column(name = "contacts")
    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    @Column(name = "nic_coverage")
    public String getNicCoverage() {
        return nicCoverage;
    }

    public void setNicCoverage(String nicCoverage) {
        this.nicCoverage = nicCoverage;
    }

    @Column(name = "website")
    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    @Column(name = "types_communication")
    public String getTypesOfCommunication() {
        return typesOfCommunication;
    }

    public void setTypesOfCommunication(String typesOfCommunication) {
        this.typesOfCommunication = typesOfCommunication;
    }

    @Column(name = "important_notes")
    public String getImportantNotes() {
        return importantNotes;
    }

    public void setImportantNotes(String importantNotes) {
        this.importantNotes = importantNotes;
    }

    @Column(name = "key_people")
    public String getKeyPeople() {
        return keyPeople;
    }

    public void setKeyPeople(String keyPeople) {
        this.keyPeople = keyPeople;
    }

}
