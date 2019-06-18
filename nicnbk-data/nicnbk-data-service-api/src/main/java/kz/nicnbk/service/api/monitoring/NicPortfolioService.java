package kz.nicnbk.service.api.monitoring;

import kz.nicnbk.repo.model.monitoring.NicPortfolio;
import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.monitoring.NicPortfolioResultDto;
import org.apache.poi.ss.usermodel.Row;

import java.util.Set;

/**
 * Created by Pak on 13.06.2019.
 */

public interface NicPortfolioService {

    NicPortfolioResultDto get();

    NicPortfolioResultDto upload(Set<FilesDto> filesDtoSet, String username);

    NicPortfolio create(Row row, String username);

    FilesDto getFile();
}
