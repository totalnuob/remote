package kz.nicnbk.service.datamanager;

import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.common.service.util.PaginationUtils;
import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.repo.api.corpmeetings.ICMeetingVoteRepository;
import kz.nicnbk.repo.api.lookup.*;
import kz.nicnbk.repo.api.lookup.CurrencyRepository;
import kz.nicnbk.repo.api.lookup.GeographyRepository;
import kz.nicnbk.repo.api.lookup.StrategyRepository;
import kz.nicnbk.repo.api.macromonitor.MacroMonitorFieldRepository;
import kz.nicnbk.repo.api.macromonitor.MacroMonitorTypeRepository;
import kz.nicnbk.repo.api.pe.IndustryRepository;
import kz.nicnbk.repo.api.reporting.*;
import kz.nicnbk.repo.api.reporting.hedgefunds.SingularityNICChartOfAccountsRepository;
import kz.nicnbk.repo.api.reporting.privateequity.TarragonNICChartOfAccountsRepository;
import kz.nicnbk.repo.api.reporting.realestate.TerraNICChartOfAccountsRepository;
import kz.nicnbk.repo.model.base.BaseTypeEntity;
import kz.nicnbk.repo.model.base.BaseTypeEntityImpl;
import kz.nicnbk.repo.model.benchmark.Benchmark;
import kz.nicnbk.repo.model.common.*;
import kz.nicnbk.repo.model.corpmeetings.*;
import kz.nicnbk.repo.model.files.FilesType;
import kz.nicnbk.repo.model.hf.*;
import kz.nicnbk.repo.model.m2s2.MeetingArrangedBy;
import kz.nicnbk.repo.model.m2s2.MeetingType;
import kz.nicnbk.repo.model.m2s2.MemoType;
import kz.nicnbk.repo.model.macromonitor.MacroMonitorField;
import kz.nicnbk.repo.model.macromonitor.MacroMonitorType;
import kz.nicnbk.repo.model.news.NewsType;
import kz.nicnbk.repo.model.pe.PEIndustry;
import kz.nicnbk.repo.model.reporting.*;
import kz.nicnbk.repo.model.reporting.PeriodicReportType;
import kz.nicnbk.repo.model.reporting.hedgefunds.FinancialStatementCategory;
import kz.nicnbk.repo.model.reporting.hedgefunds.HFChartOfAccountsType;
import kz.nicnbk.repo.model.reporting.hedgefunds.SingularityNICChartOfAccounts;
import kz.nicnbk.repo.model.reporting.privateequity.*;
import kz.nicnbk.repo.model.reporting.realestate.*;
import kz.nicnbk.repo.model.risk.PortfolioVar;
import kz.nicnbk.repo.model.tripmemo.TripType;
import kz.nicnbk.service.api.reporting.PeriodicReportService;
import kz.nicnbk.service.api.reporting.hedgefunds.HFGeneralLedgerBalanceService;
import kz.nicnbk.service.api.reporting.privateequity.PEStatementBalanceService;
import kz.nicnbk.service.api.reporting.privateequity.PEStatementCashflowsService;
import kz.nicnbk.service.api.reporting.privateequity.PEStatementOperationsService;
import kz.nicnbk.service.api.reporting.privateequity.PeriodicReportPEService;
import kz.nicnbk.service.api.reporting.realestate.PeriodicReportREService;
import kz.nicnbk.service.dto.common.EntitySaveResponseDto;
import kz.nicnbk.service.dto.common.ListResponseDto;
import kz.nicnbk.service.dto.reporting.*;
import kz.nicnbk.service.dto.reporting.realestate.TerraNICReportingChartOfAccountsDto;
import kz.nicnbk.service.dto.strategy.StrategyDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by magzumov on 07.07.2016.
 */
@Service
public class LookupServiceImpl implements LookupService {

    private static final Logger logger = LoggerFactory.getLogger(LookupServiceImpl.class);

    int DEFAULT_PAGES_PER_VIEW = 5;

    /* Number of elements per page */
    int DEFAULT_PAGE_SIZE = 20;


    @Autowired
    private GeographyRepository geographyRepository;

    @Autowired
    private StrategyRepository strategyRepository;

    @Autowired
    private SubstrategyRepository substrategyRepository;

    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    private IndustryRepository industryRepository;

    @Autowired
    private HedgeFundStatusRepository hedgeFundStatusRepository;

    @Autowired
    private ManagerStatusRepository managerStatusRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private LegalStructureRepository legalStructureRepository;

    @Autowired
    private SubscriptionFrequencyRepository subscriptionFrequencyRepository;

    @Autowired
    private RedemptionFrequencyRepository redemptionFrequencyRepository;

    @Autowired
    private RedemptionNotificationPeriodRepository redemptionNotificationPeriodRepository;

    @Autowired
    private HedgeFundSidePocketRepository hedgeFundSidePocketRepository;

    @Autowired
    private NewsTypeRepository newsTypeRepository;

    @Autowired
    private MemoTypeRepository memoTypeRepository;

    @Autowired
    private MeetingTypeRepository meetingTypeRepository;

    @Autowired
    private MeetingArrangedByRepository meetingArrangedByRepository;

    @Autowired
    private FilesTypeRepository filesTypeRepository;

    @Autowired
    private MacroMonitorFieldRepository macroMonitorFieldRepository;

    @Autowired
    private MacroMonitorTypeRepository macroMonitorTypeRepository;

    @Autowired
    private TripTypeRepository tripTypeRepository;

    @Autowired
    private PeriodicReportTypeRepository periodicReportTypeRepository;

    @Autowired
    private ReportStatusRepository reportStatusRepository;

    @Autowired
    private PEInvestmentTypeRepository peInvestmentTypeRepository;

    @Autowired
    private HFFinancialStatementTypeRepository hfFinancialStatementTypeRepository;

    @Autowired
    private NBChartOfAccountsRepository nbChartOfAccountsRepository;

    @Autowired
    private PeriodicDataChartAccountsTypeRepository chartAccountsTypeRepository;

    @Autowired
    private NICReportingChartOfAccountsRepository nicReportingChartOfAccountsRepository;

    @Autowired
    private TarragonNICChartOfAccountsRepository tarragonNICChartOfAccountsRepository;

    @Autowired
    private PETrancheTypeRepository peTrancheTypeRepository;

    @Autowired
    private RETrancheTypeRepository reTrancheTypeRepository;

    @Autowired
    private ICMeetingAttendeeAbsenceTypeRepository icMeetingAttendeeAbsenceTypeRepository;

    @Autowired
    private ICMeetingPlaceTypeRepository icMeetingPlaceTypeRepository;

    @Autowired
    private ICMeetingVoteTypeRepository icMeetingVoteTypeRepository;

    @Autowired
    private TerraNICChartOfAccountsRepository terraNICChartOfAccountsRepository;

    @Autowired
    private ReserveCalculationExpenseTypeRepository reserveCalculationExpenseTypeRepository;

    @Autowired
    private ReserveCalculationEntityTypeRepository reserveCalculationEntityTypeRepository;

    @Autowired
    private CorpMeetingTypeRepository corpMeetingTypeRepository;

    @Autowired
    private ReserveCalculationExportSignerTypeRepository reserveCalculationExportSignerTypeRepository;

    @Autowired
    private ReserveCalculationExportDoerTypeRepository reserveCalculationExportDoerTypeRepository;

    @Autowired
    private ReserveCalculationExportApproveListTypeRepository reserveCalculationExportApproveListTypeRepository;

    @Autowired
    private REBalanceTypeRepository reBalanceTypeRepository;

    @Autowired
    private PEBalanceTypeRepository peBalanceTypeRepository;

    @Autowired
    private PEOperationsTypeRepository peOperationsTypeRepository;

    @Autowired
    private PECashflowsTypeRepository peCashflowsTypeRepository;

    @Autowired
    private HFChartOfAccountsTypeRepository hfChartOfAccountsTypeRepository;

    @Autowired
    private REChartOfAccountsTypeRepository reChartOfAccountsTypeRepository;

    @Autowired
    private REProfitLossTypeRepository reProfitLossTypeRepository;

    @Autowired
    private PeriodicDataTypeRepository periodicDataTypeRepository;

    @Autowired
    private SingularityNICChartOfAccountsRepository singularityNICChartOfAccountsRepository;

    @Autowired
    private PEStatementBalanceService peStatementBalanceService;

    @Autowired
    private PEStatementOperationsService peStatementOperationsService;

    @Autowired
    private PEStatementCashflowsService peStatementCashflowsService;

    @Autowired
    private HFGeneralLedgerBalanceService hfGeneralLedgerBalanceService;

    @Autowired
    private PeriodicReportREService reService;

    @Autowired
    private PeriodicReportService periodicReportService;

    @Autowired
    private PeriodicReportPEService periodicReportPEService;

    @Autowired
    private PeriodicReportREService periodicReportREService;

    @Autowired
    private ICMeetingTypeRepository icMeetingTypeRepository;

    @Autowired
    private BenchmarkRepository benchmarkTypeRepository;

    @Autowired
    private PortfolioVarRepository portfolioTypeRepository;

    @Autowired
    private BloombergStationRepository bloombergStationRepository;


    @Override
    public <T extends BaseTypeEntity> T findByTypeAndCode(Class<T> clazz, String code) {

        // TODO: implement lookup cash

        try {
            if (code == null) {
                return null;
            }else{
                code = code.trim();
            }

            if (clazz.equals(NewsType.class)) {
                return (T) this.newsTypeRepository.findByCode(code);
            } else if (clazz.equals(MeetingType.class)) {
                return (T) this.meetingTypeRepository.findByCode(code);
            } else if (clazz.equals(MeetingArrangedBy.class)) {
                return (T) this.meetingArrangedByRepository.findByCode(code);
            } else if (clazz.equals(Currency.class)) {
                return (T) this.currencyRepository.findByCode(code);
            } else if (clazz.equals(FilesType.class)) {
                return (T) this.filesTypeRepository.findByCode(code);
            } else if (clazz.equals(Strategy.class)) {
                return (T) this.strategyRepository.findByCode(code);
            } else if (clazz.equals(Geography.class)) {
                return (T) this.geographyRepository.findByCode(code);
            } else if (clazz.equals(HedgeFundStatus.class)) {
                return (T) this.managerStatusRepository.findByCode(code);
            } else if (clazz.equals(Country.class)) {
                return (T) this.countryRepository.findByCode(code);
            } else if (clazz.equals(LegalStructure.class)) {
                return (T) this.legalStructureRepository.findByCode(code);
            } else if (clazz.equals(SubscriptionFrequency.class)) {
                return (T) this.subscriptionFrequencyRepository.findByCode(code);
            } else if (clazz.equals(RedemptionFrequency.class)) {
                return (T) this.redemptionFrequencyRepository.findByCode(code);
            } else if (clazz.equals(RedemptionNotificationPeriod.class)) {
                return (T) this.redemptionNotificationPeriodRepository.findByCode(code);
            } else if (clazz.equals(HedgeFundSidePocket.class)) {
                return (T) this.hedgeFundSidePocketRepository.findByCode(code);
            } else if (clazz.equals(Substrategy.class)) {
                return (T) this.substrategyRepository.findByCode(code);
            }else if (clazz.equals(Strategy.class)) {
                return (T) this.strategyRepository.findByCode(code);
            } else if(clazz.equals(TripType.class)){
                return (T) this.tripTypeRepository.findByCode(code);
            } else if (clazz.equals(Geography.class)) {
                return (T) this.geographyRepository.findByCode(code);
            }else if (clazz.equals(PEIndustry.class)) {
                return (T) this.industryRepository.findByCode(code);
            } else if(clazz.equals(MacroMonitorField.class)){
                return (T) this.macroMonitorFieldRepository.findByCodeOrderByIdAsc(code);
            } else if(clazz.equals(MacroMonitorType.class)){
                return (T) this.macroMonitorTypeRepository.findByCode(code);
            }else if (clazz.equals(PeriodicReportType.class)) {
                return (T) this.periodicReportTypeRepository.findByCode(code);
            } else if (clazz.equals(ReportStatus.class)) {
                return (T) this.reportStatusRepository.findByCode(code);
            } else if (clazz.equals(PEInvestmentType.class)) {
                return (T) this.peInvestmentTypeRepository.findByCode(code);
            } else if (clazz.equals(FinancialStatementCategory.class)) {
                return (T) this.hfFinancialStatementTypeRepository.findByCode(code);
            } else if (clazz.equals(NICReportingChartOfAccounts.class)) {
                return (T) this.nicReportingChartOfAccountsRepository.findByCode(code);
            } else if (clazz.equals(ReserveCalculationExpenseType.class)) {
                return (T) this.reserveCalculationExpenseTypeRepository.findByCode(code);
            } else if (clazz.equals(ReserveCalculationEntityType.class)) {
                return (T) this.reserveCalculationEntityTypeRepository.findByCode(code);
            } else if (clazz.equals(ReserveCalculationExportSignerType.class)) {
                return (T) this.reserveCalculationExportSignerTypeRepository.findByCode(code);
            } else if (clazz.equals(ReserveCalculationExportDoerType.class)) {
                return (T) this.reserveCalculationExportDoerTypeRepository.findByCode(code);
            } else if (clazz.equals(ReserveCalculationExportApproveListType.class)) {
                return (T) this.reserveCalculationExportApproveListTypeRepository.findByCode(code);
            } else if (clazz.equals(CorpMeetingType.class)) {
                return (T) this.corpMeetingTypeRepository.findByCode(code);
            } else if (clazz.equals(REBalanceType.class)) {
                return (T) this.reBalanceTypeRepository.findByCode(code);
            } else if (clazz.equals(ICMeetingTopicType.class)) {
                return (T) this.icMeetingTypeRepository.findByCode(code);
            } else if (clazz.equals(Benchmark.class)) {
                return (T) this.benchmarkTypeRepository.findByCode(code);
            } else if (clazz.equals(PortfolioVar.class)) {
                return (T) this.portfolioTypeRepository.findByCode(code);
            } else if (clazz.equals(REChartOfAccountsType.class)) {
                return (T) this.reChartOfAccountsTypeRepository.findByCode(code);
            }else if(clazz.equals(PETrancheType.class)){
                return (T) this.peTrancheTypeRepository.findByCode(code);
            }else if(clazz.equals(RETrancheType.class)){
                return (T) this.reTrancheTypeRepository.findByCode(code);
            }else if(clazz.equals(ICMeetingAttendeeAbsenceType.class)){
                return (T) this.icMeetingAttendeeAbsenceTypeRepository.findByCode(code);
            }else if(clazz.equals(ICMeetingPlaceType.class)){
                return (T) this.icMeetingPlaceTypeRepository.findByCode(code);
            }else if(clazz.equals(ICMeetingVoteType.class)){
                return (T) this.icMeetingVoteTypeRepository.findByCode(code);
            }else{
                logger.error("Failed to load lookups for clazz=" + clazz + ", code=" + code);
            }
        }catch (Exception ex){
            logger.error("Failed to load lookups with error: clazz=" + clazz + ", code=" + code, ex);
        }
        if(clazz.getSimpleName().equals("Geography")){
            Iterator<Geography> iterator = geographyRepository.findAll().iterator();
            while(iterator.hasNext()){
                Geography geography = iterator.next();
                if(geography.getCode().equals(code)){
                    return (T) geography;
                };
            }
        }

        if(clazz.getSimpleName().equals("PEIndustry")) {
            Iterator<PEIndustry> iterator = industryRepository.findAll().iterator();
            while(iterator.hasNext()){
                PEIndustry PEIndustry = iterator.next();
                if(PEIndustry.getCode().equals(code)) {
                    return (T) PEIndustry;
                }
            }
        }

        return null;
    }


