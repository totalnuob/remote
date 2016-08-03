package kz.nicnbk.service.datamanager;

import kz.nicnbk.repo.api.lookup.GeographyRepository;
import kz.nicnbk.repo.api.lookup.StrategyRepository;
import kz.nicnbk.repo.model.base.BaseTypeEntity;
import kz.nicnbk.repo.model.common.Currency;
import kz.nicnbk.repo.model.common.Geography;
import kz.nicnbk.repo.model.common.Strategy;
import kz.nicnbk.repo.model.files.FilesType;
import kz.nicnbk.repo.model.lookup.FileTypeLookup;
import kz.nicnbk.repo.model.lookup.MeetingTypeLookup;
import kz.nicnbk.repo.model.lookup.NewsTypeLookup;
import kz.nicnbk.repo.model.m2s2.MeetingArrangedBy;
import kz.nicnbk.repo.model.m2s2.MeetingType;
import kz.nicnbk.repo.model.news.NewsType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;

/**
 * Created by magzumov on 07.07.2016.
 */
@Service
public class LookupTypeServiceImpl implements LookupTypeService {

    @Autowired
    private GeographyRepository geographyRepository;

    @Autowired
    private StrategyRepository strategyRepository;

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
            if(code.equals(Currency.USD)){
                Currency currency = new Currency();
                currency.setId(1);
                currency.setCode(Currency.USD);
                return (T) currency;
            }
        }
        if(clazz.getSimpleName().equals("Currency")){
            if(code.equals(Currency.EUR)){
                Currency currency = new Currency();
                currency.setId(2);
                currency.setCode(Currency.EUR);
                return (T) currency;
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
        return null;
    }
}
