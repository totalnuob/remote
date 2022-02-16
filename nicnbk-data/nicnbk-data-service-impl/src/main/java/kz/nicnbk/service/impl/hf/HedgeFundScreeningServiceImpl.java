package kz.nicnbk.service.impl.hf;

import kz.nicnbk.common.service.exception.ExcelFileParseException;
import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.common.service.util.*;
import kz.nicnbk.repo.api.hf.*;
import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.repo.model.files.Files;
import kz.nicnbk.repo.model.hf.*;
import kz.nicnbk.repo.model.lookup.BenchmarkLookup;
import kz.nicnbk.repo.model.lookup.CurrencyLookup;
import kz.nicnbk.repo.model.lookup.FileTypeLookup;
import kz.nicnbk.service.api.benchmark.BenchmarkService;
import kz.nicnbk.service.api.common.CurrencyRatesService;
import kz.nicnbk.service.api.email.EmailService;
import kz.nicnbk.service.api.employee.EmployeeService;
import kz.nicnbk.service.api.files.FileService;
import kz.nicnbk.service.api.hf.HedgeFundScoringService;
import kz.nicnbk.service.api.hf.HedgeFundScreeningService;
import kz.nicnbk.service.converter.hf.*;
import kz.nicnbk.service.dto.benchmark.BenchmarkValueDto;
import kz.nicnbk.service.dto.common.*;
import kz.nicnbk.service.dto.employee.EmployeeDto;
import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.hf.*;
import kz.nicnbk.service.dto.lookup.CurrencyRatesDto;
import org.apache.commons.lang.text.StrBuilder;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.io.*;
import java.util.*;

/**
 * Created by magzumov on 12.11.2018.
 */

@Service
public class HedgeFundScreeningServiceImpl implements HedgeFundScreeningService {

    private static final Logger logger = LoggerFactory.getLogger(HedgeFundScreeningServiceImpl.class);

    /* Root folder on the server */
    @Value("${filestorage.root.directory}")
    private String rootDirectory;

    @Autowired
    private HedgeFundScoringService scoringService;

    @Autowired
    private HedgeFundScreeningRepository screeningRepository;

    @Autowired
    private HedgeFundScreeningEntityConverter screeningEntityConverter;

    @Autowired
    private HedgeFundScreeningParsedDataEntityConverter parsedDataEntityConverter;

    @Autowired
    private HedgeFundScreeningParsedFundParamsDataEntityConverter parsedFundParamsEntityConverter;

    @Autowired
    private HedgeFundScreeningParsedUcitsDataEntityConverter parsedUcitsDataEntityConverter;

    @Autowired
    private HedgeFundScreeningParsedDataRepository parsedDataRepository;

    @Autowired
    private HedgeFundScreeningParsedParamDataRepository parsedFundParamDataRepository;

    @Autowired
    private HedgeFundScreeningEditedFundRepository editedFundRepository;

    @Autowired
    private HedgeFundScreeningAddedFundRepository addedFundRepository;

    @Autowired
    private HedgeFundScreeningAddedFundReturnRepository addedFundReturnRepository;

    @Autowired
    private HedgeFundScreeningParsedUcitsDataRepository parsedUcitsDataRepository;

    @Autowired
    private HedgeFundScreeningParsedDataReturnRepository parsedDataReturnRepository;

    @Autowired
    private HedgeFundScreeningParsedDataAUMRepository parsedDataAUMRepository;

    @Autowired
    private HedgeFundScreeningParsedUcitsDataAUMRepository parsedUcitsDataAUMRepository;

    @Autowired
    private HedgeFundScreeningParsedDataReturnEntityConverter parsedDataReturnEntityConverter;

    @Autowired
    private HedgeFundScreeningParsedDataAUMEntityConverter parsedDataAUMEntityConverter;

    @Autowired
    private HedgeFundScreeningParsedUcitsDataAUMEntityConverter parsedUcitsDataAUMEntityConverter;

    @Autowired
    private HedgeFundScreeningFilteredResultRepository filteredResultRepository;

    @Autowired
    private HedgeFundScreeningFilteredResultEntityConverter filteredResultEntityConverter;

    @Autowired
    private CurrencyRatesService currencyRatesService;

    @Autowired
    private FileService fileService;

    @Autowired
    private BenchmarkService benchmarkService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private HedgeFundScreeningSavedResultsRepository screeningSavedResultsRepository;

    @Autowired
    private HedgeFundScreeningSavedResultsAddedFundRepository screeningSavedResultsAddedFundRepository;

    @Autowired
    private HedgeFundScreeningSavedResultsAddedFundReturnRepository screeningSavedResultsAddedFundReturnRepository;

    @Autowired
    private HedgeFundScreeningSavedResultsEditedFundRepository screeningSavedResultsEditedFundRepository;

    @Autowired
    private HedgeFundScreeningSavedResultFundsRepository screeningSavedResultFundsRepository;

    @Autowired
    private HedgeFundScreeningSavedResultFundsEntityConverter screeningSavedResultFundsEntityConverter;

    @Autowired
    private HedgeFundScreeningSavedResultsCurrencyRepository screeningSavedResultsCurrencyRepository;

    @Autowired
    private HedgeFundScreeningSavedResultsCurrencyEntityConverter screeningSavedResultsCurrencyEntityConverter;

    @Autowired
    private HedgeFundScreeningSavedResultsBenchmarkRepository screeningSavedResultsBenchmarkRepository;

    @Autowired
    private HedgeFundScreeningSavedResultsBenchmarkEntityConverter screeningSavedResultsBenchmarkEntityConverter;



    /*** CRUD operations **********************************************************************************************/
    @Override
    public Long saveScreening(HedgeFundScreeningDto screeningDto, String username) {
        try {
            if(screeningDto.getId() == null){
                screeningDto.setCreator(username);
            }else{
                if(!checkScreeningEditable(screeningDto.getId())){
                    logger.error("Error saving HF Screening: screening is not editable [user]=" + username);
                    return null;
                }
                screeningDto.setUpdater(username);
            }
            HedgeFundScreening entity = this.screeningEntityConverter.assemble(screeningDto);

            if (entity != null) {
                if(screeningDto.getId() != null) {
                    HedgeFundScreening existingEntity = this.screeningRepository.findOne(screeningDto.getId());
                    if (existingEntity != null) {
                        entity.setCreator(existingEntity.getCreator());
                        entity.setDataFile(existingEntity.getDataFile());
                        entity.setUcitsFile(existingEntity.getUcitsFile());
                    }
                }

                Long id = this.screeningRepository.save(entity).getId();
                logger.info("Successfully saved HF Screening: id=" + id + " [user]=" + username);
                return id;
            }
        }catch (Exception ex){
            logger.error("Error saving HF Screening (with exception) [user]=" + username, ex);
        }
        return null;
    }

    @Override
    public HedgeFundScreeningDto getScreening(Long id) {
        if(id != null) {
            HedgeFundScreening entity = this.screeningRepository.findOne(id);
            if(entity != null){
                HedgeFundScreeningDto dto = this.screeningEntityConverter.disassemble(entity);

                // file
                if(entity.getDataFile() != null){
                    dto.setFileId(entity.getDataFile().getId());
                    dto.setFileName(entity.getDataFile().getFileName());
                }

                if(entity.getUcitsFile() != null){
                    dto.setUcitsFileId(entity.getUcitsFile().getId());
                    dto.setUcitsFileName(entity.getUcitsFile().getFileName());
                }
                if(entity.getParamsFile() != null){
                    dto.setParamsFileId(entity.getParamsFile().getId());
                    dto.setParamsFileName(entity.getParamsFile().getFileName());
                }

                // parsed data
                List<HedgeFundScreeningParsedDataDto> parsedData = getParsedDataFundInfo(id);
                if(parsedData != null && !parsedData.isEmpty()){
                    dto.setParsedData(parsedData);
                }

                // parsed data ucits
                List<HedgeFundScreeningParsedDataDto> parsedUcitsData = getParsedUcitsData(id);
                if(parsedUcitsData != null && !parsedUcitsData.isEmpty()){
                    dto.setParsedUcitsData(parsedUcitsData);
                }

                // Fund params data
                List<HedgeFundScreeningFundParamDataDto> fundParamData = getParsedFundParamData(id);
                if(fundParamData != null && !fundParamData.isEmpty()){
                    dto.setParsedFundParamData(fundParamData);
                }

                dto.setEditable(checkScreeningEditable(id));
                return dto;
            }
        }
        return null;
    }