    @Override
    public List<BaseDictionaryDto> getCurrencies(){
        try {
            List<BaseDictionaryDto> dtoList = new ArrayList<>();
            Iterator<Currency> iterator = this.currencyRepository.findAll().iterator();
            while (iterator.hasNext()) {
                Currency currency = iterator.next();
                BaseDictionaryDto geographyDto = disassemble(currency);
                dtoList.add(geographyDto);
            }
            return dtoList;
        }catch (Exception ex){
            logger.error("Failed to load lookup: Currency", ex);
        }
        return null;
    }

    @Override
    public List<BaseDictionaryDto> getBenchmarkTypes() {
        try {
            List<BaseDictionaryDto> dtoList = new ArrayList<>();
            Iterator<Benchmark> iterator = this.benchmarkTypeRepository.findAll().iterator();
            while (iterator.hasNext()) {
                Benchmark entity = iterator.next();
                BaseDictionaryDto dto = disassemble(entity);
                dtoList.add(dto);
            }
            return dtoList;
        }catch (Exception ex){
            logger.error("Failed to load lookup: Benchmark", ex);
        }
        return null;
    }

    @Override
    public List<BaseDictionaryDto> getBloombergStations() {
        try {
            List<BaseDictionaryDto> dtoList = new ArrayList<>();
            Iterator<BloombergStation> iterator = this.bloombergStationRepository.findAll().iterator();
            while (iterator.hasNext()) {
                BloombergStation entity = iterator.next();
                BaseDictionaryDto dto = disassemble(entity);
                dtoList.add(dto);
            }
            return dtoList;
        }catch (Exception ex){
            logger.error("Failed to load lookup: Bloomberg Station", ex);
        }
        return null;
    }

    @Override
    public List<BaseDictionaryDto> getPortfolioVarTypes() {
        try {
            List<BaseDictionaryDto> dtoList = new ArrayList<>();
            Iterator<PortfolioVar> iterator = this.portfolioTypeRepository.findAll().iterator();
            while (iterator.hasNext()) {
                PortfolioVar entity = iterator.next();
                BaseDictionaryDto dto = disassemble(entity);
                dtoList.add(dto);
            }
            return dtoList;
        } catch (Exception ex) {
            logger.error("Failed to load lokup: Portfolio VaR", ex);
        }

        return null;
    }

    @Override
    public List<BaseDictionaryDto> getHedgeFundStatuses() {
        try {
            List<BaseDictionaryDto> dtoList = new ArrayList<>();
            Iterator<HedgeFundStatus> iterator = this.hedgeFundStatusRepository.findAll().iterator();
            while (iterator.hasNext()) {
                HedgeFundStatus entity = iterator.next();
                BaseDictionaryDto dto = disassemble(entity);
                dtoList.add(dto);
            }
            return dtoList;
        }catch (Exception ex){
            logger.error("Failed to load lookup: HedgeFundStatus", ex);
        }
        return null;
    }

    @Override
    public List<BaseDictionaryDto> getSubscriptionFrequencies() {
        try {
            List<BaseDictionaryDto> dtoList = new ArrayList<>();
            Iterator<SubscriptionFrequency> iterator = this.subscriptionFrequencyRepository.findAll().iterator();
            while (iterator.hasNext()) {
                SubscriptionFrequency entity = iterator.next();
                BaseDictionaryDto dto = disassemble(entity);
                dtoList.add(dto);
            }
            return dtoList;
        }catch (Exception ex){
            logger.error("Failed to load lookup: SubscriptionFrequency", ex);
        }
        return null;
    }

    @Override
    public List<BaseDictionaryDto> getRedemptionFrequencies() {
        try {
            List<BaseDictionaryDto> dtoList = new ArrayList<>();
            Iterator<RedemptionFrequency> iterator = this.redemptionFrequencyRepository.findAll().iterator();
            while (iterator.hasNext()) {
                RedemptionFrequency entity = iterator.next();
                BaseDictionaryDto dto = disassemble(entity);
                dtoList.add(dto);
            }
            return dtoList;
        }catch (Exception ex){
            logger.error("Failed to load lookup: RedemptionFrequency", ex);
        }
        return null;
    }

    @Override
    public List<BaseDictionaryDto> getRedemptionNoticePeriods() {
        try {
            List<BaseDictionaryDto> dtoList = new ArrayList<>();
            Iterator<RedemptionNotificationPeriod> iterator = this.redemptionNotificationPeriodRepository.findAll().iterator();
            while (iterator.hasNext()) {
                RedemptionNotificationPeriod entity = iterator.next();
                BaseDictionaryDto dto = disassemble(entity);
                dtoList.add(dto);
            }
            return dtoList;
        }catch (Exception ex){
            logger.error("Failed to load lookup: RedemptionNotificationPeriod", ex);
        }
        return null;
    }

    @Override
    public List<BaseDictionaryDto> getSidePockets() {
        try {
            List<BaseDictionaryDto> dtoList = new ArrayList<>();
            Iterator<HedgeFundSidePocket> iterator = this.hedgeFundSidePocketRepository.findAll().iterator();
            while (iterator.hasNext()) {
                HedgeFundSidePocket entity = iterator.next();
                BaseDictionaryDto dto = disassemble(entity);
                dtoList.add(dto);
            }
            return dtoList;
        }catch (Exception ex){
            logger.error("Failed to load lookup: HedgeFundSidePocket", ex);
        }
        return null;
    }

    @Override
    public List<BaseDictionaryDto> getPrivateEquityStrategies(){
        return getStrategies(Strategy.TYPE_PRIVATE_EQUITY);
    }

    @Override
    public List<BaseDictionaryDto> getHedgeFundsStrategies(){
        return getStrategies(Strategy.TYPE_HEDGE_FUNDS);
    }

    @Override
    public List<BaseDictionaryDto> getHedgeFundsSubStrategies(String strategy){
        return getSubStrategies(strategy);
    }

    @Override
    public List<BaseDictionaryDto> getRealEstateStrategies(){
        return getStrategies(Strategy.TYPE_REAL_ESTATE);
    }

    @Override
    public List<StrategyDto> getAllStrategies(){
        try {
            List<StrategyDto> dtoList = new ArrayList<>();
            List<Strategy> entityList = this.strategyRepository.findByAllGroups();
            if (entityList != null) {
                for (Strategy entity : entityList) {
                    BaseDictionaryDto dto = disassemble(entity);
                    StrategyDto strategyDto = new StrategyDto(dto, entity.getGroupType());
                    dtoList.add(strategyDto);
                }
            }
            return dtoList;
        }catch (Exception ex){
            logger.error("Failed to load lookup: Strategy (all)", ex);
        }
        return null;
    }

    @Override
    public StrategyDto getStrategyByNameEndAndType(String nameEn, int type){
        Strategy entity = this.strategyRepository.findByNameEnAndGroupType(nameEn, type);
        if(entity != null) {
            BaseDictionaryDto dto = disassemble(entity);
            StrategyDto strategyDto = new StrategyDto(dto, entity.getGroupType());
            if(entity.getParent() != null){
                BaseDictionaryDto parentDto = disassemble(entity.getParent());
                StrategyDto parentStrategyDto = new StrategyDto(parentDto, (entity.getParent().getGroupType()));
                strategyDto.setParent(parentDto);
            }
            return strategyDto;
        }
        return null;
    }

    private List<BaseDictionaryDto> getStrategies(int group){
        try {
            List<BaseDictionaryDto> dtoList = new ArrayList<>();
            List<Strategy> entityList = group != 0 ? this.strategyRepository.findByGroupType(group) : this.strategyRepository.findByAllGroups();
            if (entityList != null) {
                for (Strategy entity : entityList) {
                    BaseDictionaryDto dto = disassemble(entity);
                    dtoList.add(dto);
                }
            }
            return dtoList;
        }catch (Exception ex){
            logger.error("Failed to load lookup: Strategy, type=" + group, ex);
        }
        return null;
    }

    private List<BaseDictionaryDto> getSubStrategies(String strategyCode){
        try {
            List<BaseDictionaryDto> dtoList = new ArrayList<>();
            List<Substrategy> entityList = this.substrategyRepository.findByStrategy(strategyCode);
            if (entityList != null) {
                for (Substrategy entity : entityList) {
                    BaseDictionaryDto dto = disassemble(entity);
                    dtoList.add(dto);
                }
            }
            return dtoList;
        }catch (Exception ex){
            logger.error("Failed to load lookup: Substrategy", ex);
        }
        return null;
    }

    @Override
    public List<BaseDictionaryDto> getGeographies(){
        try {
            List<BaseDictionaryDto> dtoList = new ArrayList<>();
            Iterator<Geography> iterator = this.geographyRepository.findAll().iterator();
            while (iterator.hasNext()) {
                Geography entity = iterator.next();
                BaseDictionaryDto geographyDto = disassemble(entity);
                dtoList.add(geographyDto);
            }
            return dtoList;
        }catch (Exception ex){
            logger.error("Failed to load lookup: Geography", ex);
        }
        return null;
    }

    @Override
    public List<BaseDictionaryDto> getPEIndustry(){
        try {
            List<BaseDictionaryDto> dtoList = new ArrayList<>();
            Iterator<PEIndustry> iterator = this.industryRepository.findAll().iterator();
            while (iterator.hasNext()) {
                PEIndustry entity = iterator.next();
                BaseDictionaryDto industryDto = disassemble(entity);
                dtoList.add(industryDto);
            }
            return dtoList;
        }catch (Exception ex){
            logger.error("Failed to load lookup: PEIndustry", ex);
        }
        return null;
    }

    @Override
    public List<BaseDictionaryDto> getNBChartOfAccounts() {
        try {
            List<BaseDictionaryDto> dtoList = new ArrayList<>();
            Iterator<NBChartOfAccounts> iterator = this.nbChartOfAccountsRepository.findAll(new Sort(Sort.Direction.ASC, "code")).iterator();
            while (iterator.hasNext()) {
                NBChartOfAccounts entity = iterator.next();
                BaseDictionaryDto dto = disassemble(entity);
                dto.setEditable(checkEditableTypedLookup(NB_CHART_ACCOUNTS, dto));
                dtoList.add(dto);
            }
            return dtoList;
        }catch (Exception ex){
            logger.error("Failed to load lookup: NBChartOfAccounts", ex);
        }
        return null;
    }

    @Override
    public List<NICReportingChartOfAccountsDto> getNICReportingChartOfAccounts(String code) {
        try {
            List<NICReportingChartOfAccountsDto> dtoList = new ArrayList<>();
            Iterator<NICReportingChartOfAccounts> iterator = null;
            if(code != null){
                iterator = this.nicReportingChartOfAccountsRepository.findByNBChartOfAccountsCode(code).iterator();
            }else{
                iterator = this.nicReportingChartOfAccountsRepository.findAll(new Sort(Sort.Direction.ASC, "code")).iterator();
            }
            while (iterator.hasNext()) {
                NICReportingChartOfAccounts entity = iterator.next();
                BaseDictionaryDto baseDictionaryDto = disassemble(entity);
                NICReportingChartOfAccountsDto dto = new NICReportingChartOfAccountsDto(baseDictionaryDto);
                if(entity.getNbChartOfAccounts() != null) {
                    dto.setNBChartOfAccounts(disassemble(entity.getNbChartOfAccounts()));
                }
                dto.setEditable(true);
                dtoList.add(dto);
            }
            return dtoList;
        }catch (Exception ex){
            logger.error("Failed to load lookup: NICReportingChartOfAccounts", ex);
        }
        return null;

    }

    @Override
    public List<BaseDictionaryDto> getNICReportingChartOfAccountsType() {
        try {
            List<BaseDictionaryDto> dtoList = new ArrayList<>();
            Iterator<PeriodicDataChartAccountsType> iterator = this.chartAccountsTypeRepository.findAll(new Sort(Sort.Direction.ASC, "code")).iterator();
            while (iterator.hasNext()) {
                PeriodicDataChartAccountsType entity = iterator.next();
                BaseDictionaryDto dto = disassemble(entity);
                dtoList.add(dto);
            }
            return dtoList;
        }catch (Exception ex){
            logger.error("Failed to load lookup: ChartOfAccountsType", ex);
        }
        return null;
    }

    @Override
    public NICChartOfAccountsPagedSearchResultDto searchNICReportingChartOfAccounts(NICChartOfAccountsSearchParamsDto searchParams) {
        if(searchParams == null){
            searchParams = new NICChartOfAccountsSearchParamsDto();
            searchParams.setPage(0);
            searchParams.setPageSize(DEFAULT_PAGE_SIZE);
        }else if(searchParams.getPageSize() == 0){
            searchParams.setPageSize(DEFAULT_PAGE_SIZE);
        }

        NICChartOfAccountsPagedSearchResultDto result = new NICChartOfAccountsPagedSearchResultDto();
        int page = searchParams != null && searchParams.getPage() > 0 ? searchParams.getPage() - 1 : 0;
        Page<NICReportingChartOfAccounts> entityPage = this.nicReportingChartOfAccountsRepository.searchByNBCode(searchParams.getNbCode(),
                new PageRequest(page, searchParams.getPageSize(),
                        new Sort(Sort.Direction.ASC, "code", "id")));
        if(entityPage != null && entityPage.getContent() != null){

            result.setTotalElements(entityPage.getTotalElements());
            if (entityPage.getTotalElements() > 0) {
                result.setShowPageFrom(PaginationUtils.getShowPageFrom(DEFAULT_PAGES_PER_VIEW, page + 1));
                result.setShowPageTo(PaginationUtils.getShowPageTo(DEFAULT_PAGES_PER_VIEW,
                        page + 1, result.getShowPageFrom(), entityPage.getTotalPages()));
            }
            result.setTotalPages(entityPage.getTotalPages());
            result.setCurrentPage(page + 1);
            if (searchParams != null) {
                result.setSearchParams(searchParams.getSearchParamsAsString());
            }

            if(entityPage != null && entityPage.getContent() != null) {
                for(NICReportingChartOfAccounts entity: entityPage.getContent()){
                    NICReportingChartOfAccountsDto dto = new NICReportingChartOfAccountsDto(disassemble(entity));
                    dto.setNBChartOfAccounts(disassemble(entity.getNbChartOfAccounts()));
                    dto.setEditable(true);
                    result.add(dto);
                }
            }
        }

        return result;
    }

