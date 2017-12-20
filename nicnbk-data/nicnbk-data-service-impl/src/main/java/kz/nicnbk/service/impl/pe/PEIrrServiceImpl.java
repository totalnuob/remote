package kz.nicnbk.service.impl.pe;

import kz.nicnbk.service.api.pe.PEIrrService;
import kz.nicnbk.service.dto.pe.PECashflowDto;
import kz.nicnbk.service.dto.pe.PEGrossCashflowDto;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Pak on 13.10.2017.
 */
@Service
public class PEIrrServiceImpl implements PEIrrService {

    @Override
    public List<PECashflowDto> checkAndCleanCF(List<PEGrossCashflowDto> grossCashflowDtoList) {

        try {
            if (grossCashflowDtoList == null) {
                return null;
            }

            List<PECashflowDto> cashflowDtoList = new ArrayList<>();

            for (PEGrossCashflowDto grossCashflowDto : grossCashflowDtoList) {
                if (grossCashflowDto.getDate() == null || grossCashflowDto.getGrossCF() == null) {
                    return null;
                }
                if (grossCashflowDto.getGrossCF() != 0.0) {
                    cashflowDtoList.add(new PECashflowDto(grossCashflowDto.getDate(), grossCashflowDto.getGrossCF()));
                }
            }

            return cashflowDtoList;
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public Double getNPV(List<PECashflowDto> cashflowDtoList, double dailyRate) {

        try {
            if (dailyRate == -1) {
                return null;
            }

            Date initialDate = cashflowDtoList.get(0).getDate();

            Double doubleSum = 0.0;

            for (PECashflowDto cashflowDto : cashflowDtoList) {
                doubleSum += cashflowDto.getCashflow() / Math.pow(1 + dailyRate, (cashflowDto.getDate().getTime() - initialDate.getTime()) / 86400000);
            }

            if (!doubleSum.isNaN()) {
                return doubleSum;
            }

            //BigDecimal usage
            BigDecimal bigDecimalSum = new BigDecimal(0).setScale(1000, BigDecimal.ROUND_HALF_UP);

            for (PECashflowDto cashflowDto : cashflowDtoList) {
                BigDecimal bigDecimalCF = new BigDecimal(cashflowDto.getCashflow()).setScale(2000, BigDecimal.ROUND_HALF_UP);
                BigDecimal power = new BigDecimal(1 + dailyRate).pow((int) ((cashflowDto.getDate().getTime() - initialDate.getTime()) / 86400000));
                bigDecimalSum = bigDecimalSum.add(bigDecimalCF.divide(power, 1000, BigDecimal.ROUND_HALF_UP));
            }

            return bigDecimalSum.doubleValue();
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public Double getIRR(List<PEGrossCashflowDto> cashflowDtoList) {

        try {
            List<PECashflowDto> cashflowDtoListTrimmed = checkAndCleanCF(cashflowDtoList);

            if (cashflowDtoListTrimmed == null) {
                return null;
            }

            if (cashflowDtoListTrimmed.isEmpty()) {
                return 0.0;
            }

            Double a = null;
            Double b = null;
            Double npv;

            for (int i = 0; i < 30; i++) {
                npv = getNPV(cashflowDtoListTrimmed, i / 100.0);

                if (npv != null && npv == 0.0) {
                    return i / 100.0;
                } else if (npv != null && npv > 0.0 && a == null) {
                    a = i / 100.0;
                } else if (npv != null && npv < 0.0 && b == null) {
                    b = i / 100.0;
                }

                npv = getNPV(cashflowDtoListTrimmed, - i / 100.0);

                if (npv != null && npv == 0.0) {
                    return - i / 100.0;
                } else if (npv != null && npv > 0.0 && a == null) {
                    a = - i / 100.0;
                } else if (npv != null && npv < 0.0 && b == null) {
                    b = - i / 100.0;
                }

                if (a != null && b != null) {
                    break;
                }
            }

            System.out.println(cashflowDtoList.get(0).getCompanyName() + " " + a + " " + b);

            if (a != null && b != null) {
                while (Math.abs(b - a) > 0.0000000000000001) {
                    npv = getNPV(cashflowDtoListTrimmed, (a + b) / 2.0);

                    if (npv != null && npv == 0.0) {
                        return (a + b) / 2.0;
                    } else if (npv != null && npv > 0.0) {
                        a = (a + b) / 2.0;
                    } else if (npv != null && npv < 0.0) {
                        b = (a + b) / 2.0;
                    } else {
                        return null;
                    }
                }

                return (Math.pow(1 + a, 365.0) - 1) * 100;
            }

            return null;
        } catch (Exception ex) {
            return null;
        }
    }
}
