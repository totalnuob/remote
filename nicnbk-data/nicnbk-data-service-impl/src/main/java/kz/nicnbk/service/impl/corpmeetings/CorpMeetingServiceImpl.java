package kz.nicnbk.service.impl.corpmeetings;

import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.common.service.util.PaginationUtils;
import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.repo.api.corpmeetings.*;
import kz.nicnbk.repo.model.corpmeetings.*;
import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.repo.model.lookup.FileTypeLookup;
import kz.nicnbk.repo.model.lookup.corpmeetings.ICMeetingTopicTypeLookup;
import kz.nicnbk.service.api.corpmeetings.CorpMeetingService;
import kz.nicnbk.service.api.employee.EmployeeService;
import kz.nicnbk.service.api.files.FileService;
import kz.nicnbk.service.converter.corpmeetings.ICMeetingTopicEntityConverter;
import kz.nicnbk.service.converter.corpmeetings.ICMeetingsEntityConverter;
import kz.nicnbk.service.dto.authentication.UserRoles;
import kz.nicnbk.service.dto.common.EntitySaveResponseDto;
import kz.nicnbk.service.dto.common.ResponseStatusType;
import kz.nicnbk.service.dto.corpmeetings.*;
import kz.nicnbk.service.dto.employee.EmployeeDto;
import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.files.NamedFilesDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by zhambyl on 04-Aug-16.
 */
@Service
public class CorpMeetingServiceImpl implements CorpMeetingService {

    private static final Logger logger = LoggerFactory.getLogger(CorpMeetingServiceImpl.class);

    public static final int maxOpenEntities = 5;

    @Autowired
    private FileService fileService;

    @Autowired
    private ICMeetingTopicRepository icMeetingTopicRepository;

    @Autowired
    private ICMeetingFilesRepository icMeetingFilesRepository;

    @Autowired
    private ICMeetingTopicFilesRepository icMeetingTopicFilesRepository;

    @Autowired
    private ICMeetingTopicEntityConverter icMeetingTopicEntityConverter;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private ICMeetingsRepository icMeetingsRepository;