    @Override
    public List<TarragonNICReportingChartOfAccountsDto> getAddableTarragonNICReportingChartOfAccounts() {
        try {
            List<TarragonNICReportingChartOfAccountsDto> dtoList = new ArrayList<>();
            List<TarragonNICChartOfAccounts> entities = this.tarragonNICChartOfAccountsRepository.findByAddable(true);

            if(entities != null){
                for(TarragonNICChartOfAccounts entity: entities){
                    TarragonNICReportingChartOfAccountsDto dto = new TarragonNICReportingChartOfAccountsDto();
                    dto.setTarragonChartOfAccountsName(entity.getTarragonChartOfAccountsName());
                    if(entity.getNicReportingChartOfAccounts() != null) {
                        BaseDictionaryDto nicChartAccountsBaseDto = disassemble(entity.getNicReportingChartOfAccounts());
                        NICReportingChartOfAccountsDto nicChartAccountsDto = new NICReportingChartOfAccountsDto(nicChartAccountsBaseDto);
                        if (entity.getNicReportingChartOfAccounts().getNbChartOfAccounts() != null) {
                            nicChartAccountsDto.setNBChartOfAccounts(disassemble(entity.getNicReportingChartOfAccounts().getNbChartOfAccounts()));
                        }
                        dto.setNICChartOfAccounts(nicChartAccountsDto);
                    }
                    dtoList.add(dto);
                }
            }

            return dtoList;
        }catch (Exception ex){
            logger.error("Failed to load lookup: NICReportingChartOfAccounts", ex);
        }
        return null;

    }

    @Override
    public List<BaseDictionaryDto> getTarragonTrancheTypes() {

        try {
            List<BaseDictionaryDto> dtoList = new ArrayList<>();
            Iterator<PETrancheType> iterator = this.peTrancheTypeRepository.findAll().iterator();
            while (iterator.hasNext()) {
                PETrancheType entity = iterator.next();
                BaseDictionaryDto dto = disassemble(entity);
                dtoList.add(dto);
            }
            return dtoList;
        } catch (Exception ex) {
            logger.error("Failed to load lookup: PETrancheType", ex);
        }
        return null;
    }

    @Override
    public List<BaseDictionaryDto> getTerraTrancheTypes() {

        try {
            List<BaseDictionaryDto> dtoList = new ArrayList<>();
            Iterator<RETrancheType> iterator = this.reTrancheTypeRepository.findAll().iterator();
            while (iterator.hasNext()) {
                RETrancheType entity = iterator.next();
                BaseDictionaryDto dto = disassemble(entity);
                dtoList.add(dto);
            }
            return dtoList;
        } catch (Exception ex) {
            logger.error("Failed to load lookup: RETrancheType", ex);
        }
        return null;
    }

    @Override
    public List<TerraNICReportingChartOfAccountsDto> getAddableTerraNICReportingChartOfAccounts() {
        try {
            List<TerraNICReportingChartOfAccountsDto> dtoList = new ArrayList<>();
            List<TerraNICChartOfAccounts> entities = this.terraNICChartOfAccountsRepository.findByAddable(true);

            if(entities != null){
                for(TerraNICChartOfAccounts entity: entities){
                    TerraNICReportingChartOfAccountsDto dto = new TerraNICReportingChartOfAccountsDto();
                    dto.setTerraChartOfAccountsName(entity.getTerraChartOfAccountsName());
                    if(entity.getNicReportingChartOfAccounts() != null) {
                        BaseDictionaryDto nicChartAccountsBaseDto = disassemble(entity.getNicReportingChartOfAccounts());
                        NICReportingChartOfAccountsDto nicChartAccountsDto = new NICReportingChartOfAccountsDto(nicChartAccountsBaseDto);
                        if (entity.getNicReportingChartOfAccounts().getNbChartOfAccounts() != null) {
                            nicChartAccountsDto.setNBChartOfAccounts(disassemble(entity.getNicReportingChartOfAccounts().getNbChartOfAccounts()));
                        }
                        dto.setNICChartOfAccounts(nicChartAccountsDto);
                    }
                    dtoList.add(dto);
                }
            }

            return dtoList;
        }catch (Exception ex){
            logger.error("Failed to load lookup: NICReportingChartOfAccounts", ex);
        }
        return null;

    }

    @Override
    public List<BaseDictionaryDto> getReserveCalculationExpenseTypeLookup() {
        List<BaseDictionaryDto> dtoList = new ArrayList<>();
        Iterator<ReserveCalculationExpenseType>  iterator = this.reserveCalculationExpenseTypeRepository.findAll().iterator();
        while (iterator.hasNext()) {
            ReserveCalculationExpenseType entity = iterator.next();
            BaseDictionaryDto type = disassemble(entity);
            dtoList.add(type);
        }
        return dtoList;
    }

    @Override
    public List<BaseDictionaryDto> getReserveCalculationEntityTypeLookup() {
        List<BaseDictionaryDto> dtoList = new ArrayList<>();
        Iterator<ReserveCalculationEntityType>  iterator = this.reserveCalculationEntityTypeRepository.findAll().iterator();
        while (iterator.hasNext()) {
            ReserveCalculationEntityType entity = iterator.next();
            BaseDictionaryDto type = disassemble(entity);
            dtoList.add(type);
        }
        return dtoList;
    }

    @Override
    public List<BaseDictionaryDto> getReserveCalculationExportSignerTypeLookup() {
        List<BaseDictionaryDto> dtoList = new ArrayList<>();
        Iterator<ReserveCalculationExportSignerType>  iterator = this.reserveCalculationExportSignerTypeRepository.findAll().iterator();
        while (iterator.hasNext()) {
            ReserveCalculationExportSignerType entity = iterator.next();
            BaseDictionaryDto type = disassemble(entity);
            dtoList.add(type);
        }
        return dtoList;
    }

    @Override
    public List<BaseDictionaryDto> getReserveCalculationExportDoerTypeLookup() {
        List<BaseDictionaryDto> dtoList = new ArrayList<>();
        Iterator<ReserveCalculationExportDoerType>  iterator = this.reserveCalculationExportDoerTypeRepository.findAll().iterator();
        while (iterator.hasNext()) {
            ReserveCalculationExportDoerType entity = iterator.next();
            BaseDictionaryDto type = disassemble(entity);
            dtoList.add(type);
        }
        return dtoList;
    }

    @Override
    public List<BaseDictionaryDto> getReserveCalculationExportApproveListTypeLookup() {
        List<BaseDictionaryDto> dtoList = new ArrayList<>();
        Iterator<ReserveCalculationExportApproveListType>  iterator = this.reserveCalculationExportApproveListTypeRepository.findAll().iterator();
        while (iterator.hasNext()) {
            ReserveCalculationExportApproveListType entity = iterator.next();
            BaseDictionaryDto type = disassemble(entity);
            dtoList.add(type);
        }
        return dtoList;
    }

    @Override
    public List<BaseDictionaryDto> getMMFields(){
        List<BaseDictionaryDto> dtoList = new ArrayList<>();
        Iterator<MacroMonitorField> iterator = this.macroMonitorFieldRepository.findAll().iterator();
        while(iterator.hasNext()) {
            MacroMonitorField entity = iterator.next();
            BaseDictionaryDto mmFieldDto = disassemble(entity);
            dtoList.add(mmFieldDto);
        }
        return  dtoList;
    }

    // TODO: refactor as common lookup converter
    private BaseDictionaryDto disassemble(BaseTypeEntity entity){
        BaseDictionaryDto dto = new BaseDictionaryDto();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setNameEn(entity.getNameEn());
        dto.setNameRu(entity.getNameRu());
        dto.setNameKz(entity.getNameKz());
        return dto;
    }

    private void setValues(BaseDictionaryDto source, BaseTypeEntityImpl dest){
        dest.setCode(source.getCode());
        dest.setNameEn(source.getNameEn());
        dest.setNameRu(source.getNameRu() == null ? "" : source.getNameRu());
        dest.setNameKz(source.getNameKz());
    }

    @Override
    public List<BaseDictionaryDto> getAllNewsTypes() {
        try {
            List<BaseDictionaryDto> dtoList = new ArrayList<>();
            Iterator<NewsType> iterator = this.newsTypeRepository.findAll().iterator();
            while (iterator.hasNext()) {
                NewsType entity = iterator.next();
                BaseDictionaryDto dto = disassemble(entity);
                dtoList.add(dto);
            }
            return dtoList;
        } catch (Exception ex) {
            logger.error("Failed to load lookup: NewsType", ex);
        }
        return null;
    }

    @Override
    public List<BaseDictionaryDto> getAllMemoTypes() {
        try {
            List<BaseDictionaryDto> dtoList = new ArrayList<>();
            Iterator<MemoType> iterator = this.memoTypeRepository.findAll().iterator();
            while (iterator.hasNext()) {
                MemoType entity = iterator.next();
                BaseDictionaryDto dto = disassemble(entity);
                dtoList.add(dto);
            }
            return dtoList;
        } catch (Exception ex) {
            logger.error("Failed to load lookup: MemoType", ex);
        }
        return null;
    }

    @Override
    public List<BaseDictionaryDto> getAllMeetingTypes() {
        try {
            List<BaseDictionaryDto> dtoList = new ArrayList<>();
            Iterator<MeetingType> iterator = this.meetingTypeRepository.findAll().iterator();
            while (iterator.hasNext()) {
                MeetingType entity = iterator.next();
                BaseDictionaryDto dto = disassemble(entity);
                dtoList.add(dto);
            }
            return dtoList;
        } catch (Exception ex) {
            logger.error("Failed to load lookup: MeetingType", ex);
        }
        return null;
    }

    @Override
    public List<BaseDictionaryDto> getAllCorpMeetingTypes() {
        try {
            List<BaseDictionaryDto> dtoList = new ArrayList<>();
            Iterator<CorpMeetingType> iterator = this.corpMeetingTypeRepository.findAll().iterator();
            while (iterator.hasNext()) {
                CorpMeetingType entity = iterator.next();
                BaseDictionaryDto dto = disassemble(entity);
                dtoList.add(dto);
            }
            return dtoList;
        } catch (Exception ex) {
            logger.error("Failed to load lookup: CorpMeetingType", ex);
        }
        return null;
    }

    @Override
    public List<BaseDictionaryDto> getNBReportingTarragonBalanceTypeLookup() {
        try {
            List<BaseDictionaryDto> dtoList = new ArrayList<>();
            Iterator<PEBalanceType> iterator = this.peBalanceTypeRepository.findAll().iterator();
            while (iterator.hasNext()) {
                PEBalanceType entity = iterator.next();
                BaseDictionaryDto dto = disassemble(entity);
                if(entity.getParent() != null){
                    dto.setParent(disassemble(entity.getParent()));
                }

                dto.setEditable(checkEditableTypedLookup(PE_BALANCE_TYPE, dto));

                dtoList.add(dto);
            }
            return dtoList;
        } catch (Exception ex) {
            logger.error("Failed to load lookup: PEBalanceType", ex);
        }
        return null;
    }

    @Override
    public List<BaseDictionaryDto> getNBReportingTarragonOperationsTypeLookup() {
        try {
            List<BaseDictionaryDto> dtoList = new ArrayList<>();
            Iterator<PEOperationsType> iterator = this.peOperationsTypeRepository.findAll().iterator();
            while (iterator.hasNext()) {
                PEOperationsType entity = iterator.next();
                BaseDictionaryDto dto = disassemble(entity);
                if(entity.getParent() != null){
                    dto.setParent(disassemble(entity.getParent()));
                }
                dto.setEditable(checkEditableTypedLookup(PE_OPS_TYPE, dto));
                dtoList.add(dto);
            }
            return dtoList;
        } catch (Exception ex) {
            logger.error("Failed to load lookup: PEOperationsType", ex);
        }
        return null;
    }

    @Override
    public List<BaseDictionaryDto> getNBReportingTarragonCashflowsTypeLookup() {
        try {
            List<BaseDictionaryDto> dtoList = new ArrayList<>();
            Iterator<PECashflowsType> iterator = this.peCashflowsTypeRepository.findAll().iterator();
            while (iterator.hasNext()) {
                PECashflowsType entity = iterator.next();
                BaseDictionaryDto dto = disassemble(entity);
                if(entity.getParent() != null){
                    dto.setParent(disassemble(entity.getParent()));
                }
                dto.setEditable(checkEditableTypedLookup(PE_CASHFLOW_TYPE, dto));
                dtoList.add(dto);
            }
            return dtoList;
        } catch (Exception ex) {
            logger.error("Failed to load lookup: PECashflowsType", ex);
        }
        return null;
    }

    @Override
    public List<BaseDictionaryDto> getNBReportingTarragonInvestmentTypeLookup() {
        try {
            List<BaseDictionaryDto> dtoList = new ArrayList<>();
            Iterator<PEInvestmentType> iterator = this.peInvestmentTypeRepository.findAll().iterator();
            while (iterator.hasNext()) {
                PEInvestmentType entity = iterator.next();
                BaseDictionaryDto dto = disassemble(entity);
                //dto.setEditable(checkEditableTypedLookup(PE_INVESTMENT_TYPE, dto));
                dtoList.add(dto);
            }
            return dtoList;
        } catch (Exception ex) {
            logger.error("Failed to load lookup: PEInvestmentType", ex);
        }
        return null;
    }

    @Override
    public List<BaseDictionaryDto> getNBReportingSingularityChartAccountsTypeLookup() {
        try {
            List<BaseDictionaryDto> dtoList = new ArrayList<>();
            Iterator<HFChartOfAccountsType> iterator = this.hfChartOfAccountsTypeRepository.findAll().iterator();
            while (iterator.hasNext()) {
                HFChartOfAccountsType entity = iterator.next();
                BaseDictionaryDto dto = disassemble(entity);
                if(entity.getParent() != null){
                    dto.setParent(disassemble(entity.getParent()));
                }
                dto.setEditable(checkEditableTypedLookup(HF_CHART_ACCOUNTS_TYPE, dto));
                dtoList.add(dto);
            }
            return dtoList;
        } catch (Exception ex) {
            logger.error("Failed to load lookup: HFChartOfAccountsType", ex);
        }
        return null;
    }

    @Override
    public List<BaseDictionaryDto> getNBReportingTerraChartAccountsTypeLookup() {
        // UNUSED
        try {
            List<BaseDictionaryDto> dtoList = new ArrayList<>();
            Iterator<REChartOfAccountsType> iterator = this.reChartOfAccountsTypeRepository.findAll().iterator();
            while (iterator.hasNext()) {
                REChartOfAccountsType entity = iterator.next();
                BaseDictionaryDto dto = disassemble(entity);
                if(entity.getParent() != null){
                    dto.setParent(disassemble(entity.getParent()));
                }
                dtoList.add(dto);
            }
            return dtoList;
        } catch (Exception ex) {
            logger.error("Failed to load lookup: REChartOfAccountsType", ex);
        }
        return null;
    }

