package kz.nicnbk.service.datamanager;

import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.repo.api.lookup.*;
import kz.nicnbk.repo.api.lookup.CurrencyRepository;
import kz.nicnbk.repo.api.lookup.GeographyRepository;
import kz.nicnbk.repo.api.lookup.StrategyRepository;
import kz.nicnbk.repo.api.macromonitor.MacroMonitorFieldRepository;
import kz.nicnbk.repo.api.macromonitor.MacroMonitorTypeRepository;
import kz.nicnbk.repo.api.pe.IndustryRepository;
import kz.nicnbk.repo.api.reporting.NBChartOfAccountsRepository;
import kz.nicnbk.repo.api.reporting.NICReportingChartOfAccountsRepository;
import kz.nicnbk.repo.api.reporting.ReserveCalculationEntityTypeRepository;
import kz.nicnbk.repo.api.reporting.ReserveCalculationExpenseTypeRepository;
import kz.nicnbk.repo.api.reporting.privateequity.TarragonNICChartOfAccountsRepository;
import kz.nicnbk.repo.model.base.BaseTypeEntity;
import kz.nicnbk.repo.model.common.*;
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
import kz.nicnbk.repo.model.reporting.hedgefunds.FinancialStatementCategory;
import kz.nicnbk.repo.model.reporting.privateequity.PEInvestmentType;
import kz.nicnbk.repo.model.reporting.privateequity.TarragonNICChartOfAccounts;
import kz.nicnbk.repo.model.tripmemo.TripType;
import kz.nicnbk.service.dto.reporting.NICReportingChartOfAccountsDto;
import kz.nicnbk.service.dto.reporting.TarragonNICReportingChartOfAccountsDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    private NICReportingChartOfAccountsRepository nicReportingChartOfAccountsRepository;

    @Autowired
    private TarragonNICChartOfAccountsRepository tarragonNICChartOfAccountsRepository;

    @Autowired
    private ReserveCalculationExpenseTypeRepository reserveCalculationExpenseTypeRepository;

    @Autowired
    private ReserveCalculationEntityTypeRepository reserveCalculationEntityTypeRepository;

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

    private List<BaseDictionaryDto> getStrategies(int group){
        try {
            List<BaseDictionaryDto> dtoList = new ArrayList<>();
            List<Strategy> entityList = this.strategyRepository.findByGroupType(group);
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
                dtoList.add(dto);
            }
            return dtoList;
        }catch (Exception ex){
            logger.error("Failed to load lookup: NICReportingChartOfAccounts", ex);
        }
        return null;

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
        dto.setCode(entity.getCode());
        dto.setNameEn(entity.getNameEn());
        dto.setNameRu(entity.getNameRu());
        dto.setNameKz(entity.getNameKz());
        return dto;
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
}