    @Autowired
    private ICMeetingsEntityConverter icMeetingsEntityConverter;


//    @Deprecated
//    @Override
//    public EntitySaveResponseDto save(CorpMeetingDto corpMeetingDto, String updater) {
//        try {
//            CorpMeeting entity = corpMeetingsEntityConverter.assemble(corpMeetingDto);
//            if(corpMeetingDto.getId() == null){ // CREATE
//                EmployeeDto employee = this.employeeService.findByUsername(updater);
//                // set creator
//                entity.setCreator(new Employee(employee.getId()));
//            }else{ // UPDATE
//                // set creator
//                CorpMeeting currentEntity = this.corpMeetingsRepository.findOne(corpMeetingDto.getId());
//                Employee employee = currentEntity.getCreator();
//                entity.setCreator(employee);
//                // set creation date
//                Date creationDate = currentEntity.getCreationDate();
//                entity.setCreationDate(creationDate);
//                // set update date
//                entity.setUpdateDate(new Date());
//                // set updater
//                EmployeeDto updatedby = this.employeeService.findByUsername(updater);
//                entity.setUpdater(new Employee(updatedby.getId()));
//            }
//
//            entity = corpMeetingsRepository.save(entity);
//            logger.info(corpMeetingDto.getId() == null ? "Corp meeting created: " + entity.getId() + ", by " + entity.getCreator().getUsername() :
//                    "Corp meeting updated: " + entity.getId() + ", by " + updater);
//
//            EntitySaveResponseDto saveResponseDto = new EntitySaveResponseDto();
//            saveResponseDto.setEntityId(entity.getId());
//            saveResponseDto.setStatus(ResponseStatusType.SUCCESS);
//            return saveResponseDto;
//        }catch (Exception ex){
//            logger.error("Error saving Corp meeting: " + (corpMeetingDto != null && corpMeetingDto.getId() != null ? corpMeetingDto.getId() : "new") ,ex);
//            EntitySaveResponseDto saveResponseDto = new EntitySaveResponseDto();
//            saveResponseDto.setErrorMessageEn("Error saving Corp meeting: " + (corpMeetingDto != null && corpMeetingDto.getId() != null ? corpMeetingDto.getId() : "new"));
//            return saveResponseDto;
//        }
//    }
//    @Deprecated
//    @Override
//    public CorpMeetingDto get(Long id) {
//        try {
//            CorpMeeting entity = corpMeetingsRepository.findOne(id);
//            CorpMeetingDto dto = corpMeetingsEntityConverter.disassemble(entity);
//
//            dto.setFiles(getAttachments(id));
//            return dto;
//        }catch(Exception ex){
//            logger.error("Error loading corp meeting: " + id, ex);
//        }
//        return null;
//    }
//    @Deprecated
//    @Override
//    public boolean safeDelete(Long id, String username) {
//        try {
//            CorpMeeting entity = corpMeetingsRepository.findOne(id);
//            entity.setDeleted(true);
//
//            this.corpMeetingsRepository.save(entity);
//            logger.info("Corp meeting deleted with id=" + id + " [user]=" + username);
//            return true;
//        }catch(Exception ex){
//            logger.error("Error deleting corp meeting: id=" + id + ", ]user]=" + username, ex);
//        }
//        return false;
//    }
//    @Deprecated
//    @Override
//    public CorpMeetingsPagedSearchResult search(CorpMeetingsSearchParamsDto searchParams) {
//        try {
//            Page<CorpMeeting> entitiesPage = null;
//            int page = 0;
//            if (searchParams == null || searchParams.isEmpty()) {
//                int pageSize = searchParams != null && searchParams.getPageSize() > 0 ? searchParams.getPageSize() : DEFAULT_PAGE_SIZE;
//                page = searchParams != null && searchParams.getPage() > 0 ? searchParams.getPage() - 1 : 0;
//                entitiesPage = corpMeetingsRepository.findAllByOrderByDateDesc(new PageRequest(page, pageSize, new Sort(Sort.Direction.DESC, "date", "id")));
//            } else {
//                page = searchParams.getPage() > 0 ? searchParams.getPage() - 1 : 0;
//
//                entitiesPage = corpMeetingsRepository.searchMeetings(StringUtils.isValue(searchParams.getType()) ? searchParams.getType() : null, searchParams.getNumber(),
//                        searchParams.getSearchText(), searchParams.getDateFrom(), searchParams.getDateTo(),
//                        new PageRequest(page, searchParams.getPageSize(), new Sort(Sort.Direction.DESC, "date", "id")));
//
//            }
//
//            CorpMeetingsPagedSearchResult result = new CorpMeetingsPagedSearchResult();
//            if (entitiesPage != null) {
//                result.setTotalElements(entitiesPage.getTotalElements());
//                if (entitiesPage.getTotalElements() > 0) {
//                    result.setShowPageFrom(PaginationUtils.getShowPageFrom(DEFAULT_PAGES_PER_VIEW, page));
//                    result.setShowPageTo(PaginationUtils.getShowPageTo(DEFAULT_PAGES_PER_VIEW,
//                            page, result.getShowPageFrom(), entitiesPage.getTotalPages()));
//                }
//                result.setTotalPages(entitiesPage.getTotalPages());
//                result.setCurrentPage(page + 1);
//                if (searchParams != null) {
//                    result.setSearchParams(searchParams.getSearchParamsAsString());
//                }
//                result.setCorpMeetings(corpMeetingsEntityConverter.disassembleList(entitiesPage.getContent()));
//            }
//            return result;
//        }catch(Exception ex){
//            // TODO: log search params
//            logger.error("Error searching corp meetings", ex);
//        }
//        return null;
//    }
//    @Deprecated
//    @Override
//    public Set<FilesDto> saveAttachments(Long meetingId, Set<FilesDto> attachments) {
//        try {
//            Set<FilesDto> dtoSet = new HashSet<>();
//            if (attachments != null) {
//                Iterator<FilesDto> iterator = attachments.iterator();
//                while (iterator.hasNext()) {
//                    FilesDto filesDto = iterator.next();
//                    if (filesDto.getId() == null) {
//                        Long fileId = fileService.save(filesDto, FileTypeLookup.CORP_MEETING_MATERIALS.getCatalog());
//                        logger.info("Saved corp meeting attachment files: meeting id=" + meetingId + ", file=" + fileId);
//
//                        CorpMeetingFiles corpMeetingFiles = new CorpMeetingFiles(meetingId, fileId);
//                        corpMeetingFilesRepository.save(corpMeetingFiles);
//                        logger.info("Saved corp meeting attachment information to DB: meeting id=" + meetingId + ", file=" + fileId);
//
//                        FilesDto newFileDto = new FilesDto();
//                        newFileDto.setId(fileId);
//                        newFileDto.setFileName(filesDto.getFileName());
//                        dtoSet.add(newFileDto);
//                    }
//                }
//            }
//            return dtoSet;
//        }catch (Exception ex){
//            logger.error("Error saving corp meeting attachments: corp meeting id=" + meetingId, ex);
//        }
//        return null;
//    }
//    @Deprecated
//    @Override
//    public Set<FilesDto> getAttachments(Long meetingId) {
//        try {
//            List<CorpMeetingFiles> entities = corpMeetingFilesRepository.getFilesByMeetingId(meetingId);
//            Set<FilesDto> files = new HashSet<>();
//            if (entities != null) {
//                for (CorpMeetingFiles corpMeetingFiles : entities) {
//                    FilesDto fileDto = new FilesDto();
//                    fileDto.setId(corpMeetingFiles.getFile().getId());
//                    fileDto.setFileName(corpMeetingFiles.getFile().getFileName());
//                    files.add(fileDto);
//                }
//            }
//            return files;
//        }catch(Exception ex){
//            logger.error("Error getting corp meeting attachments: corp meeting id=" + meetingId, ex);
//        }
//        return null;
//    }
//    @Deprecated
//    @Override
//    public boolean safeDeleteICMeetingAttachment(Long meetingId, Long fileId, String username) {
//        try {
//            ICMeetingFiles entity = icMeetingFilesRepository.getFilesByFileId(fileId);
//            if (entity != null && entity.getIcMeeting().getId().longValue() == meetingId) {
//                boolean deleted = fileService.safeDelete(fileId);
//                if(!deleted){
//                    logger.error("Failed to delete(safe) IC meeting attachment: IC meeting =" + meetingId + ", file=" + fileId + ", by " + username);
//                }else{
//                    logger.info("Deleted(safe) IC meeting  attachment: IC meeting =" + meetingId + ", file=" + fileId + ", by " + username);
//                }
//                return deleted;
//            }
//        }catch (Exception e){
//            logger.error("Failed to delete(safe) IC meeting  attachment with error: IC meeting =" + meetingId + ", file=" + fileId + ", by " + username, e);
//        }
//        return false;
//    }