    @Override
    public List<BaseDictionaryDto> getNBReportingTerraBalanceTypeLookup() {
        try {
            List<BaseDictionaryDto> dtoList = new ArrayList<>();
            Iterator<REBalanceType> iterator = this.reBalanceTypeRepository.findAll().iterator();
            while (iterator.hasNext()) {
                REBalanceType entity = iterator.next();
                BaseDictionaryDto dto = disassemble(entity);
                if(entity.getParent() != null){
                    dto.setParent(disassemble(entity.getParent()));
                }
                dto.setEditable(checkEditableTypedLookup(RE_BALANCE_TYPE, dto));
                dtoList.add(dto);
            }
            return dtoList;
        } catch (Exception ex) {
            logger.error("Failed to load lookup: REBalanceType", ex);
        }
        return null;
    }

    @Override
    public List<BaseDictionaryDto> getNBReportingTerraProfitLossTypeLookup() {
        try {
            List<BaseDictionaryDto> dtoList = new ArrayList<>();
            Iterator<REProfitLossType> iterator = this.reProfitLossTypeRepository.findAll().iterator();
            while (iterator.hasNext()) {
                REProfitLossType entity = iterator.next();
                BaseDictionaryDto dto = disassemble(entity);
                if(entity.getParent() != null){
                    dto.setParent(disassemble(entity.getParent()));
                }
                dto.setEditable(checkEditableTypedLookup(RE_PROFIT_LOSS_TYPE, dto));
                dtoList.add(dto);
            }
            return dtoList;
        } catch (Exception ex) {
            logger.error("Failed to load lookup: REProfitLossType", ex);
        }
        return null;
    }

    @Override
    public List<BaseDictionaryDto> getPeriodicDataTypesLookup() {
        try {
            List<BaseDictionaryDto> dtoList = new ArrayList<>();
            Iterator<PeriodicDataType> iterator = this.periodicDataTypeRepository.findAll().iterator();
            while (iterator.hasNext()) {
                PeriodicDataType entity = iterator.next();
                BaseDictionaryDto dto = disassemble(entity);
                dtoList.add(dto);
            }
            return dtoList;
        } catch (Exception ex) {
            logger.error("Failed to load lookup: PeriodicDataType", ex);
        }
        return null;
    }


    private boolean checkEditableTypedLookup(String type, BaseDictionaryDto lookup){
        if(lookup.getId() == null){
            return true;
        }
        if (type.equalsIgnoreCase(PE_BALANCE_TYPE)) {
            boolean exists = this.peStatementBalanceService.existEntityWithType(lookup.getCode());
            if(exists){
                return false;
            }
            List<PEBalanceType> entitiesByParent = this.peBalanceTypeRepository.findByParentCode(lookup.getCode());
            if(entitiesByParent != null && !entitiesByParent.isEmpty()){
                return false;
            }
            return true;
        }else if (type.equalsIgnoreCase(PE_OPS_TYPE)) {
            boolean exists = this.peStatementOperationsService.existEntityWithType(lookup.getCode());
            if(exists){
                return false;
            }
            List<PEOperationsType> entitiesByParent = this.peOperationsTypeRepository.findByParentCode(lookup.getCode());
            if(entitiesByParent != null && !entitiesByParent.isEmpty()){
                return false;
            }
            return true;
        }else if (type.equalsIgnoreCase(PE_CASHFLOW_TYPE)) {
            boolean exists = this.peStatementCashflowsService.existEntityWithType(lookup.getCode());
            if(exists){
                return false;
            }
            List<PECashflowsType> entitiesByParent = this.peCashflowsTypeRepository.findByParentCode(lookup.getCode());
            if(entitiesByParent != null && !entitiesByParent.isEmpty()){
                return false;
            }
            return true;
        }else if (type.equalsIgnoreCase(PE_INVESTMENT_TYPE)) {
//            boolean exists = this.peScheduleInvestmentService.existEntityWithType(lookup.getCode());
//            if(exists){
//                return false;
//            }
            return true;
        }else if (type.equalsIgnoreCase(HF_CHART_ACCOUNTS_TYPE)) {
            boolean exists = this.hfGeneralLedgerBalanceService.existEntityWithChartAccountsType(lookup.getCode());
            if(exists){
                return false;
            }
            List<HFChartOfAccountsType> entitiesByParent = this.hfChartOfAccountsTypeRepository.findByParentCode(lookup.getCode());
            if(entitiesByParent != null && !entitiesByParent.isEmpty()){
                return false;
            }
            return true;
        }else if (type.equalsIgnoreCase(RE_BALANCE_TYPE)) {
            boolean exists = this.reService.existBalanceEntityWithType(lookup.getCode());
            if(exists){
                return false;
            }
            List<REBalanceType> entitiesByParent = this.reBalanceTypeRepository.findByParentCode(lookup.getCode());
            if(entitiesByParent != null && !entitiesByParent.isEmpty()){
                return false;
            }
            return true;
        }else if (type.equalsIgnoreCase(RE_PROFIT_LOSS_TYPE)) {
            boolean exists = this.reService.existProfitLossEntityWithType(lookup.getCode());
            if(exists){
                return false;
            }
            List<REProfitLossType> entitiesByParent = this.reProfitLossTypeRepository.findByParentCode(lookup.getCode());
            if(entitiesByParent != null && !entitiesByParent.isEmpty()){
                return false;
            }
            return true;
        }else if (type.equalsIgnoreCase(NB_CHART_ACCOUNTS)) {
            Iterator<NICReportingChartOfAccounts> iterator = this.nicReportingChartOfAccountsRepository.findByNBChartOfAccountsCode(lookup.getCode()).iterator();
            if(iterator != null && iterator.hasNext()){
                if(lookup.getId() != null) {
                    NBChartOfAccounts existingEntity = this.nbChartOfAccountsRepository.findOne(lookup.getId());
                    if (existingEntity != null && !existingEntity.getCode().equals(lookup.getCode())) {
                        // Cannot change code of used lookup value
                        return false;
                    }
                }
                //return false;
            }
            return true;
        }else{
            String errorMessage = "Error checking lookup value for type '" + type + "'. Could not find matching lookup by type.";
            logger.error(errorMessage);
            return false;
        }
    }

    private EntitySaveResponseDto saveLookupPEBalanceType(BaseDictionaryDto lookup, String username){
        EntitySaveResponseDto saveResponseDto = new EntitySaveResponseDto();

        if(StringUtils.isEmpty(lookup.getCode())){
            String errorMessage = "Error saving lookup value: code is required";
            logger.error(errorMessage);
            saveResponseDto.setErrorMessageEn(errorMessage);
            return saveResponseDto;
        }else if(StringUtils.isEmpty(lookup.getNameEn())){
            String errorMessage = "Error saving lookup value: name en is required";
            logger.error(errorMessage);
            saveResponseDto.setErrorMessageEn(errorMessage);
            return saveResponseDto;
        }
        PEBalanceType entity = new PEBalanceType();
        setValues(lookup, entity);
        if(lookup.getId() != null){
            //Check editable
            if(!checkEditableTypedLookup(PE_BALANCE_TYPE, lookup)){
                String errorMessage = "Error saving PE Balance type: lookup value is not editable";
                logger.error(errorMessage);
                saveResponseDto.setErrorMessageEn(errorMessage);
                return saveResponseDto;
            }
            entity.setId(lookup.getId());


        }else{
            PEBalanceType existingEntity = this.peBalanceTypeRepository.findByCode(lookup.getCode());
            if(existingEntity != null){
                String errorMessage = "Error saving lookup value: duplicate code '" + lookup.getCode() + "'";
                logger.error(errorMessage);
                saveResponseDto.setErrorMessageEn(errorMessage);
                return saveResponseDto;
            }
        }

        if(lookup.getParent() != null && lookup.getParent().getCode() != null) {
            PEBalanceType parent = this.peBalanceTypeRepository.findByCode(lookup.getParent().getCode());
            if(parent != null){
                entity.setParent(parent);
            }
        }
        Integer id = this.peBalanceTypeRepository.save(entity).getId();
        saveResponseDto.setEntityId(new Long(id));
        saveResponseDto.setSuccessMessageEn("Successfully saved lookup value");
        logger.info("Successfully saved PE Balance type lookup value: id=" + lookup.getId() + ", code=" + lookup.getCode()
                + ", name en=" + lookup.getNameEn() + " [user=" + username + "]");
        return saveResponseDto;
    }

    private EntitySaveResponseDto saveLookupPEOperationsType(BaseDictionaryDto lookup, String username){
        EntitySaveResponseDto saveResponseDto = new EntitySaveResponseDto();
        if(StringUtils.isEmpty(lookup.getCode())){
            String errorMessage = "Error saving lookup value: code is required";
            logger.error(errorMessage);
            saveResponseDto.setErrorMessageEn(errorMessage);
            return saveResponseDto;
        }else if(StringUtils.isEmpty(lookup.getNameEn())){
            String errorMessage = "Error saving lookup value: name en is required";
            logger.error(errorMessage);
            saveResponseDto.setErrorMessageEn(errorMessage);
            return saveResponseDto;
        }
        PEOperationsType entity = new PEOperationsType();
        if(lookup.getId() != null){
            //Check editable
            if(!checkEditableTypedLookup(PE_OPS_TYPE, lookup)){
                String errorMessage = "Error saving PE Operations type: lookup value is not editable";
                logger.error(errorMessage);
                saveResponseDto.setErrorMessageEn(errorMessage);
                return saveResponseDto;
            }
            entity.setId(lookup.getId());

        }else{
            PEOperationsType existingEntity = this.peOperationsTypeRepository.findByCode(lookup.getCode());
            if(existingEntity != null){
                String errorMessage = "Error saving lookup value: duplicate code '" + lookup.getCode() + "'";
                logger.error(errorMessage);
                saveResponseDto.setErrorMessageEn(errorMessage);
                return saveResponseDto;
            }
        }

        setValues(lookup, entity);
        if(lookup.getParent() != null && lookup.getParent().getCode() != null) {
            PEOperationsType parent = this.peOperationsTypeRepository.findByCode(lookup.getParent().getCode());
            if(parent != null){
                entity.setParent(parent);
            }
        }
        Integer id = this.peOperationsTypeRepository.save(entity).getId();
        saveResponseDto.setEntityId(new Long(id));
        saveResponseDto.setSuccessMessageEn("Successfully saved lookup value");
        logger.info("Successfully saved PE Operations type lookup value: id=" + lookup.getId() + ", code=" + lookup.getCode()
                + ", name en=" + lookup.getNameEn() + "[user=" + username + "]");
        return saveResponseDto;
    }

    private EntitySaveResponseDto saveLookupPECashflowsType(BaseDictionaryDto lookup, String username){
        EntitySaveResponseDto saveResponseDto = new EntitySaveResponseDto();
        if(StringUtils.isEmpty(lookup.getCode())){
            String errorMessage = "Error saving lookup value: code is required";
            logger.error(errorMessage);
            saveResponseDto.setErrorMessageEn(errorMessage);
            return saveResponseDto;
        }else if(StringUtils.isEmpty(lookup.getNameEn())){
            String errorMessage = "Error saving lookup value: name en is required";
            logger.error(errorMessage);
            saveResponseDto.setErrorMessageEn(errorMessage);
            return saveResponseDto;
        }
        PECashflowsType entity = new PECashflowsType();
        if(lookup.getId() != null){
            //Check editable
            if(!checkEditableTypedLookup(PE_CASHFLOW_TYPE, lookup)){
                String errorMessage = "Error saving PE Cashflows type: lookup value is not editable";
                logger.error(errorMessage);
                saveResponseDto.setErrorMessageEn(errorMessage);
                return saveResponseDto;
            }
            entity.setId(lookup.getId());

        }else{
            PECashflowsType existingEntity = this.peCashflowsTypeRepository.findByCode(lookup.getCode());
            if(existingEntity != null){
                String errorMessage = "Error saving lookup value: duplicate code '" + lookup.getCode() + "'";
                logger.error(errorMessage);
                saveResponseDto.setErrorMessageEn(errorMessage);
                return saveResponseDto;
            }
        }

        setValues(lookup, entity);
        if(lookup.getParent() != null && lookup.getParent().getCode() != null) {
            PECashflowsType parent = this.peCashflowsTypeRepository.findByCode(lookup.getParent().getCode());
            if(parent != null){
                entity.setParent(parent);
            }
        }
        Integer id = this.peCashflowsTypeRepository.save(entity).getId();
        saveResponseDto.setEntityId(new Long(id));
        saveResponseDto.setSuccessMessageEn("Successfully saved lookup value");
        logger.info("Successfully saved PE Cashflows type lookup value: id=" + lookup.getId() + ", code=" + lookup.getCode()
                + ", name en=" + lookup.getNameEn() + "[user=" + username + "]");
        return saveResponseDto;
    }

    private EntitySaveResponseDto saveLookupPEInvestmentType(BaseDictionaryDto lookup, String username){
        EntitySaveResponseDto saveResponseDto = new EntitySaveResponseDto();
        if(StringUtils.isEmpty(lookup.getCode())){
            String errorMessage = "Error saving lookup value: code is required";
            logger.error(errorMessage);
            saveResponseDto.setErrorMessageEn(errorMessage);
            return saveResponseDto;
        }else if(StringUtils.isEmpty(lookup.getNameEn())){
            String errorMessage = "Error saving lookup value: name en is required";
            logger.error(errorMessage);
            saveResponseDto.setErrorMessageEn(errorMessage);
            return saveResponseDto;
        }
        PEInvestmentType entity = new PEInvestmentType();
        if(lookup.getId() != null){
            //Check editable
            if(!checkEditableTypedLookup(PE_INVESTMENT_TYPE, lookup)){
                String errorMessage = "Error saving PE Investment type: lookup value is not editable";
                logger.error(errorMessage);
                saveResponseDto.setErrorMessageEn(errorMessage);
                return saveResponseDto;
            }
            entity.setId(lookup.getId());

        }else{
            PEInvestmentType existingEntity = this.peInvestmentTypeRepository.findByCode(lookup.getCode());
            if(existingEntity != null){
                String errorMessage = "Error saving lookup value: duplicate code '" + lookup.getCode() + "'";
                logger.error(errorMessage);
                saveResponseDto.setErrorMessageEn(errorMessage);
                return saveResponseDto;
            }
        }

        setValues(lookup, entity);

        Integer id = this.peInvestmentTypeRepository.save(entity).getId();
        saveResponseDto.setEntityId(new Long(id));
        saveResponseDto.setSuccessMessageEn("Successfully saved lookup value");
        logger.info("Successfully saved PE Investment type lookup value: id=" + lookup.getId() + ", code=" + lookup.getCode()
                + ", name en=" + lookup.getNameEn() + "[user=" + username + "]");
        return saveResponseDto;
    }

