package kz.nicnbk.service.impl.monitoring;

import kz.nicnbk.repo.api.files.FilesRepository;
import kz.nicnbk.repo.api.monitoring.LiquidPortfolioRepository;
import kz.nicnbk.service.api.files.FileService;
import kz.nicnbk.service.api.monitoring.LiquidPortfolioService;
import kz.nicnbk.service.converter.files.FilesEntityConverter;
import kz.nicnbk.service.converter.monitoring.LiquidPortfolioEntityConverter;
import kz.nicnbk.service.dto.common.ResponseStatusType;
import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.monitoring.LiquidPortfolioDto;
import kz.nicnbk.service.dto.monitoring.LiquidPortfolioResultDto;
import kz.nicnbk.service.impl.files.FilePathResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Created by Pak on 20.06.2019.
 */

@Service
public class LiquidPortfolioServiceImpl implements LiquidPortfolioService {

    private static final Logger logger = LoggerFactory.getLogger(LiquidPortfolioServiceImpl.class);

    @Autowired
    private LiquidPortfolioRepository repository;

    @Autowired
    private LiquidPortfolioEntityConverter converter;

    @Autowired
    private FileService fileService;

    @Autowired
    private FilesRepository filesRepository;

    @Autowired
    private FilesEntityConverter filesEntityConverter;

    @Autowired
    private FilePathResolver filePathResolver;

    @Override
    public LiquidPortfolioResultDto get() {
        try {
            List<LiquidPortfolioDto> liquidPortfolioDtoList = this.converter.disassembleList(this.repository.findAllByOrderByDateAsc());
            return new LiquidPortfolioResultDto(liquidPortfolioDtoList, ResponseStatusType.SUCCESS, "", "Liquid Portfolio data has been loaded successfully!", "");
        } catch (Exception ex) {
            logger.error("Error loading Liquid Portfolio data, ", ex);
        }
        return new LiquidPortfolioResultDto(null, ResponseStatusType.FAIL, "", "Failed to load Liquid Portfolio data!", "");
    }

    @Override
    public LiquidPortfolioResultDto upload(Set<FilesDto> filesDtoSet, String username) {

        try {
            throw new Exception();
        } catch (Exception ex) {
            logger.error("Failed to update Liquid Portfolio data, ", ex);
            return new LiquidPortfolioResultDto(null, ResponseStatusType.FAIL, "", "Failed to update Liquid Portfolio data!", "");
        }
    }
}
