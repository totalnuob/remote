package kz.nicnbk.service.dto.hf;

import kz.nicnbk.common.service.model.CreateUpdateBaseEntityDto;
import kz.nicnbk.repo.model.hf.HFManager;
import kz.nicnbk.repo.model.hf.HFResearch;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by zhambyl on 06/11/2018.
 */
public class HFResearchDto extends CreateUpdateBaseEntityDto<HFResearch> {

    private HFManagerDto managerDto;
    private String investmentsDates;
    private Double allocationSize;
    private String contacts;
    private String nicCoverage;
    private String website;
    private String typesOfCommunication;
    private String importantNotes;
    private String keyPeople;

    private List<HFResearchPageDto> researchPages;

    public List<HFResearchPageDto> getResearchPages() {
        return researchPages;
    }

    public void setResearchPages(List<HFResearchPageDto> researchPages) {
        this.researchPages = researchPages;
    }

    private String owner;

    public HFManagerDto getManager() {
        return managerDto;
    }

    public void setManager(HFManagerDto manager) {
        this.managerDto = manager;
    }

    public String getInvestmentsDates() {
        return investmentsDates;
    }

    public void setInvestmentsDates(String investmentsDates) {
        this.investmentsDates = investmentsDates;
    }

    public Double getAllocationSize() {
        return allocationSize;
    }

    public void setAllocationSize(Double allocationSize) {
        this.allocationSize = allocationSize;
    }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public String getNicCoverage() {
        return nicCoverage;
    }

    public void setNicCoverage(String nicCoverage) {
        this.nicCoverage = nicCoverage;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getTypesOfCommunication() {
        return typesOfCommunication;
    }

    public void setTypesOfCommunication(String typesOfCommunication) {
        this.typesOfCommunication = typesOfCommunication;
    }

    public String getImportantNotes() {
        return importantNotes;
    }

    public void setImportantNotes(String importantNotes) {
        this.importantNotes = importantNotes;
    }

    public String getKeyPeople() {
        return keyPeople;
    }

    public void setKeyPeople(String keyPeople) {
        this.keyPeople = keyPeople;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getOwner() {

        return owner;
    }
}
