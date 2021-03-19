package kz.nicnbk.repo.model.monitoring;

import kz.nicnbk.repo.model.base.BaseEntity;
import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.repo.model.files.Files;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Pak on 13.06.2019.
 */

@Entity(name = "monitoring_nic_portfolio")
public class NicPortfolio extends BaseEntity {

    private Employee updater;

    private Files file;

    @Column(nullable = false)
    private Date date;

    private Double nicTotalAumNav;

    private Double transitionPortfolioNav;
    private Double transitionPortfolioMtd;
    private Double transitionPortfolioQtd;
    private Double transitionPortfolioYtd;

    private Double alternativePortfolioNav;
    private Double alternativePortfolioMtd;
    private Double alternativePortfolioQtd;
    private Double alternativePortfolioYtd;

    private Double fixedPortfolioNav;
    private Double fixedPortfolioMtd;
    private Double fixedPortfolioQtd;
    private Double fixedPortfolioYtd;

    private Double equityPortfolioNav;
    private Double equityPortfolioMtd;
    private Double equityPortfolioQtd;
    private Double equityPortfolioYtd;

    private Double hedgeFundsNav;
    private Double hedgeFundsMtd;
    private Double hedgeFundsQtd;
    private Double hedgeFundsYtd;

    private Double hedgeFundsClassANav;
    private Double hedgeFundsClassAMtd;
    private Double hedgeFundsClassAQtd;
    private Double hedgeFundsClassAYtd;

    private Double hedgeFundsClassBNav;
    private Double hedgeFundsClassBMtd;
    private Double hedgeFundsClassBQtd;
    private Double hedgeFundsClassBYtd;

    private Double privateEquityNav;
    private Double privateEquityMtd;
    private Double privateEquityQtd;
    private Double privateEquityYtd;

    private Double privateEquityTarragonANav;
    private Double privateEquityTarragonAMtd;
    private Double privateEquityTarragonAQtd;
    private Double privateEquityTarragonAYtd;

    private Double privateEquityTarragonA2Nav;
    private Double privateEquityTarragonA2Mtd;
    private Double privateEquityTarragonA2Qtd;
    private Double privateEquityTarragonA2Ytd;

    private Double privateEquityTarragonBNav;
    private Double privateEquityTarragonBMtd;
    private Double privateEquityTarragonBQtd;
    private Double privateEquityTarragonBYtd;

    private Double privateEquityTarragonB2Nav;
    private Double privateEquityTarragonB2Mtd;
    private Double privateEquityTarragonB2Qtd;
    private Double privateEquityTarragonB2Ytd;

    private Double realEstateNav;
    private Double realEstateMtd;
    private Double realEstateQtd;
    private Double realEstateYtd;

    private Double nickMfOtherNav;
    private Double nickMfOtherMtd;
    private Double nickMfOtherQtd;
    private Double nickMfOtherYtd;

    private Double transferNav;
    private Double transferMtd;
    private Double transferQtd;
    private Double transferYtd;

    public NicPortfolio() {
    }

