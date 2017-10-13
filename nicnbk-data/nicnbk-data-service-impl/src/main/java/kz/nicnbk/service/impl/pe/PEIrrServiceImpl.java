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
        try {
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
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public Double getIRR(List<PEGrossCashflowDto> cashflowDtoList) {

        double a = 0.0;
        double b = 0.0;

        for (int i = 0; i < 1000; i++) {
            if (i == 999) {
                return null;
            }
            if (getNPV(cashflowDtoList, - i / (double) (1 + i)) > 0) {
                a = - i / (double) (1 + i);
                break;
            }
        }

        for (int i = 0; i < 1000; i++) {
            if (i == 999) {
                return null;
            }
            if (getNPV(cashflowDtoList, (double) i ) < 0) {
                b = (double) i;
                break;
            }
        }

//        while (Math.abs(getNPV(cashflowDtoList, a) - getNPV(cashflowDtoList, b)) > 0.000001)
        while (Math.abs(a - b) > 0.0000000000000001) {
            if (getNPV(cashflowDtoList, (a + b) / 2) > 0) {
                a = (a + b) / 2;
            } else {
                b = (a + b) / 2;
            }
        }

        //        return (Math.pow(1 + a, 365.0) - 1) * 100;
        return Math.round((Math.pow(1 + a, 365.0) - 1) * 10000) / 100.0;
    }
}
