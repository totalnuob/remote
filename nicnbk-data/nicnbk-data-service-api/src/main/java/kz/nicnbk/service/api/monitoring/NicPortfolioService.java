package kz.nicnbk.service.api.monitoring;

import kz.nicnbk.service.dto.monitoring.NicPortfolioDto;

import java.util.List;

/**
 * Created by Pak on 13.06.2019.
 */

public interface NicPortfolioService {

    List<NicPortfolioDto> get();
}