    /* IC MEETING TOPIC ***********************************************************************************************/
    @Override
    public EntitySaveResponseDto saveICMeetingTopic(ICMeetingTopicDto dto, String updater) {
        try {
            EntitySaveResponseDto saveResponseDto = new EntitySaveResponseDto();
            if(StringUtils.isEmpty(dto.getType())){
                String errorMessage = "Error saving IC Meeting Topic: type required";
                logger.error(errorMessage);
                saveResponseDto.setErrorMessageEn(errorMessage);
                return saveResponseDto;
            }
            if(StringUtils.isEmpty(dto.getName())){
                String errorMessage = "Error saving IC Meeting Topic: short name required";
                logger.error(errorMessage);
                saveResponseDto.setErrorMessageEn(errorMessage);
                return saveResponseDto;
            }
            ICMeetingTopic entity = icMeetingTopicEntityConverter.assemble(dto);
            if(dto.getId() == null){ // CREATE
                EmployeeDto employee = this.employeeService.findByUsername(updater);
                // set creator
                entity.setCreator(new Employee(employee.getId()));
            }else{ // UPDATE
                // set creator
                if(!checkEditableICMeetingTopicByIdAndUsername(dto.getId(), updater)){
                    String errorMessage = "Error saving IC Meeting Topic with id " + dto.getId() + ": entity not editable";
                    logger.error(errorMessage);
                    saveResponseDto.setErrorMessageEn(errorMessage);
                    return saveResponseDto;
                }
                ICMeetingTopic currentEntity = this.icMeetingTopicRepository.findOne(dto.getId());
                Employee employee = currentEntity.getCreator();
                entity.setCreator(employee);
                // set creation date
                Date creationDate = currentEntity.getCreationDate();
                entity.setCreationDate(creationDate);
                // set update date
                entity.setUpdateDate(new Date());
                // set updater
                EmployeeDto updatedby = this.employeeService.findByUsername(updater);
                entity.setUpdater(new Employee(updatedby.getId()));
            }
            if(entity.getIcMeeting() != null && StringUtils.isEmpty(entity.getDecision())){
                ICMeetingDto icMeeting = getICMeeting(entity.getIcMeeting().getId());
                if(icMeeting != null && icMeeting.getClosed() != null && icMeeting.getClosed().booleanValue()) {
                    String errorMessage = "Selected IC meeting has status CLOSED, field 'DECISION MADE' is required";
                    logger.error(errorMessage);
                    saveResponseDto.setErrorMessageEn(errorMessage);
                    return saveResponseDto;
                }
            }

            entity = icMeetingTopicRepository.save(entity);
            logger.info(dto.getId() == null ? "IC meeting topic created: " + entity.getId() + ", by " + entity.getCreator().getUsername() :
                    "IC meeting topic updated: " + entity.getId() + ", by " + updater);

            saveResponseDto.setEntityId(entity.getId());
            saveResponseDto.setStatus(ResponseStatusType.SUCCESS);
            return saveResponseDto;
        }catch (Exception ex){
            logger.error("Error saving IC meeting topic: " + (dto != null && dto.getId() != null ? dto.getId() : "new") ,ex);
            EntitySaveResponseDto saveResponseDto = new EntitySaveResponseDto();
            saveResponseDto.setErrorMessageEn("Error saving IC meeting topic: " + (dto != null && dto.getId() != null ? dto.getId() : "new"));
            return saveResponseDto;
        }
    }

    @Override
    public ICMeetingTopicDto getICMeetingTopic(Long id) {
        try {
            ICMeetingTopic entity = icMeetingTopicRepository.findOne(id);
            ICMeetingTopicDto dto = icMeetingTopicEntityConverter.disassemble(entity);

            // set files
            dto.setMaterials(getICMeetingTopicAttachments(id));
            return dto;
        }catch(Exception ex){
            logger.error("Error loading corp meeting: " + id, ex);
        }
        return null;
    }

    @Override
    public Set<NamedFilesDto> getICMeetingTopicAttachments(Long id){
        try {
            List<ICMeetingTopicFiles> entities = icMeetingTopicFilesRepository.getFilesByMeetingId(id);

            Set<NamedFilesDto> files = new HashSet<>();
            if (entities != null) {
                for (ICMeetingTopicFiles icMeetingTopicFiles : entities) {
                    FilesDto fileDto = new FilesDto();
                    fileDto.setId(icMeetingTopicFiles.getFile().getId());
                    fileDto.setFileName(icMeetingTopicFiles.getFile().getFileName());
                    NamedFilesDto namedFilesDto = new NamedFilesDto();
                    namedFilesDto.setFile(fileDto);
                    namedFilesDto.setName("????");
                    files.add(namedFilesDto);
                }
            }
            return files;
        }catch(Exception ex){
            logger.error("Error getting IC meeting topic attachments: IC meeting topic id=" + id, ex);
        }
        return null;
    }

