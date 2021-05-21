package kz.nicnbk.service.converter.employee;

import kz.nicnbk.repo.model.employee.ResetToken;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.converter.dozer.BaseDozerTypedEntityConverter;
import kz.nicnbk.service.dto.employee.ResetTokenDto;
import org.springframework.stereotype.Component;

@Component
public class ResetTokenEntityConverter extends BaseDozerEntityConverter<ResetToken, ResetTokenDto> {
}