    private EntitySaveResponseDto saveLookupHFChartAccountsType(BaseDictionaryDto lookup, String username){
        EntitySaveResponseDto saveResponseDto = new EntitySaveResponseDto();
        if(StringUtils.isEmpty(lookup.getCode())){
            String errorMessage = "Error saving lookup value: code is required";
            logger.error(errorMessage);
            saveResponseDto.setErrorMessageEn(errorMessage);
            return saveResponseDto;
        }else if(StringUtils.isEmpty(lookup.getNameEn())){
            String errorMessage = "Error saving lookup value: name en is required";
            logger.error(errorMessage);
            saveResponseDto.setErrorMessageEn(errorMessage);
            return saveResponseDto;
        }
        HFChartOfAccountsType entity = new HFChartOfAccountsType();
        if(lookup.getId() != null){
            //Check editable
            if(!checkEditableTypedLookup(HF_CHART_ACCOUNTS_TYPE, lookup)){
                String errorMessage = "Error saving HF Chart of accounts type: lookup value is not editable";
                logger.error(errorMessage);
                saveResponseDto.setErrorMessageEn(errorMessage);
                return saveResponseDto;
            }
            entity.setId(lookup.getId());
        }else{
            HFChartOfAccountsType existingEntity = this.hfChartOfAccountsTypeRepository.findByCode(lookup.getCode());
            if(existingEntity != null){
                String errorMessage = "Error saving lookup value: duplicate code '" + lookup.getCode() + "'";
                logger.error(errorMessage);
                saveResponseDto.setErrorMessageEn(errorMessage);
                return saveResponseDto;
            }
        }
        setValues(lookup, entity);
        if(lookup.getParent() != null && lookup.getParent().getCode() != null) {
            HFChartOfAccountsType parent = this.hfChartOfAccountsTypeRepository.findByCode(lookup.getParent().getCode());
            if(parent != null){
                entity.setParent(parent);
            }
        }
        Integer id = this.hfChartOfAccountsTypeRepository.save(entity).getId();
        saveResponseDto.setEntityId(new Long(id));
        saveResponseDto.setSuccessMessageEn("Successfully saved lookup value");
        logger.info("Successfully saved HF Chart of accounts type lookup value: id=" + lookup.getId() + ", code=" +
                lookup.getCode() + ", name en=" + lookup.getNameEn() + "[user=" + username + "]");
        return saveResponseDto;
    }

    private EntitySaveResponseDto saveLookupREChartAccountsType(BaseDictionaryDto lookup, String username){
        EntitySaveResponseDto saveResponseDto = new EntitySaveResponseDto();
        if(StringUtils.isEmpty(lookup.getCode())){
            String errorMessage = "Error saving lookup value: code is required";
            logger.error(errorMessage);
            saveResponseDto.setErrorMessageEn(errorMessage);
            return saveResponseDto;
        }else if(StringUtils.isEmpty(lookup.getNameEn())){
            String errorMessage = "Error saving lookup value: name en is required";
            logger.error(errorMessage);
            saveResponseDto.setErrorMessageEn(errorMessage);
            return saveResponseDto;
        }
        REChartOfAccountsType entity = new REChartOfAccountsType();
        if(lookup.getId() != null){
            //Check editable
            if(!checkEditableTypedLookup(RE_CHART_ACCOUNTS_TYPE, lookup)){
                String errorMessage = "Error saving HF Chart of accounts type: lookup value is not editable";
                logger.error(errorMessage);
                saveResponseDto.setErrorMessageEn(errorMessage);
                return saveResponseDto;
            }
            entity.setId(lookup.getId());
        }else{
            REChartOfAccountsType existingEntity = this.reChartOfAccountsTypeRepository.findByCode(lookup.getCode());
            if(existingEntity != null){
                String errorMessage = "Error saving lookup value: duplicate code '" + lookup.getCode() + "'";
                logger.error(errorMessage);
                saveResponseDto.setErrorMessageEn(errorMessage);
                return saveResponseDto;
            }
        }
        setValues(lookup, entity);
        if(lookup.getParent() != null && lookup.getParent().getCode() != null) {
            REChartOfAccountsType parent = this.reChartOfAccountsTypeRepository.findByCode(lookup.getParent().getCode());
            if(parent != null){
                entity.setParent(parent);
            }
        }
        Integer id = this.reChartOfAccountsTypeRepository.save(entity).getId();
        saveResponseDto.setEntityId(new Long(id));
        saveResponseDto.setSuccessMessageEn("Successfully saved lookup value");
        logger.info("Successfully saved RE Chart of accounts type lookup value: id=" + lookup.getId() + ", code=" +
                lookup.getCode() + ", name en=" + lookup.getNameEn() + "[user=" + username + "]");
        return saveResponseDto;
    }

    private EntitySaveResponseDto saveLookupREBalanceType(BaseDictionaryDto lookup, String username){
        EntitySaveResponseDto saveResponseDto = new EntitySaveResponseDto();
        if(StringUtils.isEmpty(lookup.getCode())){
            String errorMessage = "Error saving lookup value: code is required";
            logger.error(errorMessage);
            saveResponseDto.setErrorMessageEn(errorMessage);
            return saveResponseDto;
        }else if(StringUtils.isEmpty(lookup.getNameEn())){
            String errorMessage = "Error saving lookup value: name en is required";
            logger.error(errorMessage);
            saveResponseDto.setErrorMessageEn(errorMessage);
            return saveResponseDto;
        }
        REBalanceType entity = new REBalanceType();
        if(lookup.getId() != null){
            //Check editable
            if(!checkEditableTypedLookup(RE_BALANCE_TYPE, lookup)){
                String errorMessage = "Error saving RE Balance type: lookup value is not editable";
                logger.error(errorMessage);
                saveResponseDto.setErrorMessageEn(errorMessage);
                return saveResponseDto;
            }
            entity.setId(lookup.getId());

        }else{
            REBalanceType existingEntity = this.reBalanceTypeRepository.findByCode(lookup.getCode());
            if(existingEntity != null){
                String errorMessage = "Error saving lookup value: duplicate code '" + lookup.getCode() + "'";
                logger.error(errorMessage);
                saveResponseDto.setErrorMessageEn(errorMessage);
                return saveResponseDto;
            }
        }

        setValues(lookup, entity);
        if(lookup.getParent() != null && lookup.getParent().getCode() != null) {
            REBalanceType parent = this.reBalanceTypeRepository.findByCode(lookup.getParent().getCode());
            if(parent != null){
                entity.setParent(parent);
            }
        }
        Integer id = this.reBalanceTypeRepository.save(entity).getId();
        saveResponseDto.setEntityId(new Long(id));
        saveResponseDto.setSuccessMessageEn("Successfully saved lookup value");
        logger.info("Successfully saved RE Balance type lookup value: id=" + lookup.getId() + ", code=" +
                lookup.getCode() + ", name en=" + lookup.getNameEn() + "[user=" + username + "]");
        return saveResponseDto;
    }

    private EntitySaveResponseDto saveLookupREProfitLossType(BaseDictionaryDto lookup, String username){
        EntitySaveResponseDto saveResponseDto = new EntitySaveResponseDto();
        if(StringUtils.isEmpty(lookup.getCode())){
            String errorMessage = "Error saving lookup value: code is required";
            logger.error(errorMessage);
            saveResponseDto.setErrorMessageEn(errorMessage);
            return saveResponseDto;
        }else if(StringUtils.isEmpty(lookup.getNameEn())){
            String errorMessage = "Error saving lookup value: name en is required";
            logger.error(errorMessage);
            saveResponseDto.setErrorMessageEn(errorMessage);
            return saveResponseDto;
        }
        REProfitLossType entity = new REProfitLossType();
        if(lookup.getId() != null){
            //Check editable
            if(!checkEditableTypedLookup(RE_PROFIT_LOSS_TYPE, lookup)){
                String errorMessage = "Error saving RE Profit loss type: lookup value is not editable";
                logger.error(errorMessage);
                saveResponseDto.setErrorMessageEn(errorMessage);
                return saveResponseDto;
            }
            entity.setId(lookup.getId());

        }else{
            REProfitLossType existingEntity = this.reProfitLossTypeRepository.findByCode(lookup.getCode());
            if(existingEntity != null){
                String errorMessage = "Error saving lookup value: duplicate code '" + lookup.getCode() + "'";
                logger.error(errorMessage);
                saveResponseDto.setErrorMessageEn(errorMessage);
                return saveResponseDto;
            }
        }

        setValues(lookup, entity);
        if(lookup.getParent() != null && lookup.getParent().getCode() != null) {
            REProfitLossType parent = this.reProfitLossTypeRepository.findByCode(lookup.getParent().getCode());
            if(parent != null){
                entity.setParent(parent);
            }
        }
        Integer id = this.reProfitLossTypeRepository.save(entity).getId();
        saveResponseDto.setEntityId(new Long(id));
        saveResponseDto.setSuccessMessageEn("Successfully saved lookup value");
        logger.info("Successfully saved RE Profit loss type lookup value: id=" + lookup.getId() + ", code=" +
                lookup.getCode() + ", name en=" + lookup.getNameEn() + "[user=" + username + "]");
        return saveResponseDto;
    }

    private EntitySaveResponseDto saveLookupNBChartAccounts(BaseDictionaryDto lookup, String username){
        EntitySaveResponseDto saveResponseDto = new EntitySaveResponseDto();
        if(StringUtils.isEmpty(lookup.getCode())){
            String errorMessage = "Error saving lookup value: code is required";
            logger.error(errorMessage);
            saveResponseDto.setErrorMessageEn(errorMessage);
            return saveResponseDto;
        }else if(StringUtils.isEmpty(lookup.getNameRu())){
            String errorMessage = "Error saving lookup value: name ru is required";
            logger.error(errorMessage);
            saveResponseDto.setErrorMessageEn(errorMessage);
            return saveResponseDto;
        }
        NBChartOfAccounts entity = new NBChartOfAccounts();
        if(lookup.getId() != null){
            //Check editable
            if(!checkEditableTypedLookup(NB_CHART_ACCOUNTS, lookup)){
                String errorMessage = "Error saving NB Chart of accounts: lookup value is not editable";
                logger.error(errorMessage);
                saveResponseDto.setErrorMessageEn(errorMessage);
                return saveResponseDto;
            }
            entity.setId(lookup.getId());
        }else{
            NBChartOfAccounts existingEntity = this.nbChartOfAccountsRepository.findByCode(lookup.getCode());
            if(existingEntity != null){
                String errorMessage = "Error saving lookup value: duplicate code '" + lookup.getCode() + "'";
                logger.error(errorMessage);
                saveResponseDto.setErrorMessageEn(errorMessage);
                return saveResponseDto;
            }
        }

        setValues(lookup, entity);
        Integer id = this.nbChartOfAccountsRepository.save(entity).getId();
        saveResponseDto.setEntityId(new Long(id));
        saveResponseDto.setSuccessMessageEn("Successfully saved lookup value");
        logger.info("Successfully saved NB Chart accounts lookup value: id=" + lookup.getId() + ", code=" +
                lookup.getCode() + ", name ru=" + lookup.getNameRu() + " [user=" + username + "]");
        return saveResponseDto;
    }
    @Override
    public EntitySaveResponseDto saveStrategyLookup(StrategyDto strategyDto, String username){
        EntitySaveResponseDto saveResponseDto = new EntitySaveResponseDto();
        if(StringUtils.isEmpty(strategyDto.getCode())){
            String errorMessage = "Error saving lookup value: code is required";
            logger.error(errorMessage);
            saveResponseDto.setErrorMessageEn(errorMessage);
            return saveResponseDto;
        }
        if(strategyDto.getParent() != null){
            if(strategyDto.getCode().equalsIgnoreCase(strategyDto.getParent().getCode())){
                String errorMessage = "Error saving lookup value: cannot be self parent";
                logger.error(errorMessage);
                saveResponseDto.setErrorMessageEn(errorMessage);
                return saveResponseDto;
            }
        }

        try{
            Strategy entity = new Strategy();
            entity.setId(strategyDto.getId());
            setValues(strategyDto, entity);
            entity.setGroupType(strategyDto.getGroupType());

            Integer id = this.strategyRepository.save(entity).getId();
            saveResponseDto.setEntityId(new Long(id));
            saveResponseDto.setSuccessMessageEn("Successfully saved Strategy lookup value");
            logger.info("Successfully saved Strategy lookup value: id=" + id.longValue()+ ", code=" +
                    strategyDto.getCode() + ", name en=" + strategyDto.getNameEn() + " [user=" + username + "]");
            return saveResponseDto;
        }catch (Exception ex){
            String errorMessage = "Error saving Strategy lookup value";
            logger.error(errorMessage, ex);
            saveResponseDto.setErrorMessageEn(errorMessage);
            return saveResponseDto;
        }
    }

    @Override
    public EntitySaveResponseDto saveTypedLookupValue(String type, BaseDictionaryDto lookup, String username) {
        EntitySaveResponseDto saveResponseDto = new EntitySaveResponseDto();
        if(StringUtils.isEmpty(type)){
            String errorMessage = "Error saving lookup value: lookup type is missing";
            logger.error(errorMessage);
            saveResponseDto.setErrorMessageEn(errorMessage);
            return saveResponseDto;
        }
        if(lookup == null){
            String errorMessage = "Error saving lookup value: lookup value is missing";
            logger.error(errorMessage);
            saveResponseDto.setErrorMessageEn(errorMessage);
            return saveResponseDto;
        }
        if(StringUtils.isEmpty(lookup.getCode())){
            String errorMessage = "Error saving lookup value: code is required";
            logger.error(errorMessage);
            saveResponseDto.setErrorMessageEn(errorMessage);
            return saveResponseDto;
        }
        if(lookup.getParent() != null){
            if(lookup.getCode().equalsIgnoreCase(lookup.getParent().getCode())){
                String errorMessage = "Error saving lookup value: cannot be self parent";
                logger.error(errorMessage);
                saveResponseDto.setErrorMessageEn(errorMessage);
                return saveResponseDto;
            }
        }

        try {
            if (type.equalsIgnoreCase(PE_BALANCE_TYPE)) {
                return saveLookupPEBalanceType(lookup, username);
            }else if (type.equalsIgnoreCase(PE_OPS_TYPE)) {
                return saveLookupPEOperationsType(lookup, username);
            }else if (type.equalsIgnoreCase(PE_CASHFLOW_TYPE)) {
                return saveLookupPECashflowsType(lookup, username);
            }else if (type.equalsIgnoreCase(PE_INVESTMENT_TYPE)) {
                return saveLookupPEInvestmentType(lookup, username);
            }else if (type.equalsIgnoreCase(HF_CHART_ACCOUNTS_TYPE)) {
                return saveLookupHFChartAccountsType(lookup, username);
            }else if (type.equalsIgnoreCase("RE_CHART_ACCOUNTS_TYPE")) {
                return saveLookupREChartAccountsType(lookup, username);
            }else if (type.equalsIgnoreCase(RE_BALANCE_TYPE)) {
                return saveLookupREBalanceType(lookup, username);
            }else if (type.equalsIgnoreCase(RE_PROFIT_LOSS_TYPE)) {
                return saveLookupREProfitLossType(lookup, username);
            }else if (type.equalsIgnoreCase(NB_CHART_ACCOUNTS)) {
                return saveLookupNBChartAccounts(lookup, username);
            }else{
                String errorMessage = "Error saving lookup value for type '" + type + "'. Could not find matching lookup by type.";
                logger.error(errorMessage);
                saveResponseDto.setErrorMessageEn(errorMessage);
            }

        }catch (Exception ex){
            String errorMessage = "Error saving lookup value for type '" + type + "'";
            logger.error(errorMessage, ex);
            saveResponseDto.setErrorMessageEn(errorMessage);
        }

        return saveResponseDto;
    }

