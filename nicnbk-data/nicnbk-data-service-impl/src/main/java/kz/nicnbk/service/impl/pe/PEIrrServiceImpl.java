package kz.nicnbk.service.impl.pe;

import kz.nicnbk.service.api.pe.PEIrrService;
import kz.nicnbk.service.dto.pe.PEGrossCashflowDto;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by Pak on 13.10.2017.
 */
@Service
public class PEIrrServiceImpl implements PEIrrService {

    @Override
    public Double getNPV(List<PEGrossCashflowDto> cashflowDtoList, double dailyRate) {

        if (cashflowDtoList == null || dailyRate == -1) {
            return null;
        }

        double sum = 0.0;

        if (cashflowDtoList.isEmpty()) {
            return sum;
        }

        Date initialDate = cashflowDtoList.get(0).getDate();

        for (PEGrossCashflowDto cashflowDto : cashflowDtoList) {
            if (cashflowDto.getDate() == null) {
                return null;
            }
            sum += cashflowDto.getGrossCF() == null ? 0.0 : cashflowDto.getGrossCF() / Math.pow(1 + dailyRate, (cashflowDto.getDate().getTime() - initialDate.getTime()) / 86400000);
        }

        return sum;
    }

    @Override
    public Double getIRR(List<PEGrossCashflowDto> cashflowDtoList) {

        getNPV(cashflowDtoList,0.0001);

        return 1.0;
    }
}
