package kz.nicnbk.service.datamanager;

import kz.nicnbk.repo.model.base.BaseTypeEntity;
import kz.nicnbk.repo.model.common.Currency;
import kz.nicnbk.repo.model.files.FilesType;
import kz.nicnbk.repo.model.lookup.FileTypeLookup;
import kz.nicnbk.repo.model.lookup.MeetingTypeLookup;
import kz.nicnbk.repo.model.lookup.NewsTypeLookup;
import kz.nicnbk.repo.model.m2s2.MeetingArrangedBy;
import kz.nicnbk.repo.model.m2s2.MeetingType;
import kz.nicnbk.repo.model.news.NewsType;
import org.springframework.stereotype.Service;

/**
 * Created by magzumov on 07.07.2016.
 */
@Service
public class LookupTypeServiceImpl implements LookupTypeService {

    @Override
    public <T extends BaseTypeEntity> T findByTypeAndCode(Class<T> clazz, String code) {

        // TODO: implement lookup cash

        if(code == null){
            return null;
        }

        if(clazz.getSimpleName().equals("NewsType")){
            if(code.equals(NewsTypeLookup.GENERAL.getCode())){
                NewsType newsType = new NewsType();
                newsType.setId(10);
                newsType.setCode("GENERAL");
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
                arrangedBy.setId(10);
                arrangedBy.setCode(MeetingArrangedBy.BY_NIC);
                return (T) arrangedBy;
            }
        }
        if(clazz.getSimpleName().equals("Currency")){
            if(code.equals(Currency.CURR1)){
                Currency currency = new Currency();
                currency.setId(10);
                currency.setCode(Currency.CURR1);
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
        return null;
    }
}
