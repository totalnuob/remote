package kz.nicnbk.service.converter.hf;

import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.repo.model.common.Currency;
import kz.nicnbk.repo.model.common.Strategy;
import kz.nicnbk.repo.model.hf.*;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.datamanager.LookupService;
import kz.nicnbk.service.dto.hf.HedgeFundDto2;
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
public class HedgeFundEntityConverter extends BaseDozerEntityConverter<HedgeFund, HedgeFundDto2> {

    @Autowired
    private LookupService lookupService;

    @Override
    public HedgeFund assemble(HedgeFundDto2 dto){
        HedgeFund entity = super.assemble(dto);

        // manager

        //strategy
        if(StringUtils.isNotEmpty(dto.getStrategy())) {
            Strategy strategy = lookupService.findByTypeAndCode(Strategy.class, dto.getStrategy());
            entity.setStrategy(strategy);
        }

        // AUM currency
        if(StringUtils.isNotEmpty(dto.getAumCurrency())) {
            Currency currency = lookupService.findByTypeAndCode(Currency.class, dto.getAumCurrency());
            entity.setAumCurrency(currency);
        }

        //status
        if(StringUtils.isNotEmpty(dto.getStatus())) {
            HedgeFundStatus status = lookupService.findByTypeAndCode(HedgeFundStatus.class, dto.getStatus());
            entity.setStatus(status);
        }


        // subscription frequency
        if(StringUtils.isNotEmpty(dto.getSubscriptionFrequency())) {
            SubscriptionFrequency frequency = lookupService.findByTypeAndCode(SubscriptionFrequency.class, dto.getSubscriptionFrequency());
            entity.setSubscriptionFrequency(frequency);
        }

        // redemption frequency
        if(StringUtils.isNotEmpty(dto.getRedemptionFrequency())) {
            RedemptionFrequency frequency = lookupService.findByTypeAndCode(RedemptionFrequency.class, dto.getSubscriptionFrequency());
            entity.setRedemptionFrequency(frequency);
        }

        // redemption notice period
        if(StringUtils.isNotEmpty(dto.getRedemptionNotificationPeriod())) {
            RedemptionNotificationPeriod frequency = lookupService.findByTypeAndCode(RedemptionNotificationPeriod.class, dto.getSubscriptionFrequency());
            entity.setRedemptionNotificationPeriod(frequency);
        }

        // side pocket
        if(StringUtils.isNotEmpty(dto.getSidePocket())){
            HedgeFundSidePocket lookupValue = lookupService.findByTypeAndCode(HedgeFundSidePocket.class, dto.getSidePocket());
            entity.setSidePocket(lookupValue);
        }



        return entity;
    }

    @Override
    public HedgeFundDto2 disassemble(HedgeFund entity){
        HedgeFundDto2 dto = super.disassemble(entity);

        // strategy
        if(entity.getStrategy() != null){
            dto.setStrategy(entity.getStrategy().getCode());
        }

        // AUM currency
        if(entity.getAumCurrency() != null){
            dto.setAumCurrency(entity.getAumCurrency().getCode());
        }

        // status
        if(entity.getStatus() != null){
            dto.setStatus(entity.getStatus().getCode());
        }


        // subscription frequency
        if(entity.getSubscriptionFrequency() != null){
            dto.setSubscriptionFrequency(entity.getSubscriptionFrequency().getCode());
        }

        // redemption freq type
        if(entity.getRedemptionFrequency() != null){
            dto.setRedemptionFrequency(entity.getRedemptionFrequency().getCode());
        }

        // redemption notice period
        if(entity.getRedemptionNotificationPeriod() != null){
            dto.setRedemptionNotificationPeriod(entity.getRedemptionNotificationPeriod().getCode());
        }

        // side pocket
        if(entity.getSidePocket() != null){
            dto.setSidePocket(entity.getSidePocket().getCode());
        }


        return dto;
    }

    public List<HedgeFundDto2> disassembleList(List<HedgeFund> entities){
        List<HedgeFundDto2> dtoList = new ArrayList<>();
        if(entities != null){
            for(HedgeFund entity: entities){
                dtoList.add(disassemble(entity));
            }
        }
        return dtoList;
    }

    public Set<HedgeFundDto2> disassembleSet(List<HedgeFund> entities){
        Set<HedgeFundDto2> dtoSet = new HashSet<>();
        if(entities != null){
            for(HedgeFund entity: entities){
                dtoSet.add(disassemble(entity));
            }
        }
        return dtoSet;
    }

}
