package kz.nicnbk.service.impl.monitoring;

import kz.nicnbk.common.service.util.DateUtils;
import kz.nicnbk.common.service.util.ExcelUtils;
import kz.nicnbk.common.service.util.HashUtils;
import kz.nicnbk.repo.api.employee.EmployeeRepository;
import kz.nicnbk.repo.api.files.FilesRepository;
import kz.nicnbk.repo.api.lookup.FilesTypeRepository;
import kz.nicnbk.repo.api.monitoring.NicPortfolioRepository;
import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.repo.model.files.Files;
import kz.nicnbk.repo.model.files.FilesType;
import kz.nicnbk.repo.model.lookup.FileTypeLookup;
import kz.nicnbk.repo.model.monitoring.NicPortfolio;
import kz.nicnbk.service.api.files.FileService;
import kz.nicnbk.service.api.monitoring.NicPortfolioService;
import kz.nicnbk.service.converter.files.FilesEntityConverter;
import kz.nicnbk.service.converter.monitoring.NicPortfolioEntityConverter;
import kz.nicnbk.service.dto.common.ResponseStatusType;
import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.monitoring.NicPortfolioDto;
import kz.nicnbk.service.dto.monitoring.NicPortfolioResultDto;
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
 * Created by Pak on 13.06.2019.
 */

@Service
public class NicPortfolioServiceImpl implements NicPortfolioService {

    private static final Logger logger = LoggerFactory.getLogger(NicPortfolioServiceImpl.class);

    /* Root folder on the server */
    @Value("${filestorage.root.directory}")
    private String rootDirectory;

    @Autowired
    private NicPortfolioRepository repository;

    @Autowired
    private NicPortfolioEntityConverter converter;

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
    public NicPortfolioResultDto get() {
        try {
            List<NicPortfolioDto> nicPortfolioDtoList = this.converter.disassembleList(this.repository.findAllByOrderByDateAsc());
            return new NicPortfolioResultDto(nicPortfolioDtoList, ResponseStatusType.SUCCESS, "", "NIC Portfolio data has been loaded successfully!", "");
        } catch (Exception ex) {
            logger.error("Error loading NIC Portfolio data, ", ex);
        }
        return new NicPortfolioResultDto(null, ResponseStatusType.FAIL, "", "Failed to load NIC Portfolio data!", "");
    }



    @Override
    public NicPortfolioResultDto upload(Set<FilesDto> filesDtoSet, String updater) {

        try {
            Employee employee = this.employeeRepository.findByUsername(updater);

            if (employee == null) {
                logger.error("Failed to update NIC Portfolio data: the user is not found in the database!");
                return new NicPortfolioResultDto(null, ResponseStatusType.FAIL, "", "Failed to update NIC Portfolio data: the user is not found in the database!", "");
            }

            FilesDto filesDto;
            Iterator<Row> rowIterator;
            Row previousRow = null;
            Row currentRow;
            int previousMonth;
            int currentMonth;
            int rowNumber = 0;
            List<NicPortfolio> portfolioList = new ArrayList<>();

            try {
                filesDto = filesDtoSet.iterator().next();
                InputStream inputFile = new ByteArrayInputStream(filesDto.getBytes());
                XSSFWorkbook workbook = new XSSFWorkbook(inputFile);
                XSSFSheet sheet = workbook.getSheet("Database");
                rowIterator = sheet.iterator();
            } catch (Exception ex) {
                logger.error("Failed to update NIC Portfolio data: the file or the sheet 'Database' cannot be opened, ", ex);
                return new NicPortfolioResultDto(null, ResponseStatusType.FAIL, "", "Failed to update NIC Portfolio data: the file or the sheet 'Database' cannot be opened!", "");
            }

            for (int i = 0; i < 15; i++) {
                if(rowIterator.hasNext()) {
                    previousRow = rowIterator.next();
                    rowNumber++;
                } else {
                    logger.error("Failed to update NIC Portfolio data: the sheet 'Database' contains less than 15 rows!");
                    return new NicPortfolioResultDto(null, ResponseStatusType.FAIL, "", "Failed to update NIC Portfolio data: the sheet 'Database' contains less than 15 rows!", "");
                }
            }

            while (rowIterator.hasNext()) {
                currentRow = rowIterator.next();
                rowNumber++;

                if (previousRow.getCell(0) == null || currentRow.getCell(0) == null) {
                    //end of data
                    break;
                }

                try {
                    previousMonth = DateUtils.getMonth(previousRow.getCell(0).getDateCellValue());
                    currentMonth = DateUtils.getMonth(currentRow.getCell(0).getDateCellValue());

                    if (previousRow.getCell(0).getDateCellValue().after(new Date())) {
                        break;
                    }

                    if (previousMonth != currentMonth) {
                        portfolioList.add(this.create(previousRow, employee));
                    }
                } catch (Exception ex) {
                    logger.error("Failed to update NIC Portfolio data: error parsing row #" + rowNumber + ", ", ex);
                    return new NicPortfolioResultDto(null, ResponseStatusType.FAIL, "", "Failed to update NIC Portfolio data: error parsing row #" + rowNumber + "!", "");
                }

                previousRow = currentRow;
            }

            try {
                if(portfolioList.size() > 0) {
                    filesDto.setType(FileTypeLookup.MONITORING_NIC_PORTFOLIO.getCode());
                    Long fileId = this.fileService.save(filesDto, FileTypeLookup.MONITORING_NIC_PORTFOLIO.getCatalog());

                    Files file = new Files();
                    file.setId(fileId);
                    for (NicPortfolio portfolio : portfolioList) {
                        portfolio.setFile(file);
                    }

                    this.repository.save(portfolioList);

                    List<Long> portfoliosToDelete = new ArrayList<>();
                    List<Long> filesToDelete = new ArrayList<>();

                    for (NicPortfolio portfolio : this.repository.findAll()) {
                        if (portfolio.getFile() != null && ! portfolio.getFile().getId().equals(fileId)) {
                            portfoliosToDelete.add(portfolio.getId());
                            if ( ! filesToDelete.contains(portfolio.getFile().getId())) {
                                filesToDelete.add(portfolio.getFile().getId());
                            }
                        }
                    }

                    for (Long id : portfoliosToDelete) {
                        this.repository.delete(id);
                    }

                    for (Long id : filesToDelete) {
                        this.fileService.delete(id);
                    }
                }
            } catch (Exception ex) {
                logger.error("Failed to update NIC Portfolio data: repository problem, ", ex);
                return new NicPortfolioResultDto(null, ResponseStatusType.FAIL, "", "Failed to update NIC Portfolio data: repository problem!", "");
            }

            logger.info("NIC Portfolio data has been updated successfully, updater: " + updater);
            List<NicPortfolioDto> nicPortfolioDtoList = this.converter.disassembleList(this.repository.findAllByOrderByDateAsc());
            return new NicPortfolioResultDto(nicPortfolioDtoList, ResponseStatusType.SUCCESS, "", "NIC Portfolio data has been updated successfully!", "");
        } catch (Exception ex) {
            logger.error("Failed to update NIC Portfolio data, ", ex);
            return new NicPortfolioResultDto(null, ResponseStatusType.FAIL, "", "Failed to update NIC Portfolio data!", "");
        }
    }

