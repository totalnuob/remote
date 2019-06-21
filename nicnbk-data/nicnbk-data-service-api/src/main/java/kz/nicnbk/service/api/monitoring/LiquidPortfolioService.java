package kz.nicnbk.service.api.monitoring;

import kz.nicnbk.repo.model.monitoring.LiquidPortfolio;
import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.monitoring.LiquidPortfolioResultDto;
import org.apache.poi.ss.usermodel.Row;

import java.util.List;
import java.util.Set;

/**
 * Created by Pak on 20.06.2019.
 */

public interface LiquidPortfolioService {

    LiquidPortfolioResultDto get();

    LiquidPortfolioResultDto upload(Set<FilesDto> filesDtoSet, String username);

    List<LiquidPortfolio> updateFixed(List<LiquidPortfolio> portfolioList, Row row, String username, Long fileId);

    List<LiquidPortfolio> updateEquity(List<LiquidPortfolio> portfolioList, Row row, String username, Long fileId);

    List<LiquidPortfolio> updateTransition(List<LiquidPortfolio> portfolioList, Row row, String username, Long fileId);

    FilesDto getFileWithInputStream();
}
