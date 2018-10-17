package kz.nicnbk.service.impl.reporting;

import kz.nicnbk.common.service.util.DateUtils;
import kz.nicnbk.common.service.util.ExcelUtils;
import kz.nicnbk.common.service.util.MathUtils;
import kz.nicnbk.common.service.util.PaginationUtils;
import kz.nicnbk.repo.api.common.CurrencyRatesRepository;
import kz.nicnbk.repo.api.reporting.ReserveCalculationFilesRepository;
import kz.nicnbk.repo.api.reporting.ReserveCalculationRepository;
import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.repo.model.lookup.FileTypeLookup;
import kz.nicnbk.repo.model.lookup.reporting.CapitalCallExportTypeLookup;
import kz.nicnbk.repo.model.reporting.*;
import kz.nicnbk.service.api.common.CurrencyRatesService;
import kz.nicnbk.service.api.employee.EmployeeService;
import kz.nicnbk.service.api.files.FileService;
import kz.nicnbk.service.api.reporting.PeriodicReportService;
import kz.nicnbk.service.api.reporting.privateequity.ReserveCalculationService;
import kz.nicnbk.service.converter.reporting.ReserveCalculationConverter;
import kz.nicnbk.service.datamanager.LookupService;
import kz.nicnbk.service.dto.common.EntitySaveResponseDto;
import kz.nicnbk.service.dto.employee.EmployeeDto;
import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.lookup.CurrencyRatesDto;
import kz.nicnbk.service.dto.reporting.*;
import kz.nicnbk.service.dto.reporting.PeriodicReportType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * Created by magzumov on 30.11.2017.
 */

@Service
public class ReserveCalculationServiceImpl implements ReserveCalculationService {

    private static final Logger logger = LoggerFactory.getLogger(ReserveCalculationServiceImpl.class);

    /* Root folder on the server */
    @Value("${filestorage.root.directory}")
    private String rootDirectory;

    @Autowired
    private PeriodicReportService periodicReportService;

    @Autowired
    ReserveCalculationRepository reserveCalculationRepository;

    @Autowired
    private CurrencyRatesService currencyRatesService;

    @Autowired
    ReserveCalculationConverter reserveCalculationConverter;

    @Autowired
    private CurrencyRatesRepository currencyRatesRepository;

    @Autowired
    private FileService fileService;

    @Autowired
    private ReserveCalculationFilesRepository reserveCalculationFilesRepository;

    @Autowired
    private LookupService lookupService;

    @Autowired
    private EmployeeService employeeService;

    @Override
    public List<ReserveCalculationDto> getAllReserveCalculations() {
        List<ReserveCalculationDto> records = new ArrayList<>();
        try {

            Date mostRecentFinalReportDate = null;
            List<PeriodicReportDto> periodicReportDtos = this.periodicReportService.getAllPeriodicReports();
            if(periodicReportDtos != null){
                for(PeriodicReportDto periodicReportDto: periodicReportDtos){
                    if(periodicReportDto.getStatus() != null && periodicReportDto.getStatus().equalsIgnoreCase(PeriodicReportType.SUBMITTED.getCode())) {
                        if (mostRecentFinalReportDate == null || mostRecentFinalReportDate.compareTo(periodicReportDto.getReportDate()) < 0) {
                            mostRecentFinalReportDate = periodicReportDto.getReportDate();
                        }
                    }
                }
            }

            Iterator<ReserveCalculation> entitiesIterator = this.reserveCalculationRepository.findAll(new Sort(Sort.Direction.DESC, "date", "id")).iterator();
            if (entitiesIterator != null) {
                while (entitiesIterator.hasNext()) {
                    ReserveCalculation entity = entitiesIterator.next();
                    ReserveCalculationDto dto = this.reserveCalculationConverter.disassemble(entity);
                    setAdditionalFields(dto);

                    Set<FilesDto> files = getAttachments(entity.getId());
                    if(files != null){
                        dto.setFiles(files);
                    }

                    records.add(dto);
                }
            }
        } catch (Exception ex) {
            logger.error("Error loading reserve calculations", ex);
            return null;
        }
        return records;
    }

    private void setAdditionalFields(List<ReserveCalculationDto> dtoList){
        if(dtoList != null){
            for(ReserveCalculationDto dto: dtoList){
                setAdditionalFields(dto);
            }
        }
    }

    private void setAdditionalFields(ReserveCalculationDto dto){
        Date mostRecentFinalReportDate = null;
        List<PeriodicReportDto> periodicReportDtos = this.periodicReportService.getAllPeriodicReports();
        if(periodicReportDtos != null){
            for(PeriodicReportDto periodicReportDto: periodicReportDtos){
                if(periodicReportDto.getStatus() != null && periodicReportDto.getStatus().equalsIgnoreCase(PeriodicReportType.SUBMITTED.getCode())) {
                    if (mostRecentFinalReportDate == null || mostRecentFinalReportDate.compareTo(periodicReportDto.getReportDate()) < 0) {
                        mostRecentFinalReportDate = periodicReportDto.getReportDate();
                    }
                }
            }
        }

        // set currency rate
        Date nextDay = DateUtils.getNextDay(dto.getDate());
        CurrencyRatesDto currencyRatesDto = this.currencyRatesService.getRateForDateAndCurrency(nextDay, "USD");
        if (currencyRatesDto == null || currencyRatesDto.getValue() == null) {
            // TODO: error message
            logger.error("No currency rate for date '" + nextDay + "', currency='USD'");
            //return null;
        } else {
            dto.setCurrencyRate(currencyRatesDto.getValue());
        }

        // set amount kzt
        if (dto.getAmount() != null && currencyRatesDto != null && currencyRatesDto.getValue() != null) {
            dto.setAmountKZT(new BigDecimal(currencyRatesDto.getValue().doubleValue()).multiply(new BigDecimal(dto.getAmount())).setScale(2, RoundingMode.HALF_UP).doubleValue());
        }

        if(mostRecentFinalReportDate == null || dto.getDate() == null){
            dto.setCanDelete(true);
        }else { //mostRecentFinalReportDate != null && dto.getDate() != null
            dto.setCanDelete(dto.getDate().compareTo(mostRecentFinalReportDate) <= 0 ? false : true);
        }
    }

    @Override
    public ReserveCalculationPagedSearchResult search(ReserveCalculationSearchParams searchParams) {
        try {
            Page<ReserveCalculation> entityPage = null;
            int page = 0;

            int pageSize = searchParams != null && searchParams.getPageSize() > 0 ? searchParams.getPageSize() : DEFAULT_PAGE_SIZE;
            page = searchParams != null && searchParams.getPage() > 0 ? searchParams.getPage() - 1 : 0;
            entityPage = reserveCalculationRepository.search(new PageRequest(page, pageSize, new Sort(Sort.Direction.DESC, "date", "id")));

            ReserveCalculationPagedSearchResult result = new ReserveCalculationPagedSearchResult();
            if (entityPage != null) {
                result.setTotalElements(entityPage.getTotalElements());
                if (entityPage.getTotalElements() > 0) {
                    result.setShowPageFrom(PaginationUtils.getShowPageFrom(DEFAULT_PAGES_PER_VIEW, page + 1));
                    result.setShowPageTo(PaginationUtils.getShowPageTo(DEFAULT_PAGES_PER_VIEW,
                            page + 1, result.getShowPageFrom(), entityPage.getTotalPages()));
                }
                result.setTotalPages(entityPage.getTotalPages());
                result.setCurrentPage(page + 1);
                if (searchParams != null) {
                    result.setSearchParams(searchParams.getSearchParamsAsString());
                }
                List<ReserveCalculationDto> dtoList = reserveCalculationConverter.disassembleList(entityPage.getContent());
                for(ReserveCalculationDto dto: dtoList){
                    Set<FilesDto> files = getAttachments(dto.getId());
                    if(files != null){
                        dto.setFiles(files);
                    }
                }

                result.setRecords(dtoList);
                setAdditionalFields(result.getRecords());
                return result;
            }

        }catch(Exception ex){
            // TODO: log search params
            logger.error("Error searching reserve calculations", ex);
        }
        return null;
    }


