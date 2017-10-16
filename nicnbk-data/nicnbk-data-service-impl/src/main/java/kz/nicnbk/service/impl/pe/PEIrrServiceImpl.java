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

            for (PEGrossCashflowDto cashflowDto : cashflowDtoList) {
                if (cashflowDto.getDate() == null) {
                    return null;
                }
                if (cashflowDto.getGrossCF() != null) {
                    doubleSum += cashflowDto.getGrossCF() / Math.pow(1 + dailyRate, (cashflowDto.getDate().getTime() - initialDate.getTime()) / 86400000);
                }
            }

//            return doubleSum;

            BigDecimal bigDecimalSum = new BigDecimal(0).setScale(1000, BigDecimal.ROUND_HALF_UP);

            try {
                for (PEGrossCashflowDto cashflowDto : cashflowDtoList) {
                    if (cashflowDto.getGrossCF() != null) {
                        BigDecimal bigDecimalCF = new BigDecimal(cashflowDto.getGrossCF()).setScale(2000, BigDecimal.ROUND_HALF_UP);
                        BigDecimal power = new BigDecimal(1 + dailyRate).pow((int) ((cashflowDto.getDate().getTime() - initialDate.getTime()) / 86400000));
                        BigDecimal c = bigDecimalCF.divide(power, 1000, BigDecimal.ROUND_HALF_UP);
                        bigDecimalSum = bigDecimalSum.add(c);
                    }
                }
            } catch (Exception ex) {
                System.out.println("Return null!");
                return null;
            }

            return bigDecimalSum.doubleValue();
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
        for (int i = 0; i < 2; i++) {
            Double npv = getNPV(cashflowDtoList, - i / (double) (1 + i));
            if ( npv != null && npv >= 0) {
                a = - i / (double) (1 + i);
                break;
            }
        }

        for (int i = 0; i < 2; i++) {
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
