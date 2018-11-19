package kz.nicnbk.service.impl.hf;

import kz.nicnbk.common.service.util.DateUtils;
import kz.nicnbk.common.service.util.ExcelUtils;
import kz.nicnbk.common.service.util.PaginationUtils;
import kz.nicnbk.repo.api.hf.*;
import kz.nicnbk.repo.model.files.Files;
import kz.nicnbk.repo.model.hf.*;
import kz.nicnbk.repo.model.lookup.FileTypeLookup;
import kz.nicnbk.service.api.files.FileService;
import kz.nicnbk.service.api.hf.HedgeFundScreeningService;
import kz.nicnbk.service.converter.hf.*;
import kz.nicnbk.service.dto.common.FileUploadResultDto;
import kz.nicnbk.service.dto.common.ResponseStatusType;
import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.hf.*;
import org.apache.poi.ss.formula.functions.Intercept;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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
    private HedgeFundScreeningParsedDataRepository parsedDataRepository;

    @Autowired
    private HedgeFundScreeningParsedDataReturnRepository parsedDataReturnRepository;

    @Autowired
    private HedgeFundScreeningParsedDataAUMRepository parsedDataAUMRepository;

    @Autowired
    private HedgeFundScreeningParsedDataReturnEntityConverter parsedDataReturnEntityConverter;

    @Autowired
    private HedgeFundScreeningParsedDataAUMEntityConverter parsedDataAUMEntityConverter;

    @Autowired
    private HedgeFundScreeningFilteredResultRepository filteredResultRepository;

    @Autowired
    private HedgeFundScreeningFilteredResultEntityConverter filteredResultEntityConverter;

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

                // parsed data
                List<HedgeFundScreeningParsedDataDto> parsedData = getParsedData(id);
                if(parsedData != null && !parsedData.isEmpty()){
                    dto.setParsedData(parsedData);
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
                    this.parsedDataReturnRepository.findByScreeningIdAndDateRange(searchParamsDto.getScreeningId(), dateFrom, dateTo,
                            new Sort(Sort.Direction.ASC, "fundId"));

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
                    this.parsedDataAUMRepository.findByScreeningIdAndDateRange(searchParamsDto.getScreeningId(), dateFrom, dateTo,
                            new Sort(Sort.Direction.ASC, "fundId"));

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

    private List<HedgeFundScreeningParsedDataDto> getParsedData(Long screeningId){
        List<HedgeFundScreeningParsedData> entities = this.parsedDataRepository.findByScreeningId(screeningId, new Sort(Sort.Direction.ASC, "fundId"));
        if(entities != null){
            return parsedDataEntityConverter.disassembleList(entities);
        }
        return null;
    }

    private FileUploadResultDto parseDataFile(FilesDto filesDto, Long screeningId){
        FileUploadResultDto resultDtoSheet1 = parseDataFileSheet1(filesDto, screeningId);
        if(resultDtoSheet1.getStatus() == ResponseStatusType.FAIL){
            deleteFileAndAssociation(filesDto.getId(), screeningId);
            deleteParsedDataSheet1(screeningId);
            return resultDtoSheet1;
        }

        FileUploadResultDto resultDtoSheet2 = parseDataFileSheet2(filesDto, screeningId);
        if(resultDtoSheet2.getStatus() == ResponseStatusType.FAIL){
            deleteFileAndAssociation(filesDto.getId(), screeningId);
            deleteParsedDataSheet2(screeningId);
            return resultDtoSheet2;
        }

        FileUploadResultDto resultDtoSheet3 = parseDataFileSheet3(filesDto, screeningId);
        if(resultDtoSheet3.getStatus() == ResponseStatusType.FAIL){
            deleteFileAndAssociation(filesDto.getId(), screeningId);
            deleteParsedDataSheet3(screeningId);
            return resultDtoSheet3;
        }

        return resultDtoSheet1;
    }

    private FileUploadResultDto deleteFileAndAssociation(Long fileId, Long screeningId){
        FileUploadResultDto resultDtoSheet = new FileUploadResultDto();
        try{
            HedgeFundScreening entity = this.screeningRepository.findOne(screeningId);
            entity.setDataFile(null);
            this.screeningRepository.save(entity);
        }catch (Exception ex){
            String errorMessage = resultDtoSheet.getMessage().getMessageText() + " " +
                    "Failed unset file for screening (unsuccessful parsing).";
            logger.error(errorMessage);
            resultDtoSheet.setErrorMessageEn(errorMessage);
            return resultDtoSheet;
        }

        boolean deleted = this.fileService.delete(fileId);
        if(!deleted){
            String errorMessage = resultDtoSheet.getMessage().getMessageText() + " " +
                    "Failed to delete file (unsuccessful parsing).";
            logger.error(errorMessage);
            resultDtoSheet.setErrorMessageEn(errorMessage);
            return resultDtoSheet;
        }else{
            resultDtoSheet.setStatus(ResponseStatusType.SUCCESS);
            return resultDtoSheet;
        }
    }

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
            List<HedgeFundScreeningParsedData> entities = this.parsedDataEntityConverter.assembleList(records);
            if(entities != null && !entities.isEmpty()) {
                try {
                    // delete parsed data
                    deleteParsedDataSheet1(screeningId);

                    this.parsedDataRepository.save(entities);
                    resultDto.setStatus(ResponseStatusType.SUCCESS);
                    logger.info("Successfully saved parsed HF Screening Parsed data (sheet 1) to database: screening id= " + screeningId);
                }catch (Exception ex){
                    String errorMessage = "Error saving HF Screening Parsed data sheet 1) to database: screening id=" + screeningId;
                    logger.error(errorMessage, ex);
                    resultDto.setErrorMessageEn(errorMessage);
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
                    if(headers.get("Returns Currency") != null){
                        String value = ExcelUtils.getStringValueFromCell(row.getCell(headers.get("Returns Currency").intValue()));
                        returnsCurrency = value;
                    }
                    // AUMS
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
            List<HedgeFundScreeningParsedDataAUM> entities = this.parsedDataAUMEntityConverter.assembleList(records);
            if(entities != null && !entities.isEmpty()) {
                try {
                    // delete parsed data
                    deleteParsedDataSheet3(screeningId);

                    this.parsedDataAUMRepository.save(entities);
                    resultDto.setStatus(ResponseStatusType.SUCCESS);
                    logger.info("Successfully saved parsed HF Screening Parsed AUMs (sheet 3) to database: screening id= " + screeningId);
                }catch (Exception ex){
                    String errorMessage = "Error saving HF Screening Parsed data AUMs (sheet 3) to database";
                    logger.error(errorMessage, ex);
                    resultDto.setErrorMessageEn(errorMessage);
                }
            }
        }

        return resultDto;
    }


    private boolean deleteParsedDataSheet1(Long screeningId){
        try {
            this.parsedDataRepository.deleteByScreeningId(screeningId);
            return true;
        }catch (Exception ex){
            logger.error("Error deleting parsed data for HF Screening with id=" + screeningId + "(with exception)", ex);
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

    private boolean deleteParsedDataSheet3(Long screeningId){
        try {
            this.parsedDataAUMRepository.deleteByScreeningId(screeningId);
            return true;
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
        FileUploadResultDto result = deleteFileAndAssociation(entity.getDataFile().getId(), screeningId);
        if(result.getStatus() == ResponseStatusType.FAIL){
            String errorMessage = "Failed to delete HF Screening data: file id=" + entity.getDataFile().getId() + ", screening id=" + screeningId;
            logger.error(errorMessage);
            return false;
        }
        boolean deletedSheet1 = deleteParsedDataSheet1(screeningId);
        boolean deletedSheet2 = deleteParsedDataSheet2(screeningId);
        boolean deletedSheet3 = deleteParsedDataSheet3(screeningId);
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

        HedgeFundScreeningFilteredResultStatisticsDto statisticsDto = new HedgeFundScreeningFilteredResultStatisticsDto();
        if(params != null) {

            // TODO: params are valid

            // get qualified
            Integer[][] qualified = getFilteredResultStatisticsQualified(params);
            statisticsDto.setQualified(qualified);
            statisticsDto.setParameters(new HedgeFundScreeningFilteredResultDto(params));
        }else{
            // TODO: ALL FUNDS ?
        }
        return statisticsDto;
    }

    @Override
    public List<HedgeFundScreeningParsedDataDto> getFilteredResultQualifiedFundList(HedgeFundScreeningFilteredResultDto params) {

        // TODO : MANAGER AUM

        List<HedgeFundScreeningParsedDataDto> fundList = new ArrayList<>();
        // RETURNS
        Date dateFromReturns = DateUtils.moveDateByMonths(params.getStartDate(), 0 - (params.getLookbackReturns().intValue() + params.getTrackRecord().intValue()));
        Date dateToReturns = DateUtils.moveDateByMonths(dateFromReturns, params.getLookbackReturns().intValue() + params.getTrackRecord().intValue());
        List<HedgeFundScreeningFundCounts>  fundCounts = this.parsedDataReturnRepository.getFundIdCounts(params.getScreeningId(), dateFromReturns, dateToReturns);
        List<HedgeFundScreeningFundCounts>  validFundCounts = new ArrayList<>();
        if(fundCounts != null && !fundCounts.isEmpty()) {
            for(HedgeFundScreeningFundCounts fundCount: fundCounts){
                // Check track record
                if(fundCount.getCount() != null && fundCount.getCount().intValue() >= params.getTrackRecord().intValue()){
                    validFundCounts.add(fundCount);
                }
            }
        }


        // AUM
        Date dateFromAUM = DateUtils.moveDateByMonths(params.getStartDate(), 0 - params.getLookbackAUM().intValue());
        Date dateToAUM = DateUtils.moveDateByMonths(dateFromAUM, params.getLookbackAUM().intValue());

        List<HedgeFundScreeningParsedDataAUM>  fundsAUM = this.parsedDataAUMRepository.getLastAUMByFundId(params.getScreeningId(),
                DateUtils.getFirstDayOfCurrentMonth(dateFromAUM), DateUtils.getFirstDayOfCurrentMonth(dateToAUM));
        List<HedgeFundScreeningParsedDataAUM>  validFundsAUM = new ArrayList<>();
        HashMap<Long, Double> fundAUMValues = new HashMap<>();
        if(fundsAUM != null && !fundsAUM.isEmpty()) {
            for(HedgeFundScreeningParsedDataAUM fundAUM:  fundsAUM){
                if(params.getFundAUM() == null || fundAUM.getValue().doubleValue() >= params.getFundAUM().doubleValue()){
                    validFundsAUM.add(fundAUM);
                    fundAUMValues.put(fundAUM.getFundId(), fundAUM.getValue());
                }
            }
        }

        Long[] fundIds = getIntersectingFunds(validFundCounts, validFundsAUM);
        if(fundIds != null){
            for(int i = 0; i < fundIds.length; i++){
                HedgeFundScreeningParsedData entity = this.parsedDataRepository.findByFundId(fundIds[i]);
                if(entity != null){
                    HedgeFundScreeningParsedDataDto dto = this.parsedDataEntityConverter.disassemble(entity);
                    dto.setFundAUM(fundAUMValues.get(entity.getFundId()));
                    if(dto.getFundAUM().doubleValue() >= params.getManagerAUM().doubleValue() ||
                            dto.getManagerAUM() != null && dto.getManagerAUM().doubleValue() >= params.getManagerAUM().doubleValue()){
                        fundList.add(dto);
                    }

                }
            }
        }
        return fundList;
    }

    private Integer[][] getFilteredResultStatisticsQualified(HedgeFundScreeningFilteredResultDto params){



        // TODO: using  getFilteredResultQualifiedFundList()



        // 1. Get returns lookback
        Map<Integer, List<HedgeFundScreeningFundCounts>> lookbackReturnMap = new HashMap<>();
        for(int lookback = 0; lookback <= params.getLookbackReturns().intValue(); lookback++){
            Date dateFrom = DateUtils.moveDateByMonths(params.getStartDate(), 0 - (lookback + params.getTrackRecord().intValue()));
            Date dateTo = DateUtils.moveDateByMonths(dateFrom, lookback + params.getTrackRecord().intValue());
            List<HedgeFundScreeningFundCounts>  fundCounts = this.parsedDataReturnRepository.getFundIdCounts(params.getScreeningId(), dateFrom, dateTo);
            List<HedgeFundScreeningFundCounts>  validFundCounts = new ArrayList<>();
            if(fundCounts != null && !fundCounts.isEmpty()) {
                for(HedgeFundScreeningFundCounts fundCount: fundCounts){
                    // Check track record
                    if(fundCount.getCount() != null && fundCount.getCount().intValue() >= params.getTrackRecord().intValue()){
                        validFundCounts.add(fundCount);
                    }
                }
            }
            lookbackReturnMap.put(lookback, validFundCounts);
        }

        // 2. Get AUM lookback
        Map<Integer, List<HedgeFundScreeningParsedDataAUM>> lookbackAUMMap = new HashMap<>();
        for(int lookback = 0; lookback <= params.getLookbackReturns().intValue(); lookback++){
            Date dateFrom = DateUtils.moveDateByMonths(params.getStartDate(), 0 - lookback);
            Date dateTo = DateUtils.moveDateByMonths(dateFrom, lookback);

            List<HedgeFundScreeningParsedDataAUM>  fundsAUM = this.parsedDataAUMRepository.getLastAUMByFundId(params.getScreeningId(),
                    DateUtils.getFirstDayOfCurrentMonth(dateFrom), DateUtils.getFirstDayOfCurrentMonth(dateTo));
            List<HedgeFundScreeningParsedDataAUM>  validFundsAUM = new ArrayList<>();
            if(fundsAUM != null && !fundsAUM.isEmpty()) {
                for(HedgeFundScreeningParsedDataAUM fundAUM:  fundsAUM){
                    if(params.getFundAUM() == null || fundAUM.getValue().doubleValue() >= params.getFundAUM().doubleValue()){
                        validFundsAUM.add(fundAUM);
                    }
                }
            }
            lookbackAUMMap.put(lookback, validFundsAUM);
        }

        // 3. Check fund AUM

        // 4. Check manager AUM if needed

        Integer[][] values = new Integer[params.getLookbackReturns().intValue()+2][params.getLookbackAUM().intValue()+2];
        for(int i = 0; i < params.getLookbackReturns().intValue() + 2; i++){
            for(int j = 0; j < params.getLookbackAUM().intValue() + 2; j++){
                if(i == 0 && j == 0){
                    values[i][j] = null;
                    continue;
                }else if(i == 0){
                    values[i][j] = j - 1;
                }else if(j == 0){
                    values[i][j] = i - 1;
                }else {
                    values[i][j] =
                            getIntersectingFunds(lookbackReturnMap.get(i - 1), lookbackAUMMap.get(j - 1)).length;
                }
            }
        }

        return values;
    }

    private Long[] getIntersectingFunds(List<HedgeFundScreeningFundCounts> returnsByFund,
                                         List<HedgeFundScreeningParsedDataAUM> AUMByFund){

        Set<Long> existingFunds = new HashSet<>();
        List<Long> fundIds = new ArrayList<>();
        int result = 0;
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
        return fundIds.toArray(funds);
    }


}