    /**
     * Returns reserve calculation records for the specified month.
     * E.g. if date is 31.08.2018, then returns records with date
     * from 01.08.2018 to 31.08.2018.
     *
     * @param code - capital call type code
     * @param date - date
     * @param useValuationDate - true/false
     * @return - reserve calculation records
     */
    @Override
    public List<ReserveCalculationDto> getReserveCalculationsForMonth(String code, Date date, boolean useValuationDate) {

        Date mostRecentFinalReportDate = null;
        List<PeriodicReportDto> periodicReportDtos = this.periodicReportService.getAllPeriodicReports();
        if(periodicReportDtos != null){
            for(PeriodicReportDto periodicReportDto: periodicReportDtos){
                if(periodicReportDto.getStatus() != null && periodicReportDto.getStatus().equalsIgnoreCase(PeriodicReportType.SUBMITTED.getCode())) {
                    if (mostRecentFinalReportDate == null || mostRecentFinalReportDate.compareTo(periodicReportDto.getReportDate()) < 0) {
                        mostRecentFinalReportDate = periodicReportDto.getReportDate();
                    }
                }
            }
        }

        Date fromDate = DateUtils.getFirstDayOfCurrentMonth(date);
        Date toDate = DateUtils.getFirstDayOfNextMonth(date);
        List<ReserveCalculation> entities = null;
        if(useValuationDate){
            entities = this.reserveCalculationRepository.getEntitiesByExpenseTypeBetweenDatesUsingValuationDate(code, fromDate, toDate);
        }else {
            entities = this.reserveCalculationRepository.getEntitiesByExpenseTypeBetweenDates(code, fromDate, toDate);
        }

        List<ReserveCalculationDto> records = this.reserveCalculationConverter.disassembleList(entities);

        List<ReserveCalculationDto> updatedRecords = new ArrayList<>();
        for (ReserveCalculationDto record : records) {
            if(useValuationDate){
                if(record.getValueDate() != null){
                    if(record.getValueDate().compareTo(toDate) > 0){
                        break;
                    }
                }
            }
            // set currency rate
            Date nextDay = DateUtils.getNextDay(record.getDate());
            CurrencyRatesDto currencyRatesDto = this.currencyRatesService.getRateForDateAndCurrency(nextDay, "USD");
            if (currencyRatesDto == null || currencyRatesDto.getValue() == null) {
                // TODO: error message
                logger.error("No currency rate for date '" + nextDay + "', currency='USD'");
                //return null;
            } else {
                record.setCurrencyRate(currencyRatesDto.getValue());
            }

            if(mostRecentFinalReportDate == null || record.getDate() == null){
                record.setCanDelete(true);
            }else { //mostRecentFinalReportDate != null && dto.getDate() != null
                record.setCanDelete(record.getDate().compareTo(mostRecentFinalReportDate) <= 0 ? false : true);
            }

            // set amount kzt
            if (record.getAmount() != null && currencyRatesDto != null && currencyRatesDto.getValue() != null) {
                record.setAmountKZT(MathUtils.multiply(currencyRatesDto.getValue().doubleValue(), record.getAmount()));
            }

            if(useValuationDate){
                updatedRecords.add(record);
            }
        }

        if(useValuationDate){
            return updatedRecords;
        }else {
            return records;
        }
    }

    @Override
    public Double getReserveCalculationSumKZTForMonth(String code, Date date) {
        List<ReserveCalculationDto> records = getReserveCalculationsForMonth(code, date, false);
        Double sum = 0.0;
        if (records != null) {
            for (ReserveCalculationDto record : records) {
                if (record.getCurrencyRate() != null) {
                    sum = MathUtils.add(sum, record.getAmountKZT());
                } else {
                    String errorMessage = "Reserve Calculations sum: one of records has no currency rate. Date '" + DateUtils.getDateFormatted(record.getDate()) + "'";
                    logger.error(errorMessage);
                    throw new IllegalStateException(errorMessage);
                }
            }
        }
        return sum;
    }

    @Override
    public boolean save(List<ReserveCalculationDto> records) {
        try {
            if (records != null) {
                List<ReserveCalculation> entities = this.reserveCalculationConverter.assembleList(records);
                this.reserveCalculationRepository.save(entities);
            }
        } catch (Exception ex) {
            logger.error("Error saving reserve calculation records", ex);
            return false;
        }

        return true;
    }

    @Override
    public EntitySaveResponseDto save(ReserveCalculationDto record, String updater) {
        EntitySaveResponseDto entitySaveResponseDto = new EntitySaveResponseDto();
        try {
            ReserveCalculation entity = this.reserveCalculationConverter.assemble(record);

            EmployeeDto updaterEntity = this.employeeService.findByUsername(updater);
            if(entity.getId() != null){
                entity.setUpdater(new Employee(updaterEntity.getId()));
                entity.setUpdateDate(new Date());
                ReserveCalculation existingEntity = reserveCalculationRepository.findOne(entity.getId());
                entity.setCreationDate(existingEntity.getCreationDate());
                entity.setCreator(existingEntity.getCreator());
            }else if(updaterEntity != null && entity.getId() == null){
                entity.setCreator(new Employee(updaterEntity.getId()));
            }

            entity = this.reserveCalculationRepository.save(entity);
            ReserveCalculationDto savedEntity = this.reserveCalculationConverter.disassemble(entity);
            entitySaveResponseDto.setSuccessMessageEn("Successfully saved reserve calculation record, " +
                    "id = " + savedEntity.getId() + " [user=" + updater + "]");
            entitySaveResponseDto.setEntityId(savedEntity.getId());
            entitySaveResponseDto.setCreationDate(savedEntity.getCreationDate());
        } catch (Exception ex) {
            logger.error("Error saving reserve calculation record [user=" + updater + "]", ex);
            entitySaveResponseDto.setErrorMessageEn("Error saving reserve calculation record [user=" + updater + "]");
        }

        return entitySaveResponseDto;
    }

    //@Override
    private List<ReserveCalculationDto> getReserveCalculationsByExpenseType(String code) {
        Date mostRecentFinalReportDate = null;
        List<PeriodicReportDto> periodicReportDtos = this.periodicReportService.getAllPeriodicReports();
        if(periodicReportDtos != null){
            for(PeriodicReportDto periodicReportDto: periodicReportDtos){
                if(periodicReportDto.getStatus() != null && periodicReportDto.getStatus().equalsIgnoreCase(PeriodicReportType.SUBMITTED.getCode())) {
                    if (mostRecentFinalReportDate == null || mostRecentFinalReportDate.compareTo(periodicReportDto.getReportDate()) < 0) {
                        mostRecentFinalReportDate = periodicReportDto.getReportDate();
                    }
                }
            }
        }

        List<ReserveCalculationDto> records = new ArrayList<>();
        List<ReserveCalculation> entities = this.reserveCalculationRepository.getEntitiesByExpenseType(code);
        if (entities != null) {
            for (ReserveCalculation entity : entities) {
                ReserveCalculationDto dto = this.reserveCalculationConverter.disassemble(entity);

                // set currency rate
                Date nextDay = DateUtils.getNextDay(dto.getDate());
                CurrencyRatesDto currencyRatesDto = this.currencyRatesService.getRateForDateAndCurrency(nextDay, "USD");
                if (currencyRatesDto == null || currencyRatesDto.getValue() == null) {
                    // TODO: error message
                    logger.error("No currency rate for date '" + DateUtils.getDateFormatted(nextDay) + "', currency='USD'");
                    throw new IllegalStateException("No currency rate for date '" + DateUtils.getDateFormatted(nextDay) + "', currency='USD'");
                }
                dto.setCurrencyRate(currencyRatesDto.getValue());

                // set amount kzt
                if (dto.getAmount() != null) {
                    dto.setAmountKZT(new BigDecimal(currencyRatesDto.getValue().doubleValue()).multiply(new BigDecimal(dto.getAmount())).setScale(2, RoundingMode.HALF_UP).doubleValue());
                }

                if(mostRecentFinalReportDate == null || dto.getDate() == null){
                    dto.setCanDelete(true);
                }else { //mostRecentFinalReportDate != null && dto.getDate() != null
                    dto.setCanDelete(dto.getDate().compareTo(mostRecentFinalReportDate) <= 0 ? false : true);
                }

                records.add(dto);
            }
        }
        return records;
    }

