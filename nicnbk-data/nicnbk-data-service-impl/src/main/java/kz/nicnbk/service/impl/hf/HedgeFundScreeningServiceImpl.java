package kz.nicnbk.service.impl.hf;

import kz.nicnbk.common.service.exception.ExcelFileParseException;
import kz.nicnbk.common.service.util.*;
import kz.nicnbk.repo.api.hf.*;
import kz.nicnbk.repo.model.files.Files;
import kz.nicnbk.repo.model.hf.*;
import kz.nicnbk.repo.model.lookup.CurrencyLookup;
import kz.nicnbk.repo.model.lookup.FileTypeLookup;
import kz.nicnbk.service.api.common.CurrencyRatesService;
import kz.nicnbk.service.api.files.FileService;
import kz.nicnbk.service.api.hf.HedgeFundScoringService;
import kz.nicnbk.service.api.hf.HedgeFundScreeningService;
import kz.nicnbk.service.converter.hf.*;
import kz.nicnbk.service.dto.common.FileUploadResultDto;
import kz.nicnbk.service.dto.common.ListResponseDto;
import kz.nicnbk.service.dto.common.ResponseStatusType;
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
    private HedgeFundScreeningParsedUcitsDataEntityConverter parsedUcitsDataEntityConverter;

    @Autowired
    private HedgeFundScreeningParsedDataRepository parsedDataRepository;

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


    /*** CRUD operations **********************************************************************************************/
    @Override
    public Long save(HedgeFundScreeningDto screeningDto, String username) {
        try {
            if(screeningDto.getId() == null){
                screeningDto.setCreator(username);
            }else{
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
    public HedgeFundScreeningDto get(Long id) {
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

                return dto;
            }
        }
        return null;
    }

    @Override
    public List<HedgeFundScreeningDto> getAll() {
        List<HedgeFundScreeningDto> screenings = new ArrayList<>();
        Iterator<HedgeFundScreening> entitiesIterator = this.screeningRepository.findAll().iterator();
        if(entitiesIterator != null){
            while(entitiesIterator.hasNext()){
                HedgeFundScreening entity = entitiesIterator.next();
                HedgeFundScreeningDto dto = this.screeningEntityConverter.disassemble(entity);
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
    public HedgeFundScreeningPagedSearchResult search(HedgeFundScreeningSearchParams searchParams) {
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
    public List<HedgeFundScreeningFilteredResultDto> getFilteredResultsByScreeningId(Long screeningId) {
        List<HedgeFundScreeningFilteredResult> entities = filteredResultRepository.findByScreeningId(screeningId);
        if(entities != null){
            List<HedgeFundScreeningFilteredResultDto> results = this.filteredResultEntityConverter.disassembleList(entities);
            return results;
        }
        return new ArrayList<HedgeFundScreeningFilteredResultDto>();

    }

    @Override
    public Long saveFilteredResult(HedgeFundScreeningFilteredResultDto dto, String username) {
        try {
            if(dto.getId() == null){
                dto.setCreator(username);
            }else{
                dto.setUpdater(username);
                dto.setCreator(null);
            }
            HedgeFundScreeningFilteredResult entity = this.filteredResultEntityConverter.assemble(dto);

            if (entity != null) {
                Long id = this.filteredResultRepository.save(entity).getId();
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

            //excluded funds
            List<HedgeFundScreeningParsedDataDto> excludedFunds = getExcludedFunds(filteredResultDto.getId());
            filteredResultDto.setExcludedFunds(excludedFunds);

            return filteredResultDto;
        }

        return null;
    }

    @Override
    public HedgeFundScreeningFilteredResultDto getFilteredResultWithoutFundsInfo(Long id) {
        HedgeFundScreeningFilteredResult entity = this.filteredResultRepository.findOne(id);
        if(entity != null){
            HedgeFundScreeningFilteredResultDto dto =  this.filteredResultEntityConverter.disassemble(entity);

            return dto;
        }

        return null;
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

    private FileUploadResultDto deleteFileAndAssociation(Long fileId, Long screeningId, String fileType){
        FileUploadResultDto resultDtoSheet = new FileUploadResultDto();
        try{
            HedgeFundScreening entity = this.screeningRepository.findOne(screeningId);
            if(fileType.equalsIgnoreCase(FileTypeLookup.HF_SCREENING_DATA_FILE.getCode())) {
                entity.setDataFile(null);
            }else if(fileType.equalsIgnoreCase(FileTypeLookup.HF_SCREENING_UCITS_FILE.getCode())) {
                entity.setUcitsFile(null);
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
                this.parsedDataRepository.deleteByScreeningId(screeningId);
                return true;
            }else if(type.equalsIgnoreCase(FileTypeLookup.HF_SCREENING_UCITS_FILE.getCode())) {
                this.parsedUcitsDataRepository.deleteByScreeningId(screeningId);
                return true;
            }
        }catch (Exception ex){
            logger.error("Error deleting parsed data (" + type + ") for HF Screening with id=" + screeningId + "(with exception)", ex);
        }
        return false;
    }

    private boolean deleteParsedDataSheet2(Long screeningId){
        try {
            parsedDataReturnRepository.deleteByScreeningId(screeningId);
            return true;
        }catch (Exception ex){
            logger.error("Error deleting parsed data for HF Screening (RETURNS) with id=" + screeningId + "(with exception)", ex);
        }
        return false;
    }

    private boolean deleteParsedDataSheet3(Long screeningId, String type){
        try {
            if(type.equalsIgnoreCase(FileTypeLookup.HF_SCREENING_DATA_FILE.getCode())) {
                this.parsedDataAUMRepository.deleteByScreeningId(screeningId);
                return true;
            }else if(type.equalsIgnoreCase(FileTypeLookup.HF_SCREENING_UCITS_FILE.getCode())) {
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

            // Added funds (not in input file)
            Map<Integer, List<HedgeFundScreeningParsedDataDto>> addedFundsQualifiedMap = getAddedFundsQualifiedByReturnLookbackMap(params);
            if(addedFundsQualifiedMap != null){
                for(int i = 0; i <= params.getLookbackReturns().intValue(); i++){
                    if(addedFundsQualifiedMap.get(i) != null){
                        for(int j = 1; j < qualified[i+1].length; j++){
                            qualified[i+1][j] = qualified[i+1][j] + addedFundsQualifiedMap.get(i).size();
                        }
                    }
                }
            }

//            Integer[][] qualified_ = getFilteredResultStatisticsQualified_(params);
//
//            System.out.println("QUALIFIED COMPARISON CHECK");
//            int errors = 0;
//            if(qualified.length != qualified_.length){
//                System.out.println("MISMATCH length: " + qualified.length + " != " + qualified_.length);
//                errors++;
//            }
//            for(int i = 0; i < qualified.length; i++){
//                if(qualified[i].length != qualified_[i].length){
//                    System.out.println("MISMATCH length i=" + i + " : " + qualified[i].length + " != " + qualified_[i].length);
//                    errors++;
//                }
//                for(int j = 0; j < qualified.length; j++){
//                    if(qualified[i][j] != qualified_[i][j]){
//                        System.out.println("MISMATCH: " + qualified[i][j] + " != " + qualified_[i][j]);
//                        errors++;
//                    }
//                }
//            }
//            if(errors > 0) {
//                System.out.println("QUALIFIED ERROR comparing: " + errors);
//            }else{
//                System.out.println("QUALIFIED OK");
//            }

            //System.out.println("UNDECIDED");
            Integer[][] undecided = getFilteredResultStatisticsUndecided(params);
            statisticsDto.setUndecided(undecided);


//            Integer[][] undecided_ = getFilteredResultStatisticsUndecided_(params);
//            System.out.println("UNDECIDED COMPARISON CHECK");
//            errors = 0;
//            if(undecided.length != undecided_.length){
//                System.out.println("MISMATCH length: " + undecided.length + " != " + undecided_.length);
//                errors++;
//            }
//            for(int i = 0; i < undecided.length; i++){
//                if(undecided[i].length != undecided_[i].length){
//                    System.out.println("MISMATCH length i=" + i + " : " + undecided[i].length + " != " + undecided_[i].length);
//                    errors++;
//                }
//                for(int j = 0; j < undecided.length; j++){
//                    if(undecided[i][j] != undecided_[i][j]){
//                        System.out.println("MISMATCH: " + undecided[i][j] + " != " + undecided_[i][j]);
//                        errors++;
//                    }
//                }
//            }
//            if(errors > 0) {
//                System.out.println("UNDECIDED ERROR comparing: " + errors);
//            }else{
//                System.out.println("UNDECIDED OK");
//            }

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
                        if(addedFundsQualifiedMap != null && addedFundsQualifiedMap.get(i - 1) != null) {
                            count = totalCount + addedFundsQualifiedMap.get(i - 1).size();
                        }

                        unqualified[i][j] = (int) (count - (long) qualified[i][j] - (long) undecided[i][j]);
                    }
                }
            }
            statisticsDto.setUnqualified(unqualified);

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

    private Map<Integer, List<HedgeFundScreeningParsedDataDto>> getAddedFundsQualifiedByReturnLookbackMap(HedgeFundScreeningFilteredResultDto params){
        Map<Integer, List<HedgeFundScreeningParsedDataDto>> fundsMap = null;
        if(params.getId() != null) {
            List<HedgeFundScreeningParsedDataDto> addedFunds = getAddedFundsByFilteredResultId(params.getId());
            if(addedFunds != null && !addedFunds.isEmpty()){
                fundsMap = new HashMap();
                for(HedgeFundScreeningParsedDataDto addedFund: addedFunds){
                    if(addedFund.getFundAUM() != null && addedFund.getFundAUM().doubleValue() >= params.getFundAUM().doubleValue()){
                        // valid AUM
                        for(int i = 0; i <= params.getLookbackReturns().intValue(); i++){
                            Date dateFrom = DateUtils.moveDateByMonths(params.getStartDateFromTextOrCurrent(), 0 - (Math.max(0, params.getTrackRecord().intValue() + params.getLookbackReturns().intValue() - 1)));
                            List<HedgeFundScreeningFundReturnDto> returns = addedFund.getReturns();
                            Collections.sort(returns);

                            Date previousDate = null;
                            int count = 0;
                            for(HedgeFundScreeningFundReturnDto returnDto: returns){
                                Date date = DateUtils.getMM_YYYYYFormatLastDayMonthDate(returnDto.getDate());
                                Date startDate = DateUtils.getMM_YYYYYFormatLastDayMonthDate(params.getStartDateMonth());
                                if(date.compareTo(dateFrom) < 0 || date.compareTo(startDate) > 0){
                                    // skip this return
                                    continue;
                                }
                                if(previousDate == null){
                                    previousDate = date;
                                    count = 1;
                                }else{
                                    Date nextMonth = DateUtils.moveDateByMonths(previousDate, 1);
                                    nextMonth = DateUtils.getLastDayOfCurrentMonth(nextMonth);
                                    //if(DateUtils.isSameMonth(nextMonth, date)){
                                    if(DateUtils.isSameDate(nextMonth, date)){
                                        count++;
                                        if(count == params.getTrackRecord().intValue()){
                                            break;
                                        }
                                    }else{
                                        count = 0;
                                    }
                                    previousDate = date;
                                }
                            }
                            if(count >= params.getTrackRecord().intValue()){
                                // valid fund
                                List<HedgeFundScreeningParsedDataDto> existing = fundsMap.get(i);
                                if(existing == null){
                                    existing = new ArrayList<>();
                                }
                                existing.add(addedFund);
                                fundsMap.put(i, existing);

                            }
                        }
                    }
                }
            }
        }

        return fundsMap;
    }

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

        // Excluded funds
        List<HedgeFundScreeningParsedDataDto>  excludedFunds = getExcludedFunds(params.getId());
        if(excludedFunds != null && !excludedFunds.isEmpty()){
            List<HedgeFundScreeningFundAUMDto> resultFundsWithoutExcluded = new ArrayList<>();
            for(HedgeFundScreeningFundAUMDto fund: resultFunds){
                boolean found = false;
                for(HedgeFundScreeningParsedDataDto excludedFund: excludedFunds){
                    if(fund.getFundId().longValue() == excludedFund.getFundId().longValue()){
                        // skip this fund
                        found = true;
                        break;
                    }
                }
                if(!found){
                    resultFundsWithoutExcluded.add(fund);
                }
            }

            resultFunds = resultFundsWithoutExcluded;
        }

        if(resultFunds != null){
            List<HedgeFundScreeningParsedDataDto> fundList = getResultFundListWithAdditionalInfo(params.getScreeningId(), resultFunds);

            Map<Integer, List<HedgeFundScreeningParsedDataDto>> addedFundsQualifiedMap = getAddedFundsQualifiedByReturnLookbackMap(params);
            if(addedFundsQualifiedMap != null){
                List<HedgeFundScreeningParsedDataDto> addedFunds = addedFundsQualifiedMap.get(params.getLookbackReturns().intValue());
                if(addedFunds != null) {
                    fundList.addAll(addedFunds);
                }
            }

            // SCORING
            HedgeFundScoringFundParamsDto scoringParams = new HedgeFundScoringFundParamsDto();
            scoringParams.setFilteredResultId(params.getId());
            scoringParams.setLookbackReturn(params.getLookbackReturns().intValue());
            scoringParams.setLookbackAUM(params.getLookbackAUM().intValue());

            if(withScoring) {
                responseDto = this.scoringService.getCalculatedScoring(fundList, scoringParams);
                if (responseDto.getStatus() != null && responseDto.getStatus().getCode().equalsIgnoreCase(ResponseStatusType.SUCCESS.getCode())) {
                    // SUCCESS
                } else {
                    // FAILED
                    return responseDto;
                }
            }else{
                responseDto.setRecords(fundList);
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
        List<HedgeFundScreeningParsedDataDto>  excludedFunds = getExcludedFunds(params.getId());
        if(excludedFunds != null && !excludedFunds.isEmpty()){
            List<HedgeFundScreeningFundAUMDto> resultFundsWithoutExcluded = new ArrayList<>();
            for(HedgeFundScreeningFundAUMDto fund: resultFunds){
                boolean found = false;
                for(HedgeFundScreeningParsedDataDto excludedFund: excludedFunds){
                    if(fund.getFundId().longValue() == excludedFund.getFundId().longValue()){
                        // skip this fund
                        found = true;
                        break;
                    }
                }
                if(!found){
                    resultFundsWithoutExcluded.add(fund);
                }
            }

            resultFunds = resultFundsWithoutExcluded;
        }
        if(resultFunds != null){
            List<HedgeFundScreeningParsedDataDto> fundList = getResultFundListWithAdditionalInfo(params.getScreeningId(), resultFunds);
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

        fillUnqualifiedFundsRecentValidValues(unqualifiedList, params);

        Collections.sort(unqualifiedList);
//        for(HedgeFundScreeningParsedDataDto dto: unqualifiedList){
//            System.out.println(dto.getFundId().longValue());
//        }
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
                return true;

            }catch (Exception ex){
                logger.error("HF Screening - failed to update Manager AUM (with exception) [user]=" + username, ex);
            }

        }
        return false;
    }

    @Override
    public boolean updateFundInfo(HedgeFundScreeningParsedDataDto fund, String username) {
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
                if (fund.getFundAUM() == null || fund.getFundAUM().doubleValue() == 0){
                    // Fund AUM required
                    logger.error("HF Screening  - failed to update fund info [screening id =" + fund.getScreening().getId().longValue() +
                            ", fund id = " + fund.getFundId().longValue() + "]: fund AUM required");
                    return false;
                }

                // TODO: Check AUM


                // TODO: Check track record


                HedgeFundScreeningAddedFund entity = this.addedFundRepository.findByFundNameAndFilteredResultId(fund.getFundName(), fund.getFilteredResultId());
                if(entity == null){
                    entity = new HedgeFundScreeningAddedFund();
                    HedgeFundScreeningFilteredResult filteredResult = new HedgeFundScreeningFilteredResult();
                    filteredResult.setId(fund.getFilteredResultId());
                    entity.setFilteredResult(filteredResult);
                }

                List<HedgeFundScreeningParsedData> parsedFunds = this.parsedDataRepository.findByFundNameAndScreeningId(fund.getFundName(), fund.getScreening().getId());
                if(parsedFunds != null && !parsedFunds.isEmpty()){
                    logger.error("Failed to create new fund (added): fund already exists");
                    return false;
                }

                //entity.setFundId(fund.getFundId());
                entity.setFundName(fund.getFundName());
                entity.setInvestmentManager(fund.getInvestmentManager());
                entity.setMainStrategy(fund.getMainStrategy());
                entity.setFundAUM(fund.getFundAUM());
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
                return true;
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
                                "fund id=" + (fund.getFundId() != null ? fund.getFundId().longValue() : null)
                                + " [username=" + username + "]");
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
                return true;
//                HedgeFundScreeningParsedData entity = this.parsedDataRepository.findByFundIdAndScreeningId(fund.getFundId(), fund.getScreening().getId());
//
//                if(entity == null){
//                    logger.error("Failed to load fund info: screening=" + fund.getScreening().getId().longValue() + ", fund id=" +fund.getFundId().longValue() );
//                    return false;
//                }
//                entity.setEditedFundAUM(fund.getEditedFundAUM());
//                if(entity.getEditedFundAUM() != null) {
//                    if (StringUtils.isNotEmpty(fund.getEditedFundAUMDateMonthYear())) {
//                        Date date = DateUtils.getMM_YYYYYFormatLastDayMonthDate(fund.getEditedFundAUMDateMonthYear());
//                        entity.setEditedFundAUMDate(date);
//                    }
//                    entity.setEditedFundAUMComment(fund.getEditedFundAUMComment());
//                }else{
//                    entity.setEditedFundAUMDate(null);
//                    entity.setEditedFundAUMComment(null);
//                }
//                entity.setManagerAUM(fund.getManagerAUM());
//
//                this.parsedDataRepository.save(entity);
//                logger.info("HF Screening - Successfully updated fund info: " + "screening id=" +fund.getScreening().getId().longValue() +
//                        ", fund id=" + fund.getFundId().longValue() + " [user]=" + username);
//                return true
            }

        }catch (Exception ex){
            logger.error("HF Screening - failed to update fund info (with exception) [user]=" + username, ex);
        }
        return false;
    }

    @Override
    public boolean deleteAddedFund(String fundName, Long filteredResultI, String username) {
        try {
            HedgeFundScreeningAddedFund addedFund = this.addedFundRepository.findByFundNameAndFilteredResultId(fundName, filteredResultI);
            if(addedFund != null) {
                // clear returns
                this.addedFundReturnRepository.deleteByFundId(addedFund.getId());

                // delete fund
                this.addedFundRepository.deleteByFundNameAndFilteredResultId(fundName, filteredResultI);
                return true;
            }
        }catch (Exception ex){
            this.logger.error("Failed to delete added fund: fundName=" + fundName + ", filteredResultId=" + filteredResultI + "[user=" + username + "]", ex);
        }
        return false;
    }

    @Override
    public boolean excludeParsedFund(Long filteredResultId, Long fundId,  String excludeComment, String username){
//        try {
//            HedgeFundScreeningParsedData entity = this.parsedDataRepository.findByFundIdAndScreeningId(fundId, screeningId);
//            if (entity != null) {
//                entity.setExcluded(true);
//                this.parsedDataRepository.save(entity);
//                return true;
//            }
//        }catch (Exception ex){
//            logger.error("Failed to exclude fund: screening id " + (screeningId != null ? screeningId.longValue() : null) +
//                    ", fund id " + (fundId != null ? fundId.longValue() : null) + " [username=" + username + "]");
//        }
//        return false;
        try {
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
            }else{
                entity = new HedgeFundScreeningEditedFund();
                HedgeFundScreeningFilteredResult filteredResult = this.filteredResultRepository.findOne(filteredResultId);
                if(filteredResult != null) {
                    entity.setFilteredResult(filteredResult);

                    HedgeFundScreeningParsedData parsedDataEntity = this.parsedDataRepository.findByFundIdAndScreeningId(fundId, filteredResult.getScreening().getId());
                    entity.setParsedData(parsedDataEntity);

                    entity.setExcluded(true);
                    entity.setExcludeComment(excludeComment);
                }else{
                    logger.error("Failed to exclude fund: filter not found with id=" + filteredResultId + " [username=" + username + "]");
                    return false;
                }
            }
            editedFundRepository.save(entity);
            return true;
        }catch (Exception ex){
            logger.error("Failed to exclude fund: filter id " + (filteredResultId != null ? filteredResultId.longValue() : null) +
                    ", fund id " + (fundId != null ? fundId.longValue() : null) + " [username=" + username + "]");
        }
        return false;
    }

    @Override
    public boolean includeParsedFund(Long filteredResultId, Long fundId, String username){
//        try {
//            HedgeFundScreeningParsedData entity = this.parsedDataRepository.findByFundIdAndScreeningId(fundId, screeningId);
//            if (entity != null) {
//                entity.setExcluded(null);
//                this.parsedDataRepository.save(entity);
//                return true;
//            }
//        }catch (Exception ex){
//            logger.error("Failed to include fund: screening id " + (screeningId != null ? screeningId.longValue() : null) +
//                    ", fund id " + (fundId != null ? fundId.longValue() : null) + " [username=" + username + "]");
//        }
//        return false;

        try {
            HedgeFundScreeningEditedFund entity = this.editedFundRepository.findByFilteredResultIdAndFundId(filteredResultId, fundId);
            if (entity != null) {
                entity.setExcluded(false);
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
                    editedFundRepository.save(entity);
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

        List<HedgeFundScreeningEditedFund> editedFunds =
                this.editedFundRepository.findByFilteredResultIdAndExcluded(filteredResultId, true);
        if(editedFunds != null){
            List<HedgeFundScreeningParsedDataDto> funds = new ArrayList<>();
            for(HedgeFundScreeningEditedFund fund: editedFunds){
                HedgeFundScreeningParsedDataDto fundDto = parsedDataEntityConverter.disassemble(fund.getParsedData());
                fundDto.setExcluded(true);
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
            Date dateFrom = DateUtils.moveDateByMonths(params.getStartDateFromTextOrCurrent(), 0 - (Math.max(0, params.getTrackRecord().intValue() + lookback - 1)));

            Map<Long, List<Date>> fundReturnCountsMap = new HashMap<>();
            if(fundReturns != null && !fundReturns.isEmpty()) {
                for(HedgeFundScreeningParsedDataReturn fundReturn: fundReturns){
                    if(fundReturn.getDate().compareTo(dateFrom) < 0){
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
            Set<Long> fundIds = fundReturnCountsMap.keySet();
            Iterator<Long> fundIterator = fundIds.iterator();
            while(fundIterator.hasNext()){
                Long fundId = fundIterator.next();
                List<Date> dates = fundReturnCountsMap.get(fundId);
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
                        //if(DateUtils.isSameMonth(nextMonth, date)){
                        if(DateUtils.isSameDate(nextMonth, date)){
                            count++;
                            if(count == params.getTrackRecord().intValue()){
                                break;
                            }
                        }else{
                            count = 0;
                        }
                        previousDate = date;
                    }
                }
                if(count >= params.getTrackRecord().intValue()){
                    HedgeFundScreeningFundCounts fundCounts = new HedgeFundScreeningFundCounts(fundId, count);
                    validFundCounts.add(fundCounts);
                }
            }


            lookbackReturnMap.put(lookback, validFundCounts);
        }
        return lookbackReturnMap;
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
                    // non-ISD
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

        // Parsed Data Map (fund id and manager)
        List<HedgeFundScreeningParsedDataDto> parsedData = getParsedDataFundInfo(params.getScreeningId());
        Map<Long, HedgeFundScreeningParsedDataDto> parsedDataMapByFundId = new HashMap<>();
        List<HedgeFundScreeningParsedDataDto> editedFunds = getEditedIncludedFunds(params.getId());
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
            Map<String, HedgeFundScreeningStrategyAUMDto> strategyAUMByInvestorName = new HashMap<>();
            Set<String> missingCurrencyInvestors = new HashSet<>();
            for(HedgeFundScreeningParsedDataAUM fundAUM: fundsAUM){
                // sorted by fundId and date
                if(currentFund == null || currentFund.longValue() != fundAUM.getFundId().longValue()){
                    // takes first-in, i.e. most recent since it is sorted
                    currentFund = fundAUM.getFundId();
                    HedgeFundScreeningFundAUMDto fundAUMDto = new HedgeFundScreeningFundAUMDto(fundAUM.getFundId(), fundAUM.getValue(),
                            null, null, fundAUM.getReturnsCurrency(), null, fundAUM.getDate(),
                            parsedDataMapByFundId.get(fundAUM.getFundId()).getEditedFundAUM(),
                            parsedDataMapByFundId.get(fundAUM.getFundId()).getEditedFundAUMDate(),
                            parsedDataMapByFundId.get(fundAUM.getFundId()).getEditedFundAUMComment(), false);
                    uniqueFundsAUM.put(fundAUM.getFundId(), fundAUMDto);

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

                    if(strategyAUMByInvestorName.get(investorName) == null){
                        HedgeFundScreeningStrategyAUMDto value =
                                new HedgeFundScreeningStrategyAUMDto(investorName, fundAUMValueUSD, fundAUM.getReturnsCurrency(), false);
                        strategyAUMByInvestorName.put(investorName, value);
                    }else{
                        Double value = strategyAUMByInvestorName.get(investorName).getValue();
                        strategyAUMByInvestorName.get(investorName).setValue(MathUtils.add(value, fundAUMValueUSD));
                    }
                }
            }

            // Add ucits to strategy AUM Map
            Map<String, HedgeFundScreeningStrategyAUMDto> ucitsInvestorMap = getUcitsInvestorMapByLookback(params, lookback);
            ucitsInvestorMap.forEach((ucitsKey, ucitsValue)->{
                if(strategyAUMByInvestorName.get(ucitsKey) != null){
                    if(ucitsValue != null) {
                        strategyAUMByInvestorName.get(ucitsKey).addValue(ucitsValue.getValue());
                    }
                }else{
                    strategyAUMByInvestorName.put(ucitsKey, ucitsValue);
                }
            });

            // Add funds with updated AUM
            //List<HedgeFundScreeningParsedDataDto> editedFunds = getEditedIncludedFunds(params.getId());
            if(editedFunds != null && !editedFunds.isEmpty()){
                for(HedgeFundScreeningParsedDataDto editedFund: editedFunds){
                    if(editedFund.getEditedFundAUM() != null && editedFund.getEditedFundAUM().doubleValue() > 0){
                        if(uniqueFundsAUM.get(editedFund.getFundId()) == null){
                            HedgeFundScreeningFundAUMDto fundAUMDto = new HedgeFundScreeningFundAUMDto();
                            fundAUMDto.setFundId(editedFund.getFundId());

                            fundAUMDto.setFundAUM(null);
                            fundAUMDto.setFundAUMValueUSD(null);

                            //fundAUMDto.setFundAUMCurrency(editedFund.getParsedData().getCurrency());
                            fundAUMDto.setFundAUMDate(editedFund.getEditedFundAUMDate());

                            fundAUMDto.setEditedFundAUM(editedFund.getEditedFundAUM());
                            fundAUMDto.setEditedFundAUMDate(editedFund.getEditedFundAUMDate());
                            fundAUMDto.setEditedFundAUMComment(editedFund.getEditedFundAUMComment());

                            uniqueFundsAUM.put(editedFund.getFundId(), fundAUMDto);

                        }
                    }
                }
            }
//            for(HedgeFundScreeningParsedDataDto dataDto: parsedData){
//                if(dataDto.getEditedFundAUM() != null && dataDto.getEditedFundAUM().doubleValue() > 0){
//                    if(uniqueFundsAUM.get(dataDto.getFundId()) == null){
//                        HedgeFundScreeningFundAUMDto fundAUMDto = new HedgeFundScreeningFundAUMDto();
//                        fundAUMDto.setFundId(dataDto.getFundId());
//
//                        fundAUMDto.setFundAUM(null);
//                        fundAUMDto.setFundAUMValueUSD(null);
//
//                        fundAUMDto.setFundAUMCurrency(dataDto.getCurrency());
//                        fundAUMDto.setFundAUMDate(dataDto.getEditedFundAUMDate());
//
//                        fundAUMDto.setEditedFundAUM(dataDto.getEditedFundAUM());
//                        fundAUMDto.setEditedFundAUMDate(dataDto.getEditedFundAUMDate());
//
//                        uniqueFundsAUM.put(dataDto.getFundId(), fundAUMDto);
//
//                    }
//                }
//            }


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
                                    value.getFundAUM(), null, null, value.getFundAUMCurrency(), fundAUMValueUSD,
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
                String investorName = parsedDataMapByFundId.get(value.getFundId()).getInvestmentManager();
                Double strategyAUM = strategyAUMByInvestorName.get(investorName) != null ? strategyAUMByInvestorName.get(investorName).getValue(): null;

                boolean strategyAUMHasMissingCurrencyRates = strategyAUMByInvestorName.get(investorName) != null && strategyAUMByInvestorName.get(investorName).isHasMissingCurrencyRates();
                strategyAUMHasMissingCurrencyRates = strategyAUMHasMissingCurrencyRates && missingCurrencyInvestors.contains(investorName);

                Double fundAUMValue = Math.max(strategyAUM != null ? strategyAUM : 0, (fundAUMValueUSD != null ? fundAUMValueUSD.doubleValue() : 0.0));

                if(fundAUMValue.doubleValue() >= params.getFundAUM().doubleValue()){
                    if(type == 1){
                        // QUALIFIED
                        if(fundAUMValue.doubleValue() >= params.getManagerAUM().doubleValue()) {
                            HedgeFundScreeningFundAUMDto fundAUMDto = new HedgeFundScreeningFundAUMDto(value.getFundId(),
                                    value.getFundAUM(), strategyAUM, null, value.getFundAUMCurrency(), fundAUMValueUSD,
                                    value.getFundAUMDate(), value.getEditedFundAUM(), value.getEditedFundAUMDate(), value.getEditedFundAUMComment(),
                                    missingCurrencyInvestors.contains(parsedDataMapByFundId.get(value.getFundId()).getInvestmentManager()));
                            fundAUMMap.put(value.getFundId(), fundAUMDto);
                        }else{
                            // check manager AUM
                            Double managerAUM = parsedDataMapByFundId.get(value.getFundId()) != null ?
                                    parsedDataMapByFundId.get(value.getFundId()).getManagerAUM() : null;
                            if(managerAUM != null && managerAUM.doubleValue() >= params.getManagerAUM().doubleValue()){
                                HedgeFundScreeningFundAUMDto fundAUMDto = new HedgeFundScreeningFundAUMDto(value.getFundId(),
                                        value.getFundAUM(), strategyAUM, null, value.getFundAUMCurrency(), fundAUMValueUSD, value.getFundAUMDate(),
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
                            if(managerAUM == null || managerAUM.doubleValue() < params.getManagerAUM().doubleValue() && strategyAUMHasMissingCurrencyRates) {
                                //manager AUM missing, or manager AUM less that threshold but there are missing strategy AUM (by currency)
                                HedgeFundScreeningFundAUMDto fundAUMDto = new HedgeFundScreeningFundAUMDto(value.getFundId(),
                                        value.getFundAUM(), strategyAUM, null, value.getFundAUMCurrency(), fundAUMValueUSD, value.getFundAUMDate(),
                                        value.getEditedFundAUM(), value.getEditedFundAUMDate(), value.getEditedFundAUMComment(),
                                        missingCurrencyInvestors.contains(parsedDataMapByFundId.get(value.getFundId()).getInvestmentManager()));
                                fundAUMMap.put(value.getFundId(), fundAUMDto);
                            }

                        }else if(fundAUMValue.doubleValue() < params.getFundAUM().doubleValue() && strategyAUMHasMissingCurrencyRates){
                            HedgeFundScreeningFundAUMDto fundAUMDto = new HedgeFundScreeningFundAUMDto(value.getFundId(),
                                    value.getFundAUM(), strategyAUM, null, value.getFundAUMCurrency(), fundAUMValueUSD, value.getFundAUMDate(),
                                    value.getEditedFundAUM(), value.getEditedFundAUMDate(),  value.getEditedFundAUMComment(),
                                    missingCurrencyInvestors.contains(parsedDataMapByFundId.get(value.getFundId()).getInvestmentManager()));
                            fundAUMMap.put(value.getFundId(), fundAUMDto);
                        }
                    }
                }
            });

            // Add funds that have no AUM data, but have investor manager (strategy) AUM
            getFundsByStrategyAUMWithoutFundAUM(params, type, strategyAUMByInvestorName, parsedData, fundAUMMap,
                    parsedDataMapByFundId, missingCurrencyInvestors);


            lookbackAUMMap.put(lookback, new ArrayList<>(fundAUMMap.values()));
        }
        return lookbackAUMMap;
    }

    private void getFundsByStrategyAUMWithoutFundAUM(HedgeFundScreeningFilteredResultDto params, int type,
                                                     Map<String, HedgeFundScreeningStrategyAUMDto> strategyAUMByInvestorName,
                                                     List<HedgeFundScreeningParsedDataDto> parsedData,
                                                     Map<Long, HedgeFundScreeningFundAUMDto> fundAUMMap,
                                                     Map<Long, HedgeFundScreeningParsedDataDto> parsedDataMapByFundId,
                                                     Set<String> missingCurrencyInvestors){
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
                    List<HedgeFundScreeningParsedDataDto>  excludedFunds = getExcludedFunds(filteredResultId);
                    if(excludedFunds != null && !excludedFunds.isEmpty()){
                        List<HedgeFundScreeningFundAUMDto> resultFundsWithoutExcluded = new ArrayList<>();
                        for(HedgeFundScreeningFundAUMDto fund: resultFunds){
                            boolean found = false;
                            for(HedgeFundScreeningParsedDataDto excludedFund: excludedFunds){
                                if(fund.getFundId().longValue() == excludedFund.getFundId().longValue()){
                                    // skip this fund
                                    found = true;
                                    break;
                                }
                            }
                            if(!found){
                                resultFundsWithoutExcluded.add(fund);
                            }
                        }

                        resultFunds = resultFundsWithoutExcluded;
                    }
                    values[i][j] = resultFunds.size();
                }
            }
        }
        return values;
    }

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
        for(HedgeFundScreeningParsedDataDto record: fundList) {
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
                            //record.setFundAUMDate(fundAUM.getDate());
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
            Double recentStrategyAUM = 0.0;
            Date minStrategyAUMDate = null;
            Set<Long> fundIds = parsedDataMapByManagerName.get(record.getInvestmentManager());
            if(fundIds != null && !fundIds.isEmpty()) {
                List<HedgeFundScreeningParsedDataAUM> strategyFundAUMs = this.parsedDataAUMRepository.getLastAUMByFundIdList(
                        params.getScreeningId(), dateFrom, dateTo, fundIds);
                if (strategyFundAUMs != null) {
                    for (HedgeFundScreeningParsedDataAUM fundStrategyAUM : strategyFundAUMs) {
                        if (fundStrategyAUM.getReturnsCurrency() != null && !fundStrategyAUM.getReturnsCurrency().equalsIgnoreCase(CurrencyLookup.USD.getCode())) {
                            // non-USD
                            CurrencyRatesDto currencyRatesDto =
                                    this.currencyRatesService.getLstRateForMonthDateAndCurrencyBackwards(DateUtils.getLastDayOfCurrentMonth(fundStrategyAUM.getDate()), fundStrategyAUM.getReturnsCurrency());
                            if (currencyRatesDto != null && currencyRatesDto.getValueUSD() != null) {
                                recentStrategyAUM = MathUtils.add(recentStrategyAUM, MathUtils.multiply(fundStrategyAUM.getValue(), currencyRatesDto.getValueUSD()));
                                if (minStrategyAUMDate == null || minStrategyAUMDate.compareTo(fundStrategyAUM.getDate()) > 0) {
                                    minStrategyAUMDate = fundStrategyAUM.getDate();
                                }
                            }else{
                                // missing currency
                                record.setStrategyAUMWithMissingCurrency(true);
                            }
                        } else {
                            // USD
                            recentStrategyAUM = MathUtils.add(recentStrategyAUM, fundStrategyAUM.getValue());
                            if (minStrategyAUMDate == null || minStrategyAUMDate.compareTo(fundStrategyAUM.getDate()) > 0) {
                                minStrategyAUMDate = fundStrategyAUM.getDate();
                            }
                        }
                    }
                }

                if (recentStrategyAUM != 0.0 && minStrategyAUMDate != null) {
                    if(minStrategyAUMDate.compareTo(dateFromLookback) < 0) {
                        record.setRecentStrategyAUM(recentStrategyAUM);
                        record.setRecentStrategyAUMDate(minStrategyAUMDate);
                    }else{
                        record.setStrategyAUM(recentStrategyAUM);
                    }
                }
            }

            // TODO: Ucits as Strategy AUM


            // Set recent track record
            List<HedgeFundScreeningParsedDataReturn> returns =
                    this.parsedDataReturnRepository.findByScreeningIdAndFundId(params.getScreeningId(), record.getFundId(), new Sort(Sort.Direction.DESC, "date"));
            if (returns != null && returns.size() >= params.getTrackRecord().intValue()) {
                Date firstDate = null;
                Date prevDate = null;
                int dateCounter = 0;
                for (HedgeFundScreeningParsedDataReturn returnValue : returns) {
                    // MUST BE DESCENDING ORDER
                    if (firstDate != null && prevDate != null &&
                            DateUtils.getMonthsChanged(returnValue.getDate(), prevDate) == 2) {
                        prevDate = returnValue.getDate();
                        dateCounter++;
                        if (dateCounter == params.getTrackRecord().intValue()) {
                            record.setRecentTrackRecordDate(firstDate);
                            if (firstDate.compareTo(DateUtils.moveDateByMonths(params.getStartDateFromTextOrCurrent(), params.getLookbackReturns().intValue())) == 0) {
                                // unqualified by AUM
                                record.setRecentTrackRecordDateWithinLookback(true);
                            } else {
                                // unqualified by track record
                                record.setRecentTrackRecordDateWithinLookback(false);
                            }
                            break;
                        }
                    } else {
                        firstDate = returnValue.getDate();
                        prevDate = returnValue.getDate();
                        dateCounter = 1;
                    }
                }
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
        List<HedgeFundScreeningFundAUMDto> resultFunds = new ArrayList<>();
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
                        resultFunds.add(fundAUM);
                    }
                }else if(fundAUM.getStrategyAUM() != null && fundAUM.getStrategyAUM().doubleValue() != 0.0){
                    if(fundAUM.getFundId() != null && existingFunds.contains(fundAUM.getFundId())){
                        resultFunds.add(fundAUM);
                    }
                }else if(fundAUM.getEditedFundAUM() != null && fundAUM.getEditedFundAUM().doubleValue() != 0.0){
                    if(fundAUM.getFundId() != null && existingFunds.contains(fundAUM.getFundId())){
                        resultFunds.add(fundAUM);
                    }
                }
            }
        }

        // Excluded funds

//        List<HedgeFundScreeningParsedData>  excludedFunds = this.parsedDataRepository.findExcludedFundsByScreeningId(screeningId);
//        if(excludedFunds != null && !excludedFunds.isEmpty()){
//            List<HedgeFundScreeningFundAUMDto> resultFundsWithoutExcluded = new ArrayList<>();
//            for(HedgeFundScreeningFundAUMDto fund: resultFunds){
//                boolean found = false;
//                for(HedgeFundScreeningParsedData excludedFund: excludedFunds){
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
//            return resultFundsWithoutExcluded;
//        }
//        List<HedgeFundScreeningEditedFund>  excludedFunds = this.editedFundRepository.findByFilteredResultId(filteredResultId);
//        if(excludedFunds != null && !excludedFunds.isEmpty()){
//            List<HedgeFundScreeningFundAUMDto> resultFundsWithoutExcluded = new ArrayList<>();
//            for(HedgeFundScreeningFundAUMDto fund: resultFunds){
//                boolean found = false;
//                for(HedgeFundScreeningEditedFund excludedFund: excludedFunds){
//                    if(fund.getFundId().longValue() == excludedFund.getParsedData().getFundId().longValue()){
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
//            return resultFundsWithoutExcluded;
//        }



        return resultFunds;
    }

    private List<HedgeFundScreeningParsedDataDto> getResultFundListWithAdditionalInfo(Long screeningId, List<HedgeFundScreeningFundAUMDto> fundList){
        List<HedgeFundScreeningParsedDataDto> resultList = new ArrayList<>();
        List<HedgeFundScreeningParsedDataDto> parsedData = getParsedDataFundInfo(screeningId);
        if(parsedData != null){
            for(HedgeFundScreeningParsedDataDto parsedDataDto: parsedData){
                for(HedgeFundScreeningFundAUMDto fund: fundList){
                    if(fund.getFundId().longValue() == parsedDataDto.getFundId().longValue()){

                        if(fund.getFundAUMCurrency() != null && !fund.getFundAUMCurrency().equalsIgnoreCase(CurrencyLookup.USD.getCode())){
                            // non-USD
                            parsedDataDto.setCurrency(fund.getFundAUMCurrency());
                            parsedDataDto.setFundAUMByCurrency(fund.getFundAUM()); // initial value

                            parsedDataDto.setFundAUM(fund.getFundAUMValueUSD()); // USD value
                        }else{
                            // USD
                            parsedDataDto.setFundAUM(fund.getFundAUM());

                        }
                        parsedDataDto.setFundAUMDate(fund.getFundAUMDate());

                        parsedDataDto.setStrategyAUM(fund.getStrategyAUM());
                        parsedDataDto.setStrategyAUMWithMissingCurrency(fund.isStrategyAUMWithMissingCurrency());

                        parsedDataDto.setEditedFundAUM(fund.getEditedFundAUM());
                        parsedDataDto.setEditedFundAUMDate(fund.getEditedFundAUMDate());
                        parsedDataDto.setEditedFundAUMComment(fund.getEditedFundAUMComment());
                        if(fund.getEditedFundAUM() != null && fund.getEditedFundAUMDate() != null){
                            parsedDataDto.setEditedFundAUMDateMonthYear(DateUtils.getMonthYearDate(fund.getEditedFundAUMDate()));
                        }

                        resultList.add(parsedDataDto);
                    }
                }
            }
        }
        return resultList;
    }


    @Override
    public FilesDto getQualifiedFundListAsStream(Long filteredResultId, int lookbackAUM, int lookbackReturn){
        FilesDto filesDto = new FilesDto();

        Resource resource = new ClassPathResource("export_template/hf_scoring/HF_SCORING_TEMPLATE.xlsx");
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
        if(responseDto.getStatus() != null && responseDto.getStatus().getCode().equalsIgnoreCase(ResponseStatusType.FAIL.getCode())){
            logger.error("HF Scoring - Failed to export fund list: filter id=" + filteredResultId.longValue() + ", lookback AUM=" +
                    lookbackAUM + ", lookback return=" + lookbackReturn + (responseDto.getMessage() != null ? responseDto.getMessage().getMessageText() : ""));
            //return null;
        }
        try {
            excelFileToRead = resource.getInputStream();
        } catch (IOException e) {
            logger.error("Reporting: Export file template not found: 'HF_SCORING_TEMPLATE.xlsx'");
            return null;
            //e.printStackTrace();
        }


        try {
            XSSFWorkbook workbook = new XSSFWorkbook(excelFileToRead);
            XSSFSheet sheet = workbook.getSheetAt(0);
            if(responseDto != null && responseDto.getRecords() != null && !responseDto.getRecords().isEmpty()) {
                for (int i = 0; i < responseDto.getRecords().size(); i++) {
                    HedgeFundScreeningParsedDataDto fund  = (HedgeFundScreeningParsedDataDto) responseDto.getRecords().get(i);
                    Row row = sheet.getRow(i + 2);
                    if(row == null){
                        row = sheet.createRow(i + 2);
                        for (int j = 0; j <= 8; j++) {
                            Row prevStyleRow = sheet.getRow(i);
                            if (prevStyleRow.getCell(j) != null && prevStyleRow.getCell(j).getCellStyle() != null) {
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

            HedgeFundScreeningDto screeningDto = get(filteredResultDto.getScreeningId());
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

                Double strategyAUM =  parsedDataMapByFundId.get(fundAUM.getFundId()) != null ?
                        managersMapByName.get(parsedDataMapByFundId.get(fundAUM.getFundId()).getInvestmentManager()) : null;
                fundAUMValue = Math.max(strategyAUM != null ? strategyAUM : 0, fundAUMValue);

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
