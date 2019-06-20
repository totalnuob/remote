package kz.nicnbk.service.api.monitoring;

import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.monitoring.LiquidPortfolioResultDto;

import java.util.Set;

/**
 * Created by Pak on 20.06.2019.
 */

public interface LiquidPortfolioService {

    LiquidPortfolioResultDto get();

    LiquidPortfolioResultDto upload(Set<FilesDto> filesDtoSet, String username);
}