    @Override
    public boolean safeDeleteICMeetingTopic(Long id, String username) {
        try {
            ICMeetingTopic entity = icMeetingTopicRepository.findOne(id);
            if(!checkEditableICMeetingTopicByIdAndUsername(id, username)){
                String errorMessage = "Error safe deleting IC Meeting topic with id + " + id + ": entity not editable";
                logger.error(errorMessage);
                return false;
            }
            entity.setDeleted(true);
//            List<ICMeetingFiles> files = this.icMeetingFilesRepository.getFilesByMeetingId(entity.getId());
//            if(files != null && !files.isEmpty()){
//                for(ICMeetingFiles file: files){
//                    this.fileService.safeDelete(file.getFile().getId());
//                }
//            }
            this.icMeetingTopicRepository.save(entity);
            logger.info("IC meeting topic safe deleted with id=" + id + " [user]=" + username);
            return true;
        }catch(Exception ex){
            logger.error("Error safe deleting IC meeting topic: id=" + id + ", ]user]=" + username, ex);
        }
        return false;
    }

    private boolean checkEditableICMeetingTopicByIdAndUsername(Long id, String username){
        try {
            ICMeetingTopic entity = icMeetingTopicRepository.findOne(id);
            if(entity != null){
                if(entity.getIcMeeting() != null && entity.getIcMeeting().getClosed() != null &&
                        entity.getIcMeeting().getClosed().booleanValue()){
                    return false;
                }
                String type = entity.getType() != null ? entity.getType().getCode() : null;
                if(!checkUserRolesForICMeetingTopicByTypeAndUsername(type, username, true)){
                    return false;
                }

                return true;
            }
        }catch(Exception ex){
            logger.error("Error checking IC meeting topic editable: id =" + id, ex);
        }
        return false;
    }

    @Deprecated
    public boolean checkUserRolesForICMeetingTopicByTypeAndUsername(String type, String username, boolean editing){
        EmployeeDto updaterDto = this.employeeService.findByUsername(username);
        if(updaterDto == null || updaterDto.getRoles() == null || updaterDto.getRoles().isEmpty()){
            return false;
        }
        boolean icMember = false;
        boolean accessByDept = false;
        for(BaseDictionaryDto role: updaterDto.getRoles()){
            if(role.getCode().equalsIgnoreCase(UserRoles.ADMIN.getCode()) ||
                    role.getCode().equalsIgnoreCase(UserRoles.CORP_MEETING_EDIT.getCode()) || type == null){
                return true;
            }
            if(role.getCode().equalsIgnoreCase(UserRoles.IC_MEMBER.getCode())){
                icMember = true;
            }
            if(icMember) {
                if (!editing) {
                    // IC Members can view all
                    return true;
                }
            }
            if(type != null && type.equalsIgnoreCase(ICMeetingTopicTypeLookup.HEDGE_FUNDS.getCode()) &&
                    role.getCode().equalsIgnoreCase(UserRoles.HEDGE_FUNDS_EDIT.getCode())){
                accessByDept = true;
            }else if(type != null && type.equalsIgnoreCase(ICMeetingTopicTypeLookup.PRIVATE_EQUITY.getCode()) &&
                    role.getCode().equalsIgnoreCase(UserRoles.PRIVATE_EQUITY_EDIT.getCode())){
                accessByDept = true;
            }else if(type != null && type.equalsIgnoreCase(ICMeetingTopicTypeLookup.REAL_ESTATE.getCode()) &&
                    role.getCode().equalsIgnoreCase(UserRoles.REAL_ESTATE_EDIT.getCode())){
                accessByDept = true;
            }else if(type != null && type.equalsIgnoreCase(ICMeetingTopicTypeLookup.STRATEGY_RISKS.getCode()) &&
                    role.getCode().equalsIgnoreCase(UserRoles.STRATEGY_RISK_EDIT.getCode())){
                accessByDept = true;
            }else if(type != null && type.equalsIgnoreCase(ICMeetingTopicTypeLookup.REPORTING.getCode()) &&
                    role.getCode().equalsIgnoreCase(UserRoles.REPORTING_EDIT.getCode())){
                accessByDept = true;
            }
        }
        return icMember && accessByDept;
    }

    private Set<String> getAllowedICMeetingTopicTypeCodesForUser(String username, boolean editing){
        Set<String> allowedCodes = new HashSet<>();
        for(ICMeetingTopicTypeLookup value: ICMeetingTopicTypeLookup.values()){
            if(checkUserRolesForICMeetingTopicByTypeAndUsername(value.getCode(), username, editing)){
                allowedCodes.add(value.getCode());
            }
        }
        return allowedCodes.isEmpty() ? null : allowedCodes;
    }

