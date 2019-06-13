package kz.nicnbk.service.dto.monitoring;

import kz.nicnbk.common.service.model.BaseEntityDto;
import kz.nicnbk.repo.model.monitoring.NicPortfolio;

import java.util.Date;

/**
 * Created by Pak on 13.06.2019.
 */

public class NicPortfolioDto extends BaseEntityDto<NicPortfolio> {

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

    private Double privateEquityNav;
    private Double privateEquityMtd;
    private Double privateEquityQtd;
    private Double privateEquityYtd;

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
