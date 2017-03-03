package kz.nicnbk.ws.rest;

import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.service.datamanager.LookupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by magzumov on 04.08.2016.
 */

@RestController
@RequestMapping("/lookup")
public class LookupServiceREST {

    @Autowired
    private LookupService lookupService;

    @RequestMapping(value = "/PEStrategy", method = RequestMethod.GET)
    public List<BaseDictionaryDto> getPEStrategies(){
        List<BaseDictionaryDto> lookups = this.lookupService.getPrivateEquityStrategies();
        return lookups;
    }

    @RequestMapping(value = "/HFStrategy", method = RequestMethod.GET)
    public List<BaseDictionaryDto> getHFStrategies(){
        List<BaseDictionaryDto> lookups = this.lookupService.getHedgeFundsStrategies();
        return lookups;
    }

    @RequestMapping(value = "/HFSubStrategy/{strategy}", method = RequestMethod.GET)
    public List<BaseDictionaryDto> getHFSubStrategies(@PathVariable String strategy){
        List<BaseDictionaryDto> lookups = this.lookupService.getHedgeFundsSubStrategies(strategy);
        return lookups;
    }

    @RequestMapping(value = "/REStrategy", method = RequestMethod.GET)
    public List<BaseDictionaryDto> getREStrategies(){
        List<BaseDictionaryDto> lookups = this.lookupService.getRealEstateStrategies();
        return lookups;
    }

    @RequestMapping(value = "/Geography", method = RequestMethod.GET)
    public List<BaseDictionaryDto> getGeographies(){
        List<BaseDictionaryDto> lookups = this.lookupService.getGeographies();
        return lookups;
    }

    @RequestMapping(value = "/Currency", method = RequestMethod.GET)
    public List<BaseDictionaryDto> getCurrencies(){
        List<BaseDictionaryDto> lookups = this.lookupService.getCurrencies();
        return lookups;
    }

    @RequestMapping(value = "/HedgeFundStatus", method = RequestMethod.GET)
    public List<BaseDictionaryDto> getHedgeFundStatuses(){
        List<BaseDictionaryDto> lookups = this.lookupService.getHedgeFundStatuses();
        return lookups;
    }

    @RequestMapping(value = "/SubscriptionFrequency", method = RequestMethod.GET)
    public List<BaseDictionaryDto> getSubscriptionFrequencies(){
        List<BaseDictionaryDto> lookups = this.lookupService.getSubscriptionFrequencies();
        return lookups;
    }

    @RequestMapping(value = "/RedemptionFrequency", method = RequestMethod.GET)
    public List<BaseDictionaryDto> getRedemptionFrequencies(){
        List<BaseDictionaryDto> lookups = this.lookupService.getRedemptionFrequencies();
        return lookups;
    }

    @RequestMapping(value = "/RedemptionNoticePeriods", method = RequestMethod.GET)
    public List<BaseDictionaryDto> getRedemptionNoticePeriods(){
        List<BaseDictionaryDto> lookups = this.lookupService.getRedemptionNoticePeriods();
        return lookups;
    }

    @RequestMapping(value = "/SidePocket", method = RequestMethod.GET)
    public List<BaseDictionaryDto> getSidePockets(){
        List<BaseDictionaryDto> lookups = this.lookupService.getSidePockets();
        return lookups;
    }

    @RequestMapping(value = "/PEIndustry", method = RequestMethod.GET)
    public List<BaseDictionaryDto> getPEIndustry(){
        List<BaseDictionaryDto> lookups = this.lookupService.getPEIndustry();
        return lookups;
    }
}