    @Override
    public List<ReserveCalculationDto> getReserveCalculationsByExpenseTypeBeforeDate(String code, Date date) {
        Date mostRecentFinalReportDate = null;
        List<PeriodicReportDto> periodicReportDtos = this.periodicReportService.getAllPeriodicReports();
        if(periodicReportDtos != null){
            for(PeriodicReportDto periodicReportDto: periodicReportDtos){
                if(periodicReportDto.getStatus() != null && periodicReportDto.getStatus().equalsIgnoreCase(PeriodicReportType.SUBMITTED.getCode())) {
                    if (mostRecentFinalReportDate == null || mostRecentFinalReportDate.compareTo(periodicReportDto.getReportDate()) < 0) {
                        mostRecentFinalReportDate = periodicReportDto.getReportDate();
                    }
                }
            }
        }

        List<ReserveCalculationDto> records = new ArrayList<>();
        List<ReserveCalculation> entities = this.reserveCalculationRepository.getEntitiesByExpenseType(code);
        if (entities != null) {
            for (ReserveCalculation entity : entities) {
                ReserveCalculationDto dto = this.reserveCalculationConverter.disassemble(entity);
                if(dto.getDate().compareTo(date) > 0){
                    // capital calls after report period
                    continue;
                }

                // set currency rate
                Date nextDay = DateUtils.getNextDay(dto.getDate());
                CurrencyRatesDto currencyRatesDto = this.currencyRatesService.getRateForDateAndCurrency(nextDay, "USD");
                if (currencyRatesDto == null || currencyRatesDto.getValue() == null) {
                    // TODO: error message
                    logger.error("No currency rate for date '" + DateUtils.getDateFormatted(nextDay) + "', currency='USD'");
                    throw new IllegalStateException("No currency rate for date '" + DateUtils.getDateFormatted(nextDay) + "', currency='USD'");
                }
                dto.setCurrencyRate(currencyRatesDto.getValue());

                // set amount kzt
                if (dto.getAmount() != null) {
                    dto.setAmountKZT(new BigDecimal(currencyRatesDto.getValue().doubleValue()).multiply(new BigDecimal(dto.getAmount())).setScale(2, RoundingMode.HALF_UP).doubleValue());
                }

                if(mostRecentFinalReportDate == null || dto.getDate() == null){
                    dto.setCanDelete(true);
                }else { //mostRecentFinalReportDate != null && dto.getDate() != null
                    dto.setCanDelete(dto.getDate().compareTo(mostRecentFinalReportDate) <= 0 ? false : true);
                }

                records.add(dto);
            }
        }
        return records;
    }

    @Override
    public List<ReserveCalculationDto> getReserveCalculationsByExpenseTypeAfterDate(String code, Date date) {
        Date mostRecentFinalReportDate = null;
        List<PeriodicReportDto> periodicReportDtos = this.periodicReportService.getAllPeriodicReports();
        if(periodicReportDtos != null){
            for(PeriodicReportDto periodicReportDto: periodicReportDtos){
                if(periodicReportDto.getStatus() != null && periodicReportDto.getStatus().equalsIgnoreCase(PeriodicReportType.SUBMITTED.getCode())) {
                    if (mostRecentFinalReportDate == null || mostRecentFinalReportDate.compareTo(periodicReportDto.getReportDate()) < 0) {
                        mostRecentFinalReportDate = periodicReportDto.getReportDate();
                    }
                }
            }
        }

        List<ReserveCalculationDto> records = new ArrayList<>();
        List<ReserveCalculation> entities = this.reserveCalculationRepository.getEntitiesByExpenseType(code);
        if (entities != null) {
            for (ReserveCalculation entity : entities) {
                ReserveCalculationDto dto = this.reserveCalculationConverter.disassemble(entity);
                if(dto.getDate().compareTo(date) <= 0){
                    // capital calls before report period
                    continue;
                }

                // set currency rate
                Date nextDay = DateUtils.getNextDay(dto.getDate());
                CurrencyRatesDto currencyRatesDto = this.currencyRatesService.getRateForDateAndCurrency(nextDay, "USD");
                if (currencyRatesDto == null || currencyRatesDto.getValue() == null) {
                    // TODO: error message
                    logger.error("No currency rate for date '" + DateUtils.getDateFormatted(nextDay) + "', currency='USD'");
                    throw new IllegalStateException("No currency rate for date '" + DateUtils.getDateFormatted(nextDay) + "', currency='USD'");
                }
                dto.setCurrencyRate(currencyRatesDto.getValue());

                // set amount kzt
                if (dto.getAmount() != null) {
                    dto.setAmountKZT(new BigDecimal(currencyRatesDto.getValue().doubleValue()).multiply(new BigDecimal(dto.getAmount())).setScale(2, RoundingMode.HALF_UP).doubleValue());
                }

                if(mostRecentFinalReportDate == null || dto.getDate() == null){
                    dto.setCanDelete(true);
                }else { //mostRecentFinalReportDate != null && dto.getDate() != null
                    dto.setCanDelete(dto.getDate().compareTo(mostRecentFinalReportDate) <= 0 ? false : true);
                }

                records.add(dto);
            }
        }
        return records;
    }

    @Override
    public FilesDto getExportFileStream(Long recordId, String type, ReserveCalculationExportParamsDto exportParamsDto) {
        if (recordId == null) {
            logger.error("Capital call export: record id not specified");
            return null;
        }
        if (type == null) {
            logger.error("Capital call export: type not specified");
            return null;
        }

        if (type.equalsIgnoreCase(CapitalCallExportTypeLookup.TO_OPERATIONS.getCode())) {
            return getOperationsExportInputStream(recordId, exportParamsDto);
        } else if (type.equalsIgnoreCase(CapitalCallExportTypeLookup.TO_SPV.getCode())) {
            return getSPVExportInputStream(recordId, exportParamsDto);
        } else if (type.equalsIgnoreCase(CapitalCallExportTypeLookup.ORDER.getCode())) {
            return getOrderExportInputStream(recordId, exportParamsDto);
        } else if (type.equalsIgnoreCase(CapitalCallExportTypeLookup.ADM_FEE_TO_SPV.getCode())) {
            return getAdmFeeSPVExportInputStream(recordId, exportParamsDto);
        }
        logger.error("Capital call export: type not matched '" + type + "'");
        return null;
    }

    @Override
    public boolean deleteReserveCalculationRecord(Long recordId) {
        try {
            ReserveCalculation entity = this.reserveCalculationRepository.findOne(recordId);
            if(entity == null){
                logger.error("Reserve calculation - No record found to delete: record id " + recordId);
                return false;
            }

            // Get date of most recent report with status SUBMITTED
            Date mostRecentFinalReportDate = null;
            List<PeriodicReportDto> periodicReportDtos = this.periodicReportService.getAllPeriodicReports();
            if(periodicReportDtos != null){
                for(PeriodicReportDto periodicReportDto: periodicReportDtos){
                    if(periodicReportDto.getStatus() != null && periodicReportDto.getStatus().equalsIgnoreCase(PeriodicReportType.SUBMITTED.getCode())) {
                        if (mostRecentFinalReportDate == null || mostRecentFinalReportDate.compareTo(periodicReportDto.getReportDate()) < 0) {
                            mostRecentFinalReportDate = periodicReportDto.getReportDate();
                        }
                    }
                }
            }

            // cannot delete record with date earlier than most recent SUBMITTED report date
            if(mostRecentFinalReportDate != null && entity.getDate().compareTo(mostRecentFinalReportDate) <= 0){
                logger.error("Reserve Calculation delete record failed for record id " + recordId +
                        ": finalized report exists with date '" + DateUtils.getDateFormatted(mostRecentFinalReportDate) +
                        "', record date '" + DateUtils.getDateFormatted(entity.getDate()) + "'");
                return false;
            }

            // delete attachments
            List<ReserveCalculationFiles> entries = this.reserveCalculationFilesRepository.getFilesByEntityId(recordId);
            for(ReserveCalculationFiles entry: entries){
                Long fileId = entry.getFile().getId();
                this.reserveCalculationFilesRepository.delete(entry);
                fileService.delete(fileId);
            }

            this.reserveCalculationRepository.delete(entity);
            return true;
        }catch (Exception ex){
            logger.error("Error deleting Reserve Calculation Record: record id " + recordId, ex);
        }
        return false;
    }

