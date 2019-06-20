package kz.nicnbk.service.impl.monitoring;

import kz.nicnbk.common.service.util.DateUtils;
import kz.nicnbk.common.service.util.ExcelUtils;
import kz.nicnbk.repo.api.files.FilesRepository;
import kz.nicnbk.repo.api.lookup.FilesTypeRepository;
import kz.nicnbk.repo.api.monitoring.LiquidPortfolioRepository;
import kz.nicnbk.repo.model.files.Files;
import kz.nicnbk.repo.model.files.FilesType;
import kz.nicnbk.repo.model.lookup.FileTypeLookup;
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
    private FilesTypeRepository filesTypeRepository;

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
            List<LiquidPortfolioDto> shortList = new ArrayList<>();

            for (int i = 0; i < liquidPortfolioDtoList.size() - 1; i++) {
                if (DateUtils.getMonth(liquidPortfolioDtoList.get(i).getDate()) !=  DateUtils.getMonth(liquidPortfolioDtoList.get(i+1).getDate())) {
                    shortList.add(liquidPortfolioDtoList.get(i));
                }
            }

            return new LiquidPortfolioResultDto(shortList, ResponseStatusType.SUCCESS, "", "Liquid Portfolio data has been loaded successfully!", "");
        } catch (Exception ex) {
            logger.error("Error loading Liquid Portfolio data, ", ex);
        }
        return new LiquidPortfolioResultDto(null, ResponseStatusType.FAIL, "", "Failed to load Liquid Portfolio data!", "");
    }

    @Override
    public LiquidPortfolioResultDto upload(Set<FilesDto> filesDtoSet, String updater) {

        try {
            FilesDto filesDto;
            InputStream inputFile;
            XSSFWorkbook workbook;
            XSSFSheet sheetFixed;
            XSSFSheet sheetEquity;
            XSSFSheet sheetTransition;
            Iterator<Row> rowIteratorFixed;
            Iterator<Row> rowIteratorEquity;
            Iterator<Row> rowIteratorTransition;
            int rowNumberFixed = 0;
            int rowNumberEquity = 0;
            int rowNumberTransition = 0;
            List<LiquidPortfolio> portfolioList;
            Long fileId;

            // Read all the data that we have in the repository
            try {
                portfolioList = this.repository.findAllByOrderByDateAsc();
            } catch (Exception ex) {
                logger.error("Failed to update Liquid Portfolio data: repository problem, ", ex);
                return new LiquidPortfolioResultDto(null, ResponseStatusType.FAIL, "", "Failed to update Liquid Portfolio data: repository problem!", "");
            }

            // Try to open the file
            try {
                filesDto = filesDtoSet.iterator().next();
                inputFile = new ByteArrayInputStream(filesDto.getBytes());
                workbook = new XSSFWorkbook(inputFile);
                sheetFixed = workbook.getSheet("Fixed");
                sheetEquity = workbook.getSheet("Equity");
                sheetTransition = workbook.getSheet("Transition");

            } catch (Exception ex) {
                logger.error("Failed to update Liquid Portfolio data: the file cannot be opened, ", ex);
                return new LiquidPortfolioResultDto(null, ResponseStatusType.FAIL, "", "Failed to update Liquid Portfolio data: the file cannot be opened!", "");
            }

            // Save the file
            try {
                filesDto.setType(FileTypeLookup.MONITORING_LIQUID_PORTFOLIO.getCode());
                fileId = this.fileService.save(filesDto, FileTypeLookup.MONITORING_LIQUID_PORTFOLIO.getCatalog());
            } catch (Exception ex) {
                logger.error("Failed to update Liquid Portfolio data: repository problem, ", ex);
                return new LiquidPortfolioResultDto(null, ResponseStatusType.FAIL, "", "Failed to update Liquid Portfolio data: repository problem!", "");
            }

            // Open the three sheets one by one

            if (sheetFixed != null) {
                rowIteratorFixed = sheetFixed.iterator();

                // Check if the sheet has a header
                for (int i = 0; i < 6; i++) {
                    if(rowIteratorFixed.hasNext()) {
                        rowIteratorFixed.next();
                        rowNumberFixed++;
                    } else {
                        logger.error("Failed to update Liquid Portfolio data: the sheet 'Fixed' contains less than 6 rows!");
                        return new LiquidPortfolioResultDto(null, ResponseStatusType.FAIL, "", "Failed to update Liquid Portfolio data: the sheet 'Fixed' contains less than 6 rows!", "");
                    }
                }

                // Update our data with information from the sheet
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

                        portfolioList = this.updateFixed(portfolioList, row, updater, fileId);

                    } catch (Exception ex) {
                        logger.error("Failed to update Liquid Portfolio data: error parsing row #" + rowNumberFixed + " in sheet 'Fixed', ", ex);
                        return new LiquidPortfolioResultDto(null, ResponseStatusType.FAIL, "", "Failed to update Liquid Portfolio data: error parsing row #" + rowNumberFixed + " in sheet 'Fixed'!", "");
                    }
                }
            }

            if (sheetEquity != null) {
                rowIteratorEquity = sheetEquity.iterator();

                // Check if the sheet has a header
                for (int i = 0; i < 4; i++) {
                    if(rowIteratorEquity.hasNext()) {
                        rowIteratorEquity.next();
                        rowNumberEquity++;
                    } else {
                        logger.error("Failed to update Liquid Portfolio data: the sheet 'Equity' contains less than 4 rows!");
                        return new LiquidPortfolioResultDto(null, ResponseStatusType.FAIL, "", "Failed to update Liquid Portfolio data: the sheet 'Equity' contains less than 4 rows!", "");
                    }
                }

                // Update our data with information from the sheet
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

                        portfolioList = this.updateEquity(portfolioList, row, updater, fileId);

                    } catch (Exception ex) {
                        logger.error("Failed to update Liquid Portfolio data: error parsing row #" + rowNumberEquity + " in sheet 'Equity', ", ex);
                        return new LiquidPortfolioResultDto(null, ResponseStatusType.FAIL, "", "Failed to update Liquid Portfolio data: error parsing row #" + rowNumberEquity + " in sheet 'Equity'!", "");
                    }
                }
            }

            if (sheetTransition != null) {
                rowIteratorTransition = sheetTransition.iterator();

                // Check if the sheet has a header
                for (int i = 0; i < 6; i++) {
                    if(rowIteratorTransition.hasNext()) {
                        rowIteratorTransition.next();
                        rowNumberTransition++;
                    } else {
                        logger.error("Failed to update Liquid Portfolio data: the sheet 'Transition' contains less than 6 rows!");
                        return new LiquidPortfolioResultDto(null, ResponseStatusType.FAIL, "", "Failed to update Liquid Portfolio data: the sheet 'Transition' contains less than 6 rows!", "");
                    }
                }

                // Update our data with information from the sheet
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

                        portfolioList = this.updateTransition(portfolioList, row, updater, fileId);

                    } catch (Exception ex) {
                        logger.error("Failed to update Liquid Portfolio data: error parsing row #" + rowNumberTransition + " in sheet 'Transition', ", ex);
                        return new LiquidPortfolioResultDto(null, ResponseStatusType.FAIL, "", "Failed to update Liquid Portfolio data: error parsing row #" + rowNumberTransition + " in sheet 'Transition'!", "");
                    }
                }
            }

            // Save the data back to the repository, delete old files
            try {
                this.repository.save(portfolioList);

                FilesType dummyFilesType = new FilesType();
                dummyFilesType.setId(filesTypeRepository.findByCode(FileTypeLookup.MONITORING_LIQUID_PORTFOLIO.getCode()).getId());

                List<Files> filesList = filesRepository.findAllByType(dummyFilesType);

                for (Files files : filesList) {
                    boolean delete = true;
                    if (this.repository.findByFileFixed(files).size() > 0 ||
                            this.repository.findByFileEquity(files).size() > 0 ||
                            this.repository.findByFileTransition(files).size() > 0
                            ) {
                        delete = false;
                    }
                    if (delete) {
                        this.fileService.delete(files.getId());
                    }
                }
            } catch (Exception ex) {
                logger.error("Failed to update Liquid Portfolio data: repository problem, ", ex);
                return new LiquidPortfolioResultDto(null, ResponseStatusType.FAIL, "", "Failed to update Liquid Portfolio data: repository problem!", "");
            }

            logger.info("Liquid Portfolio data has been updated successfully from sheets 'Fixed', 'Equity', 'Transition', updater: " + updater);
            List<LiquidPortfolioDto> liquidPortfolioDtoList = this.converter.disassembleList(this.repository.findAllByOrderByDateAsc());
            return new LiquidPortfolioResultDto(liquidPortfolioDtoList, ResponseStatusType.SUCCESS, "", "Liquid Portfolio data has been updated successfully from sheets 'Fixed', 'Equity', 'Transition'!", "");
        } catch (Exception ex) {
            logger.error("Failed to update Liquid Portfolio data, ", ex);
            return new LiquidPortfolioResultDto(null, ResponseStatusType.FAIL, "", "Failed to update Liquid Portfolio data!", "");
        }
    }

    @Override
    public List<LiquidPortfolio> updateFixed(List<LiquidPortfolio> portfolioList, Row row, String updater, Long fileId) {
        Files dummyFile = new Files();
        dummyFile.setId(fileId);

        boolean alreadyExists = false;

        for (LiquidPortfolio portfolio : portfolioList) {
            if (portfolio.getDate().getTime() == row.getCell(0).getDateCellValue().getTime()) {

                portfolio.setUpdaterFixed(updater);
                portfolio.setFileFixed(dummyFile);

                portfolio.setTotalFixed(ExcelUtils.getDoubleValueFromCell(row.getCell(8)));
                portfolio.setTotalFixedFlow(ExcelUtils.getDoubleValueFromCell(row.getCell(7)));
                portfolio.setGovernmentsFixed(ExcelUtils.getDoubleValueFromCell(row.getCell(15)));
                portfolio.setGovernmentsFixedFlow(ExcelUtils.getDoubleValueFromCell(row.getCell(14)));
                portfolio.setCorporates(ExcelUtils.getDoubleValueFromCell(row.getCell(22)));
                portfolio.setCorporatesFlow(ExcelUtils.getDoubleValueFromCell(row.getCell(21)));
                portfolio.setAgencies(ExcelUtils.getDoubleValueFromCell(row.getCell(29)));
                portfolio.setAgenciesFlow(ExcelUtils.getDoubleValueFromCell(row.getCell(28)));
                portfolio.setSupranationals(ExcelUtils.getDoubleValueFromCell(row.getCell(36)));
                portfolio.setSupranationalsFlow(ExcelUtils.getDoubleValueFromCell(row.getCell(35)));
                portfolio.setCurrency(ExcelUtils.getDoubleValueFromCell(row.getCell(43)));
                portfolio.setOptions(ExcelUtils.getDoubleValueFromCell(row.getCell(107)));
                portfolio.setCashFixed(ExcelUtils.getDoubleValueFromCell(row.getCell(204)));
                portfolio.setCashBrokerAndFutures(ExcelUtils.getDoubleValueFromCell(row.getCell(196)));

                alreadyExists = true;
            }
        }

        if (!alreadyExists) {
            portfolioList.add(new LiquidPortfolio(
                    updater,
                    null,
                    null,
                    dummyFile,
                    null,
                    null,
                    row.getCell(0).getDateCellValue(),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(8)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(7)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(15)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(14)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(22)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(21)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(29)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(28)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(36)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(35)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(43)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(107)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(204)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(196)),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            ));
        }

        return portfolioList;
    }

    @Override
    public List<LiquidPortfolio> updateEquity(List<LiquidPortfolio> portfolioList, Row row, String updater, Long fileId) {
        Files dummyFile = new Files();
        dummyFile.setId(fileId);

        boolean alreadyExists = false;

        for (LiquidPortfolio portfolio : portfolioList) {
            if (portfolio.getDate().getTime() == row.getCell(0).getDateCellValue().getTime()) {

                portfolio.setUpdaterEquity(updater);
                portfolio.setFileEquity(dummyFile);

                portfolio.setTotalEquity(ExcelUtils.getDoubleValueFromCell(row.getCell(9)));
                portfolio.setTotalEquityFlow(ExcelUtils.getDoubleValueFromCell(row.getCell(8)));
                portfolio.setCashEquity(ExcelUtils.getDoubleValueFromCell(row.getCell(15)));
                portfolio.setEtf(ExcelUtils.getDoubleValueFromCell(row.getCell(30)));
                portfolio.setEtfFlow(ExcelUtils.getDoubleValueFromCell(row.getCell(29)));

                alreadyExists = true;
            }
        }

        if (!alreadyExists) {
            portfolioList.add(new LiquidPortfolio(
                    null,
                    updater,
                    null,
                    null,
                    dummyFile,
                    null,
                    row.getCell(0).getDateCellValue(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    ExcelUtils.getDoubleValueFromCell(row.getCell(9)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(8)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(15)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(30)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(29)),
                    null,
                    null,
                    null,
                    null,
                    null
            ));
        }

        return portfolioList;
    }

    @Override
    public List<LiquidPortfolio> updateTransition(List<LiquidPortfolio> portfolioList, Row row, String updater, Long fileId) {
        Files dummyFile = new Files();
        dummyFile.setId(fileId);

        boolean alreadyExists = false;

        for (LiquidPortfolio portfolio : portfolioList) {
            if (portfolio.getDate().getTime() == row.getCell(0).getDateCellValue().getTime()) {

                portfolio.setUpdaterTransition(updater);
                portfolio.setFileTransition(dummyFile);

                portfolio.setTotalTransition(ExcelUtils.getDoubleValueFromCell(row.getCell(8)));
                portfolio.setTotalTransitionFlow(ExcelUtils.getDoubleValueFromCell(row.getCell(7)));
                portfolio.setCashTransition(ExcelUtils.getDoubleValueFromCell(row.getCell(22)));
                portfolio.setGovernmentsTransition(ExcelUtils.getDoubleValueFromCell(row.getCell(15)));
                portfolio.setGovernmentsTransitionFlow(ExcelUtils.getDoubleValueFromCell(row.getCell(14)));

                alreadyExists = true;
            }
        }

        if (!alreadyExists) {
            portfolioList.add(new LiquidPortfolio(
                    null,
                    null,
                    updater,
                    null,
                    null,
                    dummyFile,
                    row.getCell(0).getDateCellValue(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    ExcelUtils.getDoubleValueFromCell(row.getCell(8)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(7)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(22)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(15)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(14))
            ));
        }

        return portfolioList;
    }
}
