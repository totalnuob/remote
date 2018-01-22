package kz.nicnbk.ws.rest;

import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.repo.model.m2s2.MeetingType;
import kz.nicnbk.repo.model.m2s2.MemoType;
import kz.nicnbk.service.datamanager.LookupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
public class LookupServiceREST extends CommonServiceREST{

    @Autowired
    private LookupService lookupService;

    @RequestMapping(value = "/MemoType", method = RequestMethod.GET)
    public ResponseEntity getAllMemoTypes(){
        List<BaseDictionaryDto> lookups = this.lookupService.getAllMemoTypes();
        return buildResponse(lookups);
    }

    @RequestMapping(value = "/MeetingType", method = RequestMethod.GET)
    public ResponseEntity getAllMeetingTypes(){
        List<BaseDictionaryDto> lookups = this.lookupService.getAllMeetingTypes();
        return buildResponse(lookups);
    }

    @RequestMapping(value = "/PEStrategy", method = RequestMethod.GET)
    public ResponseEntity getPEStrategies(){
        List<BaseDictionaryDto> lookups = this.lookupService.getPrivateEquityStrategies();
        return buildResponse(lookups);
    }

    @RequestMapping(value = "/HFStrategy", method = RequestMethod.GET)
    public ResponseEntity getHFStrategies(){
        List<BaseDictionaryDto> lookups = this.lookupService.getHedgeFundsStrategies();
        return buildResponse(lookups);
    }

    @RequestMapping(value = "/HFSubStrategy/{strategy}", method = RequestMethod.GET)
    public ResponseEntity getHFSubStrategies(@PathVariable String strategy){
        List<BaseDictionaryDto> lookups = this.lookupService.getHedgeFundsSubStrategies(strategy);
        return buildResponse(lookups);
    }

    @RequestMapping(value = "/REStrategy", method = RequestMethod.GET)
    public ResponseEntity getREStrategies(){
        List<BaseDictionaryDto> lookups = this.lookupService.getRealEstateStrategies();
        return buildResponse(lookups);
    }

    @RequestMapping(value = "/Geography", method = RequestMethod.GET)
    public ResponseEntity getGeographies(){
        List<BaseDictionaryDto> lookups = this.lookupService.getGeographies();
        return buildResponse(lookups);
    }

    @RequestMapping(value = "/Currency", method = RequestMethod.GET)
    public ResponseEntity getCurrencies(){
        List<BaseDictionaryDto> lookups = this.lookupService.getCurrencies();
        return buildResponse(lookups);
    }

    @RequestMapping(value = "/HedgeFundStatus", method = RequestMethod.GET)
    public ResponseEntity getHedgeFundStatuses(){
        List<BaseDictionaryDto> lookups = this.lookupService.getHedgeFundStatuses();
        return buildResponse(lookups);
    }

    @RequestMapping(value = "/SubscriptionFrequency", method = RequestMethod.GET)
    public ResponseEntity getSubscriptionFrequencies(){
        List<BaseDictionaryDto> lookups = this.lookupService.getSubscriptionFrequencies();
        return buildResponse(lookups);
    }

    @RequestMapping(value = "/RedemptionFrequency", method = RequestMethod.GET)
    public ResponseEntity getRedemptionFrequencies(){
        List<BaseDictionaryDto> lookups = this.lookupService.getRedemptionFrequencies();
        return buildResponse(lookups);
    }

    @RequestMapping(value = "/RedemptionNoticePeriods", method = RequestMethod.GET)
    public ResponseEntity getRedemptionNoticePeriods(){
        List<BaseDictionaryDto> lookups = this.lookupService.getRedemptionNoticePeriods();
        return buildResponse(lookups);
    }

    @RequestMapping(value = "/SidePocket", method = RequestMethod.GET)
    public ResponseEntity getSidePockets(){
        List<BaseDictionaryDto> lookups = this.lookupService.getSidePockets();
        return buildResponse(lookups);
    }

    @RequestMapping(value = "/PEIndustry", method = RequestMethod.GET)
    public ResponseEntity getPEIndustry(){
        List<BaseDictionaryDto> lookups = this.lookupService.getPEIndustry();
        return buildResponse(lookups);
    }

    @RequestMapping(value = "/MMFields", method = RequestMethod.GET)
    public List<BaseDictionaryDto> getMMFields(){
        List<BaseDictionaryDto> lookups = this.lookupService.getMMFields();
        return lookups;
    }

}