    @Override
    public Set<FilesDto> saveAttachments(Long recordId, Set<FilesDto> attachments) {
        try {
            Set<FilesDto> dtoSet = new HashSet<>();
            if (attachments != null) {
                Iterator<FilesDto> iterator = attachments.iterator();
                while (iterator.hasNext()) {
                    FilesDto filesDto = iterator.next();
                    if (filesDto.getId() == null) {
                        Long fileId = fileService.save(filesDto, FileTypeLookup.CC_ATTACHMENT.getCatalog());
                        logger.info("Saved reserve calculation attachment file: record=" + recordId + ", file=" + fileId);
                        ReserveCalculationFiles entityFiles = new ReserveCalculationFiles(recordId, fileId);
                        this.reserveCalculationFilesRepository.save(entityFiles);
                        logger.info("Saved reserve calculation attachment info: record=" + recordId + ", file=" + fileId);

                        FilesDto newFileDto = new FilesDto();
                        newFileDto.setId(fileId);
                        newFileDto.setFileName(filesDto.getFileName());
                        dtoSet.add(newFileDto);
                    }
                }
            }
            return dtoSet;
        }catch (Exception ex){
            logger.error("Error saving reserve calculation attachments: record=" + recordId, ex);
        }
        return null;
    }

    @Override
    public boolean safeDeleteReserveCalculationAttachment(Long recordId, Long fileId, String username) {
        Date mostRecentFinalReportDate = null;
        List<PeriodicReportDto> periodicReportDtos = this.periodicReportService.getAllPeriodicReports();
        if(periodicReportDtos != null){
            for(PeriodicReportDto periodicReportDto: periodicReportDtos){
                if(periodicReportDto.getStatus() != null && periodicReportDto.getStatus().equalsIgnoreCase(PeriodicReportType.SUBMITTED.getCode())) {
                    if (mostRecentFinalReportDate == null || mostRecentFinalReportDate.compareTo(periodicReportDto.getReportDate()) < 0) {
                        mostRecentFinalReportDate = periodicReportDto.getReportDate();
                    }
                }
            }
        }

        try{
            ReserveCalculationFiles entity = reserveCalculationFilesRepository.getFilesByFileId(fileId);

            if(mostRecentFinalReportDate != null && entity.getEntity().getDate() != null &&
                    entity.getEntity().getDate().compareTo(mostRecentFinalReportDate) <= 0){
                logger.error("Cannot delete attachment for reserve calculation record with date = " +
                        DateUtils.getDateFormatted(entity.getEntity().getDate()) + ", final report exists for period = " + DateUtils.getDateFormatted(mostRecentFinalReportDate));
                return false;
            }

            if (entity != null && entity.getEntity().getId().longValue() == recordId) {
                boolean deleted = fileService.safeDelete(fileId);
                if(!deleted){
                    logger.error("Failed to delete(safe) reserve calculation attachment: record=" + recordId + ", file=" + fileId + ", by " + username);
                }else{
                    logger.info("Deleted(safe)reserve calculation attachment: record=" + recordId + ", file=" + fileId + ", by " + username);
                }
                return true;
            }else{
                logger.error("Failed to delete(safe) reserve calculation attachment: record=" + recordId + ", file=" + fileId + ", by " + username +
                        ". Reserve Calculation-File entity not found");
            }
        }catch (Exception ex){
            logger.error("Failed to delete(safe) reserve calculation attachment with exception: record=" + recordId + ", file=" + fileId + ", by " + username, ex);
        }
        return false;
    }

    @Override
    public boolean deleteReserveCalculationAttachment(Long recordId, Long fileId, String username) {
        Date mostRecentFinalReportDate = null;
        List<PeriodicReportDto> periodicReportDtos = this.periodicReportService.getAllPeriodicReports();
        if(periodicReportDtos != null){
            for(PeriodicReportDto periodicReportDto: periodicReportDtos){
                if(periodicReportDto.getStatus() != null && periodicReportDto.getStatus().equalsIgnoreCase(PeriodicReportType.SUBMITTED.getCode())) {
                    if (mostRecentFinalReportDate == null || mostRecentFinalReportDate.compareTo(periodicReportDto.getReportDate()) < 0) {
                        mostRecentFinalReportDate = periodicReportDto.getReportDate();
                    }
                }
            }
        }

        try{
            ReserveCalculationFiles entity = reserveCalculationFilesRepository.getFilesByFileId(fileId);
            if(entity.getEntity().getId().compareTo(recordId) != 0){
                logger.error("Failed to delete reserve calculation attachment file: provided record does not match, recordid =" + recordId);
                return false;
            }

            if(mostRecentFinalReportDate != null && entity.getEntity().getDate() != null &&
                    entity.getEntity().getDate().compareTo(mostRecentFinalReportDate) <= 0){
                logger.error("Cannot delete attachment for reserve calculation record with date = " +
                        DateUtils.getDateFormatted(entity.getEntity().getDate()) + ", final report exists for period = " + DateUtils.getDateFormatted(mostRecentFinalReportDate));
                return false;
            }

            if (entity != null && entity.getEntity().getId().longValue() == recordId) {
                reserveCalculationFilesRepository.delete(entity);
                boolean deleted = fileService.delete(fileId);
                if(!deleted){
                    logger.error("Failed to delete(safe) reserve calculation attachment: record=" + recordId + ", file=" + fileId + ", by " + username);
                }else{
                    logger.info("Deleted(safe)reserve calculation attachment: record=" + recordId + ", file=" + fileId + ", by " + username);
                }
                return true;
            }else{
                logger.error("Failed to delete(safe) reserve calculation attachment: record=" + recordId + ", file=" + fileId + ", by " + username +
                        ". Reserve Calculation-File entity not found");
            }
        }catch (Exception ex){
            logger.error("Failed to delete(safe) reserve calculation attachment with exception: record=" + recordId + ", file=" + fileId + ", by " + username, ex);
        }
        return false;
    }

    @Override
    public ReserveCalculationDto getRecordById(Long recordId) {
        ReserveCalculation entity = this.reserveCalculationRepository.findOne(recordId);
        if(entity != null){
            return this.reserveCalculationConverter.disassemble(entity);
        }
        return null;
    }

    @Override
    public boolean excludeIncludeRecord(Long recordId, String name) {
        ReserveCalculation entity = this.reserveCalculationRepository.findOne(recordId);
        if(entity != null){
            if(name != null && name.equalsIgnoreCase("Capital call capital adjustment")) {
                boolean currentValue = entity.getExcludeOppositeFromTerraCalculation() == null ? false : entity.getExcludeOppositeFromTerraCalculation().booleanValue();
                entity.setExcludeOppositeFromTerraCalculation(!currentValue);
            }else{
                boolean currentValue = entity.getExcludeFromTerraCalculation() == null ? false : entity.getExcludeFromTerraCalculation().booleanValue();
                entity.setExcludeFromTerraCalculation(!currentValue);
            }
            this.reserveCalculationRepository.save(entity);
            return true;
        }
        return false;
    }

