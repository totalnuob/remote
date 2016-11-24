package kz.nicnbk.service.datamanager;

import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.repo.api.lookup.*;
import kz.nicnbk.repo.model.base.BaseTypeEntity;
import kz.nicnbk.repo.model.common.*;
import kz.nicnbk.repo.model.files.FilesType;
import kz.nicnbk.repo.model.hf.*;
import kz.nicnbk.repo.model.lookup.FileTypeLookup;
import kz.nicnbk.repo.model.lookup.MeetingTypeLookup;
import kz.nicnbk.repo.model.lookup.NewsTypeLookup;
import kz.nicnbk.repo.model.m2s2.MeetingArrangedBy;
import kz.nicnbk.repo.model.m2s2.MeetingType;
import kz.nicnbk.repo.model.news.NewsType;
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
    private ManagerTypeRepository managerTypeRepository;

    @Autowired
    private ManagerStatusRepository managerStatusRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private LegalStructureRepository legalStructureRepository;

    @Autowired
    private SubscriptionFrequencyRepository subscriptionFrequencyRepository;

    @Autowired
    private ManagementFeeTypeRepository managementFeeTypeRepository;

    @Autowired
    private PerformanceFeeTypeRepository performanceFeeTypeRepository;

    @Autowired
    private PerformanceFeePayFrequencyRepository performanceFeePayFrequencyRepository;

    @Autowired
    private RedemptionFrequencyTypeRepository redemptionFrequencyTypeRepository;

    @Autowired
    private RedemptionNotificationPeriodRepository redemptionNotificationPeriodRepository;



    @Override
    public <T extends BaseTypeEntity> T findByTypeAndCode(Class<T> clazz, String code) {

        // TODO: implement lookup cash

        if(code == null){
            return null;
        }

        if(clazz.getSimpleName().equals("NewsType")){
            if(code.equals(NewsTypeLookup.GENERAL.getCode())){
                NewsType newsType = new NewsType();
                newsType.setId(1);
                newsType.setCode("GENERAL");
                return (T) newsType;
            }
            if(code.equals(NewsTypeLookup.PRIVATE_EQUITY.getCode())){
                NewsType newsType = new NewsType();
                newsType.setId(2);
                newsType.setCode("PE");
                return (T) newsType;
            }
            if(code.equals(NewsTypeLookup.HEDGE_FUNDS.getCode())){
                NewsType newsType = new NewsType();
                newsType.setId(3);
                newsType.setCode("HF");
                return (T) newsType;
            }
            if(code.equals(NewsTypeLookup.SOVEREIGN_WEALTH_FUNDS
                    .getCode())){
                NewsType newsType = new NewsType();
                newsType.setId(4);
                newsType.setCode("SWF");
                return (T) newsType;
            }
            if(code.equals(NewsTypeLookup.REAL_ESTATE
                    .getCode())){
                NewsType newsType = new NewsType();
                newsType.setId(5);
                newsType.setCode("RE");
                return (T) newsType;
            }
            if(code.equals(NewsTypeLookup.RISK_MANAGEMENT
                    .getCode())){
                NewsType newsType = new NewsType();
                newsType.setId(6);
                newsType.setCode("RM");
                return (T) newsType;
            }
        }

        if(clazz.getSimpleName().equals("MeetingType")){
            if(code.equals(MeetingTypeLookup.MEETING.getCode())){
                MeetingType meetingType = new MeetingType();
                meetingType.setId(1);
                meetingType.setCode(MeetingTypeLookup.MEETING.getCode());
                return (T) meetingType;
            }
            if(code.equals(MeetingTypeLookup.CALL.getCode())){
                MeetingType meetingType = new MeetingType();
                meetingType.setId(2);
                meetingType.setCode(MeetingTypeLookup.CALL.getCode());
                return (T) meetingType;
            }
        }

        if(clazz.getSimpleName().equals("MeetingArrangedBy")){
            if(code.equals(MeetingArrangedBy.BY_NIC)){
                MeetingArrangedBy arrangedBy = new MeetingArrangedBy();
                arrangedBy.setId(1);
                arrangedBy.setCode(MeetingArrangedBy.BY_NIC);
                return (T) arrangedBy;
            }
            if(code.equals(MeetingArrangedBy.BY_GP)){
                MeetingArrangedBy arrangedBy = new MeetingArrangedBy();
                arrangedBy.setId(2);
                arrangedBy.setCode(MeetingArrangedBy.BY_GP);
                return (T) arrangedBy;
            }
            if(code.equals(MeetingArrangedBy.BY_OTHER)){
                MeetingArrangedBy arrangedBy = new MeetingArrangedBy();
                arrangedBy.setId(3);
                arrangedBy.setCode(MeetingArrangedBy.BY_OTHER);
                return (T) arrangedBy;
            }
        }
        if(clazz.getSimpleName().equals("Currency")){
            Iterator<Currency> iterator = currencyRepository.findAll().iterator();
            while(iterator.hasNext()){
                Currency currency = iterator.next();
                if(currency.getCode().equals(code)){
                    return (T) currency;
                };
            }
        }

        if(clazz.getSimpleName().equals("FilesType")){
            if(code.equals(FileTypeLookup.MEMO_ATTACHMENT.getCode())){
                FilesType filesType = new FilesType();
                filesType.setId(1);
                filesType.setCode(FileTypeLookup.MEMO_ATTACHMENT.getCode());
                return (T) filesType;
            }
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

        if(clazz.getSimpleName().equals("ManagerType")){
            Iterator<ManagerType> iterator = managerTypeRepository.findAll().iterator();
            while(iterator.hasNext()){
                ManagerType managerType = iterator.next();
                if(managerType.getCode().equals(code)){
                    return (T) managerType;
                };
            }

        }

        if(clazz.getSimpleName().equals("FundStatus")){
            Iterator<FundStatus> iterator = managerStatusRepository.findAll().iterator();
            while(iterator.hasNext()){
                FundStatus type = iterator.next();
                if(type.getCode().equals(code)){
                    return (T) type;
                };
            }
        }

        if(clazz.getSimpleName().equals("Country")){
            Iterator<Country> iterator = countryRepository.findAll().iterator();
            while(iterator.hasNext()){
                Country type = iterator.next();
                if(type.getCode().equals(code)){
                    return (T) type;
                };
            }
        }

        if(clazz.getSimpleName().equals("LegalStructure")){
            Iterator<LegalStructure> iterator = legalStructureRepository.findAll().iterator();
            while(iterator.hasNext()){
                LegalStructure type = iterator.next();
                if(type.getCode().equals(code)){
                    return (T) type;
                };
            }
        }

        if(clazz.getSimpleName().equals("SubscriptionFrequency")){
            Iterator<SubscriptionFrequency> iterator = this.subscriptionFrequencyRepository.findAll().iterator();
            while(iterator.hasNext()){
                SubscriptionFrequency type = iterator.next();
                if(type.getCode().equals(code)){
                    return (T) type;
                };
            }
        }

        if(clazz.getSimpleName().equals("ManagementFeeType")){
            Iterator<ManagementFeeType> iterator = this.managementFeeTypeRepository.findAll().iterator();
            while(iterator.hasNext()){
                ManagementFeeType type = iterator.next();
                if(type.getCode().equals(code)){
                    return (T) type;
                };
            }
        }

        if(clazz.getSimpleName().equals("PerformanceFeeType")){
            Iterator<PerformanceFeeType> iterator = this.performanceFeeTypeRepository.findAll().iterator();
            while(iterator.hasNext()){
                PerformanceFeeType type = iterator.next();
                if(type.getCode().equals(code)){
                    return (T) type;
                };
            }
        }

        if(clazz.getSimpleName().equals("PerformanceFeePayFrequencyType")){
            Iterator<PerformanceFeePayFrequencyType> iterator = this.performanceFeePayFrequencyRepository.findAll().iterator();
            while(iterator.hasNext()){
                PerformanceFeePayFrequencyType type = iterator.next();
                if(type.getCode().equals(code)){
                    return (T) type;
                };
            }
        }

        if(clazz.getSimpleName().equals("RedemptionFrequencyType")){
            Iterator<RedemptionFrequencyType> iterator = this.redemptionFrequencyTypeRepository.findAll().iterator();
            while(iterator.hasNext()){
                RedemptionFrequencyType type = iterator.next();
                if(type.getCode().equals(code)){
                    return (T) type;
                };
            }
        }

        if(clazz.getSimpleName().equals("RedemptionNotificationPeriodType")){
            Iterator<RedemptionNotificationPeriodType> iterator = this.redemptionNotificationPeriodRepository.findAll().iterator();
            while(iterator.hasNext()){
                RedemptionNotificationPeriodType type = iterator.next();
                if(type.getCode().equals(code)){
                    return (T) type;
                };
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
    public List<BaseDictionaryDto> getPrivateEquityStrategies(){
        return getStrategies(Strategy.TYPE_PRIVATE_EQUITY);
    }

    @Override
    public List<BaseDictionaryDto> getHedgeFundsStrategy(){
        return getStrategies(Strategy.TYPE_HEDGE_FUNDS);
    }

    @Override
    public List<BaseDictionaryDto> getHedgeFundsSubStrategy(String strategy){
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

    // TODO: refactor as common lookup converter
    private BaseDictionaryDto disassemble(BaseTypeEntity entity){
        BaseDictionaryDto dto = new BaseDictionaryDto();
        dto.setCode(entity.getCode());
        dto.setNameEn(entity.getNameEn());
        return dto;
    }

}
