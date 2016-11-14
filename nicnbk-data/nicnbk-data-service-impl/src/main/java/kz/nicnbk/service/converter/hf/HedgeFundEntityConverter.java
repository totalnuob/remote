package kz.nicnbk.service.converter.hf;

import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.repo.model.common.Country;
import kz.nicnbk.repo.model.common.Currency;
import kz.nicnbk.repo.model.common.Geography;
import kz.nicnbk.repo.model.common.Strategy;
import kz.nicnbk.repo.model.hf.*;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.datamanager.LookupService;
import kz.nicnbk.service.dto.hf.HFManagerDto;
import kz.nicnbk.service.dto.hf.HedgeFundDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by timur on 19.10.2016.
 */

@Component
public class HedgeFundEntityConverter extends BaseDozerEntityConverter<HedgeFund, HedgeFundDto> {

    @Autowired
    private LookupService lookupService;

    @Override
    public HedgeFund assemble(HedgeFundDto dto){
        HedgeFund entity = super.assemble(dto);

        // manager

        //strategy
        if(StringUtils.isNotEmpty(dto.getStrategy())) {
            Strategy strategy = lookupService.findByTypeAndCode(Strategy.class, dto.getStrategy());
            entity.setStrategy(strategy);
        }

        // geography
        if(StringUtils.isNotEmpty(dto.getGeography())) {
            Geography geography = lookupService.findByTypeAndCode(Geography.class, dto.getGeography());
            entity.setGeography(geography);
        }

        // share class currency
        if(StringUtils.isNotEmpty(dto.getShareClassCurrency())) {
            Currency currency = lookupService.findByTypeAndCode(Currency.class, dto.getShareClassCurrency());
            entity.setShareClassCurrency(currency);
        }

        //status
        if(StringUtils.isNotEmpty(dto.getStatus())) {
            FundStatus status = lookupService.findByTypeAndCode(FundStatus.class, dto.getStatus());
            entity.setStatus(status);
        }

        //legal structure
        if(StringUtils.isNotEmpty(dto.getLegalStructure())) {
            LegalStructure legalStructure = lookupService.findByTypeAndCode(LegalStructure.class, dto.getLegalStructure());
            entity.setLegalStructure(legalStructure);
        }

        //domicile country
        if(StringUtils.isNotEmpty(dto.getDomicileCountry())) {
            Country domicileCountry = lookupService.findByTypeAndCode(Country.class, dto.getDomicileCountry());
            entity.setDomicileCountry(domicileCountry);
        }

        // investment currency
        if(StringUtils.isNotEmpty(dto.getInvestmentCurrency())) {
            Currency currency = lookupService.findByTypeAndCode(Currency.class, dto.getInvestmentCurrency());
            entity.setInvestmentCurrency(currency);
        }

        // subscription frequency
        if(StringUtils.isNotEmpty(dto.getSubscriptionFrequency())) {
            SubscriptionFrequency frequency = lookupService.findByTypeAndCode(SubscriptionFrequency.class, dto.getSubscriptionFrequency());
            entity.setSubscriptionFrequency(frequency);
        }

        // mng fee type
        if(StringUtils.isNotEmpty(dto.getManagementFeeType())) {
            ManagementFeeType mngFeeType = lookupService.findByTypeAndCode(ManagementFeeType.class, dto.getManagementFeeType());
            entity.setManagementFeeType(mngFeeType);
        }

        // performance fee type
        if(StringUtils.isNotEmpty(dto.getPerformanceFeeType())) {
            PerformanceFeeType performanceFeeType = lookupService.findByTypeAndCode(PerformanceFeeType.class, dto.getPerformanceFeeType());
            entity.setPerformanceFeeType(performanceFeeType);
        }

        // performance fee pay freq type
        if(StringUtils.isNotEmpty(dto.getPerformanceFeePayFrequency())) {
            PerformanceFeePayFrequencyType frequencyType = lookupService.findByTypeAndCode(PerformanceFeePayFrequencyType.class,
                    dto.getPerformanceFeePayFrequency());
            entity.setPerformanceFeePayFrequency(frequencyType);
        }

        // redemption freq type
        if(StringUtils.isNotEmpty(dto.getRedemptionFrequency())) {
            RedemptionFrequencyType frequencyType = lookupService.findByTypeAndCode(RedemptionFrequencyType.class,
                    dto.getRedemptionFrequency());
            entity.setRedemptionFrequency(frequencyType);
        }

        // redemption notification period type
        if(StringUtils.isNotEmpty(dto.getRedemptionNotificationPeriod())) {
            RedemptionNotificationPeriodType notificationPeriodType = lookupService.findByTypeAndCode(RedemptionNotificationPeriodType.class,
                    dto.getRedemptionNotificationPeriod());
            entity.setRedemptionNotificationPeriod(notificationPeriodType);
        }

        return entity;
    }

    @Override
    public HedgeFundDto disassemble(HedgeFund entity){
        HedgeFundDto dto = super.disassemble(entity);

        // strategy
        if(entity.getStrategy() != null){
            dto.setStrategy(entity.getStrategy().getCode());
        }

        // geography
        if(entity.getGeography() != null){
            dto.setGeography(entity.getGeography().getCode());
        }

        // share class currency
        if(entity.getShareClassCurrency() != null) {
            dto.setShareClassCurrency(entity.getShareClassCurrency().getCode());
        }

        // status
        if(entity.getStatus() != null){
            dto.setStatus(entity.getStatus().getCode());
        }

        // legal structure
        if(entity.getLegalStructure() != null){
            dto.setLegalStructure(entity.getLegalStructure().getCode());
        }

        // domicile country
        if(entity.getDomicileCountry() != null){
            dto.setDomicileCountry(entity.getDomicileCountry().getCode());
        }

        // investment currency
        if(entity.getInvestmentCurrency() != null){
            dto.setInvestmentCurrency(entity.getInvestmentCurrency().getCode());
        }

        // subscription frequency
        if(entity.getSubscriptionFrequency() != null){
            dto.setSubscriptionFrequency(entity.getSubscriptionFrequency().getCode());
        }

        // management fee type
        if(entity.getManagementFeeType() != null){
            dto.setManagementFeeType(entity.getManagementFeeType().getCode());
        }

        // performance fee type
        if(entity.getPerformanceFeeType() != null){
            dto.setPerformanceFeeType(entity.getPerformanceFeeType().getCode());
        }

        // performance fee pay freq type
        if(entity.getPerformanceFeePayFrequency() != null){
            dto.setPerformanceFeePayFrequency(entity.getPerformanceFeePayFrequency().getCode());
        }

        // redemption freq type
        if(entity.getRedemptionFrequency() != null){
            dto.setRedemptionFrequency(entity.getRedemptionFrequency().getCode());
        }

        // redemption notification period type
        if(entity.getRedemptionNotificationPeriod() != null){
            dto.setRedemptionNotificationPeriod(entity.getRedemptionNotificationPeriod().getCode());
        }

        return dto;
    }

    public List<HedgeFundDto> disassembleList(List<HedgeFund> entities){
        List<HedgeFundDto> dtoList = new ArrayList<>();
        if(entities != null){
            for(HedgeFund entity: entities){
                dtoList.add(disassemble(entity));
            }
        }
        return dtoList;
    }

    public Set<HedgeFundDto> disassembleSet(List<HedgeFund> entities){
        Set<HedgeFundDto> dtoSet = new HashSet<>();
        if(entities != null){
            for(HedgeFund entity: entities){
                dtoSet.add(disassemble(entity));
            }
        }
        return dtoSet;
    }

}
