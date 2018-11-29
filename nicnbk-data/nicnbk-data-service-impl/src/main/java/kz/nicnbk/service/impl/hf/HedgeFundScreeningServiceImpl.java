package kz.nicnbk.service.impl.hf;

import kz.nicnbk.common.service.util.DateUtils;
import kz.nicnbk.common.service.util.ExcelUtils;
import kz.nicnbk.common.service.util.MathUtils;
import kz.nicnbk.common.service.util.PaginationUtils;
import kz.nicnbk.repo.api.hf.*;
import kz.nicnbk.repo.model.files.Files;
import kz.nicnbk.repo.model.hf.*;
import kz.nicnbk.repo.model.lookup.CurrencyLookup;
import kz.nicnbk.repo.model.lookup.FileTypeLookup;
import kz.nicnbk.service.api.common.CurrencyRatesService;
import kz.nicnbk.service.api.files.FileService;
import kz.nicnbk.service.api.hf.HedgeFundScreeningService;
import kz.nicnbk.service.converter.hf.*;
import kz.nicnbk.service.dto.common.FileUploadResultDto;
import kz.nicnbk.service.dto.common.ResponseStatusType;
import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.hf.*;
import kz.nicnbk.service.dto.lookup.CurrencyRatesDto;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.*;

/**
 * Created by magzumov on 12.11.2018.
 */

@Service
public class HedgeFundScreeningServiceImpl implements HedgeFundScreeningService {

    private static final Logger logger = LoggerFactory.getLogger(HedgeFundScreeningServiceImpl.class);

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



    @Override
    public Long save(HedgeFundScreeningDto screeningDto, String username) {
        try {
            if(screeningDto.getId() == null){
                screeningDto.setCreator(username);
            }else{
                screeningDto.setUpdater(username);
                screeningDto.setCreator(null);
            }
            HedgeFundScreening entity = this.screeningEntityConverter.assemble(screeningDto);

            if (entity != null) {
                Long id = this.screeningRepository.save(entity).getId();
                logger.info("Successfully saved HF Screening: id=" + id + " [user]=" + username);
                return id;
            }
        }catch (Exception ex){
            logger.error("Error saving HF Screening (with exception) [user]=" + username, ex);
        }
        return null;
    }

    @Autowired
    private FileService fileService;

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
                List<HedgeFundScreeningParsedDataDto> parsedData = getParsedData(id);
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
    public List<HedgeFundScreeningParsedDataDateValueCombinedDto> searchParsedReturns(HedgeFundScreeningParsedDataDateValueSearchParamsDto searchParamsDto) {
        List<HedgeFundScreeningParsedDataDateValueCombinedDto> returns = new ArrayList<>();
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
                Date maxDate = this.parsedDataReturnRepository.getMaxDate(searchParamsDto.getScreeningId());
                if(maxDate != null) {
                    dateFrom = DateUtils.moveDateByMonths(maxDate, (0 - 11));
                    dateTo = DateUtils.moveDateByMonths(dateFrom, 11);
                }else{
                   // no returns
                    return returns;
                }
            }