    public Set<FilesDto> getAttachments(Long recordId){
        try {
            List<ReserveCalculationFiles> entities = reserveCalculationFilesRepository.getFilesByEntityId(recordId);
            Set<FilesDto> files = new HashSet<>();
            if (entities != null) {
                for (ReserveCalculationFiles entity : entities) {
                    FilesDto fileDto = new FilesDto();
                    fileDto.setId(entity.getFile().getId());
                    fileDto.setFileName(entity.getFile().getFileName());
                    files.add(fileDto);
                }
            }
            return files;
        }catch(Exception ex){
            logger.error("Error getting reserve calculation attachments: record =" + recordId, ex);
        }
        return null;
    }

    private ReserveCalculationDto getReserveCalculationRecordById(Long recordId){
        return this.reserveCalculationConverter.disassemble(this.reserveCalculationRepository.findOne(recordId));
    }

    private FilesDto getOperationsExportInputStream(Long recordId, ReserveCalculationExportParamsDto exportParamsDto){

        FilesDto filesDto = new FilesDto();
        ReserveCalculationDto record = getReserveCalculationRecordById(recordId);

        Resource resource = new ClassPathResource("export_template/capital_call/CC_TA_OA_TEMPLATE.xlsx");
        InputStream excelFileToRead = null;
        try {
            excelFileToRead = resource.getInputStream();
        } catch (IOException e) {
            logger.error("Reporting: Export file template not found: 'CC_TA_OA_TEMPLATE.xlsx'");
            return null;
            //e.printStackTrace();
        }


        String investmentCategory = "Additional Subscription";
        // Check if it is the first capital call from given asset class (recipient)
        if(record.getRecipient() != null && record.getRecipient().getCode() != null) {
            String entityNameStart = "";
            if(record.getRecipient().getCode().startsWith("SING")){
                entityNameStart = "SING";
            }else if(record.getRecipient().getCode().startsWith("TARR")){
                entityNameStart = "TARR";
            }else if(record.getRecipient().getCode().startsWith("TERRA")){
                entityNameStart = "TERRA";
            }
            int entitiesCount = this.reserveCalculationRepository.getEntitiesCountByRecipientTypeStartsWith(entityNameStart);
            if(entitiesCount == 1){
                investmentCategory = "Direct Investment";
            }
        }

        try {
            XSSFWorkbook workbook = new XSSFWorkbook(excelFileToRead);
            XSSFSheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            boolean endOfTable = false;
            while (rowIterator.hasNext() && !endOfTable) { // each row
                Row row = rowIterator.next();

                if(ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(2), "Investment Category*") &&
                        ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(3), "<investment_category>")){
                    row.getCell(3).setCellValue(investmentCategory);
                }else if(ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(2), "Original Investment Approval Date:") &&
                        ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(3), "<dd.MM.yyyy>")){
                    row.getCell(3).setCellValue(DateUtils.getDateFormatted(record.getDate()));
                }else if(ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(2), "Value Date:") &&
                        ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(3), "<date_text>")){
                    row.getCell(3).setCellValue(DateUtils.getDateEnglishTextualDate(record.getDate()));
                }else if(ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(2), "Amount:") &&
                        ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(3), "<amount>")){
                    row.getCell(3).setCellValue(String.format(Locale.ENGLISH, "$%,.2f", record.getAmount()));
                }else if(ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(2), "<DIRECTORNAME>-Director") &&
                        ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(3), "<dd.MM.yyyy>")){
                    ReserveCalculationExportSignerType signerType =
                            this.lookupService.findByTypeAndCode(ReserveCalculationExportSignerType.class, exportParamsDto.getDirector());
                    if(signerType != null){
                        row.getCell(2).setCellValue(signerType.getNameEn() + "-Director");
                    }else{
                        row.getCell(2).setCellValue("Director");
                    }
                    row.getCell(3).setCellValue(DateUtils.getDateFormatted(record.getDate()));
                }else if(ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(2), "<viza_1>")){
                    if(exportParamsDto != null && exportParamsDto.getApproveList() != null && !exportParamsDto.getApproveList().isEmpty()){
                        ReserveCalculationExportApproveListType approverType =
                                this.lookupService.findByTypeAndCode(ReserveCalculationExportApproveListType.class, exportParamsDto.getApproveList().get(0));
                        if(approverType != null) {
                            row.getCell(2).setCellValue(approverType.getNameRu() + "________________");
                        }else{
                            row.getCell(2).setCellValue("________________");
                        }
                    }else{
                        row.getCell(2).setCellValue("");
                    }
                }else if(ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(2), "<viza_2>")){
                    if(exportParamsDto != null && exportParamsDto.getApproveList() != null && exportParamsDto.getApproveList().size() > 1){
                        ReserveCalculationExportApproveListType approverType =
                                this.lookupService.findByTypeAndCode(ReserveCalculationExportApproveListType.class, exportParamsDto.getApproveList().get(1));
                        if(approverType != null) {
                            row.getCell(2).setCellValue(approverType.getNameRu() + "________________");
                        }else{
                            row.getCell(2).setCellValue("________________");
                        }
                    }else{
                        row.getCell(2).setCellValue("");
                    }
                }else if(ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(2), "<viza_3>")){
                    if(exportParamsDto != null && exportParamsDto.getApproveList() != null && exportParamsDto.getApproveList().size() > 2){
                        ReserveCalculationExportApproveListType approverType =
                                this.lookupService.findByTypeAndCode(ReserveCalculationExportApproveListType.class, exportParamsDto.getApproveList().get(2));
                        if(approverType != null) {
                            row.getCell(2).setCellValue(approverType.getNameRu() + "________________");
                        }else{
                            row.getCell(2).setCellValue("________________");
                        }
                    }else{
                        row.getCell(2).setCellValue("");
                    }
                }else if(ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(2), "<doer>")){
                    if(exportParamsDto != null && exportParamsDto.getDoer() != null){
                        ReserveCalculationExportDoerType doerType =
                                this.lookupService.findByTypeAndCode(ReserveCalculationExportDoerType.class, exportParamsDto.getDoer());
                        if(doerType != null){
                            row.getCell(2).setCellValue(doerType.getNameRu());
                        }else{
                            row.getCell(2).setCellValue("");
                        }
                    }else{
                        row.getCell(2).setCellValue("");
                    }
                }
            }
            //
            File tmpDir = new File(this.rootDirectory + "/tmp/nbrk_reporting");

            // write to new
            String filePath = tmpDir + "/CC_TA_OA_" + MathUtils.getRandomNumber(0, 10000) + ".xlsx";
            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }

            InputStream inputStream = new FileInputStream(filePath);
            filesDto.setInputStream(inputStream);
            filesDto.setFileName(filePath);
            return filesDto;
        } catch (IOException e) {
            logger.error("IO Exception when exporting Capital Call TA OA", e);
        }

        return null;
    }

    private FilesDto getSPVExportInputStream(Long recordId, ReserveCalculationExportParamsDto exportParamsDto){

        FilesDto filesDto = new FilesDto();
        ReserveCalculationDto record = getReserveCalculationRecordById(recordId);

        Resource resource = new ClassPathResource("export_template/capital_call/CC_OA_SPV_TEMPLATE.xlsx");
        InputStream excelFileToRead = null;

        String investmentCategory = "Additional Subscription";
        // Check if it is the first capital call from given asset class (recipient)
        if(record.getRecipient() != null && record.getRecipient().getCode() != null) {
            String entityNameStart = "";
            if(record.getRecipient().getCode().startsWith("SING")){
                entityNameStart = "SING";
            }else if(record.getRecipient().getCode().startsWith("TARR")){
                entityNameStart = "TARR";
            }else if(record.getRecipient().getCode().startsWith("TERRA")){
                entityNameStart = "TERRA";
            }
            int entitiesCount = this.reserveCalculationRepository.getEntitiesCountByRecipientTypeStartsWith(entityNameStart);
            if(entitiesCount == 1){
                investmentCategory = "Direct Investment";
            }
        }

        try {
            excelFileToRead = resource.getInputStream();
        } catch (IOException e) {
            logger.error("Reporting: Export file template not found: 'CC_OA_SPV_TEMPLATE.xlsx'");
            return null;
            //e.printStackTrace();
        }

        try {
            XSSFWorkbook workbook = new XSSFWorkbook(excelFileToRead);
            XSSFSheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            boolean endOfTable = false;
            String beneficiary = record.getRecipient().getCode().startsWith("SING") ? "Singularity, Ltd." :
                    record.getRecipient().getCode().startsWith("TARR") ? "Tarragon LP" :
                            record.getRecipient().getCode().startsWith("TERRA") ? "Terra L.P." : "";
            String bankCode = record.getRecipient().getCode().startsWith("TARR") ? "CTZIUS33" :
                    record.getRecipient().getCode().startsWith("SING") ? "021 000 018" :
                            record.getRecipient().getCode().startsWith("TERRA") ? "122-016-066" : "";
            String bankDetails = record.getRecipient().getCode().startsWith("TARR") ? "Citizens Bank, N.A., One Citizens Drive Riverside, RI 02915-3000" :
                    //record.getRecipient().getCode().startsWith("SING") ? "Bank of New York Mellon, 1 Wall Street, New York" :
                    record.getRecipient().getCode().startsWith("SING") ? "Bank of New York Mellon, 240 Greenwich Street, New York" :
                            record.getRecipient().getCode().startsWith("TERRA") ? "City National Bank, Los Angeles, CA" : "";
            String accountNumber = record.getRecipient().getCode().startsWith("TARR") ? "4010297920" :
                    record.getRecipient().getCode().startsWith("SING_B") ? "8901300101" :
                            record.getRecipient().getCode().startsWith("SING_A") ? "8901274828" :
                                    record.getRecipient().getCode().startsWith("TERRA") ? "210-45-9367" : "";
            String reference = record.getRecipient().getCode().startsWith("TARR") ? "NICK Master Fund Ltd." :
                    record.getRecipient().getCode().startsWith("SING") ? "Grosvenor Capital Management, L.P." :
                            record.getRecipient().getCode().startsWith("TERRA") ? "NICK Master Fund Ltd." +
                                    (investmentCategory.equalsIgnoreCase("Direct investment") ? "Capital Call#1" : "") : "";

            while (rowIterator.hasNext() && !endOfTable) { // each row
                Row row = rowIterator.next();
                if(ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(2), "Investment Category*") &&
                        ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(4), "<investment_category>")){
                    row.getCell(4).setCellValue(investmentCategory);
                }else if(ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(2), "Original Investment Approval Date:") &&
                        ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(4), "<dd.MM.yyyy>")){
                    row.getCell(4).setCellValue(DateUtils.getDateFormatted(record.getDate()));
                }else if(ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(2), "Use of Funds (New, Add-on, Mgmt Fee, etc.):") &&
                        ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(4), "<use_of_funds>")) {
                    row.getCell(4).setCellValue("Funds transfer from NICK Operating to " + beneficiary);
                }else if(ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(2), "Value Date:") &&
                        ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(4), "<date_text_with_time>")){
                    Date date = record.getValueDate() != null ? record.getValueDate() : record.getDate();
                    row.getCell(4).setCellValue(DateUtils.getDateEnglishTextualDate(date) + " before 4 pm London time");
                }else if(ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(2), "Amount:") &&
                        ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(4), "<amount>")){
                    // TODO: amount formatting
                    Double amount = record.getAmountToSPV() != null ? record.getAmountToSPV() : record.getAmount();
                    row.getCell(4).setCellValue(String.format(Locale.ENGLISH, "$%,.2f", amount));
                }else if(ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(2), "Bank Code (SWIFT or ABA):") &&
                        ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(4), "<bank_code>")){
                    row.getCell(4).setCellValue(bankCode);
                }else if(ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(2), "Bank Name and Address:") &&
                        ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(4), "<bank_details>")){
                    row.getCell(4).setCellValue(bankDetails);
                }else if(ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(2), "Account number or CHIPS UID:") &&
                        ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(4), "<account_number>")){
                    row.getCell(4).setCellValue(accountNumber);
                }else if(ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(2), "Beneficiary's Name:") &&
                        ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(4), "<beneficiary>")){
                    row.getCell(4).setCellValue(beneficiary);
                }else if(ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(2), "Reference:") &&
                        ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(4), "<reference>")){
                    row.getCell(4).setCellValue(reference);
                }else if(ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(2), "<DIRECTORNAME>-Director") &&
                        ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(4), "<dd.MM.yyyy>")){
                    ReserveCalculationExportSignerType signerType =
                            this.lookupService.findByTypeAndCode(ReserveCalculationExportSignerType.class, exportParamsDto.getDirector());
                    if(signerType != null){
                        row.getCell(2).setCellValue(signerType.getNameEn() + "-Director");
                    }else{
                        row.getCell(2).setCellValue("Director");
                    }
                    row.getCell(4).setCellValue(DateUtils.getDateFormatted(record.getDate()));
                }else if(ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(2), "<viza_1>")){
                    if(exportParamsDto != null && exportParamsDto.getApproveList() != null && !exportParamsDto.getApproveList().isEmpty()){
                        ReserveCalculationExportApproveListType approverType =
                                this.lookupService.findByTypeAndCode(ReserveCalculationExportApproveListType.class, exportParamsDto.getApproveList().get(0));
                        if(approverType != null) {
                            row.getCell(2).setCellValue(approverType.getNameRu() + "________________");
                        }else{
                            row.getCell(2).setCellValue("________________");
                        }
                    }
                }else if(ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(2), "<viza_2>")){
                    if(exportParamsDto != null && exportParamsDto.getApproveList() != null && exportParamsDto.getApproveList().size() > 1){
                        ReserveCalculationExportApproveListType approverType =
                                this.lookupService.findByTypeAndCode(ReserveCalculationExportApproveListType.class, exportParamsDto.getApproveList().get(1));
                        if(approverType != null) {
                            row.getCell(2).setCellValue(approverType.getNameRu() + "________________");
                        }else{
                            row.getCell(2).setCellValue("________________");
                        }
                    }else{
                        row.getCell(2).setCellValue("");
                    }
                }else if(ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(2), "<viza_3>")){
                    if(exportParamsDto != null && exportParamsDto.getApproveList() != null && exportParamsDto.getApproveList().size() > 2){
                        ReserveCalculationExportApproveListType approverType =
                                this.lookupService.findByTypeAndCode(ReserveCalculationExportApproveListType.class, exportParamsDto.getApproveList().get(2));
                        if(approverType != null) {
                            row.getCell(2).setCellValue(approverType.getNameRu() + "________________");
                        }else{
                            row.getCell(2).setCellValue("________________");
                        }
                    }else{
                        row.getCell(2).setCellValue("");
                    }
                }else if(ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(2), "<doer>")){
                    if(exportParamsDto != null && exportParamsDto.getDoer() != null){
                        ReserveCalculationExportDoerType doerType =
                                this.lookupService.findByTypeAndCode(ReserveCalculationExportDoerType.class, exportParamsDto.getDoer());
                        if(doerType != null){
                            row.getCell(2).setCellValue(doerType.getNameRu());
                        }else{
                            row.getCell(2).setCellValue("");
                        }
                    }else{
                        row.getCell(2).setCellValue("");
                    }
                }
            }
            //
            File tmpDir = new File(this.rootDirectory + "/tmp/nbrk_reporting");

            // write to new
            String filePath = tmpDir + "/CC_TA_OA_" + MathUtils.getRandomNumber(0, 10000) + ".xlsx";
            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }

            InputStream inputStream = new FileInputStream(filePath);
            filesDto.setInputStream(inputStream);
            filesDto.setFileName(filePath);
            return filesDto;
        } catch (IOException e) {
            logger.error("IO Exception when exporting Capital Call TA OA", e);
        }

        return null;
    }

    private FilesDto getAdmFeeSPVExportInputStream(Long recordId, ReserveCalculationExportParamsDto exportParamsDto){

        FilesDto filesDto = new FilesDto();
        ReserveCalculationDto record = getReserveCalculationRecordById(recordId);

        Resource resource = new ClassPathResource("export_template/capital_call/CC_ADM_FEE_OA_SPV_TEMPLATE.xlsx");
        InputStream excelFileToRead = null;

        try {
            excelFileToRead = resource.getInputStream();
        } catch (IOException e) {
            logger.error("Reporting: Export file template not found: 'CC_ADM_FEE_OA_SPV_TEMPLATE.xlsx'");
            return null;
            //e.printStackTrace();
        }

        try {
            XSSFWorkbook workbook = new XSSFWorkbook(excelFileToRead);
            XSSFSheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            boolean endOfTable = false;
            String beneficiary = record.getRecipient() != null && record.getRecipient().getCode().equalsIgnoreCase("BNY_M") ?
                    "The Bank of New York Mellon" : record.getRecipient() != null && record.getRecipient().getCode().equalsIgnoreCase("MAPLES") ?
                    "Maples and Calder" : "";
            String bankCode = record.getRecipient() != null && record.getRecipient().getCode().equalsIgnoreCase("BNY_M") ?
                    "021000018" : record.getRecipient() != null && record.getRecipient().getCode().equalsIgnoreCase("MAPLES") ?
                    "BNTBKYKYXXX" : "";
            String bankDetails = record.getRecipient() != null && record.getRecipient().getCode().equalsIgnoreCase("BNY_M") ?
                    "The Bank of New York Mellon" : record.getRecipient() != null && record.getRecipient().getCode().equalsIgnoreCase("MAPLES") ?
                    "Butterfield Bank (Cayman) Ltd., PO Box 705, 12 Albert Panton Street Grand Cayman KY1-1107" : "";

            String intermediaryBankCode = record.getRecipient() != null && record.getRecipient().getCode().equalsIgnoreCase("BNY_M") ?
                    "" : record.getRecipient() != null && record.getRecipient().getCode().equalsIgnoreCase("MAPLES") ?
                    "IRVTUS3NXXX" : "";

            String intermediaryBankDetails = record.getRecipient() != null && record.getRecipient().getCode().equalsIgnoreCase("BNY_M") ?
                    "" : record.getRecipient() != null && record.getRecipient().getCode().equalsIgnoreCase("MAPLES") ?
                    "BNY Mellon, One Wall Street, New York, NY 10286" : "";

            String accountNumber = record.getRecipient() != null && record.getRecipient().getCode().equalsIgnoreCase("BNY_M") ?
                    "8901245267" : record.getRecipient() != null && record.getRecipient().getCode().equalsIgnoreCase("MAPLES") ?
                    "0110103434400" : "";

            String useOfFunds = record.getRecipient() != null && record.getRecipient().getCode().equalsIgnoreCase("BNY_M") ?
                    "Invoice payment for administration services from NICK Operating to the Bank of New York Mellon." :
                    record.getRecipient() != null && record.getRecipient().getCode().equalsIgnoreCase("MAPLES") ?
                    "Invoice payment for services from NICK Operating to Maples Corporate Services Ltd." : "";

            while (rowIterator.hasNext() && !endOfTable) { // each row
                Row row = rowIterator.next();
                if(ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(2), "Original Investment Approval Date:") &&
                        ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(4), "<dd.MM.yyyy>")){
                    row.getCell(4).setCellValue(DateUtils.getDateFormatted(record.getDate()));
                }else if(ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(2), "Use of Funds (New, Add-on, Mgmt Fee, etc.):") &&
                        ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(4), "<use_of_funds>")) {
                    row.getCell(4).setCellValue(useOfFunds);
                }else if(ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(2), "Value Date:") &&
                        ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(4), "<date_text>")){
                    Date date = record.getValueDate() != null ? record.getValueDate() : record.getDate();
                    row.getCell(4).setCellValue(DateUtils.getDateEnglishTextualDate(date));
                }else if(ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(2), "Amount:") &&
                        ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(4), "<amount>")){
                    // TODO: amount formatting
                    Double amount = record.getAmountToSPV() != null ? record.getAmountToSPV() : record.getAmount();
                    row.getCell(4).setCellValue(String.format(Locale.ENGLISH, "$%,.2f", amount));
                }else if(ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(2), "Bank Code (SWIFT or ABA):") &&
                        ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(4), "<bank_code>")){
                    row.getCell(4).setCellValue(bankCode);
                }else if(ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(2), "Bank Name and Address:") &&
                        ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(4), "<bank_details>")){
                    row.getCell(4).setCellValue(bankDetails);
                }else if(ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(2), "Bank Code (SWIFT or ABA):") &&
                        ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(4), "<interm_bank_code>")){
                    row.getCell(4).setCellValue(intermediaryBankCode);
                }else if(ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(2), "Bank Name and Address:") &&
                        ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(4), "<interm_bank_details>")){
                    row.getCell(4).setCellValue(intermediaryBankDetails);
                }else if(ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(2), "Account number or CHIPS UID:") &&
                        ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(4), "<account_number>")){
                    row.getCell(4).setCellValue(accountNumber);
                }else if(ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(2), "Beneficiary's Name:") &&
                        ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(4), "<beneficiary>")){
                    row.getCell(4).setCellValue(beneficiary);
                }else if(ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(2), "Reference:") &&
                        ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(4), "<reference>")){
                    row.getCell(4).setCellValue(record.getReferenceInfo());
                }else if(ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(2), "<DIRECTORNAME>-Director") &&
                        ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(4), "<dd.MM.yyyy>")){
                    ReserveCalculationExportSignerType signerType =
                            this.lookupService.findByTypeAndCode(ReserveCalculationExportSignerType.class, exportParamsDto.getDirector());
                    if(signerType != null){
                        row.getCell(2).setCellValue(signerType.getNameEn() + "-Director");
                    }else{
                        row.getCell(2).setCellValue("Director");
                    }
                    row.getCell(4).setCellValue(DateUtils.getDateFormatted(record.getDate()));
                }else if(ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(2), "<viza_1>")){
                    if(exportParamsDto != null && exportParamsDto.getApproveList() != null && !exportParamsDto.getApproveList().isEmpty()){
                        ReserveCalculationExportApproveListType approverType =
                                this.lookupService.findByTypeAndCode(ReserveCalculationExportApproveListType.class, exportParamsDto.getApproveList().get(0));
                        if(approverType != null) {
                            row.getCell(2).setCellValue(approverType.getNameRu() + "________________");
                        }else{
                            row.getCell(2).setCellValue("________________");
                        }
                    }
                }else if(ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(2), "<viza_2>")){
                    if(exportParamsDto != null && exportParamsDto.getApproveList() != null && exportParamsDto.getApproveList().size() > 1){
                        ReserveCalculationExportApproveListType approverType =
                                this.lookupService.findByTypeAndCode(ReserveCalculationExportApproveListType.class, exportParamsDto.getApproveList().get(1));
                        if(approverType != null) {
                            row.getCell(2).setCellValue(approverType.getNameRu() + "________________");
                        }else{
                            row.getCell(2).setCellValue("________________");
                        }
                    }else{
                        row.getCell(2).setCellValue("");
                    }
                }else if(ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(2), "<viza_3>")){
                    if(exportParamsDto != null && exportParamsDto.getApproveList() != null && exportParamsDto.getApproveList().size() > 2){
                        ReserveCalculationExportApproveListType approverType =
                                this.lookupService.findByTypeAndCode(ReserveCalculationExportApproveListType.class, exportParamsDto.getApproveList().get(2));
                        if(approverType != null) {
                            row.getCell(2).setCellValue(approverType.getNameRu() + "________________");
                        }else{
                            row.getCell(2).setCellValue("________________");
                        }
                    }else{
                        row.getCell(2).setCellValue("");
                    }
                }else if(ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(2), "<doer>")){
                    if(exportParamsDto != null && exportParamsDto.getDoer() != null){
                        ReserveCalculationExportDoerType doerType =
                                this.lookupService.findByTypeAndCode(ReserveCalculationExportDoerType.class, exportParamsDto.getDoer());
                        if(doerType != null){
                            row.getCell(2).setCellValue(doerType.getNameRu());
                        }else{
                            row.getCell(2).setCellValue("");
                        }
                    }else{
                        row.getCell(2).setCellValue("");
                    }
                }
            }
            //
            File tmpDir = new File(this.rootDirectory + "/tmp/nbrk_reporting");

            // write to new
            String filePath = tmpDir + "/CC_AMD_FEE_OA_SPV_" + MathUtils.getRandomNumber(0, 10000) + ".xlsx";
            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }

            InputStream inputStream = new FileInputStream(filePath);
            filesDto.setInputStream(inputStream);
            filesDto.setFileName(filePath);
            return filesDto;
        } catch (IOException e) {
            logger.error("IO Exception when exporting Capital Call OA SPT (ADM_FEE)", e);
        }

        return null;
    }

    private FilesDto getOrderExportInputStream(Long recordId, ReserveCalculationExportParamsDto exportParamsDto){

        FilesDto filesDto = new FilesDto();
        ReserveCalculationDto record = getReserveCalculationRecordById(recordId);

        Resource resource = new ClassPathResource("export_template/capital_call/CC_ORDER_TEMPLATE.docx");
        InputStream wordFileToRead = null;
        try {
            wordFileToRead = resource.getInputStream();
        } catch (IOException e) {
            logger.error("Reporting: Export file template not found: 'CC_ORDER_TEMPLATE.docx'");
            return null;
            //e.printStackTrace();
        }

        String transactionType = "Additional Subscription";
        // Check if it is the first capital call from given asset class (recipient)
        if(record.getRecipient() != null && record.getRecipient().getCode() != null) {
            String entityNameStart = "";
            if(record.getRecipient().getCode().startsWith("SING")){
                entityNameStart = "SING";
            }else if(record.getRecipient().getCode().startsWith("TARR")){
                entityNameStart = "TARR";
            }else if(record.getRecipient().getCode().startsWith("TERRA")){
                entityNameStart = "TERRA";
            }
            int entitiesCount = this.reserveCalculationRepository.getEntitiesCountByRecipientTypeStartsWith(entityNameStart);
            if(entitiesCount == 1){
                transactionType = "Initial Subscription";
            }
        }

        try {
            XWPFDocument document = new XWPFDocument(wordFileToRead);
            List<XWPFParagraph> paragraphs = document.getParagraphs();
            if(paragraphs != null){
                for(XWPFParagraph paragraph: paragraphs){
                    List<XWPFRun> runs = paragraph.getRuns();
                    if (runs != null) {
                        for (XWPFRun r : runs) {
                            String text = r.getText(0);
                            if (text != null && text.contains("INPUTDATE")) {
                                text = text.replace("INPUTDATE", DateUtils.getDateEnglishTextualDate(record.getDate()));
                                r.setText(text, 0);
                            }else if (text != null && text.contains("INPUTVALUEDATE")) {
                                text = text.replace("INPUTVALUEDATE", DateUtils.getDateEnglishTextualDate(record.getDate()));
                                r.setText(text, 0);
                            }else if (text != null && text.contains("INPUTENTITY")) {

                                String name = record.getRecipient().getCode().startsWith("TARR") ? "Tarragon LP (Class B)" :
                                        record.getRecipient().getCode().startsWith("SING") ? "Singularity Ltd. (Class A)" :
                                                record.getRecipient().getCode().startsWith("TERRA") ? "Terra LP (Class C)" :
                                        record.getRecipient().getNameEn();

                                text = text.replace("INPUTENTITY", name);
                                r.setText(text, 0);
                            }else if (text != null && text.contains("INPUTAMOUNT")) {

                                text = text.replace("INPUTAMOUNT", String.format(Locale.ENGLISH, "$%,.2f", record.getAmount()));
                                r.setText(text, 0);
                            }else if (text != null && text.contains("TRANSACTIONTYPE")) {
                                text = text.replace("TRANSACTIONTYPE", transactionType);
                                r.setText(text, 0);
                            }else if (text != null && text.contains("DIRECTORNAME")) {
                                ReserveCalculationExportSignerType signerType =
                                        this.lookupService.findByTypeAndCode(ReserveCalculationExportSignerType.class, exportParamsDto.getDirector());
                                if(signerType != null) {
                                    text = text.replace("DIRECTORNAME", signerType.getNameEn());
                                    r.setText(text, 0);
                                }else{
                                    text = text.replace("DIRECTORNAME", "");
                                    r.setText(text, 0);
                                }

                            }else if (text != null && text.contains("<viza_1>")) {
                                if(exportParamsDto != null && exportParamsDto.getApproveList() != null && !exportParamsDto.getApproveList().isEmpty()){
                                    ReserveCalculationExportApproveListType approverType =
                                            this.lookupService.findByTypeAndCode(ReserveCalculationExportApproveListType.class, exportParamsDto.getApproveList().get(0));
                                    if(approverType != null) {
                                        text = text.replace("<viza_1>", approverType.getNameRu() + "________________");
                                        r.setText(text, 0);
                                    }else{
                                        text = text.replace("<viza_1>", "________________");
                                        r.setText(text, 0);
                                    }
                                }else{
                                    text = text.replace("<viza_1>", "");
                                    r.setText(text, 0);
                                }
                            }else if (text != null && text.contains("<viza_2>")) {
                                if(exportParamsDto != null && exportParamsDto.getApproveList() != null && exportParamsDto.getApproveList().size() > 1){
                                    ReserveCalculationExportApproveListType approverType =
                                            this.lookupService.findByTypeAndCode(ReserveCalculationExportApproveListType.class, exportParamsDto.getApproveList().get(1));
                                    if(approverType != null) {
                                        text = text.replace("<viza_2>", approverType.getNameRu() + "________________");
                                        r.setText(text, 0);
                                    }else{
                                        text = text.replace("<viza_2>", "________________");
                                        r.setText(text, 0);
                                    }
                                }else{
                                    text = text.replace("<viza_2>", "");
                                    r.setText(text, 0);
                                }
                            }else if (text != null && text.contains("<viza_3>")) {
                                if(exportParamsDto != null && exportParamsDto.getApproveList() != null && exportParamsDto.getApproveList().size() > 2){
                                    ReserveCalculationExportApproveListType approverType =
                                            this.lookupService.findByTypeAndCode(ReserveCalculationExportApproveListType.class, exportParamsDto.getApproveList().get(2));
                                    if(approverType != null) {
                                        text = text.replace("<viza_3>", approverType.getNameRu() + "________________");
                                        r.setText(text, 0);
                                    }else{
                                        text = text.replace("<viza_3>", "________________");
                                        r.setText(text, 0);
                                    }
                                }else{
                                    text = text.replace("<viza_3>", "");
                                    r.setText(text, 0);
                                }
                            }else if (text != null && text.contains("<doer>")) {
                                if(exportParamsDto != null && exportParamsDto.getDoer() != null){
                                    ReserveCalculationExportDoerType doerType =
                                            this.lookupService.findByTypeAndCode(ReserveCalculationExportDoerType.class, exportParamsDto.getDoer());
                                    if(doerType != null) {
                                        text = text.replace("<doer>", doerType.getNameRu());
                                        r.setText(text, 0);
                                    }else{
                                        text = text.replace("<doer>", "");
                                        r.setText(text, 0);
                                    }
                                }else{
                                    text = text.replace("<doer>", "");
                                    r.setText(text, 0);
                                }
                            }
                        }
                    }
                }

                File tmpDir = new File(this.rootDirectory + "/tmp/nbrk_reporting");

                // write to new
                String filePath = tmpDir + "/CC_ORDER_" + MathUtils.getRandomNumber(0, 10000) + ".docx";
                try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                    document.write(outputStream);
                }

                InputStream inputStream = new FileInputStream(filePath);
                filesDto.setInputStream(inputStream);
                filesDto.setFileName(filePath);
                return filesDto;
            }

        } catch (IOException e) {
            logger.error("IO Exception when exporting Capital Call Order", e);
            //e.printStackTrace();
        }

        return null;
    }
}