    @Override
    public boolean deleteTypedLookupValueByTypeAndId(String type, Integer id, String username) {
        try {
            boolean deleted = false;
            if (type.equalsIgnoreCase(PE_BALANCE_TYPE)) {
                deleted = deleteLookupPEBalanceType(id);
            }else if (type.equalsIgnoreCase(PE_OPS_TYPE)) {
                deleted = deleteLookupPEOperationsType(id);
            }else if (type.equalsIgnoreCase(PE_CASHFLOW_TYPE)) {
                deleted = deleteLookupPECashflowsType(id);
            }else if (type.equalsIgnoreCase(HF_CHART_ACCOUNTS_TYPE)) {
                deleted = deleteLookupHFChartAccountsType(id);
                //}else if (type.equalsIgnoreCase("RE_CHART_ACCOUNTS_TYPE")) {
            }else if (type.equalsIgnoreCase(RE_BALANCE_TYPE)) {
                deleted = deleteLookupREBalanceType(id);
            }else if (type.equalsIgnoreCase(RE_PROFIT_LOSS_TYPE)) {
                deleted = deleteLookupREProfitLossType(id);
            }else if (type.equalsIgnoreCase(NB_CHART_ACCOUNTS)) {
                deleted = deleteLookupNBChartAccounts(id);
            }else{
                String errorMessage = "Error deleting lookup value for type '" + type + "'. Could not find matching lookup by type.";
                logger.error(errorMessage);
                return false;
            }
            if(deleted){
                logger.info("Successfully deleted typed lookup value: type=" + type + ", id=" + id + " [user=" + username + "]");
            }
            return deleted;

        }catch (Exception ex){
            logger.error("Error deleting currency rate record: id " + id, ex);
            return false;
        }
    }

    private boolean deleteLookupPEBalanceType(Integer id){
        try{
            PEBalanceType entity = this.peBalanceTypeRepository.findOne(id);
            if(entity != null){
                if(checkEditableTypedLookup(PE_BALANCE_TYPE, disassemble(entity))){
                    this.peBalanceTypeRepository.delete(id);
                    logger.info("Successfully deleted PE Balance type lookup value: id=" + id + ", name en=" + entity.getNameEn());
                    return true;
                }else{
                    logger.error("Error deleting PE Balance type lookup value with id " + id + ": entity is not editable");
                    return false;
                }
            }else{
                logger.error("Error deleting PE Balance type lookup value with id " + id + ": entity not found");
            }
        }catch (Exception ex){
            logger.error("Error deleting PE Balance type lookup value with id " + id, ex);
        }
        return false;
    }

    private boolean deleteLookupPEOperationsType(Integer id){
        try{
            PEOperationsType entity = this.peOperationsTypeRepository.findOne(id);
            if(entity != null){
                if(checkEditableTypedLookup(PE_OPS_TYPE, disassemble(entity))){
                    this.peOperationsTypeRepository.delete(id);
                    logger.info("Successfully deleted PE Operations type lookup value: id=" + id + ", name en=" + entity.getNameEn());
                    return true;
                }else{
                    logger.error("Error deleting PE Operations type lookup value with id " + id + ": entity is not editable");
                    return false;
                }
            }else{
                logger.error("Error deleting PE Operations type lookup value with id " + id + ": entity not found");
            }
        }catch (Exception ex){
            logger.error("Error deleting PE Operations type lookup value with id " + id, ex);
        }
        return false;
    }

    private boolean deleteLookupPECashflowsType(Integer id){
        try{
            PECashflowsType entity = this.peCashflowsTypeRepository.findOne(id);
            if(entity != null){
                if(checkEditableTypedLookup(PE_CASHFLOW_TYPE, disassemble(entity))){
                    this.peCashflowsTypeRepository.delete(id);
                    logger.info("Successfully deleted PE Cashflows type lookup value: id=" + id + ", name en=" + entity.getNameEn());
                    return true;
                }else{
                    logger.error("Error deleting PE Cashflows type lookup value with id " + id + ": entity is not editable");
                    return false;
                }
            }else{
                logger.error("Error deleting PE Cashflows type lookup value with id " + id + ": entity not found");
            }
        }catch (Exception ex){
            logger.error("Error deleting PE Cashflows type lookup value with id " + id, ex);
        }
        return false;
    }

    private boolean deleteLookupHFChartAccountsType(Integer id){
        try{
            HFChartOfAccountsType entity = this.hfChartOfAccountsTypeRepository.findOne(id);
            if(entity != null){
                if(checkEditableTypedLookup(HF_CHART_ACCOUNTS_TYPE, disassemble(entity))){
                    this.hfChartOfAccountsTypeRepository.delete(id);
                    logger.info("Successfully deleted HF Chart of accounts type lookup value: id=" + id + ", name en=" + entity.getNameEn());
                    return true;
                }else{
                    logger.error("Error deleting  HF Chart of accounts type lookup value with id " + id + ": entity is not editable");
                    return false;
                }
            }else{
                logger.error("Error deleting HF Chart of accounts type lookup value with id " + id + ": entity not found");
            }
        }catch (Exception ex){
            logger.error("Error deleting HF Chart of accounts type lookup value with id " + id, ex);
        }
        return false;
    }

    private boolean deleteLookupREBalanceType(Integer id){
        try{
            REBalanceType entity = this.reBalanceTypeRepository.findOne(id);
            if(entity != null){
                if(checkEditableTypedLookup(RE_BALANCE_TYPE, disassemble(entity))){
                    this.reBalanceTypeRepository.delete(id);
                    logger.info("Successfully deleted RE Balance type lookup value: id=" + id + ", name en=" + entity.getNameEn());
                    return true;
                }else{
                    logger.error("Error deleting  RE Balance type lookup value with id " + id + ": entity is not editable");
                    return false;
                }
            }else{
                logger.error("Error deleting RE Balance type lookup value with id " + id + ": entity not found");
            }
        }catch (Exception ex){
            logger.error("Error deleting RE Balance type lookup value with id " + id, ex);
        }
        return false;
    }

    private boolean deleteLookupREProfitLossType(Integer id){
        try{
            REProfitLossType entity = this.reProfitLossTypeRepository.findOne(id);
            if(entity != null){
                if(checkEditableTypedLookup(RE_PROFIT_LOSS_TYPE, disassemble(entity))){
                    this.reProfitLossTypeRepository.delete(id);
                    logger.info("Successfully deleted RE Profit loss type lookup value: id=" + id + ", name en=" + entity.getNameEn());
                    return true;
                }else{
                    logger.error("Error deleting  RE Profit loss type lookup value with id " + id + ": entity is not editable");
                    return false;
                }
            }else{
                logger.error("Error deleting RE Profit loss type lookup value with id " + id + ": entity not found");
            }
        }catch (Exception ex){
            logger.error("Error deleting RE Profit loss type lookup value with id " + id, ex);
        }
        return false;
    }

    private boolean deleteLookupNBChartAccounts(Integer id){
        try{
            NBChartOfAccounts entity = this.nbChartOfAccountsRepository.findOne(id);
            if(entity != null){
                if(checkEditableTypedLookup(NB_CHART_ACCOUNTS, disassemble(entity))){
                    this.nbChartOfAccountsRepository.delete(id);
                    logger.info("Successfully deleted NB Chart of accounts type lookup value: id=" + id + ", name ru=" + entity.getNameRu());
                    return true;
                }else{
                    logger.error("Error deleting  NB Chart of accounts value with id " + id + ": entity is not editable");
                    return false;
                }
            }else{
                logger.error("Error deleting NB Chart of accounts lookup value with id " + id + ": entity not found");
            }
        }catch (Exception ex){
            logger.error("Error deleting NB Chart of accounts lookup value with id " + id, ex);
        }
        return false;
    }

    @Override
    public List<CommonNICReportingChartOfAccountsDto> getSingularityNICReportingChartOfAccounts() {
        List<CommonNICReportingChartOfAccountsDto> dtoList = new ArrayList<>();
        Iterator<SingularityNICChartOfAccounts> iterator = singularityNICChartOfAccountsRepository.findAll(new Sort(Sort.Direction.ASC, "singularityAccountNumber")).iterator();
        if(iterator != null){
            while(iterator.hasNext()){
                SingularityNICChartOfAccounts entity = iterator.next();
                CommonNICReportingChartOfAccountsDto dto = new CommonNICReportingChartOfAccountsDto();
                dto.setId(entity.getId());
                dto.setAccountNumber(entity.getSingularityAccountNumber());
                dto.setNICChartOfAccounts(new NICReportingChartOfAccountsDto(entity.getNicReportingChartOfAccounts()));
                dto.setAddable(false);
                //dto.setNegativeOnly();
                //dto.setPositiveOnly();
                //dto.setEditable(checkEditableMatchingSingularityAccountNumber(entity.getSingularityAccountNumber()));
                dto.setEditable(true);
                dto.setDeletable(checkDeletableMatchingSingularityAccountNumber(dto.getAccountNumber()));

                if(entity.getChartAccountsType() != null) {
                    dto.setChartAccountsType(new BaseDictionaryDto(entity.getChartAccountsType().getCode(),
                            entity.getChartAccountsType().getNameEn(), entity.getChartAccountsType().getNameRu(), entity.getChartAccountsType().getNameKz()));
                }
                dtoList.add(dto);
            }
        }
        return dtoList;
    }


    @Override
    public List<CommonNICReportingChartOfAccountsDto> getTarragonNICReportingChartOfAccounts() {
        List<CommonNICReportingChartOfAccountsDto> dtoList = new ArrayList<>();
        Iterator<TarragonNICChartOfAccounts> iterator = tarragonNICChartOfAccountsRepository.findAll(new Sort(Sort.Direction.ASC, "tarragonChartOfAccountsName")).iterator();
        if(iterator != null){
            while(iterator.hasNext()){
                TarragonNICChartOfAccounts entity = iterator.next();
                CommonNICReportingChartOfAccountsDto dto = new CommonNICReportingChartOfAccountsDto();
                dto.setId(entity.getId());
                dto.setNameEn(entity.getTarragonChartOfAccountsName());
                dto.setNICChartOfAccounts(new NICReportingChartOfAccountsDto(entity.getNicReportingChartOfAccounts()));
                dto.setAddable(entity.isAddable());
//                dto.setNegativeOnly(entity.getNegativeOnly());
//                dto.setPositiveOnly(entity.getPositiveOnly());
                //dto.setEditable(checkEditableMatchingTarragonChartAccountsLongDescription(entity.getTarragonChartOfAccountsName()));
                if(entity.getChartAccountsType() != null) {
                    dto.setChartAccountsType(new BaseDictionaryDto(entity.getChartAccountsType().getCode(),
                            entity.getChartAccountsType().getNameEn(), entity.getChartAccountsType().getNameRu(), entity.getChartAccountsType().getNameKz()));
                }
                dto.setEditable(true);
                dto.setDeletable(checkDeletableMatchingTarragonChartAccountsLongDescription(dto.getNameEn()));
                dtoList.add(dto);
            }
        }
        return dtoList;
    }

    @Override
    public List<CommonNICReportingChartOfAccountsDto> getTerraNICReportingChartOfAccounts() {
        List<CommonNICReportingChartOfAccountsDto> dtoList = new ArrayList<>();
        Iterator<TerraNICChartOfAccounts> iterator = this.terraNICChartOfAccountsRepository.findAll(new Sort(Sort.Direction.ASC, "terraChartOfAccountsName")).iterator();
        if(iterator != null){
            while(iterator.hasNext()){
                TerraNICChartOfAccounts entity = iterator.next();
                CommonNICReportingChartOfAccountsDto dto = new CommonNICReportingChartOfAccountsDto();
                dto.setId(entity.getId());
                dto.setNameEn(entity.getTerraChartOfAccountsName());
                dto.setNICChartOfAccounts(new NICReportingChartOfAccountsDto(entity.getNicReportingChartOfAccounts()));
                dto.setAddable(entity.isAddable());
                //dto.setNegativeOnly(entity.getNegativeOnly());
                //dto.setPositiveOnly(entity.getPositiveOnly());
                if(entity.getChartAccountsType() != null) {
                    dto.setChartAccountsType(new BaseDictionaryDto(entity.getChartAccountsType().getCode(),
                            entity.getChartAccountsType().getNameEn(), entity.getChartAccountsType().getNameRu(), entity.getChartAccountsType().getNameKz()));
                }
                dto.setEditable(true);
                dto.setDeletable(checkDeletableMatchingTerraChartAccountsLongDescription(dto.getNameEn()));

                dtoList.add(dto);
            }
        }
        return dtoList;
    }

