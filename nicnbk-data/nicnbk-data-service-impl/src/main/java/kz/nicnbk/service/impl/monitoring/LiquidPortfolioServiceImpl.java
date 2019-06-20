package kz.nicnbk.service.impl.monitoring;

import kz.nicnbk.repo.api.files.FilesRepository;
import kz.nicnbk.repo.api.monitoring.LiquidPortfolioRepository;
import kz.nicnbk.repo.model.monitoring.LiquidPortfolio;
import kz.nicnbk.service.api.files.FileService;
import kz.nicnbk.service.api.monitoring.LiquidPortfolioService;
import kz.nicnbk.service.converter.files.FilesEntityConverter;
import kz.nicnbk.service.converter.monitoring.LiquidPortfolioEntityConverter;
import kz.nicnbk.service.dto.common.ResponseStatusType;
import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.monitoring.LiquidPortfolioDto;
import kz.nicnbk.service.dto.monitoring.LiquidPortfolioResultDto;
import kz.nicnbk.service.impl.files.FilePathResolver;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;

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
    public LiquidPortfolioResultDto upload(Set<FilesDto> filesDtoSet, String updater) {

        try {
            FilesDto filesDto;
            Iterator<Row> rowIteratorFixed;
            Iterator<Row> rowIteratorEquity;
            Iterator<Row> rowIteratorTransition;
            int rowNumberFixed = 0;
            int rowNumberEquity = 0;
            int rowNumberTransition = 0;
            List<LiquidPortfolio> portfolioList = new ArrayList<>();

            try {
                filesDto = filesDtoSet.iterator().next();
                InputStream inputFile = new ByteArrayInputStream(filesDto.getBytes());
                XSSFWorkbook workbook = new XSSFWorkbook(inputFile);
                XSSFSheet sheetFixed = workbook.getSheet("Fixed");
                rowIteratorFixed = sheetFixed.iterator();
                XSSFSheet sheetEquity = workbook.getSheet("Equity");
                rowIteratorEquity = sheetEquity.iterator();
                XSSFSheet sheetTransition = workbook.getSheet("Transition");
                rowIteratorTransition = sheetTransition.iterator();
            } catch (Exception ex) {
                logger.error("Failed to update Liquid Portfolio data: the file or one of the sheets 'Fixed', 'Equity', 'Transition' cannot be opened, ", ex);
                return new LiquidPortfolioResultDto(null, ResponseStatusType.FAIL, "", "Failed to update Liquid Portfolio data: the file or one of the sheets 'Fixed', 'Equity', 'Transition' cannot be opened!", "");
            }

            for (int i = 0; i < 6; i++) {
                if(rowIteratorFixed.hasNext()) {
                    rowIteratorFixed.next();
                    rowNumberFixed++;
                } else {
                    logger.error("Failed to update Liquid Portfolio data: the sheet 'Fixed' contains less than 6 rows!");
                    return new LiquidPortfolioResultDto(null, ResponseStatusType.FAIL, "", "Failed to update Liquid Portfolio data: the sheet 'Fixed' contains less than 6 rows!", "");
                }
            }

            for (int i = 0; i < 4; i++) {
                if(rowIteratorEquity.hasNext()) {
                    rowIteratorEquity.next();
                    rowNumberEquity++;
                } else {
                    logger.error("Failed to update Liquid Portfolio data: the sheet 'Equity' contains less than 4 rows!");
                    return new LiquidPortfolioResultDto(null, ResponseStatusType.FAIL, "", "Failed to update Liquid Portfolio data: the sheet 'Equity' contains less than 4 rows!", "");
                }
            }

            for (int i = 0; i < 6; i++) {
                if(rowIteratorTransition.hasNext()) {
                    rowIteratorTransition.next();
                    rowNumberTransition++;
                } else {
                    logger.error("Failed to update Liquid Portfolio data: the sheet 'Transition' contains less than 6 rows!");
                    return new LiquidPortfolioResultDto(null, ResponseStatusType.FAIL, "", "Failed to update Liquid Portfolio data: the sheet 'Transition' contains less than 6 rows!", "");
                }
            }

            while (rowIteratorFixed.hasNext()) {
                Row row = rowIteratorFixed.next();
                rowNumberFixed++;

                if (row.getCell(0) == null) {
                    break;
                }

                try {
                    if (row.getCell(0).getDateCellValue().after(new Date())) {
                        break;
                    }

                } catch (Exception ex) {
                    logger.error("Failed to update Liquid Portfolio data: error parsing row #" + rowNumberFixed + " in sheet 'Fixed', ", ex);
                    return new LiquidPortfolioResultDto(null, ResponseStatusType.FAIL, "", "Failed to update Liquid Portfolio data: error parsing row #" + rowNumberFixed + " in sheet 'Fixed'!", "");
                }
            }

            while (rowIteratorEquity.hasNext()) {
                Row row = rowIteratorEquity.next();
                rowNumberEquity++;

                if (row.getCell(0) == null) {
                    break;
                }

                try {
                    if (row.getCell(0).getDateCellValue().after(new Date())) {
                        break;
                    }

                } catch (Exception ex) {
                    logger.error("Failed to update Liquid Portfolio data: error parsing row #" + rowNumberEquity + " in sheet 'Equity', ", ex);
                    return new LiquidPortfolioResultDto(null, ResponseStatusType.FAIL, "", "Failed to update Liquid Portfolio data: error parsing row #" + rowNumberEquity + " in sheet 'Equity'!", "");
                }
            }

            while (rowIteratorTransition.hasNext()) {
                Row row = rowIteratorTransition.next();
                rowNumberTransition++;

                if (row.getCell(0) == null) {
                    break;
                }

                try {
                    if (row.getCell(0).getDateCellValue().after(new Date())) {
                        break;
                    }

                } catch (Exception ex) {
                    logger.error("Failed to update Liquid Portfolio data: error parsing row #" + rowNumberTransition + " in sheet 'Transition', ", ex);
                    return new LiquidPortfolioResultDto(null, ResponseStatusType.FAIL, "", "Failed to update Liquid Portfolio data: error parsing row #" + rowNumberTransition + " in sheet 'Transition'!", "");
                }
            }

            throw new Exception();
        } catch (Exception ex) {
            logger.error("Failed to update Liquid Portfolio data, ", ex);
            return new LiquidPortfolioResultDto(null, ResponseStatusType.FAIL, "", "Failed to update Liquid Portfolio data!", "");
        }
    }
}
