package kz.nicnbk.service.impl.pe;

import kz.nicnbk.service.api.pe.PEIrrService;
import kz.nicnbk.service.dto.pe.PEGrossCashflowDto;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

            if (cashflowDtoList.isEmpty()) {
                return 0.0;
            }

            Date initialDate = cashflowDtoList.get(0).getDate();

            double doubleSum = 0.0;
            BigDecimal bigDecimalSum = new BigDecimal(0);

            for (PEGrossCashflowDto cashflowDto : cashflowDtoList) {
                if (cashflowDto.getDate() == null) {
                    return null;
                }
                if (cashflowDto.getGrossCF() != null) {
                    doubleSum += cashflowDto.getGrossCF() / Math.pow(1 + dailyRate, (cashflowDto.getDate().getTime() - initialDate.getTime()) / 86400000);
                    BigDecimal a = new BigDecimal(Math.pow(1 + dailyRate, (cashflowDto.getDate().getTime() - initialDate.getTime()) / 86400000));
                    BigDecimal b = new BigDecimal(cashflowDto.getGrossCF()).divide(a, 100, BigDecimal.ROUND_CEILING);
                    bigDecimalSum = bigDecimalSum.add(b);
                }
            }

//            return bigDecimalSum.doubleValue();
            return doubleSum;
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public Double getIRR(List<PEGrossCashflowDto> cashflowDtoList) {

        Double a = null;
        Double b = null;
        long N = 10000000;

        // fast search
        for (int i = 0; i < 100; i++) {
            Double npv = getNPV(cashflowDtoList, - i / (double) (1 + i));
            if ( npv != null && npv >= 0) {
                a = - i / (double) (1 + i);
                break;
            }
        }

        for (int i = 0; i < 100; i++) {
            Double npv = getNPV(cashflowDtoList, (double) i );
            if ( npv != null && npv <= 0) {
                b = (double) i;
                break;
            }
        }

        // deep search
//        if (a == null) {
//            for (long i = 0; i < 2 * N; i++) {
//                Double npv = getNPV(cashflowDtoList, i / (double) N - 1);
//                if ( npv != null && npv >= 0) {
//                    a = i / (double) N - 1;
//                    break;
//                }
//            }
//        }
//
//        if (b == null) {
//            for (long i = 0; i < 2 * N; i++) {
//                Double npv = getNPV(cashflowDtoList, i / (double) N - 1);
//                if ( npv != null && npv <= 0) {
//                    b = i / (double) N - 1;
//                    break;
//                }
//            }
//        }

        if (a != null && b != null) {
            while (Math.abs(b - a) > 0.0000000000000001) {
                Double npv = getNPV(cashflowDtoList, (a + b) / 2);
                if (npv != null && npv >= 0) {
                    a = (a + b) / 2;
                } else if (npv != null && npv <= 0) {
                    b = (a + b) / 2;
                } else {
                    return null;
                }
            }

            return (Math.pow(1 + a, 365.0) - 1) * 100;
//            return Math.round((Math.pow(1 + a, 365.0) - 1) * 10000) / 100.0;
        }

        return null;
    }
}