    public NicPortfolio(Employee updater, Files file, Date date, Double nicTotalAumNav, Double transitionPortfolioNav, Double transitionPortfolioMtd, Double transitionPortfolioQtd, Double transitionPortfolioYtd, Double alternativePortfolioNav, Double alternativePortfolioMtd, Double alternativePortfolioQtd, Double alternativePortfolioYtd, Double fixedPortfolioNav, Double fixedPortfolioMtd, Double fixedPortfolioQtd, Double fixedPortfolioYtd, Double equityPortfolioNav, Double equityPortfolioMtd, Double equityPortfolioQtd, Double equityPortfolioYtd, Double hedgeFundsNav, Double hedgeFundsMtd, Double hedgeFundsQtd, Double hedgeFundsYtd, Double hedgeFundsClassANav, Double hedgeFundsClassAMtd, Double hedgeFundsClassAQtd, Double hedgeFundsClassAYtd, Double hedgeFundsClassBNav, Double hedgeFundsClassBMtd, Double hedgeFundsClassBQtd, Double hedgeFundsClassBYtd, Double privateEquityNav, Double privateEquityMtd, Double privateEquityQtd, Double privateEquityYtd, Double privateEquityTarragonANav, Double privateEquityTarragonAMtd, Double privateEquityTarragonAQtd, Double privateEquityTarragonAYtd, Double privateEquityTarragonA2Nav, Double privateEquityTarragonA2Mtd, Double privateEquityTarragonA2Qtd, Double privateEquityTarragonA2Ytd, Double privateEquityTarragonBNav, Double privateEquityTarragonBMtd, Double privateEquityTarragonBQtd, Double privateEquityTarragonBYtd, Double privateEquityTarragonB2Nav, Double privateEquityTarragonB2Mtd, Double privateEquityTarragonB2Qtd, Double privateEquityTarragonB2Ytd, Double realEstateNav, Double realEstateMtd, Double realEstateQtd, Double realEstateYtd, Double nickMfOtherNav, Double nickMfOtherMtd, Double nickMfOtherQtd, Double nickMfOtherYtd, Double transferNav, Double transferMtd, Double transferQtd, Double transferYtd) {
        this.updater = updater;
        this.file = file;
        this.date = date;
        this.nicTotalAumNav = nicTotalAumNav;
        this.transitionPortfolioNav = transitionPortfolioNav;
        this.transitionPortfolioMtd = transitionPortfolioMtd;
        this.transitionPortfolioQtd = transitionPortfolioQtd;
        this.transitionPortfolioYtd = transitionPortfolioYtd;
        this.alternativePortfolioNav = alternativePortfolioNav;
        this.alternativePortfolioMtd = alternativePortfolioMtd;
        this.alternativePortfolioQtd = alternativePortfolioQtd;
        this.alternativePortfolioYtd = alternativePortfolioYtd;
        this.fixedPortfolioNav = fixedPortfolioNav;
        this.fixedPortfolioMtd = fixedPortfolioMtd;
        this.fixedPortfolioQtd = fixedPortfolioQtd;
        this.fixedPortfolioYtd = fixedPortfolioYtd;
        this.equityPortfolioNav = equityPortfolioNav;
        this.equityPortfolioMtd = equityPortfolioMtd;
        this.equityPortfolioQtd = equityPortfolioQtd;
        this.equityPortfolioYtd = equityPortfolioYtd;
        this.hedgeFundsNav = hedgeFundsNav;
        this.hedgeFundsMtd = hedgeFundsMtd;
        this.hedgeFundsQtd = hedgeFundsQtd;
        this.hedgeFundsYtd = hedgeFundsYtd;
        this.hedgeFundsClassANav = hedgeFundsClassANav;
        this.hedgeFundsClassAMtd = hedgeFundsClassAMtd;
        this.hedgeFundsClassAQtd = hedgeFundsClassAQtd;
        this.hedgeFundsClassAYtd = hedgeFundsClassAYtd;
        this.hedgeFundsClassBNav = hedgeFundsClassBNav;
        this.hedgeFundsClassBMtd = hedgeFundsClassBMtd;
        this.hedgeFundsClassBQtd = hedgeFundsClassBQtd;
        this.hedgeFundsClassBYtd = hedgeFundsClassBYtd;
        this.privateEquityNav = privateEquityNav;
        this.privateEquityMtd = privateEquityMtd;
        this.privateEquityQtd = privateEquityQtd;
        this.privateEquityYtd = privateEquityYtd;
        this.privateEquityTarragonANav = privateEquityTarragonANav;
        this.privateEquityTarragonAMtd = privateEquityTarragonAMtd;
        this.privateEquityTarragonAQtd = privateEquityTarragonAQtd;
        this.privateEquityTarragonAYtd = privateEquityTarragonAYtd;
        this.privateEquityTarragonA2Nav = privateEquityTarragonA2Nav;
        this.privateEquityTarragonA2Mtd = privateEquityTarragonA2Mtd;
        this.privateEquityTarragonA2Qtd = privateEquityTarragonA2Qtd;
        this.privateEquityTarragonA2Ytd = privateEquityTarragonA2Ytd;
        this.privateEquityTarragonBNav = privateEquityTarragonBNav;
        this.privateEquityTarragonBMtd = privateEquityTarragonBMtd;
        this.privateEquityTarragonBQtd = privateEquityTarragonBQtd;
        this.privateEquityTarragonBYtd = privateEquityTarragonBYtd;
        this.privateEquityTarragonB2Nav = privateEquityTarragonB2Nav;
        this.privateEquityTarragonB2Mtd = privateEquityTarragonB2Mtd;
        this.privateEquityTarragonB2Qtd = privateEquityTarragonB2Qtd;
        this.privateEquityTarragonB2Ytd = privateEquityTarragonB2Ytd;
        this.realEstateNav = realEstateNav;
        this.realEstateMtd = realEstateMtd;
        this.realEstateQtd = realEstateQtd;
        this.realEstateYtd = realEstateYtd;
        this.nickMfOtherNav = nickMfOtherNav;
        this.nickMfOtherMtd = nickMfOtherMtd;
        this.nickMfOtherQtd = nickMfOtherQtd;
        this.nickMfOtherYtd = nickMfOtherYtd;
        this.transferNav = transferNav;
        this.transferMtd = transferMtd;
        this.transferQtd = transferQtd;
        this.transferYtd = transferYtd;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UpdatedBy")
    public Employee getUpdater() {
        return updater;
    }

    public void setUpdater(Employee updater) {
        this.updater = updater;
    }

    @ManyToOne
    @JoinColumn(name = "file_id")
    public Files getFile() {
        return file;
    }

    public void setFile(Files file) {
        this.file = file;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Double getNicTotalAumNav() {
        return nicTotalAumNav;
    }

    public void setNicTotalAumNav(Double nicTotalAumNav) {
        this.nicTotalAumNav = nicTotalAumNav;
    }

    public Double getTransitionPortfolioNav() {
        return transitionPortfolioNav;
    }

    public void setTransitionPortfolioNav(Double transitionPortfolioNav) {
        this.transitionPortfolioNav = transitionPortfolioNav;
    }

    public Double getTransitionPortfolioMtd() {
        return transitionPortfolioMtd;
    }

    public void setTransitionPortfolioMtd(Double transitionPortfolioMtd) {
        this.transitionPortfolioMtd = transitionPortfolioMtd;
    }

    public Double getTransitionPortfolioQtd() {
        return transitionPortfolioQtd;
    }

    public void setTransitionPortfolioQtd(Double transitionPortfolioQtd) {
        this.transitionPortfolioQtd = transitionPortfolioQtd;
    }

    public Double getTransitionPortfolioYtd() {
        return transitionPortfolioYtd;
    }

    public void setTransitionPortfolioYtd(Double transitionPortfolioYtd) {
        this.transitionPortfolioYtd = transitionPortfolioYtd;
    }

    public Double getAlternativePortfolioNav() {
        return alternativePortfolioNav;
    }

    public void setAlternativePortfolioNav(Double alternativePortfolioNav) {
        this.alternativePortfolioNav = alternativePortfolioNav;
    }

    public Double getAlternativePortfolioMtd() {
        return alternativePortfolioMtd;
    }

    public void setAlternativePortfolioMtd(Double alternativePortfolioMtd) {
        this.alternativePortfolioMtd = alternativePortfolioMtd;
    }

    public Double getAlternativePortfolioQtd() {
        return alternativePortfolioQtd;
    }

    public void setAlternativePortfolioQtd(Double alternativePortfolioQtd) {
        this.alternativePortfolioQtd = alternativePortfolioQtd;
    }

    public Double getAlternativePortfolioYtd() {
        return alternativePortfolioYtd;
    }

    public void setAlternativePortfolioYtd(Double alternativePortfolioYtd) {
        this.alternativePortfolioYtd = alternativePortfolioYtd;
    }

    public Double getFixedPortfolioNav() {
        return fixedPortfolioNav;
    }

    public void setFixedPortfolioNav(Double fixedPortfolioNav) {
        this.fixedPortfolioNav = fixedPortfolioNav;
    }

    public Double getFixedPortfolioMtd() {
        return fixedPortfolioMtd;
    }

    public void setFixedPortfolioMtd(Double fixedPortfolioMtd) {
        this.fixedPortfolioMtd = fixedPortfolioMtd;
    }

    public Double getFixedPortfolioQtd() {
        return fixedPortfolioQtd;
    }

    public void setFixedPortfolioQtd(Double fixedPortfolioQtd) {
        this.fixedPortfolioQtd = fixedPortfolioQtd;
    }

    public Double getFixedPortfolioYtd() {
        return fixedPortfolioYtd;
    }

    public void setFixedPortfolioYtd(Double fixedPortfolioYtd) {
        this.fixedPortfolioYtd = fixedPortfolioYtd;
    }

    public Double getEquityPortfolioNav() {
        return equityPortfolioNav;
    }

    public void setEquityPortfolioNav(Double equityPortfolioNav) {
        this.equityPortfolioNav = equityPortfolioNav;
    }

    public Double getEquityPortfolioMtd() {
        return equityPortfolioMtd;
    }

    public void setEquityPortfolioMtd(Double equityPortfolioMtd) {
        this.equityPortfolioMtd = equityPortfolioMtd;
    }

    public Double getEquityPortfolioQtd() {
        return equityPortfolioQtd;
    }

    public void setEquityPortfolioQtd(Double equityPortfolioQtd) {
        this.equityPortfolioQtd = equityPortfolioQtd;
    }

    public Double getEquityPortfolioYtd() {
        return equityPortfolioYtd;
    }

    public void setEquityPortfolioYtd(Double equityPortfolioYtd) {
        this.equityPortfolioYtd = equityPortfolioYtd;
    }

    public Double getHedgeFundsNav() {
        return hedgeFundsNav;
    }

    public void setHedgeFundsNav(Double hedgeFundsNav) {
        this.hedgeFundsNav = hedgeFundsNav;
    }

    public Double getHedgeFundsMtd() {
        return hedgeFundsMtd;
    }

    public void setHedgeFundsMtd(Double hedgeFundsMtd) {
        this.hedgeFundsMtd = hedgeFundsMtd;
    }

    public Double getHedgeFundsQtd() {
        return hedgeFundsQtd;
    }

    public void setHedgeFundsQtd(Double hedgeFundsQtd) {
        this.hedgeFundsQtd = hedgeFundsQtd;
    }

    public Double getHedgeFundsYtd() {
        return hedgeFundsYtd;
    }

    public void setHedgeFundsYtd(Double hedgeFundsYtd) {
        this.hedgeFundsYtd = hedgeFundsYtd;
    }

    public Double getHedgeFundsClassANav() {
        return hedgeFundsClassANav;
    }

    public void setHedgeFundsClassANav(Double hedgeFundsClassANav) {
        this.hedgeFundsClassANav = hedgeFundsClassANav;
    }

    public Double getHedgeFundsClassAMtd() {
        return hedgeFundsClassAMtd;
    }

    public void setHedgeFundsClassAMtd(Double hedgeFundsClassAMtd) {
        this.hedgeFundsClassAMtd = hedgeFundsClassAMtd;
    }

    public Double getHedgeFundsClassAQtd() {
        return hedgeFundsClassAQtd;
    }

    public void setHedgeFundsClassAQtd(Double hedgeFundsClassAQtd) {
        this.hedgeFundsClassAQtd = hedgeFundsClassAQtd;
    }

    public Double getHedgeFundsClassAYtd() {
        return hedgeFundsClassAYtd;
    }

    public void setHedgeFundsClassAYtd(Double hedgeFundsClassAYtd) {
        this.hedgeFundsClassAYtd = hedgeFundsClassAYtd;
    }

    public Double getHedgeFundsClassBNav() {
        return hedgeFundsClassBNav;
    }

    public void setHedgeFundsClassBNav(Double hedgeFundsClassBNav) {
        this.hedgeFundsClassBNav = hedgeFundsClassBNav;
    }

    public Double getHedgeFundsClassBMtd() {
        return hedgeFundsClassBMtd;
    }

    public void setHedgeFundsClassBMtd(Double hedgeFundsClassBMtd) {
        this.hedgeFundsClassBMtd = hedgeFundsClassBMtd;
    }

    public Double getHedgeFundsClassBQtd() {
        return hedgeFundsClassBQtd;
    }

    public void setHedgeFundsClassBQtd(Double hedgeFundsClassBQtd) {
        this.hedgeFundsClassBQtd = hedgeFundsClassBQtd;
    }

    public Double getHedgeFundsClassBYtd() {
        return hedgeFundsClassBYtd;
    }

    public void setHedgeFundsClassBYtd(Double hedgeFundsClassBYtd) {
        this.hedgeFundsClassBYtd = hedgeFundsClassBYtd;
    }

    public Double getPrivateEquityNav() {
        return privateEquityNav;
    }

    public void setPrivateEquityNav(Double privateEquityNav) {
        this.privateEquityNav = privateEquityNav;
    }

    public Double getPrivateEquityMtd() {
        return privateEquityMtd;
    }

    public void setPrivateEquityMtd(Double privateEquityMtd) {
        this.privateEquityMtd = privateEquityMtd;
    }

    public Double getPrivateEquityQtd() {
        return privateEquityQtd;
    }

    public void setPrivateEquityQtd(Double privateEquityQtd) {
        this.privateEquityQtd = privateEquityQtd;
    }

    public Double getPrivateEquityYtd() {
        return privateEquityYtd;
    }

    public void setPrivateEquityYtd(Double privateEquityYtd) {
        this.privateEquityYtd = privateEquityYtd;
    }

    public Double getPrivateEquityTarragonANav() {
        return privateEquityTarragonANav;
    }

    public void setPrivateEquityTarragonANav(Double privateEquityTarragonANav) {
        this.privateEquityTarragonANav = privateEquityTarragonANav;
    }

    public Double getPrivateEquityTarragonAMtd() {
        return privateEquityTarragonAMtd;
    }

    public void setPrivateEquityTarragonAMtd(Double privateEquityTarragonAMtd) {
        this.privateEquityTarragonAMtd = privateEquityTarragonAMtd;
    }

    public Double getPrivateEquityTarragonAQtd() {
        return privateEquityTarragonAQtd;
    }

    public void setPrivateEquityTarragonAQtd(Double privateEquityTarragonAQtd) {
        this.privateEquityTarragonAQtd = privateEquityTarragonAQtd;
    }

    public Double getPrivateEquityTarragonAYtd() {
        return privateEquityTarragonAYtd;
    }

    public void setPrivateEquityTarragonAYtd(Double privateEquityTarragonAYtd) {
        this.privateEquityTarragonAYtd = privateEquityTarragonAYtd;
    }

    public Double getPrivateEquityTarragonA2Nav() {
        return privateEquityTarragonA2Nav;
    }

    public void setPrivateEquityTarragonA2Nav(Double privateEquityTarragonA2Nav) {
        this.privateEquityTarragonA2Nav = privateEquityTarragonA2Nav;
    }

    public Double getPrivateEquityTarragonA2Mtd() {
        return privateEquityTarragonA2Mtd;
    }

    public void setPrivateEquityTarragonA2Mtd(Double privateEquityTarragonA2Mtd) {
        this.privateEquityTarragonA2Mtd = privateEquityTarragonA2Mtd;
    }

    public Double getPrivateEquityTarragonA2Qtd() {
        return privateEquityTarragonA2Qtd;
    }

    public void setPrivateEquityTarragonA2Qtd(Double privateEquityTarragonA2Qtd) {
        this.privateEquityTarragonA2Qtd = privateEquityTarragonA2Qtd;
    }

    public Double getPrivateEquityTarragonA2Ytd() {
        return privateEquityTarragonA2Ytd;
    }

    public void setPrivateEquityTarragonA2Ytd(Double privateEquityTarragonA2Ytd) {
        this.privateEquityTarragonA2Ytd = privateEquityTarragonA2Ytd;
    }

    public Double getPrivateEquityTarragonBNav() {
        return privateEquityTarragonBNav;
    }

    public void setPrivateEquityTarragonBNav(Double privateEquityTarragonBNav) {
        this.privateEquityTarragonBNav = privateEquityTarragonBNav;
    }

    public Double getPrivateEquityTarragonBMtd() {
        return privateEquityTarragonBMtd;
    }

    public void setPrivateEquityTarragonBMtd(Double privateEquityTarragonBMtd) {
        this.privateEquityTarragonBMtd = privateEquityTarragonBMtd;
    }

    public Double getPrivateEquityTarragonBQtd() {
        return privateEquityTarragonBQtd;
    }

    public void setPrivateEquityTarragonBQtd(Double privateEquityTarragonBQtd) {
        this.privateEquityTarragonBQtd = privateEquityTarragonBQtd;
    }

    public Double getPrivateEquityTarragonBYtd() {
        return privateEquityTarragonBYtd;
    }

    public void setPrivateEquityTarragonBYtd(Double privateEquityTarragonBYtd) {
        this.privateEquityTarragonBYtd = privateEquityTarragonBYtd;
    }

    public Double getPrivateEquityTarragonB2Nav() {
        return privateEquityTarragonB2Nav;
    }

    public void setPrivateEquityTarragonB2Nav(Double privateEquityTarragonB2Nav) {
        this.privateEquityTarragonB2Nav = privateEquityTarragonB2Nav;
    }

    public Double getPrivateEquityTarragonB2Mtd() {
        return privateEquityTarragonB2Mtd;
    }

    public void setPrivateEquityTarragonB2Mtd(Double privateEquityTarragonB2Mtd) {
        this.privateEquityTarragonB2Mtd = privateEquityTarragonB2Mtd;
    }

    public Double getPrivateEquityTarragonB2Qtd() {
        return privateEquityTarragonB2Qtd;
    }

    public void setPrivateEquityTarragonB2Qtd(Double privateEquityTarragonB2Qtd) {
        this.privateEquityTarragonB2Qtd = privateEquityTarragonB2Qtd;
    }

    public Double getPrivateEquityTarragonB2Ytd() {
        return privateEquityTarragonB2Ytd;
    }

    public void setPrivateEquityTarragonB2Ytd(Double privateEquityTarragonB2Ytd) {
        this.privateEquityTarragonB2Ytd = privateEquityTarragonB2Ytd;
    }

    public Double getRealEstateNav() {
        return realEstateNav;
    }

    public void setRealEstateNav(Double realEstateNav) {
        this.realEstateNav = realEstateNav;
    }

    public Double getRealEstateMtd() {
        return realEstateMtd;
    }

    public void setRealEstateMtd(Double realEstateMtd) {
        this.realEstateMtd = realEstateMtd;
    }

    public Double getRealEstateQtd() {
        return realEstateQtd;
    }

    public void setRealEstateQtd(Double realEstateQtd) {
        this.realEstateQtd = realEstateQtd;
    }

    public Double getRealEstateYtd() {
        return realEstateYtd;
    }

    public void setRealEstateYtd(Double realEstateYtd) {
        this.realEstateYtd = realEstateYtd;
    }

    public Double getNickMfOtherNav() {
        return nickMfOtherNav;
    }

    public void setNickMfOtherNav(Double nickMfOtherNav) {
        this.nickMfOtherNav = nickMfOtherNav;
    }

    public Double getNickMfOtherMtd() {
        return nickMfOtherMtd;
    }

    public void setNickMfOtherMtd(Double nickMfOtherMtd) {
        this.nickMfOtherMtd = nickMfOtherMtd;
    }

    public Double getNickMfOtherQtd() {
        return nickMfOtherQtd;
    }

    public void setNickMfOtherQtd(Double nickMfOtherQtd) {
        this.nickMfOtherQtd = nickMfOtherQtd;
    }

    public Double getNickMfOtherYtd() {
        return nickMfOtherYtd;
    }

    public void setNickMfOtherYtd(Double nickMfOtherYtd) {
        this.nickMfOtherYtd = nickMfOtherYtd;
    }

    public Double getTransferNav() {
        return transferNav;
    }

    public void setTransferNav(Double transferNav) {
        this.transferNav = transferNav;
    }

    public Double getTransferMtd() {
        return transferMtd;
    }

    public void setTransferMtd(Double transferMtd) {
        this.transferMtd = transferMtd;
    }

    public Double getTransferQtd() {
        return transferQtd;
    }

    public void setTransferQtd(Double transferQtd) {
        this.transferQtd = transferQtd;
    }

    public Double getTransferYtd() {
        return transferYtd;
    }

    public void setTransferYtd(Double transferYtd) {
        this.transferYtd = transferYtd;
    }
}
