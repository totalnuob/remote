package kz.nicnbk.service.api.reporting.hedgefunds;

import kz.nicnbk.service.dto.common.EntitySaveResponseDto;
import kz.nicnbk.service.dto.lookup.PortfolioVarPagedSearchResult;
import kz.nicnbk.service.dto.lookup.PortfolioVarSearchParams;
import kz.nicnbk.service.dto.risk.PortfolioVarValueDto;

import java.util.Date;
import java.util.List;

public interface HFPortfolioRiskService {
    List<PortfolioVarValueDto> getValuesFromDateAsList(Date fromDate, String benchmarkCode);

    PortfolioVarPagedSearchResult search(PortfolioVarSearchParams searchParams);

    EntitySaveResponseDto save(PortfolioVarValueDto dto, String username);

    PortfolioVarValueDto getPortfolioVarEndOfMonthForDate(Date date, String portfolioVarCode);
}
