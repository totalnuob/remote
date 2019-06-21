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
            List<LiquidPortfolio> liquidPortfolioList = this.repository.findAllByOrderByDateAsc();
            List<LiquidPortfolioDto> liquidPortfolioDtoList = this.converter.disassembleList(liquidPortfolioList);
            List<LiquidPortfolioDto> shortList = new ArrayList<>();

            for (int i = 0; i < liquidPortfolioDtoList.size() - 1; i++) {
                if (DateUtils.getMonth(liquidPortfolioDtoList.get(i).getDate()) !=  DateUtils.getMonth(liquidPortfolioDtoList.get(i+1).getDate())) {
                    shortList.add(this.calculateMtdQtdYtd(liquidPortfolioDtoList.get(i).getDate(), liquidPortfolioList));
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

    @Override
    public LiquidPortfolioDto calculateMtdQtdYtd(Date date, List<LiquidPortfolio> liquidPortfolioList) {
        List<Double> returnsTotalFixedMtd = new ArrayList<>();
        List<Double> returnsTotalFixedQtd = new ArrayList<>();
        List<Double> returnsTotalFixedYtd = new ArrayList<>();
        List<Double> returnsGovernmentsFixedMtd = new ArrayList<>();
        List<Double> returnsGovernmentsFixedQtd = new ArrayList<>();
        List<Double> returnsGovernmentsFixedYtd = new ArrayList<>();
        List<Double> returnsCorporatesMtd = new ArrayList<>();
        List<Double> returnsCorporatesQtd = new ArrayList<>();
        List<Double> returnsCorporatesYtd = new ArrayList<>();
        List<Double> returnsAgenciesMtd = new ArrayList<>();
        List<Double> returnsAgenciesQtd = new ArrayList<>();
        List<Double> returnsAgenciesYtd = new ArrayList<>();
        List<Double> returnsSupranationalsMtd = new ArrayList<>();
        List<Double> returnsSupranationalsQtd = new ArrayList<>();
        List<Double> returnsSupranationalsYtd = new ArrayList<>();
        List<Double> returnsCashBrokerAndFuturesMtd = new ArrayList<>();
        List<Double> returnsCashBrokerAndFuturesQtd = new ArrayList<>();
        List<Double> returnsCashBrokerAndFuturesYtd = new ArrayList<>();
        List<Double> returnsTotalEquityMtd = new ArrayList<>();
        List<Double> returnsTotalEquityQtd = new ArrayList<>();
        List<Double> returnsTotalEquityYtd = new ArrayList<>();
        List<Double> returnsEtfMtd = new ArrayList<>();
        List<Double> returnsEtfQtd = new ArrayList<>();
        List<Double> returnsEtfYtd = new ArrayList<>();
        List<Double> returnsTotalTransitionMtd = new ArrayList<>();
        List<Double> returnsTotalTransitionQtd = new ArrayList<>();
        List<Double> returnsTotalTransitionYtd = new ArrayList<>();
        List<Double> returnsGovernmentsTransitionMtd = new ArrayList<>();
        List<Double> returnsGovernmentsTransitionQtd = new ArrayList<>();
        List<Double> returnsGovernmentsTransitionYtd = new ArrayList<>();

        LiquidPortfolioDto portfolioDto = null;

        for (int i = 1; i < liquidPortfolioList.size(); i++) {
            if (liquidPortfolioList.get(i).getDate().getTime() == date.getTime()) {
                portfolioDto = this.converter.disassemble(liquidPortfolioList.get(i));
            }

            if (liquidPortfolioList.get(i).getDate().after(date)) {
                break;
            }

            if (DateUtils.getMonth(liquidPortfolioList.get(i).getDate()) == DateUtils.getMonth(date)) {
                returnsTotalFixedMtd.add(this.interest(liquidPortfolioList.get(i).getTotalFixed(), liquidPortfolioList.get(i-1).getTotalFixed(), liquidPortfolioList.get(i).getTotalFixedFlow()));
                returnsGovernmentsFixedMtd.add(this.interest(liquidPortfolioList.get(i).getGovernmentsFixed(), liquidPortfolioList.get(i-1).getGovernmentsFixed(), liquidPortfolioList.get(i).getGovernmentsFixedFlow()));
                returnsCorporatesMtd.add(this.interest(liquidPortfolioList.get(i).getCorporates(), liquidPortfolioList.get(i-1).getCorporates(), liquidPortfolioList.get(i).getCorporatesFlow()));
                returnsAgenciesMtd.add(this.interest(liquidPortfolioList.get(i).getAgencies(), liquidPortfolioList.get(i-1).getAgencies(), liquidPortfolioList.get(i).getAgenciesFlow()));
                returnsSupranationalsMtd.add(this.interest(liquidPortfolioList.get(i).getSupranationals(), liquidPortfolioList.get(i-1).getSupranationals(), liquidPortfolioList.get(i).getSupranationalsFlow()));
                returnsCashBrokerAndFuturesMtd.add(this.interest(liquidPortfolioList.get(i).getCashBrokerAndFutures(), liquidPortfolioList.get(i-1).getCashBrokerAndFutures(), null));
                returnsTotalEquityMtd.add(this.interest(liquidPortfolioList.get(i).getTotalEquity(), liquidPortfolioList.get(i-1).getTotalEquity(), liquidPortfolioList.get(i).getTotalEquityFlow()));
                returnsEtfMtd.add(this.interest(liquidPortfolioList.get(i).getEtf(), liquidPortfolioList.get(i-1).getEtf(), liquidPortfolioList.get(i).getEtfFlow()));
                returnsTotalTransitionMtd.add(this.interest(liquidPortfolioList.get(i).getTotalTransition(), liquidPortfolioList.get(i-1).getTotalTransition(), liquidPortfolioList.get(i).getTotalTransitionFlow()));
                returnsGovernmentsTransitionMtd.add(this.interest(liquidPortfolioList.get(i).getGovernmentsTransition(), liquidPortfolioList.get(i-1).getGovernmentsTransition(), liquidPortfolioList.get(i).getGovernmentsTransitionFlow()));
            }

            if (DateUtils.getMonth(liquidPortfolioList.get(i).getDate()) / 3 == DateUtils.getMonth(date) / 3) {
                returnsTotalFixedQtd.add(this.interest(liquidPortfolioList.get(i).getTotalFixed(), liquidPortfolioList.get(i-1).getTotalFixed(), liquidPortfolioList.get(i).getTotalFixedFlow()));
                returnsGovernmentsFixedQtd.add(this.interest(liquidPortfolioList.get(i).getGovernmentsFixed(), liquidPortfolioList.get(i-1).getGovernmentsFixed(), liquidPortfolioList.get(i).getGovernmentsFixedFlow()));
                returnsCorporatesQtd.add(this.interest(liquidPortfolioList.get(i).getCorporates(), liquidPortfolioList.get(i-1).getCorporates(), liquidPortfolioList.get(i).getCorporatesFlow()));
                returnsAgenciesQtd.add(this.interest(liquidPortfolioList.get(i).getAgencies(), liquidPortfolioList.get(i-1).getAgencies(), liquidPortfolioList.get(i).getAgenciesFlow()));
                returnsSupranationalsQtd.add(this.interest(liquidPortfolioList.get(i).getSupranationals(), liquidPortfolioList.get(i-1).getSupranationals(), liquidPortfolioList.get(i).getSupranationalsFlow()));
                returnsCashBrokerAndFuturesQtd.add(this.interest(liquidPortfolioList.get(i).getCashBrokerAndFutures(), liquidPortfolioList.get(i-1).getCashBrokerAndFutures(), null));
                returnsTotalEquityQtd.add(this.interest(liquidPortfolioList.get(i).getTotalEquity(), liquidPortfolioList.get(i-1).getTotalEquity(), liquidPortfolioList.get(i).getTotalEquityFlow()));
                returnsEtfQtd.add(this.interest(liquidPortfolioList.get(i).getEtf(), liquidPortfolioList.get(i-1).getEtf(), liquidPortfolioList.get(i).getEtfFlow()));
                returnsTotalTransitionQtd.add(this.interest(liquidPortfolioList.get(i).getTotalTransition(), liquidPortfolioList.get(i-1).getTotalTransition(), liquidPortfolioList.get(i).getTotalTransitionFlow()));
                returnsGovernmentsTransitionQtd.add(this.interest(liquidPortfolioList.get(i).getGovernmentsTransition(), liquidPortfolioList.get(i-1).getGovernmentsTransition(), liquidPortfolioList.get(i).getGovernmentsTransitionFlow()));
            }

            if (DateUtils.getYear(liquidPortfolioList.get(i).getDate()) == DateUtils.getYear(date)) {
                returnsTotalFixedYtd.add(this.interest(liquidPortfolioList.get(i).getTotalFixed(), liquidPortfolioList.get(i-1).getTotalFixed(), liquidPortfolioList.get(i).getTotalFixedFlow()));
                returnsGovernmentsFixedYtd.add(this.interest(liquidPortfolioList.get(i).getGovernmentsFixed(), liquidPortfolioList.get(i-1).getGovernmentsFixed(), liquidPortfolioList.get(i).getGovernmentsFixedFlow()));
                returnsCorporatesYtd.add(this.interest(liquidPortfolioList.get(i).getCorporates(), liquidPortfolioList.get(i-1).getCorporates(), liquidPortfolioList.get(i).getCorporatesFlow()));
                returnsAgenciesYtd.add(this.interest(liquidPortfolioList.get(i).getAgencies(), liquidPortfolioList.get(i-1).getAgencies(), liquidPortfolioList.get(i).getAgenciesFlow()));
                returnsSupranationalsYtd.add(this.interest(liquidPortfolioList.get(i).getSupranationals(), liquidPortfolioList.get(i-1).getSupranationals(), liquidPortfolioList.get(i).getSupranationalsFlow()));
                returnsCashBrokerAndFuturesYtd.add(this.interest(liquidPortfolioList.get(i).getCashBrokerAndFutures(), liquidPortfolioList.get(i-1).getCashBrokerAndFutures(), null));
                returnsTotalEquityYtd.add(this.interest(liquidPortfolioList.get(i).getTotalEquity(), liquidPortfolioList.get(i-1).getTotalEquity(), liquidPortfolioList.get(i).getTotalEquityFlow()));
                returnsEtfYtd.add(this.interest(liquidPortfolioList.get(i).getEtf(), liquidPortfolioList.get(i-1).getEtf(), liquidPortfolioList.get(i).getEtfFlow()));
                returnsTotalTransitionYtd.add(this.interest(liquidPortfolioList.get(i).getTotalTransition(), liquidPortfolioList.get(i-1).getTotalTransition(), liquidPortfolioList.get(i).getTotalTransitionFlow()));
                returnsGovernmentsTransitionYtd.add(this.interest(liquidPortfolioList.get(i).getGovernmentsTransition(), liquidPortfolioList.get(i-1).getGovernmentsTransition(), liquidPortfolioList.get(i).getGovernmentsTransitionFlow()));
            }

        }

        if (portfolioDto == null) {
            return null;
        }

        portfolioDto.setTotalFixedMtd(this.product(returnsTotalFixedMtd));
        portfolioDto.setTotalFixedQtd(this.product(returnsTotalFixedQtd));
        portfolioDto.setTotalFixedYtd(this.product(returnsTotalFixedYtd));

        portfolioDto.setGovernmentsFixedMtd(this.product(returnsGovernmentsFixedMtd));
        portfolioDto.setGovernmentsFixedQtd(this.product(returnsGovernmentsFixedQtd));
        portfolioDto.setGovernmentsFixedYtd(this.product(returnsGovernmentsFixedYtd));

        portfolioDto.setCorporatesMtd(this.product(returnsCorporatesMtd));
        portfolioDto.setCorporatesQtd(this.product(returnsCorporatesQtd));
        portfolioDto.setCorporatesYtd(this.product(returnsCorporatesYtd));

        portfolioDto.setAgenciesMtd(this.product(returnsAgenciesMtd));
        portfolioDto.setAgenciesQtd(this.product(returnsAgenciesQtd));
        portfolioDto.setAgenciesYtd(this.product(returnsAgenciesYtd));

        portfolioDto.setSupranationalsMtd(this.product(returnsSupranationalsMtd));
        portfolioDto.setSupranationalsQtd(this.product(returnsSupranationalsQtd));
        portfolioDto.setSupranationalsYtd(this.product(returnsSupranationalsYtd));

        portfolioDto.setCashBrokerAndFuturesMtd(this.product(returnsCashBrokerAndFuturesMtd));
        portfolioDto.setCashBrokerAndFuturesQtd(this.product(returnsCashBrokerAndFuturesQtd));
        portfolioDto.setCashBrokerAndFuturesYtd(this.product(returnsCashBrokerAndFuturesYtd));

        portfolioDto.setTotalEquityMtd(this.product(returnsTotalEquityMtd));
        portfolioDto.setTotalEquityQtd(this.product(returnsTotalEquityQtd));
        portfolioDto.setTotalEquityYtd(this.product(returnsTotalEquityYtd));

        portfolioDto.setEtfMtd(this.product(returnsEtfMtd));
        portfolioDto.setEtfQtd(this.product(returnsEtfQtd));
        portfolioDto.setEtfYtd(this.product(returnsEtfYtd));

        portfolioDto.setTotalTransitionMtd(this.product(returnsTotalTransitionMtd));
        portfolioDto.setTotalTransitionQtd(this.product(returnsTotalTransitionQtd));
        portfolioDto.setTotalTransitionYtd(this.product(returnsTotalTransitionYtd));

        portfolioDto.setGovernmentsTransitionMtd(this.product(returnsGovernmentsTransitionMtd));
        portfolioDto.setGovernmentsTransitionQtd(this.product(returnsGovernmentsTransitionQtd));
        portfolioDto.setGovernmentsTransitionYtd(this.product(returnsGovernmentsTransitionYtd));

        return portfolioDto;
    }

    @Override
    public Double interest(Double valueCurrent, Double valuePrevious, Double valueCurrentFlow) {
        if (valueCurrent == null || valuePrevious == null || valuePrevious == 0.0) {
            return null;
        } else {
            return (valueCurrent - ((valueCurrentFlow == null)?0.0:valueCurrentFlow)) / valuePrevious;
        }
    }

    @Override
    public Double product(List<Double> list) {
        double prod = 1.0;
        if (list == null) {
            return null;
        }
        for (Double element : list) {
            if (element == null) {
                return null;
            }
            prod *= element;
        }
        return prod;
    }
}
