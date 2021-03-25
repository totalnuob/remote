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
    private Double transitionPortfolioTrailing;
    private Double transitionPortfolioRPInception;
    private Double transitionPortfolioInception;

    private Double alternativePortfolioNav;
    private Double alternativePortfolioMtd;
    private Double alternativePortfolioQtd;
    private Double alternativePortfolioYtd;
    private Double alternativePortfolioTrailing;
    private Double alternativePortfolioRPInception;
    private Double alternativePortfolioInception;

    private Double fixedPortfolioNav;
    private Double fixedPortfolioMtd;
    private Double fixedPortfolioQtd;
    private Double fixedPortfolioYtd;
    private Double fixedPortfolioTrailing;
    private Double fixedPortfolioRPInception;
    private Double fixedPortfolioInception;

    private Double equityPortfolioNav;
    private Double equityPortfolioMtd;
    private Double equityPortfolioQtd;
    private Double equityPortfolioYtd;
    private Double equityPortfolioTrailing;
    private Double equityPortfolioRPInception;
    private Double equityPortfolioInception;

    private Double nickMFConsolidatedNav;
    private Double nickMFConsolidatedMtd;
    private Double nickMFConsolidatedQtd;
    private Double nickMFConsolidatedYtd;
    private Double nickMFConsolidatedTrailing;
    private Double nickMFConsolidatedRPInception;
    private Double nickMFConsolidatedInception;

    private Double hedgeFundsNav;
    private Double hedgeFundsMtd;
    private Double hedgeFundsQtd;
    private Double hedgeFundsYtd;
    private Double hedgeFundsTrailing;
    private Double hedgeFundsRPInception;
    private Double hedgeFundsInception;

    private Double hedgeFundsClassANav;
    private Double hedgeFundsClassAMtd;
    private Double hedgeFundsClassAQtd;
    private Double hedgeFundsClassAYtd;
    private Double hedgeFundsClassATrailing;
    private Double hedgeFundsClassARPInception;
    private Double hedgeFundsClassAInception;

    private Double hedgeFundsClassBNav;
    private Double hedgeFundsClassBMtd;
    private Double hedgeFundsClassBQtd;
    private Double hedgeFundsClassBYtd;
    private Double hedgeFundsClassBTrailing;
    private Double hedgeFundsClassBRPInception;
    private Double hedgeFundsClassBInception;

    private Double privateEquityNav;
    private Double privateEquityMtd;
    private Double privateEquityQtd;
    private Double privateEquityYtd;
    private Double privateEquityTrailing;
    private Double privateEquityRPInception;
    private Double privateEquityInception;

    private Double privateEquityTarragonANav;
    private Double privateEquityTarragonAMtd;
    private Double privateEquityTarragonAQtd;
    private Double privateEquityTarragonAYtd;
    private Double privateEquityTarragonATrailing;
    private Double privateEquityTarragonARPInception;
    private Double privateEquityTarragonAInception;

    private Double privateEquityTarragonA2Nav;
    private Double privateEquityTarragonA2Mtd;
    private Double privateEquityTarragonA2Qtd;
    private Double privateEquityTarragonA2Ytd;
    private Double privateEquityTarragonA2Trailing;
    private Double privateEquityTarragonA2RPInception;
    private Double privateEquityTarragonA2Inception;

    private Double privateEquityTarragonBNav;
    private Double privateEquityTarragonBMtd;
    private Double privateEquityTarragonBQtd;
    private Double privateEquityTarragonBYtd;
    private Double privateEquityTarragonBTrailing;
    private Double privateEquityTarragonBRPInception;
    private Double privateEquityTarragonBInception;

    private Double privateEquityTarragonB2Nav;
    private Double privateEquityTarragonB2Mtd;
    private Double privateEquityTarragonB2Qtd;
    private Double privateEquityTarragonB2Ytd;
    private Double privateEquityTarragonB2Trailing;
    private Double privateEquityTarragonB2RPInception;
    private Double privateEquityTarragonB2Inception;

    private Double realEstateNav;
    private Double realEstateMtd;
    private Double realEstateQtd;
    private Double realEstateYtd;
    private Double realEstateTrailing;
    private Double realEstateRPInception;
    private Double realEstateInception;

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

    public NicPortfolio(Employee updater, Files file, Date date, Double nicTotalAumNav, Double transitionPortfolioNav, Double transitionPortfolioMtd, Double transitionPortfolioQtd, Double transitionPortfolioYtd, Double transitionPortfolioTrailing, Double transitionPortfolioRPInception, Double transitionPortfolioInception, Double alternativePortfolioNav, Double alternativePortfolioMtd, Double alternativePortfolioQtd, Double alternativePortfolioYtd, Double alternativePortfolioTrailing, Double alternativePortfolioRPInception, Double alternativePortfolioInception, Double fixedPortfolioNav, Double fixedPortfolioMtd, Double fixedPortfolioQtd, Double fixedPortfolioYtd, Double fixedPortfolioTrailing, Double fixedPortfolioRPInception, Double fixedPortfolioInception, Double equityPortfolioNav, Double equityPortfolioMtd, Double equityPortfolioQtd, Double equityPortfolioYtd, Double equityPortfolioTrailing, Double equityPortfolioRPInception, Double equityPortfolioInception, Double nickMFConsolidatedNav, Double nickMFConsolidatedMtd, Double nickMFConsolidatedQtd, Double nickMFConsolidatedYtd, Double nickMFConsolidatedTrailing, Double nickMFConsolidatedRPInception, Double nickMFConsolidatedInception, Double hedgeFundsNav, Double hedgeFundsMtd, Double hedgeFundsQtd, Double hedgeFundsYtd, Double hedgeFundsTrailing, Double hedgeFundsRPInception, Double hedgeFundsInception, Double hedgeFundsClassANav, Double hedgeFundsClassAMtd, Double hedgeFundsClassAQtd, Double hedgeFundsClassAYtd, Double hedgeFundsClassATrailing, Double hedgeFundsClassARPInception, Double hedgeFundsClassAInception, Double hedgeFundsClassBNav, Double hedgeFundsClassBMtd, Double hedgeFundsClassBQtd, Double hedgeFundsClassBYtd, Double hedgeFundsClassBTrailing, Double hedgeFundsClassBRPInception, Double hedgeFundsClassBInception, Double privateEquityNav, Double privateEquityMtd, Double privateEquityQtd, Double privateEquityYtd, Double privateEquityTrailing, Double privateEquityRPInception, Double privateEquityInception, Double privateEquityTarragonANav, Double privateEquityTarragonAMtd, Double privateEquityTarragonAQtd, Double privateEquityTarragonAYtd, Double privateEquityTarragonATrailing, Double privateEquityTarragonARPInception, Double privateEquityTarragonAInception, Double privateEquityTarragonA2Nav, Double privateEquityTarragonA2Mtd, Double privateEquityTarragonA2Qtd, Double privateEquityTarragonA2Ytd, Double privateEquityTarragonA2Trailing, Double privateEquityTarragonA2RPInception, Double privateEquityTarragonA2Inception, Double privateEquityTarragonBNav, Double privateEquityTarragonBMtd, Double privateEquityTarragonBQtd, Double privateEquityTarragonBYtd, Double privateEquityTarragonBTrailing, Double privateEquityTarragonBRPInception, Double privateEquityTarragonBInception, Double privateEquityTarragonB2Nav, Double privateEquityTarragonB2Mtd, Double privateEquityTarragonB2Qtd, Double privateEquityTarragonB2Ytd, Double privateEquityTarragonB2Trailing, Double privateEquityTarragonB2RPInception, Double privateEquityTarragonB2Inception, Double realEstateNav, Double realEstateMtd, Double realEstateQtd, Double realEstateYtd, Double realEstateTrailing, Double realEstateRPInception, Double realEstateInception, Double nickMfOtherNav, Double nickMfOtherMtd, Double nickMfOtherQtd, Double nickMfOtherYtd, Double transferNav, Double transferMtd, Double transferQtd, Double transferYtd) {
        this.updater = updater;
        this.file = file;
        this.date = date;
        this.nicTotalAumNav = nicTotalAumNav;
        this.transitionPortfolioNav = transitionPortfolioNav;
        this.transitionPortfolioMtd = transitionPortfolioMtd;
        this.transitionPortfolioQtd = transitionPortfolioQtd;
        this.transitionPortfolioYtd = transitionPortfolioYtd;
        this.transitionPortfolioTrailing = transitionPortfolioTrailing;
        this.transitionPortfolioRPInception = transitionPortfolioRPInception;
        this.transitionPortfolioInception = transitionPortfolioInception;
        this.alternativePortfolioNav = alternativePortfolioNav;
        this.alternativePortfolioMtd = alternativePortfolioMtd;
        this.alternativePortfolioQtd = alternativePortfolioQtd;
        this.alternativePortfolioYtd = alternativePortfolioYtd;
        this.alternativePortfolioTrailing = alternativePortfolioTrailing;
        this.alternativePortfolioRPInception = alternativePortfolioRPInception;
        this.alternativePortfolioInception = alternativePortfolioInception;
        this.fixedPortfolioNav = fixedPortfolioNav;
        this.fixedPortfolioMtd = fixedPortfolioMtd;
        this.fixedPortfolioQtd = fixedPortfolioQtd;
        this.fixedPortfolioYtd = fixedPortfolioYtd;
        this.fixedPortfolioTrailing = fixedPortfolioTrailing;
        this.fixedPortfolioRPInception = fixedPortfolioRPInception;
        this.fixedPortfolioInception = fixedPortfolioInception;
        this.equityPortfolioNav = equityPortfolioNav;
        this.equityPortfolioMtd = equityPortfolioMtd;
        this.equityPortfolioQtd = equityPortfolioQtd;
        this.equityPortfolioYtd = equityPortfolioYtd;
        this.equityPortfolioTrailing = equityPortfolioTrailing;
        this.equityPortfolioRPInception = equityPortfolioRPInception;
        this.equityPortfolioInception = equityPortfolioInception;
        this.nickMFConsolidatedNav = nickMFConsolidatedNav;
        this.nickMFConsolidatedMtd = nickMFConsolidatedMtd;
        this.nickMFConsolidatedQtd = nickMFConsolidatedQtd;
        this.nickMFConsolidatedYtd = nickMFConsolidatedYtd;
        this.nickMFConsolidatedTrailing = nickMFConsolidatedTrailing;
        this.nickMFConsolidatedRPInception = nickMFConsolidatedRPInception;
        this.nickMFConsolidatedInception = nickMFConsolidatedInception;
        this.hedgeFundsNav = hedgeFundsNav;
        this.hedgeFundsMtd = hedgeFundsMtd;
        this.hedgeFundsQtd = hedgeFundsQtd;
        this.hedgeFundsYtd = hedgeFundsYtd;
        this.hedgeFundsTrailing = hedgeFundsTrailing;
        this.hedgeFundsRPInception = hedgeFundsRPInception;
        this.hedgeFundsInception = hedgeFundsInception;
        this.hedgeFundsClassANav = hedgeFundsClassANav;
        this.hedgeFundsClassAMtd = hedgeFundsClassAMtd;
        this.hedgeFundsClassAQtd = hedgeFundsClassAQtd;
        this.hedgeFundsClassAYtd = hedgeFundsClassAYtd;
        this.hedgeFundsClassATrailing = hedgeFundsClassATrailing;
        this.hedgeFundsClassARPInception = hedgeFundsClassARPInception;
        this.hedgeFundsClassAInception = hedgeFundsClassAInception;
        this.hedgeFundsClassBNav = hedgeFundsClassBNav;
        this.hedgeFundsClassBMtd = hedgeFundsClassBMtd;
        this.hedgeFundsClassBQtd = hedgeFundsClassBQtd;
        this.hedgeFundsClassBYtd = hedgeFundsClassBYtd;
        this.hedgeFundsClassBTrailing = hedgeFundsClassBTrailing;
        this.hedgeFundsClassBRPInception = hedgeFundsClassBRPInception;
        this.hedgeFundsClassBInception = hedgeFundsClassBInception;
        this.privateEquityNav = privateEquityNav;
        this.privateEquityMtd = privateEquityMtd;
        this.privateEquityQtd = privateEquityQtd;
        this.privateEquityYtd = privateEquityYtd;
        this.privateEquityTrailing = privateEquityTrailing;
        this.privateEquityRPInception = privateEquityRPInception;
        this.privateEquityInception = privateEquityInception;
        this.privateEquityTarragonANav = privateEquityTarragonANav;
        this.privateEquityTarragonAMtd = privateEquityTarragonAMtd;
        this.privateEquityTarragonAQtd = privateEquityTarragonAQtd;
        this.privateEquityTarragonAYtd = privateEquityTarragonAYtd;
        this.privateEquityTarragonATrailing = privateEquityTarragonATrailing;
        this.privateEquityTarragonARPInception = privateEquityTarragonARPInception;
        this.privateEquityTarragonAInception = privateEquityTarragonAInception;
        this.privateEquityTarragonA2Nav = privateEquityTarragonA2Nav;
        this.privateEquityTarragonA2Mtd = privateEquityTarragonA2Mtd;
        this.privateEquityTarragonA2Qtd = privateEquityTarragonA2Qtd;
        this.privateEquityTarragonA2Ytd = privateEquityTarragonA2Ytd;
        this.privateEquityTarragonA2Trailing = privateEquityTarragonA2Trailing;
        this.privateEquityTarragonA2RPInception = privateEquityTarragonA2RPInception;
        this.privateEquityTarragonA2Inception = privateEquityTarragonA2Inception;
        this.privateEquityTarragonBNav = privateEquityTarragonBNav;
        this.privateEquityTarragonBMtd = privateEquityTarragonBMtd;
        this.privateEquityTarragonBQtd = privateEquityTarragonBQtd;
        this.privateEquityTarragonBYtd = privateEquityTarragonBYtd;
        this.privateEquityTarragonBTrailing = privateEquityTarragonBTrailing;
        this.privateEquityTarragonBRPInception = privateEquityTarragonBRPInception;
        this.privateEquityTarragonBInception = privateEquityTarragonBInception;
        this.privateEquityTarragonB2Nav = privateEquityTarragonB2Nav;
        this.privateEquityTarragonB2Mtd = privateEquityTarragonB2Mtd;
        this.privateEquityTarragonB2Qtd = privateEquityTarragonB2Qtd;
        this.privateEquityTarragonB2Ytd = privateEquityTarragonB2Ytd;
        this.privateEquityTarragonB2Trailing = privateEquityTarragonB2Trailing;
        this.privateEquityTarragonB2RPInception = privateEquityTarragonB2RPInception;
        this.privateEquityTarragonB2Inception = privateEquityTarragonB2Inception;
        this.realEstateNav = realEstateNav;
        this.realEstateMtd = realEstateMtd;
        this.realEstateQtd = realEstateQtd;
        this.realEstateYtd = realEstateYtd;
        this.realEstateTrailing = realEstateTrailing;
        this.realEstateRPInception = realEstateRPInception;
        this.realEstateInception = realEstateInception;
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

    public Double getTransitionPortfolioTrailing() {
        return transitionPortfolioTrailing;
    }

    public void setTransitionPortfolioTrailing(Double transitionPortfolioTrailing) {
        this.transitionPortfolioTrailing = transitionPortfolioTrailing;
    }

    public Double getTransitionPortfolioRPInception() {
        return transitionPortfolioRPInception;
    }

    public void setTransitionPortfolioRPInception(Double transitionPortfolioRPInception) {
        this.transitionPortfolioRPInception = transitionPortfolioRPInception;
    }

    public Double getTransitionPortfolioInception() {
        return transitionPortfolioInception;
    }

    public void setTransitionPortfolioInception(Double transitionPortfolioInception) {
        this.transitionPortfolioInception = transitionPortfolioInception;
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

    public Double getAlternativePortfolioTrailing() {
        return alternativePortfolioTrailing;
    }

    public void setAlternativePortfolioTrailing(Double alternativePortfolioTrailing) {
        this.alternativePortfolioTrailing = alternativePortfolioTrailing;
    }

    public Double getAlternativePortfolioRPInception() {
        return alternativePortfolioRPInception;
    }

    public void setAlternativePortfolioRPInception(Double alternativePortfolioRPInception) {
        this.alternativePortfolioRPInception = alternativePortfolioRPInception;
    }

    public Double getAlternativePortfolioInception() {
        return alternativePortfolioInception;
    }

    public void setAlternativePortfolioInception(Double alternativePortfolioInception) {
        this.alternativePortfolioInception = alternativePortfolioInception;
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

    public Double getFixedPortfolioTrailing() {
        return fixedPortfolioTrailing;
    }

    public void setFixedPortfolioTrailing(Double fixedPortfolioTrailing) {
        this.fixedPortfolioTrailing = fixedPortfolioTrailing;
    }

    public Double getFixedPortfolioRPInception() {
        return fixedPortfolioRPInception;
    }

    public void setFixedPortfolioRPInception(Double fixedPortfolioRPInception) {
        this.fixedPortfolioRPInception = fixedPortfolioRPInception;
    }

    public Double getFixedPortfolioInception() {
        return fixedPortfolioInception;
    }

    public void setFixedPortfolioInception(Double fixedPortfolioInception) {
        this.fixedPortfolioInception = fixedPortfolioInception;
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

    public Double getEquityPortfolioTrailing() {
        return equityPortfolioTrailing;
    }

    public void setEquityPortfolioTrailing(Double equityPortfolioTrailing) {
        this.equityPortfolioTrailing = equityPortfolioTrailing;
    }

    public Double getEquityPortfolioRPInception() {
        return equityPortfolioRPInception;
    }

    public void setEquityPortfolioRPInception(Double equityPortfolioRPInception) {
        this.equityPortfolioRPInception = equityPortfolioRPInception;
    }

    public Double getEquityPortfolioInception() {
        return equityPortfolioInception;
    }

    public void setEquityPortfolioInception(Double equityPortfolioInception) {
        this.equityPortfolioInception = equityPortfolioInception;
    }

    public Double getNickMFConsolidatedNav() {
        return nickMFConsolidatedNav;
    }

    public void setNickMFConsolidatedNav(Double nickMFConsolidatedNav) {
        this.nickMFConsolidatedNav = nickMFConsolidatedNav;
    }

    public Double getNickMFConsolidatedMtd() {
        return nickMFConsolidatedMtd;
    }

    public void setNickMFConsolidatedMtd(Double nickMFConsolidatedMtd) {
        this.nickMFConsolidatedMtd = nickMFConsolidatedMtd;
    }

    public Double getNickMFConsolidatedQtd() {
        return nickMFConsolidatedQtd;
    }

    public void setNickMFConsolidatedQtd(Double nickMFConsolidatedQtd) {
        this.nickMFConsolidatedQtd = nickMFConsolidatedQtd;
    }

    public Double getNickMFConsolidatedYtd() {
        return nickMFConsolidatedYtd;
    }

    public void setNickMFConsolidatedYtd(Double nickMFConsolidatedYtd) {
        this.nickMFConsolidatedYtd = nickMFConsolidatedYtd;
    }

    public Double getNickMFConsolidatedTrailing() {
        return nickMFConsolidatedTrailing;
    }

    public void setNickMFConsolidatedTrailing(Double nickMFConsolidatedTrailing) {
        this.nickMFConsolidatedTrailing = nickMFConsolidatedTrailing;
    }

    public Double getNickMFConsolidatedRPInception() {
        return nickMFConsolidatedRPInception;
    }

    public void setNickMFConsolidatedRPInception(Double nickMFConsolidatedRPInception) {
        this.nickMFConsolidatedRPInception = nickMFConsolidatedRPInception;
    }

    public Double getNickMFConsolidatedInception() {
        return nickMFConsolidatedInception;
    }

    public void setNickMFConsolidatedInception(Double nickMFConsolidatedInception) {
        this.nickMFConsolidatedInception = nickMFConsolidatedInception;
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

    public Double getHedgeFundsTrailing() {
        return hedgeFundsTrailing;
    }

    public void setHedgeFundsTrailing(Double hedgeFundsTrailing) {
        this.hedgeFundsTrailing = hedgeFundsTrailing;
    }

    public Double getHedgeFundsRPInception() {
        return hedgeFundsRPInception;
    }

    public void setHedgeFundsRPInception(Double hedgeFundsRPInception) {
        this.hedgeFundsRPInception = hedgeFundsRPInception;
    }

    public Double getHedgeFundsInception() {
        return hedgeFundsInception;
    }

    public void setHedgeFundsInception(Double hedgeFundsInception) {
        this.hedgeFundsInception = hedgeFundsInception;
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

    public Double getHedgeFundsClassATrailing() {
        return hedgeFundsClassATrailing;
    }

    public void setHedgeFundsClassATrailing(Double hedgeFundsClassATrailing) {
        this.hedgeFundsClassATrailing = hedgeFundsClassATrailing;
    }

    public Double getHedgeFundsClassARPInception() {
        return hedgeFundsClassARPInception;
    }

    public void setHedgeFundsClassARPInception(Double hedgeFundsClassARPInception) {
        this.hedgeFundsClassARPInception = hedgeFundsClassARPInception;
    }

    public Double getHedgeFundsClassAInception() {
        return hedgeFundsClassAInception;
    }

    public void setHedgeFundsClassAInception(Double hedgeFundsClassAInception) {
        this.hedgeFundsClassAInception = hedgeFundsClassAInception;
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

    public Double getHedgeFundsClassBTrailing() {
        return hedgeFundsClassBTrailing;
    }

    public void setHedgeFundsClassBTrailing(Double hedgeFundsClassBTrailing) {
        this.hedgeFundsClassBTrailing = hedgeFundsClassBTrailing;
    }

    public Double getHedgeFundsClassBRPInception() {
        return hedgeFundsClassBRPInception;
    }

    public void setHedgeFundsClassBRPInception(Double hedgeFundsClassBRPInception) {
        this.hedgeFundsClassBRPInception = hedgeFundsClassBRPInception;
    }

    public Double getHedgeFundsClassBInception() {
        return hedgeFundsClassBInception;
    }

    public void setHedgeFundsClassBInception(Double hedgeFundsClassBInception) {
        this.hedgeFundsClassBInception = hedgeFundsClassBInception;
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

    public Double getPrivateEquityTrailing() {
        return privateEquityTrailing;
    }

    public void setPrivateEquityTrailing(Double privateEquityTrailing) {
        this.privateEquityTrailing = privateEquityTrailing;
    }

    public Double getPrivateEquityRPInception() {
        return privateEquityRPInception;
    }

    public void setPrivateEquityRPInception(Double privateEquityRPInception) {
        this.privateEquityRPInception = privateEquityRPInception;
    }

    public Double getPrivateEquityInception() {
        return privateEquityInception;
    }

    public void setPrivateEquityInception(Double privateEquityInception) {
        this.privateEquityInception = privateEquityInception;
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

    public Double getPrivateEquityTarragonATrailing() {
        return privateEquityTarragonATrailing;
    }

    public void setPrivateEquityTarragonATrailing(Double privateEquityTarragonATrailing) {
        this.privateEquityTarragonATrailing = privateEquityTarragonATrailing;
    }

    public Double getPrivateEquityTarragonARPInception() {
        return privateEquityTarragonARPInception;
    }

    public void setPrivateEquityTarragonARPInception(Double privateEquityTarragonARPInception) {
        this.privateEquityTarragonARPInception = privateEquityTarragonARPInception;
    }

    public Double getPrivateEquityTarragonAInception() {
        return privateEquityTarragonAInception;
    }

    public void setPrivateEquityTarragonAInception(Double privateEquityTarragonAInception) {
        this.privateEquityTarragonAInception = privateEquityTarragonAInception;
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

    public Double getPrivateEquityTarragonA2Trailing() {
        return privateEquityTarragonA2Trailing;
    }

    public void setPrivateEquityTarragonA2Trailing(Double privateEquityTarragonA2Trailing) {
        this.privateEquityTarragonA2Trailing = privateEquityTarragonA2Trailing;
    }

    public Double getPrivateEquityTarragonA2RPInception() {
        return privateEquityTarragonA2RPInception;
    }

    public void setPrivateEquityTarragonA2RPInception(Double privateEquityTarragonA2RPInception) {
        this.privateEquityTarragonA2RPInception = privateEquityTarragonA2RPInception;
    }

    public Double getPrivateEquityTarragonA2Inception() {
        return privateEquityTarragonA2Inception;
    }

    public void setPrivateEquityTarragonA2Inception(Double privateEquityTarragonA2Inception) {
        this.privateEquityTarragonA2Inception = privateEquityTarragonA2Inception;
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

    public Double getPrivateEquityTarragonBTrailing() {
        return privateEquityTarragonBTrailing;
    }

    public void setPrivateEquityTarragonBTrailing(Double privateEquityTarragonBTrailing) {
        this.privateEquityTarragonBTrailing = privateEquityTarragonBTrailing;
    }

    public Double getPrivateEquityTarragonBRPInception() {
        return privateEquityTarragonBRPInception;
    }

    public void setPrivateEquityTarragonBRPInception(Double privateEquityTarragonBRPInception) {
        this.privateEquityTarragonBRPInception = privateEquityTarragonBRPInception;
    }

    public Double getPrivateEquityTarragonBInception() {
        return privateEquityTarragonBInception;
    }

    public void setPrivateEquityTarragonBInception(Double privateEquityTarragonBInception) {
        this.privateEquityTarragonBInception = privateEquityTarragonBInception;
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

    public Double getPrivateEquityTarragonB2Trailing() {
        return privateEquityTarragonB2Trailing;
    }

    public void setPrivateEquityTarragonB2Trailing(Double privateEquityTarragonB2Trailing) {
        this.privateEquityTarragonB2Trailing = privateEquityTarragonB2Trailing;
    }

    public Double getPrivateEquityTarragonB2RPInception() {
        return privateEquityTarragonB2RPInception;
    }

    public void setPrivateEquityTarragonB2RPInception(Double privateEquityTarragonB2RPInception) {
        this.privateEquityTarragonB2RPInception = privateEquityTarragonB2RPInception;
    }

    public Double getPrivateEquityTarragonB2Inception() {
        return privateEquityTarragonB2Inception;
    }

    public void setPrivateEquityTarragonB2Inception(Double privateEquityTarragonB2Inception) {
        this.privateEquityTarragonB2Inception = privateEquityTarragonB2Inception;
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

    public Double getRealEstateTrailing() {
        return realEstateTrailing;
    }

    public void setRealEstateTrailing(Double realEstateTrailing) {
        this.realEstateTrailing = realEstateTrailing;
    }

    public Double getRealEstateRPInception() {
        return realEstateRPInception;
    }

    public void setRealEstateRPInception(Double realEstateRPInception) {
        this.realEstateRPInception = realEstateRPInception;
    }

    public Double getRealEstateInception() {
        return realEstateInception;
    }

    public void setRealEstateInception(Double realEstateInception) {
        this.realEstateInception = realEstateInception;
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
