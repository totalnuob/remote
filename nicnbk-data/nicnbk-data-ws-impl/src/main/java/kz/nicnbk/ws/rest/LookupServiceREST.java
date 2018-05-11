package kz.nicnbk.ws.rest;

import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.repo.model.m2s2.MeetingType;
import kz.nicnbk.repo.model.m2s2.MemoType;
import kz.nicnbk.service.datamanager.LookupService;
import kz.nicnbk.service.dto.reporting.NICReportingChartOfAccountsDto;
import kz.nicnbk.service.dto.reporting.TarragonNICReportingChartOfAccountsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

/**
 * Created by magzumov on 04.08.2016.
 */

@RestController
@RequestMapping("/lookup")
public class LookupServiceREST extends CommonServiceREST{

    @Autowired
    private LookupService lookupService;

    @RequestMapping(value = "/NewsType", method = RequestMethod.GET)
    public ResponseEntity getAllNewsTypes(){
        List<BaseDictionaryDto> lookups = this.lookupService.getAllNewsTypes();
        return buildNonNullResponse(lookups);
    }

    @RequestMapping(value = "/MemoType", method = RequestMethod.GET)
    public ResponseEntity getAllMemoTypes(){
        List<BaseDictionaryDto> lookups = this.lookupService.getAllMemoTypes();
        return buildNonNullResponse(lookups);
    }

    @RequestMapping(value = "/MeetingType", method = RequestMethod.GET)
    public ResponseEntity getAllMeetingTypes(){
        List<BaseDictionaryDto> lookups = this.lookupService.getAllMeetingTypes();
        return buildNonNullResponse(lookups);
    }

    @RequestMapping(value = "/PEStrategy", method = RequestMethod.GET)
    public ResponseEntity getPEStrategies(){
        List<BaseDictionaryDto> lookups = this.lookupService.getPrivateEquityStrategies();
        return buildNonNullResponse(lookups);
    }

    @RequestMapping(value = "/HFStrategy", method = RequestMethod.GET)
    public ResponseEntity getHFStrategies(){
        List<BaseDictionaryDto> lookups = this.lookupService.getHedgeFundsStrategies();
        return buildNonNullResponse(lookups);
    }

    @RequestMapping(value = "/HFSubStrategy/{strategy}", method = RequestMethod.GET)
    public ResponseEntity getHFSubStrategies(@PathVariable String strategy){
        List<BaseDictionaryDto> lookups = this.lookupService.getHedgeFundsSubStrategies(strategy);
        return buildNonNullResponse(lookups);
    }

    @RequestMapping(value = "/REStrategy", method = RequestMethod.GET)
    public ResponseEntity getREStrategies(){
        List<BaseDictionaryDto> lookups = this.lookupService.getRealEstateStrategies();
        return buildNonNullResponse(lookups);
    }

    @RequestMapping(value = "/Geography", method = RequestMethod.GET)
    public ResponseEntity getGeographies(){
        List<BaseDictionaryDto> lookups = this.lookupService.getGeographies();
        return buildNonNullResponse(lookups);
    }

    @RequestMapping(value = "/Currency", method = RequestMethod.GET)
    public ResponseEntity getCurrencies(){
        List<BaseDictionaryDto> lookups = this.lookupService.getCurrencies();
        return buildNonNullResponse(lookups);
    }

    @RequestMapping(value = "/HedgeFundStatus", method = RequestMethod.GET)
    public ResponseEntity getHedgeFundStatuses(){
        List<BaseDictionaryDto> lookups = this.lookupService.getHedgeFundStatuses();
        return buildNonNullResponse(lookups);
    }

    @RequestMapping(value = "/SubscriptionFrequency", method = RequestMethod.GET)
    public ResponseEntity getSubscriptionFrequencies(){
        List<BaseDictionaryDto> lookups = this.lookupService.getSubscriptionFrequencies();
        return buildNonNullResponse(lookups);
    }

    @RequestMapping(value = "/RedemptionFrequency", method = RequestMethod.GET)
    public ResponseEntity getRedemptionFrequencies(){
        List<BaseDictionaryDto> lookups = this.lookupService.getRedemptionFrequencies();
        return buildNonNullResponse(lookups);
    }