    private boolean checkScreeningEditable(Long id){
        // check if all existing filters editable
        List<HedgeFundScreeningFilteredResultDto> filteredResults = getFilteredResultsByScreeningId(id);
        if(filteredResults != null && !filteredResults.isEmpty()){
            for(HedgeFundScreeningFilteredResultDto filteredResultDto: filteredResults){
                if(!checkFilteredResultEditable(filteredResultDto.getId())){
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public List<HedgeFundScreeningDto> getAllScreenings() {
        List<HedgeFundScreeningDto> screenings = new ArrayList<>();
        Iterator<HedgeFundScreening> entitiesIterator = this.screeningRepository.findAll().iterator();
        if(entitiesIterator != null){
            while(entitiesIterator.hasNext()){
                HedgeFundScreening entity = entitiesIterator.next();
                HedgeFundScreeningDto dto = this.screeningEntityConverter.disassemble(entity);
                dto.setEditable(checkScreeningEditable(entity.getId()));
                screenings.add(dto);
            }
        }
        return screenings;
    }

    @Override
    public List<HedgeFundScreeningParsedDataDateValueCombinedDto> searchParsedReturns(HedgeFundScreeningParsedDataDateValueSearchParamsDto searchParamsDto) {
        List<HedgeFundScreeningParsedDataDateValueCombinedDto> returns = new ArrayList<>();
        if(searchParamsDto != null && searchParamsDto.getScreeningId() != null) {
            Date dateFrom = null;
            Date dateTo = null;
            if(searchParamsDto.getNumberOfMonths() != null && searchParamsDto.getDate() != null){
                if(searchParamsDto.getNumberOfMonths().intValue() < 0) {
                    dateFrom = DateUtils.getLastDayOfCurrentMonth(DateUtils.moveDateByMonths(searchParamsDto.getDate(), searchParamsDto.getNumberOfMonths().intValue()));
                    dateTo = DateUtils.getLastDayOfCurrentMonth(DateUtils.moveDateByMonths(dateFrom, Math.abs(searchParamsDto.getNumberOfMonths().intValue())));
                }else{
                    dateTo = DateUtils.getLastDayOfCurrentMonth(DateUtils.moveDateByMonths(searchParamsDto.getDate(), searchParamsDto.getNumberOfMonths().intValue() + 1));
                    dateFrom = DateUtils.getLastDayOfCurrentMonth(DateUtils.moveDateByMonths(dateTo, 0 - searchParamsDto.getNumberOfMonths().intValue()));
                }
            }else{
                Date maxDate = this.parsedDataReturnRepository.getMaxDate(searchParamsDto.getScreeningId());
                if(maxDate != null) {
                    dateFrom = DateUtils.getLastDayOfCurrentMonth(DateUtils.moveDateByMonths(maxDate, (0 - 11)));
                    dateTo = DateUtils.getLastDayOfCurrentMonth(DateUtils.moveDateByMonths(dateFrom, 11));
                }else{
                   // no returns
                    return returns;
                }
            }

            Date[] dates = new Date[12];
            for(int i = 0; i < 12; i++){
                Date date = DateUtils.getLastDayOfCurrentMonth(DateUtils.moveDateByMonths(dateFrom, i));
                dates[i] = date;
            }

            List<HedgeFundScreeningParsedDataReturn> foundEntities =
                    this.parsedDataReturnRepository.findByScreeningIdAndDateRange(searchParamsDto.getScreeningId(), dateFrom, dateTo);

            if(foundEntities != null && !foundEntities.isEmpty()){
                Map<Long, HedgeFundScreeningParsedDataDateValueCombinedDto> combinedMap = new HashMap<>();
                for(HedgeFundScreeningParsedDataReturn entity: foundEntities){
                    HedgeFundScreeningParsedDataDateValueCombinedDto returnDto = null;
                    if(combinedMap.get(entity.getFundId()) != null){
                        returnDto = combinedMap.get(entity.getFundId());
                    }else{
                        returnDto = new HedgeFundScreeningParsedDataDateValueCombinedDto();
                        returnDto.setFundId(entity.getFundId());
                        returnDto.setFundName(entity.getFundName());
                        returnDto.setReturnsCurrency(entity.getReturnsCurrency());
                        returnDto.setDates(dates);
                        returnDto.setValues(new Double[12]);
                        combinedMap.put(entity.getFundId(), returnDto);
                    }

                    for(int i = 0; i < 12; i++){
                        if(returnDto.getDates()[i] != null && entity.getDate() != null &&
                                DateUtils.getYear(returnDto.getDates()[i]) == DateUtils.getYear(entity.getDate()) &&
                                DateUtils.getMonth(returnDto.getDates()[i]) == DateUtils.getMonth(entity.getDate())){
                            // same date
                            returnDto.getValues()[i] = entity.getValue();
                            break;
                        }
                    }
                }

                List<HedgeFundScreeningParsedDataDto> parsedData = getParsedDataFundInfo(searchParamsDto.getScreeningId());
                if(parsedData != null){
                    for(HedgeFundScreeningParsedDataDto parsedDataDto: parsedData){
                        HedgeFundScreeningParsedDataDateValueCombinedDto dto = new HedgeFundScreeningParsedDataDateValueCombinedDto();
                        dto.setFundId(parsedDataDto.getFundId());
                        dto.setFundName(parsedDataDto.getFundName());
                        if(combinedMap.get(dto.getFundId()) != null){
                            dto.setValues(combinedMap.get(dto.getFundId()).getValues());
                            dto.setReturnsCurrency(combinedMap.get(dto.getFundId()).getReturnsCurrency());
                        }
                        dto.setDates(dates);
                        returns.add(dto);
                    }
                }

                Collections.sort(returns);
            }
        }
        return returns;
    }

    @Override
    public List<HedgeFundScreeningParsedDataDateValueCombinedDto> searchParsedAUMS(HedgeFundScreeningParsedDataDateValueSearchParamsDto searchParamsDto) {
        List<HedgeFundScreeningParsedDataDateValueCombinedDto> returns = new ArrayList<>();
        if(searchParamsDto != null && searchParamsDto.getScreeningId() != null) {
            Date dateFrom = null;
            Date dateTo = null;
            if(searchParamsDto.getNumberOfMonths() != null && searchParamsDto.getDate() != null){
                if(searchParamsDto.getNumberOfMonths().intValue() < 0) {
                    dateFrom = DateUtils.getLastDayOfCurrentMonth(DateUtils.moveDateByMonths(searchParamsDto.getDate(), searchParamsDto.getNumberOfMonths().intValue()));
                    dateTo = DateUtils.getLastDayOfCurrentMonth(DateUtils.moveDateByMonths(dateFrom, Math.abs(searchParamsDto.getNumberOfMonths().intValue())));
                }else{
                    dateTo = DateUtils.getLastDayOfCurrentMonth(DateUtils.moveDateByMonths(searchParamsDto.getDate(), searchParamsDto.getNumberOfMonths().intValue() + 1));
                    dateFrom = DateUtils.getLastDayOfCurrentMonth(DateUtils.moveDateByMonths(dateTo, 0 - searchParamsDto.getNumberOfMonths().intValue()));
                }
            }else{
                Date maxDate = this.parsedDataAUMRepository.getMaxDate(searchParamsDto.getScreeningId());
                if(maxDate != null) {
                    dateFrom = DateUtils.getLastDayOfCurrentMonth(DateUtils.moveDateByMonths(maxDate, (0 - 11)));
                    dateTo = DateUtils.getLastDayOfCurrentMonth(DateUtils.moveDateByMonths(dateFrom, 11));
                }else{
                    // no returns
                    return returns;
                }
            }

            Date[] dates = new Date[12];
            for(int i = 0; i < 12; i++){
                Date date = DateUtils.getLastDayOfCurrentMonth(DateUtils.moveDateByMonths(dateFrom, i));
                dates[i] = date;
            }

            List<HedgeFundScreeningParsedDataAUM> foundEntities =
                    this.parsedDataAUMRepository.findByScreeningIdAndDateRange(searchParamsDto.getScreeningId(), dateFrom, dateTo, new Sort(Sort.Direction.DESC, "fundId", "date"));

            if(foundEntities != null && !foundEntities.isEmpty()){
                Map<Long, HedgeFundScreeningParsedDataDateValueCombinedDto> combinedMap = new HashMap<>();
                for(HedgeFundScreeningParsedDataAUM entity: foundEntities){
                    HedgeFundScreeningParsedDataDateValueCombinedDto returnDto = null;
                    if(combinedMap.get(entity.getFundId()) != null){
                        returnDto = combinedMap.get(entity.getFundId());
                    }else{
                        returnDto = new HedgeFundScreeningParsedDataDateValueCombinedDto();
                        returnDto.setFundId(entity.getFundId());
                        returnDto.setFundName(entity.getFundName());
                        returnDto.setReturnsCurrency(entity.getReturnsCurrency());
                        returnDto.setDates(dates);
                        returnDto.setValues(new Double[12]);
                        combinedMap.put(entity.getFundId(), returnDto);
                    }

                    for(int i = 0; i < 12; i++){
                        if(returnDto.getDates()[i] != null && entity.getDate() != null &&
                                DateUtils.getYear(returnDto.getDates()[i]) == DateUtils.getYear(entity.getDate()) &&
                                DateUtils.getMonth(returnDto.getDates()[i]) == DateUtils.getMonth(entity.getDate())){
                            // same date
                            returnDto.getValues()[i] = entity.getValue();
                            break;
                        }
                    }
                }

                List<HedgeFundScreeningParsedDataDto> parsedData = getParsedDataFundInfo(searchParamsDto.getScreeningId());
                if(parsedData != null){
                    for(HedgeFundScreeningParsedDataDto parsedDataDto: parsedData){
                        HedgeFundScreeningParsedDataDateValueCombinedDto dto = new HedgeFundScreeningParsedDataDateValueCombinedDto();
                        dto.setFundId(parsedDataDto.getFundId());
                        dto.setFundName(parsedDataDto.getFundName());
                        if(combinedMap.get(dto.getFundId()) != null){
                            dto.setValues(combinedMap.get(dto.getFundId()).getValues());
                        }
                        dto.setDates(dates);
                        returns.add(dto);
                    }
                }

                Collections.sort(returns);
            }
        }
        return returns;
    }

    @Override
    public List<HedgeFundScreeningParsedDataDateValueCombinedDto> searchParsedUcitsAUMS(HedgeFundScreeningParsedDataDateValueSearchParamsDto searchParamsDto) {
        List<HedgeFundScreeningParsedDataDateValueCombinedDto> aums = new ArrayList<>();
        if(searchParamsDto != null && searchParamsDto.getScreeningId() != null) {
            Date dateFrom = null;
            Date dateTo = null;
            if(searchParamsDto.getNumberOfMonths() != null && searchParamsDto.getDate() != null){
                if(searchParamsDto.getNumberOfMonths().intValue() < 0) {
                    dateFrom = DateUtils.moveDateByMonths(searchParamsDto.getDate(), searchParamsDto.getNumberOfMonths().intValue());
                    dateTo = DateUtils.moveDateByMonths(dateFrom, Math.abs(searchParamsDto.getNumberOfMonths().intValue()));
                }else{
                    dateTo = DateUtils.moveDateByMonths(searchParamsDto.getDate(), searchParamsDto.getNumberOfMonths().intValue() + 1);
                    dateFrom = DateUtils.moveDateByMonths(dateTo, 0 - searchParamsDto.getNumberOfMonths().intValue());
                }
            }else{
                Date maxDate = this.parsedUcitsDataAUMRepository.getMaxDate(searchParamsDto.getScreeningId());
                if(maxDate != null) {
                    dateFrom = DateUtils.moveDateByMonths(maxDate, (0 - 11));
                    dateTo = DateUtils.moveDateByMonths(dateFrom, 11);
                }else{
                    // no returns
                    return aums;
                }
            }

            Date[] dates = new Date[12];
            for(int i = 0; i < 12; i++){
                Date date = DateUtils.moveDateByMonths(dateFrom, i);
                dates[i] = date;
            }

            List<HedgeFundScreeningParsedUcitsDataAUM> foundEntities =
                    this.parsedUcitsDataAUMRepository.findByScreeningIdAndDateRange(searchParamsDto.getScreeningId(), dateFrom, dateTo, new Sort(Sort.Direction.DESC, "fundId", "date"));

            if(foundEntities != null && !foundEntities.isEmpty()){
                Map<Long, HedgeFundScreeningParsedDataDateValueCombinedDto> combinedMap = new HashMap<>();
                for(HedgeFundScreeningParsedUcitsDataAUM entity: foundEntities){
                    HedgeFundScreeningParsedDataDateValueCombinedDto returnDto = null;
                    if(combinedMap.get(entity.getFundId()) != null){
                        returnDto = combinedMap.get(entity.getFundId());
                    }else{
                        returnDto = new HedgeFundScreeningParsedDataDateValueCombinedDto();
                        returnDto.setFundId(entity.getFundId());
                        returnDto.setFundName(entity.getFundName());
                        returnDto.setReturnsCurrency(entity.getReturnsCurrency());
                        returnDto.setDates(dates);
                        returnDto.setValues(new Double[12]);
                        combinedMap.put(entity.getFundId(), returnDto);
                    }

                    for(int i = 0; i < 12; i++){
                        if(returnDto.getDates()[i] != null && entity.getDate() != null &&
                                DateUtils.getYear(returnDto.getDates()[i]) == DateUtils.getYear(entity.getDate()) &&
                                DateUtils.getMonth(returnDto.getDates()[i]) == DateUtils.getMonth(entity.getDate())){
                            // same date
                            returnDto.getValues()[i] = entity.getValue();
                            break;
                        }
                    }
                }

                List<HedgeFundScreeningParsedDataDto> parsedUcitsData = getParsedUcitsData(searchParamsDto.getScreeningId());
                if(parsedUcitsData != null){
                    for(HedgeFundScreeningParsedDataDto parsedDataDto: parsedUcitsData){
                        HedgeFundScreeningParsedDataDateValueCombinedDto dto = new HedgeFundScreeningParsedDataDateValueCombinedDto();
                        dto.setFundId(parsedDataDto.getFundId());
                        dto.setFundName(parsedDataDto.getFundName());
                        if(combinedMap.get(dto.getFundId()) != null){
                            dto.setValues(combinedMap.get(dto.getFundId()).getValues());
                        }
                        dto.setDates(dates);
                        aums.add(dto);
                    }
                }

                Collections.sort(aums);
            }
        }
        return aums;
    }

    @Override
    public HedgeFundScreeningPagedSearchResult searchScreenings(HedgeFundScreeningSearchParams searchParams) {
        try {
            Page<HedgeFundScreening> entitiesPage = null;
            int page = searchParams.getPage() > 0 ? searchParams.getPage() - 1 : 0;

            entitiesPage = screeningRepository.search(searchParams.getDateFromNonEmpty(), searchParams.getDateToNonEmpty(), searchParams.getSearchTextLowerCase(),
                    new PageRequest(page, searchParams.getPageSize(), new Sort(Sort.Direction.DESC, "date", "name")));

            HedgeFundScreeningPagedSearchResult result = new HedgeFundScreeningPagedSearchResult();
            if (entitiesPage != null) {
                result.setTotalElements(entitiesPage.getTotalElements());
                if (entitiesPage.getTotalElements() > 0) {
                    result.setShowPageFrom(PaginationUtils.getShowPageFrom(DEFAULT_PAGES_PER_VIEW, page));
                    result.setShowPageTo(PaginationUtils.getShowPageTo(DEFAULT_PAGES_PER_VIEW,
                            page, result.getShowPageFrom(), entitiesPage.getTotalPages()));
                }
                result.setTotalPages(entitiesPage.getTotalPages());
                result.setCurrentPage(page + 1);
                if (searchParams != null) {
                    result.setSearchParams(searchParams.getSearchParamsAsString());
                }
                result.setScreenings(this.screeningEntityConverter.disassembleList(entitiesPage.getContent()));
                for(HedgeFundScreeningDto screening: result.getScreenings()){
                    screening.setEditable(checkScreeningEditable(screening.getId()));
                    List<HedgeFundScreeningFilteredResultDto> filters = getFilteredResultsByScreeningId(screening.getId());
                    screening.setExistingFilteredResults(filters != null ? filters.size() : 0);
                }

                //Collections.sort(result.getScreenings());
            }
            return result;
        }catch(Exception ex){
            logger.error("Error searching HF Screenings", ex);
        }
        return null;
    }

    @Override
    public FileUploadResultDto saveAndParseAttachmentDataFile(Long screeningId, FilesDto filesDto, String username) {
        try {

            // TODO: check permissions? editable screening?
            if(!checkScreeningEditable(screeningId)){
                logger.error("Error saving HF Screening data file: screening is not editable [user]=" + username);
                return null;
            }

            FileUploadResultDto fileUploadResultDto = new FileUploadResultDto();

            if (filesDto != null && filesDto.getId() == null) {
                Long fileId = fileService.save(filesDto, FileTypeLookup.HF_SCREENING_DATA_FILE.getCatalog()); // CATALOG IS ONE
                if(fileId == null){
                    logger.error("Saved HF Screening attachment file: file save return null for file id");
                    return null;
                }
                logger.info("Saved HF Screening attachment file: file id=" + fileId);

                HedgeFundScreening entity = this.screeningRepository.findOne(screeningId);
                entity.setDataFile(new Files(fileId));
                entity.setUpdateDate(new Date());
                EmployeeDto updater = this.employeeService.findByUsername(username);
                if(updater != null){
                    entity.setUpdater(new Employee(updater.getId()));
                }
                this.screeningRepository.save(entity);

                logger.info("Saved HF Screening attachment file information to DB: file id=" + fileId + " [user]=" + username);

                filesDto.setId(fileId);

                // Parse file
                fileUploadResultDto = parseDataFile(filesDto, screeningId);

                if(fileUploadResultDto.getStatus() == ResponseStatusType.SUCCESS){
                    fileUploadResultDto.setFileId(fileId);
                    fileUploadResultDto.setFileName(filesDto.getFileName());
                }else{

//                    // delete file
//                    boolean deleted = fileService.delete(fileId);
//                    if (!deleted) {
//                        String errorMessage = "Error occurred when parsing file. When cancelling file upload, failed to delete file.";
//                        logger.error(errorMessage);
//                        fileUploadResultDto.setErrorMessageEn(fileUploadResultDto.getMessage().getNameEn() + " " + errorMessage);
//                    }
                }
            }
            return fileUploadResultDto;
        }catch (Exception ex){
            logger.error("Error saving HF Screening attachments", ex);
        }
        return null;
    }

    @Override
    public void sendEmailNotificationForAudit(Long screeningId, FilesDto filesDto, String username) {
        try {
            HedgeFundScreening entity = this.screeningRepository.findOne(screeningId);
            String screeningName = entity.getName();
            String fileName = filesDto.getFileName();

            emailService.sendHtmlMail("aitmyrza@nicnbk.kz", "UNIC Notification – HF Screening/Scoring file upload",
                    "<p>Data file " + "<u><b>" + fileName + "</b></u>" + " has been uploaded for screening  " + "<u><b>" + screeningName + "</b></u>" +
                            " by user " + "<u><b>" + username + "</b></u>");
        } catch (Exception ex) {
            logger.error("Error sending notification email");
        }
    }

    @Override
    public FileUploadResultDto saveAttachmentUcitsFile(Long screeningId, FilesDto filesDto, String username) {
        try {

            // TODO: check permissions? editable screening?

            FileUploadResultDto fileUploadResultDto = new FileUploadResultDto();

            if (filesDto != null && filesDto.getId() == null) {
                Long fileId = fileService.save(filesDto, FileTypeLookup.HF_SCREENING_UCITS_FILE.getCatalog()); // CATALOG IS ONE
                if(fileId == null){
                    logger.error("Saved HF Screening Ucits file: file save return null for file id");
                    return null;
                }
                logger.info("Saved HF Screening Ucits file: file id=" + fileId);

                HedgeFundScreening entity = this.screeningRepository.findOne(screeningId);
                entity.setUcitsFile(new Files(fileId));
                this.screeningRepository.save(entity);

                logger.info("Saved HF Screening Ucits file information to DB: file id=" + fileId + " [user]=" + username);

                // Parse file
                fileUploadResultDto = parseDataFile(filesDto, screeningId);

                if(fileUploadResultDto.getStatus() == ResponseStatusType.SUCCESS){
                    fileUploadResultDto.setFileId(fileId);
                    fileUploadResultDto.setFileName(filesDto.getFileName());
                }else{
                    // delete file
                    boolean deleted = fileService.delete(fileId);
                    if (!deleted) {
                        String errorMessage = "Error occurred when parsing file. When cancelling file upload, failed to delete file.";
                        logger.error(errorMessage);
                        fileUploadResultDto.setErrorMessageEn(fileUploadResultDto.getMessage().getNameEn() + " " + errorMessage);
                    }
                }
            }
            return fileUploadResultDto;
        }catch (Exception ex){
            logger.error("Error saving HF Screening attachments", ex);
        }
        return null;
    }

    @Override
    public FileUploadResultDto saveFundParamsFile(Long screeningId, FilesDto filesDto, String username) {
        try {

            // TODO: check permissions? editable screening?

            FileUploadResultDto fileUploadResultDto = new FileUploadResultDto();

            if (filesDto != null && filesDto.getId() == null) {
                Long fileId = fileService.save(filesDto, FileTypeLookup.HF_SCREENING_PARAMS_FILE.getCatalog()); // CATALOG IS ONE
                if(fileId == null){
                    logger.error("Saved HF Screening Params file: file save return null for file id");
                    return null;
                }
                logger.info("Saved HF Screening Params file: file id=" + fileId);

                HedgeFundScreening entity = this.screeningRepository.findOne(screeningId);
                entity.setParamsFile(new Files(fileId));
                this.screeningRepository.save(entity);

                logger.info("Saved HF Screening Params file information to DB: file id=" + fileId + " [user]=" + username);

                // Parse file
                fileUploadResultDto = parseFundParamsFile(filesDto, screeningId);

                if(fileUploadResultDto.getStatus() == ResponseStatusType.SUCCESS){
                    fileUploadResultDto.setFileId(fileId);
                    fileUploadResultDto.setFileName(filesDto.getFileName());
                }else{
                    // delete file
                    boolean deleted = fileService.delete(fileId);
                    if (!deleted) {
                        String errorMessage = "Error occurred when parsing file. When cancelling file upload, failed to delete file.";
                        logger.error(errorMessage);
                        fileUploadResultDto.setErrorMessageEn(fileUploadResultDto.getMessage().getNameEn() + " " + errorMessage);
                    }
                }
            }
            return fileUploadResultDto;
        }catch (Exception ex){
            logger.error("Error saving HF Screening Fund params file", ex);
        }
        return null;
    }

    @Override
    public List<HedgeFundScreeningFilteredResultDto> getFilteredResultsByScreeningId(Long screeningId) {
        List<HedgeFundScreeningFilteredResult> entities = filteredResultRepository.findByScreeningId(screeningId);
        if(entities != null){
            List<HedgeFundScreeningFilteredResultDto> results = this.filteredResultEntityConverter.disassembleList(entities);
            for(HedgeFundScreeningFilteredResultDto dto: results){
                dto.setEditable(checkFilteredResultEditable(dto.getId()));
            }
            return results;
        }
        return new ArrayList<HedgeFundScreeningFilteredResultDto>();

    }

    @Override
    public Long saveFilters(HedgeFundScreeningFilteredResultDto dto, String username) {
        try {
            if(dto.getId() == null){
                dto.setCreator(username);
            }else{
                if(!checkFilteredResultEditable(dto.getId())){
                    logger.error("Error saving filtered result: not editable [user]=" + username);
                    return null;
                }
                dto.setUpdater(username);
                dto.setCreator(null);
            }
            HedgeFundScreeningFilteredResult entity = this.filteredResultEntityConverter.assemble(dto);

            if (entity != null) {
                Long id = this.filteredResultRepository.save(entity).getId();

                HedgeFundScreening screening = this.screeningRepository.findOne(dto.getScreeningId());
                if(screening != null) {
                    EmployeeDto updater = this.employeeService.findByUsername(username);
                    if (updater != null) {
                        screening.setUpdateDate(new Date());
                        screening.setUpdater(new Employee(updater.getId()));
                        this.screeningRepository.save(screening);
                    }
                }
                logger.info("Successfully saved HF Screening filtered result: id=" + id + " [user]=" + username);
                return id;
            }
        }catch (Exception ex){
            logger.error("Error saving HF Screening filtered result with exception) [user]=" + username, ex);
        }
        return null;
    }

    @Override
    public HedgeFundScreeningFilteredResultDto getFilteredResultWithFundsInfo(Long id) {
        HedgeFundScreeningFilteredResultDto filteredResultDto = getFilteredResultWithoutFundsInfo(id);
        if(filteredResultDto != null){
            // result statistics
            HedgeFundScreeningFilteredResultStatisticsDto statisticsDto = getFilteredResultStatistics(filteredResultDto);
            filteredResultDto.setFilteredResultStatistics(statisticsDto);

            // added funds
            List<HedgeFundScreeningParsedDataDto> addedFunds = getAddedFundsByFilteredResultId(id);
            filteredResultDto.setAddedFunds(addedFunds);

            // edited funds
            List<HedgeFundScreeningParsedDataDto> editedFunds = getEditedIncludedFunds(id);
            filteredResultDto.setEditedFunds(editedFunds);


            //excluded funds
            List<HedgeFundScreeningParsedDataDto> excludedFunds = getExcludedFunds(filteredResultDto.getId());
            filteredResultDto.setExcludedFunds(excludedFunds);

            // get auto-excluded funds
            List<HedgeFundScreeningParsedDataDto> autoExcludedFunds = getAutoExcludedFunds(filteredResultDto.getScreeningId(), id);
            filteredResultDto.setAutoExcludedFunds(autoExcludedFunds);

            return filteredResultDto;
        }

        return null;
    }

    private boolean checkFilteredResultEditable(Long id){
        return !hasSavedResultsByFilterId(id);
    }

    @Override
    public HedgeFundScreeningFilteredResultDto getFilteredResultWithoutFundsInfo(Long id) {
        HedgeFundScreeningFilteredResult entity = this.filteredResultRepository.findOne(id);
        if(entity != null){
            HedgeFundScreeningFilteredResultDto dto =  this.filteredResultEntityConverter.disassemble(entity);
            dto.setEditable(checkFilteredResultEditable(id));
            return dto;
        }

        return null;
    }

    private List<HedgeFundScreeningParsedDataDto> getToCheckFundsByFilteredResultId(Long screeningId, Long filteredResultId){
        List<HedgeFundScreeningParsedDataDto> toCheckFunds = new ArrayList<>();
        List<HedgeFundScreeningParsedDataDto> excludedFunds = getExcludedFunds(filteredResultId);
        List<HedgeFundScreeningParsedDataDto> fundList = getParsedDataFundInfo(screeningId);
        if(fundList != null){
            for(HedgeFundScreeningParsedDataDto fund: fundList){
                if(fund.getFundName() == null || fund.getFundName().toUpperCase().contains("SICAV") ||
                        fund.getFundName().toUpperCase().contains("UCITS")){
                    toCheckFunds.add(fund);
                }
            }
        }
        if(excludedFunds != null && !excludedFunds.isEmpty()){
            List<HedgeFundScreeningParsedDataDto> withoutExcluded = new ArrayList<>();
            for(HedgeFundScreeningParsedDataDto fund: toCheckFunds){
                boolean excluded = false;
                for(HedgeFundScreeningParsedDataDto excludedFund: excludedFunds){
                    if(fund.getFundName() != null && excludedFund.getFundName() != null &&
                            fund.getFundName().equalsIgnoreCase(excludedFund.getFundName())){
                        excluded = true;
                        break;
                    }
                }
                if(!excluded){
                    withoutExcluded.add(fund);
                }
            }
            toCheckFunds = withoutExcluded;
        }
        return toCheckFunds;
    }

    private List<HedgeFundScreeningParsedDataDto> getAddedFundsByFilteredResultId(Long filteredResultId){
        List<HedgeFundScreeningParsedDataDto> fundList = new ArrayList<>();
        List<HedgeFundScreeningAddedFund> addedFunds = this.addedFundRepository.findByFilteredResultId(filteredResultId, new Sort(Sort.Direction.ASC, "fundName"));
        if(addedFunds != null){
            for(HedgeFundScreeningAddedFund fund: addedFunds){
                HedgeFundScreeningParsedDataDto fundDto = new HedgeFundScreeningParsedDataDto();
                fundDto.setFilteredResultId(filteredResultId);
                fundDto.setFundId(fund.getFundId());
                fundDto.setFundName(fund.getFundName());
                fundDto.setInvestmentManager(fund.getInvestmentManager());
                fundDto.setMainStrategy(fund.getMainStrategy());
                fundDto.setFundAUM(fund.getFundAUM());
                fundDto.setFundAUMComment(fund.getFundAUMComment());
                fundDto.setManagerAUM(fund.getManagerAUM());
                fundDto.setAdded(true);
                // fund returns
                List<HedgeFundScreeningAddedFundReturn> returns = this.addedFundReturnRepository.findByFundId(fund.getId(), new Sort(Sort.Direction.ASC, "date"));
                if(returns != null) {
                    List<HedgeFundScreeningFundReturnDto> returnDtoList = new ArrayList<>();
                    for(HedgeFundScreeningAddedFundReturn returnEntity: returns){
                        HedgeFundScreeningFundReturnDto returnDto = new HedgeFundScreeningFundReturnDto();
                        returnDto.setDate(DateUtils.getMM_YYYYYFormatDate(returnEntity.getDate()));
                        returnDto.setValue(returnEntity.getValue());
                        returnDtoList.add(returnDto);
                    }
                    fundDto.setReturns(returnDtoList);
                }
                fundList.add(fundDto);
            }
        }
        return fundList;
    }

    /******************************************************************************************************************/


    /*** Parsing input file *******************************************************************************************/

    private FileUploadResultDto parseDataFile(FilesDto filesDto, Long screeningId){
        FileUploadResultDto resultDtoSheet1 = parseDataFileSheet1(filesDto, screeningId);
        if(resultDtoSheet1.getStatus() == ResponseStatusType.FAIL){
            deleteFileAndAssociation(filesDto.getId(), screeningId, filesDto.getType());
            deleteParsedDataSheet1(screeningId, filesDto.getType());
            return resultDtoSheet1;
        }

        if(filesDto.getType().equalsIgnoreCase(FileTypeLookup.HF_SCREENING_DATA_FILE.getCode())){
            FileUploadResultDto resultDtoSheet2 = parseDataFileSheet2(filesDto, screeningId);
            if(resultDtoSheet2.getStatus() == ResponseStatusType.FAIL){
                deleteFileAndAssociation(filesDto.getId(), screeningId, filesDto.getType());
                deleteParsedDataSheet2(screeningId);
                return resultDtoSheet2;
            }
        }


        FileUploadResultDto resultDtoSheet3 = parseDataFileSheet3(filesDto, screeningId);
        if(resultDtoSheet3.getStatus() == ResponseStatusType.FAIL){
            deleteFileAndAssociation(filesDto.getId(), screeningId, filesDto.getType());
            deleteParsedDataSheet3(screeningId, filesDto.getType());
            return resultDtoSheet3;
        }

        return resultDtoSheet1;
    }

    private FileUploadResultDto parseFundParamsFile(FilesDto filesDto, Long screeningId){
        FileUploadResultDto resultDto = new FileUploadResultDto();
        List<HedgeFundScreeningFundParamDataDto> records = new ArrayList<>();
        Iterator<Row> rowIterator = null;
        try {
            rowIterator = ExcelUtils.getRowIterator(filesDto.getBytes(), filesDto.getFileName(), 0);
        }catch (ExcelFileParseException ex){
            String errorMessage = "Error parsing params file." + ex.getMessage();
            logger.error(errorMessage, ex);
            resultDto.setErrorMessageEn(errorMessage);
        }
        if(rowIterator != null) {
            int rowNum = 0;
            Map<String, Integer> headers = new HashMap<>();
            while (rowIterator.hasNext()) { // each row
                Row row = rowIterator.next();
                if(rowNum == 0){
                    // First row
                    Iterator<Cell> cellIterator = row.cellIterator();
                    while(cellIterator != null && cellIterator.hasNext()){
                        Cell cell = cellIterator.next();
                        headers.put(cell.getStringCellValue(), cell.getColumnIndex());
                    }
                }else{
                    HedgeFundScreeningFundParamDataDto record = new HedgeFundScreeningFundParamDataDto();
                    record.setScreening(new HedgeFundScreeningDto(screeningId));

                    if(headers.get("Fund Name") != null){
                        String value = ExcelUtils.getStringValueFromCell(row.getCell(headers.get("Fund Name").intValue()));
                        record.setFundName(value);
                    }
                    if(headers.get("Omega") != null){
                        Double value = ExcelUtils.getDoubleValueFromCell(row.getCell(headers.get("Omega").intValue()));
                        record.setOmega(value);
                    }
                    if(headers.get("AnnR") != null){
                        Double value = ExcelUtils.getDoubleValueFromCell(row.getCell(headers.get("AnnR").intValue()));
                        record.setAnnualizedReturn(value);
                    }
                    if(headers.get("Alpha") != null){
                        Double value = ExcelUtils.getDoubleValueFromCell(row.getCell(headers.get("Alpha").intValue()));
                        record.setAlpha(value);
                    }
                    if(headers.get("Beta") != null){
                        Double value = ExcelUtils.getDoubleValueFromCell(row.getCell(headers.get("Beta").intValue()));
                        record.setBeta(value);
                    }
                    if(headers.get("Sortino") != null){
                        Double value = ExcelUtils.getDoubleValueFromCell(row.getCell(headers.get("Sortino").intValue()));
                        record.setSortino(value);
                    }
                    if(headers.get("cfVar") != null){
                        Double value = ExcelUtils.getDoubleValueFromCell(row.getCell(headers.get("cfVar").intValue()));
                        record.setCfVar(value);
                    }
                    if(!record.isEmpty()){
                        records.add(record);
                    }
                }
                rowNum++;
            }

        }

        if(!records.isEmpty()){
            if(filesDto.getType() != null && filesDto.getType().equalsIgnoreCase(FileTypeLookup.HF_SCREENING_PARAMS_FILE.getCode())) {
                List<HedgeFundScreeningParsedParamData> entities = this.parsedFundParamsEntityConverter.assembleList(records);
                if (entities != null && !entities.isEmpty()) {
                    try {
                        //TODO: check parsed data?????

                        // delete parsed data
                        deleteParsedFundParamsData(screeningId);

                        this.parsedFundParamDataRepository.save(entities);
                        resultDto.setStatus(ResponseStatusType.SUCCESS);
                        logger.info("Successfully saved parsed HF Screening fund params data (sheet 1) to database: screening id= " + screeningId);
                    } catch (Exception ex) {
                        String errorMessage = "Error saving HF Screening fund params data sheet 1) to database: screening id=" + screeningId;
                        logger.error(errorMessage, ex);
                        resultDto.setErrorMessageEn(errorMessage);
                    }
                }
            }
        }

        return resultDto;
    }

    private FileUploadResultDto deleteFileAndAssociation(Long fileId, Long screeningId, String fileType){
        FileUploadResultDto resultDtoSheet = new FileUploadResultDto();
        try{
            HedgeFundScreening entity = this.screeningRepository.findOne(screeningId);
            if(fileType.equalsIgnoreCase(FileTypeLookup.HF_SCREENING_DATA_FILE.getCode())) {
                entity.setDataFile(null);
            }else if(fileType.equalsIgnoreCase(FileTypeLookup.HF_SCREENING_UCITS_FILE.getCode())) {
                entity.setUcitsFile(null);
            }else if(fileType.equalsIgnoreCase(FileTypeLookup.HF_SCREENING_PARAMS_FILE.getCode())) {
                entity.setParamsFile(null);
            }

            this.screeningRepository.save(entity);
        }catch (Exception ex){
            String errorMessage = resultDtoSheet.getMessage().getMessageText() + " " +
                    "Failed unset file for screening.";
            logger.error(errorMessage);
            resultDtoSheet.setErrorMessageEn(errorMessage);
            return resultDtoSheet;
        }

        boolean deleted = this.fileService.delete(fileId);
        if(!deleted){
            String errorMessage = resultDtoSheet.getMessage().getMessageText() + " " +
                    "Failed to delete file.";
            logger.error(errorMessage);
            resultDtoSheet.setErrorMessageEn(errorMessage);
            return resultDtoSheet;
        }else{
            logger.info("Data file successfully deleted: fileid=" + fileId.longValue() + ", screening id=" + screeningId.longValue());
            resultDtoSheet.setStatus(ResponseStatusType.SUCCESS);
            return resultDtoSheet;
        }
    }

    private List<HedgeFundScreeningParsedDataDto> getParsedDataFundInfo(Long screeningId){
        List<HedgeFundScreeningParsedData> entities = this.parsedDataRepository.findByScreeningId(screeningId, new Sort(Sort.Direction.ASC, "fundId"));
        if(entities != null){
            return parsedDataEntityConverter.disassembleList(entities);
        }
        return null;
    }

    private List<HedgeFundScreeningParsedDataDto> getParsedUcitsData(Long screeningId){
        List<HedgeFundScreeningParsedUcitsData> entities = this.parsedUcitsDataRepository.findByScreeningId(screeningId, new Sort(Sort.Direction.ASC, "fundId"));
        if(entities != null){
            return parsedUcitsDataEntityConverter.disassembleList(entities);
        }
        return null;
    }

    private List<HedgeFundScreeningFundParamDataDto> getParsedFundParamData(Long screeningId){
        List<HedgeFundScreeningFundParamDataDto> records = new ArrayList<>();
        List<HedgeFundScreeningParsedParamData> entities = this.parsedFundParamDataRepository.findByScreeningId(screeningId, new Sort(Sort.Direction.ASC, "fundName"));
        if(entities != null){
            records = this.parsedFundParamsEntityConverter.disassembleList(entities);
            return records;
        }
        return null;
    }

//    private FileUploadResultDto deleteUcitsFileAndAssociation(Long fileId, Long screeningId){
//        FileUploadResultDto resultDtoSheet = new FileUploadResultDto();
//        try{
//            HedgeFundScreening entity = this.screeningRepository.findOne(screeningId);
//            entity.setUcitsFile(null);
//            this.screeningRepository.save(entity);
//        }catch (Exception ex){
//            String errorMessage = resultDtoSheet.getMessage().getMessageText() + " " +
//                    "Failed unset ucits file for screening.";
//            logger.error(errorMessage);
//            resultDtoSheet.setErrorMessageEn(errorMessage);
//            return resultDtoSheet;
//        }
//
//        boolean deleted = this.fileService.delete(fileId);
//        if(!deleted){
//            String errorMessage = resultDtoSheet.getMessage().getMessageText() + " " +
//                    "Failed to delete ucits file.";
//            logger.error(errorMessage);
//            resultDtoSheet.setErrorMessageEn(errorMessage);
//            return resultDtoSheet;
//        }else{
//            resultDtoSheet.setStatus(ResponseStatusType.SUCCESS);
//            return resultDtoSheet;
//        }
//    }

    private FileUploadResultDto parseDataFileSheet1(FilesDto filesDto, Long screeningId){
        FileUploadResultDto resultDto = new FileUploadResultDto();
        List<HedgeFundScreeningParsedDataDto> records = new ArrayList<>();
        Iterator<Row> rowIterator = null;
        try {
            rowIterator = ExcelUtils.getRowIterator(filesDto.getBytes(), filesDto.getFileName(), 0);
        }catch (ExcelFileParseException ex){
            String errorMessage = "Error parsing data file." + ex.getMessage();
            logger.error(errorMessage, ex);
            resultDto.setErrorMessageEn(errorMessage);
        }
        if(rowIterator != null) {
            int rowNum = 0;
            Map<String, Integer> headers = new HashMap<>();
            while (rowIterator.hasNext()) { // each row
                Row row = rowIterator.next();
                if(rowNum == 0){
                    // First row
                    Iterator<Cell> cellIterator = row.cellIterator();
                    while(cellIterator != null && cellIterator.hasNext()){
                        Cell cell = cellIterator.next();
                        headers.put(cell.getStringCellValue(), cell.getColumnIndex());
                        //System.out.println(cell.getColumnIndex() + "-" + ExcelUtils.getTextValueFromAnyCell(cell));
                    }
                }else{
                    HedgeFundScreeningParsedDataDto record = new HedgeFundScreeningParsedDataDto();
                    record.setScreening(new HedgeFundScreeningDto(screeningId));

                    if(headers.get("Fund Id") != null){
                        try {
                            String value = ExcelUtils.getTextValueFromAnyCell(row.getCell(headers.get("Fund Id").intValue()));
                            double doubleValue = Double.parseDouble(value);
                            long fundId = (long) doubleValue;
                            record.setFundId(fundId);
                        }catch (Exception ex){

                        }
                    }if(headers.get("Fund Name") != null){
                        String value = ExcelUtils.getStringValueFromCell(row.getCell(headers.get("Fund Name").intValue()));
                        record.setFundName(value);
                    }
                    if(headers.get("Investment Manager") != null){
                        String value = ExcelUtils.getStringValueFromCell(row.getCell(headers.get("Investment Manager").intValue()));
                        record.setInvestmentManager(value);
                    }
                    if(headers.get("Main Strategy") != null){
                        String value = ExcelUtils.getStringValueFromCell(row.getCell(headers.get("Main Strategy").intValue()));
                        record.setMainStrategy(value);
                    }
                    if(headers.get("Fund Status") != null){
                        String value = ExcelUtils.getStringValueFromCell(row.getCell(headers.get("Fund Status").intValue()));
                        record.setFundStatus(value);
                    }
                    if(!record.isEmpty()){
                        records.add(record);
                    }
                }
                rowNum++;
            }

        }

        if(!records.isEmpty()){
            if(filesDto.getType() != null && filesDto.getType().equalsIgnoreCase(FileTypeLookup.HF_SCREENING_DATA_FILE.getCode())) {
                List<HedgeFundScreeningParsedData> entities = this.parsedDataEntityConverter.assembleList(records);
                if (entities != null && !entities.isEmpty()) {
                    try {
                        //check parsed data
                        String errorMessage = getParsedDataSheet1ErrorMessage(records);
                        if(errorMessage != null){
                            resultDto.setErrorMessageEn(errorMessage);
                            return resultDto;
                        }

                        // delete parsed data
                        deleteParsedDataSheet1(screeningId, FileTypeLookup.HF_SCREENING_DATA_FILE.getCode());

                        this.parsedDataRepository.save(entities);
                        resultDto.setStatus(ResponseStatusType.SUCCESS);
                        logger.info("Successfully saved parsed HF Screening Parsed data (sheet 1) to database: screening id= " + screeningId);
                    } catch (Exception ex) {
                        String errorMessage = "Error saving HF Screening Parsed data sheet 1) to database: screening id=" + screeningId;
                        logger.error(errorMessage, ex);
                        resultDto.setErrorMessageEn(errorMessage);
                    }
                }
            }else if(filesDto.getType() != null && filesDto.getType().equalsIgnoreCase(FileTypeLookup.HF_SCREENING_UCITS_FILE.getCode())){
                List<HedgeFundScreeningParsedUcitsData> entities = this.parsedUcitsDataEntityConverter.assembleList(records);
                if (entities != null && !entities.isEmpty()) {
                    try {
                        // delete parsed data
                        deleteParsedDataSheet1(screeningId, FileTypeLookup.HF_SCREENING_UCITS_FILE.getCode());

                        this.parsedUcitsDataRepository.save(entities);
                        resultDto.setStatus(ResponseStatusType.SUCCESS);
                        logger.info("Successfully saved parsed HF Screening Parsed  ucits data (sheet 1) to database: screening id= " + screeningId);
                    } catch (Exception ex) {
                        String errorMessage = "Error saving HF Screening Parsed ucits data sheet 1) to database: screening id=" + screeningId;
                        logger.error(errorMessage, ex);
                        resultDto.setErrorMessageEn(errorMessage);
                    }
                }
            }
        }

        return resultDto;
    }

    private FileUploadResultDto parseDataFileSheet2(FilesDto filesDto, Long screeningId){
        FileUploadResultDto resultDto = new FileUploadResultDto();
        List<HedgeFundScreeningParsedDataDateValueDto> records = new ArrayList<>();
        Iterator<Row> rowIterator = ExcelUtils.getRowIterator(filesDto.getBytes(), filesDto.getFileName(), "Returns");
        if(rowIterator != null) {
            int rowNum = 0;
            Map<String, Integer> headers = new HashMap<>();
            while (rowIterator.hasNext()) { // each row
                Row row = rowIterator.next();
                if(rowNum == 0){
                    // First row
                    Iterator<Cell> cellIterator = row.cellIterator();
                    while(cellIterator != null && cellIterator.hasNext()){
                        Cell cell = cellIterator.next();
                        headers.put(cell.getStringCellValue(), cell.getColumnIndex());
                        //System.out.println(cell.getColumnIndex() + "-" + ExcelUtils.getTextValueFromAnyCell(cell));
                    }
                }else{
                    Long fundId = null;
                    String fundName = null;
                    String returnsCurrency = null;
                    if(headers.get("Fund Id") != null){
                        try {
                            String value = ExcelUtils.getTextValueFromAnyCell(row.getCell(headers.get("Fund Id").intValue()));
                            double doubleValue = Double.parseDouble(value);
                            long findId = (long) doubleValue;
                            fundId = findId;
                        }catch (Exception ex){

                        }
                    }if(headers.get("Fund Name") != null){
                        String value = ExcelUtils.getStringValueFromCell(row.getCell(headers.get("Fund Name").intValue()));
                        fundName = value;
                    }
                    if(headers.get("Returns Currency") != null){
                        String value = ExcelUtils.getStringValueFromCell(row.getCell(headers.get("Returns Currency").intValue()));
                        returnsCurrency = value;
                    }
                    // Returns
                    Set<String> keySet = headers.keySet();
                    if(keySet != null){
                        Iterator<String> iterator = keySet.iterator();
                        while(iterator.hasNext()){
                            String key = iterator.next();
                            if(!key.equals("Fund Id") && !key.equals("Fund Name") && !key.equals("Returns Currency")){
                                Double value = ExcelUtils.getDoubleValueFromCell(row.getCell(headers.get(key)));
                                if(value != null){
                                    HedgeFundScreeningParsedDataDateValueDto record = new HedgeFundScreeningParsedDataDateValueDto();

                                    Date date = DateUtils.getMMMYYYYYFormatLastDayMonthDate(key);
                                    if (date != null) {
                                        record.setDate(date);
                                    }else{
                                        resultDto.setErrorMessageEn("Error parsing RETURNS: failed to parse date from '" + key + "'");
                                        return resultDto;
                                    }
                                    record.setValue(value);

                                    record.setFundId(fundId);
                                    record.setFundName(fundName);
                                    record.setReturnsCurrency(returnsCurrency);
                                    record.setScreening(new HedgeFundScreeningDto(screeningId));
                                    if(!record.isEmpty()){
                                        records.add(record);
                                    }
                                }
                            }

                        }
                    }

                }
                rowNum++;
            }

        }

        if(!records.isEmpty()){
            List<HedgeFundScreeningParsedDataReturn> entities = this.parsedDataReturnEntityConverter.assembleList(records);
            if(entities != null && !entities.isEmpty()) {
                try {
                    // delete parsed data
                    deleteParsedDataSheet2(screeningId);

                    this.parsedDataReturnRepository.save(entities);
                    resultDto.setStatus(ResponseStatusType.SUCCESS);
                    logger.info("Successfully saved parsed HF Screening Parsed Returns (sheet 2) to database: screening id= " + screeningId);
                }catch (Exception ex){
                    String errorMessage = "Error saving HF Screening Parsed Returns (sheet 2) to database";
                    logger.error(errorMessage, ex);
                    resultDto.setErrorMessageEn(errorMessage);
                }
            }
        }

        return resultDto;
    }

    private FileUploadResultDto parseDataFileSheet3(FilesDto filesDto, Long screeningId){
        FileUploadResultDto resultDto = new FileUploadResultDto();
        List<HedgeFundScreeningParsedDataDateValueDto> records = new ArrayList<>();
        Iterator<Row> rowIterator = ExcelUtils.getRowIterator(filesDto.getBytes(), filesDto.getFileName(), "AUM");
        if(rowIterator != null) {
            int rowNum = 0;
            Map<String, Integer> headers = new HashMap<>();
            while (rowIterator.hasNext()) { // each row
                Row row = rowIterator.next();
                if(rowNum == 0){
                    // First row
                    Iterator<Cell> cellIterator = row.cellIterator();
                    while(cellIterator != null && cellIterator.hasNext()){
                        Cell cell = cellIterator.next();
                        headers.put(cell.getStringCellValue(), cell.getColumnIndex());
                        //System.out.println(cell.getColumnIndex() + "-" + ExcelUtils.getTextValueFromAnyCell(cell));
                    }
                }else{
                    Long fundId = null;
                    String fundName = null;
                    String returnsCurrency = null;
                    if(headers.get("Fund Id") != null){
                        try {
                            String value = ExcelUtils.getTextValueFromAnyCell(row.getCell(headers.get("Fund Id").intValue()));
                            double doubleValue = Double.parseDouble(value);
                            long findId = (long) doubleValue;
                            fundId = findId;
                        }catch (Exception ex){

                        }
                    }if(headers.get("Fund Name") != null){
                        String value = ExcelUtils.getStringValueFromCell(row.getCell(headers.get("Fund Name").intValue()));
                        fundName = value;
                    }
                    if(headers.get("Currency") != null){
                        String value = ExcelUtils.getStringValueFromCell(row.getCell(headers.get("Currency").intValue()));
                        returnsCurrency = value;
                    }
                    // AUMS
                    Set<String> keySet = headers.keySet();
                    if(keySet != null){
                        Iterator<String> iterator = keySet.iterator();
                        while(iterator.hasNext()){
                            String key = iterator.next();
                            if(!key.equals("Fund Id") && !key.equals("Fund Name") && !key.equals("Currency")){
                                Double value = ExcelUtils.getDoubleValueFromCell(row.getCell(headers.get(key)));
                                if(value != null){
                                    HedgeFundScreeningParsedDataDateValueDto record = new HedgeFundScreeningParsedDataDateValueDto();

                                    Date date = DateUtils.getMMMYYYYYFormatLastDayMonthDate(key);
                                    if(date != null){
                                        record.setDate(date);
                                    }else{
                                        resultDto.setErrorMessageEn("Error parsing AUM: failed to parse date from '" + key + "'");
                                        return resultDto;
                                    }
                                    record.setValue(value);

                                    record.setFundId(fundId);
                                    record.setFundName(fundName);
                                    record.setReturnsCurrency(returnsCurrency);
                                    record.setScreening(new HedgeFundScreeningDto(screeningId));
                                    if(!record.isEmpty()){
                                        records.add(record);
                                    }
                                }
                            }

                        }
                    }

                }
                rowNum++;
            }

        }

        if(!records.isEmpty()){
            if(filesDto.getType().equalsIgnoreCase(FileTypeLookup.HF_SCREENING_DATA_FILE.getCode())) {
                List<HedgeFundScreeningParsedDataAUM> entities = this.parsedDataAUMEntityConverter.assembleList(records);
                if (entities != null && !entities.isEmpty()) {
                    try {
                        // delete parsed data
                        deleteParsedDataSheet3(screeningId, FileTypeLookup.HF_SCREENING_DATA_FILE.getCode());

                        this.parsedDataAUMRepository.save(entities);
                        resultDto.setStatus(ResponseStatusType.SUCCESS);
                        logger.info("Successfully saved parsed HF Screening Parsed AUMs (sheet 3) to database (" +
                                FileTypeLookup.HF_SCREENING_DATA_FILE.getCode() + ") : screening id= " + screeningId);
                    } catch (Exception ex) {
                        String errorMessage = "Error saving HF Screening Parsed data AUMs (sheet 3) to database (" +
                                FileTypeLookup.HF_SCREENING_DATA_FILE.getCode() + ")";
                        logger.error(errorMessage, ex);
                        resultDto.setErrorMessageEn(errorMessage);
                    }
                }
            }else if(filesDto.getType().equalsIgnoreCase(FileTypeLookup.HF_SCREENING_UCITS_FILE.getCode())){
                List<HedgeFundScreeningParsedUcitsDataAUM> entities = this.parsedUcitsDataAUMEntityConverter.assembleList(records);
                if (entities != null && !entities.isEmpty()) {
                    try {
                        // delete parsed data
                        deleteParsedDataSheet3(screeningId, FileTypeLookup.HF_SCREENING_UCITS_FILE.getCode());

                        this.parsedUcitsDataAUMRepository.save(entities);
                        resultDto.setStatus(ResponseStatusType.SUCCESS);
                        logger.info("Successfully saved parsed HF Screening Parsed AUMs (sheet 3) to database (" +
                                FileTypeLookup.HF_SCREENING_UCITS_FILE.getCode() + ") : screening id= " + screeningId);
                    } catch (Exception ex) {
                        String errorMessage = "Error saving HF Screening Parsed data AUMs (sheet 3) to database (" +
                                FileTypeLookup.HF_SCREENING_UCITS_FILE.getCode() + ")";
                        logger.error(errorMessage, ex);
                        resultDto.setErrorMessageEn(errorMessage);
                    }
                }
            }
        }

        return resultDto;
    }

    private String getParsedDataSheet1ErrorMessage(List<HedgeFundScreeningParsedDataDto> records){
        if(records != null){
            Set<Long> fundIds = new HashSet<>();
            for(HedgeFundScreeningParsedDataDto record: records){
                if(record.getFundName() == null){
                    String errorMessage = "Failed to parse HS Screening file sheet 1: fund name null";
                    logger.error(errorMessage);
                    return errorMessage;
                }else if(record.getInvestmentManager() == null){
                    String errorMessage = "Failed to parse HS Screening file sheet 1: investment manager null";
                    logger.error(errorMessage);
                    return errorMessage;
                }else if(record.getFundId() == null){
                    String errorMessage = "Failed to parse HS Screening file sheet 1: fund id null";
                    logger.error(errorMessage);
                    return errorMessage;
                }else if(fundIds.contains(record.getFundId())){
                    String errorMessage = "Failed to parse HS Screening file sheet 1: duplicate fund id";
                    logger.error(errorMessage);
                    return errorMessage;
                }

                fundIds.add(record.getFundId());
            }
        }
        return null;
    }

    private boolean deleteParsedDataSheet1(Long screeningId, String type){
        try {
            if(type.equalsIgnoreCase(FileTypeLookup.HF_SCREENING_DATA_FILE.getCode())) {
                logger.error("Successfully deleted parsed data (" + type + ") for HF Screening with id=" + screeningId + "(with exception)");
                this.parsedDataRepository.deleteByScreeningId(screeningId);
                return true;
            }else if(type.equalsIgnoreCase(FileTypeLookup.HF_SCREENING_UCITS_FILE.getCode())) {
                logger.error("Successfully deleted parsed data (" + type + ") for HF Screening with id=" + screeningId + "(with exception)");
                this.parsedUcitsDataRepository.deleteByScreeningId(screeningId);
                return true;
            }
        }catch (Exception ex){
            logger.error("Error deleting parsed data (" + type + ") for HF Screening with id=" + screeningId + "(with exception)", ex);
        }
        return false;
    }

    private boolean deleteParsedFundParamsData(Long screeningId){
        try {
            this.parsedFundParamDataRepository.deleteByScreeningId(screeningId);
            return true;
        }catch (Exception ex){
            logger.error("Error deleting fund params data for HF Screening with id=" + screeningId + "(with exception)", ex);
        }
        return false;
    }

    private boolean deleteParsedDataSheet2(Long screeningId){
        try {
            parsedDataReturnRepository.deleteByScreeningId(screeningId);
            logger.error("Successfully deleted parsed data (sheet 2) for HF Screening with id=" + screeningId + "(with exception)");
            return true;
        }catch (Exception ex){
            logger.error("Error deleting parsed data for HF Screening (RETURNS) with id=" + screeningId + "(with exception)", ex);
        }
        return false;
    }

    private boolean deleteParsedDataSheet3(Long screeningId, String type){
        try {
            if(type.equalsIgnoreCase(FileTypeLookup.HF_SCREENING_DATA_FILE.getCode())) {
                logger.error("Successfully deleted parsed data (" + type + ") for HF Screening with id=" + screeningId + "(with exception)");
                this.parsedDataAUMRepository.deleteByScreeningId(screeningId);
                return true;
            }else if(type.equalsIgnoreCase(FileTypeLookup.HF_SCREENING_UCITS_FILE.getCode())) {
                logger.error("Successfully deleted parsed data (" + type + ") for HF Screening with id=" + screeningId + "(with exception)");
                this.parsedUcitsDataAUMRepository.deleteByScreeningId(screeningId);
                return true;
            }
        }catch (Exception ex){
            logger.error("Error deleting parsed data for HF Screening (AUMS) with id=" + screeningId + "(with exception)", ex);
        }
        return false;
    }

    @Override
    public boolean removeFileAndData(Long fileId, Long screeningId, String username){
        if(!checkScreeningEditable(screeningId)){
            logger.error("Error removing data file for HF Screening: screening is not editable [user]=" + username);
            return false;
        }
        HedgeFundScreening entity = this.screeningRepository.findOne(screeningId);

        if(fileId == null || entity.getDataFile() == null || entity.getDataFile().getId().longValue() != fileId.longValue()) {
            return false;
        }
        FileUploadResultDto result = deleteFileAndAssociation(entity.getDataFile().getId(), screeningId, FileTypeLookup.HF_SCREENING_DATA_FILE.getCode());
        if(result.getStatus() == ResponseStatusType.FAIL){
            String errorMessage = "Failed to delete HF Screening data: file id=" + entity.getDataFile().getId() + ", screening id=" + screeningId;
            logger.error(errorMessage);
            return false;
        }

        EmployeeDto updater = this.employeeService.findByUsername(username);
        if (updater != null) {
            entity.setUpdateDate(new Date());
            entity.setUpdater(new Employee(updater.getId()));
            this.screeningRepository.save(entity);
        }

        boolean deletedSheet1 = deleteParsedDataSheet1(screeningId, FileTypeLookup.HF_SCREENING_DATA_FILE.getCode());
        boolean deletedSheet2 = deleteParsedDataSheet2(screeningId);
        boolean deletedSheet3 = deleteParsedDataSheet3(screeningId, FileTypeLookup.HF_SCREENING_DATA_FILE.getCode());
        if(!deletedSheet1){
            logger.error("Failed to delete HF Screening parsed data (sheet 1): file id=" + entity.getDataFile().getId() +
                    ", screening id=" + screeningId + " [user]=" + username);
            return false;
        }
        if(!deletedSheet2){
            logger.error("Failed to delete HF Screening parsed data (sheet 2 - RETURNS): file id=" + entity.getDataFile().getId() +
                    ", screening id=" + screeningId + " [user]=" + username);
            return false;
        }
        if(!deletedSheet3){
            logger.error("Failed to delete HF Screening parsed data (sheet 3 - AUMs): file id=" + entity.getDataFile().getId() +
                    ", screening id=" + screeningId + " [user]=" + username);
            return false;
        }
        logger.info("Successfully deleted HF Screening file and parsed data: screening id=" + screeningId + " [user]=" + username);

        try {
            this.filteredResultRepository.deleteByScreeningId(screeningId);
        }catch (Exception ex){
            logger.error("Error deleting filters: screening id=" + screeningId + " (with exception)", ex);
            return false;
        }
        return true;
    }

    @Override
    public boolean removeUcitsFileAndData(Long fileId, Long screeningId, String username){
        HedgeFundScreening entity = this.screeningRepository.findOne(screeningId);

        if(fileId == null || entity.getUcitsFile() == null || entity.getUcitsFile().getId().longValue() != fileId.longValue()) {
            return false;
        }
        FileUploadResultDto result = deleteFileAndAssociation(fileId, screeningId, FileTypeLookup.HF_SCREENING_UCITS_FILE.getCode());
        if(result.getStatus() == ResponseStatusType.FAIL){
            String errorMessage = "Failed to delete HF Screening ucits: file id=" + entity.getDataFile().getId() + ", screening id=" + screeningId;
            logger.error(errorMessage);
            return false;
        }
        boolean deletedSheet1 = deleteParsedDataSheet1(screeningId, FileTypeLookup.HF_SCREENING_UCITS_FILE.getCode());
        boolean deletedSheet3 = deleteParsedDataSheet3(screeningId, FileTypeLookup.HF_SCREENING_UCITS_FILE.getCode());
        if(!deletedSheet1){
            logger.error("Failed to delete HF Screening parsed ucits (sheet 1): file id=" + entity.getDataFile().getId() +
                    ", screening id=" + screeningId + " [user]=" + username);
            return false;
        }
        if(!deletedSheet3){
            logger.error("Failed to delete HF Screening parsed ucits (sheet 3 - AUMs): file id=" + entity.getDataFile().getId() +
                    ", screening id=" + screeningId + " [user]=" + username);
            return false;
        }
        logger.info("Successfully deleted HF Screening ucits file and parsed data: screening id=" + screeningId + " [user]=" + username);

//        try {
//            this.filteredResultRepository.deleteByScreeningId(screeningId);
//        }catch (Exception ex){
//            logger.error("Error deleting filters: screening id=" + screeningId + " (with exception)", ex);
//            return false;
//        }
        return true;
    }

    @Override
    public boolean removeFundParamsFileAndData(Long fileId, Long screeningId, String username){
        if(!checkScreeningEditable(screeningId)){
            logger.error("Error removing fund params file for HF Screening: screening is not editable [user]=" + username);
            return false;
        }
        HedgeFundScreening entity = this.screeningRepository.findOne(screeningId);

        if(fileId == null || entity.getParamsFile() == null || entity.getParamsFile().getId().longValue() != fileId.longValue()) {
            return false;
        }
        FileUploadResultDto result = deleteFileAndAssociation(entity.getParamsFile().getId(), screeningId, FileTypeLookup.HF_SCREENING_PARAMS_FILE.getCode());
        if(result.getStatus() == ResponseStatusType.FAIL){
            String errorMessage = "Failed to delete HF Screening fund params data: file id=" + entity.getParamsFile().getId() + ", screening id=" + screeningId;
            logger.error(errorMessage);
            return false;
        }

        EmployeeDto updater = this.employeeService.findByUsername(username);
        if (updater != null) {
            entity.setUpdateDate(new Date());
            entity.setUpdater(new Employee(updater.getId()));
            this.screeningRepository.save(entity);
        }

        boolean deletedSheet1 = deleteParsedFundParamsData(screeningId);
        if(!deletedSheet1){
            logger.error("Failed to delete HF Screening Fund params data (sheet 1): file id=" + entity.getDataFile().getId() +
                    ", screening id=" + screeningId + " [user]=" + username);
            return false;
        }
        logger.info("Successfully deleted HF Screening Fund params file and parsed data: screening id=" + screeningId + " [user]=" + username);
        return true;
    }

    /******************************************************************************************************************/


    /*** Calculating Screening Results*********************************************************************************/
    @Override
    public HedgeFundScreeningFilteredResultStatisticsDto getFilteredResultStatistics(HedgeFundScreeningFilteredResultDto params) {

        long start = new Date().getTime();
        HedgeFundScreeningFilteredResultStatisticsDto statisticsDto = new HedgeFundScreeningFilteredResultStatisticsDto();
        if(params != null) {

            // TODO: params are valid

            //long start = new Date().getTime();

            // get qualified
            //System.out.println("QUALIFIED");
            Integer[][] qualified = getFilteredResultStatisticsQualified(params);
            statisticsDto.setQualified(qualified);

            Integer[][] undecided = getFilteredResultStatisticsUndecided(params);
            statisticsDto.setUndecided(undecided);

            List<HedgeFundScreeningParsedDataDto> addedFunds = getAddedFundsByFilteredResultId(params.getId());
            Integer[][] unqualified = new Integer[params.getLookbackReturns().intValue() + 2][params.getLookbackAUM().intValue() + 2];
            Long totalCount = this.parsedDataRepository.countByScreeningId(params.getScreeningId());
            for(int i = 0; i < unqualified.length; i++){
                for(int j = 0; j < unqualified[i].length; j++){
                    if(i == 0 && j == 0){
                        unqualified[i][j] = null;
                        continue;
                    }else if(i == 0){
                        unqualified[i][j] = j - 1;
                    }else if(j == 0){
                        unqualified[i][j] = i - 1;
                    }else {
                        long count = totalCount;
                        if(addedFunds != null && !addedFunds.isEmpty()) {
                            count = totalCount + addedFunds.size();
                        }

                        unqualified[i][j] = (int) (count - (long) qualified[i][j] - (long) undecided[i][j]);
                    }
                }
            }
            statisticsDto.setUnqualified(unqualified);

            // final results
            if(params.getId() != null) {
                // final result
                statisticsDto.setFinalResults(getFinalResultsByFilteredResultId(params.getId()));

                // archives
                statisticsDto.setArchivedResults(getArchivedFinalResultsByFilteredResultId(params.getId()));
            }

//            System.out.println("QUALIFIED CHECK");
//            for(int i = 0; i < qualified.length; i++){
//                for(int j = 0; j < qualified[i].length; j++){
//                    if(i == 0 || j == 0){
//                        continue;
//                    }
//                    int value = qualified[i][j];
//                    HedgeFundScreeningFilteredResultDto newParams = new HedgeFundScreeningFilteredResultDto(params);
//                    newParams.setLookbackAUM(j-1);
//                    newParams.setLookbackReturns(i-1);
//                    newParams.setStartDate(null);
//                    newParams.setId(params.getId());
//                    ListResponseDto qualifiedResponse = getFilteredResultQualifiedFundList(newParams, false);
//                    List<HedgeFundScreeningFundAUMDto> records = qualifiedResponse.getRecords();
//                    if(records.size() != qualified[i][j]){
//                        System.out.println("AUM l=" + i + ", RETURN l=" + j + "; MISMATCH: stats =" + qualified[i][j] + ", fundlist=" + records.size() );
//                    }else{
//                        //System.out.println("AUM l=" + i + ", RETURN l=" + j + "; OK!");
//                    }
//                }
//            }
//            System.out.println("QUALIFIED CHECK DONE.");
//            System.out.println("UNDECIDED CHECK ...");
//            for(int i = 0; i < undecided.length; i++){
//                for(int j = 0; j < undecided[i].length; j++){
//                    if(i == 0 || j == 0){
//                        continue;
//                    }
//                    int value = undecided[i][j];
//                    HedgeFundScreeningFilteredResultDto newParams = new HedgeFundScreeningFilteredResultDto(params);
//                    newParams.setLookbackAUM(j-1);
//                    newParams.setLookbackReturns(i-1);
//                    newParams.setStartDate(null);
//                    newParams.setId(params.getId());
//                    List<HedgeFundScreeningParsedDataDto> records = getFilteredResultUndecidedFundList(newParams);
//                    if(records.size() != value){
//                        System.out.println("AUM l=" + i + ", RETURN l=" + j + "; MISMATCH: stats =" + value + ", fundlist=" + records.size() );
//                    }else{
//                        //System.out.println("AUM l=" + i + ", RETURN l=" + j + "; OK!");
//                    }
//                }
//            }
//            System.out.println("UNDECIDED CHECK DONE");
//            System.out.println("UNQUALIFIED CHECK ...");
//            for(int i = 0; i < unqualified.length; i++){
//                for(int j = 0; j < unqualified[i].length; j++){
//                    if(i == 0 || j == 0){
//                        continue;
//                    }
//                    int value = unqualified[i][j];
//                    HedgeFundScreeningFilteredResultDto newParams = new HedgeFundScreeningFilteredResultDto(params);
//                    newParams.setLookbackAUM(j-1);
//                    newParams.setLookbackReturns(i-1);
//                    newParams.setStartDate(null);
//                    newParams.setId(params.getId());
//                    List<HedgeFundScreeningParsedDataDto> records = getFilteredResultUnqualifiedFundList(newParams);
//                    if(records.size() != value){
//                        System.out.println("AUM l=" + i + ", RETURN l=" + j + "; MISMATCH: stats =" + value + ", fundlist=" + records.size() );
//                    }else{
//                        //System.out.println("AUM l=" + i + ", RETURN l=" + j + "; OK!");
//                    }
//                }
//            }
//            System.out.println("UNQUALIFIED CHECK DONE");

            statisticsDto.setParameters(new HedgeFundScreeningFilteredResultDto(params));
//            long end = new Date().getTime();
//            System.out.println("Total time = " + (end-start) / 1000.);
        }else{
            // TODO: ALL FUNDS ?
        }
        long end = new Date().getTime();
        System.out.println("Statistics total time = " + (end-start) / 1000.);


//        checkMatrixWithFundList(statisticsDto, params);
//
//        System.out.println();
//
//        int totalErrors = 0;
//        for(int i = 1; i <= params.getLookbackReturns(); i++){
//            for(int j = 1; j <= params.getLookbackAUM(); j++){
//                HedgeFundScreeningFilteredResultDto params2 = new HedgeFundScreeningFilteredResultDto(params);
//                params2.setLookbackReturns(i);
//                params2.setLookbackAUM(j);
//                totalErrors += checkQualifiedFundList(params2);
//            }
//        }
//        if(totalErrors > 0){
//            System.out.println("QUALIFIED FUND LIST CHECK WITH ERRORS: " + totalErrors);
//        }else{
//            System.out.println("QUALIFIED FUND LIST CHECK - OK");
//        }
//
//        totalErrors = 0;
//        for(int i = 1; i <= params.getLookbackReturns(); i++){
//            for(int j = 1; j <= params.getLookbackAUM(); j++){
//                HedgeFundScreeningFilteredResultDto params2 = new HedgeFundScreeningFilteredResultDto(params);
//                params2.setLookbackReturns(i);
//                params2.setLookbackAUM(j);
//                totalErrors += checkUndecidedFundList(params2);
//            }
//        }
//
//        if(totalErrors > 0){
//            System.out.println("UNDECIDED FUND LIST CHECK WITH ERRORS: " + totalErrors);
//        }else{
//            System.out.println("UNDECIDED FUND LIST CHECK - OK");
//        }

        return statisticsDto;
    }

    private List<HedgeFundScreeningFinalResultsDto> getArchivedFinalResultsByFilteredResultId(Long filteredResultId){
        List<HedgeFundScreeningSavedResultsDto> archives = getArchivedSavedResultsByFilteredResultId(filteredResultId);
        if(archives != null && !archives.isEmpty()){
            List<HedgeFundScreeningFinalResultsDto> archivedResults = new ArrayList<>();
            for(HedgeFundScreeningSavedResultsDto archive: archives){
                HedgeFundScreeningFinalResultsDto finalResult = getFinalResultsWithInfo(archive);
                archivedResults.add(finalResult);
            }
            return archivedResults;
        }
        return null;
    }

    private HedgeFundScreeningFinalResultsDto getFinalResultsByFilteredResultId(Long filteredResultId){
        HedgeFundScreeningSavedResultsDto savedResultsDto = getNonArchivedSavedResultsByFilteredResultId(filteredResultId);
        if(savedResultsDto != null) {
            HedgeFundScreeningFinalResultsDto finalResult = getFinalResultsWithInfo(savedResultsDto);
            return finalResult;
        }
        return null;
    }

    private HedgeFundScreeningFinalResultsDto getFinalResultsWithInfo( HedgeFundScreeningSavedResultsDto savedResultsDto){
        HedgeFundScreeningFinalResultsDto finalResultsDto = new HedgeFundScreeningFinalResultsDto();
        finalResultsDto.setFilteredResult(savedResultsDto.getFilteredResult());
        finalResultsDto.setId(savedResultsDto.getId());
        finalResultsDto.setFundAUM(savedResultsDto.getFundAUM());
        finalResultsDto.setManagerAUM(savedResultsDto.getManagerAUM());
        finalResultsDto.setStartDate(savedResultsDto.getStartDate());
        finalResultsDto.setStartDateMonth(DateUtils.getMonthYearDate(savedResultsDto.getStartDate()));
        finalResultsDto.setTrackRecord(savedResultsDto.getTrackRecord());
        finalResultsDto.setLookbackAUM(savedResultsDto.getLookbackAUM());
        finalResultsDto.setLookbackReturns(savedResultsDto.getLookbackReturns());
        finalResultsDto.setDescription(savedResultsDto.getDescription());
        finalResultsDto.setSelectedLookbackAUM(savedResultsDto.getSelectedLookbackAUM());
        finalResultsDto.setSelectedLookbackReturn(savedResultsDto.getSelectedLookbackReturn());

        finalResultsDto.setCreationDate(savedResultsDto.getCreationDate());
        finalResultsDto.setCreator(savedResultsDto.getCreator() != null ? savedResultsDto.getCreator() : null);
        finalResultsDto.setUpdateDate(savedResultsDto.getUpdateDate());
        finalResultsDto.setUpdater(savedResultsDto.getUpdater() != null ? savedResultsDto.getUpdater() : null);

        // fund list
        List<HedgeFundScreeningSavedResultFundsDto> funds = getSavedResultFundsBySavedResultsId(savedResultsDto.getId());
        if(funds != null && !funds.isEmpty()){
            List<HedgeFundScreeningSavedResultFundsDto> qualifiedFunds = new ArrayList<>();
            List<HedgeFundScreeningSavedResultFundsDto> unqualifiedFunds = new ArrayList<>();
            List<HedgeFundScreeningSavedResultFundsDto> undecidedFunds = new ArrayList<>();
            for(HedgeFundScreeningSavedResultFundsDto fund: funds){
                if(fund.getType() == 1){
                    qualifiedFunds.add(fund);
                }else if(fund.getType() == 2){
                    unqualifiedFunds.add(fund);
                }else if(fund.getType() == 3){
                    undecidedFunds.add(fund);
                }
            }
            finalResultsDto.setQualifiedFunds(qualifiedFunds);
            finalResultsDto.setUnqualifiedFunds(unqualifiedFunds);
            finalResultsDto.setUndecidedFunds(undecidedFunds);

            if(qualifiedFunds != null && !qualifiedFunds.isEmpty()){
                if(qualifiedFunds.size() > 50){
                    if(MathUtils.subtract(2, qualifiedFunds.get(50).getTotalScore(), qualifiedFunds.get(49).getTotalScore()) == 0.00){
                        // remove with total score equal to 51-st fund total score
                        int index = 49;
                        Double currentTotalScore = qualifiedFunds.get(49).getTotalScore();
                        while(currentTotalScore.doubleValue() == qualifiedFunds.get(49).getTotalScore().doubleValue()){
                            index--;
                            currentTotalScore = qualifiedFunds.get(index).getTotalScore();
                        }

                        finalResultsDto.setTop50qualifiedFunds(qualifiedFunds.subList(0, index + 1));
                    }else{
                        // first 50
                        finalResultsDto.setTop50qualifiedFunds(qualifiedFunds.subList(0, 50));
                    }
                }else{
                    finalResultsDto.setTop50qualifiedFunds(qualifiedFunds);
                }
            }
        }

        // currency rates
        List<HedgeFundScreeningSavedResultsCurrency> currencyRateEntities = this.screeningSavedResultsCurrencyRepository.findBySavedResultsIdOrderByCurrencyAscDateAsc(savedResultsDto.getId());
        if(currencyRateEntities != null && !currencyRateEntities.isEmpty()){
            List<HedgeFundScreeningSavedResultCurrencyDto> currencyRates = new ArrayList<>();
            for(HedgeFundScreeningSavedResultsCurrency currencyRate: currencyRateEntities) {
                HedgeFundScreeningSavedResultCurrencyDto currencyRateDto = new HedgeFundScreeningSavedResultCurrencyDto();
                currencyRateDto.setCurrency(new BaseDictionaryDto(currencyRate.getCurrency().getCode(), currencyRate.getCurrency().getNameEn(),
                        currencyRate.getCurrency().getNameRu(), currencyRate.getCurrency().getNameKz()));
                currencyRateDto.setDate(currencyRate.getDate());
                currencyRateDto.setValue(currencyRate.getValue());
                currencyRateDto.setAverageValue(currencyRate.getAverageValue());
                currencyRateDto.setAverageValueYear(currencyRate.getAverageValueYear());
                currencyRateDto.setValueUSD(currencyRate.getValueUSD());
                currencyRates.add(currencyRateDto);
            }
            finalResultsDto.setCurrencyRates(currencyRates);
        }

        // benchmarks
        List<HedgeFundScreeningSavedResultsBenchmark> benchmarkEntities = this.screeningSavedResultsBenchmarkRepository.findBySavedResultsIdOrderByBenchmarkIdDescDateAsc(savedResultsDto.getId());
        if(benchmarkEntities != null && !benchmarkEntities.isEmpty()){
            List<HedgeFundScreeningSavedResultBenchmarkDto> benchmarks = new ArrayList<>();
            for(HedgeFundScreeningSavedResultsBenchmark benchmark: benchmarkEntities) {
                HedgeFundScreeningSavedResultBenchmarkDto benchmarkDto = new HedgeFundScreeningSavedResultBenchmarkDto();
                benchmarkDto.setBenchmark(new BaseDictionaryDto(benchmark.getBenchmark().getCode(), benchmark.getBenchmark().getNameEn(),
                        benchmark.getBenchmark().getNameRu(), benchmark.getBenchmark().getNameKz()));
                benchmarkDto.setDate(benchmark.getDate());
                //benchmarkDto.setReturnValue(benchmark.getReturnValue());
                benchmarkDto.setIndexValue(benchmark.getIndexValue());
                benchmark.setCalculatedMonthReturn(benchmark.getCalculatedMonthReturn());
                benchmarkDto.setYtd(benchmark.getYtd());
                benchmarks.add(benchmarkDto);
            }
            finalResultsDto.setBenchmarks(benchmarks);
        }

        // edited funds
        List<HedgeFundScreeningSavedResultsEditedFund> editedFundEntities = this.screeningSavedResultsEditedFundRepository.findBySavedResultsId(savedResultsDto.getId());
        if(editedFundEntities != null && !editedFundEntities.isEmpty()){
            List<HedgeFundScreeningSavedResultFundsDto> editedFunds = new ArrayList<>();
            for(HedgeFundScreeningSavedResultsEditedFund fundEntity: editedFundEntities){
                HedgeFundScreeningSavedResultFundsDto fund = new HedgeFundScreeningSavedResultFundsDto();
                fund.setFundId(fundEntity.getId());
                fund.setFundName(fundEntity.getParsedData().getFundName());
                fund.setInvestmentManager(fundEntity.getParsedData().getInvestmentManager());
                fund.setMainStrategy(fundEntity.getParsedData().getMainStrategy());
                fund.setEditedFundAUM(fundEntity.getEditedFundAUM());
                fund.setEditedFundAUMDate(fundEntity.getEditedFundAUMDate());
                fund.setEditedFundAUMComment(fundEntity.getEditedFundAUMComment());
                fund.setManagerAUM(fundEntity.getManagerAUM());
                fund.setExcluded(fundEntity.getExcluded());

                editedFunds.add(fund);
            }

            finalResultsDto.setEditedFunds(editedFunds);
        }
        return finalResultsDto;
    }

//    private Map<Integer, List<HedgeFundScreeningParsedDataDto>> getAddedFundsQualifiedByReturnLookbackMap(HedgeFundScreeningFilteredResultDto params){
//        Map<Integer, List<HedgeFundScreeningParsedDataDto>> fundsMap = null;
//        if(params.getId() != null) {
//            List<HedgeFundScreeningParsedDataDto> addedFunds = getAddedFundsByFilteredResultId(params.getId());
//            if(addedFunds != null && !addedFunds.isEmpty()){
//                fundsMap = new HashMap();
//                for(HedgeFundScreeningParsedDataDto addedFund: addedFunds){
//                    if(addedFund.getFundAUM() != null && addedFund.getFundAUM().doubleValue() >= params.getFundAUM().doubleValue()){
//                        // valid AUM
//                        for(int i = 0; i <= params.getLookbackReturns().intValue(); i++){
//                            Date dateFrom = DateUtils.moveDateByMonths(params.getStartDateFromTextOrCurrent(), 0 - (Math.max(0, params.getTrackRecord().intValue() + params.getLookbackReturns().intValue() - 1)));
//                            List<HedgeFundScreeningFundReturnDto> returns = addedFund.getReturns();
//                            Collections.sort(returns);
//
//                            Date previousDate = null;
//                            int count = 0;
//                            for(HedgeFundScreeningFundReturnDto returnDto: returns){
//                                Date date = DateUtils.getMM_YYYYYFormatLastDayMonthDate(returnDto.getDate());
//                                Date startDate = DateUtils.getMM_YYYYYFormatLastDayMonthDate(params.getStartDateMonth());
//                                if(date.compareTo(dateFrom) < 0 || date.compareTo(startDate) > 0){
//                                    // skip this return
//                                    continue;
//                                }
//                                if(previousDate == null){
//                                    previousDate = date;
//                                    count = 1;
//                                }else{
//                                    Date nextMonth = DateUtils.moveDateByMonths(previousDate, 1);
//                                    nextMonth = DateUtils.getLastDayOfCurrentMonth(nextMonth);
//                                    //if(DateUtils.isSameMonth(nextMonth, date)){
//                                    if(DateUtils.isSameDate(nextMonth, date)){
//                                        count++;
//                                        if(count == params.getTrackRecord().intValue()){
//                                            break;
//                                        }
//                                    }else{
//                                        count = 0;
//                                    }
//                                    previousDate = date;
//                                }
//                            }
//                            if(count >= params.getTrackRecord().intValue()){
//                                // valid fund
//                                List<HedgeFundScreeningParsedDataDto> existing = fundsMap.get(i);
//                                if(existing == null){
//                                    existing = new ArrayList<>();
//                                }
//                                existing.add(addedFund);
//                                fundsMap.put(i, existing);
//
//                            }
//                        }
//                    }
//                }
//            }
//        }
//
//        return fundsMap;
//    }

    @Override
    public ListResponseDto getFilteredResultQualifiedFundList(HedgeFundScreeningFilteredResultDto params, boolean withScoring) {
        ListResponseDto responseDto = new ListResponseDto();
        // RETURNS
        Map<Integer, List<HedgeFundScreeningFundCounts>> returnsMap = getQualifiedFundMapByReturnLookback(params, params.getLookbackReturns().intValue());
        Assert.isTrue(returnsMap.size() == 1);
        Assert.isTrue(returnsMap.get(params.getLookbackReturns().intValue()) != null);

        // AUM
        Map<Integer, List<HedgeFundScreeningFundAUMDto>> aumsMap = getQualifiedUndecidedAUMLookbackMap(params, 1, params.getLookbackAUM().intValue());
        Assert.isTrue(aumsMap.size() == 1);
        Assert.isTrue(aumsMap.get(params.getLookbackAUM().intValue()) != null);

        List<HedgeFundScreeningFundAUMDto> resultFunds =
                getIntersectingFunds(returnsMap.get(params.getLookbackReturns().intValue()), aumsMap.get(params.getLookbackAUM().intValue()));

        if(resultFunds != null){
            List<HedgeFundScreeningParsedDataDto> fundList = getResultFundListWithAdditionalInfo(params.getScreeningId(), params.getId(), resultFunds);

//            Map<Integer, List<HedgeFundScreeningParsedDataDto>> addedFundsQualifiedMap = getAddedFundsQualifiedByReturnLookbackMap(params);
//            if(addedFundsQualifiedMap != null){
//                List<HedgeFundScreeningParsedDataDto> addedFunds = addedFundsQualifiedMap.get(params.getLookbackReturns().intValue());
//                if(addedFunds != null) {
//                    fundList.addAll(addedFunds);
//                }
//            }

            // SCORING
            HedgeFundScoringFundParamsDto scoringParams = new HedgeFundScoringFundParamsDto();
            scoringParams.setScreeningId(params.getScreeningId());
            scoringParams.setFilteredResultId(params.getId());
            scoringParams.setLookbackReturn(params.getLookbackReturns().intValue());
            scoringParams.setLookbackAUM(params.getLookbackAUM().intValue());

            responseDto.setRecords(fundList);
            if(withScoring) {
                responseDto = this.scoringService.getCalculatedScoring(fundList, scoringParams);
                if (responseDto.getStatus() != null && responseDto.getStatus().getCode().equalsIgnoreCase(ResponseStatusType.SUCCESS.getCode())) {
                    // SUCCESS
                } else {
                    // FAILED
                    return responseDto;
                }
            }else{
            }
            Collections.sort(responseDto.getRecords());

            responseDto.setStatus(ResponseStatusType.SUCCESS);
            return responseDto;
        }

        return responseDto;
    }

    @Override
    public ListResponseDto getFilteredResultQualifiedFundListAlternative(HedgeFundScreeningFilteredResultDto params, boolean withScoring) {
        ListResponseDto responseDto = new ListResponseDto();
        // RETURNS
        Map<Integer, List<HedgeFundScreeningFundCounts>> returnsMap = getQualifiedFundMapByReturnLookback(params, params.getLookbackReturns().intValue());
        Assert.isTrue(returnsMap.size() == 1);
        Assert.isTrue(returnsMap.get(params.getLookbackReturns().intValue()) != null);

        // AUM
        Map<Integer, List<HedgeFundScreeningFundAUMDto>> aumsMap = getQualifiedUndecidedAUMLookbackMap(params, 1, params.getLookbackAUM().intValue());
        Assert.isTrue(aumsMap.size() == 1);
        Assert.isTrue(aumsMap.get(params.getLookbackAUM().intValue()) != null);

        List<HedgeFundScreeningFundAUMDto> resultFunds =
                getIntersectingFunds(returnsMap.get(params.getLookbackReturns().intValue()), aumsMap.get(params.getLookbackAUM().intValue()));

        if(resultFunds != null){
            List<HedgeFundScreeningParsedDataDto> fundList = getResultFundListWithAdditionalInfo(params.getScreeningId(), params.getId(), resultFunds);

//            Map<Integer, List<HedgeFundScreeningParsedDataDto>> addedFundsQualifiedMap = getAddedFundsQualifiedByReturnLookbackMap(params);
//            if(addedFundsQualifiedMap != null){
//                List<HedgeFundScreeningParsedDataDto> addedFunds = addedFundsQualifiedMap.get(params.getLookbackReturns().intValue());
//                if(addedFunds != null) {
//                    fundList.addAll(addedFunds);
//                }
//            }

            // SCORING
            HedgeFundScoringFundParamsDto scoringParams = new HedgeFundScoringFundParamsDto();
            scoringParams.setScreeningId(params.getScreeningId());
            scoringParams.setFilteredResultId(params.getId());
            scoringParams.setLookbackReturn(params.getLookbackReturns().intValue());
            scoringParams.setLookbackAUM(params.getLookbackAUM().intValue());

            responseDto.setRecords(fundList);
            if(withScoring) {
                responseDto = this.scoringService.getCalculatedScoringAlternative(fundList, scoringParams);
                if (responseDto.getStatus() != null && responseDto.getStatus().getCode().equalsIgnoreCase(ResponseStatusType.SUCCESS.getCode())) {
                    // SUCCESS
                } else {
                    // FAILED
                    return responseDto;
                }
            }else{
            }
            Collections.sort(responseDto.getRecords());

            responseDto.setStatus(ResponseStatusType.SUCCESS);
            return responseDto;
        }

        return responseDto;
    }

    @Override
    public List<HedgeFundScreeningParsedDataDto> getFilteredResultUndecidedFundList(HedgeFundScreeningFilteredResultDto params)  {
//        // RETURNS
//        List<HedgeFundScreeningFundCounts>  validFundCounts = getQualifiedFundReturnCounts(params, params.getLookbackReturns().intValue());
//
//        // AUM
//        List<HedgeFundScreeningParsedDataAUM>  undecidedFundsAUM = getAUMFundListByTypeAndLookback(params, params.getLookbackAUM().intValue(), 2);
//
//        Map<Long, HedgeFundScreeningParsedDataAUM> validFundsAUMMap = new HashMap<>();
//        for(HedgeFundScreeningParsedDataAUM fundAUM: undecidedFundsAUM){
//            validFundsAUMMap.put(fundAUM.getFundId(), fundAUM);
//        }
//
//        Long[] fundIds = getIntersectingFunds(validFundCounts, undecidedFundsAUM);
//
//        if(fundIds != null){
//            List<HedgeFundScreeningParsedDataDto> undecidedFundList = getQualifiedOrUndecidedFundListByTypeAndManagerAUM(params, undecidedFundsAUM, fundIds, 2);
//            Collections.sort(undecidedFundList);
//            return undecidedFundList;
//        }
//
//        return  new ArrayList<>();
//
//

        // RETURNS
        Map<Integer, List<HedgeFundScreeningFundCounts>> returnsMap = getQualifiedFundMapByReturnLookback(params, params.getLookbackReturns().intValue());
        Assert.isTrue(returnsMap.size() == 1);
        Assert.isTrue(returnsMap.get(params.getLookbackReturns().intValue()) != null);

        // AUM
        Map<Integer, List<HedgeFundScreeningFundAUMDto>> aumsMap = getQualifiedUndecidedAUMLookbackMap(params, 2, params.getLookbackAUM().intValue());
        Assert.isTrue(aumsMap.size() == 1);
        Assert.isTrue(aumsMap.get(params.getLookbackAUM().intValue()) != null);

        List<HedgeFundScreeningFundAUMDto> resultFunds =
                getIntersectingFunds(returnsMap.get(params.getLookbackReturns().intValue()), aumsMap.get(params.getLookbackAUM().intValue()));

        // Excluded funds
//        List<HedgeFundScreeningParsedDataDto>  excludedFunds = getExcludedFunds(params.getId());
//        if(excludedFunds != null && !excludedFunds.isEmpty()){
//            List<HedgeFundScreeningFundAUMDto> resultFundsWithoutExcluded = new ArrayList<>();
//            for(HedgeFundScreeningFundAUMDto fund: resultFunds){
//                boolean found = false;
//                for(HedgeFundScreeningParsedDataDto excludedFund: excludedFunds){
//                    if(fund.getFundId().longValue() == excludedFund.getFundId().longValue()){
//                        // skip this fund
//                        found = true;
//                        break;
//                    }
//                }
//                if(!found){
//                    resultFundsWithoutExcluded.add(fund);
//                }
//            }
//
//            resultFunds = resultFundsWithoutExcluded;
//        }

        if(resultFunds != null){
            List<HedgeFundScreeningParsedDataDto> fundList = getResultFundListWithAdditionalInfo(params.getScreeningId(), params.getId(), resultFunds);
            Collections.sort(fundList);
            return fundList;
        }

        return new ArrayList<>();
    }

    @Override
    public List<HedgeFundScreeningParsedDataDto> getFilteredResultUnqualifiedFundList(HedgeFundScreeningFilteredResultDto params) {
        List<HedgeFundScreeningParsedDataDto> unqualifiedList = new ArrayList<>();
        //List<HedgeFundScreeningParsedDataDto> qualifiedList = getFilteredResultQualifiedFundList(params);
        ListResponseDto qualifiedListResponse = getFilteredResultQualifiedFundList(params, false);
        if(qualifiedListResponse.getStatus() != null && qualifiedListResponse.getStatus().getCode().equalsIgnoreCase(ResponseStatusType.FAIL.getCode())){
            // Error when generating qualified fund list, scoring returned errors
            // TODO: if error?
        }
        List<HedgeFundScreeningParsedDataDto> undecidedList = getFilteredResultUndecidedFundList(params);

        List<HedgeFundScreeningParsedDataDto> parsedDataList = getParsedDataFundInfo(params.getScreeningId());
        if(parsedDataList != null && !parsedDataList.isEmpty()){
            for(HedgeFundScreeningParsedDataDto parsedDataDto: parsedDataList){
                boolean exists = false;
                for(HedgeFundScreeningParsedDataDto qualified: (List<HedgeFundScreeningParsedDataDto>)qualifiedListResponse.getRecords()){
                    if(qualified.getFundId() != null && qualified.getFundId().longValue() == parsedDataDto.getFundId().longValue()){
                        exists = true;
                        break;
                    }
                }
                if(!exists) {
                    for (HedgeFundScreeningParsedDataDto undecided : undecidedList) {
                        if(undecided.getFundId() != null && undecided.getFundId().longValue() == parsedDataDto.getFundId().longValue()){
                            exists = true;
                            break;
                        }
                    }
                    if(!exists){
                        // set fund AUM
                        unqualifiedList.add(parsedDataDto);
                    }
                }
            }
        }

        Set<String> existingAddedFunds = new HashSet<>();

        for(HedgeFundScreeningParsedDataDto qualified: (List<HedgeFundScreeningParsedDataDto>)qualifiedListResponse.getRecords()){
            if(qualified.getFundId() == null && qualified.isAdded()){
                existingAddedFunds.add(qualified.getFundName());
            }
        }
        for (HedgeFundScreeningParsedDataDto undecided : undecidedList) {
            if(undecided.getFundId() == null && undecided.isAdded()){
                existingAddedFunds.add(undecided.getFundName());
            }
        }
        List<HedgeFundScreeningParsedDataDto> addedFunds = getAddedFundsByFilteredResultId(params.getId());
        if(addedFunds != null && !addedFunds.isEmpty()){
            for(HedgeFundScreeningParsedDataDto addedFund: addedFunds) {
                if (!existingAddedFunds.contains(addedFund.getFundName())) {
                    HedgeFundScreeningParsedDataDto unqualifiedAddedFund = new HedgeFundScreeningParsedDataDto();
                    unqualifiedAddedFund.setFilteredResultId(params.getId());
                    unqualifiedAddedFund.setAdded(true);
                    unqualifiedAddedFund.setFundName(addedFund.getFundName());
                    unqualifiedAddedFund.setInvestmentManager(addedFund.getInvestmentManager());
                    unqualifiedAddedFund.setMainStrategy(addedFund.getMainStrategy());
                    unqualifiedAddedFund.setFundAUM(addedFund.getFundAUM());
                    unqualifiedAddedFund.setManagerAUM(addedFund.getManagerAUM());

                    //TODO: returns

                    unqualifiedList.add(unqualifiedAddedFund);
                }
            }
        }

        fillUnqualifiedFundsRecentValidValues(unqualifiedList, params);

        Collections.sort(unqualifiedList);
        return unqualifiedList;
    }

    @Deprecated
    @Override
    public boolean updateManagerAUM(List<HedgeFundScreeningParsedDataDto> fundList, String username) {
        if(fundList != null && !fundList.isEmpty()){
            try{
                for(HedgeFundScreeningParsedDataDto fund: fundList){
                    if(fund.getFundId() != null){
                        HedgeFundScreeningParsedData entity = this.parsedDataRepository.findByFundIdAndScreeningId(fund.getFundId(), fund.getScreening().getId());
                        entity.setManagerAUM(fund.getManagerAUM());

                        this.parsedDataRepository.save(entity);
                        logger.info("HF Screening - Successfully updated Manager AUM: fund id=" + fund.getFundId().longValue() + " [user]=" + username);
                    }
                }
                EmployeeDto updater = this.employeeService.findByUsername(username);
                if (updater != null && fundList.get(0).getScreening() != null) {
                    HedgeFundScreening screening = this.screeningRepository.findOne(fundList.get(0).getScreening().getId());
                    if(screening != null) {
                        screening.setUpdateDate(new Date());
                        screening.setUpdater(new Employee(updater.getId()));
                        this.screeningRepository.save(screening);
                    }
                }
                return true;

            }catch (Exception ex){
                logger.error("HF Screening - failed to update Manager AUM (with exception) [user]=" + username, ex);
            }

        }
        return false;
    }

    @Override
    public boolean updateFundInfo(HedgeFundScreeningParsedDataDto fund, String username) {

        if(fund.getFilteredResultId() != null && !checkFilteredResultEditable(fund.getFilteredResultId())){
            logger.error("Failed to update fund info: filtered result is not editable [" + username + "]");
            return false;
        }

        try{
            if (fund.getScreening() == null || fund.getScreening().getId() == null) {
                long screeningId = fund.getScreening() != null && fund.getScreening().getId() != null ? fund.getScreening().getId().longValue() : null;
                logger.error("HF Screening  - failed to update fund info [screening id =" + screeningId + "]: screening id required");
                return false;
            }
            if(fund.getFilteredResultId() == null){
                logger.error("HF Screening  - failed to update fund info [screening id =" + fund.getScreening().getId().longValue() +
                        ", fund id = " + fund.getFilteredResultId().longValue() + "]: filtered result id required");
                return false;
            }

            if(fund.isAdded()){
                if (StringUtils.isEmpty(fund.getFundName())) {
                    // Fund Name required
                    logger.error("HF Screening  - failed to update fund info [screening id =" + fund.getScreening().getId().longValue() +
                            ", fund id = " + fund.getFundId().longValue() + "]: fund name required");
                    return false;
                }
                if (StringUtils.isEmpty(fund.getInvestmentManager())) {
                    // Fund Name required
                    logger.error("HF Screening  - failed to update fund info [screening id =" + fund.getScreening().getId().longValue() +
                            ", fund id = " + fund.getFundId().longValue() + "]: investment manager required");
                    return false;
                }
                if (fund.getFundAUM() == null || fund.getFundAUM().doubleValue() == 0){
                    // Fund AUM required
                    logger.error("HF Screening  - failed to update fund info [screening id =" + fund.getScreening().getId().longValue() +
                            ", fund id = " + fund.getFundId().longValue() + "]: fund AUM required");
                    return false;
                }

                HedgeFundScreeningAddedFund entity = this.addedFundRepository.findByFundNameAndFilteredResultId(fund.getFundName(), fund.getFilteredResultId());
                if(entity == null){
                    entity = new HedgeFundScreeningAddedFund();
                    HedgeFundScreeningFilteredResult filteredResult = new HedgeFundScreeningFilteredResult();
                    filteredResult.setId(fund.getFilteredResultId());
                    entity.setFilteredResult(filteredResult);
                }

                List<HedgeFundScreeningParsedData> parsedFunds = this.parsedDataRepository.findByFundNameAndScreeningId(fund.getFundName(), fund.getScreening().getId());
                if(parsedFunds != null && !parsedFunds.isEmpty()){
                    logger.error("Failed to create new fund (added): fund already exists - " + fund.getFundName());
                    return false;
                }
                //entity.setFundId(fund.getFundId());
                entity.setFundName(fund.getFundName().trim());
                entity.setInvestmentManager(fund.getInvestmentManager().trim());
                entity.setMainStrategy(fund.getMainStrategy());
                entity.setFundAUM(fund.getFundAUM());
                entity.setFundAUMComment(fund.getFundAUMComment());
                entity.setFundAUMDate(fund.getFundAUMDate());
                entity.setManagerAUM(fund.getManagerAUM());

                this.addedFundRepository.save(entity);

                // save track record
                if(fund.getReturns() != null && !fund.getReturns().isEmpty()){
                    List<HedgeFundScreeningAddedFundReturn> fundReturnEntities = new ArrayList<>();
                    for(HedgeFundScreeningFundReturnDto returnDto: fund.getReturns()) {
                        if(returnDto.getDate() != null && returnDto.getValue() != null) {
                            HedgeFundScreeningAddedFundReturn returnEntity = new HedgeFundScreeningAddedFundReturn();
                            returnEntity.setAddedFund(entity);
                            Date date = DateUtils.getMM_YYYYYFormatLastDayMonthDate(returnDto.getDate());
                            if(date != null) {
                                returnEntity.setDate(date);
                                returnEntity.setValue(returnDto.getValue());
                                fundReturnEntities.add(returnEntity);
                            }
                        }
                    }
                    this.addedFundReturnRepository.deleteByFundId(entity.getId());
                    this.addedFundReturnRepository.save(fundReturnEntities);
                    // TODO: delete other returns
                }else{
                    this.addedFundReturnRepository.deleteByFundId(entity.getId());
                }

                logger.info("HF Screening - Successfully updated fund info: " + "screening id=" +fund.getScreening().getId().longValue() +
                        ", fund name=" + fund.getFundName() + " [user]=" + username);
                //return true;
            }else {
                if (fund.getEditedFundAUM() != null && fund.getEditedFundAUM().doubleValue() > 0 && StringUtils.isEmpty(fund.getEditedFundAUMComment())) {
                    // Comment required
                    logger.error("HF Screening  - failed to update fund info [screening id =" + fund.getScreening().getId().longValue() +
                            ", fund id = " + fund.getFundId().longValue() + "]: comment required");
                    return false;
                }
                if (fund.getEditedFundAUM() != null && fund.getEditedFundAUM().doubleValue() > 0 && StringUtils.isEmpty(fund.getEditedFundAUMDateMonthYear())) {
                    // Date required
                    logger.error("HF Screening  - failed to update fund info [screening id =" + fund.getScreening().getId().longValue() +
                            ", fund id = " + fund.getFundId().longValue() + "]: date required");
                    return false;
                }

                HedgeFundScreeningEditedFund editedFundEntity = this.editedFundRepository.findByFilteredResultIdAndFundId(fund.getFilteredResultId(), fund.getFundId());
                if(editedFundEntity != null){
                    if(editedFundEntity.getExcluded() != null && editedFundEntity.getExcluded().booleanValue()){
                        // excluded fund
                        logger.error("Failed to update fund info: fund is excluded; filter id " +
                                (fund.getFilteredResultId() != null ? fund.getFilteredResultId().longValue() : null) +
                                " fund id=" + (fund.getFundId() != null ? fund.getFundId().longValue() : null) +
                                " [username=" + username + "]");
                        return false;
                    }
                }else{
                    editedFundEntity = new HedgeFundScreeningEditedFund();
                    HedgeFundScreeningFilteredResultDto filteredResultDto = getFilteredResultWithoutFundsInfo(fund.getFilteredResultId());
                    if(filteredResultDto != null){

                    }else{
                        logger.error("Failed to update fund info: filter not found with id=" +
                                (fund.getFilteredResultId() != null ? fund.getFilteredResultId().longValue() : null) + " [username=" + username + "]");
                        return false;
                    }
                    HedgeFundScreeningFilteredResult filteredResult = new HedgeFundScreeningFilteredResult();
                    filteredResult.setId(fund.getFilteredResultId());
                    editedFundEntity.setFilteredResult(filteredResult);

                    HedgeFundScreeningParsedData fundScreeningParsedData =
                            this.parsedDataRepository.findByFundIdAndScreeningId(fund.getFundId(), filteredResultDto.getScreeningId());
                    editedFundEntity.setParsedData(fundScreeningParsedData);
                }

                editedFundEntity.setEditedFundAUM(fund.getEditedFundAUM());
                editedFundEntity.setEditedFundAUMComment(fund.getEditedFundAUMComment());
                if(fund.getEditedFundAUMDate() != null){
                    editedFundEntity.setEditedFundAUMDate(fund.getEditedFundAUMDate());
                }else if(fund.getEditedFundAUMDateMonthYear() != null) {
                    Date editFundAUMDate = DateUtils.getMM_YYYYYFormatLastDayMonthDate(fund.getEditedFundAUMDateMonthYear());
                    editedFundEntity.setEditedFundAUMDate(editFundAUMDate);
                }
                editedFundEntity.setManagerAUM(fund.getManagerAUM());

                this.editedFundRepository.save(editedFundEntity);
                //return true;
            }
        }catch (Exception ex){
            logger.error("HF Screening - failed to update fund info (with exception) [user]=" + username, ex);
            return false;
        }

        // UPDATE manager AUM
        // added funds
        List<HedgeFundScreeningAddedFund> addedFunds = addedFundRepository.findByFilteredResultId(fund.getFilteredResultId(), new Sort(Sort.Direction.ASC, "fundName"));
        if(addedFunds != null && !addedFunds.isEmpty()){
            for(HedgeFundScreeningAddedFund addedFund: addedFunds){
                if(addedFund.getInvestmentManager() != null && fund.getInvestmentManager() != null &&
                        addedFund.getInvestmentManager().equalsIgnoreCase(fund.getInvestmentManager())){
                    addedFund.setManagerAUM(fund.getManagerAUM());
                }
            }
            this.addedFundRepository.save(addedFunds);
        }

        // edited funds
        List<HedgeFundScreeningEditedFund> editedFunds = this.editedFundRepository.findAllByFilteredResultId(fund.getFilteredResultId());
        Set<Long> existingEditedFunds = new HashSet<>();
        if(editedFunds != null && !editedFunds.isEmpty()){
            for(HedgeFundScreeningEditedFund editedFund: editedFunds){
                if(editedFund.getParsedData() != null && editedFund.getParsedData().getInvestmentManager() != null && fund.getInvestmentManager() != null &&
                        editedFund.getParsedData().getInvestmentManager().equalsIgnoreCase(fund.getInvestmentManager())){
                    editedFund.setManagerAUM(fund.getManagerAUM());
                    existingEditedFunds.add(editedFund.getParsedData().getId());
                }
            }
            this.editedFundRepository.save(editedFunds);
        }
        List<HedgeFundScreeningParsedDataDto> parsedDataDtoList = getParsedDataFundInfo(fund.getScreening().getId());
        if(parsedDataDtoList != null && !parsedDataDtoList.isEmpty()){
            List<HedgeFundScreeningEditedFund> newEditedFunds = new ArrayList<>();
            for(HedgeFundScreeningParsedDataDto dataDto: parsedDataDtoList){
                if(fund.getManagerAUM() != null && fund.getManagerAUM().doubleValue() != 0.0 &&
                        dataDto.getInvestmentManager() != null && fund.getInvestmentManager() != null &&
                        dataDto.getInvestmentManager().equalsIgnoreCase(fund.getInvestmentManager())){
                    // check if not already added to editedFunds
                    if(existingEditedFunds.contains(dataDto.getId())){
                        // skip existing
                        continue;
                    }
                    // add manager AUM
                    HedgeFundScreeningEditedFund newEditedFund = new HedgeFundScreeningEditedFund();

                    HedgeFundScreeningFilteredResult filteredResult = new HedgeFundScreeningFilteredResult();
                    filteredResult.setId(fund.getFilteredResultId());
                    newEditedFund.setFilteredResult(filteredResult);

                    newEditedFund.setManagerAUM(fund.getManagerAUM());
                    HedgeFundScreeningParsedData parsedData = new HedgeFundScreeningParsedData();
                    parsedData.setId(dataDto.getId());
                    newEditedFund.setParsedData(parsedData);

                    newEditedFunds.add(newEditedFund);
                }
            }

            this.editedFundRepository.save(newEditedFunds);
        }

        EmployeeDto updater = this.employeeService.findByUsername(username);
        if (updater != null && fund.getScreening() != null) {
            HedgeFundScreening screening = this.screeningRepository.findOne(fund.getScreening().getId());
            if(screening != null) {
                screening.setUpdateDate(new Date());
                screening.setUpdater(new Employee(updater.getId()));
                this.screeningRepository.save(screening);
            }
        }
        logger.info("Successfully updated fund info for fund: fundid=" + (fund.getFundId() != null ? fund.getFundId().longValue() : "null") +
                ", fundname=" + fund.getFundName() + " [updater]=" + username);
        return true;
    }

    @Override
    public boolean deleteAddedFund(String fundName, Long filteredResultId, String username) {
        try {
            if(!checkFilteredResultEditable(filteredResultId)){
                logger.error("Failed to delete added fund : filtered result is not editable [" + username + "]");
                return false;
            }
            HedgeFundScreeningAddedFund addedFund = this.addedFundRepository.findByFundNameAndFilteredResultId(fundName, filteredResultId);
            if(addedFund != null) {
                // clear returns
                this.addedFundReturnRepository.deleteByFundId(addedFund.getId());

                // delete fund
                this.addedFundRepository.deleteByFundNameAndFilteredResultId(fundName, filteredResultId);

                EmployeeDto updater = this.employeeService.findByUsername(username);
                HedgeFundScreeningFilteredResultDto filteredResultDto = getFilteredResultWithoutFundsInfo(filteredResultId);
                if (updater != null && filteredResultDto != null && filteredResultDto.getScreening() != null) {
                    HedgeFundScreening screening = this.screeningRepository.findOne(filteredResultDto.getScreening().getId());
                    if(screening != null) {
                        screening.setUpdateDate(new Date());
                        screening.setUpdater(new Employee(updater.getId()));
                        this.screeningRepository.save(screening);
                    }
                }
                logger.info("Successfully deleted added fund: fundname=" + fundName + ", filterid=" + filteredResultId.longValue() + " [updater]=" + username);
                return true;
            }
        }catch (Exception ex){
            this.logger.error("Failed to delete added fund: fundName=" + fundName + ", filteredResultId=" + filteredResultId + "[user=" + username + "]", ex);
        }
        return false;
    }

    @Override
    public boolean excludeParsedFund(Long filteredResultId, Long fundId,  String excludeComment, boolean excludeFromStrategyAUM, String username){

        try {
            if(!checkFilteredResultEditable(filteredResultId)){
                logger.error("Failed to exclude fund : filtered result is not editable [" + username + "]");
                return false;
            }
            if(StringUtils.isEmpty(excludeComment)){
                logger.error("Failed to exclude fund - comment required: filter id=" +
                        (filteredResultId != null ? filteredResultId.longValue() : null) +
                        ", fund id=" + (fundId != null ? fundId.longValue() : null) + " [username=" + username + "]");
                return false;
            }
            HedgeFundScreeningEditedFund entity = this.editedFundRepository.findByFilteredResultIdAndFundId(filteredResultId, fundId);
            if (entity != null) {
                entity.setExcluded(true);
                entity.setExcludeComment(excludeComment);
                entity.setExcludeFromStrategyAUM(excludeFromStrategyAUM);
            }else{
                entity = new HedgeFundScreeningEditedFund();
                HedgeFundScreeningFilteredResult filteredResult = this.filteredResultRepository.findOne(filteredResultId);
                if(filteredResult != null) {
                    entity.setFilteredResult(filteredResult);

                    HedgeFundScreeningParsedData parsedDataEntity = this.parsedDataRepository.findByFundIdAndScreeningId(fundId, filteredResult.getScreening().getId());
                    entity.setParsedData(parsedDataEntity);

                    entity.setExcluded(true);
                    entity.setExcludeComment(excludeComment);
                    entity.setExcludeFromStrategyAUM(excludeFromStrategyAUM);
                }else{
                    logger.error("Failed to exclude fund: filter not found with id=" + filteredResultId + " [username=" + username + "]");
                    return false;
                }
            }
            editedFundRepository.save(entity);
            EmployeeDto updater = this.employeeService.findByUsername(username);
            HedgeFundScreeningFilteredResultDto filteredResultDto = getFilteredResultWithoutFundsInfo(filteredResultId);
            if (updater != null && filteredResultDto != null && filteredResultDto.getScreening() != null) {
                HedgeFundScreening screening = this.screeningRepository.findOne(filteredResultDto.getScreening().getId());
                if(screening != null) {
                    screening.setUpdateDate(new Date());
                    screening.setUpdater(new Employee(updater.getId()));
                    this.screeningRepository.save(screening);
                }
            }
            logger.info("Successfully excluded fund: fundid=" + fundId.longValue() + ", filterid=" + filteredResultId.longValue() +
                    "[updater]=" + username);
            return true;
        }catch (Exception ex){
            logger.error("Failed to exclude fund: filter id " + (filteredResultId != null ? filteredResultId.longValue() : null) +
                    ", fund id " + (fundId != null ? fundId.longValue() : null) + " [username=" + username + "]");
        }
        return false;
    }

    @Override
    public boolean includeParsedFund(Long filteredResultId, Long fundId, String username){

        try {
            if(!checkFilteredResultEditable(filteredResultId)){
                logger.error("Failed to include added fund : filtered result is not editable [" + username + "]");
                return false;
            }
            HedgeFundScreeningEditedFund entity = this.editedFundRepository.findByFilteredResultIdAndFundId(filteredResultId, fundId);
            if (entity != null) {
                entity.setExcluded(false);
                entity.setExcludeComment(null);
                entity.setExcludeFromStrategyAUM(false);
                this.editedFundRepository.save(entity);
                return true;
            }else{
                entity = new HedgeFundScreeningEditedFund();
                HedgeFundScreeningFilteredResult filteredResult = this.filteredResultRepository.findOne(filteredResultId);
                if(filteredResult != null) {
                    entity.setFilteredResult(filteredResult);

                    HedgeFundScreeningParsedData parsedDataEntity = this.parsedDataRepository.findByFundIdAndScreeningId(fundId, filteredResult.getScreening().getId());
                    entity.setParsedData(parsedDataEntity);

                    entity.setExcluded(false);
                    entity.setExcludeComment(null);
                    entity.setExcludeFromStrategyAUM(false);
                    editedFundRepository.save(entity);

                    EmployeeDto updater = this.employeeService.findByUsername(username);
                    HedgeFundScreeningFilteredResultDto filteredResultDto = getFilteredResultWithoutFundsInfo(filteredResultId);
                    if (updater != null && filteredResultDto != null && filteredResultDto.getScreening() != null) {
                        HedgeFundScreening screening = this.screeningRepository.findOne(filteredResultDto.getScreening().getId());
                        if(screening != null) {
                            screening.setUpdateDate(new Date());
                            screening.setUpdater(new Employee(updater.getId()));
                            this.screeningRepository.save(screening);
                        }
                    }
                    logger.info("Successfully included fund: fundid=" + fundId.longValue() + ", filterid=" + filteredResultId.longValue() +
                            "[updater]=" + username);
                    return true;
                }else{
                    logger.error("Failed to include fund: filter not found with id=" + filteredResultId + " [username=" + username + "]");
                    return false;
                }
            }
        }catch (Exception ex){
            logger.error("Failed to exclude fund: filter id " + (filteredResultId != null ? filteredResultId.longValue() : null) +
                    ", fund id " + (fundId != null ? fundId.longValue() : null) + " [username=" + username + "]");
        }
        return false;
    }

    private List<HedgeFundScreeningParsedDataDto> getEditedIncludedFunds(Long filteredResultId){

        List<HedgeFundScreeningEditedFund> editedFunds =
                this.editedFundRepository.findByFilteredResultIdAndExcluded(filteredResultId, false);
        if(editedFunds != null){
            List<HedgeFundScreeningParsedDataDto> funds = new ArrayList<>();
            for(HedgeFundScreeningEditedFund fund: editedFunds){
                HedgeFundScreeningParsedDataDto fundDto = parsedDataEntityConverter.disassemble(fund.getParsedData());
                // set fund aum
                List<HedgeFundScreeningParsedDataAUM> aums =
                        parsedDataAUMRepository.findByScreeningIdAndFundIdSorted(fund.getFilteredResult().getScreening().getId(),
                                fund.getParsedData().getFundId(), new Sort(Sort.Direction.DESC, "date"));
                if(aums != null && !aums.isEmpty()){
                    fundDto.setFundAUM(aums.get(0).getValue());
                    fundDto.setCurrency(aums.get(0).getReturnsCurrency());
                }
                //
                fundDto.setEditedFundAUM(fund.getEditedFundAUM());
                fundDto.setEditedFundAUMDate(fund.getEditedFundAUMDate());
                fundDto.setEditedFundAUMComment(fund.getEditedFundAUMComment());
                fundDto.setManagerAUM(fund.getManagerAUM());
                funds.add(fundDto);
            }
            return funds;
        }
        return null;
    }

    private List<HedgeFundScreeningParsedDataDto> getEditedFundsAll(Long filteredResultId){
        List<HedgeFundScreeningEditedFund> editedFunds = this.editedFundRepository.findAllByFilteredResultId(filteredResultId);
        if(editedFunds != null){
            List<HedgeFundScreeningParsedDataDto> funds = new ArrayList<>();
            for(HedgeFundScreeningEditedFund fund: editedFunds){
                HedgeFundScreeningParsedDataDto fundDto = parsedDataEntityConverter.disassemble(fund.getParsedData());
                fundDto.setEditedFundAUM(fund.getEditedFundAUM());
                fundDto.setEditedFundAUMDate(fund.getEditedFundAUMDate());
                fundDto.setEditedFundAUMComment(fund.getEditedFundAUMComment());
                fundDto.setManagerAUM(fund.getManagerAUM());
                fundDto.setExcluded(fund.getExcluded());
                fundDto.setExcludeComment(fund.getExcludeComment());
                fundDto.setExcludeFromStrategyAUM(fund.getExcludeFromStrategyAUM());
                funds.add(fundDto);
            }
            return funds;
        }
        return null;
    }

    private List<HedgeFundScreeningParsedDataDto> getExcludedFunds(Long filteredResultId){
//        List<HedgeFundScreeningParsedData> entities = this.parsedDataRepository.findExcludedFundsByScreeningId(screeningId);
//        if(entities != null){
//            List<HedgeFundScreeningParsedDataDto> funds = parsedDataEntityConverter.disassembleList(entities);
//            // set fund AUM
//            for(HedgeFundScreeningParsedDataDto fundDto: funds) {
//                HedgeFundScreeningParsedDataAUM maxAUM = this.parsedDataAUMRepository.getLastAUMByFundId(screeningId, fundDto.getFundId());
//                if(maxAUM != null) {
//                    if (maxAUM.getReturnsCurrency() != null && !maxAUM.getReturnsCurrency().equalsIgnoreCase(CurrencyLookup.USD.getCode())) {
//                        // oon-USD
//                        CurrencyRatesDto currencyRatesDto = this.currencyRatesService.getLstRateForMonthDateAndCurrencyBackwards(DateUtils.getLastDayOfCurrentMonth(maxAUM.getDate()), maxAUM.getReturnsCurrency());
//                        if (currencyRatesDto != null && currencyRatesDto.getValueUSD() != null) {
//                            Double fundAUMValueUSD = MathUtils.multiply(currencyRatesDto.getValueUSD(), maxAUM.getValue());
//                            fundDto.setFundAUM(fundAUMValueUSD);
//                            fundDto.setCurrency(maxAUM.getReturnsCurrency());
//                            fundDto.setFundAUMByCurrency(maxAUM.getValue());
//                        } else {
//                            //
//                        }
//                    } else {
//                        fundDto.setFundAUM(maxAUM.getValue());
//                    }
//                }else{}
//
//            }
//            return funds;
//        }
//        return null;
        HedgeFundScreeningFilteredResult filteredResult = filteredResultRepository.findOne(filteredResultId);
        if(filteredResult == null){
            return null;
        }
        List<HedgeFundScreeningEditedFund> editedFunds =
                this.editedFundRepository.findByFilteredResultIdAndExcluded(filteredResultId, true);
        if(editedFunds != null){
            List<HedgeFundScreeningParsedDataDto> funds = new ArrayList<>();
            for(HedgeFundScreeningEditedFund fund: editedFunds){
                HedgeFundScreeningParsedDataDto fundDto = parsedDataEntityConverter.disassemble(fund.getParsedData());
                fundDto.setExcluded(true);
                fundDto.setExcludeComment(fund.getExcludeComment());
                fundDto.setExcludeFromStrategyAUM(fund.getExcludeFromStrategyAUM() != null ? fund.getExcludeFromStrategyAUM().booleanValue() : false);

                HedgeFundScreeningParsedDataAUM lastAUM = this.parsedDataAUMRepository.getLastAUMByFundId(filteredResult.getScreening().getId(), fund.getParsedData().getFundId());
                fundDto.setFundAUM(lastAUM != null ? lastAUM.getValue(): null);

                fundDto.setEditedFundAUM(fund.getEditedFundAUM());
                fundDto.setEditedFundAUMDate(fund.getEditedFundAUMDate());
                fundDto.setEditedFundAUMComment(fund.getEditedFundAUMComment());
                fundDto.setManagerAUM(fund.getManagerAUM());
                funds.add(fundDto);
            }
            return funds;
        }
        return null;
    }

    private Map<String, HedgeFundScreeningParsedDataDto> getIncludedFundsMap(Long filteredResultId){
        Map<String, HedgeFundScreeningParsedDataDto> includedFundsMap = new HashMap<>();
        List<HedgeFundScreeningParsedDataDto> includedFunds = getIncludedFunds(filteredResultId);
        if(includedFunds != null){
            for(HedgeFundScreeningParsedDataDto fund: includedFunds){
                includedFundsMap.put(fund.getFundName(), fund);
            }
        }
        return includedFundsMap;
    }

    private List<HedgeFundScreeningParsedDataDto> getIncludedFunds(Long filteredResultId){
        List<HedgeFundScreeningEditedFund> includedFunds =
                this.editedFundRepository.findIncludedByFilteredResultId(filteredResultId);
        if(includedFunds != null){
            List<HedgeFundScreeningParsedDataDto> funds = new ArrayList<>();
            for(HedgeFundScreeningEditedFund fund: includedFunds){
                HedgeFundScreeningParsedDataDto fundDto = parsedDataEntityConverter.disassemble(fund.getParsedData());
                fundDto.setExcluded(false);
                fundDto.setExcludeComment(fund.getExcludeComment());

                fundDto.setEditedFundAUM(fund.getEditedFundAUM());
                fundDto.setEditedFundAUMDate(fund.getEditedFundAUMDate());
                fundDto.setEditedFundAUMComment(fund.getEditedFundAUMComment());
                fundDto.setManagerAUM(fund.getManagerAUM());
                funds.add(fundDto);
            }
            return funds;
        }
        return null;
    }


    private List<HedgeFundScreeningParsedDataDto> getAutoExcludedFunds(Long screeningId, Long filteredResultId){
        List<HedgeFundScreeningParsedDataDto> autoExcludedFunds = new ArrayList<>();
        List<HedgeFundScreeningParsedDataDto> parsedDataList = getParsedDataFundInfo(screeningId);
        Map<String, HedgeFundScreeningParsedDataDto> includedFunds = getIncludedFundsMap(filteredResultId);
        if(parsedDataList != null){
            for(HedgeFundScreeningParsedDataDto fundDto: parsedDataList){
                if(isLiquidating(fundDto.getFundStatus()) || isUCITS(fundDto.getFundName()) ||
                        isSICAV(fundDto.getFundName())){
                    if(includedFunds.get(fundDto.getFundName()) == null) {
                        autoExcludedFunds.add(fundDto);
                    }
                }
            }
        }
        return autoExcludedFunds;
    }

    private boolean isLiquidating(String fundStatus){
        return fundStatus !=null && fundStatus.toUpperCase().equalsIgnoreCase("IN LIQUIDATION");
    }

    private boolean isUCITS(String fundName){
        return fundName !=null && fundName.toUpperCase().contains("UCITS");
    }

    private boolean isSICAV(String fundName){
        return fundName !=null && fundName.toUpperCase().contains("SICAV");
    }

    private Integer[][] getFilteredResultStatisticsQualified(HedgeFundScreeningFilteredResultDto params){

        // 1. Returns lookback
        Map<Integer, List<HedgeFundScreeningFundCounts>> lookbackReturnMap = getQualifiedFundMapByReturnLookback(params, 0);

        // 2. AUM lookback
//        long start = new Date().getTime();
        Map<Integer, List<HedgeFundScreeningFundAUMDto>> lookbackAUMMap = getQualifiedUndecidedAUMLookbackMap(params, 1, 0);
//        long end = new Date().getTime();
//        System.out.println("AUM time = " + (end-start) / 1000.);
        Integer[][] values = getLookbackMatrix(params.getLookbackReturns().intValue(), params.getLookbackAUM().intValue(),
                lookbackReturnMap, lookbackAUMMap, params.getId());

        return values;
    }

    private Integer[][] getFilteredResultStatisticsUndecided(HedgeFundScreeningFilteredResultDto params){

        // 1. Returns lookback
        Map<Integer, List<HedgeFundScreeningFundCounts>> lookbackReturnMap = getQualifiedFundMapByReturnLookback(params, 0);

        // 2. AUM lookback
        Map<Integer, List<HedgeFundScreeningFundAUMDto>> lookbackAUMMap = getQualifiedUndecidedAUMLookbackMap(params, 2, 0);

        Integer[][] values = getLookbackMatrix(params.getLookbackReturns().intValue(), params.getLookbackAUM().intValue(),
                lookbackReturnMap, lookbackAUMMap, params.getId());
        return values;
    }


    private Map<Integer, List<HedgeFundScreeningFundCounts>> getQualifiedFundMapByReturnLookback(HedgeFundScreeningFilteredResultDto params, int lookbackStart){
        Map<Integer, List<HedgeFundScreeningFundCounts>> lookbackReturnMap = new HashMap<>();

        Date dateFromMax = DateUtils.moveDateByMonths(params.getStartDateFromTextOrCurrent(), 0 - (Math.max(0, params.getTrackRecord().intValue() + params.getLookbackReturns().intValue() - 1)));
        Assert.isTrue(DateUtils.getMonthsChanged(dateFromMax, params.getStartDateFromTextOrCurrent()) == params.getTrackRecord().intValue() + params.getLookbackReturns().intValue());

        List<HedgeFundScreeningParsedDataReturn>  fundReturns =
                this.parsedDataReturnRepository.findByScreeningIdAndDateRange(params.getScreeningId(), dateFromMax, params.getStartDateFromTextOrCurrent());

        for(int lookback = lookbackStart; lookback <= params.getLookbackReturns().intValue(); lookback++){
            // Check date
            Date dateFrom = DateUtils.getLastDayOfCurrentMonth(DateUtils.moveDateByMonths(params.getStartDateFromTextOrCurrent(), 0 - (Math.max(0, params.getTrackRecord().intValue() + lookback - 1))));
            Date dateTo = DateUtils.getLastDayOfCurrentMonth(DateUtils.moveDateByMonths(params.getStartDateFromTextOrCurrent(), (0 - lookback)));

            Map<Long, List<Date>> fundReturnCountsMap = new HashMap<>();
            if(fundReturns != null && !fundReturns.isEmpty()) {
                for(HedgeFundScreeningParsedDataReturn fundReturn: fundReturns){
                    if(fundReturn.getDate().before(dateFrom) || fundReturn.getDate().after(dateTo)){
                        // skip dates not in this lookback range
                        continue;
                    }
                    // Track record
                    if(fundReturnCountsMap.get(fundReturn.getFundId()) == null){
                        List<Date> dates = new ArrayList<>();
                        dates.add(fundReturn.getDate());
                        fundReturnCountsMap.put(fundReturn.getFundId(), dates);
                    }else{
                        fundReturnCountsMap.get(fundReturn.getFundId()).add(fundReturn.getDate());
                    }
                }
            }

            List<HedgeFundScreeningFundCounts>  validFundCounts = new ArrayList<>();
            // Added funds track record
            if(params.getId() != null) {
                List<HedgeFundScreeningAddedFundReturn> addedFundReturns = this.addedFundReturnRepository.findByFilterResultId(params.getId());
                if(addedFundReturns != null && !addedFundReturns.isEmpty()){
                    Map<String, List<Date>> addedFundReturnsMap = new HashMap<>();
                    for(HedgeFundScreeningAddedFundReturn addedFundReturn: addedFundReturns){
                        if(addedFundReturn.getDate().compareTo(dateFrom) < 0){
                            // skip dates not in this lookback range
                            continue;
                        }
                        // Track record
                        if(addedFundReturnsMap.get(addedFundReturn.getAddedFund().getFundName()) == null){
                            List<Date> dates = new ArrayList<>();
                            dates.add(addedFundReturn.getDate());
                            addedFundReturnsMap.put(addedFundReturn.getAddedFund().getFundName(), dates);
                        }else{
                            addedFundReturnsMap.get(addedFundReturn.getAddedFund().getFundName()).add(addedFundReturn.getDate());
                        }
                    }

                    Set<String> fundNames = addedFundReturnsMap.keySet();
                    Iterator<String> fundIterator = fundNames.iterator();
                    while(fundIterator.hasNext()){
                        String fundName = fundIterator.next();
                        List<Date> dates = addedFundReturnsMap.get(fundName);
                        boolean valid = getValidFundCounts(dates, params.getTrackRecord().intValue());
                        if(valid){
                            HedgeFundScreeningFundCounts fundCounts = new HedgeFundScreeningFundCounts(fundName, params.getTrackRecord().intValue());
                            validFundCounts.add(fundCounts);
                        }
                    }
                }
            }

            //List<HedgeFundScreeningFundCounts>  validFundCounts = new ArrayList<>();
            Set<Long> fundIds = fundReturnCountsMap.keySet();
            Iterator<Long> fundIterator = fundIds.iterator();
            while(fundIterator.hasNext()){
                Long fundId = fundIterator.next();
                List<Date> dates = fundReturnCountsMap.get(fundId);
                boolean valid = getValidFundCounts(dates, params.getTrackRecord().intValue());
                if(valid){
                    HedgeFundScreeningFundCounts fundCounts = new HedgeFundScreeningFundCounts(fundId, params.getTrackRecord().intValue());
                    validFundCounts.add(fundCounts);
                }
            }

            lookbackReturnMap.put(lookback, validFundCounts);
        }
        return lookbackReturnMap;
    }

    private boolean getValidFundCounts(List<Date> dates, int trackRecord){
        Collections.sort(dates);
        Date previousDate = null;
        int count = 0;
        for(Date date: dates){
            if(previousDate == null){
                previousDate = date;
                count = 1;
            }else{
                Date nextMonth = DateUtils.moveDateByMonths(previousDate, 1);
                nextMonth = DateUtils.getLastDayOfCurrentMonth(nextMonth);
                if(DateUtils.isSameDate(nextMonth, date)){
                    count++;
                    if(count == trackRecord){
                        break;
                    }
                }else{
                    count = 0;
                }
                previousDate = date;
            }
        }
        return count >= trackRecord;
    }

    @Override
    public double[] getAddedFundReturns(Long filteredResultId, String fundName, int trackRecord, Date dateFrom, Date dateTo){

        double[] returns = new double[trackRecord];

        List<HedgeFundScreeningAddedFundReturn> fundReturns =
                this.addedFundReturnRepository.findByFilteredResultIdAndFundName(filteredResultId, fundName, dateFrom, dateTo, new Sort(Sort.Direction.DESC, "date"));

        if(fundReturns != null){
            Date previousDate = null;
            int count = 0;
            int index = 0;
            for(HedgeFundScreeningAddedFundReturn fundReturn: fundReturns){
                if(previousDate == null){
                    previousDate = fundReturn.getDate();
                    count = 1;
                    returns[index] = fundReturn.getValue();
                    index++;
                }else{
                    Date nextMonth = DateUtils.moveDateByMonths(previousDate, -1);
                    nextMonth = DateUtils.getLastDayOfCurrentMonth(nextMonth);
                    //if(DateUtils.isSameMonth(nextMonth, date)){
                    if(DateUtils.isSameDate(nextMonth, fundReturn.getDate())){
                        count++;
                        returns[index] = fundReturn.getValue();
                        index++;
                        if(count == trackRecord){
                            break;
                        }
                    }else{
                        count = 0;
                        returns = new double[trackRecord];
                        index = 0;
                    }
                    previousDate = fundReturn.getDate();
                }
            }
            if(count >= trackRecord){
                return returns;
            }
        }
        return null;
    }

    @Override
    public double[] getParsedFundReturns(Long screeningId, Long fundId, int trackRecord, Date dateFrom, Date dateTo){

        double[] returns = new double[trackRecord];

        List<HedgeFundScreeningParsedDataReturn> fundReturns =
                this.parsedDataReturnRepository.findByScreeningIdAndFundIdDateRange(screeningId, fundId, dateFrom, dateTo, new Sort(Sort.Direction.DESC, "date"));

        if(fundReturns != null){
            Date previousDate = null;
            int count = 0;
            int index = 0;
            for(HedgeFundScreeningParsedDataReturn fundReturn: fundReturns){
                Double value = null;
                if(fundReturn.getReturnsCurrency() != null && !fundReturn.getReturnsCurrency().equalsIgnoreCase(CurrencyLookup.USD.getCode())){
                    // non-USD
                    Date currencyDate = DateUtils.getLastDayOfCurrentMonth(fundReturn.getDate());
                    CurrencyRatesDto currencyRatesDto = this.currencyRatesService.getLstRateForMonthDateAndCurrencyBackwards(currencyDate, fundReturn.getReturnsCurrency());
                    if(currencyRatesDto != null && currencyRatesDto.getValueUSD() != null){
                        Date prevMonth = DateUtils.getLastDayOfCurrentMonth(DateUtils.moveDateByMonths(currencyDate, -1));
                        CurrencyRatesDto prevCurrencyRatesDto = this.currencyRatesService.getLstRateForMonthDateAndCurrencyBackwards(prevMonth, fundReturn.getReturnsCurrency());
                        if(prevCurrencyRatesDto == null || prevCurrencyRatesDto.getValueUSD() == null){
                            String errorMessage = "Failed to convert non-USD fund return - missing currency rate for currency=" + fundReturn.getReturnsCurrency() +
                                    ", date=" + DateUtils.getDateFormatted(prevMonth) + "; screeningId=" + screeningId +
                                    ", fundId=" + fundId;
                            logger.error(errorMessage);
                            throw new IllegalStateException(errorMessage);
                        }
                        int scale = 10;
                        Double currencyRatio = MathUtils.divide(scale, MathUtils.subtract(scale, currencyRatesDto.getValueUSD(), prevCurrencyRatesDto.getValueUSD()), prevCurrencyRatesDto.getValueUSD());
                        value = MathUtils.multiply(scale, MathUtils.add(scale, 1.0, currencyRatio), MathUtils.add(scale, 1.0, fundReturn.getValue()));
                        value = MathUtils.subtract(scale, value, 1.0);
                    }else{
                        String errorMessage = "Failed to convert non-USD fund return - missing currency rate for currency=" + fundReturn.getReturnsCurrency() +
                                ", date=" + DateUtils.getDateFormatted(currencyDate) + "; screeningId=" + screeningId +
                                ", fundId=" + fundId;
                        logger.error(errorMessage);
                        throw new IllegalStateException(errorMessage);
                    }
                }else{
                    value = fundReturn.getValue();
                }

                if(previousDate == null){
                    previousDate = fundReturn.getDate();
                    count = 1;
                    returns[index] = value;
                    index++;
                }else{
                    Date nextMonth = DateUtils.moveDateByMonths(previousDate, -1);
                    nextMonth = DateUtils.getLastDayOfCurrentMonth(nextMonth);
                    //if(DateUtils.isSameMonth(nextMonth, date)){
                    if(DateUtils.isSameDate(nextMonth, fundReturn.getDate())){
                        count++;
                        returns[index] = value;
                        index++;
                        if(count == trackRecord){
                            break;
                        }
                    }else{
                        count = 0;
                        returns = new double[trackRecord];
                        index = 0;
                    }
                    previousDate = fundReturn.getDate();
                }
            }
            return returns;
//            if(count >= trackRecord){
//                return returns;
//            }
        }
        return null;

    }

    private Map<Integer, List<HedgeFundScreeningFundAUMDto>> getQualifiedUndecidedAUMLookbackMap(HedgeFundScreeningFilteredResultDto params, int type, int lookbackStart){
        Map<Integer, List<HedgeFundScreeningFundAUMDto>> lookbackAUMMap = new HashMap<>();

        // 1. Parsed Data Map (fund id and manager)
        List<HedgeFundScreeningParsedDataDto> parsedData = getParsedDataFundInfo(params.getScreeningId());
        Map<Long, HedgeFundScreeningParsedDataDto> parsedDataMapByFundId = new HashMap<>();
        List<HedgeFundScreeningParsedDataDto> editedFunds = getEditedIncludedFunds(params.getId());
        List<HedgeFundScreeningParsedDataDto> excludedFunds = getExcludedFunds(params.getId());
        List<HedgeFundScreeningParsedDataDto> autoExcludedFunds = getAutoExcludedFunds(params.getScreeningId(), params.getId());
        if(parsedData != null && !parsedData.isEmpty()){
            for(HedgeFundScreeningParsedDataDto dataDto: parsedData){
                if(editedFunds != null && !editedFunds.isEmpty()){
                    // Update Fund Info (fund and manager AUM)
                    for(HedgeFundScreeningParsedDataDto editedFund: editedFunds){
                        if(editedFund.getFundId().longValue() == dataDto.getFundId().longValue()){
                            dataDto.setEditedFundAUM(editedFund.getEditedFundAUM());
                            dataDto.setEditedFundAUMComment(editedFund.getEditedFundAUMComment());
                            dataDto.setEditedFundAUMDate(editedFund.getEditedFundAUMDate());
                            dataDto.setManagerAUM(editedFund.getManagerAUM());
                            break;
                        }
                    }
                }
                parsedDataMapByFundId.put(dataDto.getFundId(), dataDto);
            }
        }

        for(int lookback = lookbackStart; lookback <= params.getLookbackAUM().intValue(); lookback++){

            Date dateFromMax =  DateUtils.moveDateByMonths(params.getStartDateFromTextOrCurrent(), 0 - lookback);
            // AUM records for screening and date range
            // order by fund id, date
            List<HedgeFundScreeningParsedDataAUM>  fundsAUM = this.parsedDataAUMRepository.findByScreeningIdAndDateRange(params.getScreeningId(),
                    DateUtils.getFirstDayOfCurrentMonth(dateFromMax), DateUtils.getLastDayOfCurrentMonth(params.getStartDateFromTextOrCurrent()),
                    new Sort(Sort.Direction.DESC, "fundId", "date"));

            Long currentFund = null;
            Map<Long, HedgeFundScreeningFundAUMDto>  uniqueFundsAUM = new HashMap<>();
            //Map<String, HedgeFundScreeningStrategyAUMDto> strategyAUMByInvestorName = new HashMap<>();
            Set<String> missingCurrencyInvestors = new HashSet<>();
            for(HedgeFundScreeningParsedDataAUM fundAUM: fundsAUM){
                // sorted by fundId and date
                if(currentFund == null || currentFund.longValue() != fundAUM.getFundId().longValue()){
                    // takes first-in, i.e. most recent since it is sorted
                    currentFund = fundAUM.getFundId();

                    String investorName = parsedDataMapByFundId.get(fundAUM.getFundId()).getInvestmentManager();
                    boolean fundAUMIsUSD = fundAUM.getReturnsCurrency() == null || fundAUM.getReturnsCurrency().equalsIgnoreCase(CurrencyLookup.USD.getCode());
                    Double fundAUMValueUSD = null;
                    if(!fundAUMIsUSD){
                        // non-USD
                        CurrencyRatesDto currencyRatesDto = this.currencyRatesService.getLstRateForMonthDateAndCurrencyBackwards(DateUtils.getLastDayOfCurrentMonth(fundAUM.getDate()), fundAUM.getReturnsCurrency());
                        if(currencyRatesDto != null && currencyRatesDto.getValueUSD() != null){
                            fundAUMValueUSD = MathUtils.multiply(currencyRatesDto.getValueUSD(), fundAUM.getValue());
                        }else{
                            // Missing currency rate
                            missingCurrencyInvestors.add(investorName);
                            continue;
                        }
                    }else{
                        // USD
                        fundAUMValueUSD = fundAUM.getValue();
                    }

                    // Update strategy AUM for updated funds
                    if(parsedDataMapByFundId.get(fundAUM.getFundId()).getEditedFundAUM() != null){
                        fundAUMValueUSD = parsedDataMapByFundId.get(fundAUM.getFundId()).getEditedFundAUM();
                    }

                    // Excluded funds
                    boolean isExcluded = false;
                    boolean isStrategyAUMExcluded = false;
                    for(HedgeFundScreeningParsedDataDto excludedFund: excludedFunds){
                        if(excludedFund.isExcluded() && excludedFund.getFundId() != null && fundAUM.getFundId() != null &&
                                excludedFund.getFundId().longValue() == fundAUM.getFundId().longValue()){
                            // skip excluded funds
                            isExcluded = true;
                            if(excludedFund.isExcludeFromStrategyAUM()){
                                isStrategyAUMExcluded = true;
                            }
                            break;
                        }
                    }

                    boolean autoExcludedFund = false;
                    if(autoExcludedFunds != null){
                        for(HedgeFundScreeningParsedDataDto fund: autoExcludedFunds){
                            if(fund.getFundId() != null && fundAUM.getFundId() != null &&
                                    fund.getFundId().longValue() == fundAUM.getFundId().longValue()){
                                // auto excluded fund
                                autoExcludedFund = true;
                                break;

                            }
                        }
                    }

                    if(!isExcluded && !autoExcludedFund){
                        HedgeFundScreeningFundAUMDto fundAUMDto = new HedgeFundScreeningFundAUMDto(fundAUM.getFundId(), fundAUM.getValue(),
                                null, parsedDataMapByFundId.get(fundAUM.getFundId()).getManagerAUM(), fundAUM.getReturnsCurrency(), null, fundAUM.getDate(),
                                parsedDataMapByFundId.get(fundAUM.getFundId()).getEditedFundAUM(),
                                parsedDataMapByFundId.get(fundAUM.getFundId()).getEditedFundAUMDate(),
                                parsedDataMapByFundId.get(fundAUM.getFundId()).getEditedFundAUMComment(), false);
                        uniqueFundsAUM.put(fundAUM.getFundId(), fundAUMDto);
                    }
//                    if(!autoExcludedFund && !isStrategyAUMExcluded) {
//                        if (strategyAUMByInvestorName.get(investorName) == null) {
//                            HedgeFundScreeningStrategyAUMDto value =
//                                    new HedgeFundScreeningStrategyAUMDto(investorName, fundAUMValueUSD, fundAUM.getReturnsCurrency(), false);
//                            strategyAUMByInvestorName.put(investorName, value);
//                        } else {
//                            Double value = strategyAUMByInvestorName.get(investorName).getValue();
//                            strategyAUMByInvestorName.get(investorName).setValue(MathUtils.add(value, fundAUMValueUSD));
//                        }
//                    }
                }
            }

            // Added funds: update Strategy AUM
            List<HedgeFundScreeningParsedDataDto> addedFunds = getAddedFundsByFilteredResultId(params.getId());
//            if(addedFunds != null) {
//                for (HedgeFundScreeningParsedDataDto addedFund : addedFunds) {
//                    String investorName = addedFund.getInvestmentManager();
//                    if(investorName != null) {
//                        if (strategyAUMByInvestorName.get(investorName) == null) {
//                            strategyAUMByInvestorName.put(investorName, new HedgeFundScreeningStrategyAUMDto(investorName, 0.0, "USD", false));
//                        }
//                        Double value = strategyAUMByInvestorName.get(investorName).getValue();
//                        strategyAUMByInvestorName.get(investorName).setValue(MathUtils.add(value, addedFund.getFundAUM()));
//                    }
//                }
//            }

            // Add ucits to strategy AUM Map
//            Map<String, HedgeFundScreeningStrategyAUMDto> ucitsInvestorMap = getUcitsInvestorMapByLookback(params, lookback);
//            ucitsInvestorMap.forEach((ucitsKey, ucitsValue)->{
//                if(strategyAUMByInvestorName.get(ucitsKey) != null){
//                    if(ucitsValue != null) {
//                        strategyAUMByInvestorName.get(ucitsKey).addValue(ucitsValue.getValue());
//                    }
//                }else{
//                    strategyAUMByInvestorName.put(ucitsKey, ucitsValue);
//                }
//            });

            // Add funds with updated AUM
            if(editedFunds != null && !editedFunds.isEmpty()){
                for(HedgeFundScreeningParsedDataDto editedFund: editedFunds){
                    if(editedFund.isExcluded()){
                       continue;
                    }
                    if(autoExcludedFunds != null) {
                        boolean autoExcluded = false;
                        for (HedgeFundScreeningParsedDataDto fundDto : autoExcludedFunds) {
                            if (editedFund.getFundId() != null && fundDto.getFundId() != null &&
                                    editedFund.getFundId().longValue() == fundDto.getFundId().longValue()) {
                                autoExcluded = true;
                                break;
                            }
                        }
                        if(autoExcluded){
                            continue;
                        }
                    }
                    if(editedFund.getEditedFundAUM() != null && editedFund.getEditedFundAUM().doubleValue() > 0){
                        if(uniqueFundsAUM.get(editedFund.getFundId()) == null){
                            HedgeFundScreeningFundAUMDto fundAUMDto = new HedgeFundScreeningFundAUMDto();
                            fundAUMDto.setFundId(editedFund.getFundId());

                            fundAUMDto.setFundAUM(null);
                            fundAUMDto.setFundAUMValueUSD(null);

                            fundAUMDto.setFundAUMDate(editedFund.getEditedFundAUMDate());

                            fundAUMDto.setEditedFundAUM(editedFund.getEditedFundAUM());
                            fundAUMDto.setEditedFundAUMDate(editedFund.getEditedFundAUMDate());
                            fundAUMDto.setEditedFundAUMComment(editedFund.getEditedFundAUMComment());
                            uniqueFundsAUM.put(editedFund.getFundId(), fundAUMDto);
                        }else {
                            // update fund aum
                            HedgeFundScreeningFundAUMDto fundAUMDto = uniqueFundsAUM.get(editedFund.getFundId());
                            fundAUMDto.setEditedFundAUM(editedFund.getEditedFundAUM());
                            fundAUMDto.setEditedFundAUMDate(editedFund.getEditedFundAUMDate());
                            fundAUMDto.setEditedFundAUMComment(editedFund.getEditedFundAUMComment());
                            fundAUMDto.setManagerAUM(editedFund.getManagerAUM());
                        }

//                        String investorName = parsedDataMapByFundId.get(editedFund.getFundId()) != null ?
//                                parsedDataMapByFundId.get(editedFund.getFundId()).getInvestmentManager() : null;
//                        if(investorName != null) {
//                            if(strategyAUMByInvestorName.get(investorName) == null){
//                                strategyAUMByInvestorName.put(investorName, new HedgeFundScreeningStrategyAUMDto(investorName, 0.0, "USD", false));
//                            }
//                            strategyAUMByInvestorName.get(investorName).setValue(MathUtils.add(strategyAUMByInvestorName.get(investorName).getValue(), editedFund.getEditedFundAUM()));
//                        }
                    }
                }
            }

            Map<Long, HedgeFundScreeningFundAUMDto> fundAUMMap = new HashMap<>();
            uniqueFundsAUM.forEach((key, value)->{
                // Check Currency
                Double fundAUMValueUSD = null;
                boolean fundAUMIsUSD = value.getFundAUMCurrency() == null || value.getFundAUMCurrency().equalsIgnoreCase(CurrencyLookup.USD.getCode());
                if (!fundAUMIsUSD) {
                    // non-USD
                    CurrencyRatesDto currencyRatesDto =
                            this.currencyRatesService.getLstRateForMonthDateAndCurrencyBackwards(DateUtils.getLastDayOfCurrentMonth(value.getFundAUMDate()), value.getFundAUMCurrency());
                    if (currencyRatesDto != null && currencyRatesDto.getValueUSD() != null) {
                        fundAUMValueUSD = MathUtils.multiply(currencyRatesDto.getValueUSD(), value.getFundAUM());
                    } else {
                        // Missing currency rate
                        if (type == 1) { // QUALIFIED
                            return;
                        } else if (type == 2) { // UNDECIDED
                            HedgeFundScreeningFundAUMDto fundAUMDto = new HedgeFundScreeningFundAUMDto(value.getFundId(),
                                    value.getFundAUM(), null, value.getManagerAUM(), value.getFundAUMCurrency(), fundAUMValueUSD,
                                    value.getFundAUMDate(),value.getEditedFundAUM(), value.getEditedFundAUMDate(), value.getEditedFundAUMComment(),
                                    missingCurrencyInvestors.contains(parsedDataMapByFundId.get(value.getFundId()).getInvestmentManager()));
                            fundAUMMap.put(value.getFundId(), fundAUMDto);
                            return;
                        }
                    }
                } else {
                    fundAUMValueUSD = value.getFundAUM();
                }
                if(value.getEditedFundAUM() != null){
                    fundAUMValueUSD = value.getEditedFundAUM();
                }


                // Strategy AUM
                //String investorName = parsedDataMapByFundId.get(value.getFundId()).getInvestmentManager();
                //Double strategyAUM = strategyAUMByInvestorName.get(investorName) != null ? strategyAUMByInvestorName.get(investorName).getValue(): null;

                //boolean strategyAUMHasMissingCurrencyRates = strategyAUMByInvestorName.get(investorName) != null && strategyAUMByInvestorName.get(investorName).isHasMissingCurrencyRates();
                //strategyAUMHasMissingCurrencyRates = strategyAUMHasMissingCurrencyRates && missingCurrencyInvestors.contains(investorName);

                Double fundAUMValue = fundAUMValueUSD != null ? fundAUMValueUSD.doubleValue() : 0.0;//Math.max(strategyAUM != null ? strategyAUM : 0, (fundAUMValueUSD != null ? fundAUMValueUSD.doubleValue() : 0.0));

                if(fundAUMValue.doubleValue() >= params.getFundAUM().doubleValue()){
                    if(type == 1){
                        // QUALIFIED
                        if(fundAUMValue.doubleValue() >= params.getManagerAUM().doubleValue()) {
                            HedgeFundScreeningFundAUMDto fundAUMDto = new HedgeFundScreeningFundAUMDto(value.getFundId(),
                                    value.getFundAUM(), fundAUMValue/*strategyAUM*/, value.getManagerAUM(), value.getFundAUMCurrency(), fundAUMValueUSD,
                                    value.getFundAUMDate(), value.getEditedFundAUM(), value.getEditedFundAUMDate(), value.getEditedFundAUMComment(),
                                    missingCurrencyInvestors.contains(parsedDataMapByFundId.get(value.getFundId()).getInvestmentManager()));
                            fundAUMMap.put(value.getFundId(), fundAUMDto);
                        }else{
                            // check manager AUM
                            Double managerAUM = parsedDataMapByFundId.get(value.getFundId()) != null ?
                                    parsedDataMapByFundId.get(value.getFundId()).getManagerAUM() : null;
                            if(managerAUM != null && managerAUM.doubleValue() >= params.getManagerAUM().doubleValue()){
                                HedgeFundScreeningFundAUMDto fundAUMDto = new HedgeFundScreeningFundAUMDto(value.getFundId(),
                                        value.getFundAUM(), fundAUMValue/*strategyAUM*/, value.getManagerAUM(), value.getFundAUMCurrency(), fundAUMValueUSD, value.getFundAUMDate(),
                                        value.getEditedFundAUM(), value.getEditedFundAUMDate(), value.getEditedFundAUMComment(),
                                        missingCurrencyInvestors.contains(parsedDataMapByFundId.get(value.getFundId()).getInvestmentManager()));
                                fundAUMMap.put(value.getFundId(), fundAUMDto);
                            }
                        }
                    }else if(type == 2){
                        //UNDECIDED
                        if(fundAUMValue.doubleValue() < params.getManagerAUM().doubleValue()) {
                            // Check manager AUM
                            Double managerAUM = parsedDataMapByFundId.get(value.getFundId()).getManagerAUM();
                            if(managerAUM == null /*|| managerAUM.doubleValue() < params.getManagerAUM().doubleValue() && strategyAUMHasMissingCurrencyRates*/) {
                                //manager AUM missing
                                HedgeFundScreeningFundAUMDto fundAUMDto = new HedgeFundScreeningFundAUMDto(value.getFundId(),
                                        value.getFundAUM(), fundAUMValue/*strategyAUM*/, value.getManagerAUM(), value.getFundAUMCurrency(), fundAUMValueUSD, value.getFundAUMDate(),
                                        value.getEditedFundAUM(), value.getEditedFundAUMDate(), value.getEditedFundAUMComment(),
                                        missingCurrencyInvestors.contains(parsedDataMapByFundId.get(value.getFundId()).getInvestmentManager()));
                                fundAUMMap.put(value.getFundId(), fundAUMDto);
                            }

                        }
                        //else if(fundAUMValue.doubleValue() < params.getFundAUM().doubleValue() && strategyAUMHasMissingCurrencyRates){
                        //    HedgeFundScreeningFundAUMDto fundAUMDto = new HedgeFundScreeningFundAUMDto(value.getFundId(),
                        //            value.getFundAUM(),  fundAUMValue/*strategyAUM*/, value.getManagerAUM(), value.getFundAUMCurrency(), fundAUMValueUSD, value.getFundAUMDate(),
                        //            value.getEditedFundAUM(), value.getEditedFundAUMDate(),  value.getEditedFundAUMComment(),
                        //            missingCurrencyInvestors.contains(parsedDataMapByFundId.get(value.getFundId()).getInvestmentManager()));
                        //    fundAUMMap.put(value.getFundId(), fundAUMDto);
                        //}
                    }
                }
            });

            // Add funds that have no AUM data, but have investor manager (strategy) AUM
            //List<HedgeFundScreeningParsedDataDto> excludedFundsAll = new ArrayList<>();
            //excludedFundsAll.addAll(excludedFunds);
            //excludedFundsAll.addAll(autoExcludedFunds);
            //getFundsByStrategyAUMWithoutFundAUM(params, type, strategyAUMByInvestorName, parsedData, fundAUMMap,
            //        parsedDataMapByFundId, missingCurrencyInvestors, excludedFundsAll);

            List<HedgeFundScreeningFundAUMDto> values = new ArrayList<>(fundAUMMap.values());
            // Added funds
            if(addedFunds != null && type == 1) {
                // QUALIFIED
                for (HedgeFundScreeningParsedDataDto addedFund : addedFunds) {
                    String investorName = addedFund.getInvestmentManager();
                    if(investorName != null) {
                        //Double strategyAUM = strategyAUMByInvestorName.get(investorName) != null ?
                        //        strategyAUMByInvestorName.get(investorName).getValue() : null;
                        boolean fundAUMOK = addedFund.getFundAUM() != null && addedFund.getFundAUM().doubleValue() >= params.getFundAUM().doubleValue();
                        boolean managerAUMOK = (addedFund.getManagerAUM() != null && addedFund.getManagerAUM().doubleValue() >= params.getManagerAUM().doubleValue()) ||
                                (addedFund.getFundAUM() != null && addedFund.getFundAUM().doubleValue() >= params.getManagerAUM().doubleValue());
                        if (fundAUMOK && managerAUMOK) {
                            // add fund
                            HedgeFundScreeningFundAUMDto fund = new HedgeFundScreeningFundAUMDto();
                            fund.setFundName(addedFund.getFundName());
                            fund.setInvestmentManager(addedFund.getInvestmentManager());
                            fund.setMainStrategy(addedFund.getMainStrategy());

                            fund.setFundAUM(addedFund.getFundAUM());
                            //fund.setFundAUMDate();
                            //fund.setStrategyAUM(addedFund.getFundAUM());
                            fund.setManagerAUM(addedFund.getManagerAUM());
                            fund.setAddedFund(true);
                            values.add(fund);
                        }
                    }
                }
            }

            lookbackAUMMap.put(lookback, values);
        }
        return lookbackAUMMap;
    }

    private void getFundsByStrategyAUMWithoutFundAUM(HedgeFundScreeningFilteredResultDto params, int type,
                                                     Map<String, HedgeFundScreeningStrategyAUMDto> strategyAUMByInvestorName,
                                                     List<HedgeFundScreeningParsedDataDto> parsedData,
                                                     Map<Long, HedgeFundScreeningFundAUMDto> fundAUMMap,
                                                     Map<Long, HedgeFundScreeningParsedDataDto> parsedDataMapByFundId,
                                                     Set<String> missingCurrencyInvestors,
                                                     List<HedgeFundScreeningParsedDataDto> excludedFunds){
        // Check funds with no AUM, but investor AUM
        Set<String> missingInvestors = strategyAUMByInvestorName.keySet();
        if(missingInvestors != null && !missingInvestors.isEmpty()){
            for(String investor: missingInvestors){
                // get funds of this investor
                for(HedgeFundScreeningParsedDataDto dataEntry: parsedData){
                    if(fundAUMMap.get(dataEntry.getFundId()) != null || !dataEntry.getInvestmentManager().equalsIgnoreCase(investor)){
                        // skip, fund already added
                        continue;
                    }
                    boolean exclded = false;
                    for(HedgeFundScreeningParsedDataDto excludedFund: excludedFunds){
                        if(excludedFund.getFundId() != null &&  dataEntry.getFundId() != null &&
                                excludedFund.getFundId().longValue() == dataEntry.getFundId().longValue()){
                            exclded = true;
                            break;
                        }
                    }
                    if(exclded){
                        continue;
                    }

                    boolean strategyAUMHasMissingCurrencyRates = strategyAUMByInvestorName.get(investor) != null && strategyAUMByInvestorName.get(investor).isHasMissingCurrencyRates();
                    strategyAUMHasMissingCurrencyRates = strategyAUMHasMissingCurrencyRates &&  missingCurrencyInvestors.contains(investor);

                    Double strategyAUM = strategyAUMByInvestorName.get(investor).getValue();
                    if(strategyAUM != null) {
                        if (strategyAUM.doubleValue() >= params.getFundAUM().doubleValue()) {
                            if (type == 1) {
                                // QUALIFIED
                                if (strategyAUM.doubleValue() >= params.getManagerAUM().doubleValue()) {
                                    // add fund
                                    Double managerAUM = parsedDataMapByFundId.get(dataEntry.getFundId()) != null ?
                                            parsedDataMapByFundId.get(dataEntry.getFundId()).getManagerAUM() : null;
                                    HedgeFundScreeningFundAUMDto fundAUMDto = new HedgeFundScreeningFundAUMDto(dataEntry.getFundId(),
                                            null, strategyAUM, managerAUM, strategyAUMByInvestorName.get(investor).getCurrency(), null, null,
                                            parsedDataMapByFundId.get(dataEntry.getFundId()).getEditedFundAUM(),
                                            parsedDataMapByFundId.get(dataEntry.getFundId()).getEditedFundAUMDate(),
                                            parsedDataMapByFundId.get(dataEntry.getFundId()).getEditedFundAUMComment(),
                                            missingCurrencyInvestors.contains(investor));
                                    fundAUMMap.put(dataEntry.getFundId(), fundAUMDto);
                                    // TODO: currency null ?!

                                } else {
                                    // check manager AUM
                                    Double managerAUM = parsedDataMapByFundId.get(dataEntry.getFundId()) != null ?
                                            parsedDataMapByFundId.get(dataEntry.getFundId()).getManagerAUM() : null;
                                    if (managerAUM != null && managerAUM.doubleValue() >= params.getManagerAUM().doubleValue()) {
                                        // add fund
                                        HedgeFundScreeningFundAUMDto fundAUMDto = new HedgeFundScreeningFundAUMDto(dataEntry.getFundId(),
                                                null, strategyAUMByInvestorName.get(investor).getValue(), managerAUM, strategyAUMByInvestorName.get(investor).getCurrency(), null, null,
                                                parsedDataMapByFundId.get(dataEntry.getFundId()).getEditedFundAUM(),
                                                parsedDataMapByFundId.get(dataEntry.getFundId()).getEditedFundAUMDate(),
                                                parsedDataMapByFundId.get(dataEntry.getFundId()).getEditedFundAUMComment(),
                                                missingCurrencyInvestors.contains(investor));
                                        fundAUMMap.put(dataEntry.getFundId(), fundAUMDto);
                                    }
                                }
                            } else if (type == 2) {
                                //UNDECIDED
                                if (strategyAUM.doubleValue() < params.getManagerAUM().doubleValue()) {
                                    // Check manager AUM
                                    Double managerAUM = parsedDataMapByFundId.get(dataEntry.getFundId()).getManagerAUM();
                                    if (managerAUM == null || managerAUM.doubleValue() < params.getManagerAUM().doubleValue() && strategyAUMHasMissingCurrencyRates) {
                                        //manager AUM missing, or manager AUM less that threshold but there are missing strategy AUM (by currency)

                                        // add fund
                                        HedgeFundScreeningFundAUMDto fundAUMDto = new HedgeFundScreeningFundAUMDto(dataEntry.getFundId(),
                                                null, strategyAUMByInvestorName.get(investor).getValue(), managerAUM, strategyAUMByInvestorName.get(investor).getCurrency(), null, null,
                                                parsedDataMapByFundId.get(dataEntry.getFundId()).getEditedFundAUM(),
                                                parsedDataMapByFundId.get(dataEntry.getFundId()).getEditedFundAUMDate(),
                                                parsedDataMapByFundId.get(dataEntry.getFundId()).getEditedFundAUMComment(),
                                                missingCurrencyInvestors.contains(investor));
                                        fundAUMMap.put(dataEntry.getFundId(), fundAUMDto);
                                    }

                                } else {

                                }
                            }
                        } else if (type == 2 && strategyAUM.doubleValue() < params.getFundAUM().doubleValue() && strategyAUMHasMissingCurrencyRates) {
                            // UNDECIDED && missing currency rates

                            // add fund
                            Double managerAUM = parsedDataMapByFundId.get(dataEntry.getFundId()) != null ?
                                    parsedDataMapByFundId.get(dataEntry.getFundId()).getManagerAUM() : null;
                            HedgeFundScreeningFundAUMDto fundAUMDto = new HedgeFundScreeningFundAUMDto(dataEntry.getFundId(),
                                    null, strategyAUMByInvestorName.get(investor).getValue(), managerAUM, strategyAUMByInvestorName.get(investor).getCurrency(), null, null,
                                    parsedDataMapByFundId.get(dataEntry.getFundId()).getEditedFundAUM(),
                                    parsedDataMapByFundId.get(dataEntry.getFundId()).getEditedFundAUMDate(),
                                    parsedDataMapByFundId.get(dataEntry.getFundId()).getEditedFundAUMComment(),
                                    missingCurrencyInvestors.contains(investor));
                            fundAUMMap.put(dataEntry.getFundId(), fundAUMDto);
                        }
                    }
                }
            }
        }
    }

    private Map<String, HedgeFundScreeningStrategyAUMDto> getUcitsInvestorMapByLookback(HedgeFundScreeningFilteredResultDto params, int lookback) {

        Map<String, HedgeFundScreeningStrategyAUMDto> ucitsAUMMap = new HashMap<>();

        Map<Long, String> ucitsFundIdManagerMap = new HashMap<>();
        List<HedgeFundScreeningParsedDataDto> ucitsDataList = getParsedUcitsData(params.getScreeningId());
        for(HedgeFundScreeningParsedDataDto ucitsParsed: ucitsDataList){
            ucitsFundIdManagerMap.put(ucitsParsed.getFundId(), ucitsParsed.getInvestmentManager());
        }

        Date dateFromMax = DateUtils.moveDateByMonths(params.getStartDateFromTextOrCurrent(), 0 - lookback);
        // AUM records for screening and date range
        // order by fund id, date
        List<HedgeFundScreeningParsedUcitsDataAUM> ucitsAUM = this.parsedUcitsDataAUMRepository.findByScreeningIdAndDateRange(params.getScreeningId(),
                DateUtils.getFirstDayOfCurrentMonth(dateFromMax), DateUtils.getLastDayOfCurrentMonth(params.getStartDateFromTextOrCurrent()),
                new Sort(Sort.Direction.DESC, "fundId", "date"));

        Long currentFund = null;
        for(HedgeFundScreeningParsedUcitsDataAUM ucits: ucitsAUM){
            // sorted by fundId and date
            if(currentFund == null || currentFund.longValue() != ucits.getFundId().longValue()){
                // takes first-in, i.e. most recent since it is sorted
                currentFund = ucits.getFundId();

                String investorName = ucitsFundIdManagerMap.get(ucits.getFundId());
                HedgeFundScreeningStrategyAUMDto mapEntry = ucitsAUMMap.get(investorName);
                if(mapEntry == null){
                    mapEntry = new HedgeFundScreeningStrategyAUMDto();
                    mapEntry.setInvestorName(investorName);
                    mapEntry.setCurrency(ucits.getReturnsCurrency());
                    mapEntry.setValue(0.0);

                    ucitsAUMMap.put(investorName, mapEntry);
                }
                if(mapEntry.getCurrency() != null && !mapEntry.getCurrency().equalsIgnoreCase(CurrencyLookup.USD.getCode())) {
                    // non-USD
                    CurrencyRatesDto currencyRatesDto = this.currencyRatesService.getLstRateForMonthDateAndCurrencyBackwards(DateUtils.getLastDayOfCurrentMonth(ucits.getDate()), ucits.getReturnsCurrency());
                    if(currencyRatesDto != null && currencyRatesDto.getValueUSD() != null){
                        Double currencyValue = MathUtils.multiply(currencyRatesDto.getValueUSD(), ucits.getValue());
                        mapEntry.setValue(MathUtils.add(mapEntry.getValue(), currencyValue));
                    }else{
                        // Missing USD currency rate
                        mapEntry.setHasMissingCurrencyRates(true);
                    }
                }else{
                    // USD
                    mapEntry.setValue(MathUtils.add(mapEntry.getValue(), ucits.getValue()));
                }
            }

        }

        return ucitsAUMMap;
    }

    private Integer[][] getLookbackMatrix(int lookBackReturn, int lookbackAUM,
                                          Map<Integer, List<HedgeFundScreeningFundCounts>> lookbackReturnMap,
                                          Map<Integer,List<HedgeFundScreeningFundAUMDto>> lookbackAUMMap,
                                          Long filteredResultId){
        Integer[][] values = new Integer[lookBackReturn + 2][lookbackAUM + 2];
        for(int i = 0; i < lookBackReturn + 2; i++){
            for(int j = 0; j < lookbackAUM + 2; j++){
                if(i == 0 && j == 0){
                    values[i][j] = null;
                    continue;
                }else if(i == 0){
                    values[i][j] = j - 1;
                }else if(j == 0){
                    values[i][j] = i - 1;
                }else {
                    // TODO: filteredResultId
                    List<HedgeFundScreeningFundAUMDto> resultFunds =
                            getIntersectingFunds(lookbackReturnMap.get(i - 1), lookbackAUMMap.get(j - 1));
                    // Excluded funds
//                    List<HedgeFundScreeningParsedDataDto>  excludedFunds = getExcludedFunds(filteredResultId);
//                    if(excludedFunds != null && !excludedFunds.isEmpty()){
//                        List<HedgeFundScreeningFundAUMDto> resultFundsWithoutExcluded = new ArrayList<>();
//                        for(HedgeFundScreeningFundAUMDto fund: resultFunds){
//                            boolean found = false;
//                            for(HedgeFundScreeningParsedDataDto excludedFund: excludedFunds){
//                                if(fund.getFundId() != null && fund.getFundId().longValue() == excludedFund.getFundId().longValue()){
//                                    // skip this fund
//                                    found = true;
//                                    break;
//                                }
//                            }
//                            if(!found){
//                                resultFundsWithoutExcluded.add(fund);
//                            }
//                        }
//
//                        resultFunds = resultFundsWithoutExcluded;
//                    }
                    values[i][j] = resultFunds.size();
                }
            }
        }
        return values;
    }

    private void fillUnqualifiedFundsRecentValidValues(List<HedgeFundScreeningParsedDataDto> fundList, HedgeFundScreeningFilteredResultDto params){

        // Parsed Data Map (fund id and manager)
        List<HedgeFundScreeningParsedDataDto> parsedData = getParsedDataFundInfo(params.getScreeningId());
        Map<String, Set<Long>> parsedDataMapByManagerName = new HashMap<>();

        if(parsedData != null && !parsedData.isEmpty()){
            for(HedgeFundScreeningParsedDataDto dataDto: parsedData){
                if(parsedDataMapByManagerName.get(dataDto.getInvestmentManager()) != null){
                    parsedDataMapByManagerName.get(dataDto.getInvestmentManager()).add(dataDto.getFundId());
                }else {
                    Set<Long> dtoSet= new HashSet<>();
                    dtoSet.add(dataDto.getFundId());
                    parsedDataMapByManagerName.put(dataDto.getInvestmentManager(), dtoSet);
                }
            }
        }
        Date dateFromLookback =  DateUtils.moveDateByMonths(params.getStartDateFromTextOrCurrent(), 0 - params.getLookbackAUM().intValue());
        Date dateFrom = DateUtils.getFirstDayOfCurrentMonth(DateUtils.getDate("01.01.1970"));
        Date dateTo = params.getStartDateFromTextOrCurrent() != null ? params.getStartDateFromTextOrCurrent() : DateUtils.getFirstDayOfNextMonth(new Date());

        List<HedgeFundScreeningParsedDataDto> excludedFunds = getExcludedFunds(params.getId());
        List<HedgeFundScreeningParsedDataDto> autoExcluded = getAutoExcludedFunds(params.getScreeningId(), params.getId());
        for(HedgeFundScreeningParsedDataDto record: fundList) {
            if(record.isAdded()){
                continue;
            }
            HedgeFundScreeningParsedDataAUM  fundAUM = this.parsedDataAUMRepository.getLastAUMByDatesAndFundId(params.getScreeningId(),
                    dateFrom, dateTo, record.getFundId());
            if(fundAUM != null) {
                // Set most recent fund AUM
                if (fundAUM.getReturnsCurrency() != null && !fundAUM.getReturnsCurrency().equalsIgnoreCase(CurrencyLookup.USD.getCode())) {
                    CurrencyRatesDto currencyRatesDto =
                            this.currencyRatesService.getLstRateForMonthDateAndCurrencyBackwards(DateUtils.getLastDayOfCurrentMonth(fundAUM.getDate()), fundAUM.getReturnsCurrency());
                    record.setCurrency(fundAUM.getReturnsCurrency());
                    if (currencyRatesDto != null && currencyRatesDto.getValueUSD() != null) {
                        // check AUM date
                        if(fundAUM.getDate().compareTo(dateFromLookback) < 0) {
                            // not in lookback period
                            record.setRecentFundAUM(MathUtils.multiply(fundAUM.getValue(), currencyRatesDto.getValueUSD()));
                            record.setRecentFundAUMDate(fundAUM.getDate());
                        }else{
                            // lookback period
                            record.setFundAUM(MathUtils.multiply(fundAUM.getValue(), currencyRatesDto.getValueUSD()));
                            record.setFundAUMDate(fundAUM.getDate());
                        }
                    }else{
                        record.setFundAUMByCurrency(fundAUM.getValue());
                        record.setFundAUMDate(fundAUM.getDate());
                    }
                } else {
                    if(fundAUM.getDate().compareTo(dateFromLookback) < 0) {
                        record.setRecentFundAUM(fundAUM.getValue());
                        record.setRecentFundAUMDate(fundAUM.getDate());
                    }else{
                        record.setFundAUM(fundAUM.getValue());
                        //record.setFundAUMDate(fundAUM.getDate());
                    }
                }
            }

            // Set recent Strategy AUM
//            Double recentStrategyAUM = 0.0;
//            Date minStrategyAUMDate = null;
//            Set<Long> fundIds = parsedDataMapByManagerName.get(record.getInvestmentManager());
//            if(fundIds != null && !fundIds.isEmpty()) {
//                List<HedgeFundScreeningParsedDataAUM> strategyFundAUMs = this.parsedDataAUMRepository.getLastAUMByFundIdList(
//                        params.getScreeningId(), dateFrom, dateTo, fundIds);
//                if (strategyFundAUMs != null) {
//                    for (HedgeFundScreeningParsedDataAUM fundStrategyAUM : strategyFundAUMs) {
//                        if (fundStrategyAUM.getReturnsCurrency() != null && !fundStrategyAUM.getReturnsCurrency().equalsIgnoreCase(CurrencyLookup.USD.getCode())) {
//                            // non-USD
//                            CurrencyRatesDto currencyRatesDto =
//                                    this.currencyRatesService.getLstRateForMonthDateAndCurrencyBackwards(DateUtils.getLastDayOfCurrentMonth(fundStrategyAUM.getDate()), fundStrategyAUM.getReturnsCurrency());
//                            if (currencyRatesDto != null && currencyRatesDto.getValueUSD() != null) {
//                                recentStrategyAUM = MathUtils.add(recentStrategyAUM, MathUtils.multiply(fundStrategyAUM.getValue(), currencyRatesDto.getValueUSD()));
//                                if (minStrategyAUMDate == null || minStrategyAUMDate.compareTo(fundStrategyAUM.getDate()) > 0) {
//                                    minStrategyAUMDate = fundStrategyAUM.getDate();
//                                }
//                            }else{
//                                // missing currency
//                                record.setStrategyAUMWithMissingCurrency(true);
//                            }
//                        } else {
//                            // USD
//                            recentStrategyAUM = MathUtils.add(recentStrategyAUM, fundStrategyAUM.getValue());
//                            if (minStrategyAUMDate == null || minStrategyAUMDate.compareTo(fundStrategyAUM.getDate()) > 0) {
//                                minStrategyAUMDate = fundStrategyAUM.getDate();
//                            }
//                        }
//                    }
//                }
//
//                if (recentStrategyAUM != 0.0 && minStrategyAUMDate != null) {
//                    if(minStrategyAUMDate.compareTo(dateFromLookback) < 0) {
//                        record.setRecentStrategyAUM(recentStrategyAUM);
//                        record.setRecentStrategyAUMDate(minStrategyAUMDate);
//                    }else{
//                        record.setStrategyAUM(recentStrategyAUM);
//                    }
//                }
//            }

            // Set recent track record
            List<HedgeFundScreeningParsedDataReturn> returns =
                    this.parsedDataReturnRepository.findByScreeningIdAndFundId(params.getScreeningId(), record.getFundId(), new Sort(Sort.Direction.DESC, "date"));
            if (returns != null && returns.size() >= params.getTrackRecord().intValue()) {
                Date firstDate = null;
                Date prevDate = null;
                int dateCounter = 0;

                Date validTrackRecordStartDate = null;
                Date validTrackRecordEndDate = null;
                for (HedgeFundScreeningParsedDataReturn returnValue : returns) {
                    // MUST BE DESCENDING ORDER
                    if (firstDate != null && prevDate != null &&
                            DateUtils.getMonthsChanged(returnValue.getDate(), prevDate) == 2) {
                        prevDate = returnValue.getDate();
                        dateCounter++;

//                        if (dateCounter == params.getTrackRecord().intValue()) {
//                            record.setRecentTrackRecordDate(firstDate);
//
//                            // TODO: if track record start on params.STARTDATE not exactle on lookback date, but also valid
//                            // TODO: e.g. returns start on SEP 2019, but AUG 2019 is ok too
//                            // TODO: trackRecordCheck !!!!
//
//                            if (firstDate.compareTo(DateUtils.getLastDayOfCurrentMonth(DateUtils.moveDateByMonths(params.getStartDateFromTextOrCurrent(),(0 - params.getLookbackReturns().intValue())))) == 0) {
//                                // unqualified by AUM
//                                record.setRecentTrackRecordDateWithinLookback(true);
//                            } else {
//                                // unqualified by track record
//                                record.setRecentTrackRecordDateWithinLookback(false);
//                            }
//                            break;
//                        }
                    } else {
                        if(dateCounter >= params.getTrackRecord().intValue()){
                            // valid returns sequence
                            validTrackRecordStartDate = firstDate;
                            validTrackRecordEndDate = prevDate;
                            break;
                        }else {
                            firstDate = returnValue.getDate();
                            prevDate = returnValue.getDate();
                            dateCounter = 1;
                        }
                    }
                }

                if(validTrackRecordStartDate == null && dateCounter >= params.getTrackRecord().intValue()){
                    // valid returns sequence
                    validTrackRecordStartDate = firstDate;
                    validTrackRecordEndDate = prevDate;
                }

                if(validTrackRecordStartDate != null && validTrackRecordEndDate != null){
                    record.setRecentTrackRecordDate(validTrackRecordStartDate);
                    Date trackRecordStartDate = DateUtils.getLastDayOfCurrentMonth(DateUtils.moveDateByMonths(params.getStartDateFromTextOrCurrent(),(0 - params.getLookbackReturns().intValue())));
                    Date trackRecordEndDate = DateUtils.getLastDayOfCurrentMonth(DateUtils.moveDateByMonths(trackRecordStartDate, (0 - params.getTrackRecord().intValue() + 1)));
                    if(validTrackRecordStartDate.compareTo(trackRecordStartDate) >= 0 &&
                            validTrackRecordEndDate.compareTo(trackRecordEndDate) <= 0){
                        // track record within lookback
                        record.setRecentTrackRecordDateWithinLookback(true);
                    }else{
                        record.setRecentTrackRecordDateWithinLookback(false);
                    }
                }
            }

            // STRATEGY AUM CHECK
//            Double strategyAUMValueToCheck = null;
//            if(record.getStrategyAUM() != null && record.getStrategyAUM().doubleValue() != 0){
//                strategyAUMValueToCheck = record.getStrategyAUM();
//            }else if(record.getFundAUM() != null && record.getFundAUM().doubleValue() != 0){
//                strategyAUMValueToCheck = record.getFundAUM();
//            }

//            if(strategyAUMValueToCheck != null && strategyAUMValueToCheck.doubleValue() >= params.getFundAUM().doubleValue()){
//                record.setStrategyAUMCheck(true);
//            }else{
//                record.setStrategyAUMCheck(false);
//            }
            if(record.getFundAUM() != null && record.getFundAUM().doubleValue() >= params.getFundAUM().doubleValue()){
                record.setFundAUMCheck(true);
            }else{
                record.setFundAUMCheck(false);
            }

            // MANAGER AUM CHECK
            if((record.getManagerAUM() != null && record.getManagerAUM().doubleValue() >= params.getManagerAUM().doubleValue()) ||
                    (record.getFundAUM() != null && record.getFundAUM().doubleValue() >= params.getManagerAUM().doubleValue())){
                record.setManagerAUMCheck(true);
            }else{
                record.setManagerAUMCheck(false);
            }

            // TRACK RECORD CHECK
            if(record.getRecentTrackRecordDate() != null && record.getRecentTrackRecordDateWithinLookback()){
                record.setTrackRecordCheck(true);
            }else{
                record.setTrackRecordCheck(false);
            }

            // Excluded funds
            if(excludedFunds != null){
                for(HedgeFundScreeningParsedDataDto excludedFund: excludedFunds){
                    if(record.getFundId() != null && excludedFund.getFundId() != null &&
                            record.getFundId().longValue() == excludedFund.getFundId().longValue()){
                        record.setExcluded(true);
                        break;
                    }
                }
            }
            if(autoExcluded != null){
                for(HedgeFundScreeningParsedDataDto excludedFund: autoExcluded){
                    if(record.getFundId() != null && excludedFund.getFundId() != null &&
                            record.getFundId().longValue() == excludedFund.getFundId().longValue()){
                        record.setExcluded(true);
                        break;
                    }
                }
            }
        }
    }

    private void fillUcitsStrategyManagerAUMMap(Map<String, Double> managersMapByName, Long screeningId, Date dateFrom, Date dateTo){
        // Ucits AUM
        List<HedgeFundScreeningParsedUcitsDataAUM>  ucitsFundsAUM = this.parsedUcitsDataAUMRepository.findByScreeningIdAndDateRange(screeningId,
                DateUtils.getFirstDayOfCurrentMonth(dateFrom), DateUtils.getLastDayOfCurrentMonth(dateTo), new Sort(Sort.Direction.DESC, "fundId", "date"));
        // Parsed Data Map (fund id and manager)
        List<HedgeFundScreeningParsedDataDto> parsedUcitsData = getParsedUcitsData(screeningId);
        Map<Long, HedgeFundScreeningParsedDataDto> parsedUcitsDataMapByFundId = new HashMap<>();
        if(parsedUcitsData != null && !parsedUcitsData.isEmpty()){
            for(HedgeFundScreeningParsedDataDto dataDto: parsedUcitsData){
                parsedUcitsDataMapByFundId.put(dataDto.getFundId(), dataDto);
            }
        }
        if(ucitsFundsAUM != null && !ucitsFundsAUM.isEmpty()) {
            for(HedgeFundScreeningParsedUcitsDataAUM fundAUM:  ucitsFundsAUM){
                Double currencyRateValue = null;
                if(fundAUM.getReturnsCurrency() != null && !fundAUM.getReturnsCurrency().equalsIgnoreCase(CurrencyLookup.USD.getCode())){
                    // non-USD
                    CurrencyRatesDto currencyRatesDto = this.currencyRatesService.getLstRateForMonthDateAndCurrencyBackwards(DateUtils.getLastDayOfCurrentMonth(fundAUM.getDate()), fundAUM.getReturnsCurrency());
                    if(currencyRatesDto != null && currencyRatesDto.getValueUSD() != null){
                        currencyRateValue = currencyRatesDto.getValueUSD();
                    }else{
                        // skip this fund
                        continue;
                    }
                }
                HedgeFundScreeningParsedDataDto parsedUcitsDataDto = parsedUcitsDataMapByFundId.get(fundAUM.getFundId());
                if (parsedUcitsDataDto != null) {
                    String managerName = parsedUcitsDataDto.getInvestmentManager();
                    // Currency
                    Double valueUSD = currencyRateValue != null && fundAUM.getReturnsCurrency() != null &&
                            !fundAUM.getReturnsCurrency().equalsIgnoreCase(CurrencyLookup.USD.getCode())
                            ? MathUtils.multiply(fundAUM.getValue(), currencyRateValue) : fundAUM.getValue();
                    if (managersMapByName.get(managerName) != null) {
                        managersMapByName.put(managerName, MathUtils.add(managersMapByName.get(managerName), valueUSD));
                    } else {
                        managersMapByName.put(managerName, valueUSD);
                    }
                }
            }
        }
    }


    private List<HedgeFundScreeningFundAUMDto> getIntersectingFunds(List<HedgeFundScreeningFundCounts> returnsByFund,
                                         List<HedgeFundScreeningFundAUMDto> AUMByFund) {

        Set<Long> existingFunds = new HashSet<>();
        Set<String> existingFundNames = new HashSet<>();
        List<HedgeFundScreeningFundAUMDto> resultFunds = new ArrayList<>();
        if(returnsByFund != null && !returnsByFund.isEmpty()){
            for(HedgeFundScreeningFundCounts fundCounts: returnsByFund){
                if(fundCounts.getCount() != null && fundCounts.getCount().longValue() > 0){
                    if(fundCounts.getFundId() != null) {
                        existingFunds.add(fundCounts.getFundId());
                    }else if(fundCounts.getFundName() != null){
                        existingFundNames.add(fundCounts.getFundName());
                    }
                }
            }
        }
        if(AUMByFund != null && !AUMByFund.isEmpty()){
            for(HedgeFundScreeningFundAUMDto fundAUM: AUMByFund){
                boolean byFundAUM = fundAUM.getFundAUM() != null && fundAUM.getFundAUM().doubleValue() != 0.0;
                boolean byStrategyAUM = fundAUM.getStrategyAUM() != null && fundAUM.getStrategyAUM().doubleValue() != 0.0;
                boolean byEditedFundAUM = fundAUM.getEditedFundAUM() != null && fundAUM.getEditedFundAUM().doubleValue() != 0.0;
                if(byFundAUM || byStrategyAUM || byEditedFundAUM){
                    if (fundAUM.getFundId() != null && existingFunds.contains(fundAUM.getFundId())) {
                        resultFunds.add(fundAUM);
                    }else if(fundAUM.getFundName() != null && existingFundNames.contains(fundAUM.getFundName())){
                        resultFunds.add(fundAUM);
                    }
                }
//                if(!fundAUM.isAddedFund()) {
//                    if (fundAUM.getFundAUM() != null && fundAUM.getFundAUM().doubleValue() != 0.0) {
//                        if (fundAUM.getFundId() != null && existingFunds.contains(fundAUM.getFundId())) {
//                            resultFunds.add(fundAUM);
//                        }else if(fundAUM.getFundName() != null && existingFundNames.contains(fundAUM.getFundName())){
//                            resultFunds.add(fundAUM);
//                        }
//                    } else if (fundAUM.getStrategyAUM() != null && fundAUM.getStrategyAUM().doubleValue() != 0.0) {
//                        if (fundAUM.getFundId() != null && existingFunds.contains(fundAUM.getFundId())) {
//                            resultFunds.add(fundAUM);
//                        }else if(fundAUM.getFundName() != null && existingFundNames.contains(fundAUM.getFundName())){
//                            resultFunds.add(fundAUM);
//                        }
//                    } else if (fundAUM.getEditedFundAUM() != null && fundAUM.getEditedFundAUM().doubleValue() != 0.0) {
//                        if (fundAUM.getFundId() != null && existingFunds.contains(fundAUM.getFundId())) {
//                            resultFunds.add(fundAUM);
//                        }
//                    }
//                }else{
//                    // TODO: check track record?
//                    resultFunds.add(fundAUM);
//                }
            }
        }
        return resultFunds;
    }

    private List<HedgeFundScreeningParsedDataDto> getResultFundListWithAdditionalInfo(Long screeningId, Long filteredResultId, List<HedgeFundScreeningFundAUMDto> fundList){
        List<HedgeFundScreeningParsedDataDto> resultList = new ArrayList<>();
        List<HedgeFundScreeningParsedDataDto> parsedData = getParsedDataFundInfo(screeningId);
        for(HedgeFundScreeningFundAUMDto fund: fundList){
            if(!fund.isAddedFund() && parsedData != null) {
                for (HedgeFundScreeningParsedDataDto parsedDataDto : parsedData) {
                    if (fund.getFundId().longValue() == parsedDataDto.getFundId().longValue()) {
                        if (fund.getFundAUMCurrency() != null && !fund.getFundAUMCurrency().equalsIgnoreCase(CurrencyLookup.USD.getCode())) {
                            // non-USD
                            parsedDataDto.setCurrency(fund.getFundAUMCurrency());
                            parsedDataDto.setFundAUMByCurrency(fund.getFundAUM()); // initial value

                            parsedDataDto.setFundAUM(fund.getFundAUMValueUSD()); // USD value
                        } else {
                            // USD
                            parsedDataDto.setFundAUM(fund.getFundAUM());

                        }
                        parsedDataDto.setFundAUMDate(fund.getFundAUMDate());

                        parsedDataDto.setStrategyAUM(fund.getStrategyAUM());
                        parsedDataDto.setStrategyAUMWithMissingCurrency(fund.isStrategyAUMWithMissingCurrency());

                        parsedDataDto.setEditedFundAUM(fund.getEditedFundAUM());
                        parsedDataDto.setEditedFundAUMDate(fund.getEditedFundAUMDate());
                        parsedDataDto.setEditedFundAUMComment(fund.getEditedFundAUMComment());
                        if (fund.getEditedFundAUM() != null && fund.getEditedFundAUMDate() != null) {
                            parsedDataDto.setEditedFundAUMDateMonthYear(DateUtils.getMonthYearDate(fund.getEditedFundAUMDate()));
                        }
                        parsedDataDto.setManagerAUM(fund.getManagerAUM());

                        resultList.add(parsedDataDto);
                         break;
                    }
                }
            }else if(fund.isAddedFund()){
                HedgeFundScreeningParsedDataDto parsedDataDto = new HedgeFundScreeningParsedDataDto();
                parsedDataDto.setFundName(fund.getFundName());
                parsedDataDto.setInvestmentManager(fund.getInvestmentManager());
                parsedDataDto.setMainStrategy(fund.getMainStrategy());
                parsedDataDto.setFundAUM(fund.getFundAUM());
                parsedDataDto.setFundAUMDate(fund.getFundAUMDate());
                parsedDataDto.setStrategyAUM(fund.getStrategyAUM());
                parsedDataDto.setManagerAUM(fund.getManagerAUM());

                parsedDataDto.setAdded(true);
                parsedDataDto.setFilteredResultId(filteredResultId);

                resultList.add(parsedDataDto);
            }
        }
        return resultList;
    }


    @Override
    public FilesDto getQualifiedFundListAsStream(Long filteredResultId, int lookbackAUM, int lookbackReturn, boolean isAlternative){
        FilesDto filesDto = new FilesDto();

        Resource resource = new ClassPathResource("export_template/hf_scoring/HF_SCORING_QUALIFIED_TEMPLATE.xlsx");
        InputStream excelFileToRead = null;

        HedgeFundScreeningFilteredResultDto filteredResultDto = getFilteredResultWithoutFundsInfo(filteredResultId);
        if(filteredResultDto == null){
            logger.error("HF Scoring fund list export failed: filter not found with id " + (filteredResultId != null ? filteredResultId.longValue() : null));
            return null;
        }

        HedgeFundScreeningFilteredResultDto params = new HedgeFundScreeningFilteredResultDto();
        params.setId(filteredResultId);
        params.setScreeningId(filteredResultDto.getScreeningId());
        params.setFundAUM(filteredResultDto.getFundAUM());
        params.setManagerAUM(filteredResultDto.getManagerAUM());
        params.setLookbackAUM(lookbackAUM);
        params.setLookbackReturns(lookbackReturn);
        params.setStartDate(filteredResultDto.getStartDate());
        params.setStartDateMonth(DateUtils.getMM_YYYYYFormatDate(filteredResultDto.getStartDate()));
        params.setTrackRecord(filteredResultDto.getTrackRecord());
        ListResponseDto responseDto = getFilteredResultQualifiedFundList(params, true);
        if (isAlternative) {
            responseDto = getFilteredResultQualifiedFundListAlternative(params, true);
        }
        if(responseDto.getStatus() != null && responseDto.getStatus().getCode().equalsIgnoreCase(ResponseStatusType.FAIL.getCode())){
            logger.error("HF Scoring - Failed to export fund list: filter id=" + filteredResultId.longValue() + ", lookback AUM=" +
                    lookbackAUM + ", lookback return=" + lookbackReturn + (responseDto.getMessage() != null ? responseDto.getMessage().getMessageText() : ""));
            //return null;
        }
        try {
            excelFileToRead = resource.getInputStream();
        } catch (IOException e) {
            logger.error("Reporting: Export file template not found: 'HF_SCORING_QUALIFIED_TEMPLATE.xlsx'");
            return null;
            //e.printStackTrace();
        }


        try {
            XSSFWorkbook workbook = new XSSFWorkbook(excelFileToRead);
            XSSFSheet sheet = workbook.getSheetAt(0);
            if(responseDto != null && responseDto.getRecords() != null && !responseDto.getRecords().isEmpty()) {
                Row row1 = sheet.getRow(1);
                Row row2 = sheet.getRow(2);
                Row row3 = sheet.getRow(3);
                Row row4 = sheet.getRow(4);
                Row row5 = sheet.getRow(5);
                Row row6 = sheet.getRow(6);
                row1.getCell(2).setCellValue(NumberUtils.truncateNumber(filteredResultDto.getFundAUM(), 1));
                row2.getCell(2).setCellValue(NumberUtils.truncateNumber(filteredResultDto.getManagerAUM(), 1));
                row3.getCell(2).setCellValue(filteredResultDto.getTrackRecord());
                row4.getCell(2).setCellValue(lookbackAUM);
                row5.getCell(2).setCellValue(lookbackReturn);
                row6.getCell(2).setCellValue(DateUtils.getDateShortMonthYearEnglishTextualDate(filteredResultDto.getStartDate()));
                for (int i = 0; i < responseDto.getRecords().size(); i++) {
                    HedgeFundScreeningParsedDataDto fund  = (HedgeFundScreeningParsedDataDto) responseDto.getRecords().get(i);
                    Row row = sheet.getRow(i + 9);
                    if(row == null){
                        row = sheet.createRow(i + 9);
                        for (int j = 0; j <= 8; j++) {
                            Row prevStyleRow = sheet.getRow(i + 7);
                            if (i > 0 && prevStyleRow.getCell(j) != null && prevStyleRow.getCell(j).getCellStyle() != null) {
                                row.createCell(j).setCellStyle(prevStyleRow.getCell(j).getCellStyle());
                            }
                        }
                    }

                    ExcelUtils.setCellValueSafe(row.getCell(0), i+1);
                    ExcelUtils.setCellValueSafe(row.getCell(1), fund.getFundName());
                    ExcelUtils.setCellValueSafe(row.getCell(2), fund.getAnnualizedReturn());
                    ExcelUtils.setCellValueSafe(row.getCell(3), fund.getSortino());
                    ExcelUtils.setCellValueSafe(row.getCell(4), fund.getBeta());
                    ExcelUtils.setCellValueSafe(row.getCell(5), fund.getCfVar());
                    ExcelUtils.setCellValueSafe(row.getCell(6), fund.getAlpha());
                    ExcelUtils.setCellValueSafe(row.getCell(7), fund.getOmega());
                    ExcelUtils.setCellValueSafe(row.getCell(8), fund.getTotalScore());
                }
            }

            File tmpDir = new File(this.rootDirectory + "/tmp/hf_scoring");
            if(!tmpDir.exists()){
                tmpDir.mkdir();
            }

            // write to new
            String filePath = tmpDir + "/HF_Scoring_" + MathUtils.getRandomNumber(0, 10000) + ".xlsx";
            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }

            InputStream inputStream = new FileInputStream(filePath);
            filesDto.setInputStream(inputStream);
            filesDto.setFileName(filePath);

            HedgeFundScreeningDto screeningDto = getScreening(filteredResultDto.getScreeningId());
            String fileName = "HF Scoring - " + screeningDto.getShortName() + " (" + DateUtils.getDay(screeningDto.getDate()) + "-" +
                    (DateUtils.getMonth(screeningDto.getDate()) + 1 < 10 ? "0" : "") +
                    (DateUtils.getMonth(screeningDto.getDate()) + 1) + "-" + DateUtils.getYear(screeningDto.getDate()) + ")";
            filesDto.setOutputFileName(fileName);
            return filesDto;
        } catch (IOException e) {
            logger.error("IO Exception when exporting HF Scoring fund list", e);
        }

        return null;
    }


    @Override
    public FilesDto getUnqualifiedFundListAsStream(Long filteredResultId, int lookbackAUM, int lookbackReturn){
        FilesDto filesDto = new FilesDto();

        Resource resource = new ClassPathResource("export_template/hf_scoring/HF_SCORING_UNQUALIFIED_TEMPLATE.xlsx");
        InputStream excelFileToRead = null;

        HedgeFundScreeningFilteredResultDto filteredResultDto = getFilteredResultWithoutFundsInfo(filteredResultId);
        if(filteredResultDto == null){
            logger.error("HF Scoring fund list export failed: filter not found with id " + (filteredResultId != null ? filteredResultId.longValue() : null));
            return null;
        }

        HedgeFundScreeningFilteredResultDto params = new HedgeFundScreeningFilteredResultDto();
        params.setId(filteredResultId);
        params.setScreeningId(filteredResultDto.getScreeningId());
        params.setFundAUM(filteredResultDto.getFundAUM());
        params.setManagerAUM(filteredResultDto.getManagerAUM());
        params.setLookbackAUM(lookbackAUM);
        params.setLookbackReturns(lookbackReturn);
        params.setStartDate(filteredResultDto.getStartDate());
        params.setStartDateMonth(DateUtils.getMM_YYYYYFormatDate(filteredResultDto.getStartDate()));
        params.setTrackRecord(filteredResultDto.getTrackRecord());
        List<HedgeFundScreeningParsedDataDto> fundList = getFilteredResultUnqualifiedFundList(params);
        if(fundList == null){
            logger.error("HF Scoring - Failed to export unqualified fund list: filter id=" + filteredResultId.longValue() + ", lookback AUM=" +
                    lookbackAUM + ", lookback return=" + lookbackReturn);
            //return null;
        }
        try {
            excelFileToRead = resource.getInputStream();
        } catch (IOException e) {
            logger.error("Reporting: Export file template not found: 'HF_SCORING_UNQUALIFIED_TEMPLATE.xlsx'");
            return null;
            //e.printStackTrace();
        }


        try {
            XSSFWorkbook workbook = new XSSFWorkbook(excelFileToRead);
            XSSFSheet sheet = workbook.getSheetAt(0);
            if(fundList != null && !fundList.isEmpty()) {
                Row row1 = sheet.getRow(1);
                Row row2 = sheet.getRow(2);
                Row row3 = sheet.getRow(3);
                Row row4 = sheet.getRow(4);
                Row row5 = sheet.getRow(5);
                Row row6 = sheet.getRow(6);
                row1.getCell(2).setCellValue(NumberUtils.truncateNumber(filteredResultDto.getFundAUM(), 1));
                row2.getCell(2).setCellValue(NumberUtils.truncateNumber(filteredResultDto.getManagerAUM(), 1));
                row3.getCell(2).setCellValue(filteredResultDto.getTrackRecord());
                row4.getCell(2).setCellValue(lookbackAUM);
                row5.getCell(2).setCellValue(lookbackReturn);
                row6.getCell(2).setCellValue(DateUtils.getDateShortMonthYearEnglishTextualDate(filteredResultDto.getStartDate()));
                for (int i = 0; i < fundList.size(); i++) {
                    HedgeFundScreeningParsedDataDto fund  = (HedgeFundScreeningParsedDataDto) fundList.get(i);
                    Row row = sheet.getRow(i + 8);
                    if(row == null){
                        row = sheet.createRow(i + 8);
                    }
                    for (int j = 0; j <= 8; j++) {
                        if(row.getCell(j) == null){
                            row.createCell(j);
                        }
                        Row prevStyleRow = sheet.getRow(i + 6);
                        if (i >= 2 && prevStyleRow.getCell(j) != null && prevStyleRow.getCell(j).getCellStyle() != null) {
                            row.getCell(j).setCellStyle(prevStyleRow.getCell(j).getCellStyle());
                        }
                    }

                    ExcelUtils.setCellValueSafe(row.getCell(0), (i+1));
                    ExcelUtils.setCellValueSafe(row.getCell(1), (fund.getFundId() != null ? fund.getFundId().toString() : ""));
                    ExcelUtils.setCellValueSafe(row.getCell(2), fund.getFundName());
                    ExcelUtils.setCellValueSafe(row.getCell(3), fund.getInvestmentManager());
                    ExcelUtils.setCellValueSafe(row.getCell(4), fund.getMainStrategy());
                    if(fund.getFundAUM() != null){
                        ExcelUtils.setCellValueSafe(row.getCell(5), NumberUtils.truncateNumber(fund.getFundAUM(), 1));
                    }else if(fund.getRecentFundAUM() != null) {
                        String recentFundAUM = NumberUtils.truncateNumber(fund.getRecentFundAUM(), 1);
                        if(fund.getRecentFundAUMDate() != null){
                            recentFundAUM += " (" + DateUtils.getDateShortMonthYearEnglishTextualDate(fund.getRecentFundAUMDate()) + ")";
                        }
                        ExcelUtils.setCellValueSafe(row.getCell(5), recentFundAUM);
                    }

                    if(fund.getStrategyAUM() != null){
                        ExcelUtils.setCellValueSafe(row.getCell(6), NumberUtils.truncateNumber(fund.getStrategyAUM(), 1));
                    }else if(fund.getRecentStrategyAUM() != null) {
                        String recentStrategyAUM = NumberUtils.truncateNumber(fund.getRecentStrategyAUM(), 1);
                        if(fund.getRecentStrategyAUMDate() != null){
                            recentStrategyAUM += " (" + DateUtils.getDateShortMonthYearEnglishTextualDate(fund.getRecentStrategyAUMDate()) + ")";
                        }
                        ExcelUtils.setCellValueSafe(row.getCell(6), recentStrategyAUM);
                    }
                    //Double strategyAUM = fund.getStrategyAUM() != null ? fund.getStrategyAUM() : fund.getRecentStrategyAUM();
                    //ExcelUtils.setCellValueSafe(row.getCell(6), strategyAUM);
                    ExcelUtils.setCellValueSafe(row.getCell(7),
                            (fund.getManagerAUM() != null ? NumberUtils.truncateNumber(fund.getManagerAUM(), 1) : ""));
                    ExcelUtils.setCellValueSafe(row.getCell(8), (fund.getRecentTrackRecordDate() != null ?
                            DateUtils.getDateShortMonthYearEnglishTextualDate(fund.getRecentTrackRecordDate()) : ""));
                }
            }

            File tmpDir = new File(this.rootDirectory + "/tmp/hf_scoring");
            if(!tmpDir.exists()){
                tmpDir.mkdir();
            }

            // write to new
            String filePath = tmpDir + "/HF_Scoring_UNQUAL_" + MathUtils.getRandomNumber(0, 10000) + ".xlsx";
            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }

            InputStream inputStream = new FileInputStream(filePath);
            filesDto.setInputStream(inputStream);
            filesDto.setFileName(filePath);

            HedgeFundScreeningDto screeningDto = getScreening(filteredResultDto.getScreeningId());
            String fileName = "HF Scoring - " + screeningDto.getShortName() + " (" + DateUtils.getDay(screeningDto.getDate()) + "-" +
                    (DateUtils.getMonth(screeningDto.getDate()) + 1 < 10 ? "0" : "") +
                    (DateUtils.getMonth(screeningDto.getDate()) + 1) + "-" + DateUtils.getYear(screeningDto.getDate()) + ")";
            filesDto.setOutputFileName(fileName);
            return filesDto;
        } catch (IOException e) {
            logger.error("IO Exception when exporting HF Scoring unqualified fund list", e);
        }

        return null;
    }

    private Set<String> getUniqueCurrencies(Long screeningId){
        Set<String> currencies = new HashSet<>();
        List<String> currencies1 = this.parsedDataReturnRepository.getUniqueCurrencies(screeningId);
        List<String> currencies2 = this.parsedDataAUMRepository.getUniqueCurrencies(screeningId);
        currencies.addAll(currencies1);
        currencies.addAll(currencies2);
        return currencies;
    }

    @Override
    @Transactional
    public ResponseDto saveResults(HedgeFundScreeningSaveParamsDto saveParamsDto, String username) {
        HedgeFundScreeningFilteredResultDto params = new HedgeFundScreeningFilteredResultDto();
        HedgeFundScreeningFilteredResultDto filteredResultDto = getFilteredResultWithoutFundsInfo(saveParamsDto.getFilteredResultId());
        if(!checkFilteredResultEditable(saveParamsDto.getFilteredResultId())){
            logger.error("Failed to save final results : filtered result already saved [" + username + "]");
            ResponseDto responseDto = new ResponseDto();
            responseDto.setErrorMessageEn("Failed to save final results : filtered result already saved [" + username + "]");
            return responseDto;
        }

        params.setId(saveParamsDto.getFilteredResultId());
        params.setScreeningId(filteredResultDto.getScreeningId());
        params.setFundAUM(filteredResultDto.getFundAUM());
        params.setManagerAUM(filteredResultDto.getManagerAUM());
        params.setStartDate(filteredResultDto.getStartDate());
        params.setTrackRecord(filteredResultDto.getTrackRecord());
        params.setLookbackReturns(saveParamsDto.getLookbackReturn());
        params.setLookbackAUM(saveParamsDto.getLookbackAUM());
        // Qualified fund list
        ListResponseDto qualifiedFundListResponse = getFilteredResultQualifiedFundList(params, true);
        // Unqualified fund list
        List<HedgeFundScreeningParsedDataDto> unqualifiedFundList = getFilteredResultUnqualifiedFundList(params);
        // Undecided fund list
        List<HedgeFundScreeningParsedDataDto> undecidedFundList = getFilteredResultUndecidedFundList(params);

        // All currencies
        Set<String> currencies = getUniqueCurrencies(filteredResultDto.getScreeningId());

        // All dates
        int move = Math.max(filteredResultDto.getTrackRecord() + filteredResultDto.getLookbackReturns(), filteredResultDto.getLookbackAUM());
        Date dateFrom = DateUtils.getLastDayOfCurrentMonth(DateUtils.moveDateByMonths(filteredResultDto.getStartDate(), 0 - move - 1));
        Date dateTo = DateUtils.getLastDayOfCurrentMonth(DateUtils.moveDateByMonths(filteredResultDto.getStartDate(), 1));

        // Currency rates
        List<CurrencyRatesDto> currencyRates = this.currencyRatesService.getRatesEndOfMonthForDateRangeAndCurrencies(dateFrom, dateTo, currencies);

        // Benchmarks
        String[] benchmarks = {BenchmarkLookup.T_BILLS.getCode(), BenchmarkLookup.SNP_500_SPTR.getCode()};
        List<BenchmarkValueDto> benchmarkValues = this.benchmarkService.getBenchmarkValuesEndOfMonthForDateRangeAndTypes(dateFrom, dateTo, 10, benchmarks);

        // Save results
        HedgeFundScreeningSavedResults savedResults = new HedgeFundScreeningSavedResults();
        savedResults.setFilteredResult(new HedgeFundScreeningFilteredResult(filteredResultDto.getId()));
        savedResults.setFundAUM(filteredResultDto.getFundAUM());
        savedResults.setManagerAUM(filteredResultDto.getManagerAUM());
        savedResults.setStartDate(filteredResultDto.getStartDate());
        savedResults.setTrackRecord(filteredResultDto.getTrackRecord());
        savedResults.setLookbackAUM(filteredResultDto.getLookbackAUM());
        savedResults.setLookbackReturns(filteredResultDto.getLookbackReturns());

        savedResults.setSelectedLookbackAUM(saveParamsDto.getLookbackAUM());
        savedResults.setSelectedLookbackReturn(saveParamsDto.getLookbackReturn());

        savedResults.setCreationDate(new Date());
        EmployeeDto creatorDto = this.employeeService.findByUsername(username);
        if (creatorDto != null) {
            savedResults.setCreator(new Employee(creatorDto.getId()));
        }

        savedResults = this.screeningSavedResultsRepository.save(savedResults);

        // Save funds
        List<HedgeFundScreeningSavedResultFunds> savedResultsFunds = new ArrayList<>();
        if(!qualifiedFundListResponse.isStatusOK()) {
            // TODO: Error fund list
        }
        List<HedgeFundScreeningSavedResultFunds> qualifiedFundEntities =
                this.screeningSavedResultFundsEntityConverter.assembleList((List<HedgeFundScreeningParsedDataDto>) qualifiedFundListResponse.getRecords(), 1, savedResults.getId());

        List<HedgeFundScreeningSavedResultFunds> unqualifiedFundEntities =
                this.screeningSavedResultFundsEntityConverter.assembleList(unqualifiedFundList, 2, savedResults.getId());

        List<HedgeFundScreeningSavedResultFunds> undecidedFundEntities =
                this.screeningSavedResultFundsEntityConverter.assembleList(undecidedFundList, 3, savedResults.getId());

        savedResultsFunds.addAll(qualifiedFundEntities);
        savedResultsFunds.addAll(unqualifiedFundEntities);
        savedResultsFunds.addAll(undecidedFundEntities);

        this.screeningSavedResultFundsRepository.save(savedResultsFunds);

        // Save added funds
        List<HedgeFundScreeningParsedDataDto> addedFunds = getAddedFundsByFilteredResultId(saveParamsDto.getFilteredResultId());
        if(addedFunds != null && !addedFunds.isEmpty()) {
            for(HedgeFundScreeningParsedDataDto addedFundDto: addedFunds){
                HedgeFundScreeningSavedResultsAddedFund entity = new HedgeFundScreeningSavedResultsAddedFund();
                entity.setFundId(addedFundDto.getFundId());
                entity.setFundName(addedFundDto.getFundName());
                entity.setInvestmentManager(addedFundDto.getInvestmentManager());
                entity.setMainStrategy(addedFundDto.getMainStrategy());
                entity.setFundAUM(addedFundDto.getFundAUM());
                entity.setFundAUMDate(addedFundDto.getFundAUMDate());
                entity.setFundAUMComment(addedFundDto.getFundAUMComment());
                entity.setManagerAUM(addedFundDto.getManagerAUM());
                entity.setSavedResults(savedResults);

                this.screeningSavedResultsAddedFundRepository.save(entity);

                // fund returns
                List<HedgeFundScreeningSavedResultsAddedFundReturn> returnEntities = new ArrayList<>();
                if (addedFundDto.getReturns() != null && !addedFundDto.getReturns().isEmpty()) {
                    for (HedgeFundScreeningFundReturnDto aReturn : addedFundDto.getReturns()) {
                        HedgeFundScreeningSavedResultsAddedFundReturn returnEntity = new HedgeFundScreeningSavedResultsAddedFundReturn();
                        returnEntity.setAddedFund(entity);
                        returnEntity.setDate(DateUtils.getMM_YYYYYFormatLastDayMonthDate(aReturn.getDate()));
                        returnEntity.setValue(aReturn.getValue());
                        returnEntities.add(returnEntity);
                    }
                    this.screeningSavedResultsAddedFundReturnRepository.save(returnEntities);
                }
            }

        }

        // Save edited funds
         List<HedgeFundScreeningEditedFund> editedFunds = this.editedFundRepository.findAllByFilteredResultId(saveParamsDto.getFilteredResultId());
        if(editedFunds != null && !editedFunds.isEmpty()){
            List<HedgeFundScreeningSavedResultsEditedFund> entities = new ArrayList<>();
            for(HedgeFundScreeningEditedFund editedFund: editedFunds){
                HedgeFundScreeningSavedResultsEditedFund entity = new HedgeFundScreeningSavedResultsEditedFund();
                entity.setSavedResults(savedResults);
                entity.setParsedData(editedFund.getParsedData());
                entity.setEditedFundAUM(editedFund.getEditedFundAUM());
                entity.setEditedFundAUMDate(editedFund.getEditedFundAUMDate());
                entity.setEditedFundAUMComment(editedFund.getEditedFundAUMComment());
                entity.setManagerAUM(editedFund.getManagerAUM());
                entity.setExcluded(editedFund.getExcluded());
                entity.setExcludeComment(editedFund.getExcludeComment());
                entity.setExcludeFromStrategyAUM(editedFund.getExcludeFromStrategyAUM());
                entities.add(entity);
            }
            this.screeningSavedResultsEditedFundRepository.save(entities);
        }

        // Save currency rates
        List<HedgeFundScreeningSavedResultsCurrency> currencyRateEntities =
                this.screeningSavedResultsCurrencyEntityConverter.assembleListFromCurrencyRates(currencyRates, savedResults.getId());
        this.screeningSavedResultsCurrencyRepository.save(currencyRateEntities);

        // Save benchmarks
        List<HedgeFundScreeningSavedResultsBenchmark> benchmarkEntities =
                this.screeningSavedResultsBenchmarkEntityConverter.assembleListFromBenchmarkValues(benchmarkValues, savedResults.getId());
        this.screeningSavedResultsBenchmarkRepository.save(benchmarkEntities);

        EmployeeDto updater = this.employeeService.findByUsername(username);
        if (updater != null && filteredResultDto != null && filteredResultDto.getScreening() != null) {
            HedgeFundScreening screening = this.screeningRepository.findOne(filteredResultDto.getScreening().getId());
            if(screening != null) {
                screening.setUpdateDate(new Date());
                screening.setUpdater(new Employee(updater.getId()));
                this.screeningRepository.save(screening);
            }
        }

        ResponseDto responseDto = new ResponseDto();
        String infoMessage = "Successfully saved results: saved results id=" + savedResults.getId().longValue() +
                "; total funds saved: " + savedResultsFunds.size() + "; total currency rates saved: " + currencyRateEntities.size() +
                "; total benchmarks saved: " + benchmarkEntities.size();
        responseDto.setSuccessMessageEn(infoMessage);
        logger.info(infoMessage);
        return responseDto;
    }


    @Override
    public ResponseDto archiveSavedResultsById(Long id, String username){
        ResponseDto responseDto = new ResponseDto();
        HedgeFundScreeningSavedResults entity = this.screeningSavedResultsRepository.findOne(id);
        if(entity != null){
            EmployeeDto updater = this.employeeService.findByUsername(username);
            // archive saved results entity
            entity.setArchived(true);
            entity.setUpdateDate(new Date());
            entity.setUpdater(updater != null ? new Employee(updater.getId()) : null);
            this.screeningSavedResultsRepository.save(entity);

            HedgeFundScreeningFilteredResultDto filteredResultDto = getFilteredResultWithoutFundsInfo(entity.getFilteredResult().getId());
            if (updater != null && filteredResultDto != null && filteredResultDto.getScreening() != null) {
                HedgeFundScreening screening = this.screeningRepository.findOne(filteredResultDto.getScreening().getId());
                if(screening != null) {
                    screening.setUpdateDate(new Date());
                    screening.setUpdater(new Employee(updater.getId()));
                    this.screeningRepository.save(screening);
                }
            }

            String message = "Successfully archived saved results with id=" + id.longValue() + " [username=" + username + "]";
            logger.info(message);
            responseDto.setSuccessMessageEn(message);
            return responseDto;
        }
        String errorMessage = "Failed to archive saved results: saved results not found with id=" +
                (id != null ? id.longValue() : null) + " [username=" + username + "]";
        logger.error(errorMessage);
        responseDto.setErrorMessageEn(errorMessage);
        return responseDto;
    }

    @Override
    @Transactional
    public ResponseDto deleteSavedResultsById(Long id, String username){
        ResponseDto responseDto = new ResponseDto();
        HedgeFundScreeningSavedResultsDto savedResultsDto = getSavedResultsById(id);
        if(savedResultsDto != null){
            // delete saved benchmarks
            List<HedgeFundScreeningSavedResultsBenchmark> benchmarks = this.screeningSavedResultsBenchmarkRepository.findBySavedResultsIdOrderByBenchmarkIdDescDateAsc(id);
            this.screeningSavedResultsBenchmarkRepository.delete(benchmarks);

            // delete saved currency rates
            List<HedgeFundScreeningSavedResultsCurrency> currencyRates = this.screeningSavedResultsCurrencyRepository.findBySavedResultsIdOrderByCurrencyAscDateAsc(id);
            this.screeningSavedResultsCurrencyRepository.delete(currencyRates);

            // delete saved funds
            List<HedgeFundScreeningSavedResultFunds> funds = this.screeningSavedResultFundsRepository.findBySavedResultsId(id);
            this.screeningSavedResultFundsRepository.delete(funds);

            // delete saved results entity
            this.screeningSavedResultsRepository.delete(id);

            EmployeeDto updater = this.employeeService.findByUsername(username);
            HedgeFundScreeningFilteredResultDto filteredResultDto = getFilteredResultWithoutFundsInfo(savedResultsDto.getFilteredResult().getId());
            if (updater != null && filteredResultDto != null && filteredResultDto.getScreening() != null) {
                HedgeFundScreening screening = this.screeningRepository.findOne(filteredResultDto.getScreening().getId());
                if(screening != null) {
                    screening.setUpdateDate(new Date());
                    screening.setUpdater(new Employee(updater.getId()));
                    this.screeningRepository.save(screening);
                }
            }

            String message = "Successfully deleted saved results with id=" + id.longValue() + " [username=" + username + "]";
            logger.info(message);
            responseDto.setSuccessMessageEn(message);
            return responseDto;
        }
        String errorMessage = "Failed to delete saved results: saved results not found with id=" +
                (id != null ? id.longValue() : null) + " [username=" + username + "]";
        logger.error(errorMessage);
        responseDto.setErrorMessageEn(errorMessage);
        return responseDto;
    }

    @Override
    @Transactional
    public ResponseDto deleteFilteredResultById(Long id, String username){
        ResponseDto responseDto = new ResponseDto();
        HedgeFundScreeningFilteredResultDto filteredResultDto = getFilteredResultWithoutFundsInfo(id);
        if(filteredResultDto != null){
            if(!checkFilteredResultEditable(id)){
                String errorMessage = "Failed to delete filtered result with id=" + id.longValue() + ": entity not editable";
                logger.error(errorMessage + " [username=" + username + "]");
                responseDto.setErrorMessageEn(errorMessage);
                return responseDto;
            }
            List<HedgeFundScreeningFinalResultsDto> archivedResults = getArchivedFinalResultsByFilteredResultId(id);
            if(archivedResults != null && !archivedResults.isEmpty()) {
                String errorMessage = "Failed to delete filtered result with id=" + id.longValue() + ": archived results exist";
                logger.error(errorMessage + " [username=" + username + "]");
                responseDto.setErrorMessageEn(errorMessage);
                return responseDto;
            }

            deleteFilteredResultEntitiesById(id);

            EmployeeDto updater = this.employeeService.findByUsername(username);
            if (updater != null && filteredResultDto != null && filteredResultDto.getScreening() != null) {
                HedgeFundScreening screening = this.screeningRepository.findOne(filteredResultDto.getScreening().getId());
                if(screening != null) {
                    screening.setUpdateDate(new Date());
                    screening.setUpdater(new Employee(updater.getId()));
                    this.screeningRepository.save(screening);
                }
            }

            String message = "Successfully deleted filtered result with id=" + id.longValue();
            logger.info(message + " [username=" + username + "]");
            responseDto.setSuccessMessageEn(message);
            return responseDto;

        }
        String errorMessage = "Failed to delete filtered result: filtered result not found with id=" +
                (id != null ? id.longValue() : null);
        logger.error(errorMessage + " [username=" + username + "]");
        responseDto.setErrorMessageEn(errorMessage);
        return responseDto;
    }

    private void deleteFilteredResultEntitiesById(Long id){
        // added funds
        List<HedgeFundScreeningAddedFund> addedFunds = this.addedFundRepository.findByFilteredResultId(id, new Sort(Sort.Direction.ASC, "id"));
        // added funds: returns
        List<HedgeFundScreeningAddedFundReturn> addedFundReturns = this.addedFundReturnRepository.findByFilterResultId(id);
        this.addedFundReturnRepository.delete(addedFundReturns);
        this.addedFundRepository.delete(addedFunds);

        // edited funds & excluded
        List<HedgeFundScreeningEditedFund> editedFunds = this.editedFundRepository.findAllByFilteredResultId(id);
        this.editedFundRepository.delete(editedFunds);

        // delete filtered result entity
        this.filteredResultRepository.delete(id);

    }

    @Override
    @Transactional
    public ResponseDto deleteScreeningById(Long id, String username){
        ResponseDto responseDto = new ResponseDto();
        HedgeFundScreeningDto screeningDto = getScreening(id);
        if(screeningDto != null){
            List<HedgeFundScreeningFilteredResultDto> filteredResults = getFilteredResultsByScreeningId(id);
            if(filteredResults != null && !filteredResults.isEmpty()){
                String errorMessage = "Failed to delete screening with id=" + id.longValue() + ": filters exist";
                logger.error(errorMessage + " [username=" + username + "]");
                responseDto.setErrorMessageEn(errorMessage);
                return responseDto;
            }

            // delete parsed data
            this.parsedDataRepository.deleteByScreeningId(id);
            this.parsedDataReturnRepository.deleteByScreeningId(id);
            this.parsedDataAUMRepository.deleteByScreeningId(id);

            // delete file
            this.fileService.safeDelete(screeningDto.getFileId());

            // delete screening entity
            this.screeningRepository.delete(id);

            String message = "Successfully deleted screening with id=" + id.longValue();
            logger.info(message + " [username=" + username + "]");
            responseDto.setSuccessMessageEn(message);
            return responseDto;
        }
        String errorMessage = "Failed to delete screening: screening not found with id=" +
                (id != null ? id.longValue() : null);
        logger.error(errorMessage + " [username=" + username + "]");
        responseDto.setErrorMessageEn(errorMessage);
        return responseDto;
    }

    @Override
    public ResponseDto markAsSavedResultNonArchived(Long savedResultId, String username) {
        ResponseDto responseDto = new ResponseDto();
        HedgeFundScreeningSavedResultsDto savedResultsDto = getSavedResultsById(savedResultId);
        if(savedResultsDto == null){
            responseDto.setErrorMessageEn("Failed to mark archive as final: saved result not found id=" + savedResultId.longValue());
            return responseDto;
        }
        HedgeFundScreeningSavedResultsDto finalResult = getNonArchivedSavedResultsByFilteredResultId(savedResultsDto.getFilteredResult().getId());
        if(finalResult != null){
            String errorMessage = "Failed to mark archive as final: final saved results already exist for filter id=" + savedResultsDto.getFilteredResult().getId().longValue();
            logger.error(errorMessage);
            responseDto.setErrorMessageEn(errorMessage);
            return responseDto;
        }
        HedgeFundScreeningSavedResults savedResults = this.screeningSavedResultsRepository.findOne(savedResultId);
        savedResults.setArchived(false);
        this.screeningSavedResultsRepository.save(savedResults);
        responseDto.setSuccessMessageEn("Successfully marked archive as final");
        return responseDto;
    }

    private HedgeFundScreeningSavedResultsDto getSavedResultsById(Long id){
        HedgeFundScreeningSavedResults entity = this.screeningSavedResultsRepository.findOne(id);
        if(entity != null){
            HedgeFundScreeningSavedResultsDto resultsDto = new HedgeFundScreeningSavedResultsDto();
            resultsDto.setFilteredResult(this.filteredResultEntityConverter.disassemble(entity.getFilteredResult()));
            resultsDto.setSelectedLookbackAUM(entity.getSelectedLookbackAUM());
            resultsDto.setSelectedLookbackReturn(entity.getSelectedLookbackReturn());

            resultsDto.setId(entity.getId());
            if(entity.getCreator() != null) {
                resultsDto.setCreator(entity.getCreator().getUsername());
            }
            resultsDto.setCreationDate(entity.getCreationDate());
            if(entity.getUpdater() != null) {
                resultsDto.setUpdater(entity.getUpdater().getUsername());
            }
            resultsDto.setUpdateDate(entity.getUpdateDate());
            return resultsDto;
        }
        return null;
    }

    private List<HedgeFundScreeningSavedResultsDto> getArchivedSavedResultsByFilteredResultId(Long filteredResultId){
        List<HedgeFundScreeningSavedResultsDto> resultsList = new ArrayList<>();
        List<HedgeFundScreeningSavedResults> savedResultsList = this.screeningSavedResultsRepository.findByFilteredResultIdAndArchived(filteredResultId, true);
        if(savedResultsList != null && !savedResultsList.isEmpty()){
            for(HedgeFundScreeningSavedResults savedResult: savedResultsList){
                HedgeFundScreeningSavedResultsDto resultsDto = new HedgeFundScreeningSavedResultsDto();
                HedgeFundScreeningFilteredResult filteredResult = this.filteredResultRepository.findOne(filteredResultId);
                HedgeFundScreeningFilteredResultDto filteredResultDto = this.filteredResultEntityConverter.disassemble(filteredResult);
                resultsDto.setFilteredResult(filteredResultDto);
                resultsDto.setFundAUM(savedResult.getFundAUM());
                resultsDto.setManagerAUM(savedResult.getManagerAUM());
                resultsDto.setStartDate(savedResult.getStartDate());
                resultsDto.setTrackRecord(savedResult.getTrackRecord());
                resultsDto.setLookbackAUM(savedResult.getLookbackAUM());
                resultsDto.setLookbackReturns(savedResult.getLookbackReturns());
                resultsDto.setDescription(savedResult.getDescription());
                resultsDto.setSelectedLookbackAUM(savedResult.getSelectedLookbackAUM());
                resultsDto.setSelectedLookbackReturn(savedResult.getSelectedLookbackReturn());

                resultsDto.setCreationDate(savedResult.getCreationDate());
                resultsDto.setCreator(savedResult.getCreator() != null ? savedResult.getCreator().getUsername() : null);
                resultsDto.setUpdateDate(savedResult.getUpdateDate());
                resultsDto.setUpdater(savedResult.getUpdater() != null ? savedResult.getUpdater().getUsername() : null);

                resultsDto.setId(savedResult.getId());
                if(savedResult.getCreator() != null) {
                    resultsDto.setCreator(savedResult.getCreator().getUsername());
                }
                resultsDto.setCreationDate(savedResult.getCreationDate());
                if(savedResult.getUpdater() != null) {
                    resultsDto.setUpdater(savedResult.getUpdater().getUsername());
                }
                resultsDto.setUpdateDate(savedResult.getUpdateDate());

                resultsList.add(resultsDto);
            }

            return resultsList;
        }
        return null;
    }

    private HedgeFundScreeningSavedResultsDto getNonArchivedSavedResultsByFilteredResultId(Long filteredResultId){
        HedgeFundScreeningSavedResultsDto resultsDto = new HedgeFundScreeningSavedResultsDto();
        List<HedgeFundScreeningSavedResults> savedResultsList = this.screeningSavedResultsRepository.findByFilteredResultIdAndArchived(filteredResultId, false);
        if(savedResultsList != null && !savedResultsList.isEmpty()){
            HedgeFundScreeningFilteredResult filteredResult = this.filteredResultRepository.findOne(filteredResultId);
            HedgeFundScreeningFilteredResultDto filteredResultDto = this.filteredResultEntityConverter.disassemble(filteredResult);
            resultsDto.setFilteredResult(filteredResultDto);
            resultsDto.setFundAUM(savedResultsList.get(0).getFundAUM());
            resultsDto.setManagerAUM(savedResultsList.get(0).getManagerAUM());
            resultsDto.setStartDate(savedResultsList.get(0).getStartDate());
            resultsDto.setTrackRecord(savedResultsList.get(0).getTrackRecord());
            resultsDto.setLookbackAUM(savedResultsList.get(0).getLookbackAUM());
            resultsDto.setLookbackReturns(savedResultsList.get(0).getLookbackReturns());
            resultsDto.setDescription(savedResultsList.get(0).getDescription());
            resultsDto.setSelectedLookbackAUM(savedResultsList.get(0).getSelectedLookbackAUM());
            resultsDto.setSelectedLookbackReturn(savedResultsList.get(0).getSelectedLookbackReturn());

            resultsDto.setId(savedResultsList.get(0).getId());
            if(savedResultsList.get(0).getCreator() != null) {
                resultsDto.setCreator(savedResultsList.get(0).getCreator().getUsername());
            }
            resultsDto.setCreationDate(savedResultsList.get(0).getCreationDate());
            if(savedResultsList.get(0).getUpdater() != null) {
                resultsDto.setUpdater(savedResultsList.get(0).getUpdater().getUsername());
            }
            resultsDto.setUpdateDate(savedResultsList.get(0).getUpdateDate());
            return resultsDto;
        }
        return null;

    }

    private List<HedgeFundScreeningSavedResultFundsDto> getSavedResultFundsBySavedResultsId(Long savedResultsId){
        List<HedgeFundScreeningSavedResultFundsDto> savedResultFunds = new ArrayList<>();
        List<HedgeFundScreeningSavedResultFunds> foundEntities = this.screeningSavedResultFundsRepository.findBySavedResultsId(savedResultsId);
        if(foundEntities != null && !foundEntities.isEmpty()) {
            savedResultFunds = this.screeningSavedResultFundsEntityConverter.disassembleList(foundEntities);
        }
        return savedResultFunds;
    }

    private boolean hasSavedResultsByFilterId(Long filterResultId){
        List<HedgeFundScreeningSavedResults> savedResultsList = this.screeningSavedResultsRepository.findByFilteredResultId(filterResultId);
        int size = savedResultsList != null && !savedResultsList.isEmpty() ? savedResultsList.size() : 0;
        return size > 0;
    }


//    private int checkQualifiedFundList(HedgeFundScreeningFilteredResultDto params){
//        StrBuilder sb = new StrBuilder();
//        List<HedgeFundScreeningParsedDataDto> fundList = getFilteredResultQualifiedFundList(params);
//        List<HedgeFundScreeningParsedDataDto> fundList_ = getFilteredResultQualifiedFundList_(params);
//        int errors = 0;
//        if(fundList.size() != fundList_.size()){
//            sb.append("QUALIFIED FUND LIST SIZE MISMATCH:" + fundList.size() + ", " + fundList_.size());
//            sb.append("\n");
//            errors++;
//        }
//        for(int i = 0; i < fundList.size(); i++){
//            // Fund Id
//            if(fundList.get(i).getFundId().longValue() != fundList_.get(i).getFundId().longValue()){
//                sb.append("QUALIFIED FUND LIST (FundId) at i=" + i + " : " + fundList.get(i).getFundId().longValue() + ", " + + fundList_.get(i).getFundId().longValue());
//                sb.append("\n");
//                errors++;
//            }
//            // Fund AUM
//            if(NumberUtils.getDouble(fundList.get(i).getFundAUM()) != NumberUtils.getDouble(fundList_.get(i).getFundAUM())){
//                sb.append("QUALIFIED FUND LIST (FundAUM) at i=" + i + " : " + fundList.get(i).getFundAUM().doubleValue() + ", " + + fundList_.get(i).getFundAUM().doubleValue());
//                sb.append("\n");
//                errors++;
//            }
//
//            // Strategy AUM
//            if(NumberUtils.getDouble(fundList.get(i).getStrategyAUM()) != NumberUtils.getDouble(fundList_.get(i).getStrategyAUM())){
//                sb.append("QUALIFIED FUND LIST (strategyAUM) at i=" + i + " : " + fundList.get(i).getStrategyAUM().doubleValue() + ", " + + fundList_.get(i).getStrategyAUM().doubleValue());
//                sb.append("\n");
//                errors++;
//            }
//        }
//        if(errors > 0){
//            System.out.println("CHECKING QUALIFIED FUND LIST: " + "Return l=" + params.getLookbackReturns() + ", AUM l=" + params.getLookbackAUM());
//            //System.out.println("QUALIFIED FUND LIST with Errors: " + errors);
//            System.out.println(sb.toString());
//        }else{
//            //System.out.println("QUALIFIED FUND LIST - (Return l=" + params.getLookbackReturns() + ", AUM l=" + params.getLookbackAUM() + ") - OK");
//        }
//
//        return errors;
//    }

    private int checkUndecidedFundList(HedgeFundScreeningFilteredResultDto params){
        StrBuilder sb = new StrBuilder();
        String startMessage = "CHECKING UNDECIDED FUND LIST: " + "Return l=" + params.getLookbackReturns() + ", AUM l=" + params.getLookbackAUM();
        List<HedgeFundScreeningParsedDataDto> fundList = getFilteredResultUndecidedFundList(params);
        List<HedgeFundScreeningParsedDataDto> fundList_ = getFilteredResultUndecidedFundList_(params);
        int errors = 0;
        if(fundList.size() != fundList_.size()){
            System.out.println("UNDECIDED FUND LIST SIZE MISMATCH:" + fundList.size() + ", " + fundList_.size());
            sb.append("\n");
            errors++;
        }
        for(int i = 0; i < fundList.size(); i++){
            // Fund Id
            if(fundList.get(i).getFundId().longValue() != fundList_.get(i).getFundId().longValue()){
                System.out.println("UNDECIDED FUND LIST (FundId) at i=" + i + " : " + fundList.get(i).getFundId().longValue() + ", " + + fundList_.get(i).getFundId().longValue());
                sb.append("\n");
                errors++;
            }
            // Fund AUM
            if(NumberUtils.getDouble(fundList.get(i).getFundAUM()) != NumberUtils.getDouble(fundList_.get(i).getFundAUM())){
                System.out.println("UNDECIDED FUND LIST (FundAUM) at i=" + i + " : " + fundList.get(i).getFundAUM().doubleValue() + ", " + + fundList_.get(i).getFundAUM().doubleValue());
                sb.append("\n");
                errors++;
            }

            // Fund AUM by Currency
            if(NumberUtils.getDouble(fundList.get(i).getFundAUMByCurrency()) != NumberUtils.getDouble(fundList_.get(i).getFundAUMByCurrency())){
                System.out.println("UNDECIDED FUND LIST (FundAUMByCurrency) at i=" + i + " : " + fundList.get(i).getFundAUM().doubleValue() + ", " + + fundList_.get(i).getFundAUM().doubleValue());
                sb.append("\n");
                errors++;
            }

            // Strategy AUM
            if(NumberUtils.getDouble(fundList.get(i).getStrategyAUM()) != NumberUtils.getDouble(fundList_.get(i).getStrategyAUM())){
                System.out.println("UNDECIDED FUND LIST (strategyAUM) at i=" + i + " : " + fundList.get(i).getStrategyAUM().doubleValue() + ", " + + fundList_.get(i).getStrategyAUM().doubleValue());
                sb.append("\n");
                errors++;
            }
        }
        if(errors > 0){
            //System.out.println("UNDECIDED FUND LIST with Errors: " + errors);
            System.out.println(startMessage);
            System.out.println(sb.toString());
        }else{
            //System.out.println("UNDECIDED FUND LIST - (Return l=" + params.getLookbackReturns() + ", AUM l=" + params.getLookbackAUM() + ") - OK");
        }

        return errors;
    }

//    private void checkMatrixWithFundList(HedgeFundScreeningFilteredResultStatisticsDto statisticsDto, HedgeFundScreeningFilteredResultDto params){
//
//        int errorCount = 0;
//        // TEST QUALIFIED
//        System.out.println("CHECK MATRIX - QUALIFIED... ");
//        if(statisticsDto.getQualified() != null && statisticsDto.getQualified().length > 0){
//            for(int i = 1; i < statisticsDto.getQualified().length; i++){
//                for(int j = 1; j < statisticsDto.getQualified()[i].length; j++){
//                    int count = statisticsDto.getQualified()[i][j];
//
//                    HedgeFundScreeningFilteredResultDto newParams = new HedgeFundScreeningFilteredResultDto(params);
//                    newParams.setLookbackReturns(i - 1);
//                    newParams.setLookbackAUM(j - 1);
//                    List<HedgeFundScreeningParsedDataDto> fundList = getFilteredResultQualifiedFundList(newParams);
//
//                    if(fundList.size() != count){
//                        System.out.println((i - 1) + "-" + (j - 1) + ": MISMATCH stats=" + count + ", fundlist=" + fundList.size());
//                        errorCount++;
//                    }else{
//                        //System.out.println((i - 1) + "-" + (j - 1) + ": OK");
//                    }
//                }
//            }
//        }
//        if(errorCount == 0){
//            System.out.println("CHECK QUALIFIED: OK");
//        }
//
//        // TEST UNDECIDED
//        errorCount = 0;
//        System.out.println("CHECK MATRIX - UNDECIDED... ");
//        if(statisticsDto.getQualified() != null && statisticsDto.getUndecided().length > 0){
//            for(int i = 1; i < statisticsDto.getUndecided().length; i++){
//                for(int j = 1; j < statisticsDto.getUndecided()[i].length; j++){
//                    int count = statisticsDto.getUndecided()[i][j];
//
//                    HedgeFundScreeningFilteredResultDto newParams = new HedgeFundScreeningFilteredResultDto(params);
//                    newParams.setLookbackReturns(i - 1);
//                    newParams.setLookbackAUM(j - 1);
//                    List<HedgeFundScreeningParsedDataDto> fundList = getFilteredResultUndecidedFundList(newParams);
//
//                    if(fundList.size() != count){
//                        System.out.println((i - 1) + "-" + (j - 1) + ": MISMATCH stats=" + count + ", fundlist=" + fundList.size());
//                        errorCount++;
//                    }else{
//                        //System.out.println((i - 1) + "-" + (j - 1) + ": OK");
//                    }
//                }
//            }
//        }
//
//        if(errorCount == 0){
//            System.out.println("CHECK UNDECIDED: OK");
//        }
//
//
//        // TEST UNQUALIFIED
////        errorCount = 0;
////        System.out.println("CHECK MATRIX - UNQUALIFIED...");
////        if(statisticsDto.getQualified() != null && statisticsDto.getUnqualified().length > 0){
////            for(int i = 1; i < statisticsDto.getUnqualified().length; i++){
////                for(int j = 1; j < statisticsDto.getUnqualified()[i].length; j++){
////                    int count = statisticsDto.getUnqualified()[i][j];
////
////                    HedgeFundScreeningFilteredResultDto newParams = new HedgeFundScreeningFilteredResultDto(params);
////                    newParams.setLookbackReturns(i - 1);
////                    newParams.setLookbackAUM(j - 1);
////                    List<HedgeFundScreeningParsedDataDto> fundList = getFilteredResultUnqualifiedFundList(newParams);
////
////                    if(fundList.size() != count){
////                        System.out.println((i - 1) + "-" + (j - 1) + ": MISMATCH stats=" + count + ", fundlist=" + fundList.size());
////                        errorCount++;
////                    }else{
////                        //System.out.println((i - 1) + "-" + (j - 1) + ": OK");
////                    }
////                }
////            }
////        }
////
////        if(errorCount == 0){
////            System.out.println("CHECK UNQUALIFIED: OK");
////        }
//
//        System.out.println("CHECK MATRIX FINISHED");
//    }


    /************ DEPRECATED **/
    @Deprecated
    //@Override
    public List<HedgeFundScreeningParsedDataDto> getFilteredResultQualifiedFundList_(HedgeFundScreeningFilteredResultDto params) {


        // RETURNS
        Map<Integer, List<HedgeFundScreeningFundCounts>> returnsMap = getQualifiedFundMapByReturnLookback(params, params.getLookbackReturns().intValue());
        Assert.isTrue(returnsMap.size() == 1);
        Assert.isTrue(returnsMap.get(params.getLookbackReturns().intValue()) != null);


        // AUM
        Map<Integer, List<HedgeFundScreeningParsedDataAUM>> aumsMap = getQualifiedUndecidedAUMLookbackMap_(params, 1, params.getLookbackAUM().intValue());
        Assert.isTrue(aumsMap.size() == 1);
        Assert.isTrue(aumsMap.get(params.getLookbackAUM().intValue()) != null);

        Long[] fundIds = getIntersectingFunds__(returnsMap.get(params.getLookbackReturns().intValue()), aumsMap.get(params.getLookbackAUM().intValue()));

        if(fundIds != null){
            List<HedgeFundScreeningParsedDataDto> fundList =  getQualifiedOrUndecidedFundListByTypeAndManagerAUM(params, aumsMap.get(params.getLookbackAUM().intValue()), fundIds, 1);
            Collections.sort(fundList);
            return fundList;
        }

        return new ArrayList<>();
    }

    @Deprecated
    //@Override
    public List<HedgeFundScreeningParsedDataDto> getFilteredResultUndecidedFundList_(HedgeFundScreeningFilteredResultDto params)  {
//        // RETURNS
//        List<HedgeFundScreeningFundCounts>  validFundCounts = getQualifiedFundReturnCounts(params, params.getLookbackReturns().intValue());
//
//        // AUM
//        List<HedgeFundScreeningParsedDataAUM>  undecidedFundsAUM = getAUMFundListByTypeAndLookback(params, params.getLookbackAUM().intValue(), 2);
//
//        Map<Long, HedgeFundScreeningParsedDataAUM> validFundsAUMMap = new HashMap<>();
//        for(HedgeFundScreeningParsedDataAUM fundAUM: undecidedFundsAUM){
//            validFundsAUMMap.put(fundAUM.getFundId(), fundAUM);
//        }
//
//        Long[] fundIds = getIntersectingFunds(validFundCounts, undecidedFundsAUM);
//
//        if(fundIds != null){
//            List<HedgeFundScreeningParsedDataDto> undecidedFundList = getQualifiedOrUndecidedFundListByTypeAndManagerAUM(params, undecidedFundsAUM, fundIds, 2);
//            Collections.sort(undecidedFundList);
//            return undecidedFundList;
//        }
//
//        return  new ArrayList<>();
//
//

        // RETURNS
        Map<Integer, List<HedgeFundScreeningFundCounts>> returnsMap = getQualifiedFundMapByReturnLookback(params, params.getLookbackReturns().intValue());
        Assert.isTrue(returnsMap.size() == 1);
        Assert.isTrue(returnsMap.get(params.getLookbackReturns().intValue()) != null);

        // AUM
        Map<Integer, List<HedgeFundScreeningParsedDataAUM>> aumsMap = getQualifiedUndecidedAUMLookbackMap_(params, 2, params.getLookbackAUM().intValue());
        Assert.isTrue(aumsMap.size() == 1);
        Assert.isTrue(aumsMap.get(params.getLookbackAUM().intValue()) != null);

        Long[] fundIds = getIntersectingFunds__(returnsMap.get(params.getLookbackReturns().intValue()), aumsMap.get(params.getLookbackAUM().intValue()));

        if(fundIds != null){
            List<HedgeFundScreeningParsedDataDto> fundList =  getQualifiedOrUndecidedFundListByTypeAndManagerAUM(params, aumsMap.get(params.getLookbackAUM().intValue()), fundIds, 2);
            Collections.sort(fundList);
            return fundList;
        }

        return new ArrayList<>();
    }

    @Deprecated
    private List<HedgeFundScreeningParsedDataDto> getQualifiedOrUndecidedFundListByTypeAndManagerAUM(HedgeFundScreeningFilteredResultDto params,
                                                                                                     List<HedgeFundScreeningParsedDataAUM>  validFundsAUM,
                                                                                                     Long[] fundIds, int type){

        List<HedgeFundScreeningParsedDataDto> fundList = new ArrayList<>();

        Map<Long, HedgeFundScreeningParsedDataAUM> validFundsAUMMap = new HashMap<>();
        for(HedgeFundScreeningParsedDataAUM fundAUM: validFundsAUM){
            validFundsAUMMap.put(fundAUM.getFundId(), fundAUM);
        }

        // Parsed Data Map (fund id and manager)
        List<HedgeFundScreeningParsedDataDto> parsedData = getParsedDataFundInfo(params.getScreeningId());
        Map<Long, HedgeFundScreeningParsedDataDto> parsedDataMapByFundId = new HashMap<>();
        if(parsedData != null && !parsedData.isEmpty()){
            for(HedgeFundScreeningParsedDataDto dataDto: parsedData){
                parsedDataMapByFundId.put(dataDto.getFundId(), dataDto);
            }
        }

        // Set up manager map
        Date date =  DateUtils.moveDateByMonths(params.getStartDateFromTextOrCurrent(), 0 - params.getLookbackAUM().intValue());
        Map<String, Double> managersMapByName = getManagerTotalStrategyAUMMap(parsedDataMapByFundId, validFundsAUM, params.getScreeningId(), date);

        for(int i = 0; i < fundIds.length; i++){
            HedgeFundScreeningParsedData entity = this.parsedDataRepository.findByFundIdAndScreeningId(fundIds[i], params.getScreeningId());
            if(entity != null){
                HedgeFundScreeningParsedDataDto dto = this.parsedDataEntityConverter.disassemble(entity);
                dto.setFundAUM(validFundsAUMMap.get(entity.getFundId()).getValue());
                if(validFundsAUMMap.get(entity.getFundId()).getReturnsCurrency() != null &&
                        !validFundsAUMMap.get(entity.getFundId()).getReturnsCurrency().equalsIgnoreCase(CurrencyLookup.USD.getCode())){
                    // non-USD
                    CurrencyRatesDto currencyRatesDto =
                            this.currencyRatesService.getLstRateForMonthDateAndCurrencyBackwards(DateUtils.getLastDayOfCurrentMonth(validFundsAUMMap.get(entity.getFundId()).getDate()), validFundsAUMMap.get(entity.getFundId()).getReturnsCurrency());
                    if(currencyRatesDto != null && currencyRatesDto.getValueUSD() != null){
                        Double valueUSD = MathUtils.multiply(currencyRatesDto.getValueUSD(), validFundsAUMMap.get(entity.getFundId()).getValue());
                        dto.setFundAUM(valueUSD);

                        dto.setCurrency(validFundsAUMMap.get(entity.getFundId()).getReturnsCurrency());
                        dto.setFundAUMByCurrency(validFundsAUMMap.get(entity.getFundId()).getValue());
                    }else{
                        // Missing currency rate
                        if(type == 2){// UNDECIDED
                            dto.setFundAUM(null);
                            dto.setCurrency(validFundsAUMMap.get(entity.getFundId()).getReturnsCurrency());
                            dto.setFundAUMByCurrency(validFundsAUMMap.get(entity.getFundId()).getValue());
                            fundList.add(dto);
                        }
                        continue;
                    }
                }
                // Strategy AUM
                Double strategyAUM = managersMapByName.get(dto.getInvestmentManager());
                dto.setStrategyAUM(strategyAUM);

                // MANAGER AUM
                if(type == 1) {
                    // QUALIFIED
                    if (dto.getFundAUM().doubleValue() >= params.getManagerAUM().doubleValue() ||
                            (managersMapByName.get(dto.getInvestmentManager()) != null &&
                                    managersMapByName.get(dto.getInvestmentManager()).doubleValue() >= params.getManagerAUM().doubleValue()) ||
                            dto.getManagerAUM() != null && dto.getManagerAUM().doubleValue() >= params.getManagerAUM().doubleValue()) {
                        fundList.add(dto);
                    }
                }else if(type == 2) {
                    // UNDECIDED
                    if ((managersMapByName.get(dto.getInvestmentManager()) == null ||
                            managersMapByName.get(dto.getInvestmentManager()).doubleValue() < params.getManagerAUM().doubleValue()) &&
                            dto.getFundAUM().doubleValue() < params.getManagerAUM().doubleValue() &&
                            dto.getManagerAUM() == null) {
                        fundList.add(dto);
                    }
                }
            }
        }


        return fundList;
    }

    @Deprecated
    private Integer[][] getFilteredResultStatisticsQualified_(HedgeFundScreeningFilteredResultDto params){

        // 1. Returns lookback
        Map<Integer, List<HedgeFundScreeningFundCounts>> lookbackReturnMap = getQualifiedFundMapByReturnLookback(params, 0);
        // 2. AUM lookback
//        long start = new Date().getTime();
        Map<Integer, List<HedgeFundScreeningParsedDataAUM>> lookbackAUMMap = getQualifiedUndecidedAUMLookbackMap_(params, 1, 0);
//        long end = new Date().getTime();
//        System.out.println("AUM time = " + (end-start) / 1000.);
        Integer[][] values = getLookbackMatrix_(params.getLookbackReturns().intValue(), params.getLookbackAUM().intValue(),
                lookbackReturnMap, lookbackAUMMap, params, 1);

        return values;
    }

    @Deprecated
    private Integer[][] getFilteredResultStatisticsUndecided_(HedgeFundScreeningFilteredResultDto params){

        // 1. Returns lookback
        Map<Integer, List<HedgeFundScreeningFundCounts>> lookbackReturnMap = getQualifiedFundMapByReturnLookback(params, 0);

        // 2. AUM lookback
        Map<Integer, List<HedgeFundScreeningParsedDataAUM>> lookbackAUMMap = getQualifiedUndecidedAUMLookbackMap_(params, 2, 0);

        Integer[][] values = getLookbackMatrix_(params.getLookbackReturns().intValue(), params.getLookbackAUM().intValue(),
                lookbackReturnMap, lookbackAUMMap, params, 2);

//        // Returns
//        List<HedgeFundScreeningFundCounts> returns = lookbackReturnMap.get(12);
//        Long[] returnIds = new Long[returns.size()];
//        int index = 0;
//        for(HedgeFundScreeningFundCounts fundCounts: returns){
//            returnIds[index] = fundCounts.getFundId();
//            index++;
//        }
//
//        Arrays.sort(returnIds);
//        System.out.println("Return l = 12:");
//        for(int i = 0; i < returnIds.length; i++){
//            System.out.println(returnIds[i]);
//        }
//
//
//        // AUM
//        List<HedgeFundScreeningParsedDataAUM> aums = lookbackAUMMap.get(4);
//        Long[] aumIds = new Long[aums.size()];
//        index = 0;
//        for(HedgeFundScreeningParsedDataAUM fundCounts: aums){
//            aumIds[index] = fundCounts.getFundId();
//            index++;
//        }
//
//        Arrays.sort(aumIds);
//        System.out.println("AUM l = 4:");
//        for(int i = 0; i < aumIds.length; i++){
//            System.out.println(aumIds[i]);
//        }
        return values;
    }

    @Deprecated
    private Map<Integer, List<HedgeFundScreeningParsedDataAUM>> getQualifiedUndecidedAUMLookbackMap_(HedgeFundScreeningFilteredResultDto params, int type, int lookbackStart){
        Map<Integer, List<HedgeFundScreeningParsedDataAUM>> lookbackAUMMap = new HashMap<>();

        // Parsed Data Map (fund id and manager)
        List<HedgeFundScreeningParsedDataDto> parsedData = getParsedDataFundInfo(params.getScreeningId());
        Map<Long, HedgeFundScreeningParsedDataDto> parsedDataMapByFundId = new HashMap<>();
        if(parsedData != null && !parsedData.isEmpty()){
            for(HedgeFundScreeningParsedDataDto dataDto: parsedData){
                parsedDataMapByFundId.put(dataDto.getFundId(), dataDto);
            }
        }

        for(int lookback = lookbackStart; lookback <= params.getLookbackAUM().intValue(); lookback++){

            Date dateFromMax =  DateUtils.moveDateByMonths(params.getStartDate(), 0 - lookback);
            // AUM records for screening and date range
            // order by fund id, date
            List<HedgeFundScreeningParsedDataAUM>  fundsAUM = this.parsedDataAUMRepository.findByScreeningIdAndDateRange(params.getScreeningId(),
                    DateUtils.getFirstDayOfCurrentMonth(dateFromMax), DateUtils.getFirstDayOfCurrentMonth(params.getStartDate()),
                    new Sort(Sort.Direction.DESC, "fundId", "date"));


            Long currentFund = null;
            List<HedgeFundScreeningParsedDataAUM>  uniqueFundsAUM = new ArrayList<>();
            Map<String, Double> strategyAUMByInvestorName = new HashMap<>();
            for(HedgeFundScreeningParsedDataAUM fundAUM: fundsAUM){
                // sorted by fundId and date
                if(currentFund == null || currentFund.longValue() != fundAUM.getFundId().longValue()){
                    // takes first-in, i.e. most recent since it is sorted
                    currentFund = fundAUM.getFundId();
                    uniqueFundsAUM.add(fundAUM);

                    String investorName = parsedDataMapByFundId.get(fundAUM.getFundId()).getInvestmentManager();
                    if(strategyAUMByInvestorName.get(investorName) == null){
                        strategyAUMByInvestorName.put(investorName, fundAUM.getValue());
                    }else{
                        Double value = strategyAUMByInvestorName.get(investorName);
                        strategyAUMByInvestorName.put(investorName, MathUtils.add(value, fundAUM.getValue()));
                    }
                }
            }

            // Add ucits to strategy AUM Map
            Map<String, HedgeFundScreeningStrategyAUMDto> ucitsInvestorMap = getUcitsInvestorMapByLookback(params, lookback);


            Map<Long, HedgeFundScreeningParsedDataAUM> fundAUMMap = new HashMap<>();
            for(HedgeFundScreeningParsedDataAUM fundAUM: uniqueFundsAUM){
                // Check Currency
                Double fundAUMValue = fundAUM.getValue().doubleValue();
                if(fundAUM.getReturnsCurrency() != null && !fundAUM.getReturnsCurrency().equalsIgnoreCase(CurrencyLookup.USD.getCode())){
                    // non-USD
                    CurrencyRatesDto currencyRatesDto = this.currencyRatesService.getLstRateForMonthDateAndCurrencyBackwards(DateUtils.getLastDayOfCurrentMonth(fundAUM.getDate()), fundAUM.getReturnsCurrency());
                    if(currencyRatesDto != null && currencyRatesDto.getValueUSD() != null){
                        fundAUMValue = MathUtils.multiply(currencyRatesDto.getValueUSD(), fundAUM.getValue());
                    }else{
                        // Missing currency rate
                        if(type == 1){ // QUALIFIED
                            continue;
                        }else if(type == 2){ // UNDECIDED
//                            HedgeFundScreeningFundAUMDto fundAUMDto = new HedgeFundScreeningFundAUMDto();
//                            fundAUMDto.setFundId(fundAUM.getFundId());
//                            fundAUMDto.setFundAUM(fundAUM.getValue());
//                            fundAUMDto.setFundAUMCurrency(fundAUM.getReturnsCurrency());
                            fundAUMMap.put(fundAUM.getFundId(), fundAUM);
                        }
                    }
                }

                String investorName = parsedDataMapByFundId.get(fundAUM.getFundId()).getInvestmentManager();
                Double strategyAUM = strategyAUMByInvestorName.get(investorName);
                HedgeFundScreeningStrategyAUMDto ucitsEntry = ucitsInvestorMap.get(investorName);
                strategyAUM = ucitsEntry != null && ucitsEntry.getValue() != null ? MathUtils.add(strategyAUM, ucitsEntry.getValue()) : strategyAUM;
                boolean strategyAUMhasMissingCurrencyRates = ucitsEntry != null && ucitsEntry.isHasMissingCurrencyRates();

                fundAUMValue = Math.max(strategyAUM != null ? strategyAUM : 0, fundAUMValue);

                if(fundAUMValue.doubleValue() >= params.getFundAUM().doubleValue()){
                    if(type == 1){
                        // QUALIFIED
                        if(fundAUMValue.doubleValue() >= params.getManagerAUM().doubleValue()) {
//                            HedgeFundScreeningFundAUMDto fundAUMDto = new HedgeFundScreeningFundAUMDto();
//                            fundAUMDto.setFundId(fundAUM.getFundId());
//                            fundAUMDto.setFundAUM(fundAUM.getValue());
//                            fundAUMDto.setFundAUMCurrency(fundAUM.getReturnsCurrency());

//                            if(fundAUM.getReturnsCurrency() != null && !fundAUM.getReturnsCurrency().equalsIgnoreCase(CurrencyLookup.USD.getCode()))){
//                                // non-USD
//                                fundAUMDto
//                            }
//                            fundAUMDto.setStrategyAUM(strategyAUM);

                            fundAUMMap.put(fundAUM.getFundId(), fundAUM);
                        }else{
                            // check manager AUM
                            Double managerAUM = parsedDataMapByFundId.get(fundAUM.getFundId()) != null ?
                                    parsedDataMapByFundId.get(fundAUM.getFundId()).getManagerAUM() : null;
                            if(managerAUM != null && managerAUM.doubleValue() >= params.getManagerAUM().doubleValue()){
                                //resultFundAUMList.add(fundAUM);
                                fundAUMMap.put(fundAUM.getFundId(), fundAUM);
                            }
                        }
                    }else if(type == 2){
                        //UNDECIDED
                        if(fundAUMValue.doubleValue() < params.getManagerAUM().doubleValue()) {
                            // Check manager AUM
                            Double managerAUM = parsedDataMapByFundId.get(fundAUM.getFundId()).getManagerAUM();
                            if(managerAUM == null || strategyAUMhasMissingCurrencyRates) {
                                //resultFundAUMList.add(fundAUM);
                                fundAUMMap.put(fundAUM.getFundId(), fundAUM);
                            }

                        }else if(fundAUMValue.doubleValue() < params.getFundAUM().doubleValue() && strategyAUMhasMissingCurrencyRates){
                            fundAUMMap.put(fundAUM.getFundId(), fundAUM);
                        }
                    }
                }
            }

            lookbackAUMMap.put(lookback, new ArrayList<>(fundAUMMap.values()));
        }
        return lookbackAUMMap;
    }

    @Deprecated
    private Integer[][] getLookbackMatrix_(int lookBackReturn, int lookbackAUM,
                                           Map<Integer, List<HedgeFundScreeningFundCounts>> lookbackReturnMap,
                                           Map<Integer,List<HedgeFundScreeningParsedDataAUM>> lookbackAUMMap,
                                           HedgeFundScreeningFilteredResultDto params, int type){
        Integer[][] values = new Integer[lookBackReturn + 2][lookbackAUM + 2];
        for(int i = 0; i < lookBackReturn + 2; i++){
            for(int j = 0; j < lookbackAUM + 2; j++){
                if(i == 0 && j == 0){
                    values[i][j] = null;
                    continue;
                }else if(i == 0){
                    values[i][j] = j - 1;
                }else if(j == 0){
                    values[i][j] = i - 1;
                }else {
                    Long[] fundIds = getIntersectingFunds__(lookbackReturnMap.get(i - 1), lookbackAUMMap.get(j - 1));

//                    params.setLookbackReturns(i - 1);
//                    params.setLookbackAUM(j - 1);
//                    List<HedgeFundScreeningParsedDataDto> fundList =
//                            getQualifiedOrUndecidedFundListByTypeAndManagerAUM(params, lookbackAUMMap.get(j - 1), fundIds, type);
//
//                    values[i][j] =  fundList.size();

                    values[i][j] = fundIds.length;
                }
            }
        }
        return values;
    }

    @Deprecated
    private Map<Integer, List<HedgeFundScreeningParsedDataAUM>> getAUMLookbackMap(HedgeFundScreeningFilteredResultDto params, int type){
        Map<Integer, List<HedgeFundScreeningParsedDataAUM>> lookbackAUMMap = new HashMap<>();
        for(int lookback = 0; lookback <= params.getLookbackAUM().intValue(); lookback++){
            List<HedgeFundScreeningParsedDataAUM> resultFundAUMList = getAUMFundListByTypeAndLookback(params, lookback, type);
            lookbackAUMMap.put(lookback, resultFundAUMList);
        }
        return lookbackAUMMap;
    }

    @Deprecated
    private Long[] getIntersectingFunds_(List<HedgeFundScreeningFundCounts> returnsByFund,
                                         List<HedgeFundScreeningFundAUMDto> AUMByFund) {

        Set<Long> existingFunds = new HashSet<>();
        List<Long> fundIds = new ArrayList<>();
        if(returnsByFund != null && !returnsByFund.isEmpty()){
            for(HedgeFundScreeningFundCounts fundCounts: returnsByFund){
                if(fundCounts.getCount() != null && fundCounts.getCount().longValue() > 0){
                    existingFunds.add(fundCounts.getFundId());
                }
            }
        }
        if(AUMByFund != null && !AUMByFund.isEmpty()){
            for(HedgeFundScreeningFundAUMDto fundAUM: AUMByFund){
                if(fundAUM.getFundAUM() != null && fundAUM.getFundAUM().doubleValue() != 0.0){
                    if(fundAUM.getFundId() != null && existingFunds.contains(fundAUM.getFundId())){
                        fundIds.add(fundAUM.getFundId());
                    }
                }
            }
        }

        Long[] funds = new Long[fundIds.size()];
//        boolean print = false;
//        Collections.sort(fundIds);
//        if (print) {
//            for (int i = 0; i < fundIds.size(); i++) {
//                System.out.println(fundIds.get(i).longValue());
//            }
//        }
        return fundIds.toArray(funds);
    }

    @Deprecated
    private Long[] getIntersectingFunds__(List<HedgeFundScreeningFundCounts> returnsByFund,
                                          List<HedgeFundScreeningParsedDataAUM> AUMByFund) {

        Set<Long> existingFunds = new HashSet<>();
        List<Long> fundIds = new ArrayList<>();
        if(returnsByFund != null && !returnsByFund.isEmpty()){
            for(HedgeFundScreeningFundCounts fundCounts: returnsByFund){
                if(fundCounts.getCount() != null && fundCounts.getCount().longValue() > 0){
                    existingFunds.add(fundCounts.getFundId());
                }
            }
        }
        if(AUMByFund != null && !AUMByFund.isEmpty()){
            for(HedgeFundScreeningParsedDataAUM fundAUM: AUMByFund){
                if(fundAUM.getValue() != null && fundAUM.getValue().doubleValue() != 0.0){
                    if(fundAUM.getFundId() != null && existingFunds.contains(fundAUM.getFundId())){
                        fundIds.add(fundAUM.getFundId());
                    }
                }
            }
        }

        Long[] funds = new Long[fundIds.size()];
//        boolean print = false;
//        Collections.sort(fundIds);
//        if (print) {
//            for (int i = 0; i < fundIds.size(); i++) {
//                System.out.println(fundIds.get(i).longValue());
//            }
//        }
        return fundIds.toArray(funds);
    }

    @Deprecated
    private Map<String, Double> getManagerTotalStrategyAUMMap(Map<Long, HedgeFundScreeningParsedDataDto> parsedDataMapByFundId,
                                                              List<HedgeFundScreeningParsedDataAUM>  fundsAUM, Long screeningId, Date date){
        // Set up manager map
        Map<String, Double> managersMapByName = new HashMap<>();
        if(fundsAUM != null && !fundsAUM.isEmpty()) {
            for(HedgeFundScreeningParsedDataAUM fundAUM:  fundsAUM){
                Double currencyRateValue = null;
                if(fundAUM.getReturnsCurrency() != null && !fundAUM.getReturnsCurrency().equalsIgnoreCase(CurrencyLookup.USD.getCode())){
                    // non-USD
                    CurrencyRatesDto currencyRatesDto = this.currencyRatesService.getLstRateForMonthDateAndCurrencyBackwards(DateUtils.getLastDayOfCurrentMonth(fundAUM.getDate()), fundAUM.getReturnsCurrency());
                    if(currencyRatesDto != null && currencyRatesDto.getValueUSD() != null){
                        currencyRateValue = currencyRatesDto.getValueUSD();
                    }else{
                        // skip this fund
                        continue;
                    }
                }
                HedgeFundScreeningParsedDataDto parsedDataDto = parsedDataMapByFundId.get(fundAUM.getFundId());
                if (parsedDataDto != null) {
                    String managerName = parsedDataDto.getInvestmentManager();
                    // Currency
                    Double valueUSD = currencyRateValue != null && fundAUM.getReturnsCurrency() != null && !fundAUM.getReturnsCurrency().equalsIgnoreCase(CurrencyLookup.USD.getCode())
                            ? MathUtils.multiply(fundAUM.getValue(), currencyRateValue) : fundAUM.getValue();
                    if (managersMapByName.get(managerName) != null) {
                        managersMapByName.put(managerName, MathUtils.add(managersMapByName.get(managerName), valueUSD));
                    } else {
                        managersMapByName.put(managerName, valueUSD);
                    }
                }
            }
        }

        // Ucits AUM
        fillUcitsStrategyManagerAUMMap(managersMapByName, screeningId, date, date);

        return managersMapByName;
    }

    @Deprecated
    private List<HedgeFundScreeningParsedDataAUM> getAUMFundListByTypeAndLookback(HedgeFundScreeningFilteredResultDto params, int lookback, int type){
        Date date =  DateUtils.moveDateByMonths(params.getStartDate(), 0 - lookback);
        List<HedgeFundScreeningParsedDataAUM>  fundsAUM = this.parsedDataAUMRepository.findByScreeningIdAndDateRange(params.getScreeningId(),
                DateUtils.getFirstDayOfCurrentMonth(date), DateUtils.getFirstDayOfCurrentMonth(date), new Sort(Sort.Direction.DESC, "fundId", "date"));

        // Parsed Data Map (fund id and manager)
        List<HedgeFundScreeningParsedDataDto> parsedData = getParsedDataFundInfo(params.getScreeningId());
        Map<Long, HedgeFundScreeningParsedDataDto> parsedDataMapByFundId = new HashMap<>();
        if(parsedData != null && !parsedData.isEmpty()){
            for(HedgeFundScreeningParsedDataDto dataDto: parsedData){
                parsedDataMapByFundId.put(dataDto.getFundId(), dataDto);
            }
        }

        // Set up manager map
        Map<String, Double> managersMapByName = getManagerTotalStrategyAUMMap(parsedDataMapByFundId, fundsAUM, params.getScreeningId(), date);


        List<HedgeFundScreeningParsedDataAUM>  resultFundAUMList = new ArrayList<>();
        if(fundsAUM != null && !fundsAUM.isEmpty()) {
            for(HedgeFundScreeningParsedDataAUM fundAUM:  fundsAUM){
                // Check Currency
                Double fundAUMValue = fundAUM.getValue().doubleValue();
                if(fundAUM.getReturnsCurrency() != null && !fundAUM.getReturnsCurrency().equalsIgnoreCase(CurrencyLookup.USD.getCode())){
                    // non-USD
                    CurrencyRatesDto currencyRatesDto = this.currencyRatesService.getLstRateForMonthDateAndCurrencyBackwards(DateUtils.getLastDayOfCurrentMonth(fundAUM.getDate()), fundAUM.getReturnsCurrency());
                    if(currencyRatesDto != null && currencyRatesDto.getValueUSD() != null){
                        fundAUMValue = MathUtils.multiply(currencyRatesDto.getValueUSD(), fundAUM.getValue());
                    }else{
                        // Missing currency rate
                        if(type == 2){// UNDECIDED
                            resultFundAUMList.add(fundAUM);
                        }
                        continue;
                    }
                }

                //Double strategyAUM =  parsedDataMapByFundId.get(fundAUM.getFundId()) != null ?
                //        managersMapByName.get(parsedDataMapByFundId.get(fundAUM.getFundId()).getInvestmentManager()) : null;
                //fundAUMValue = Math.max(strategyAUM != null ? strategyAUM : 0, fundAUMValue);

                if(fundAUMValue.doubleValue() >= params.getFundAUM().doubleValue()){
                    if(type == 1){
                        // QUALIFIED
                        if(fundAUMValue.doubleValue() >= params.getManagerAUM().doubleValue()) {
                            resultFundAUMList.add(fundAUM);
                        }else{
                            // check manager AUM
                            Double managerAUM = parsedDataMapByFundId.get(fundAUM.getFundId()) != null ?
                                    parsedDataMapByFundId.get(fundAUM.getFundId()).getManagerAUM() : null;
                            if(managerAUM != null && managerAUM.doubleValue() >= params.getManagerAUM().doubleValue()){
                                resultFundAUMList.add(fundAUM);
                            }
                        }
                    }else if(type == 2){
                        //UNDECIDED
                        if(fundAUMValue.doubleValue() < params.getManagerAUM().doubleValue()) {
                            // Check manager AUM
                            Double managerAUM = parsedDataMapByFundId.get(fundAUM.getFundId()).getManagerAUM();
                            if(managerAUM == null) {
                                resultFundAUMList.add(fundAUM);
                            }

                        }
                    }
                }
            }
        }

        return resultFundAUMList;
    }

    @Deprecated
    private Map<Integer, List<HedgeFundScreeningFundCounts>> getQualifiedReturnLookbackMapOld(HedgeFundScreeningFilteredResultDto params){
        Map<Integer, List<HedgeFundScreeningFundCounts>> lookbackReturnMap = new HashMap<>();
        for(int lookback = 0; lookback <= params.getLookbackReturns().intValue(); lookback++){
            List<HedgeFundScreeningFundCounts>  validFundCounts = getQualifiedFundReturnCounts(params, lookback);
            lookbackReturnMap.put(lookback, validFundCounts);
        }
        return lookbackReturnMap;
    }

    @Deprecated
    private List<HedgeFundScreeningFundCounts> getQualifiedFundReturnCounts(HedgeFundScreeningFilteredResultDto params, int lookback){
        Date dateTo =  DateUtils.moveDateByMonths(params.getStartDate(), 0 - lookback);
        Date dateFrom = DateUtils.moveDateByMonths(dateTo, 0 - (Math.max(0, params.getTrackRecord().intValue() - 1)));
        Assert.isTrue(DateUtils.getMonthsChanged(dateFrom, dateTo) == params.getTrackRecord().intValue());


        List<HedgeFundScreeningFundCounts>  validFundCounts = new ArrayList<>();

        // TODO:
//        List<HedgeFundScreeningFundCounts>  fundCounts = this.parsedDataReturnRepository.getFundIdCounts(params.getScreeningId(), dateFrom, dateTo);
//
//        if(fundCounts != null && !fundCounts.isEmpty()) {
//            for(HedgeFundScreeningFundCounts fundCount: fundCounts){
//                // Check track record
//                if(fundCount.getCount() != null && fundCount.getCount().intValue() >= params.getTrackRecord().intValue()){
//                    validFundCounts.add(fundCount);
//                }
//            }
//        }
        return validFundCounts;
    }

    @Deprecated
    private Integer[][] getFilteredResultStatisticsUnqualified(HedgeFundScreeningFilteredResultDto params){

        Integer[][] qualifiedValues = getFilteredResultStatisticsQualified(params);
        Integer[][] undecidedValues = getFilteredResultStatisticsUndecided(params);
        Long totalCount = this.parsedDataRepository.countByScreeningId(params.getScreeningId());
        for(int i = 1; i < qualifiedValues.length; i++){
            for(int j = 1; j < qualifiedValues.length; j++){
                qualifiedValues[i][j] = (int) (totalCount - (long) qualifiedValues[i][j] - (long) undecidedValues[i][j]);
            }
        }

        return qualifiedValues;
    }

    /******************************************************************************************************************/

}