            Date[] dates = new Date[12];
            for(int i = 0; i < 12; i++){
                Date date = DateUtils.moveDateByMonths(dateFrom, i);
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

                List<HedgeFundScreeningParsedDataDto> parsedData = getParsedData(searchParamsDto.getScreeningId());
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
    public List<HedgeFundScreeningParsedDataDateValueCombinedDto> searchParsedAUMS(HedgeFundScreeningParsedDataDateValueSearchParamsDto searchParamsDto) {
        List<HedgeFundScreeningParsedDataDateValueCombinedDto> returns = new ArrayList<>();
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
                Date maxDate = this.parsedDataAUMRepository.getMaxDate(searchParamsDto.getScreeningId());
                if(maxDate != null) {
                    dateFrom = DateUtils.moveDateByMonths(maxDate, (0 - 11));
                    dateTo = DateUtils.moveDateByMonths(dateFrom, 11);
                }else{
                    // no returns
                    return returns;
                }
            }

            Date[] dates = new Date[12];
            for(int i = 0; i < 12; i++){
                Date date = DateUtils.moveDateByMonths(dateFrom, i);
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

                List<HedgeFundScreeningParsedDataDto> parsedData = getParsedData(searchParamsDto.getScreeningId());
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
                    this.parsedUcitsDataAUMRepository.findByScreeningIdAndDateRange(searchParamsDto.getScreeningId(), dateFrom, dateTo);

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
    public FileUploadResultDto saveAttachmentDataFile(Long screeningId, FilesDto filesDto, String username) {
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

    private List<HedgeFundScreeningParsedDataDto> getParsedData(Long screeningId){
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
        Iterator<Row> rowIterator = ExcelUtils.getRowIterator(filesDto.getBytes(), filesDto.getFileName(), 0);
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
                            long findId = (long) doubleValue;
                            record.setFundId(findId);
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
        Iterator<Row> rowIterator = ExcelUtils.getRowIterator(filesDto.getBytes(), filesDto.getFileName(), 1);
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

                                    Date date = DateUtils.getDateMonthTextEngYear(key);
                                    if(date != null){
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
        Iterator<Row> rowIterator = ExcelUtils.getRowIterator(filesDto.getBytes(), filesDto.getFileName(), 2);
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

                                    Date date = DateUtils.getDateMonthTextEngYear(key);
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
    public HedgeFundScreeningFilteredResultDto getFilteredResult(Long id) {
        HedgeFundScreeningFilteredResult entity = this.filteredResultRepository.findOne(id);
        if(entity != null){
            HedgeFundScreeningFilteredResultDto dto =  this.filteredResultEntityConverter.disassemble(entity);

            // result statistics
            HedgeFundScreeningFilteredResultStatisticsDto statisticsDto = getFilteredResultStatistics(dto);
            dto.setFilteredResultStatistics(statisticsDto);

            return dto;
        }

        return null;
    }

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

            //System.out.println("UNDECIDED");
            Integer[][] undecided = getFilteredResultStatisticsUndecided(params);
            statisticsDto.setUndecided(undecided);

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
                        unqualified[i][j] = (int) (totalCount - (long) qualified[i][j] - (long) undecided[i][j]);
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


        //check(statisticsDto, params);


        return statisticsDto;
    }

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

    private Map<Integer, List<HedgeFundScreeningFundCounts>> getQualifiedReturnLookbackMap(HedgeFundScreeningFilteredResultDto params){
        Map<Integer, List<HedgeFundScreeningFundCounts>> lookbackReturnMap = new HashMap<>();
        for(int lookback = 0; lookback <= params.getLookbackReturns().intValue(); lookback++){
            List<HedgeFundScreeningFundCounts>  validFundCounts = getQualifiedFundReturnCounts(params, lookback);
            lookbackReturnMap.put(lookback, validFundCounts);
        }
        return lookbackReturnMap;
    }

    private Map<Integer, List<HedgeFundScreeningFundCounts>> getQualifiedReturnLookbackMap_2(HedgeFundScreeningFilteredResultDto params, int lookbackStart){
        Map<Integer, List<HedgeFundScreeningFundCounts>> lookbackReturnMap = new HashMap<>();

        Date dateFromMax = DateUtils.moveDateByMonths(params.getStartDate(), 0 - (Math.max(0, params.getTrackRecord().intValue() + params.getLookbackReturns().intValue() - 1)));
        Assert.isTrue(DateUtils.getMonthsChanged(dateFromMax, params.getStartDate()) == params.getTrackRecord().intValue() + params.getLookbackReturns().intValue());

        List<HedgeFundScreeningParsedDataReturn>  fundReturns =
                this.parsedDataReturnRepository.findByScreeningIdAndDateRange(params.getScreeningId(), dateFromMax, params.getStartDate());

        for(int lookback = lookbackStart; lookback <= params.getLookbackReturns().intValue(); lookback++){
            // Check date
            Date dateFrom = DateUtils.moveDateByMonths(params.getStartDate(), 0 - (Math.max(0, params.getTrackRecord().intValue() + lookback - 1)));

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

    private List<HedgeFundScreeningParsedDataAUM> getAUMFundListByTypeAndLookback(HedgeFundScreeningFilteredResultDto params, int lookback, int type){
        Date date =  DateUtils.moveDateByMonths(params.getStartDate(), 0 - lookback);
        List<HedgeFundScreeningParsedDataAUM>  fundsAUM = this.parsedDataAUMRepository.findByScreeningIdAndDateRange(params.getScreeningId(),
                DateUtils.getFirstDayOfCurrentMonth(date), DateUtils.getFirstDayOfCurrentMonth(date), new Sort(Sort.Direction.DESC, "fundId", "date"));

        // Parsed Data Map (fund id and manager)
        List<HedgeFundScreeningParsedDataDto> parsedData = getParsedData(params.getScreeningId());
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

    private Map<Integer, List<HedgeFundScreeningParsedDataAUM>> getAUMLookbackMap(HedgeFundScreeningFilteredResultDto params, int type){
        Map<Integer, List<HedgeFundScreeningParsedDataAUM>> lookbackAUMMap = new HashMap<>();
        for(int lookback = 0; lookback <= params.getLookbackAUM().intValue(); lookback++){
            List<HedgeFundScreeningParsedDataAUM> resultFundAUMList = getAUMFundListByTypeAndLookback(params, lookback, type);
            lookbackAUMMap.put(lookback, resultFundAUMList);
        }
        return lookbackAUMMap;
    }

    private Map<Integer, List<HedgeFundScreeningParsedDataAUM>> getAUMLookbackMap_2(HedgeFundScreeningFilteredResultDto params, int type, int lookbackStart){
        Map<Integer, List<HedgeFundScreeningParsedDataAUM>> lookbackAUMMap = new HashMap<>();

//        Date dateFromMax =  DateUtils.moveDateByMonths(params.getStartDate(), 0 - params.getLookbackAUM().intValue());
//        List<HedgeFundScreeningParsedDataAUM>  fundsAUM = this.parsedDataAUMRepository.findByScreeningIdAndDateRange(params.getScreeningId(),
//                DateUtils.getFirstDayOfCurrentMonth(dateFromMax), DateUtils.getFirstDayOfCurrentMonth(params.getStartDate()),
//                new Sort(Sort.Direction.DESC, "fundId", "date"));

        // Parsed Data Map (fund id and manager)
        List<HedgeFundScreeningParsedDataDto> parsedData = getParsedData(params.getScreeningId());
        Map<Long, HedgeFundScreeningParsedDataDto> parsedDataMapByFundId = new HashMap<>();
        if(parsedData != null && !parsedData.isEmpty()){
            for(HedgeFundScreeningParsedDataDto dataDto: parsedData){
                parsedDataMapByFundId.put(dataDto.getFundId(), dataDto);
            }
        }

        for(int lookback = lookbackStart; lookback <= params.getLookbackAUM().intValue(); lookback++){

            Date dateFromMax =  DateUtils.moveDateByMonths(params.getStartDate(), 0 - lookback);
            List<HedgeFundScreeningParsedDataAUM>  fundsAUM = this.parsedDataAUMRepository.findByScreeningIdAndDateRange(params.getScreeningId(),
                    DateUtils.getFirstDayOfCurrentMonth(dateFromMax), DateUtils.getFirstDayOfCurrentMonth(params.getStartDate()),
                    new Sort(Sort.Direction.DESC, "fundId", "date"));

            // TODO: Set up manager map
            // TODO: Strategy AUM
            //Map<String, Double> managersMapByName = getManagerTotalStrategyAUMMap(parsedDataMapByFundId, fundsAUM, params.getScreeningId(), date);


            //List<HedgeFundScreeningParsedDataAUM>  resultFundAUMList = new ArrayList<>();
            Map<Long, HedgeFundScreeningParsedDataAUM> fundAUMMap = new HashMap<>();
            Long currentFundId = null;
            if(fundsAUM != null && !fundsAUM.isEmpty()) {
                HedgeFundScreeningParsedDataAUM previousMissingCurrency = null;
                boolean nonUSDfundAUMExists = false;
                for(HedgeFundScreeningParsedDataAUM fundAUM:  fundsAUM){
                    // Check fund
                    if(currentFundId == null){
                        currentFundId = fundAUM.getFundId();
                    }else{
                        if(currentFundId.longValue() == fundAUM.getFundId().longValue()) { // same fund
                            // check date
                            if (fundAUMMap.get(currentFundId) != null &&
                                    fundAUMMap.get(currentFundId).getDate().compareTo(fundAUM.getDate()) > 0) {
                                continue;
                            }
                        }else{ // different fund
                            if(type == 2){// UNDECIDED
                                if(previousMissingCurrency != null && !nonUSDfundAUMExists &&
                                        fundAUMMap.get(fundAUM.getFundId().longValue()) == null && fundAUM.getValue() != null &&
                                        fundAUM.getReturnsCurrency() != null && !fundAUM.getReturnsCurrency().equalsIgnoreCase(CurrencyLookup.USD.getCode())){
                                    // AUM value that is != 0 AND non-USD AND was not added
                                    fundAUMMap.put(previousMissingCurrency.getFundId(), previousMissingCurrency);
                                }
                                previousMissingCurrency = null;
                            }
                            currentFundId = fundAUM.getFundId();
                            nonUSDfundAUMExists = false;
                        }
                    }

                    // Check Currency
                    Double fundAUMValue = fundAUM.getValue().doubleValue();
                    if(fundAUM.getReturnsCurrency() != null && !fundAUM.getReturnsCurrency().equalsIgnoreCase(CurrencyLookup.USD.getCode())){
                        // non-USD
                        CurrencyRatesDto currencyRatesDto = this.currencyRatesService.getLstRateForMonthDateAndCurrencyBackwards(DateUtils.getLastDayOfCurrentMonth(fundAUM.getDate()), fundAUM.getReturnsCurrency());
                        if(currencyRatesDto != null && currencyRatesDto.getValueUSD() != null){
                            fundAUMValue = MathUtils.multiply(currencyRatesDto.getValueUSD(), fundAUM.getValue());
                            nonUSDfundAUMExists = true;
                        }else{
                            // Missing currency rate
                            if(previousMissingCurrency == null || previousMissingCurrency.getDate().compareTo(fundAUM.getDate()) < 0) {
                                previousMissingCurrency = fundAUM;
                            }
                            continue;
                        }
                    }

                    // TODO: Strategy AUM
//                    Double strategyAUM =  parsedDataMapByFundId.get(fundAUM.getFundId()) != null ?
//                            managersMapByName.get(parsedDataMapByFundId.get(fundAUM.getFundId()).getInvestmentManager()) : null;
                    Double strategyAUM = null;
                    fundAUMValue = Math.max(strategyAUM != null ? strategyAUM : 0, fundAUMValue);

                    if(fundAUMValue.doubleValue() >= params.getFundAUM().doubleValue()){
                        if(type == 1){
                            // QUALIFIED
                            if(fundAUMValue.doubleValue() >= params.getManagerAUM().doubleValue()) {
                                //resultFundAUMList.add(fundAUM);
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
                                if(managerAUM == null) {
                                    //resultFundAUMList.add(fundAUM);
                                    fundAUMMap.put(fundAUM.getFundId(), fundAUM);
                                }

                            }
                        }
                    }
                }
            }

            lookbackAUMMap.put(lookback, new ArrayList<>(fundAUMMap.values()));
        }
        return lookbackAUMMap;
    }

    private Integer[][] getLookbackMatrix(int lookBackReturn, int lookbackAUM,
                                          Map<Integer, List<HedgeFundScreeningFundCounts>> lookbackReturnMap,
                                          Map<Integer,List<HedgeFundScreeningParsedDataAUM>> lookbackAUMMap){
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
                    values[i][j] = getIntersectingFunds(lookbackReturnMap.get(i - 1), lookbackAUMMap.get(j - 1)).length;
                }
            }
        }
        return values;
    }

    private Integer[][] getFilteredResultStatisticsQualified(HedgeFundScreeningFilteredResultDto params){

        // 1. Returns lookback
        Map<Integer, List<HedgeFundScreeningFundCounts>> lookbackReturnMap = getQualifiedReturnLookbackMap_2(params, 0);

        // 2. AUM lookback
//        long start = new Date().getTime();
        Map<Integer, List<HedgeFundScreeningParsedDataAUM>> lookbackAUMMap = getAUMLookbackMap_2(params, 1, 0);
//        long end = new Date().getTime();
//        System.out.println("AUM time = " + (end-start) / 1000.);

        Integer[][] values = getLookbackMatrix(params.getLookbackReturns().intValue(), params.getLookbackAUM().intValue(),
                lookbackReturnMap, lookbackAUMMap);

//        Integer[][] values = new Integer[params.getLookbackReturns().intValue() + 2][params.getLookbackAUM().intValue() + 2];
//        for(int i = 0; i <= params.getLookbackReturns().intValue(); i++){
//            for(int j = 0; j <= params.getLookbackAUM().intValue(); j++){
//                if(i == 0 && j == 0){
//                    values[i][j] = null;
//                    continue;
//                }else if(i == 0){
//                    values[i][j] = j - 1;
//                }else if(j == 0){
//                    values[i][j] = i - 1;
//                }else {
//                    HedgeFundScreeningFilteredResultDto lookBackParams = new HedgeFundScreeningFilteredResultDto();
//                    lookBackParams.setScreeningId(params.getScreeningId());
//                    lookBackParams.setFundAUM(params.getFundAUM());
//                    lookBackParams.setManagerAUM(params.getManagerAUM());
//                    lookBackParams.setStartDate(params.getStartDate());
//                    lookBackParams.setTrackRecord(params.getTrackRecord());
//                    lookBackParams.setLookbackReturns(i);
//                    lookBackParams.setLookbackAUM(j);
//                    List<HedgeFundScreeningParsedDataDto> fundList = getFilteredResultQualifiedFundList(lookBackParams);
//                    values[i][j] = fundList.size();
//                }
//            }
//        }

        return values;
    }

    private Integer[][] getFilteredResultStatisticsUndecided(HedgeFundScreeningFilteredResultDto params){

        // 1. Returns lookback
        Map<Integer, List<HedgeFundScreeningFundCounts>> lookbackReturnMap = getQualifiedReturnLookbackMap_2(params, 0);

        // 2. AUM lookback
        Map<Integer, List<HedgeFundScreeningParsedDataAUM>> lookbackAUMMap = getAUMLookbackMap_2(params, 2, 0);

        Integer[][] values = getLookbackMatrix(params.getLookbackReturns().intValue(), params.getLookbackAUM().intValue(),
                lookbackReturnMap, lookbackAUMMap);

        return values;
    }

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

    private List<HedgeFundScreeningParsedDataDto> getQualifiedOrUndecidedFundListByTypeAndManagerAUM(HedgeFundScreeningFilteredResultDto params,
                                                                                                     List<HedgeFundScreeningParsedDataAUM>  validFundsAUM,
                                                                                                     Long[] fundIds, int type){

        List<HedgeFundScreeningParsedDataDto> fundList = new ArrayList<>();

        Map<Long, HedgeFundScreeningParsedDataAUM> validFundsAUMMap = new HashMap<>();
        for(HedgeFundScreeningParsedDataAUM fundAUM: validFundsAUM){
            validFundsAUMMap.put(fundAUM.getFundId(), fundAUM);
        }

        // Parsed Data Map (fund id and manager)
        List<HedgeFundScreeningParsedDataDto> parsedData = getParsedData(params.getScreeningId());
        Map<Long, HedgeFundScreeningParsedDataDto> parsedDataMapByFundId = new HashMap<>();
        if(parsedData != null && !parsedData.isEmpty()){
            for(HedgeFundScreeningParsedDataDto dataDto: parsedData){
                parsedDataMapByFundId.put(dataDto.getFundId(), dataDto);
            }
        }

        // Set up manager map
        Date date =  DateUtils.moveDateByMonths(params.getStartDate(), 0 - params.getLookbackAUM().intValue());
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


    private void fillRecentValidValues(List<HedgeFundScreeningParsedDataDto> fundList,HedgeFundScreeningFilteredResultDto params){

        // Parsed Data Map (fund id and manager)
        List<HedgeFundScreeningParsedDataDto> parsedData = getParsedData(params.getScreeningId());
        Map<String, Set<Long>> parsedDataMapByManagerName = new HashMap<>();
        if(parsedData != null && !parsedData.isEmpty()){
            for(HedgeFundScreeningParsedDataDto dataDto: parsedData){
                if(parsedDataMapByManagerName.get(dataDto.getInvestmentManager()) != null){
                    parsedDataMapByManagerName.get(dataDto.getInvestmentManager()).add(dataDto.getFundId());
                }else {
                    Set<Long> dtoSet = new HashSet<>();
                    dtoSet.add(dataDto.getFundId());
                    parsedDataMapByManagerName.put(dataDto.getInvestmentManager(), dtoSet);
                }
            }
        }
        Date dateFrom = DateUtils.getFirstDayOfCurrentMonth(DateUtils.getDate("01.01.1970"));
        Date dateTo = DateUtils.getFirstDayOfNextMonth(new Date());
        for(HedgeFundScreeningParsedDataDto record: fundList) {
            HedgeFundScreeningParsedDataAUM  fundAUM = this.parsedDataAUMRepository.getLastAUMByFundId(params.getScreeningId(),
                    dateFrom, dateTo, record.getFundId());

            if(fundAUM != null) {
                // Set most recent fund AUM
                if (fundAUM.getReturnsCurrency() != null && !fundAUM.getReturnsCurrency().equalsIgnoreCase(CurrencyLookup.USD.getCode())) {
                    CurrencyRatesDto currencyRatesDto = this.currencyRatesService.getLstRateForMonthDateAndCurrencyBackwards(DateUtils.getLastDayOfCurrentMonth(fundAUM.getDate()), fundAUM.getReturnsCurrency());
                    if (currencyRatesDto != null && currencyRatesDto.getValueUSD() != null) {
                        record.setRecentFundAUM(MathUtils.multiply(fundAUM.getValue(), currencyRatesDto.getValueUSD()));
                        record.setCurrency(fundAUM.getReturnsCurrency());
                        record.setRecentFundAUMDate(fundAUM.getDate());
                    }
                } else {
                    record.setRecentFundAUM(fundAUM.getValue());
                    record.setRecentFundAUMDate(fundAUM.getDate());
                }
            }

            // TODO: Set most recent Strategy AUM
//            if (parsedDataMapByManagerName.get(record.getInvestmentManager()) != null &&
//                    parsedDataMapByManagerName.get(record.getInvestmentManager()).size() > 1) {
//
//                // Ucits manager AUM
//                Map<String, Double> managersAUMMap = new HashMap<>();
//                fillUcitsStrategyManagerAUMMap(managersAUMMap, params.getScreeningId(), dateFrom, dateTo);
//
//                // more than 1 fund for this manager
//                Set<Long> sameManagerFundIds = parsedDataMapByManagerName.get(record.getInvestmentManager());
//                if(sameManagerFundIds != null && !sameManagerFundIds.isEmpty()) {
//                    Date earliestDate = null;
//                    Double strategyAUMSum = null;
//                    String managerCurrency = null;
//                    for (Long fundId: sameManagerFundIds) {
//                        // for each fund
//                        Double mostRecentValue = null;
//                        Date mostRecentDate = null;
//
//                        List<HedgeFundScreeningParsedDataAUM>  sameManagerFundsAUM = this.parsedDataAUMRepository.findByScreeningIdAndDateRangeAndFundId(params.getScreeningId(),
//                                DateUtils.getFirstDayOfCurrentMonth(dateFrom), DateUtils.getFirstDayOfCurrentMonth(dateTo), fundId);
//                        if(sameManagerFundsAUM != null && !sameManagerFundsAUM.isEmpty()){
//                            for (HedgeFundScreeningParsedDataAUM fundAUM: sameManagerFundsAUM) {
//                                String fundCurrency = fundAUM.getReturnsCurrency() != null ? fundAUM.getReturnsCurrency() : CurrencyLookup.USD.getCode();
//                                if(managerCurrency == null){
//                                    managerCurrency = fundCurrency;
//                                }
//                                if(!managerCurrency.equalsIgnoreCase(fundCurrency)){
//                                    // different currency fund for single manager
//                                    record.setDifferentManagerCurrencies(true);
//                                    break;
//                                }
//
//                                if(mostRecentDate == null || mostRecentDate.compareTo(fundAUM.getDate()) < 0) {
//                                    Double valueUSD = null;
//                                    if (fundAUM.getReturnsCurrency() != null && !fundAUM.getReturnsCurrency().equalsIgnoreCase(CurrencyLookup.USD.getCode())) {
//                                        // non-USD
//                                        CurrencyRatesDto currencyRatesDto = this.currencyRatesService.getLstRateForMonthDateAndCurrencyBackwards(DateUtils.getLastDayOfCurrentMonth(fundAUM.getDate()), fundAUM.getReturnsCurrency());
//                                        if (currencyRatesDto != null && currencyRatesDto.getValueUSD() != null) {
//                                            valueUSD = MathUtils.multiply(fundAUM.getValue(), currencyRatesDto.getValueUSD());
//                                        }
//                                    } else {
//                                        valueUSD = fundAUM.getValue();
//                                    }
//                                    if(valueUSD != null){
//                                        mostRecentDate = fundAUM.getDate();
//                                        mostRecentValue = valueUSD;
//                                    }
//                                }
//                            }
//
//                            strategyAUMSum = MathUtils.add(strategyAUMSum, mostRecentValue);
//                        }
//                        if(earliestDate == null || (mostRecentDate != null && earliestDate.compareTo(mostRecentDate) > 0)){
//                            earliestDate = mostRecentDate;
//                        }
//                    }
//
//                    if(record.getDifferentManagerCurrencies() == null || !record.getDifferentManagerCurrencies().booleanValue()) {
//                        record.setRecentStrategyAUM(MathUtils.add(managersAUMMap.get(record.getInvestmentManager()),strategyAUMSum));
//                        record.setRecentStrategyAUMDate(earliestDate);
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
                for (HedgeFundScreeningParsedDataReturn returnValue : returns) {
                    // MUST BE DESCENDING ORDER
                    if (firstDate != null && prevDate != null &&
                            DateUtils.getMonthsChanged(returnValue.getDate(), prevDate) == 2) {
                        prevDate = returnValue.getDate();
                        dateCounter++;
                        if (dateCounter == params.getTrackRecord().intValue()) {
                            record.setRecentTrackRecordDate(firstDate);
                            if (firstDate.compareTo(DateUtils.moveDateByMonths(params.getStartDate(), params.getLookbackReturns().intValue())) == 0) {
                                record.setRecentTrackRecordDateWithinLookback(true);
                            } else {
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
        }
    }

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

    private void fillUcitsStrategyManagerAUMMap(Map<String, Double> managersMapByName, Long screeningId, Date dateFrom, Date dateTo){
        // Ucits AUM
        List<HedgeFundScreeningParsedUcitsDataAUM>  ucitsFundsAUM = this.parsedUcitsDataAUMRepository.findByScreeningIdAndDateRange(screeningId,
                DateUtils.getFirstDayOfCurrentMonth(dateFrom), DateUtils.getFirstDayOfCurrentMonth(dateTo));
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

//    @Override
//    public List<HedgeFundScreeningParsedDataDto> getFilteredResultQualifiedFundList(HedgeFundScreeningFilteredResultDto params) {
//
//        return getFilteredResultQualifiedFundList2(params);
////        // RETURNS
////        List<HedgeFundScreeningFundCounts>  validFundCounts = getQualifiedFundReturnCounts(params, params.getLookbackReturns().intValue());
////
////        // AUM
////        List<HedgeFundScreeningParsedDataAUM>  validFundsAUM = getAUMFundListByTypeAndLookback(params, params.getLookbackAUM().intValue(), 1);
////
////
////        Long[] fundIds = getIntersectingFunds(validFundCounts, validFundsAUM);
////
////        if(fundIds != null){
////            List<HedgeFundScreeningParsedDataDto> fundList =  getQualifiedOrUndecidedFundListByTypeAndManagerAUM(params, validFundsAUM, fundIds, 1);
////            Collections.sort(fundList);
////            return fundList;
////        }
////
////        return new ArrayList<>();
//    }

    @Override
    public List<HedgeFundScreeningParsedDataDto> getFilteredResultQualifiedFundList(HedgeFundScreeningFilteredResultDto params) {


        // RETURNS
        Map<Integer, List<HedgeFundScreeningFundCounts>> returnsMap = getQualifiedReturnLookbackMap_2(params, params.getLookbackReturns().intValue());
        Assert.isTrue(returnsMap.size() == 1);
        Assert.isTrue(returnsMap.get(params.getLookbackReturns().intValue()) != null);


        // AUM
        Map<Integer, List<HedgeFundScreeningParsedDataAUM>> aumsMap = getAUMLookbackMap_2(params, 1, params.getLookbackAUM().intValue());
        Assert.isTrue(aumsMap.size() == 1);
        Assert.isTrue(aumsMap.get(params.getLookbackAUM().intValue()) != null);

        Long[] fundIds = getIntersectingFunds(returnsMap.get(params.getLookbackReturns().intValue()), aumsMap.get(params.getLookbackAUM().intValue()));

        if(fundIds != null){
            List<HedgeFundScreeningParsedDataDto> fundList =  getQualifiedOrUndecidedFundListByTypeAndManagerAUM(params, aumsMap.get(params.getLookbackAUM().intValue()), fundIds, 1);
            Collections.sort(fundList);
            return fundList;
        }

        return new ArrayList<>();
    }

    @Override
    public List<HedgeFundScreeningParsedDataDto> getFilteredResultUnqualifiedFundList(HedgeFundScreeningFilteredResultDto params) {
        List<HedgeFundScreeningParsedDataDto> unqualifiedList = new ArrayList<>();
        List<HedgeFundScreeningParsedDataDto> qualifiedList = getFilteredResultQualifiedFundList(params);
        List<HedgeFundScreeningParsedDataDto> undecidedList = getFilteredResultUndecidedFundList(params);

        List<HedgeFundScreeningParsedDataDto> parsedDataList = getParsedData(params.getScreeningId());
        if(parsedDataList != null && !parsedDataList.isEmpty()){
            for(HedgeFundScreeningParsedDataDto parsedDataDto: parsedDataList){
                boolean exists = false;
                for(HedgeFundScreeningParsedDataDto qualified: qualifiedList){
                    if(qualified.getFundId().longValue() == parsedDataDto.getFundId().longValue()){
                        exists = true;
                        break;
                    }
                }
                if(!exists) {
                    for (HedgeFundScreeningParsedDataDto undecided : undecidedList) {
                        if(undecided.getFundId().longValue() == parsedDataDto.getFundId().longValue()){
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

        fillRecentValidValues(unqualifiedList, params);

        Collections.sort(unqualifiedList);
//        for(HedgeFundScreeningParsedDataDto dto: unqualifiedList){
//            System.out.println(dto.getFundId().longValue());
//        }
        return unqualifiedList;
    }

    @Override
    public List<HedgeFundScreeningParsedDataDto> getFilteredResultUndecidedFundList(HedgeFundScreeningFilteredResultDto params) {
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
        Map<Integer, List<HedgeFundScreeningFundCounts>> returnsMap = getQualifiedReturnLookbackMap_2(params, params.getLookbackReturns().intValue());
        Assert.isTrue(returnsMap.size() == 1);
        Assert.isTrue(returnsMap.get(params.getLookbackReturns().intValue()) != null);


        // AUM
        Map<Integer, List<HedgeFundScreeningParsedDataAUM>> aumsMap = getAUMLookbackMap_2(params, 2, params.getLookbackAUM().intValue());
        Assert.isTrue(aumsMap.size() == 1);
        Assert.isTrue(aumsMap.get(params.getLookbackAUM().intValue()) != null);

        Long[] fundIds = getIntersectingFunds(returnsMap.get(params.getLookbackReturns().intValue()), aumsMap.get(params.getLookbackAUM().intValue()));

        if(fundIds != null){
            List<HedgeFundScreeningParsedDataDto> fundList =  getQualifiedOrUndecidedFundListByTypeAndManagerAUM(params, aumsMap.get(params.getLookbackAUM().intValue()), fundIds, 2);
            Collections.sort(fundList);
            return fundList;
        }

        return new ArrayList<>();
    }

    private Long[] getIntersectingFunds(List<HedgeFundScreeningFundCounts> returnsByFund,
                                        List<HedgeFundScreeningParsedDataAUM> AUMByFund){

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

    private void check(HedgeFundScreeningFilteredResultStatisticsDto statisticsDto, HedgeFundScreeningFilteredResultDto params){

        // TEST QUALIFIED
        System.out.println("CHECK QUALIFIED: ");
        if(statisticsDto.getQualified() != null && statisticsDto.getQualified().length > 0){
            for(int i = 1; i < statisticsDto.getQualified().length; i++){
                for(int j = 1; j < statisticsDto.getQualified()[i].length; j++){
                    int count = statisticsDto.getQualified()[i][j];

                    HedgeFundScreeningFilteredResultDto newParams = new HedgeFundScreeningFilteredResultDto(params);
                    newParams.setLookbackReturns(i - 1);
                    newParams.setLookbackAUM(j - 1);
                    List<HedgeFundScreeningParsedDataDto> fundList = getFilteredResultQualifiedFundList(newParams);

                    if(fundList.size() != count){
                        System.out.println((i - 1) + "-" + (j - 1) + ": MISMATCH stats=" + count + ", fundlist=" + fundList.size());
                    }else{
                        System.out.println((i - 1) + "-" + (j - 1) + ": OK");
                    }
                }
            }
        }

        // TEST UNDECIDED
        System.out.println("CHECK UNDECIDED: ");
        if(statisticsDto.getQualified() != null && statisticsDto.getUndecided().length > 0){
            for(int i = 1; i < statisticsDto.getUndecided().length; i++){
                for(int j = 1; j < statisticsDto.getUndecided()[i].length; j++){
                    int count = statisticsDto.getUndecided()[i][j];

                    HedgeFundScreeningFilteredResultDto newParams = new HedgeFundScreeningFilteredResultDto(params);
                    newParams.setLookbackReturns(i - 1);
                    newParams.setLookbackAUM(j - 1);
                    List<HedgeFundScreeningParsedDataDto> fundList = getFilteredResultUndecidedFundList(newParams);

                    if(fundList.size() != count){
                        System.out.println((i - 1) + "-" + (j - 1) + ": MISMATCH stats=" + count + ", fundlist=" + fundList.size());
                    }else{
                        System.out.println((i - 1) + "-" + (j - 1) + ": OK");
                    }
                }
            }
        }

        // TEST UNQUALIFIED
        System.out.println("CHECK UNQUALIFIED: ");
        if(statisticsDto.getQualified() != null && statisticsDto.getUnqualified().length > 0){
            for(int i = 1; i < statisticsDto.getUnqualified().length; i++){
                for(int j = 1; j < statisticsDto.getUnqualified()[i].length; j++){
                    int count = statisticsDto.getUnqualified()[i][j];

                    HedgeFundScreeningFilteredResultDto newParams = new HedgeFundScreeningFilteredResultDto(params);
                    newParams.setLookbackReturns(i - 1);
                    newParams.setLookbackAUM(j - 1);
                    List<HedgeFundScreeningParsedDataDto> fundList = getFilteredResultUnqualifiedFundList(newParams);

                    if(fundList.size() != count){
                        System.out.println((i - 1) + "-" + (j - 1) + ": MISMATCH stats=" + count + ", fundlist=" + fundList.size());
                    }else{
                        System.out.println((i - 1) + "-" + (j - 1) + ": OK");
                    }
                }
            }
        }
    }
}
