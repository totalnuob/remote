package kz.nicnbk.service.converter.monitoring;

import kz.nicnbk.repo.model.monitoring.LiquidPortfolio;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.dto.monitoring.LiquidPortfolioDto;
import org.springframework.stereotype.Component;

/**
 * Created by Pak on 20.06.2019.
 */

@Component
public class LiquidPortfolioEntityConverter extends BaseDozerEntityConverter<LiquidPortfolio, LiquidPortfolioDto> {
}
