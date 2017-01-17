package kz.nicnbk.service.datamanager;

import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.repo.api.lookup.*;
import kz.nicnbk.repo.api.lookup.CurrencyRepository;
import kz.nicnbk.repo.api.lookup.GeographyRepository;
import kz.nicnbk.repo.api.lookup.StrategyRepository;
import kz.nicnbk.repo.api.pe.IndustryRepository;
import kz.nicnbk.repo.model.base.BaseTypeEntity;
import kz.nicnbk.repo.model.common.*;
import kz.nicnbk.repo.model.files.FilesType;
import kz.nicnbk.repo.model.hf.*;
import kz.nicnbk.repo.model.m2s2.MeetingArrangedBy;
import kz.nicnbk.repo.model.m2s2.MeetingType;
import kz.nicnbk.repo.model.news.NewsType;
import kz.nicnbk.repo.model.pe.PEIndustry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by magzumov on 07.07.2016.
 */
@Service
public class LookupServiceImpl implements LookupService {

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
    private MeetingTypeRepository meetingTypeRepository;

    @Autowired
    private MeetingArrangedByRepository meetingArrangedByRepository;

    @Autowired
    private FilesTypeRepository filesTypeRepository;

    @Override
    public <T extends BaseTypeEntity> T findByTypeAndCode(Class<T> clazz, String code) {

        // TODO: implement lookup cash

        if(code == null){
            return null;
        }

        if(clazz.equals(NewsType.class)){
            return (T) this.newsTypeRepository.findByCode(code);
        }else if(clazz.equals(MeetingType.class)){
            return (T) this.meetingTypeRepository.findByCode(code);
        }else if(clazz.equals(MeetingArrangedBy.class)){
            return (T) this.meetingArrangedByRepository.findByCode(code);
        }else if(clazz.equals(Currency.class)){
            return (T) this.currencyRepository.findByCode(code);
        }else if(clazz.equals(FilesType.class)){
            return (T) this.filesTypeRepository.findByCode(code);
        }else if(clazz.equals(Strategy.class)){
            return (T) this.strategyRepository.findByCode(code);
        }else if(clazz.equals(Geography.class)){
            return (T) this.geographyRepository.findByCode(code);
        }else if(clazz.equals(HedgeFundStatus.class)){
            return (T) this.managerStatusRepository.findByCode(code);
        }else if(clazz.equals(Country.class)){
            return (T) this.countryRepository.findByCode(code);
        }else if(clazz.equals(LegalStructure.class)){
            return (T) this.legalStructureRepository.findByCode(code);
        }else if(clazz.equals(SubscriptionFrequency.class)){
            return (T) this.subscriptionFrequencyRepository.findByCode(code);
        }else if(clazz.equals(RedemptionFrequency.class)){
            return (T) this.redemptionFrequencyRepository.findByCode(code);
        }else if(clazz.equals(RedemptionNotificationPeriod.class)){
            return (T) this.redemptionNotificationPeriodRepository.findByCode(code);
        }else if(clazz.equals(HedgeFundSidePocket.class)){
            return (T) this.hedgeFundSidePocketRepository.findByCode(code);
        }else if(clazz.equals(Substrategy.class)){
            return (T) this.substrategyRepository.findByCode(code);
        }

        if(clazz.getSimpleName().equals("Strategy")){
            Iterator<Strategy> iterator = strategyRepository.findAll().iterator();
            while(iterator.hasNext()){
                Strategy strategy = iterator.next();
                if(strategy.getCode().equals(code)){
                    return (T) strategy;
                };
            }

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
        List<BaseDictionaryDto> dtoList = new ArrayList<>();
        Iterator<Currency> iterator = this.currencyRepository.findAll().iterator();
        while(iterator.hasNext()){
            Currency currency = iterator.next();
            BaseDictionaryDto geographyDto = disassemble(currency);
            dtoList.add(geographyDto);
        }
        return dtoList;
    }

    @Override
    public List<BaseDictionaryDto> getHedgeFundStatuses() {
        List<BaseDictionaryDto> dtoList = new ArrayList<>();
        Iterator<HedgeFundStatus> iterator = this.hedgeFundStatusRepository.findAll().iterator();
        while(iterator.hasNext()){
            HedgeFundStatus entity = iterator.next();
            BaseDictionaryDto dto = disassemble(entity);
            dtoList.add(dto);
        }
        return dtoList;
    }

    @Override
    public List<BaseDictionaryDto> getSubscriptionFrequencies() {
        List<BaseDictionaryDto> dtoList = new ArrayList<>();
        Iterator<SubscriptionFrequency> iterator = this.subscriptionFrequencyRepository.findAll().iterator();
        while(iterator.hasNext()){
            SubscriptionFrequency entity = iterator.next();
            BaseDictionaryDto dto = disassemble(entity);
            dtoList.add(dto);
        }
        return dtoList;
    }

    @Override
    public List<BaseDictionaryDto> getRedemptionFrequencies() {
        List<BaseDictionaryDto> dtoList = new ArrayList<>();
        Iterator<RedemptionFrequency> iterator = this.redemptionFrequencyRepository.findAll().iterator();
        while(iterator.hasNext()){
            RedemptionFrequency entity = iterator.next();
            BaseDictionaryDto dto = disassemble(entity);
            dtoList.add(dto);
        }
        return dtoList;
    }

    @Override
    public List<BaseDictionaryDto> getRedemptionNoticePeriods() {
        List<BaseDictionaryDto> dtoList = new ArrayList<>();
        Iterator<RedemptionNotificationPeriod> iterator = this.redemptionNotificationPeriodRepository.findAll().iterator();
        while(iterator.hasNext()){
            RedemptionNotificationPeriod entity = iterator.next();
            BaseDictionaryDto dto = disassemble(entity);
            dtoList.add(dto);
        }
        return dtoList;
    }

    @Override
    public List<BaseDictionaryDto> getSidePockets() {
        List<BaseDictionaryDto> dtoList = new ArrayList<>();
        Iterator<HedgeFundSidePocket> iterator = this.hedgeFundSidePocketRepository.findAll().iterator();
        while(iterator.hasNext()){
            HedgeFundSidePocket entity = iterator.next();
            BaseDictionaryDto dto = disassemble(entity);
            dtoList.add(dto);
        }
        return dtoList;
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
        List<BaseDictionaryDto> dtoList = new ArrayList<>();
        List<Strategy> entityList = this.strategyRepository.findByGroupType(group);
        if(entityList != null) {
            for (Strategy entity : entityList) {
                BaseDictionaryDto dto = disassemble(entity);
                dtoList.add(dto);
            }
        }
        return dtoList;
    }

    private List<BaseDictionaryDto> getSubStrategies(String strategyCode){
        List<BaseDictionaryDto> dtoList = new ArrayList<>();
        List<Substrategy> entityList = this.substrategyRepository.findByStrategy(strategyCode);
        if(entityList != null) {
            for (Substrategy entity : entityList) {
                BaseDictionaryDto dto = disassemble(entity);
                dtoList.add(dto);
            }
        }
        return dtoList;
    }

    @Override
    public List<BaseDictionaryDto> getGeographies(){
        List<BaseDictionaryDto> dtoList = new ArrayList<>();
        Iterator<Geography> iterator = this.geographyRepository.findAll().iterator();
        while(iterator.hasNext()){
            Geography entity = iterator.next();
            BaseDictionaryDto geographyDto = disassemble(entity);
            dtoList.add(geographyDto);
        }
        return dtoList;
    }

    @Override
    public List<BaseDictionaryDto> getPEIndustry(){
        List<BaseDictionaryDto> dtoList = new ArrayList<>();
        Iterator<PEIndustry> iterator = this.industryRepository.findAll().iterator();
        while(iterator.hasNext()){
            PEIndustry entity = iterator.next();
            BaseDictionaryDto industryDto = disassemble(entity);
            dtoList.add(industryDto);
        }
        return dtoList;
    }

    // TODO: refactor as common lookup converter
    private BaseDictionaryDto disassemble(BaseTypeEntity entity){
        BaseDictionaryDto dto = new BaseDictionaryDto();
        dto.setCode(entity.getCode());
        dto.setNameEn(entity.getNameEn());
        return dto;
    }

}