    private NicPortfolio create(Row row, Employee updater) {
        try {
            return new NicPortfolio(
                    updater,
                    new Files(),
                    row.getCell(0).getDateCellValue(),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(3)),
                    // Transition
                    ExcelUtils.getDoubleValueFromCell(row.getCell(88)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(92)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(93)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(94)),
                    // Alternative
                    ExcelUtils.getDoubleValueFromCell(row.getCell(23)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(26)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(27)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(28)),
                    // Fixed
                    ExcelUtils.getDoubleValueFromCell(row.getCell(44)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(48)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(49)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(50)),
                    // Equity
                    ExcelUtils.getDoubleValueFromCell(row.getCell(66)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(70)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(71)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(72)),
                    // Hedge funds - Consolidated
                    ExcelUtils.getDoubleValueFromCell(row.getCell(152)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(157)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(158)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(159)),
                    // Hedge funds - CLASS A
                    ExcelUtils.getDoubleValueFromCell(row.getCell(174)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(179)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(180)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(181)),
                    // Hedge funds - CLASS B
                    ExcelUtils.getDoubleValueFromCell(row.getCell(196)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(201)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(202)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(203)),
                    // Private equity
                    ExcelUtils.getDoubleValueFromCell(row.getCell(218)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(222)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(223)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(224)),
                    // Real estate
                    ExcelUtils.getDoubleValueFromCell(row.getCell(240)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(244)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(245)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(246)),
                    // NICK MF
                    ExcelUtils.getDoubleValueFromCell(row.getCell(272)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(275)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(276)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(277)),
                    // Transfer
                    ExcelUtils.getDoubleValueFromCell(row.getCell(110)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(114)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(115)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(116))
            );
        } catch (Exception ex) {
            logger.error("Failed to update NIC Portfolio data: row parsing error, ", ex);
            throw ex;
        }
    }

    @Override
    public FilesDto getFileWithInputStream() {
        if(this.repository.findAllByOrderByDateAsc().size() > 0) {
            try {
                FilesType dummyFilesType = new FilesType();
                dummyFilesType.setId(filesTypeRepository.findByCode(FileTypeLookup.MONITORING_NIC_PORTFOLIO.getCode()).getId());

                List<Files> filesList = filesRepository.findAllByType(dummyFilesType);

                if (filesList == null || filesList.size() == 0) {
                    return null;
                } else if (filesList.size() == 1) {
                    Long fileId = filesList.get(0).getId();
                    FilesDto filesDto = this.filesEntityConverter.disassemble(this.filesRepository.findOne(fileId));
                    String path = filePathResolver.resolveDirectory(fileId, FileTypeLookup.MONITORING_NIC_PORTFOLIO.getCatalog());
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

                        String path = filePathResolver.resolveDirectory(fileId, FileTypeLookup.MONITORING_NIC_PORTFOLIO.getCatalog());
                        String name = HashUtils.hashMD5String(fileId.toString());
                        InputStream inputStream = new FileInputStream(path+name);
                        excelFile.setInputStream(inputStream);

                        setExportZipContent(excelFile.getOutputFileName(), out, excelFile);
                    }

                    out.close();

                    zipFile.setInputStream(new FileInputStream(zipFileName));
                    zipFile.setFileName(zipFileName);
                    zipFile.setMimeType("application/zip");

                    return zipFile;
                }
            } catch (Exception ex) {
            }
        }
        return null;
    }

    @Override
    public Date getMostRecentDate() {
        Date mostRecentDate = this.repository.getMostRecentDate();
        return mostRecentDate;
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
}
