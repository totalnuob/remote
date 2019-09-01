package kz.nicnbk.ws.rest;

import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.service.api.authentication.TokenService;
import kz.nicnbk.service.api.benchmark.BenchmarkService;
import kz.nicnbk.service.api.common.CurrencyRatesService;
import kz.nicnbk.service.datamanager.LookupService;
import kz.nicnbk.service.dto.benchmark.BenchmarkValueDto;
import kz.nicnbk.service.dto.common.EntityListSaveResponseDto;
import kz.nicnbk.service.dto.common.EntitySaveResponseDto;
import kz.nicnbk.service.dto.lookup.*;
import kz.nicnbk.service.dto.reporting.*;
import kz.nicnbk.service.dto.reporting.realestate.TerraNICReportingChartOfAccountsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

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

    @Autowired
    private TokenService tokenService;

    @Autowired
    private CurrencyRatesService currencyRatesService;

    @Autowired
    private BenchmarkService benchmarkService;

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

    @RequestMapping(value = "/CorpMeetingType", method = RequestMethod.GET)
    public ResponseEntity getAllCorpMeetingTypes(){
        List<BaseDictionaryDto> lookups = this.lookupService.getAllCorpMeetingTypes();
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

    @RequestMapping(value = "/BenchmarkType", method = RequestMethod.GET)
    public ResponseEntity getBenchmarkTypes(){
        List<BaseDictionaryDto> lookups = this.lookupService.getBenchmarkTypes();
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

    // TODO: replace with searchNICReportingChartOfAccountsByCode(...)

    @RequestMapping(value = "/NICReportingChartOfAccounts/{code}", method = RequestMethod.GET)
    public ResponseEntity getNICReportingChartOfAccountsByCode(@PathVariable String code){
        List<NICReportingChartOfAccountsDto> lookups = this.lookupService.getNICReportingChartOfAccounts(code);
        Collections.sort(lookups);
        return buildNonNullResponse(lookups);
    }

    @RequestMapping(value = "/searchNICReportingChartOfAccounts", method = RequestMethod.POST)
    public ResponseEntity searchNICReportingChartOfAccountsByCode(@RequestBody NICChartOfAccountsSearchParamsDto searchParams){
        NICChartOfAccountsPagedSearchResultDto searchResult = this.lookupService.searchNICReportingChartOfAccounts(searchParams);
        return buildNonNullResponse(searchResult);
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

    @RequestMapping(value = "/AddableTerraNICReportingChartOfAccounts/", method = RequestMethod.GET)
    public ResponseEntity getAddableTerraNICReportingChartOfAccounts(){
        List<TerraNICReportingChartOfAccountsDto> lookups = this.lookupService.getAddableTerraNICReportingChartOfAccounts();
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


    @RequestMapping(value = "/currencyRates", method = RequestMethod.POST)
    public CurrencyRatesPagedSearchResult getCurrencyRates(@RequestBody CurrencyRatesSearchParams searchParams){
        CurrencyRatesPagedSearchResult lookups = this.currencyRatesService.search(searchParams);
        return lookups;
    }

    @RequestMapping(value = "/benchmarks", method = RequestMethod.POST)
    public BenchmarkPagedSearchResult getBenchmarkValues(@RequestBody BenchmarkSearchParams searchParams){
        BenchmarkPagedSearchResult searchResult = this.benchmarkService.search(searchParams);
        return searchResult;
    }

    @PreAuthorize("hasRole('ROLE_LOOKUPS_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/currencyRates/save", method = RequestMethod.POST)
    public ResponseEntity saveCurrencyRates(@RequestBody CurrencyRatesDto currencyRatesDto){
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        EntitySaveResponseDto saveResponse = this.currencyRatesService.save(currencyRatesDto, username);
        return buildEntitySaveResponse(saveResponse);
    }

    @PreAuthorize("hasRole('ROLE_LOOKUPS_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/currencyRates/saveList", method = RequestMethod.POST)
    public ResponseEntity saveCurrencyRatesList(@RequestBody List<CurrencyRatesDto> currencyRatessDtoList){
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        EntityListSaveResponseDto saveResponse = this.currencyRatesService.save(currencyRatessDtoList, username);
        return buildEntityListSaveResponse(saveResponse);
    }

    @PreAuthorize("hasRole('ROLE_LOOKUPS_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/benchmarks/save", method = RequestMethod.POST)
    public ResponseEntity saveBenchmarkValue(@RequestBody BenchmarkValueDto benchmarkValueDto){
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        EntitySaveResponseDto saveResponse = this.benchmarkService.save(benchmarkValueDto, username);
        return buildEntitySaveResponse(saveResponse);
    }

    @PreAuthorize("hasRole('ROLE_LOOKUPS_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/benchmarks/saveList", method = RequestMethod.POST)
    public ResponseEntity saveBenchmarksList(@RequestBody List<BenchmarkValueDto> benchmarkDtoList){
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        EntityListSaveResponseDto saveResponse = this.benchmarkService.save(benchmarkDtoList, username);
        return buildEntityListSaveResponse(saveResponse);
    }

    @PreAuthorize("hasRole('ROLE_LOOKUPS_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/currencyRates/delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity deleteCurrencyRates(@PathVariable Long id){
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        boolean deleted = this.currencyRatesService.delete(id, username);
        return buildDeleteResponseEntity(deleted);
    }

    @RequestMapping(value = "/repTarragonBalanceType/", method = RequestMethod.GET)
    public ResponseEntity getNBReportingTarragonBalanceTypeLookup(){
        List<BaseDictionaryDto> lookups = this.lookupService.getNBReportingTarragonBalanceTypeLookup();
        return buildNonNullResponse(lookups);
    }

    @RequestMapping(value = "/repTarragonOperationsType/", method = RequestMethod.GET)
    public ResponseEntity getNBReportingTarragonOperationsTypeLookup(){
        List<BaseDictionaryDto> lookups = this.lookupService.getNBReportingTarragonOperationsTypeLookup();
        return buildNonNullResponse(lookups);
    }

    @RequestMapping(value = "/repTarragonCashflowsType/", method = RequestMethod.GET)
    public ResponseEntity getNBReportingTarragonCashflowsTypeLookup(){
        List<BaseDictionaryDto> lookups = this.lookupService.getNBReportingTarragonCashflowsTypeLookup();
        return buildNonNullResponse(lookups);
    }

    @RequestMapping(value = "/repSingularityChartAccountsType/", method = RequestMethod.GET)
    public ResponseEntity getNBReportingSingularityChartAccountsTypeLookup(){
        List<BaseDictionaryDto> lookups = this.lookupService.getNBReportingSingularityChartAccountsTypeLookup();
        return buildNonNullResponse(lookups);
    }

//    @RequestMapping(value = "/repTerraChartAccountsType/", method = RequestMethod.GET)
//    public ResponseEntity getNBReportingTerraChartAccountsTypeLookup(){
//        List<BaseDictionaryDto> lookups = this.lookupService.getNBReportingTerraChartAccountsTypeLookup();
//        return buildNonNullResponse(lookups);
//    }

    @RequestMapping(value = "/repTerraBalanceType/", method = RequestMethod.GET)
    public ResponseEntity getNBReportingTerraBalanceTypeLookup(){
        List<BaseDictionaryDto> lookups = this.lookupService.getNBReportingTerraBalanceTypeLookup();
        return buildNonNullResponse(lookups);
    }

    @RequestMapping(value = "/repTerraProfitLossType/", method = RequestMethod.GET)
    public ResponseEntity getNBReportingTerraProfitLossTypeLookup(){
        List<BaseDictionaryDto> lookups = this.lookupService.getNBReportingTerraProfitLossTypeLookup();
        return buildNonNullResponse(lookups);
    }

//    @RequestMapping(value = "/nbChartAccounts", method = RequestMethod.GET)
//    public ResponseEntity getNBChartAccountsLookup(){
//        List<BaseDictionaryDto> lookups = this.lookupService.getNBChartAccountsLookup();
//        return buildNonNullResponse(lookups);
//    }

    @PreAuthorize("hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/saveLookupValue/{type}", method = RequestMethod.POST)
    public ResponseEntity saveLookupValue(@PathVariable String type, @RequestBody BaseDictionaryDto lookupValue){
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        EntitySaveResponseDto saveResponseDto = this.lookupService.saveTypedLookupValue(type, lookupValue, username);
        return buildEntitySaveResponse(saveResponseDto);
    }

    @PreAuthorize("hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/lookupValue/delete/{type}/{id}", method = RequestMethod.DELETE)
    public ResponseEntity deleteLookupValue(@PathVariable String type, @PathVariable Integer id){
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        boolean deleted = this.lookupService.deleteTypedLookupValueByTypeAndId(type, id, username);
        return buildDeleteResponseEntity(deleted);
    }

    @RequestMapping(value = "/periodicDataTypes", method = RequestMethod.GET)
    public ResponseEntity getPeriodicDataTypesLookup(){
        List<BaseDictionaryDto> lookups = this.lookupService.getPeriodicDataTypesLookup();
        return buildNonNullResponse(lookups);
    }

    @RequestMapping(value = "/SingularityNICReportingChartOfAccounts/", method = RequestMethod.GET)
    public ResponseEntity getSingularityNICReportingChartOfAccounts(){
        List<CommonNICReportingChartOfAccountsDto> lookups = this.lookupService.getSingularityNICReportingChartOfAccounts();
        return buildNonNullResponse(lookups);
    }

    @RequestMapping(value = "/TarragonNICReportingChartOfAccounts/", method = RequestMethod.GET)
    public ResponseEntity getTarragonNICReportingChartOfAccounts(){
        List<CommonNICReportingChartOfAccountsDto> lookups = this.lookupService.getTarragonNICReportingChartOfAccounts();
        return buildNonNullResponse(lookups);
    }

    @RequestMapping(value = "/TerraNICReportingChartOfAccounts/", method = RequestMethod.GET)
    public ResponseEntity getTerraNICReportingChartOfAccounts(){
        List<CommonNICReportingChartOfAccountsDto> lookups = this.lookupService.getTerraNICReportingChartOfAccounts();
        return buildNonNullResponse(lookups);
    }

    @PreAuthorize("hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/saveMatchingNICChartAccounts/{type}", method = RequestMethod.POST)
    public ResponseEntity saveMatchingNICChartAccounts(@PathVariable String type, @RequestBody CommonNICReportingChartOfAccountsDto dto){
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        EntitySaveResponseDto saveResponseDto = this.lookupService.saveMatchingNICChartAccounts(type, dto, username);
        return buildEntitySaveResponse(saveResponseDto);
    }

    @PreAuthorize("hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/saveNICChartAccounts/", method = RequestMethod.POST)
    public ResponseEntity saveNICChartAccounts(@RequestBody NICReportingChartOfAccountsDto dto){
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        EntitySaveResponseDto saveResponseDto = this.lookupService.saveNICChartOfAccounts(dto, username);
        return buildEntitySaveResponse(saveResponseDto);
    }

    @PreAuthorize("hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/matchingLookupValue/delete/{type}/{id}", method = RequestMethod.DELETE)
    public ResponseEntity deleteMatchingLookupValue(@PathVariable String type, @PathVariable Long id){
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        boolean deleted = this.lookupService.deleteMatchingLookupByTypeAndId(type, id, username);
        return buildDeleteResponseEntity(deleted);
    }

    @RequestMapping(value = "/ICMeetingTopicTypes", method = RequestMethod.GET)
    public ResponseEntity getAllICMeetingTopicTypes(){
        List<BaseDictionaryDto> lookups = this.lookupService.getICMeetingTopicTypes();
        return buildNonNullResponse(lookups);
    }
}