    private EntitySaveResponseDto saveMatchingSingularityNICChartAccounts(CommonNICReportingChartOfAccountsDto dto, String username){
        EntitySaveResponseDto saveResponseDto = new EntitySaveResponseDto();

        SingularityNICChartOfAccounts entity = new SingularityNICChartOfAccounts();
        entity.setId(dto.getId());
        entity.setSingularityAccountNumber(dto.getAccountNumber());

        if(dto.getNICChartOfAccounts() != null && dto.getNICChartOfAccounts().getCode() != null) {
            NICReportingChartOfAccounts nicReportingChartOfAccounts =
                    this.nicReportingChartOfAccountsRepository.findByCode(dto.getNICChartOfAccounts().getCode());
            if(nicReportingChartOfAccounts != null){
                entity.setNicReportingChartOfAccounts(nicReportingChartOfAccounts);
            }else{
                String errorMessage = "Error saving value for NIC_SINGULARITY_CHART_ACCOUNTS: NIC Chart of " +
                        "accounts not found for code=" + dto.getNICChartOfAccounts().getCode();
                logger.error(errorMessage);
                saveResponseDto.setErrorMessageEn(errorMessage);
                return saveResponseDto;
            }
        }else{
            String errorMessage = "Error saving value for NIC_SINGULARITY_CHART_ACCOUNTS: missing required field 'nicReportingChartOfAccounts'";
            logger.error(errorMessage);
            saveResponseDto.setErrorMessageEn(errorMessage);
            return saveResponseDto;
        }

        // Positive/Negative only
        List<SingularityNICChartOfAccounts> existingRecords =
                this.singularityNICChartOfAccountsRepository.findBySingularityAccountNumber(dto.getAccountNumber());
        if(existingRecords != null && !existingRecords.isEmpty()){
            int existingPos = 0;
            int existingNeg = 0;
            for(SingularityNICChartOfAccounts existingRecord: existingRecords){
                if(dto.getId() != null && existingRecord.getId().longValue() == dto.getId().longValue()){
                    continue;
                }
                if(existingRecord.getChartAccountsType() != null && existingRecord.getChartAccountsType().getCode().equalsIgnoreCase("POSITIVE")){
                    existingPos++;
                }else if(existingRecord.getChartAccountsType() != null && existingRecord.getChartAccountsType().getCode().equalsIgnoreCase("NEGATIVE")){
                    existingNeg++;
                }else if(existingRecord.getChartAccountsType() != null && existingRecord.getChartAccountsType().getCode().equalsIgnoreCase("ALL")){
                    String errorMessage = "Error saving value for NIC_SINGULARITY_CHART_ACCOUNTS: exists " +
                            "record matching name '" + dto.getNameEn() + "' with 'ALL' (Negative and Positive) flag.'";
                    logger.error(errorMessage);
                    saveResponseDto.setErrorMessageEn(errorMessage);
                    return saveResponseDto;
                }
            }
            if(dto.getChartAccountsType() != null && dto.getChartAccountsType().getCode() != null) {
                if (dto.getChartAccountsType().getCode().equalsIgnoreCase("POSITIVE")) {
                    if (existingPos > 0) {
                        String errorMessage = "Error saving value for NIC_SINGULARITY_CHART_ACCOUNTS: exists " +
                                "record matching name '" + dto.getNameEn() + "' with 'Positive only' flag.";
                        logger.error(errorMessage);
                        saveResponseDto.setErrorMessageEn(errorMessage);
                        return saveResponseDto;
                    }
                } else if (dto.getChartAccountsType().getCode().equalsIgnoreCase("NEGATIVE")) {
                    if (existingNeg > 0) {
                        String errorMessage = "Error saving value for NIC_SINGULARITY_CHART_ACCOUNTS: exists " +
                                "record matching name '" + dto.getNameEn() + "' with 'Negative only' flag.";
                        logger.error(errorMessage);
                        saveResponseDto.setErrorMessageEn(errorMessage);
                        return saveResponseDto;
                    }
                }else if (dto.getChartAccountsType().getCode().equalsIgnoreCase("ALL")) {
                    String errorMessage = "Error saving value for NIC_SINGULARITY_CHART_ACCOUNTS: exists " +
                            "record matching name '" + dto.getNameEn() + "' cannot create record with 'ALL' flag.";
                    logger.error(errorMessage);
                    saveResponseDto.setErrorMessageEn(errorMessage);
                    return saveResponseDto;

                }
            }
        }

        if(dto.getChartAccountsType() != null && dto.getChartAccountsType().getCode() != null) {
            PeriodicDataChartAccountsType type = this.chartAccountsTypeRepository.findByCode(dto.getChartAccountsType().getCode());
            entity.setChartAccountsType(type);
        }
        if(entity.getChartAccountsType() == null){
            String errorMessage = "Error saving value for NIC_SINGULARITY_CHART_ACCOUNTS: Chart of Accounts type missing.";
            logger.error(errorMessage);
            saveResponseDto.setErrorMessageEn(errorMessage);
            return saveResponseDto;
        }

        this.singularityNICChartOfAccountsRepository.save(entity);
        saveResponseDto.setSuccessMessageEn("Successfully saved lookup value for NIC_SINGULARITY_CHART_ACCOUNTS");
        logger.info("Successfully saved lookup value for NIC_SINGULARITY_CHART_ACCOUNTS: id="  + dto.getId() +
                ", account number=" + dto.getAccountNumber() + " [user=" + username + "]");

        return saveResponseDto;

    }

    private EntitySaveResponseDto saveMatchingTarragonNICChartAccounts(CommonNICReportingChartOfAccountsDto dto, String username){
        EntitySaveResponseDto saveResponseDto = new EntitySaveResponseDto();
        TarragonNICChartOfAccounts entity = new TarragonNICChartOfAccounts();
        entity.setId(dto.getId());
        entity.setTarragonChartOfAccountsName(dto.getNameEn());
        if(dto.getAddable() != null) {
            entity.setAddable(dto.getAddable());
        }
        if(dto.getNICChartOfAccounts() != null && dto.getNICChartOfAccounts().getCode() != null) {
            NICReportingChartOfAccounts nicReportingChartOfAccounts =
                    this.nicReportingChartOfAccountsRepository.findByCode(dto.getNICChartOfAccounts().getCode());
            if(nicReportingChartOfAccounts != null){
                entity.setNicReportingChartOfAccounts(nicReportingChartOfAccounts);
            }else{
                String errorMessage = "Error saving value for NIC_TARRAGON_CHART_ACCOUNTS: NIC Chart of " +
                        "accounts not found for code=" + dto.getNICChartOfAccounts().getCode();
                logger.error(errorMessage);
                saveResponseDto.setErrorMessageEn(errorMessage);
                return saveResponseDto;
            }
        }else{
            String errorMessage = "Error saving value for NIC_TARRAGON_CHART_ACCOUNTS: missing required field 'nicReportingChartOfAccounts'";
            logger.error(errorMessage);
            saveResponseDto.setErrorMessageEn(errorMessage);
            return saveResponseDto;
        }

        // Positive/Negative only
        List<TarragonNICChartOfAccounts> existingRecords =
                this.tarragonNICChartOfAccountsRepository.findByTarragonChartOfAccountsName(dto.getNameEn());
        if(existingRecords != null && !existingRecords.isEmpty()){
            int existingPos = 0;
            int existingNeg = 0;
            for(TarragonNICChartOfAccounts existingRecord: existingRecords){
                if(dto.getId() != null && existingRecord.getId().longValue() == dto.getId().longValue()){
                    continue;
                }
                if(existingRecord.getChartAccountsType() != null && existingRecord.getChartAccountsType().getCode().equalsIgnoreCase("POSITIVE")){
                    existingPos++;
                }else if(existingRecord.getChartAccountsType() != null && existingRecord.getChartAccountsType().getCode().equalsIgnoreCase("NEGATIVE")){
                    existingNeg++;
                }else if(existingRecord.getChartAccountsType() != null && existingRecord.getChartAccountsType().getCode().equalsIgnoreCase("ALL")){
                    String errorMessage = "Error saving value for NIC_TARRAGON_CHART_ACCOUNTS: exists " +
                            "record matching name '" + dto.getNameEn() + "' with 'ALL' (Negative and Positive) flag.'";
                    logger.error(errorMessage);
                    saveResponseDto.setErrorMessageEn(errorMessage);
                    return saveResponseDto;
                }
            }
            if(dto.getChartAccountsType() != null && dto.getChartAccountsType().getCode() != null) {
                if (dto.getChartAccountsType().getCode().equalsIgnoreCase("POSITIVE")) {
                    if (existingPos > 0) {
                        String errorMessage = "Error saving value for NIC_TARRAGON_CHART_ACCOUNTS: exists " +
                                "record matching name '" + dto.getNameEn() + "' with 'Positive only' flag.";
                        logger.error(errorMessage);
                        saveResponseDto.setErrorMessageEn(errorMessage);
                        return saveResponseDto;
                    }
                } else if (dto.getChartAccountsType().getCode().equalsIgnoreCase("NEGATIVE")) {
                    if (existingNeg > 0) {
                        String errorMessage = "Error saving value for NIC_TARRAGON_CHART_ACCOUNTS: exists " +
                                "record matching name '" + dto.getNameEn() + "' with 'Negative only' flag.";
                        logger.error(errorMessage);
                        saveResponseDto.setErrorMessageEn(errorMessage);
                        return saveResponseDto;
                    }
                }else if (dto.getChartAccountsType().getCode().equalsIgnoreCase("ALL")) {
                    String errorMessage = "Error saving value for NIC_TARRAGON_CHART_ACCOUNTS: exists " +
                            "record matching name '" + dto.getNameEn() + "' cannot create record with 'ALL' flag.";
                    logger.error(errorMessage);
                    saveResponseDto.setErrorMessageEn(errorMessage);
                    return saveResponseDto;

                }
            }
        }

        if(dto.getChartAccountsType() != null && dto.getChartAccountsType().getCode() != null) {
            PeriodicDataChartAccountsType type = this.chartAccountsTypeRepository.findByCode(dto.getChartAccountsType().getCode());
            entity.setChartAccountsType(type);
        }
        if(entity.getChartAccountsType() == null){
            String errorMessage = "Error saving value for NIC_TARRAGON_CHART_ACCOUNTS: Chart of Accounts type missing.";
            logger.error(errorMessage);
            saveResponseDto.setErrorMessageEn(errorMessage);
            return saveResponseDto;
        }

        this.tarragonNICChartOfAccountsRepository.save(entity);
        saveResponseDto.setSuccessMessageEn("Successfully saved lookup value for NIC_TARRAGON_CHART_ACCOUNTS");
        logger.info("Successfully saved lookup value fro NIC_TARRAGON_CHART_ACCOUNTS: id="  + dto.getId() +
                ", name en=" + dto.getNameEn() + " [user=" + username + "]");
        return saveResponseDto;
    }

    private EntitySaveResponseDto saveMatchingTerraNICChartAccounts(CommonNICReportingChartOfAccountsDto dto, String username){
        EntitySaveResponseDto saveResponseDto = new EntitySaveResponseDto();
        TerraNICChartOfAccounts entity = new TerraNICChartOfAccounts();
        entity.setId(dto.getId());
        entity.setTerraChartOfAccountsName(dto.getNameEn());
        if(dto.getAddable() != null) {
            entity.setAddable(dto.getAddable());
        }

        if(dto.getNICChartOfAccounts() != null && dto.getNICChartOfAccounts().getCode() != null) {
            NICReportingChartOfAccounts nicReportingChartOfAccounts =
                    this.nicReportingChartOfAccountsRepository.findByCode(dto.getNICChartOfAccounts().getCode());
            if(nicReportingChartOfAccounts != null){
                entity.setNicReportingChartOfAccounts(nicReportingChartOfAccounts);
            }else{
                String errorMessage = "Error saving value for NIC_TERRA_CHART_ACCOUNTS: NIC Chart of " +
                        "accounts not found for code=" + dto.getNICChartOfAccounts().getCode();
                logger.error(errorMessage);
                saveResponseDto.setErrorMessageEn(errorMessage);
                return saveResponseDto;
            }
        }else{
            String errorMessage = "Error saving value for NIC_TERRA_CHART_ACCOUNTS: missing required field 'nicReportingChartOfAccounts'";
            logger.error(errorMessage);
            saveResponseDto.setErrorMessageEn(errorMessage);
            return saveResponseDto;
        }

        // Positive/Negative only
        List<TerraNICChartOfAccounts> existingRecords =
                this.terraNICChartOfAccountsRepository.findByTerraChartOfAccountsName(dto.getNameEn());
        if(existingRecords != null && !existingRecords.isEmpty()){
            int existingPos = 0;
            int existingNeg = 0;
            for(TerraNICChartOfAccounts existingRecord: existingRecords){
                if(dto.getId() != null && existingRecord.getId().longValue() == dto.getId().longValue()){
                    continue;
                }
                if(existingRecord.getChartAccountsType() != null && existingRecord.getChartAccountsType().getCode().equalsIgnoreCase("POSITIVE")){
                    existingPos++;
                }else if(existingRecord.getChartAccountsType() != null && existingRecord.getChartAccountsType().getCode().equalsIgnoreCase("NEGATIVE")){
                    existingNeg++;
                }else if(existingRecord.getChartAccountsType() != null && existingRecord.getChartAccountsType().getCode().equalsIgnoreCase("ALL")){
                    String errorMessage = "Error saving value for NIC_TERRA_CHART_ACCOUNTS: exists " +
                            "record matching name '" + dto.getNameEn() + "' with 'ALL' (Negative and Positive) flag.'";
                    logger.error(errorMessage);
                    saveResponseDto.setErrorMessageEn(errorMessage);
                    return saveResponseDto;
                }
            }
            if(dto.getChartAccountsType() != null && dto.getChartAccountsType().getCode() != null) {
                if (dto.getChartAccountsType().getCode().equalsIgnoreCase("POSITIVE")) {
                    if (existingPos > 0) {
                        String errorMessage = "Error saving value for NIC_TERRA_CHART_ACCOUNTS: exists " +
                                "record matching name '" + dto.getNameEn() + "' with 'Positive only' flag.";
                        logger.error(errorMessage);
                        saveResponseDto.setErrorMessageEn(errorMessage);
                        return saveResponseDto;
                    }
                } else if (dto.getChartAccountsType().getCode().equalsIgnoreCase("NEGATIVE")) {
                    if (existingNeg > 0) {
                        String errorMessage = "Error saving value for NIC_TERRA_CHART_ACCOUNTS: exists " +
                                "record matching name '" + dto.getNameEn() + "' with 'Negative only' flag.";
                        logger.error(errorMessage);
                        saveResponseDto.setErrorMessageEn(errorMessage);
                        return saveResponseDto;
                    }
                }else if (dto.getChartAccountsType().getCode().equalsIgnoreCase("ALL")) {
                    String errorMessage = "Error saving value for NIC_TERRA_CHART_ACCOUNTS: exists " +
                            "record matching name '" + dto.getNameEn() + "' cannot create record with 'ALL' flag.";
                    logger.error(errorMessage);
                    saveResponseDto.setErrorMessageEn(errorMessage);
                    return saveResponseDto;

                }
            }
        }

        if(dto.getChartAccountsType() != null && dto.getChartAccountsType().getCode() != null) {
            PeriodicDataChartAccountsType type = this.chartAccountsTypeRepository.findByCode(dto.getChartAccountsType().getCode());
            entity.setChartAccountsType(type);
        }
        if(entity.getChartAccountsType() == null){
            String errorMessage = "Error saving value for NIC_TERRA_CHART_ACCOUNTS: Chart of Accounts type missing.";
            logger.error(errorMessage);
            saveResponseDto.setErrorMessageEn(errorMessage);
            return saveResponseDto;
        }

        this.terraNICChartOfAccountsRepository.save(entity);
        saveResponseDto.setSuccessMessageEn("Successfully saved lookup value for NIC_TERRA_CHART_ACCOUNTS");
        logger.info("Successfully saved lookup value fro NIC_TERRA_CHART_ACCOUNTS: id="  + dto.getId() +
                ", name en=" + dto.getNameEn() + " [user=" + username + "]");
        return saveResponseDto;
    }