    @Override
    public ICMeetingTopicsPagedSearchResult searchICMeetingTopics(ICMeetingTopicsSearchParamsDto searchParams, String username) {
        try {
            Page<ICMeetingTopic> entitiesPage = null;
            int page = 0;
            Set<String> allowedTypes = getAllowedICMeetingTopicTypeCodesForUser(username, false);
            if (searchParams == null || searchParams.isEmpty()) {
                int pageSize = searchParams != null && searchParams.getPageSize() > 0 ? searchParams.getPageSize() : DEFAULT_PAGE_SIZE;
                page = searchParams != null && searchParams.getPage() > 0 ? searchParams.getPage() - 1 : 0;
                entitiesPage = icMeetingTopicRepository.searchAll(new PageRequest(page, pageSize, new Sort(Sort.Direction.DESC, "icMeeting.number")), allowedTypes);
            } else {
                page = searchParams.getPage() > 0 ? searchParams.getPage() - 1 : 0;

                entitiesPage = icMeetingTopicRepository.search(searchParams.getDateFromNonEmpty(), searchParams.getDateToNonEmpty(),
                        searchParams.getSearchTextLowerCase(), searchParams.getICNumberLowerCase(), searchParams.getType(),
                        new PageRequest(page, searchParams.getPageSize(), new Sort(Sort.Direction.DESC, "id")), allowedTypes);
            }

            ICMeetingTopicsPagedSearchResult result = new ICMeetingTopicsPagedSearchResult();
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
                result.setIcMeetingTopics(icMeetingTopicEntityConverter.disassembleList(entitiesPage.getContent()));
            }
            return result;
        }catch(Exception ex){
            // TODO: log search params
            logger.error("Error searching IC meeting topics", ex);
        }
        return null;
    }

    @Override
    public Set<FilesDto> saveICMeetingTopicAttachments(Long topicId, Set<FilesDto> attachments, String username) {
        try {
            Set<FilesDto> dtoSet = new HashSet<>();
            if (attachments != null) {
                Iterator<FilesDto> iterator = attachments.iterator();
                while (iterator.hasNext()) {
                    FilesDto filesDto = iterator.next();
                    if (filesDto.getId() == null) {
                        Long fileId = fileService.save(filesDto, FileTypeLookup.IC_MATERIALS.getCatalog()); // CATALOG IS ONE
                        logger.info("Saved IC meeting topic attachment files: topic id=" + topicId + ", file=" + fileId);

                        ICMeetingTopicFiles icMeetingTopicFiles = new ICMeetingTopicFiles(topicId, fileId);
                        icMeetingTopicFilesRepository.save(icMeetingTopicFiles);
                        logger.info("Saved IC meeting topic attachment information to DB: topic id=" + topicId + ", file=" + fileId + " [user]=" + username);

                        FilesDto newFileDto = new FilesDto();
                        newFileDto.setId(fileId);
                        newFileDto.setFileName(filesDto.getFileName());
                        dtoSet.add(newFileDto);
                    }
                }
            }
            return dtoSet;
        }catch (Exception ex){
            logger.error("Error saving IC meeting topic attachments: IC meeting topic id=" + topicId, ex);
        }
        return null;
    }

    @Override
    public boolean safeDeleteICMeetingTopicAttachment(Long topicId, Long fileId, String username) {
        try {
            ICMeetingTopicFiles entity = icMeetingTopicFilesRepository.getFilesByFileId(fileId);
            if (entity != null && entity.getIcMeetingTopic().getId().longValue() == topicId) {
                boolean deleted = fileService.safeDelete(fileId);
                if(!deleted){
                    logger.error("Failed to delete(safe) IC meeting topic attachment: IC topic =" + topicId + ", file=" + fileId + ", by " + username);
                }else{
                    logger.info("Deleted(safe) IC meeting topic attachment: IC topic =" + topicId + ", file=" + fileId + ", by " + username);
                }
                return deleted;
            }else{
                logger.error("Error save deleting IC meeting topic attachment: topic not found with id=" + topicId);
            }
        }catch (Exception e){
            logger.error("Failed to delete(safe) IC meeting topic attachment with error: IC topic =" + topicId + ", file=" + fileId + ", by " + username, e);
        }
        return false;
    }


    /* IC MEETING *****************************************************************************************************/
    @Override
    public EntitySaveResponseDto saveICMeeting(ICMeetingDto icMeetingDto, String updater) {
        EntitySaveResponseDto saveResponseDto = new EntitySaveResponseDto();
        try {
            ICMeeting entity = icMeetingsEntityConverter.assemble(icMeetingDto);
            if(icMeetingDto.getId() == null){ // CREATE
                EmployeeDto employee = this.employeeService.findByUsername(updater);
                // set creator
                entity.setCreator(new Employee(employee.getId()));
            }else{ // UPDATE
                // set creator
                ICMeeting currentEntity = this.icMeetingsRepository.findOne(icMeetingDto.getId());
                if(!checkEditableICMeeting(currentEntity.getId(), updater)){
                    String errorMessage = "Error saving IC Meeting with id " + currentEntity.getId() + ": entity not editable";
                    logger.error(errorMessage);
                    saveResponseDto.setErrorMessageEn(errorMessage);
                    return saveResponseDto;
                }
                Employee employee = currentEntity.getCreator();
                entity.setCreator(employee);
                // set creation date
                Date creationDate = currentEntity.getCreationDate();
                entity.setCreationDate(creationDate);
                // set update date
                entity.setUpdateDate(new Date());
                // set updater
                EmployeeDto updatedby = this.employeeService.findByUsername(updater);
                entity.setUpdater(new Employee(updatedby.getId()));
            }

            if(StringUtils.isEmpty(entity.getNumber())){
                String errorMessage = "Error saving IC Meeting: Number required";
                logger.error(errorMessage);
                saveResponseDto.setErrorMessageEn(errorMessage);
                return saveResponseDto;
            }
            if(entity.getDate() == null){
                String errorMessage = "Error saving IC Meeting: Date required";
                logger.error(errorMessage);
                saveResponseDto.setErrorMessageEn(errorMessage);
                return saveResponseDto;
            }

            List<ICMeeting> existingNumberEntities = this.icMeetingsRepository.findByNumberAndNotDeleted(entity.getNumber());
            if(existingNumberEntities != null && !existingNumberEntities.isEmpty()){
                for(ICMeeting meeting: existingNumberEntities){
                    if(entity.getId() == null || meeting.getId().longValue() != entity.getId().longValue()){
                        // exists other meeting with this number
                        String errorMessage = "Error saving IC Meeting: IC Number already exists (" + entity.getNumber() + ")";
                        logger.error(errorMessage);
                        saveResponseDto.setErrorMessageEn(errorMessage);
                        return saveResponseDto;
                    }
                }
            }
            List<ICMeeting> openMeetings = this.icMeetingsRepository.findNotClosed();

            if(openMeetings != null && !openMeetings.isEmpty()){
                boolean alreadyCounted = false;
                if(entity.getId() != null) {
                    for (ICMeeting meeting : openMeetings) {
                        if (meeting.getId().longValue() == entity.getId().longValue()) {
                            alreadyCounted = true;
                            break;
                        }
                    }
                }
                if (!alreadyCounted && (entity.getClosed() == null || !entity.getClosed().booleanValue()) &&
                        openMeetings.size() >= maxOpenEntities) {
                    String errorMessage = "Error saving IC Meeting with status OPEN: already exist max possible (" + maxOpenEntities + ") OPEN meetings";
                    logger.error(errorMessage);
                    saveResponseDto.setErrorMessageEn(errorMessage);
                    return saveResponseDto;
                }
            }

            entity = icMeetingsRepository.save(entity);
            logger.info(icMeetingDto.getId() == null ? "IC meeting created: " + entity.getId() + ", by " + entity.getCreator().getUsername() :
                    "IC meeting updated: " + entity.getId() + ", by " + updater);
            saveResponseDto.setEntityId(entity.getId());
            saveResponseDto.setStatus(ResponseStatusType.SUCCESS);
            return saveResponseDto;
        }catch (Exception ex){
            logger.error("Error saving IC meeting: " + (icMeetingDto != null && icMeetingDto.getId() != null ? icMeetingDto.getId() : "new") ,ex);
            saveResponseDto.setErrorMessageEn("Error saving IC meeting: " + (icMeetingDto != null && icMeetingDto.getId() != null ? icMeetingDto.getId() : "new"));
            return saveResponseDto;
        }
    }

    @Override
    public Set<FilesDto> saveICMeetingProtocol(Long meetingId, Set<FilesDto> attachments, String username) {
        try {
            Set<FilesDto> dtoSet = new HashSet<>();
            if (attachments != null) {
                Iterator<FilesDto> iterator = attachments.iterator();
                while (iterator.hasNext()) {
                    FilesDto filesDto = iterator.next();
                    if (filesDto.getId() == null) {
                        // Check
                        // 1. Check if protocol already exists
                        List<ICMeetingFiles> files = this.icMeetingFilesRepository.getFilesByMeetingId(meetingId);
                        if(files != null && !files.isEmpty()){
                            for(ICMeetingFiles file: files){
                                if(file.getFile() != null && (file.getFile().getDeleted()== null || !file.getFile().getDeleted().booleanValue())){
                                    logger.error("Error saving IC meeting protocol: protocol already exists for meeting with id " + meetingId);
                                    return null;
                                }
                            }
                        }
                        // 2. Check meeting is not deleted or closed
                        ICMeeting entity = this.icMeetingsRepository.findOne(meetingId);
                        if(entity == null){
                            logger.error("Error saving IC meeting protocol: no IC meeting found with id " + meetingId);
                            return null;
                        }else if(entity.getClosed() != null && entity.getClosed().booleanValue()){
                            logger.error("Error saving IC meeting protocol: meeting with id " + meetingId + " is closed");
                            return null;
                        }else if(entity.getDeleted() != null && entity.getDeleted().booleanValue()){
                            logger.error("Error saving IC meeting protocol: meeting with id " + meetingId + " is deleted");
                            return null;
                        }

                        // 3. Check decision field for topic
//                        List<ICMeetingTopic> topics = this.icMeetingTopicRepository.findByICNumberAndNotDeleted(entity.getNumber());
//                        if(topics != null && !topics.isEmpty()){
//                            for(ICMeetingTopic topic: topics){
//                                if(StringUtils.isEmpty(topic.getDecision())){
//                                    logger.error("Error saving IC meeting protocol: associated topic has empty 'DECISION' field (topic id " + topic.getId() + ")");
//                                    return null;
//                                }
//                            }
//                        }

                        Long fileId = fileService.save(filesDto, FileTypeLookup.IC_PROTOCOL.getCatalog()); // CATALOG IS ONE
                        logger.info("Saved IC meeting protocol attachment file: meeting id=" + meetingId + ", file=" + fileId + " [user]=" + username);

                        ICMeetingFiles icMeetingFiles = new ICMeetingFiles(meetingId, fileId);
                        icMeetingFilesRepository.save(icMeetingFiles);
                        logger.info("Saved IC meeting protocol attachment information to DB: meeting id=" + meetingId + ", file=" + fileId + " [user]=" + username);

                        // mark as closed
                        //ICMeeting entity = this.icMeetingsRepository.findOne(icMeetingFiles.getIcMeeting().getId());
                        entity.setClosed(true);
                        this.icMeetingsRepository.save(entity);
                        logger.info("IC MEETING PROTOCOL UPLOAD - saved IC meeting as CLOSED: meeting id=" + meetingId + ", file=" + fileId  + " [user]=" + username);


                        FilesDto newFileDto = new FilesDto();
                        newFileDto.setId(fileId);
                        newFileDto.setFileName(filesDto.getFileName());
                        dtoSet.add(newFileDto);
                    }
                }
            }
            return dtoSet;
        }catch (Exception ex){
            logger.error("Error saving IC meeting protocol ttachment: IC meeting id=" + meetingId, ex);
        }
        return null;
    }

    @Override
    public ICMeetingDto getICMeeting(Long id) {
        try {
            ICMeeting entity = icMeetingsRepository.findOne(id);
            ICMeetingDto dto = icMeetingsEntityConverter.disassemble(entity);

            return dto;
        }catch(Exception ex){
            logger.error("Error loading IC meeting: " + id, ex);
        }
        return null;
    }

    @Override
    public Set<FilesDto> getICMeetingAttachments(Long id){
        try {
            List<ICMeetingFiles> entities = icMeetingFilesRepository.getFilesByMeetingId(id);

            Set<FilesDto> files = new HashSet<>();
            if (entities != null) {
                for (ICMeetingFiles icMeetingFiles : entities) {
                    FilesDto fileDto = new FilesDto();
                    fileDto.setId(icMeetingFiles.getFile().getId());
                    fileDto.setFileName(icMeetingFiles.getFile().getFileName());
                    if(icMeetingFiles.getFile().getType() != null) {
                        fileDto.setType(icMeetingFiles.getFile().getType().getCode());
                    }
                    files.add(fileDto);
                }
            }
            return files;
        }catch(Exception ex){
            logger.error("Error getting IC meeting attachments: IC meeting id=" + id, ex);
        }
        return null;
    }

    @Override
    public boolean safeDeleteICMeeting(Long id, String username) {
        try {
            ICMeeting entity = icMeetingsRepository.findOne(id);
            if(!checkEditableICMeeting(id, username)){
                String errorMessage = "Error deleting IC Meeting with id + " + id + ": entity not editable";
                logger.error(errorMessage);
                return false;
            }
            //  check IC Meeting topics
            ICMeetingTopicsSearchParamsDto searchParamsDto = new ICMeetingTopicsSearchParamsDto();
            searchParamsDto.setIcNumber(entity.getNumber());
            ICMeetingTopicsPagedSearchResult result = searchICMeetingTopics(searchParamsDto, username);
            if(result != null && result.getIcMeetingTopics() != null && !result.getIcMeetingTopics().isEmpty()){
                return false;
            }

            entity.setDeleted(true);
            this.icMeetingsRepository.save(entity);
            logger.info("IC meeting safe deleted with id=" + id + " [user]=" + username);
            return true;
        }catch(Exception ex){
            logger.error("Error safe deleting IC meeting: id=" + id + ", ]user]=" + username, ex);
        }
        return false;
    }

    private boolean checkEditableICMeeting(Long id, String username){
        try {
            ICMeeting entity = icMeetingsRepository.findOne(id);
            if(entity != null){
                if(entity.getClosed() != null && entity.getClosed().booleanValue()){
                    return false;
                }
                if(entity.getDeleted() != null && entity.getDeleted().booleanValue()){
                    return false;
                }
                return true;
            }
        }catch(Exception ex){
            logger.error("Error checking IC meeting editable: id =" + id, ex);
        }
        return false;
    }

    @Override
    public ICMeetingsPagedSearchResult searchICMeetings(ICMeetingsSearchParamsDto searchParams) {
        try {
            Page<ICMeeting> entitiesPage = null;
            int page = 0;
            if (searchParams == null || searchParams.isEmpty()) {
                int pageSize = searchParams != null && searchParams.getPageSize() > 0 ? searchParams.getPageSize() : DEFAULT_PAGE_SIZE;
                page = searchParams != null && searchParams.getPage() > 0 ? searchParams.getPage() - 1 : 0;
                entitiesPage = icMeetingsRepository.searchAll(new PageRequest(page, pageSize));
            } else {
                page = searchParams.getPage() > 0 ? searchParams.getPage() - 1 : 0;

                entitiesPage = icMeetingsRepository.search(searchParams.getNumberNonEmpty(), searchParams.getDateFromNonEmpty(), searchParams.getDateToNonEmpty(),
                        new PageRequest(page, searchParams.getPageSize()));

            }

            ICMeetingsPagedSearchResult result = new ICMeetingsPagedSearchResult();
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
                result.setIcMeetings(icMeetingsEntityConverter.disassembleList(entitiesPage.getContent()));
                for(ICMeetingDto dto: result.getIcMeetings()){
                    // ic meeting topics
                    List<ICMeetingTopic> topic = this.icMeetingTopicRepository.findByICNumberAndNotDeleted(dto.getNumber());
                    int icTopicNumber = topic != null && !topic.isEmpty() ? topic.size() : 0;
                    dto.setNumberOfTopics(icTopicNumber);

                    // protocols
                    if(dto.getClosed() != null && dto.getClosed().booleanValue()) {
                        Set<FilesDto> files = getICMeetingAttachments(dto.getId());
                        if (!files.isEmpty()) {
                            for (FilesDto file : files) {
                                if(file.getType() != null && file.getType().equalsIgnoreCase(FileTypeLookup.IC_PROTOCOL.getCode())){
                                    dto.setProtocolFileId(file.getId());
                                    dto.setProtocolFileName(file.getFileName());
                                    break;
                                }
                            }

                        }
                    }
                }

                Collections.sort(result.getIcMeetings());
//                if(result.getIcMeetings() != null){
//                    for(ICMeetingDto dto: result.getIcMeetings()){
//                        dto.setEditable(checkEditableICMeeting(dto.getId()));
//                    }
//                }
            }
            return result;
        }catch(Exception ex){
            // TODO: log search params
            logger.error("Error searching IC meetings", ex);
        }
        return null;
    }

    @Override
    public List<ICMeetingDto> getAllICMeetings() {
        List<ICMeetingDto> dtoList = new ArrayList<>();
        Page<ICMeeting> pagedResult = this.icMeetingsRepository.searchAll(new PageRequest(0, 1000));
        if(pagedResult != null && pagedResult.getContent() != null && !pagedResult.getContent().isEmpty()){
            for(ICMeeting entity: pagedResult.getContent()){
                ICMeetingDto dto = this.icMeetingsEntityConverter.disassemble(entity);

                List<ICMeetingTopic> topic = this.icMeetingTopicRepository.findByICNumberAndNotDeleted(dto.getNumber());
                int icTopicNumber = topic != null && !topic.isEmpty() ? topic.size() : 0;
                dto.setNumberOfTopics(icTopicNumber);

                // protocol
//            if(dto.getClosed() != null && dto.getClosed().booleanValue()) {
//                Set<FilesDto> files = getICMeetingAttachments(dto.getId());
//                if (!files.isEmpty()) {
//                    for (FilesDto file : files) {
//                        if(file.getType() != null && file.getType().equalsIgnoreCase(FileTypeLookup.IC_PROTOCOL.getCode())){
//                            dto.setProtocolFileId(file.getId());
//                            dto.setProtocolFileName(file.getFileName());
//                            break;
//                        }
//                    }
//
//                }
//            }
                dtoList.add(dto);
            }
        }
        Collections.sort(dtoList);
        return dtoList;
    }

    @Override
    public boolean safeDeleteICMeetingProtocolAttachment(Long meetingId, Long fileId, String username) {
        try {
            ICMeetingFiles entity = icMeetingFilesRepository.getFilesByFileId(fileId);
            if (entity != null && entity.getIcMeeting().getId().longValue() == meetingId) {
                boolean deleted = fileService.safeDelete(fileId);
                if(!deleted){
                    logger.error("Failed to delete(safe) IC meeting attachment: IC topic =" + meetingId + ", file=" + fileId + ", by " + username);
                }else{
                    logger.info("Deleted(safe) IC meeting attachment: IC topic =" + meetingId + ", file=" + fileId + ", by " + username);

                    ICMeeting icMeeting = icMeetingsRepository.findOne(entity.getIcMeeting().getId());
                    icMeeting.setClosed(false);
                    this.icMeetingsRepository.save(icMeeting);
                    logger.info("IC MEETING PROTOCOL DELETE - saved IC meeting as NOT CLOSED: meeting id=" + meetingId + ", file=" + fileId  + " [user]=" + username);

                }
                return deleted;
            }else{
                logger.error("Error safe deleting IC meeting attachment: topic not found with id=" + meetingId);
            }
        }catch (Exception e){
            logger.error("Failed to delete(safe) IC meeting attachment with error: IC topic =" + meetingId + ", file=" + fileId + ", by " + username, e);
        }
        return false;
    }

}
