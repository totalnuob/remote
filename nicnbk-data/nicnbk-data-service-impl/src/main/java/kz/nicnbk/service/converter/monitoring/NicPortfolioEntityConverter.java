package kz.nicnbk.service.converter.monitoring;

import kz.nicnbk.repo.model.monitoring.NicPortfolio;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.dto.monitoring.NicPortfolioDto;
import org.springframework.stereotype.Component;

/**
 * Created by Pak on 13.06.2019.
 */

@Component
public class NicPortfolioEntityConverter extends BaseDozerEntityConverter<NicPortfolio, NicPortfolioDto> {
}