    @Override
    public EntitySaveResponseDto saveMatchingNICChartAccounts(String type, CommonNICReportingChartOfAccountsDto dto, String username) {
        EntitySaveResponseDto saveResponseDto = new EntitySaveResponseDto();
        if(StringUtils.isEmpty(type)){
            String errorMessage = "Error saving lookup value: lookup type is missing";
            logger.error(errorMessage);
            saveResponseDto.setErrorMessageEn(errorMessage);
            return saveResponseDto;
        }
        if(dto == null){
            String errorMessage = "Error saving lookup value: lookup value is missing";
            logger.error(errorMessage);
            saveResponseDto.setErrorMessageEn(errorMessage);
            return saveResponseDto;
        }

        try {
            if (type.equalsIgnoreCase("NIC_SINGULARITY_CHART_ACCOUNTS")) {
                return saveMatchingSingularityNICChartAccounts(dto, username);
            }else if (type.equalsIgnoreCase("NIC_TARRAGON_CHART_ACCOUNTS")) {
                return saveMatchingTarragonNICChartAccounts(dto, username);
            }else if (type.equalsIgnoreCase("NIC_TERRA_CHART_ACCOUNTS")) {
                return saveMatchingTerraNICChartAccounts(dto, username);
            }else{
                String errorMessage = "Error saving lookup value for type '" + type + "'. Could not find matching lookup by type.";
                logger.error(errorMessage);
                saveResponseDto.setErrorMessageEn(errorMessage);
            }

        }catch (Exception ex){
            String errorMessage = "Error saving lookup value for type '" + type + "'";
            logger.error(errorMessage, ex);
            saveResponseDto.setErrorMessageEn(errorMessage);
        }

        return saveResponseDto;
    }

    @Override
    public EntitySaveResponseDto saveNICChartOfAccounts(NICReportingChartOfAccountsDto dto, String username) {
        EntitySaveResponseDto saveResponseDto = new EntitySaveResponseDto();
        if(dto == null) {
            String errorMessage = "Error saving NIC Chart of Accounts: no value passed";
            logger.error(errorMessage);
            saveResponseDto.setErrorMessageEn(errorMessage);
            return saveResponseDto;
        }else if(StringUtils.isEmpty(dto.getNameRu())){
            String errorMessage = "Error saving NIC Chart of Accounts:: missing nameRu";
            logger.error(errorMessage);
            saveResponseDto.setErrorMessageEn(errorMessage);
            return saveResponseDto;
        }else if(dto.getNBChartOfAccounts() == null || StringUtils.isEmpty(dto.getNBChartOfAccounts().getCode())){
            String errorMessage = "Error saving NIC Chart of Accounts:: missing NB Chart of accounts";
            logger.error(errorMessage);
            saveResponseDto.setErrorMessageEn(errorMessage);
            return saveResponseDto;
        }

        try {
            NBChartOfAccounts nbChartOfAccounts = this.nbChartOfAccountsRepository.findByCode(dto.getNBChartOfAccounts().getCode());
            if (nbChartOfAccounts == null) {
                String errorMessage = "Error saving NIC Chart of Accounts:: NB Chart of accounts not found  by code '" + dto.getNBChartOfAccounts().getCode() + "'";
                logger.error(errorMessage);
                saveResponseDto.setErrorMessageEn(errorMessage);
                return saveResponseDto;
            }

            NICReportingChartOfAccounts entity = new NICReportingChartOfAccounts();

            String code = null;
            if(dto.getId() != null){
                NICReportingChartOfAccounts existingEntity = this.nicReportingChartOfAccountsRepository.findOne(dto.getId());
                entity.setId(existingEntity.getId());
                code = existingEntity.getCode();
            }else{
                // Generate entity code
                List<NICReportingChartOfAccounts> nicReportingChartOfAccountsByCode = this.nicReportingChartOfAccountsRepository.findByNBChartOfAccountsCode(nbChartOfAccounts.getCode());
                if(nicReportingChartOfAccountsByCode != null && !nicReportingChartOfAccountsByCode.isEmpty()){
                    String maxCode = null;
                    for(NICReportingChartOfAccounts existingEntity: nicReportingChartOfAccountsByCode){
                        if(existingEntity.getCode().equals(existingEntity.getCode().toUpperCase())){
                            // upper case
                            if(maxCode == null || maxCode.equals(maxCode.toLowerCase()) ||
                                    (maxCode.equals(maxCode.toUpperCase()) && maxCode.compareTo(existingEntity.getCode()) < 0)){
                                maxCode = existingEntity.getCode();
                            }
                        }else{
                            // lower case
                            if(maxCode == null || (maxCode.equals(maxCode.toLowerCase()) && maxCode.compareTo(existingEntity.getCode()) < 0)){
                                maxCode = existingEntity.getCode();
                            }
                        }

                    }
                    // get last part
                    String[] maxCodeSplit = maxCode.split("\\.");
                    if(maxCodeSplit != null && maxCodeSplit.length == 3){
                        String current = maxCodeSplit[2];
                        current = current.substring(current.length() - 1);
                        if(current.equals("z")){
                            code =  maxCode.replace(current, "A");
                        }else if(current.equals("Z")){
                            code = maxCodeSplit[2] + "a";
                        }else {
                            int charValue = current.charAt(0);
                            String next = String.valueOf((char) (charValue + 1));
                            //System.out.println(next);
                            code = maxCode.replace(current, next);
                        }
                    }else{
                        String errorMessage = "Error saving NIC Chart of Accounts: unexpected existing code format '" + maxCode +
                                "', expected e.g. 1033.010.a";
                        logger.error(errorMessage);
                        saveResponseDto.setErrorMessageEn(errorMessage);
                        return saveResponseDto;
                    }
                }else{ // first entry
                    code = nbChartOfAccounts.getCode() + ".a";
                }
            }
            entity.setCode(code);
            entity.setNameRu(dto.getNameRu());
            entity.setNbChartOfAccounts(nbChartOfAccounts);

            this.nicReportingChartOfAccountsRepository.save(entity);
            saveResponseDto.setSuccessMessageEn("Successfully saved NIC Chart of accounts");
            logger.info("Successfully saved lookup value fro NIC_CHART_ACCOUNTS: id="  + dto.getId() +
                    "name ru=" + dto.getNameRu() + " [user=" + username + "]");
        }catch (Exception ex){
            String errorMessage = "Error saving NIC Chart of Accounts";
            logger.error(errorMessage, ex);
            saveResponseDto.setErrorMessageEn(errorMessage);
        }

        return saveResponseDto;
    }

    @Override
    public boolean deleteMatchingLookupByTypeAndId(String type, Long id, String username){
        try {
//            if (type.equalsIgnoreCase("NIC_SINGULARITY_CHART_ACCOUNTS")) {
//                // TODO: delete NIC Chart accounts
//                return false;
//            }else
            if (type.equalsIgnoreCase("NIC_SINGULARITY_CHART_ACCOUNTS")) {
                SingularityNICChartOfAccounts entity = this.singularityNICChartOfAccountsRepository.findOne(id);
                if(entity == null){
                    logger.error("Failed to delete NIC_SINGULARITY_CHART_ACCOUNTS record with id " + id + ": record not found");
                    return false;
                }
                if(checkDeletableMatchingSingularityAccountNumber(entity.getSingularityAccountNumber())){
                    this.singularityNICChartOfAccountsRepository.delete(id);
                    logger.info("Successfully deleted NIC_SINGULARITY_CHART_ACCOUNTS lookup value: id=" + id + ", type=" + type +
                    ", account number=" + entity.getSingularityAccountNumber() + " [user=" + username + "]");
                    return true;
                }else{
                    logger.error("Failed to delete NIC_SINGULARITY_CHART_ACCOUNTS record with id " + id + ": record is not deletable");
                    return false;
                }

            }else if (type.equalsIgnoreCase("NIC_TARRAGON_CHART_ACCOUNTS")) {
                TarragonNICChartOfAccounts entity = this.tarragonNICChartOfAccountsRepository.findOne(id);
                if(entity == null){
                    logger.error("Failed to delete NIC_TARRAGON_CHART_ACCOUNTS record with id " + id + ": record not found");
                    return false;
                }
                if(checkDeletableMatchingTarragonChartAccountsLongDescription(entity.getTarragonChartOfAccountsName())){
                    this.tarragonNICChartOfAccountsRepository.delete(id);
                    logger.info("Successfully deleted NIC_TARRAGON_CHART_ACCOUNTS lookup value: id=" + id + ", type=" + type +
                            ", name=" + entity.getTarragonChartOfAccountsName() + " [user=" + username + "]");
                    return true;
                }else{
                    logger.error("Failed to delete NIC_TARRAGON_CHART_ACCOUNTS record with id " + id + ": record is not deletable");
                    return false;
                }

            }else if (type.equalsIgnoreCase("NIC_TERRA_CHART_ACCOUNTS")) {
                TerraNICChartOfAccounts entity = this.terraNICChartOfAccountsRepository.findOne(id);
                if(entity == null){
                    logger.error("Failed to delete NIC_TERRA_CHART_ACCOUNTS record with id " + id + ": record not found");
                    return false;
                }
                if(checkDeletableMatchingTerraChartAccountsLongDescription(entity.getTerraChartOfAccountsName())){
                    this.terraNICChartOfAccountsRepository.delete(id);
                    logger.info("Successfully deleted NIC_TERRA_CHART_ACCOUNTS lookup value: id=" + id + ", type=" + type +
                            ", name=" + entity.getTerraChartOfAccountsName() + " [user=" + username + "]");
                    return true;
                }else{
                    logger.error("Failed to delete NIC_TERRA_CHART_ACCOUNTS record with id " + id + ": record is not deletable");
                    return false;
                }
            }else{
                logger.error("Error deleting Matching Chart of accounts lookup value with id " + id + ": type is not known - '" + type + "'");
            }
        }catch (Exception ex){
            logger.error("Error deleting Matching Chart of accounts lookup value with id " + id + ", type '" + type + "'", ex);
        }
        return false;
    }

    @Override
    public List<BaseDictionaryDto> getICMeetingTopicTypes() {
        try {
            List<BaseDictionaryDto> dtoList = new ArrayList<>();
            Iterator<ICMeetingTopicType> iterator = this.icMeetingTypeRepository.findAll().iterator();
            while (iterator.hasNext()) {
                ICMeetingTopicType entity = iterator.next();
                BaseDictionaryDto dto = disassemble(entity);
                dtoList.add(dto);
            }
            return dtoList;
        } catch (Exception ex) {
            logger.error("Failed to load lookup: ICMeetingTopicType", ex);
        }
        return null;
    }

    @Override
    public List<BaseDictionaryDto> getICMeetingAbsenceTypes() {
        try {
            List<BaseDictionaryDto> dtoList = new ArrayList<>();
            Iterator<ICMeetingAttendeeAbsenceType> iterator = this.icMeetingAttendeeAbsenceTypeRepository.findAll().iterator();
            while (iterator.hasNext()) {
                ICMeetingAttendeeAbsenceType entity = iterator.next();
                BaseDictionaryDto dto = disassemble(entity);
                dtoList.add(dto);
            }
            return dtoList;
        } catch (Exception ex) {
            logger.error("Failed to load lookup: ICMeetingAttendeeAbsenceType", ex);
        }
        return null;
    }

    @Override
    public List<BaseDictionaryDto> getICMeetingPlaceTypes() {
        try {
            List<BaseDictionaryDto> dtoList = new ArrayList<>();
            Iterator<ICMeetingPlaceType> iterator = this.icMeetingPlaceTypeRepository.findAll().iterator();
            while (iterator.hasNext()) {
                ICMeetingPlaceType entity = iterator.next();
                BaseDictionaryDto dto = disassemble(entity);
                dtoList.add(dto);
            }
            return dtoList;
        } catch (Exception ex) {
            logger.error("Failed to load lookup: ICMeetingPlaceType", ex);
        }
        return null;
    }

    @Override
    public List<BaseDictionaryDto> getICMeetingVoteTypes() {
        try {
            List<BaseDictionaryDto> dtoList = new ArrayList<>();
            Iterator<ICMeetingVoteType> iterator = this.icMeetingVoteTypeRepository.findAll().iterator();
            while (iterator.hasNext()) {
                ICMeetingVoteType entity = iterator.next();
                BaseDictionaryDto dto = disassemble(entity);
                dtoList.add(dto);
            }
            return dtoList;
        } catch (Exception ex) {
            logger.error("Failed to load lookup: ICMeetingVoteType", ex);
        }
        return null;
    }



    private boolean checkDeletableMatchingSingularityAccountNumber(String accountNumber){
        if(accountNumber == null){
            return false;
        }
        // Get reports
        List<PeriodicReportDto> periodicReportDots = this.periodicReportService.getAllPeriodicReports();
        for(PeriodicReportDto report: periodicReportDots){
            //if(report.getStatus() != null && !report.getStatus().equalsIgnoreCase(kz.nicnbk.service.dto.reporting.PeriodicReportType.SUBMITTED.getCode())){
                ConsolidatedReportRecordHolderDto holder = this.hfGeneralLedgerBalanceService.getWithExcludedRecords(report.getId());
                if(holder != null && holder.getGeneralLedgerBalanceList() != null){
                    for(SingularityGeneralLedgerBalanceRecordDto record: holder.getGeneralLedgerBalanceList()){
                        if(record.getGLAccount() != null && record.getGLAccount().startsWith(accountNumber)){
                            return false;
                        }
                    }
                }
            //}
        }
        return true;
    }

    private boolean checkDeletableMatchingTarragonChartAccountsLongDescription(String chartAccountsLongDescription){
        if(chartAccountsLongDescription == null){
            return false;
        }
        // Get reports
        List<PeriodicReportDto> periodicReportDots = this.periodicReportService.getAllPeriodicReports();
        for(PeriodicReportDto report: periodicReportDots){
            //if(report.getStatus() != null && !report.getStatus().equalsIgnoreCase(kz.nicnbk.service.dto.reporting.PeriodicReportType.SUBMITTED.getCode())){
                ListResponseDto listResponseDto = this.periodicReportPEService.getTarragonGeneratedFormWithExcluded(report.getId());
                if(listResponseDto != null && listResponseDto.getRecords() != null){
                    for(GeneratedGeneralLedgerFormDto record: (List<GeneratedGeneralLedgerFormDto>) listResponseDto.getRecords()){
                        if(record.getChartAccountsLongDescription() != null && record.getChartAccountsLongDescription().equalsIgnoreCase(chartAccountsLongDescription)){
                            return false;
                        }
                    }
                }
            //}
        }
        return true;
    }

    private boolean checkDeletableMatchingTerraChartAccountsLongDescription(String chartAccountsLongDescription){
        if(chartAccountsLongDescription == null){
            return false;
        }
        // Get reports
        List<PeriodicReportDto> periodicReportDots = this.periodicReportService.getAllPeriodicReports();
        for(PeriodicReportDto report: periodicReportDots){
            //if(report.getStatus() != null && !report.getStatus().equalsIgnoreCase(kz.nicnbk.service.dto.reporting.PeriodicReportType.SUBMITTED.getCode())){
                ListResponseDto listResponseDto = this.periodicReportREService.getTerraGeneralLedgerFormDataWithoutExcluded(report.getId());
                if(listResponseDto != null && listResponseDto.getRecords() != null){
                    for(GeneratedGeneralLedgerFormDto record: (List<GeneratedGeneralLedgerFormDto>) listResponseDto.getRecords()){
                        if(record.getChartAccountsLongDescription() != null && record.getChartAccountsLongDescription().equalsIgnoreCase(chartAccountsLongDescription)){
                            return false;
                        }
                    }
                }
            //}
        }
        return true;
    }

}