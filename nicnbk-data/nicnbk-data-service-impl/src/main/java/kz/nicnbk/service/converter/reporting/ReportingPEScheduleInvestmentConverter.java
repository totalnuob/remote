package kz.nicnbk.service.converter.reporting;

import kz.nicnbk.repo.model.reporting.privateequity.ReportingPEScheduleInvestment;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.dto.reporting.ScheduleInvestmentsDto;
import org.springframework.stereotype.Component;

/**
 * Created by magzumov on 20.04.2017.
 */
@Component
public class ReportingPEScheduleInvestmentConverter extends BaseDozerEntityConverter<ReportingPEScheduleInvestment, ScheduleInvestmentsDto> {


//    @Override
//    public ScheduleInvestmentsDto disassemble(ReportingPEScheduleInvestment entity){
//        ScheduleInvestmentsDto dto = super.disassemble(entity);
//        return dto;
//    }

}
