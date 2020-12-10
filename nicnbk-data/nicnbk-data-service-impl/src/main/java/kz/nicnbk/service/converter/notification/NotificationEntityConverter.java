package kz.nicnbk.service.converter.notification;

import kz.nicnbk.repo.model.news.News;
import kz.nicnbk.repo.model.news.NewsType;
import kz.nicnbk.repo.model.notification.Notification;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.datamanager.LookupService;
import kz.nicnbk.service.dto.notification.NotificationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NotificationEntityConverter extends BaseDozerEntityConverter<Notification, NotificationDto> {

//    @Override
//    public Notification assemble(NotificationDto dto) {
//        Notification entity = super.assemble(dto);
//        return entity;
//    }
//
//    @Override
//    public NotificationDto disassemble(Notification entity) {
//        NotificationDto dto = super.disassemble(entity);
//        return dto;
//    }
}
