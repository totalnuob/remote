package kz.nicnbk.service.impl.pe;

import kz.nicnbk.service.api.pe.PEIrrService;
import kz.nicnbk.service.dto.pe.PEGrossCashflowDto;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Pak on 13.10.2017.
 */
@Service
public class PEIrrServiceImpl implements PEIrrService {

    @Override
    public Double getNPV(List<PEGrossCashflowDto> cashflowDtoList, double rate) {

        if (rate == -1) {
            return null;
        }






    }

    @Override
    public Double getIRR(List<PEGrossCashflowDto> cashflowDtoList) {
        return 1.0;
    }
}
