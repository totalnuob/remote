package kz.nicnbk.service.impl.monitoring;

import kz.nicnbk.common.service.util.DateUtils;
import kz.nicnbk.common.service.util.ExcelUtils;
import kz.nicnbk.common.service.util.HashUtils;
import kz.nicnbk.common.service.util.MathUtils;
import kz.nicnbk.repo.api.employee.EmployeeRepository;
import kz.nicnbk.repo.api.files.FilesRepository;
import kz.nicnbk.repo.api.lookup.FilesTypeRepository;
import kz.nicnbk.repo.api.monitoring.LiquidPortfolioRepository;
import kz.nicnbk.repo.model.employee.Employee;
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
import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by Pak on 20.06.2019.
 */

@Service
public class LiquidPortfolioServiceImpl implements LiquidPortfolioService {

    private static final Logger logger = LoggerFactory.getLogger(LiquidPortfolioServiceImpl.class);

    /* Root folder on the server */
    @Value("${filestorage.root.directory}")
    private String rootDirectory;

    @Autowired
    private LiquidPortfolioRepository repository;

    @Autowired
    private LiquidPortfolioEntityConverter converter;

    @Autowired
    private EmployeeRepository employeeRepository;

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

            if(liquidPortfolioDtoList != null && !liquidPortfolioDtoList.isEmpty()) {
                for (int i = 0; i < liquidPortfolioDtoList.size() - 1; i++) {
                    if (DateUtils.getMonth(liquidPortfolioDtoList.get(i).getDate()) != DateUtils.getMonth(liquidPortfolioDtoList.get(i + 1).getDate())) {
                        shortList.add(this.calculateMtdQtdYtd(liquidPortfolioDtoList.get(i).getDate(), liquidPortfolioList));
                    }
                }

                shortList.add(this.calculateMtdQtdYtd(liquidPortfolioDtoList.get(liquidPortfolioDtoList.size() - 1).getDate(), liquidPortfolioList));
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
            Employee employee = this.employeeRepository.findByUsername(updater);

            if (employee == null) {
                logger.error("Failed to update Liquid Portfolio data: the user is not found in the database!");
                return new LiquidPortfolioResultDto(null, ResponseStatusType.FAIL, "", "Failed to update Liquid Portfolio data: the user is not found in the database!", "");
            }

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

            // If no sheets found
            if (sheetFixed == null && sheetEquity == null && sheetTransition == null) {
                logger.error("Failed to update Liquid Portfolio data: the file doesn't contain either of the sheets 'Fixed', 'Equity', 'Transition'!");
                return new LiquidPortfolioResultDto(null, ResponseStatusType.FAIL, "", "Failed to update Liquid Portfolio data: the file doesn't contain either of the sheets 'Fixed', 'Equity', 'Transition'!", "");
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

                    if (ExcelUtils.isEmptyCellRange(row, 1, 16383)) {
                        continue;
                    }

                    try {
                        if (row.getCell(0).getDateCellValue().after(new Date())) {
                            break;
                        }

                        portfolioList = this.updateFixed(portfolioList, row, employee, fileId);

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

                    if (ExcelUtils.isEmptyCellRange(row, 1, 16383)) {
                        continue;
                    }

                    try {
                        if (row.getCell(0).getDateCellValue().after(new Date())) {
                            break;
                        }

                        portfolioList = this.updateEquity(portfolioList, row, employee, fileId);

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

                    if (ExcelUtils.isEmptyCellRange(row, 1, 16383)) {
                        continue;
                    }

                    try {
                        if (row.getCell(0).getDateCellValue().after(new Date())) {
                            break;
                        }

                        portfolioList = this.updateTransition(portfolioList, row, employee, fileId);

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

            logger.info("Liquid Portfolio data has been updated successfully from sheet(s)" + ((sheetFixed != null) ? " 'Fixed'," : "") + ((sheetEquity != null) ? " 'Equity'," : "") + ((sheetTransition != null) ? " 'Transition'," : "") + " updater: " + updater);
            LiquidPortfolioResultDto resultDto = this.get();
            return new LiquidPortfolioResultDto(resultDto.getLiquidPortfolioDtoList(), ResponseStatusType.SUCCESS, "", "Liquid Portfolio data has been updated successfully from sheet(s)" + ((sheetFixed != null) ? " 'Fixed'," : "") + ((sheetEquity != null) ? " 'Equity'," : "") + ((sheetTransition != null) ? " 'Transition'," : "") + "!", "");
        } catch (Exception ex) {
            logger.error("Failed to update Liquid Portfolio data, ", ex);
            return new LiquidPortfolioResultDto(null, ResponseStatusType.FAIL, "", "Failed to update Liquid Portfolio data!", "");
        }
    }

    @Override
    public FilesDto getFileWithInputStream() {
        if(this.repository.findAllByOrderByDateAsc().size() > 0) {
            try {
                FilesType dummyFilesType = new FilesType();
                dummyFilesType.setId(filesTypeRepository.findByCode(FileTypeLookup.MONITORING_LIQUID_PORTFOLIO.getCode()).getId());

                List<Files> filesList = filesRepository.findAllByType(dummyFilesType);

                if (filesList == null || filesList.size() == 0) {
                    return null;
                } else if (filesList.size() == 1) {
                    Long fileId = filesList.get(0).getId();
                    FilesDto filesDto = this.filesEntityConverter.disassemble(this.filesRepository.findOne(fileId));
                    String path = filePathResolver.resolveDirectory(fileId, FileTypeLookup.MONITORING_LIQUID_PORTFOLIO.getCatalog());
                    String fileName = HashUtils.hashMD5String(fileId.toString());
                    InputStream inputStream = new FileInputStream(path+fileName);
                    filesDto.setInputStream(inputStream);
                    return filesDto;
                } else {
                    FilesDto zipFile = new FilesDto();
                    String zipFileName = this.rootDirectory + "/tmp/" + HashUtils.hashMD5String(new Date().getTime() + "") + ".zip";
                    ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFileName));

                    for (int i = 0; i < filesList.size(); i++) {
                        Long fileId = filesList.get(i).getId();
                        FilesDto excelFile = this.filesEntityConverter.disassemble(this.filesRepository.findOne(fileId));

                        String path = filePathResolver.resolveDirectory(fileId, FileTypeLookup.MONITORING_LIQUID_PORTFOLIO.getCatalog());
                        String name = HashUtils.hashMD5String(fileId.toString());
                        InputStream inputStream = new FileInputStream(path+name);
                        excelFile.setInputStream(inputStream);

                        setExportZipContent(i + "_" + excelFile.getFileName(), out, excelFile);
                    }

                    out.close();

                    zipFile.setInputStream(new FileInputStream(zipFileName));
                    //zipFile.setFileName(zipFileName);
                    zipFile.setFileName("LIQ_PORTFOLIO");
                    zipFile.setMimeType("application/zip");

                    return zipFile;
                }
            } catch (Exception ex) {
            }
        }
        return null;
    }

    private void setExportZipContent(String fileName, ZipOutputStream out, FilesDto filesDto){
        try {
            ZipEntry e = new ZipEntry(fileName);
            out.putNextEntry(e);
            byte[] data = IOUtils.toByteArray(filesDto.getInputStream());
            out.write(data, 0, data.length);
            out.closeEntry();
            filesDto.getInputStream().close();
            new File(filesDto.getFileName()).delete();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private List<LiquidPortfolio> updateFixed(List<LiquidPortfolio> portfolioList, Row row, Employee updater, Long fileId) {
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

    private List<LiquidPortfolio> updateEquity(List<LiquidPortfolio> portfolioList, Row row, Employee updater, Long fileId) {
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

    private List<LiquidPortfolio> updateTransition(List<LiquidPortfolio> portfolioList, Row row, Employee updater, Long fileId) {
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

    private LiquidPortfolioDto calculateMtdQtdYtd(Date date, List<LiquidPortfolio> liquidPortfolioList) {
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

        for (int i = 0; i < liquidPortfolioList.size(); i++) {
            if (liquidPortfolioList.get(i).getDate().getTime() == date.getTime()) {
                portfolioDto = this.converter.disassemble(liquidPortfolioList.get(i));
            }

            if (i == 0) {
                continue;
            }

            if (liquidPortfolioList.get(i).getDate().after(date)) {
                break;
            }

            if (DateUtils.getYear(liquidPortfolioList.get(i).getDate()) == DateUtils.getYear(date)) {
                Double _returnsTotalFixed = this.interest(liquidPortfolioList.get(i).getTotalFixed(), liquidPortfolioList.get(i-1).getTotalFixed(), liquidPortfolioList.get(i).getTotalFixedFlow());
                Double _returnsGovernmentsFixed = this.interest(liquidPortfolioList.get(i).getGovernmentsFixed(), liquidPortfolioList.get(i-1).getGovernmentsFixed(), liquidPortfolioList.get(i).getGovernmentsFixedFlow());
                Double _returnsCorporates = this.interest(liquidPortfolioList.get(i).getCorporates(), liquidPortfolioList.get(i-1).getCorporates(), liquidPortfolioList.get(i).getCorporatesFlow());
                Double _returnsAgencies = this.interest(liquidPortfolioList.get(i).getAgencies(), liquidPortfolioList.get(i-1).getAgencies(), liquidPortfolioList.get(i).getAgenciesFlow());
                Double _returnsSupranationals = this.interest(liquidPortfolioList.get(i).getSupranationals(), liquidPortfolioList.get(i-1).getSupranationals(), liquidPortfolioList.get(i).getSupranationalsFlow());
                Double _returnsCashBrokerAndFutures = this.interest(liquidPortfolioList.get(i).getCashBrokerAndFutures(), liquidPortfolioList.get(i-1).getCashBrokerAndFutures(), null);
                Double _returnsTotalEquity = this.interest(liquidPortfolioList.get(i).getTotalEquity(), liquidPortfolioList.get(i-1).getTotalEquity(), liquidPortfolioList.get(i).getTotalEquityFlow());
                Double _returnsEtf = this.interest(liquidPortfolioList.get(i).getEtf(), liquidPortfolioList.get(i-1).getEtf(), liquidPortfolioList.get(i).getEtfFlow());
                Double _returnsTotalTransition = this.interest(liquidPortfolioList.get(i).getTotalTransition(), liquidPortfolioList.get(i-1).getTotalTransition(), liquidPortfolioList.get(i).getTotalTransitionFlow());
                Double _returnsGovernmentsTransition = this.interest(liquidPortfolioList.get(i).getGovernmentsTransition(), liquidPortfolioList.get(i-1).getGovernmentsTransition(), liquidPortfolioList.get(i).getGovernmentsTransitionFlow());

                returnsTotalFixedYtd.add(_returnsTotalFixed);
                returnsGovernmentsFixedYtd.add(_returnsGovernmentsFixed);
                returnsCorporatesYtd.add(_returnsCorporates);
                returnsAgenciesYtd.add(_returnsAgencies);
                returnsSupranationalsYtd.add(_returnsSupranationals);
                returnsCashBrokerAndFuturesYtd.add(_returnsCashBrokerAndFutures);
                returnsTotalEquityYtd.add(_returnsTotalEquity);
                returnsEtfYtd.add(_returnsEtf);
                returnsTotalTransitionYtd.add(_returnsTotalTransition);
                returnsGovernmentsTransitionYtd.add(_returnsGovernmentsTransition);

                if (DateUtils.getMonth(liquidPortfolioList.get(i).getDate()) / 3 == DateUtils.getMonth(date) / 3) {
                    returnsTotalFixedQtd.add(_returnsTotalFixed);
                    returnsGovernmentsFixedQtd.add(_returnsGovernmentsFixed);
                    returnsCorporatesQtd.add(_returnsCorporates);
                    returnsAgenciesQtd.add(_returnsAgencies);
                    returnsSupranationalsQtd.add(_returnsSupranationals);
                    returnsCashBrokerAndFuturesQtd.add(_returnsCashBrokerAndFutures);
                    returnsTotalEquityQtd.add(_returnsTotalEquity);
                    returnsEtfQtd.add(_returnsEtf);
                    returnsTotalTransitionQtd.add(_returnsTotalTransition);
                    returnsGovernmentsTransitionQtd.add(_returnsGovernmentsTransition);

                    if (DateUtils.getMonth(liquidPortfolioList.get(i).getDate()) == DateUtils.getMonth(date)) {
                        returnsTotalFixedMtd.add(_returnsTotalFixed);
                        returnsGovernmentsFixedMtd.add(_returnsGovernmentsFixed);
                        returnsCorporatesMtd.add(_returnsCorporates);
                        returnsAgenciesMtd.add(_returnsAgencies);
                        returnsSupranationalsMtd.add(_returnsSupranationals);
                        returnsCashBrokerAndFuturesMtd.add(_returnsCashBrokerAndFutures);
                        returnsTotalEquityMtd.add(_returnsTotalEquity);
                        returnsEtfMtd.add(_returnsEtf);
                        returnsTotalTransitionMtd.add(_returnsTotalTransition);
                        returnsGovernmentsTransitionMtd.add(_returnsGovernmentsTransition);
                    }
                }
            }
        }

        if (portfolioDto == null) {
            return null;
        }

        portfolioDto.setTotalFixedMtd((this.product(returnsTotalFixedMtd) == null) ? null : (this.product(returnsTotalFixedMtd) - 1.0));
        portfolioDto.setTotalFixedQtd((this.product(returnsTotalFixedQtd) == null) ? null : (this.product(returnsTotalFixedQtd) - 1.0));
        portfolioDto.setTotalFixedYtd((this.product(returnsTotalFixedYtd) == null) ? null : (this.product(returnsTotalFixedYtd) - 1.0));

        portfolioDto.setGovernmentsFixedMtd((this.product(returnsGovernmentsFixedMtd) == null) ? null : (this.product(returnsGovernmentsFixedMtd) - 1.0));
        portfolioDto.setGovernmentsFixedQtd((this.product(returnsGovernmentsFixedQtd) == null) ? null : (this.product(returnsGovernmentsFixedQtd) - 1.0));
        portfolioDto.setGovernmentsFixedYtd((this.product(returnsGovernmentsFixedYtd) == null) ? null : (this.product(returnsGovernmentsFixedYtd) - 1.0));

        portfolioDto.setCorporatesMtd((this.product(returnsCorporatesMtd) == null) ? null : (this.product(returnsCorporatesMtd) - 1.0));
        portfolioDto.setCorporatesQtd((this.product(returnsCorporatesQtd) == null) ? null : (this.product(returnsCorporatesQtd) - 1.0));
        portfolioDto.setCorporatesYtd((this.product(returnsCorporatesYtd) == null) ? null : (this.product(returnsCorporatesYtd) - 1.0));

        portfolioDto.setAgenciesMtd((this.product(returnsAgenciesMtd) == null) ? null : (this.product(returnsAgenciesMtd) - 1.0));
        portfolioDto.setAgenciesQtd((this.product(returnsAgenciesQtd) == null) ? null : (this.product(returnsAgenciesQtd) - 1.0));
        portfolioDto.setAgenciesYtd((this.product(returnsAgenciesYtd) == null) ? null : (this.product(returnsAgenciesYtd) - 1.0));

        portfolioDto.setSupranationalsMtd((this.product(returnsSupranationalsMtd) == null) ? null : (this.product(returnsSupranationalsMtd) - 1.0));
        portfolioDto.setSupranationalsQtd((this.product(returnsSupranationalsQtd) == null) ? null : (this.product(returnsSupranationalsQtd) - 1.0));
        portfolioDto.setSupranationalsYtd((this.product(returnsSupranationalsYtd) == null) ? null : (this.product(returnsSupranationalsYtd) - 1.0));

        portfolioDto.setCashBrokerAndFuturesMtd((this.product(returnsCashBrokerAndFuturesMtd) == null) ? null : (this.product(returnsCashBrokerAndFuturesMtd) - 1.0));
        portfolioDto.setCashBrokerAndFuturesQtd((this.product(returnsCashBrokerAndFuturesQtd) == null) ? null : (this.product(returnsCashBrokerAndFuturesQtd) - 1.0));
        portfolioDto.setCashBrokerAndFuturesYtd((this.product(returnsCashBrokerAndFuturesYtd) == null) ? null : (this.product(returnsCashBrokerAndFuturesYtd) - 1.0));

        portfolioDto.setTotalEquityMtd((this.product(returnsTotalEquityMtd) == null) ? null : (this.product(returnsTotalEquityMtd) - 1.0));
        portfolioDto.setTotalEquityQtd((this.product(returnsTotalEquityQtd) == null) ? null : (this.product(returnsTotalEquityQtd) - 1.0));
        portfolioDto.setTotalEquityYtd((this.product(returnsTotalEquityYtd) == null) ? null : (this.product(returnsTotalEquityYtd) - 1.0));

        portfolioDto.setEtfMtd((this.product(returnsEtfMtd) == null) ? null : (this.product(returnsEtfMtd) - 1.0));
        portfolioDto.setEtfQtd((this.product(returnsEtfQtd) == null) ? null : (this.product(returnsEtfQtd) - 1.0));
        portfolioDto.setEtfYtd((this.product(returnsEtfYtd) == null) ? null : (this.product(returnsEtfYtd) - 1.0));

        portfolioDto.setTotalTransitionMtd((this.product(returnsTotalTransitionMtd) == null) ? null : (this.product(returnsTotalTransitionMtd) - 1.0));
        portfolioDto.setTotalTransitionQtd((this.product(returnsTotalTransitionQtd) == null) ? null : (this.product(returnsTotalTransitionQtd) - 1.0));
        portfolioDto.setTotalTransitionYtd((this.product(returnsTotalTransitionYtd) == null) ? null : (this.product(returnsTotalTransitionYtd) - 1.0));

        portfolioDto.setGovernmentsTransitionMtd((this.product(returnsGovernmentsTransitionMtd) == null) ? null : (this.product(returnsGovernmentsTransitionMtd) - 1.0));
        portfolioDto.setGovernmentsTransitionQtd((this.product(returnsGovernmentsTransitionQtd) == null) ? null : (this.product(returnsGovernmentsTransitionQtd) - 1.0));
        portfolioDto.setGovernmentsTransitionYtd((this.product(returnsGovernmentsTransitionYtd) == null) ? null : (this.product(returnsGovernmentsTransitionYtd) - 1.0));

        return portfolioDto;
    }

    private Double interest(Double valueCurrent, Double valuePrevious, Double valueCurrentFlow) {
        if (valueCurrent == null || valuePrevious == null || valuePrevious == 0.0) {
            return null;
        } else {
//            return MathUtils.divide(MathUtils.subtract(valueCurrent, (valueCurrentFlow == null) ? 0.0 : valueCurrentFlow), valuePrevious);
            return (valueCurrent - ((valueCurrentFlow == null) ? 0.0 : valueCurrentFlow)) / valuePrevious;
        }
    }

    private Double product(List<Double> list) {
        Double prod = 1.0;
        if (list == null) {
            return null;
        }
        for (Double element : list) {
            if (element == null) {
                return null;
            }
//            prod = MathUtils.multiply(prod, element);
            prod *= element;
        }
        return prod;
    }
}