    @RequestMapping(value = "/RedemptionNoticePeriods", method = RequestMethod.GET)
    public ResponseEntity getRedemptionNoticePeriods(){
        List<BaseDictionaryDto> lookups = this.lookupService.getRedemptionNoticePeriods();
        return buildNonNullResponse(lookups);
    }

    @RequestMapping(value = "/SidePocket", method = RequestMethod.GET)
    public ResponseEntity getSidePockets(){
        List<BaseDictionaryDto> lookups = this.lookupService.getSidePockets();
        return buildNonNullResponse(lookups);
    }

    @RequestMapping(value = "/PEIndustry", method = RequestMethod.GET)
    public ResponseEntity getPEIndustry(){
        List<BaseDictionaryDto> lookups = this.lookupService.getPEIndustry();
        return buildNonNullResponse(lookups);
    }

    @RequestMapping(value = "/NBChartOfAccounts", method = RequestMethod.GET)
    public ResponseEntity getNBChartOfAccounts(){
        List<BaseDictionaryDto> lookups = this.lookupService.getNBChartOfAccounts();
        return buildNonNullResponse(lookups);
    }

    @RequestMapping(value = "/NICReportingChartOfAccounts/{code}", method = RequestMethod.GET)
    public ResponseEntity getNICReportingChartOfAccountsByCode(@PathVariable String code){
        List<NICReportingChartOfAccountsDto> lookups = this.lookupService.getNICReportingChartOfAccounts(code);
        Collections.sort(lookups);
        return buildNonNullResponse(lookups);
    }

    @RequestMapping(value = "/NICReportingChartOfAccounts/", method = RequestMethod.GET)
    public ResponseEntity getNICReportingChartOfAccounts(){
        List<NICReportingChartOfAccountsDto> lookups = this.lookupService.getNICReportingChartOfAccounts(null);
        Collections.sort(lookups);
        return buildNonNullResponse(lookups);
    }

    @RequestMapping(value = "/AddableTarragonNICReportingChartOfAccounts/", method = RequestMethod.GET)
    public ResponseEntity getAddableTarragonNICReportingChartOfAccounts(){
        List<TarragonNICReportingChartOfAccountsDto> lookups = this.lookupService.getAddableTarragonNICReportingChartOfAccounts();
        return buildNonNullResponse(lookups);
    }

    @RequestMapping(value = "/ReserveCalculationExpenseTypes/", method = RequestMethod.GET)
    public ResponseEntity getReserveCalculationExpenseTypeLookup(){
        List<BaseDictionaryDto> lookups = this.lookupService.getReserveCalculationExpenseTypeLookup();
        return buildNonNullResponse(lookups);
    }


    @RequestMapping(value = "/ReserveCalculationEntityTypes/", method = RequestMethod.GET)
    public ResponseEntity getReserveCalculationEntityTypeLookup(){
        List<BaseDictionaryDto> lookups = this.lookupService.getReserveCalculationEntityTypeLookup();
        return buildNonNullResponse(lookups);
    }

    @RequestMapping(value = "/ReserveCalculationExportSignerTypes/", method = RequestMethod.GET)
    public ResponseEntity getReserveCalculationExportSignerTypeLookup(){
        List<BaseDictionaryDto> lookups = this.lookupService.getReserveCalculationExportSignerTypeLookup();
        return buildNonNullResponse(lookups);
    }

    @RequestMapping(value = "/ReserveCalculationExportDoerTypes/", method = RequestMethod.GET)
    public ResponseEntity getReserveCalculationExportDoerTypeLookup(){
        List<BaseDictionaryDto> lookups = this.lookupService.getReserveCalculationExportDoerTypeLookup();
        return buildNonNullResponse(lookups);
    }

    @RequestMapping(value = "/ReserveCalculationExportApproveListTypes/", method = RequestMethod.GET)
    public ResponseEntity getReserveCalculationExportApproveListTypeLookup(){
        List<BaseDictionaryDto> lookups = this.lookupService.getReserveCalculationExportApproveListTypeLookup();
        return buildNonNullResponse(lookups);
    }

    @RequestMapping(value = "/MMFields", method = RequestMethod.GET)
    public List<BaseDictionaryDto> getMMFields(){
        List<BaseDictionaryDto> lookups = this.lookupService.getMMFields();
        return lookups;
    }

}