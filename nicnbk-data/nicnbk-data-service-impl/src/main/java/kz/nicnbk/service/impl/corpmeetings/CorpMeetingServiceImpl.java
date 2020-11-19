package kz.nicnbk.service.impl.corpmeetings;

import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.common.service.util.DateUtils;
import kz.nicnbk.common.service.util.MathUtils;
import kz.nicnbk.common.service.util.PaginationUtils;
import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.repo.api.corpmeetings.*;
import kz.nicnbk.repo.model.corpmeetings.*;
import kz.nicnbk.repo.model.employee.Department;
import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.repo.model.files.Files;
import kz.nicnbk.repo.model.lookup.FileTypeLookup;
import kz.nicnbk.repo.model.lookup.corpmeetings.ICMeetingTopicTypeLookup;
import kz.nicnbk.service.api.corpmeetings.CorpMeetingService;
import kz.nicnbk.service.api.employee.EmployeeService;
import kz.nicnbk.service.api.files.FileService;
import kz.nicnbk.service.converter.corpmeetings.ICMeetingTopicEntityConverter;
import kz.nicnbk.service.converter.corpmeetings.ICMeetingsEntityConverter;
import kz.nicnbk.service.datamanager.LookupService;
import kz.nicnbk.service.dto.authentication.UserRoles;
import kz.nicnbk.service.dto.common.EmployeeApproveDto;
import kz.nicnbk.service.dto.common.EntitySaveResponseDto;
import kz.nicnbk.service.dto.common.ResponseStatusType;
import kz.nicnbk.service.dto.corpmeetings.*;
import kz.nicnbk.service.dto.employee.EmployeeDto;
import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.files.NamedFilesDto;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
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

import java.io.*;
import java.math.BigInteger;
import java.util.*;


@Service
public class CorpMeetingServiceImpl implements CorpMeetingService {

    private static final Logger logger = LoggerFactory.getLogger(CorpMeetingServiceImpl.class);

    private static final String FONT_ARIAL ="Arial";
    private static final int FONT_SIZE = 11;
    public static final String IC_PROTOCOL_ATTENDEE_PLACEHOLDER = "ATTENDEE";
    public static final String IC_PROTOCOL_ATTENDEE_START_PLACEHOLDER = "Состав Инвестиционного комитета";
    public static final String IC_PROTOCOL_ABSENTEE_PLACEHOLDER = "ABSENTEE";
    public static final String IC_PROTOCOL_ABSENTEE_START_PLACEHOLDER = "Отсутствовали";
    public static final String IC_PROTOCOL_INVITEE_PLACEHOLDER = "INVITEE";
    public static final String IC_PROTOCOL_INVITEE_START_PLACEHOLDER = "Приглашены";
    public static final String IC_PROTOCOL_QUESTION_PLACEHOLDER = "QUESTION";
    public static final String IC_PROTOCOL_QUESTION_START_PLACEHOLDER = "Перечень вопросов";
    public static final String IC_PROTOCOL_QUESTION_MATERIAL_HEADER_PLACEHOLDER = "MATERIALS";//"Перечень документов, предоставленных по вопросу";
    public static final String IC_PROTOCOL_QUESTION_MATERIAL_PLACEHOLDER = "MATERIAL";
    public static final String IC_PROTOCOL_QUESTION_DISCUSSION_PLACEHOLDER = "DISCUSSION";
    public static final String IC_PROTOCOL_DECISION_PLACEHOLDER = "DECISION";
    public static final String IC_PROTOCOL_VOTING_PLACEHOLDER = "VOTING";

    public static final String IC_BULLETIN_DECISION_PLACEHOLDER = "DECISION";
    public static final String IC_BULLETIN_NAME_PLACEHOLDER = "NAME";


    /* Root folder on the server */
    @Value("${filestorage.root.directory}")
    private String rootDirectory;

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
    private ICMeetingTopicApprovalRepository icMeetingTopicApprovalRepository;

    @Autowired
    private ICMeetingTopicEntityConverter icMeetingTopicEntityConverter;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private ICMeetingsRepository icMeetingsRepository;

    @Autowired
    private ICMeetingVoteRepository icMeetingVoteRepository;

    @Autowired
    private ICMeetingAttendeesRepository icMeetingAttendeesRepository;

    @Autowired
    private ICMeetingInviteesRepository icMeetingInviteesRepository;

    @Autowired
    private ICMeetingsEntityConverter icMeetingsEntityConverter;

    @Autowired
    private LookupService lookupService;


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
    public EntitySaveResponseDto saveICMeetingTopic(ICMeetingTopicDto dto, FilesDto explanatoryNote,
                                                    List<FilesDto> filesDtoSet, String updater) {
        try {
            EntitySaveResponseDto saveResponseDto = new EntitySaveResponseDto();
            if(StringUtils.isEmpty(dto.getName())){
                String errorMessage = "Error saving IC Meeting Topic: name required";
                logger.error(errorMessage);
                saveResponseDto.setErrorMessageEn(errorMessage);
                return saveResponseDto;
            }
            ICMeetingTopic entity = icMeetingTopicEntityConverter.assemble(dto);
            boolean resetApprovals = false;
            if(dto.getId() == null){ // CREATE
                EmployeeDto employee = this.employeeService.findByUsername(updater);
                // set creator
                entity.setCreator(new Employee(employee.getId()));
                entity.setCreationDate(new Date());
                if(employee.getPosition() != null && employee.getPosition().getDepartment() != null) {
                    entity.setDepartment(new Department(employee.getPosition().getDepartment().getId()));
                }else{
                    String errorMessage = "Error saving IC Meeting Topic: creator must have department specified";
                    logger.error(errorMessage);
                    saveResponseDto.setErrorMessageEn(errorMessage);
                    return saveResponseDto;
                }
            }else{ // UPDATE
                if(!checkEditableICMeetingTopicByTopicIdAndUsername(dto.getId(), updater)){
                    String errorMessage = "Error saving IC Meeting Topic with id " + dto.getId() + ": entity not editable";
                    logger.error(errorMessage);
                    saveResponseDto.setErrorMessageEn(errorMessage);
                    return saveResponseDto;
                }
                ICMeetingTopic currentEntity = this.icMeetingTopicRepository.findOne(dto.getId());
                boolean icMeetingChanged = entity.getIcMeeting() != null && currentEntity.getIcMeeting() == null;
                icMeetingChanged = icMeetingChanged || entity.getIcMeeting() == null && currentEntity.getIcMeeting() != null;
                if(!icMeetingChanged && entity.getIcMeeting() != null && currentEntity.getIcMeeting() != null){
                    icMeetingChanged = entity.getIcMeeting().getId().longValue() != currentEntity.getIcMeeting().getId().longValue();
                }
                if(icMeetingChanged){
                    entity.setIcOrder(null);
                }
                // set update date
                entity.setUpdateDate(new Date());
                // set updater
                EmployeeDto updatedby = this.employeeService.findByUsername(updater);
                entity.setUpdater(new Employee(updatedby.getId()));

                if(!dto.isToPublish() && currentEntity.getPublished() != null && currentEntity.getPublished().booleanValue()){
                    resetApprovals = true;
                }
            }

            // Explanatory notes
            if(explanatoryNote != null){
                if(entity.getExplanatoryNote() != null){
                    String errorMessage = "Error saving IC meeting topic: explanatory note exists, please delete the current file before uploading new one.";
                    logger.error(errorMessage);
                    saveResponseDto = new EntitySaveResponseDto();
                    saveResponseDto.setErrorMessageEn(errorMessage);
                    return saveResponseDto;
                }
                Long fileId = fileService.save(explanatoryNote, FileTypeLookup.IC_EXPLANATORY_NOTE.getCatalog());
                entity.setExplanatoryNote(new Files(fileId));
                // RESET APPROVALS
                if(entity.getId() != null) {
                    resetApprovals = true;
                }
            }

            if(dto.isToPublish()){
                if(entity.getIcMeeting() == null){
                    String errorMessage = "Error saving IC meeting topic: cannot publish topic without IC Meeting.";
                    logger.error(errorMessage);
                    saveResponseDto = new EntitySaveResponseDto();
                    saveResponseDto.setErrorMessageEn(errorMessage);
                    return saveResponseDto;
                }
                entity.setPublished(true);
            }else{
                if(entity.getIcMeeting() != null){
                    String errorMessage = "Error saving IC meeting topic: cannot  hide(un-publish) topic with IC Meeting selected.";
                    logger.error(errorMessage);
                    saveResponseDto = new EntitySaveResponseDto();
                    saveResponseDto.setErrorMessageEn(errorMessage);
                    return saveResponseDto;
                }
                entity.setPublished(false);
                resetApprovals = true;
            }

            if(entity.getIcMeeting() != null){
                ICMeetingDto icMeetingDto = getICMeeting(entity.getIcMeeting().getId(), updater);
                if(!checkEditableICMeeting(icMeetingDto, updater)){
                    String errorMessage = "Error saving IC meeting topic: IC Meeting is not editable.";
                    logger.error(errorMessage);
                    saveResponseDto = new EntitySaveResponseDto();
                    saveResponseDto.setErrorMessageEn(errorMessage);
                    return saveResponseDto;
                }
            }
            // RESET APPROVAL: Check name or decision change
            if(!resetApprovals && dto.getId() != null) {
                ICMeetingTopic existingEntity = this.icMeetingTopicRepository.findOne(dto.getId());
                if(existingEntity != null){
                    String existingDecision = existingEntity.getDecision() != null ? existingEntity.getDecision() : "";
                    String currentDecision = dto.getDecision() != null ? dto.getDecision() : "";
                    if(!StringUtils.isEqualWithoutSpaces(existingDecision, currentDecision)){
                        resetApprovals = true;
                    }else {
                        String existingName = existingEntity.getName() != null ? existingEntity.getName() : "";
                        String currentName = dto.getName() != null ? dto.getName() : "";
                        if (!StringUtils.isEqualWithoutSpaces(existingName, currentName)) {
                            resetApprovals = true;
                        }
                    }
                }
            }
            entity = icMeetingTopicRepository.save(entity);
            logger.info(dto.getId() == null ? "IC meeting topic created: " + entity.getId() + ", by " + entity.getCreator().getUsername() :
                    "IC meeting topic updated: " + entity.getId() + ", by " + updater);

            // Materials
            List<NamedFilesDto> existingMaterials = new ArrayList<>();
            if(dto.getUploadMaterials() != null && !dto.getUploadMaterials().isEmpty() && filesDtoSet != null &&
                    !filesDtoSet.isEmpty()){
                List<NamedFilesDto> uploadMaterials = new ArrayList<>();
                for(NamedFilesDto namedFilesDto: dto.getUploadMaterials()){
                    boolean newFile = false;
                    for(FilesDto filesDto: filesDtoSet){
                        if(namedFilesDto.getFile().getFileName().equals(filesDto.getFileName())){
                            NamedFilesDto uploadMaterial = new NamedFilesDto(filesDto, namedFilesDto.getName());
                            uploadMaterials.add(uploadMaterial);
                            newFile = true;
                        }
                    }
                    if(!newFile){
                        existingMaterials.add(namedFilesDto);
                    }
                }
                saveICMeetingTopicAttachments(entity.getId(), uploadMaterials, false, updater);
                // RESET APPROVALS
                resetApprovals = true;
            }
            // uploaded/existing files renaming
            saveICMeetingTopicAttachments(entity.getId(),
                    (filesDtoSet != null && !filesDtoSet.isEmpty() ? existingMaterials : dto.getMaterials()), false, updater);

            if(resetApprovals){
                resetICMeetingTopicApprovals(entity.getId());
            }

            // TODO: error when updating? Transaction?

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

    /* IC MEETING TOPIC ***********************************************************************************************/
    @Override
    public EntitySaveResponseDto saveICMeetingTopicUpdate(ICMeetingTopicUpdateDto dto, FilesDto explanatoryNoteUpd,
                                                          List<FilesDto> materialsUpd, String updater) {
        try {
            EntitySaveResponseDto saveResponseDto = new EntitySaveResponseDto();
            if(StringUtils.isEmpty(dto.getNameUpd())){ // Name
                String errorMessage = "Error saving IC Meeting Topic update: name required";
                logger.error(errorMessage);
                saveResponseDto.setErrorMessageEn(errorMessage);
                return saveResponseDto;
            }
            if(StringUtils.isEmpty(dto.getDecisionUpd())){ // Decision
                String errorMessage = "Error saving IC Meeting Topic update: decision required";
                logger.error(errorMessage);
                saveResponseDto.setErrorMessageEn(errorMessage);
                return saveResponseDto;
            }
            if(dto.getId() == null){
                String errorMessage = "Error saving IC Meeting Topic update: need to pass IC Meeting topic id";
                logger.error(errorMessage);
                saveResponseDto.setErrorMessageEn(errorMessage);
                return saveResponseDto;
            }
            boolean resetVoting = false;
            ICMeetingTopic entity = this.icMeetingTopicRepository.findOne(dto.getId());
            if(entity != null){

                if(!checkEditableICMeetingTopicUpdateByTopicIdAndUsername(dto.getId(), updater)){
                    String errorMessage = "Error saving IC Meeting Topic update: entity is not editable";
                    logger.error(errorMessage);
                    saveResponseDto.setErrorMessageEn(errorMessage);
                    return saveResponseDto;
                }

                if(entity.getIcMeeting() == null){
                    String errorMessage = "Error saving IC Meeting Topic update: IC Meeting is not specified";
                    logger.error(errorMessage);
                    saveResponseDto.setErrorMessageEn(errorMessage);
                    return saveResponseDto;
                }
                if(isICMeetingLockedByDeadline(entity.getIcMeeting())){
                    if(entity.getIcMeeting().getUnlockedForFinalize() == null || !entity.getIcMeeting().getUnlockedForFinalize().booleanValue()){
                        String errorMessage = "Error saving IC Meeting Topic update: IC Meeting is locked by deadline";
                        logger.error(errorMessage);
                        saveResponseDto.setErrorMessageEn(errorMessage);
                        return saveResponseDto;
                    }
                }else{
                    String errorMessage = "Error saving IC Meeting Topic update: IC Meeting deadline has not passed yet";
                    logger.error(errorMessage);
                    saveResponseDto.setErrorMessageEn(errorMessage);
                    return saveResponseDto;
                }

            }else{
                String errorMessage = "Error saving IC Meeting Topic update: IC Meeting topic not found with id: " + dto.getId().longValue();
                logger.error(errorMessage);
                saveResponseDto.setErrorMessageEn(errorMessage);
                return saveResponseDto;
            }

            // Explanatory notes
            if(explanatoryNoteUpd != null){
                if(entity.getExplanatoryNoteUpd() != null){
                    String errorMessage = "Error saving IC meeting topic update: explanatory note exists, " +
                            "please delete the current file before uploading new one.";
                    logger.error(errorMessage);
                    saveResponseDto = new EntitySaveResponseDto();
                    saveResponseDto.setErrorMessageEn(errorMessage);
                    return saveResponseDto;
                }
                Long fileId = fileService.save(explanatoryNoteUpd, FileTypeLookup.IC_EXPLANATORY_NOTE.getCatalog());
                entity.setExplanatoryNoteUpd(new Files(fileId));
                resetVoting = true;
            }
            // RESET APPROVAL: Check decision or name change
            if(!resetVoting && dto.getId() != null) {
                ICMeetingTopic existingEntity = this.icMeetingTopicRepository.findOne(dto.getId());
                if(existingEntity != null){
                    String existingDecision = existingEntity.getDecisionUpd() != null ? existingEntity.getDecisionUpd() : "";
                    String currentDecision = dto.getDecisionUpd() != null ? dto.getDecisionUpd() : "";
                    if(!StringUtils.isEqualWithoutSpaces(existingDecision, currentDecision)){
                        resetVoting = true;
                    }else {
                        String existingName = existingEntity.getNameUpd() != null ? existingEntity.getNameUpd() : "";
                        String currentName = dto.getNameUpd() != null ? dto.getNameUpd() : "";
                        if (!StringUtils.isEqualWithoutSpaces(existingName, currentName)) {
                            resetVoting = true;
                        }
                    }
                }
            }
            // set update date
            entity.setUpdateDate(new Date());
            // set updater
            EmployeeDto updatedby = this.employeeService.findByUsername(updater);
            entity.setUpdater(new Employee(updatedby.getId()));

            entity.setNameUpd(dto.getNameUpd());
            entity.setDecisionUpd(dto.getDecisionUpd());
            entity.setPublishedUpd(dto.getPublishedUpd());

            entity = icMeetingTopicRepository.save(entity);
            logger.info("IC meeting topic update saved: topic id=" + entity.getId().longValue() + ", by " + updater);

            // Materials
            List<NamedFilesDto> existingMaterials = new ArrayList<>();
            if(dto.getUploadMaterialsUpd() != null && !dto.getUploadMaterialsUpd().isEmpty() && materialsUpd != null &&
                    !materialsUpd.isEmpty()){
                List<NamedFilesDto> uploadMaterials = new ArrayList<>();
                for(NamedFilesDto namedFilesDto: dto.getUploadMaterialsUpd()){
                    boolean newFile = false;
                    for(FilesDto filesDto: materialsUpd){
                        if(namedFilesDto.getFile().getFileName().equals(filesDto.getFileName())){
                            NamedFilesDto uploadMaterial = new NamedFilesDto(filesDto, namedFilesDto.getName());
                            uploadMaterials.add(uploadMaterial);
                            newFile = true;
                        }
                    }
                    if(!newFile){
                        existingMaterials.add(namedFilesDto);
                    }
                }
                saveICMeetingTopicAttachments(entity.getId(), uploadMaterials, true, updater);
                resetVoting = true;
            }

            saveICMeetingTopicAttachments(entity.getId(),
                    (materialsUpd != null && !materialsUpd.isEmpty() ? existingMaterials : dto.getUploadMaterialsUpd()), true, updater); // uploaded files renaming

            if(resetVoting){
                resetICMeetingTopicVoting(entity.getId());
            }

            // TODO: error when updating? Transaction?

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

    private boolean isICMeetingLockedByDeadline(ICMeeting icMeeting){
        // check date
        if(icMeeting.getDate() != null){
            Date icDate = DateUtils.getDateWithTime(icMeeting.getDate(), (icMeeting.getTime() !=null ? icMeeting.getTime() : "00:00"));
            if(icDate != null) {
                Date deadLine = DateUtils.moveDateByHours(icDate, -CorpMeetingService.IC_MEETING_DEADLINE_DAYS, true);
                try {
                    if (deadLine.before(new Date())) {
                        // cannot edit
                        return true;
                    }
                } catch (NumberFormatException ex) {
                }
            }
        }
        return false;
    }

    private void resetICMeetingTopicApprovals(Long icMeetingTopicId){
        List<ICMeetingTopicApproval> approvals = this.icMeetingTopicApprovalRepository.findByIcMeetingTopicId(icMeetingTopicId);
        if(approvals != null && !approvals.isEmpty()){
            for(ICMeetingTopicApproval approval: approvals){
                approval.setApproved(false);
            }
            this.icMeetingTopicApprovalRepository.save(approvals);
        }
    }

    private void resetICMeetingTopicVoting(Long icMeetingTopicId){
        this.icMeetingVoteRepository.deleteByTopicId(icMeetingTopicId);
    }

    @Deprecated
    private void saveApproveList(Long icMeetingTopicId, Set<EmployeeApproveDto> approveList){
        if (approveList != null && !approveList.isEmpty()) {
            List<ICMeetingTopicApproval> updatedApprovals = new ArrayList<>();
            List<ICMeetingTopicApproval> existingApprovals = this.icMeetingTopicApprovalRepository.findByIcMeetingTopicId(icMeetingTopicId);
            if (existingApprovals != null && !existingApprovals.isEmpty()) {
                for (EmployeeApproveDto anApproval : approveList) {
                    if (anApproval.getEmployee() == null || anApproval.getEmployee().getId() == null) {
                        continue;
                    }
                    boolean found = false;
                    for (ICMeetingTopicApproval existingApproval : existingApprovals) {
                        if (existingApproval.getEmployee().getId().longValue() == anApproval.getEmployee().getId().longValue()) {
                            found = true;
                            updatedApprovals.add(existingApproval);
                            break;
                        }
                    }
                    if (!found) {
                        // add new
                        updatedApprovals.add(new ICMeetingTopicApproval(icMeetingTopicId,
                                anApproval.getEmployee().getId(), false));
                    }
                }
            } else {
                // no existing, save all new
                for (EmployeeApproveDto anApproval : approveList) {
                    if (anApproval.getEmployee() != null && anApproval.getEmployee().getId() != null) {
                        updatedApprovals.add(new ICMeetingTopicApproval(icMeetingTopicId,
                                anApproval.getEmployee().getId(), false));
                    }
                }
            }
            this.icMeetingTopicApprovalRepository.save(updatedApprovals);
        } else {
            this.icMeetingTopicApprovalRepository.deleteByICMeetingTopicId(icMeetingTopicId);
        }
    }

    @Override
    public ICMeetingTopicDto getICMeetingTopic(Long id, String username) {
        try {
            if(!checkViewICMeetingTopicByTopicIdAndUsername(id, username)){
                return null;
            }
            ICMeetingTopic entity = icMeetingTopicRepository.findOne(id);
            ICMeetingTopicDto dto = icMeetingTopicEntityConverter.disassemble(entity);

            // set files
            dto.setMaterials(getICMeetingTopicAttachments(id, false));

            dto.setMaterialsUpd(getICMeetingTopicAttachments(id, true));

            // set approvals
            List<ICMeetingTopicApproval> approvals = this.icMeetingTopicApprovalRepository.findByIcMeetingTopicId(id);
            if(approvals != null && !approvals.isEmpty()){
                Set<EmployeeApproveDto> approveList = new HashSet<>();
                for(ICMeetingTopicApproval anApproval: approvals){
                    EmployeeDto employeeDto = this.employeeService.getEmployeeById(anApproval.getEmployee().getId());
                    approveList.add(new EmployeeApproveDto(employeeDto, anApproval.getApproved()));
                }
                dto.setApproveList(approveList);
            }
            return dto;
        }catch(Exception ex){
            logger.error("Error loading corp meeting: " + id, ex);
        }
        return null;
    }

    @Override
    public ICMeetingTopicDto getICMeetingTopicByExplanatoryFileId(Long fileId) {
        ICMeetingTopic icMeetingTopic = this.icMeetingTopicRepository.findByExplanatoryNoteIdNotDeleted(fileId);
        if(icMeetingTopic != null){
            return  icMeetingTopicEntityConverter.disassemble(icMeetingTopic);
        }
        return null;
    }

    @Override
    public ICMeetingTopicDto getICMeetingTopicByMaterialFileId(Long fileId) {
        ICMeetingTopicFiles materialFiles = this.icMeetingTopicFilesRepository.getFilesByFileId(fileId);
        if(materialFiles != null && materialFiles.getIcMeetingTopic() != null){
            return this.icMeetingTopicEntityConverter.disassemble(materialFiles.getIcMeetingTopic());
        }
        return null;
    }

    @Override
    public List<NamedFilesDto> getICMeetingTopicAttachments(Long id, Boolean update){
        try {
            List<ICMeetingTopicFiles> entities = icMeetingTopicFilesRepository.getFilesByMeetingId(id);

            List<NamedFilesDto> files = new ArrayList<>();
            if (entities != null) {
                for (ICMeetingTopicFiles icMeetingTopicFiles : entities) {
                    if(update == null || icMeetingTopicFiles.isUpdate() == update) {
                        FilesDto fileDto = new FilesDto();
                        fileDto.setId(icMeetingTopicFiles.getFile().getId());
                        fileDto.setFileName(icMeetingTopicFiles.getFile().getFileName());
                        NamedFilesDto namedFilesDto = new NamedFilesDto();
                        namedFilesDto.setFile(fileDto);
                        namedFilesDto.setName(icMeetingTopicFiles.getCustomName());
                        files.add(namedFilesDto);
                    }
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
            if(entity != null) {
                if (!checkDeletableICMeetingTopicByIdAndUserName(entity, username)) {
                    String errorMessage = "Error safe deleting IC Meeting topic with id + " + id + ": entity not editable";
                    logger.error(errorMessage);
                    return false;
                }
                entity.setDeleted(true);
                this.icMeetingTopicRepository.save(entity);
                logger.info("IC meeting topic safe deleted with id=" + id + " [user]=" + username);
                return true;
            }
        }catch(Exception ex){
            logger.error("Error safe deleting IC meeting topic: id=" + id + ", ]user]=" + username, ex);
        }
        return false;
    }

    private boolean checkDeletableICMeetingTopicByIdAndUserName(ICMeetingTopic entity, String username){
        if(entity.getIcMeeting() == null){
            return true;
        }
        if(entity.getIcMeeting().getClosed() != null && entity.getIcMeeting().getClosed().booleanValue()){
            return false;
        }
        if(entity.getIcMeeting().getDeleted() != null && entity.getIcMeeting().getDeleted().booleanValue()){
            return false;
        }
        if(isICMeetingLockedByDeadline(entity.getIcMeeting())){
            return false;
        }
        return true;
    }

    @Override
    public boolean checkViewICMeetingTopicByTopicIdAndUsername(Long id, String username){
        if(id == null || id.longValue() == 0){
            return false;
        }
        try {
            EmployeeDto editor = this.employeeService.findByUsername(username);
            if(editor == null){
                return false;
            }
            if(editor.getRoles() != null && !editor.getRoles().isEmpty()){
                for(BaseDictionaryDto role: editor.getRoles()){
                    if(role.getCode().equalsIgnoreCase(UserRoles.ADMIN.getCode()) ||
                            role.getCode().equalsIgnoreCase(UserRoles.IC_ADMIN.getCode()) ||
                            role.getCode().equalsIgnoreCase(UserRoles.IC_TOPIC_VIEW_ALL.getCode())){
                        return true;
                    }
                }
            }
            ICMeetingTopic entity = icMeetingTopicRepository.findOne(id);
            if(entity != null){
                // Check same dept roles
                int creatorDeptId = entity.getDepartment() != null ? entity.getDepartment().getId() : 0;
                int editorDeptId = editor != null && editor.getPosition() != null &&
                        editor.getPosition().getDepartment() != null ? editor.getPosition().getDepartment().getId() : 0;

                if(creatorDeptId != 0 && creatorDeptId == editorDeptId){
                    return true;
                }
                // check approvals
                if(entity.getApproveList() != null){
                    for(ICMeetingTopicApproval approval: entity.getApproveList()){
                        if(approval.getEmployee().getId().longValue() == editor.getId().longValue()){
                            // from approve list
                            return true;
                        }
                    }
                }
            }
        }catch(Exception ex){
            logger.error("Error checking IC meeting topic editable: id =" + id, ex);
        }
        return false;
    }

    private boolean checkEditableICMeetingTopicByTopicIdAndUsername(Long id, String username){
        if(id == null || id.longValue() == 0){
            // new topic
            return true;
        }
        try {
            ICMeetingTopicDto topicDto = getICMeetingTopic(id, username);
            if(topicDto != null){
                if(topicDto.getIcMeeting() != null && topicDto.getIcMeeting().getClosed() != null &&
                        topicDto.getIcMeeting().getClosed().booleanValue()){
                    return false;
                }
                if(topicDto.getIcMeeting() != null && topicDto.getIcMeeting().getDeleted() != null &&
                        topicDto.getIcMeeting().getDeleted().booleanValue()){
                    return false;
                }
                if(topicDto.getDeleted() != null && topicDto.getDeleted().booleanValue()){
                    return false;
                }
                EmployeeDto editor = this.employeeService.findByUsername(username);
                if(editor.getRoles() != null && !editor.getRoles().isEmpty()){
                    for(BaseDictionaryDto role: editor.getRoles()){
                        if(role.getCode().equalsIgnoreCase(UserRoles.ADMIN.getCode()) ||
                                role.getCode().equalsIgnoreCase(UserRoles.IC_ADMIN.getCode())){
                            return true;
                        }
                    }
                }
                if (topicDto.getIcMeeting() != null && topicDto.getIcMeeting().isLockedByDeadline()) {
                    // when IC Meeting is unlocked for finalize, save topic UPDATE is called
                    return false;
                }
                // Check same dept
                int creatorDeptId = topicDto.getDepartment() != null ? topicDto.getDepartment().getId() : 0;
                int editorDeptId = editor != null && editor.getPosition() != null &&
                        editor.getPosition().getDepartment() != null ? editor.getPosition().getDepartment().getId() : 0;

                if(creatorDeptId != 0 && creatorDeptId == editorDeptId){
                    return true;
                }
            }
        }catch(Exception ex){
            logger.error("Error checking IC meeting topic editable: id =" + id, ex);
        }
        return false;
    }

    private boolean checkEditableICMeetingTopicUpdateByTopicIdAndUsername(Long id, String username){
        if(id == null || id.longValue() == 0){
            // new topic
            return true;
        }
        try {
            ICMeetingTopicDto topicDto = getICMeetingTopic(id, username);
            if (topicDto.getIcMeeting() == null) {
                return false;
            }
            if(topicDto != null){
                if(topicDto.getIcMeeting() != null && topicDto.getIcMeeting().getClosed() != null &&
                        topicDto.getIcMeeting().getClosed().booleanValue()){
                    return false;
                }
                if(topicDto.getIcMeeting() != null && topicDto.getIcMeeting().getDeleted() != null &&
                        topicDto.getIcMeeting().getDeleted().booleanValue()){
                    return false;
                }
                if(topicDto.getDeleted() != null && topicDto.getDeleted().booleanValue()){
                    return false;
                }
                EmployeeDto editor = this.employeeService.findByUsername(username);
                if(editor.getRoles() != null && !editor.getRoles().isEmpty()){
                    for(BaseDictionaryDto role: editor.getRoles()){
                        if(role.getCode().equalsIgnoreCase(UserRoles.ADMIN.getCode()) ||
                                role.getCode().equalsIgnoreCase(UserRoles.IC_ADMIN.getCode())){
                            return true;
                        }
                    }
                }
                if (!topicDto.getIcMeeting().isLockedByDeadline()) {
                    // update only after IC lock by deadline
                    return false;
                }else if(topicDto.getIcMeeting().getUnlockedForFinalize() == null || !topicDto.getIcMeeting().getUnlockedForFinalize().booleanValue()){
                    // IC Meeting must be unlocked for update
                    return false;
                }
                // Check same dept
                int creatorDeptId = topicDto.getDepartment() != null ? topicDto.getDepartment().getId() : 0;
                int editorDeptId = editor != null && editor.getPosition() != null &&
                        editor.getPosition().getDepartment() != null ? editor.getPosition().getDepartment().getId() : 0;

                if(creatorDeptId != 0 && creatorDeptId == editorDeptId){
                    return true;
                }
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
    public ICMeetingTopicsPagedSearchResult searchICMeetingTopics(ICMeetingTopicsSearchParamsDto searchParams) {
        try {
            Page<ICMeetingTopic> entitiesPage = null;
            int page = 0;

            // Check if allowed topic by department id
            Integer departmentId = null; // all topics
            boolean viewICTopicAll = false;
            EmployeeDto employee = this.employeeService.findByUsername(searchParams.getUsername());
            if(searchParams != null && searchParams.getUsername() != null) {
                departmentId = employee.getPosition() != null && employee.getPosition().getDepartment() != null ?
                        employee.getPosition().getDepartment().getId() : -1; // -1 means dept not specified
                if (employee.getRoles() != null && !employee.getRoles().isEmpty()) {
                    for (BaseDictionaryDto role : employee.getRoles()) {
                        if (role.getCode().equalsIgnoreCase(UserRoles.IC_ADMIN.getCode()) ||
                                role.getCode().equalsIgnoreCase(UserRoles.ADMIN.getCode())) {
                            // IC ADMIN
                            departmentId = null;
                            viewICTopicAll = true;
                        }else if (role.getCode().equalsIgnoreCase(UserRoles.IC_MEMBER.getCode()) ||
                                role.getCode().equalsIgnoreCase(UserRoles.IC_TOPIC_VIEW_ALL.getCode())) {
                            // IC MEMBER
                            viewICTopicAll = true;
                        }
                    }
                }
            }

            if (searchParams == null || searchParams.isEmpty()) {
                int pageSize = searchParams != null && searchParams.getPageSize() > 0 ? searchParams.getPageSize() : DEFAULT_PAGE_SIZE;
                page = searchParams != null && searchParams.getPage() > 0 ? searchParams.getPage() - 1 : 0;
                entitiesPage = icMeetingTopicRepository.searchAllByDepartmentAndUserNonDeleted(departmentId, viewICTopicAll, new PageRequest(page, pageSize, new Sort(Sort.Direction.DESC, "id")));
            } else {
                page = searchParams.getPage() > 0 ? searchParams.getPage() - 1 : 0;

                entitiesPage = icMeetingTopicRepository.searchNonDeleted(departmentId, viewICTopicAll,
                        searchParams.getDateFromNonEmpty(), searchParams.getDateToNonEmpty(),
                        searchParams.getSearchTextLowerCase(), searchParams.getICNumberLowerCase(),
                        new PageRequest(page, searchParams.getPageSize(), new Sort(Sort.Direction.DESC, "id")));
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
    public EntitySaveResponseDto approveICMeetingTopic(Long id, String username){
        EntitySaveResponseDto responseDto = new EntitySaveResponseDto();
        EmployeeDto employeeDto = this.employeeService.findByUsername(username);

        if(employeeDto != null){
            if(!checkApprovableICMeetingTopicByTopicIdAndUsername(id, username)){
                String errorMessage = "Error approving IC Meeting Topic: forbidden. ";
                logger.error(errorMessage + "Topic id=" + id.longValue() + ", username=[" + username + "]");
                responseDto.setErrorMessageEn(errorMessage);
                return responseDto;
            }

            // TODO: check is published?

            ICMeetingTopicDto icMeetingTopic = getICMeetingTopic(id, username);
            if(icMeetingTopic.getApproveList() != null){
                for(EmployeeApproveDto approval: icMeetingTopic.getApproveList()){
                    if(approval.getEmployee() != null && approval.getEmployee().getId().longValue() ==
                            employeeDto.getId().longValue()){
                        //approve
                        ICMeetingTopicApproval approvalEntity =
                                this.icMeetingTopicApprovalRepository.findByIcMeetingTopicIdAndEmployeeId(id, employeeDto.getId());
                        if(approvalEntity != null){
                            approvalEntity.setApproved(true);
                            this.icMeetingTopicApprovalRepository.save(approvalEntity);
                            responseDto.setStatus(ResponseStatusType.SUCCESS);
                            return responseDto;
                        }else{
                            String errorMessage = "Error approving IC Meeting Topic: employee not found in approval list. ";
                            logger.error(errorMessage + "Topic id=" + id.longValue() + ", username=[" + username + "]");
                            responseDto.setErrorMessageEn(errorMessage);
                            return responseDto;
                        }
                    }
                }
            }

        }else{
            responseDto.setErrorMessageEn("Failed to approve IC Meeting Topic: user not found '" + username + "'");
            return responseDto;
        }
        String errorMessage = "Error approving IC Meeting Topic: employee not found in approval list. ";
        logger.error(errorMessage + "Topic id=" + id.longValue() + ", username=[" + username + "]");
        responseDto.setErrorMessageEn(errorMessage);
        return responseDto;
    }

    @Override
    public List<EmployeeDto> getAvailableApproveList(){
        String[] roles = {UserRoles.IC_TOPIC_RESTR.getCode(), UserRoles.IC_MEMBER.getCode()};
        return this.employeeService.findEmployeesByRoleCodes(roles);
    }

    @Override
    public List<ICMeetingTopicDto> getICMeetingTopicsByMeetingId(Long id, String username){

        // TODO: check access roles by topic department????
        Integer departmentId = null; // all topics
        boolean isICMember = false;
        EmployeeDto employee = this.employeeService.findByUsername(username);
        if(employee != null) {
            departmentId = employee.getPosition() != null && employee.getPosition().getDepartment() != null ?
                    employee.getPosition().getDepartment().getId() : -1; // -1 means dept not specified
            if (employee.getRoles() != null && !employee.getRoles().isEmpty()) {
                for (BaseDictionaryDto role : employee.getRoles()) {
                    if (role.getCode().equalsIgnoreCase(UserRoles.IC_ADMIN.getCode()) ||
                            role.getCode().equalsIgnoreCase(UserRoles.ADMIN.getCode())) {
                        // IC ADMIN
                        departmentId = null;
                    }else if (role.getCode().equalsIgnoreCase(UserRoles.IC_MEMBER.getCode())) {
                        // IC MEMBER
                        isICMember = true;
                    }
                }
            }
        }

        List<ICMeetingTopic> entities = this.icMeetingTopicRepository.findByICMeetingIdAndUserNotDeleted(id, departmentId, isICMember);
        if(entities != null) {
            List<ICMeetingTopicDto> topics = this.icMeetingTopicEntityConverter.disassembleList(entities);
            return topics;
        }
        return null;
    }

    @Override
    public List<ICMeetingTopicDto> getLimitedICMeetingTopicsByMeetingId(Long id){
        List<ICMeetingTopic> entities = icMeetingTopicRepository.findByICMeetingIdNotDeleted(id);
        if(entities != null) {
            List<ICMeetingTopicDto> topics = this.icMeetingTopicEntityConverter.disassembleList(entities);
            limitFieldsICMeetingTopics(topics);
            if(topics != null && !topics.isEmpty()){
                for(ICMeetingTopicDto topic: topics){
                    List<NamedFilesDto> materialsLimited = new ArrayList<>();
                    List<NamedFilesDto> materials = getICMeetingTopicAttachments(topic.getId(), null);
                    if(materials != null && !materials.isEmpty()){
                        for(NamedFilesDto namedFilesDto: materials){
                            namedFilesDto.setFile(null);
                            materialsLimited.add(namedFilesDto);
                        }
                        topic.setMaterials(materialsLimited);
                    }
                    topic.setMaterialsCount(materials != null ? materials.size(): 0);

                    // set voting
                    List<ICMeetingTopicsVoteDto> votes = getICMeetingTopicVoting(topic.getId());
                    topic.setVotes(votes);

                }
            }
            return topics;
        }
        return null;
    }

    private List<ICMeetingTopicsVoteDto> getICMeetingTopicVoting(Long topicId){
        List<ICMeetingTopicsVoteDto> votes = new ArrayList<>();
        List<ICMeetingVote> entities =  this.icMeetingVoteRepository.findByIcMeetingTopicId(topicId);
        if(entities != null){
            for(ICMeetingVote vote: entities){
                ICMeetingTopicsVoteDto voteDto = new ICMeetingTopicsVoteDto();
                voteDto.setIcMeetingTopicId(vote.getIcMeetingTopic().getId());
                EmployeeDto employeeDto = this.employeeService.getEmployeeById(vote.getEmployee().getId());
                voteDto.setEmployee(employeeDto);
                voteDto.setVote(vote.getVote().getCode());
                votes.add(voteDto);
            }
        }
        return votes;
    }

    private void limitFieldsICMeetingTopics(List<ICMeetingTopicDto> topics){
        if(topics != null){
            for(ICMeetingTopicDto topic: topics){
                topic.setDecision(null);
                topic.setDescription(null);
                topic.setExplanatoryNote(null);
                topic.setMaterials(null);
            }
        }

    }

//    @Deprecated
//    @Override
//    public Set<FilesDto> saveICMeetingTopicAttachments(Long topicId, Set<FilesDto> attachments, String username) {
//        try {
//            Set<FilesDto> dtoSet = new HashSet<>();
//            if (attachments != null) {
//                Iterator<FilesDto> iterator = attachments.iterator();
//                while (iterator.hasNext()) {
//                    FilesDto filesDto = iterator.next();
//                    if (filesDto.getId() == null) {
//                        Long fileId = fileService.save(filesDto, FileTypeLookup.IC_MATERIALS.getCatalog()); // CATALOG IS ONE
//                        logger.info("Saved IC meeting topic attachment files: topic id=" + topicId + ", file=" + fileId);
//
//                        ICMeetingTopicFiles icMeetingTopicFiles = new ICMeetingTopicFiles(topicId, fileId, );
//                        icMeetingTopicFilesRepository.save(icMeetingTopicFiles);
//                        logger.info("Saved IC meeting topic attachment information to DB: topic id=" + topicId + ", file=" + fileId + " [user]=" + username);
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
//            logger.error("Error saving IC meeting topic attachments: IC meeting topic id=" + topicId, ex);
//        }
//        return null;
//    }

    private boolean saveICMeetingTopicAttachments(Long topicId, List<NamedFilesDto> attachments, boolean update, String username) {
        try {
            if (attachments != null && !attachments.isEmpty()) {
                Iterator<NamedFilesDto> iterator = attachments.iterator();
                while (iterator.hasNext()) {
                    NamedFilesDto namedFilesDto = iterator.next();
                    if (namedFilesDto.getFile().getId() == null) {
                        Long fileId = fileService.save(namedFilesDto.getFile(), FileTypeLookup.IC_MATERIALS.getCatalog()); // CATALOG IS ONE
                        logger.info("Saved IC meeting topic attachment files: topic id=" + topicId + ", file=" + fileId);

                        ICMeetingTopicFiles icMeetingTopicFiles = new ICMeetingTopicFiles(topicId, fileId, namedFilesDto.getName(), update);
                        icMeetingTopicFilesRepository.save(icMeetingTopicFiles);
                        logger.info("Saved IC meeting topic attachment information to DB: topic id=" + topicId + ", file=" + fileId + " [user]=" + username);
                    }else{
                        ICMeetingTopicFiles icMeetingTopicFiles = icMeetingTopicFilesRepository.getFilesByFileId(namedFilesDto.getFile().getId());
                        if(icMeetingTopicFiles != null){
                            icMeetingTopicFiles.setCustomName(namedFilesDto.getName());
                            icMeetingTopicFilesRepository.save(icMeetingTopicFiles);
                        }
                    }
                }
            }
            return true;
        }catch (Exception ex){
            logger.error("Error saving IC meeting topic attachments: IC meeting topic id=" + topicId, ex);
        }
        return false;
    }

    @Override
    public boolean safeDeleteICMeetingTopicAttachment(Long topicId, Long fileId, String username) {
        try {
            ICMeetingTopicFiles entity = icMeetingTopicFilesRepository.getFilesByFileId(fileId);
            if (entity != null && entity.getIcMeetingTopic().getId().longValue() == topicId) {
                if(entity.isUpdate()){
                    if (!checkEditableICMeetingTopicUpdateByTopicIdAndUsername(topicId, username)) {
                        String errorMessage = "Error deleting IC Meeting Topic Attachment Update: entity not editable";
                        logger.error(errorMessage);
                        return false;
                    }
                }else {
                    if (!checkEditableICMeetingTopicByTopicIdAndUsername(topicId, username)) {
                        String errorMessage = "Error deleting IC Meeting Topic Attachment: entity not editable";
                        logger.error(errorMessage);
                        return false;
                    }
                }

                boolean deleted = fileService.safeDelete(fileId);
                if(!deleted){
                    logger.error("Failed to delete(safe) IC meeting topic attachment: IC topic =" + topicId.longValue() + ", file=" + fileId.longValue() + ", by " + username);
                }else{
                    logger.info("Deleted(safe) IC meeting topic attachment: IC topic =" + topicId.longValue() + ", file=" + fileId.longValue() + ", by " + username);
                    if(entity.isUpdate()){
                        resetICMeetingTopicVoting(topicId);
                    }else{
                        resetICMeetingTopicApprovals(topicId);
                    }
                }
                return deleted;
            }else{
                logger.error("Error save deleting IC meeting topic attachment: topic not found with id=" + topicId.longValue());
            }
        }catch (Exception e){
            logger.error("Failed to delete(safe) IC meeting topic attachment with error: IC topic =" + topicId.longValue() + ", file=" + fileId.longValue() + ", by " + username, e);
        }
        return false;
    }

    private boolean checkApprovableICMeetingTopicByTopicIdAndUsername(Long id, String username){
        if(id == null || id.longValue() == 0){
            // new topic
            return false;
        }
        try {
            ICMeetingTopic entity = icMeetingTopicRepository.findOne(id);
            if(entity != null){
                if(entity.getIcMeeting() != null && entity.getIcMeeting().getClosed() != null &&
                        entity.getIcMeeting().getClosed().booleanValue()){
                    return false;
                }
                if(entity.getDeleted() != null && entity.getDeleted().booleanValue()){
                    return false;
                }
                if(entity.getIcMeeting() != null && isICMeetingLockedByDeadline(entity.getIcMeeting())){
                    return false;
                }
                EmployeeDto editor = this.employeeService.findByUsername(username);
                boolean hasApproveRole = true;
                if(editor.getRoles() != null && !editor.getRoles().isEmpty()){
                    for(BaseDictionaryDto role: editor.getRoles()){
                        if(role.getCode().equalsIgnoreCase(UserRoles.IC_TOPIC_RESTR.getCode()) ||
                                role.getCode().equalsIgnoreCase(UserRoles.IC_MEMBER.getCode())){
                            hasApproveRole = true;
                        }
                    }
                }
                if(!hasApproveRole){
                    return false;
                }

                // Check approve list
                Set<ICMeetingTopicApproval> approveList = entity.getApproveList();
                if(approveList != null && !approveList.isEmpty()){
                    for(ICMeetingTopicApproval approval: approveList){
                        if(approval.getEmployee() != null && approval.getEmployee().getId().longValue() == editor.getId().longValue()){
                            return true;
                        }
                    }
                }
            }
        }catch(Exception ex){
            logger.error("Error checking IC meeting topic editable: id =" + id, ex);
        }
        return false;
    }

    @Override
    public boolean deleteICMeetingTopicExplanatoryNote(Long topicId, String username) {
        try {
            if(!checkEditableICMeetingTopicByTopicIdAndUsername(topicId, username)){
                String errorMessage = "Error deleting IC Meeting Topic Explanatory Note: entity not editable";
                logger.error(errorMessage);
                return false;
            }
            ICMeetingTopic entity = icMeetingTopicRepository.findOne(topicId);
            if (entity != null && entity.getExplanatoryNote() != null) {
                boolean deleted = fileService.safeDelete(entity.getExplanatoryNote().getId());
                if(deleted) {
                    long fileId = entity.getExplanatoryNote().getId().longValue();
                    entity.setExplanatoryNote(null);
                    this.icMeetingTopicRepository.save(entity);
                    logger.info("Deleted(safe) IC meeting topic explanatory note: IC topic =" + topicId + ", file=" +
                            fileId + ", by " + username);

                    resetICMeetingTopicApprovals(topicId);
                }else{
                    logger.error("Failed to delete IC meeting topic explanatory note");
                }
                return deleted;
            }else{
                logger.error("Error save deleting IC meeting topic explanatory note: topic not found with id=" + topicId);
            }
        }catch (Exception e){
            logger.error("Failed to delete(safe) IC meeting topic explanatory note with error: IC topic =" + topicId + ", by " + username, e);
        }
        return false;
    }

    @Override
    public boolean deleteICMeetingTopicExplanatoryNoteUpd(Long topicId, String username) {
        try {
            if(!checkEditableICMeetingTopicUpdateByTopicIdAndUsername(topicId, username)){
                String errorMessage = "Error deleting IC Meeting Topic Explanatory Note Upd: entity not editable";
                logger.error(errorMessage);
                return false;
            }
            ICMeetingTopic entity = icMeetingTopicRepository.findOne(topicId);
            if (entity != null && entity.getExplanatoryNoteUpd() != null) {
                boolean deleted = fileService.safeDelete(entity.getExplanatoryNoteUpd().getId());
                if(deleted) {
                    long fileId = entity.getExplanatoryNoteUpd().getId().longValue();
                    entity.setExplanatoryNoteUpd(null);
                    this.icMeetingTopicRepository.save(entity);
                    logger.info("Deleted(safe) IC meeting topic explanatory note update: IC topic =" + topicId + ", file=" +
                            fileId + ", by " + username);

                    //resetICMeetingTopicApprovals(topicId);
                    resetICMeetingTopicVoting(topicId);
                }else{
                    logger.error("Failed to delete IC meeting topic explanatory note");
                }
                return deleted;
            }else{
                logger.error("Error save deleting IC meeting topic explanatory note: topic not found with id=" + topicId);
            }
        }catch (Exception e){
            logger.error("Failed to delete(safe) IC meeting topic explanatory note with error: IC topic =" + topicId + ", by " + username, e);
        }
        return false;
    }


    /* IC MEETING *****************************************************************************************************/
    //@Transactional
    @Override
    public EntitySaveResponseDto saveICMeeting(ICMeetingDto icMeetingDto, FilesDto agendaFile, String updater) {
        EntitySaveResponseDto saveResponseDto = new EntitySaveResponseDto();
        try {
            ICMeeting entity = icMeetingsEntityConverter.assemble(icMeetingDto);
            if(icMeetingDto.getId() == null){ // CREATE
                EmployeeDto employee = this.employeeService.findByUsername(updater);
                // set creator
                entity.setCreator(new Employee(employee.getId()));
            }else{ // UPDATE
                // set creator
                if(icMeetingDto != null && !checkEditableICMeeting(icMeetingDto, updater)){
                    String errorMessage = "Error saving IC Meeting with id " + icMeetingDto.getId().longValue() + ": entity not editable";
                    logger.error(errorMessage);
                    saveResponseDto.setErrorMessageEn(errorMessage);
                    return saveResponseDto;
                }
                ICMeeting currentEntity = this.icMeetingsRepository.findOne(icMeetingDto.getId());
                Employee creator = currentEntity.getCreator();
                entity.setCreator(creator);
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

            if(agendaFile != null){
                if(entity.getAgenda() != null){
                    String errorMessage = "Error saving IC meeting : agenda exists, please delete the current file before uploading new one.";
                    logger.error(errorMessage);
                    saveResponseDto = new EntitySaveResponseDto();
                    saveResponseDto.setErrorMessageEn(errorMessage);
                    return saveResponseDto;
                }
                Long fileId = fileService.save(agendaFile, FileTypeLookup.IC_AGENDA.getCatalog());
                entity.setAgenda(new Files(fileId));
            }

            entity = icMeetingsRepository.save(entity);

            // save attendees
            if(icMeetingDto.getAttendees() != null && !icMeetingDto.getAttendees().isEmpty()){
                List<ICMeetingAttendees> attendees = new ArrayList<>();
                for(ICMeetingAttendeesDto dto: icMeetingDto.getAttendees()){
                    ICMeetingAttendees attendee = new ICMeetingAttendees();
                    attendee.setIcMeeting(entity);
                    attendee.setEmployee(new Employee(dto.getEmployee().getId()));
                    attendee.setPresent(dto.isPresent());
                    if(!dto.isPresent()) {
                        if (dto.getAbsenceType() != null && dto.getAbsenceType() != null) {
                            ICMeetingAttendeeAbsenceType absenceType = this.lookupService.findByTypeAndCode(ICMeetingAttendeeAbsenceType.class, dto.getAbsenceType());
                            if(absenceType == null) {
                                // TODO: check transactions
                                saveResponseDto.setErrorMessageEn("Failed to save IC Meeting: attendees not present must have reason specified");
                                return saveResponseDto;
                            }
                            attendee.setAbsenceType(absenceType);
                        }
                    }else{
                        attendee.setAbsenceType(null);
                    }
                    attendees.add(attendee);
                }
                this.icMeetingAttendeesRepository.deleteByICMeetingId(entity.getId());
                this.icMeetingAttendeesRepository.save(attendees);
            }else{
                // clear all existing
                this.icMeetingAttendeesRepository.deleteByICMeetingId(entity.getId());
            }

            // save invitees
            if(icMeetingDto.getInvitees() != null && !icMeetingDto.getInvitees().isEmpty()){
                List<ICMeetingInvitees> invitees = new ArrayList<>();
                for(EmployeeDto employeeDto: icMeetingDto.getInvitees()){
                    if(employeeDto.getId() != null) {
                        ICMeetingInvitees invitee = new ICMeetingInvitees();
                        invitee.setIcMeeting(entity);
                        invitee.setEmployee(new Employee(employeeDto.getId()));
                        invitees.add(invitee);
                    }
                }
                this.icMeetingInviteesRepository.deleteByICMeetingId(entity.getId());
                this.icMeetingInviteesRepository.save(invitees);
            }else{
                // clear all existing
                this.icMeetingInviteesRepository.deleteByICMeetingId(entity.getId());
            }

            // Update ic topics order
            if(icMeetingDto.getTopics() != null && !icMeetingDto.getTopics().isEmpty()){
                for(ICMeetingTopicDto topicDto: icMeetingDto.getTopics()){
                    // update order
                    this.icMeetingTopicRepository.updateICOrder(topicDto.getId(), topicDto.getIcOrder());
                }
            }

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
    public boolean deleteICMeetingAgenda(Long icMeetingId, String username) {
        try {
            ICMeetingDto icMeeting = getICMeeting(icMeetingId, username);
            if(icMeeting != null && !checkEditableICMeeting(icMeeting, username)){
                String errorMessage = "Error saving IC Meeting with id " + icMeeting.getId().longValue() + ": entity not editable";
                logger.error(errorMessage);
                return false;
            }
            ICMeeting entity = this.icMeetingsRepository.findOne(icMeetingId);
            if (entity != null && entity.getAgenda() != null) {
                boolean deleted = fileService.safeDelete(entity.getAgenda().getId());
                long fileId = entity.getAgenda().getId().longValue();
                entity.setAgenda(null);
                this.icMeetingsRepository.save(entity);
                logger.info("Deleted(safe) IC meeting agenda: IC id =" + icMeetingId.longValue() + ", file=" +
                        fileId + ", by " + username);
                return deleted;
            }else{
                logger.error("Error save deleting IC meeting agenda: ic meeting not found with id=" + icMeetingId.longValue());
            }
        }catch (Exception e){
            logger.error("Failed to delete(safe) IC meeting agenda with error: IC id =" + icMeetingId.longValue() + ", by " + username, e);
        }
        return false;
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
    public ICMeetingDto getICMeeting(Long id, String username) {
        try {
            ICMeeting entity = icMeetingsRepository.findOne(id);
            ICMeetingDto dto = icMeetingsEntityConverter.disassemble(entity);

            // attendees
            List<ICMeetingAttendeesDto> attendees = getICMeetingAttendees(id);
            dto.setAttendees(attendees);
            //Collections.sort(dto.getAttendees());

            // invitees
            List<EmployeeDto> invitees = getICMeetingInvitees(id);
            dto.setInvitees(invitees);

            // questions
            dto.setTopics(getLimitedICMeetingTopicsByMeetingId(id));
            Collections.sort(dto.getTopics());

            // voting

            return dto;
        }catch(Exception ex){
            logger.error("Error loading IC meeting: " + id, ex);
        }
        return null;
    }

    private List<ICMeetingAttendeesDto> getICMeetingAttendees(Long icMeetingId){
        List<ICMeetingAttendeesDto> attendees = new ArrayList<>();
        List<ICMeetingAttendees> entities = this.icMeetingAttendeesRepository.findByIcMeetingId(icMeetingId);
        if(entities != null){
            for(ICMeetingAttendees entity: entities){
                ICMeetingAttendeesDto attendee = new ICMeetingAttendeesDto();
                EmployeeDto employeeDto = this.employeeService.getEmployeeById(entity.getEmployee().getId());
                attendee.setEmployee(employeeDto);
                attendee.setPresent(entity.isPresent());
                if(!attendee.isPresent() && entity.getAbsenceType() != null) {
                    attendee.setAbsenceType(entity.getAbsenceType().getCode());
                }
                attendees.add(attendee);
            }
        }
        Collections.sort(attendees);
        return attendees;
    }

    private List<EmployeeDto> getICMeetingInvitees(Long icMeetingId){
        List<EmployeeDto> invitees = new ArrayList<>();
        List<ICMeetingInvitees> entities = this.icMeetingInviteesRepository.findByIcMeetingId(icMeetingId);
        if(entities != null){
            for(ICMeetingInvitees entity: entities){
                EmployeeDto employeeDto = this.employeeService.getEmployeeById(entity.getEmployee().getId());
                invitees.add(employeeDto);
            }
        }
        return invitees;
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
            ICMeetingDto dto = getICMeeting(id, username);
            if(dto != null && !checkEditableICMeeting(dto, username)){
                String errorMessage = "Error deleting IC Meeting with id + " + id + ": entity not editable";
                logger.error(errorMessage);
                return false;
            }
            ICMeeting entity = icMeetingsRepository.findOne(id);
            //  check IC Meeting topics
            ICMeetingTopicsSearchParamsDto searchParamsDto = new ICMeetingTopicsSearchParamsDto();
            searchParamsDto.setIcNumber(entity.getNumber());
            ICMeetingTopicsPagedSearchResult result = searchICMeetingTopics(searchParamsDto);
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

    private boolean checkEditableICMeeting(ICMeetingDto dto, String username){
        try {
            if(dto != null){
                if(dto.getClosed() != null && dto.getClosed().booleanValue()){
                    return false;
                }
                if(dto.getDeleted() != null && dto.getDeleted().booleanValue()){
                    return false;
                }
                if(dto.getId() != null) {
                    ICMeetingDto existingDto = getICMeeting(dto.getId(), username);
                    if(existingDto != null){
                        if(existingDto.isLockedByDeadline()) {
                            boolean adminEdit = this.employeeService.checkRoleByUsername(UserRoles.ADMIN, username)
                                    || this.employeeService.checkRoleByUsername(UserRoles.IC_ADMIN, username);
                            if (!adminEdit) {
                                return false;
                            }
                        }
                    }
                }
                return true;
            }
        }catch(Exception ex){
            logger.error("Error checking IC meeting editable: id =" + (dto != null && dto.getId() != null ? dto.getId().longValue() : null), ex);
        }
        return false;
    }

    private boolean checkICMeetingUnlockableForFinalize(ICMeetingDto dto, String username){
        return checkEditableICMeeting(dto, username) && dto.isLockedByDeadline();
    }

    private boolean checkICMeetingCanVote(ICMeetingDto dto, String username){
        // check IC Meeting
        if(dto.getClosed() != null && dto.getClosed().booleanValue()){
            return false;
        }
        if(dto.getDeleted() != null && dto.getDeleted().booleanValue()){
            return false;
        }

        // check user in the list of attending
        boolean attending = false;
        if(dto.getAttendees() != null && !dto.getAttendees().isEmpty()){
            for(ICMeetingAttendeesDto attendeesDto: dto.getAttendees()){
                if(attendeesDto.isPresent() && attendeesDto.getEmployee() != null && attendeesDto.getEmployee().getUsername() != null &&
                        attendeesDto.getEmployee().getUsername().equalsIgnoreCase(username)){
                    attending = true;
                    break;
                }
            }
        }
        if(!attending){
            return false;
        }

        // check user role
        boolean icMemberRole = this.employeeService.checkRoleByUsername(UserRoles.IC_MEMBER, username);
        if(!icMemberRole){
            return false;
        }

        return true;
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
                    if(dto.getId() != null){
                        List<ICMeetingTopicDto> topics = getLimitedICMeetingTopicsByMeetingId(dto.getId());
                        dto.setTopics(topics);
                    }
                }

                Collections.sort(result.getIcMeetings());
            }
            return result;
        }catch(Exception ex){
            // TODO: log search params
            logger.error("Error searching IC meetings", ex);
        }
        return null;
    }

    @Override
    public List<ICMeetingDto> getAllICMeetingsShort() {
        List<ICMeetingDto> dtoList = new ArrayList<>();
        Page<ICMeeting> pagedResult = this.icMeetingsRepository.searchAll(new PageRequest(0, 1000));
        if(pagedResult != null && pagedResult.getContent() != null && !pagedResult.getContent().isEmpty()){
            for(ICMeeting entity: pagedResult.getContent()){
                ICMeetingDto dto = this.icMeetingsEntityConverter.disassemble(entity);
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

    @Override
    public FilesDto getICMeetingAgendaFileStream(Long icMeetingId, String username){

        FilesDto filesDto = new FilesDto();
        ICMeetingDto icMeeting = getICMeeting(icMeetingId, username);

        final String exportFileTemplatePath = "export_template/corp_meetings/IC_AGENDA_TEMPLATE.docx";
        Resource resource = new ClassPathResource(exportFileTemplatePath);
        InputStream wordFileToRead = null;
        try {
            wordFileToRead = resource.getInputStream();
        } catch (IOException e) {
            logger.error("IC Meeting: Export file template not found: '" + exportFileTemplatePath + "'");
            e.printStackTrace();
            return null;
        }

        try {
            XWPFDocument document = new XWPFDocument(wordFileToRead);
            XWPFTable table = document.getTables().get(0);
            if(icMeeting.getTopics() != null && !icMeeting.getTopics().isEmpty()){
                int i = 1;
                for(ICMeetingTopicDto topic: icMeeting.getTopics()){
                    //insert new last row, which is a copy empty row:
                    XWPFTableRow lastRow = table.getRows().get(1);
                    CTRow ctrow = null;
                    try {
                        ctrow = CTRow.Factory.parse(lastRow.getCtRow().newInputStream());
                    } catch (XmlException e) {
                        //TODO: ???
                        //e.printStackTrace();
                    }
                    XWPFTableRow newRow = new XWPFTableRow(ctrow, table);
                    newRow.getCell(0).removeParagraph(0);
                    XWPFParagraph paragraph1 = newRow.getCell(0).addParagraph();
                    XWPFRun run1 = paragraph1.createRun();
                    run1.setFontFamily("Arial");
                    run1.setFontSize(11);
                    run1.setText(i + ".");

                    newRow.getCell(1).removeParagraph(0);
                    XWPFParagraph paragraph2 = newRow.getCell(1).addParagraph();
                    XWPFRun run2 = paragraph2.createRun();
                    run2.getCTR().addNewRPr().addNewRFonts().setHAnsi("Arial");
                    run2.setFontFamily("Arial");
                    run2.setFontSize(11);
                    run2.setText(topic.getName());

                    if(topic.getSpeaker() != null) {
                        String speakerName = topic.getSpeaker().getLastNameRu();
                        speakerName = StringUtils.isNotEmpty(speakerName) ? speakerName +
                                (StringUtils.isNotEmpty(topic.getSpeaker().getFirstNameRu()) ? " " + topic.getSpeaker().getFirstNameRu().charAt(0) + ".": "") : "";
                        newRow.getCell(2).removeParagraph(0);
                        XWPFParagraph paragraph3 = newRow.getCell(2).addParagraph();
                        XWPFRun run3 = paragraph3.createRun();
                        run3.getCTR().addNewRPr().addNewRFonts().setHAnsi("Arial");
                        run3.setFontFamily("Arial");
                        run3.setFontSize(11);
                        run3.setText(speakerName);
                    }
                    table.addRow(newRow);
                    i++;
                }
                table.removeRow(1);//empty row
            }

            List<XWPFParagraph> paragraphs = document.getParagraphs();
            if(paragraphs != null){
                for(XWPFParagraph paragraph: paragraphs){
                    List<XWPFRun> runs = paragraph.getRuns();
                    if (runs != null) {
                        for (XWPFRun r : runs) {
                            String text = r.getText(0);
                            if (text != null && text.contains("ICDATE")) {
                                if(icMeeting.getDate() != null) {
                                    text = text.replace("ICDATE", DateUtils.getDateRussianTextualDate(icMeeting.getDate()));
                                }else{
                                    text = text.replace("ICDATE", "");
                                }
                                r.setText(text, 0);
                            }else if (text != null && text.contains("ICTIME")) {
                                if(icMeeting.getTime() != null) {
                                    text = text.replace("ICTIME", icMeeting.getTime());
                                }else{
                                    text = text.replace("ICTIME", "");
                                }
                                r.setText(text, 0);
                            }else if (text != null && text.contains("ICPLACE")) {
                                if(icMeeting.getPlace() != null) {
                                    ICMeetingPlaceType place = this.lookupService.findByTypeAndCode(ICMeetingPlaceType.class, icMeeting.getPlace());
                                    if(place != null && place.getNameRu() != null){
                                        text = text.replace("ICPLACE", place.getNameRu());
                                    }else{
                                        text = text.replace("ICPLACE", "");
                                    }
                                }else{
                                    text = text.replace("ICPLACE", "");
                                }
                                r.setText(text, 0);
                            }
                        }
                    }
                }

                File tmpDir = new File(this.rootDirectory + "/tmp/corp_meetings");

                // write to new
                String filePath = tmpDir + "/IC_AGENDA_" + MathUtils.getRandomNumber(0, 10000) + ".docx";
                try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                    document.write(outputStream);
                }

                InputStream inputStream = new FileInputStream(filePath);
                filesDto.setInputStream(inputStream);
                filesDto.setFileName(filePath);
                return filesDto;
            }

        } catch (IOException e) {
            logger.error("IO Exception when exporting IC Meeting Agenda", e);
            //e.printStackTrace();
        }

        return null;
    }

    static void setParagraphRunProperties(XWPFParagraph paragraph, String fontFamily, int fontSize) {
        if (!paragraph.getCTP().isSetPPr()) paragraph.getCTP().addNewPPr();
        if (!paragraph.getCTP().getPPr().isSetRPr()) paragraph.getCTP().getPPr().addNewRPr();
        if (!paragraph.getCTP().getPPr().getRPr().isSetRFonts()) paragraph.getCTP().getPPr().getRPr().addNewRFonts();
        if (!paragraph.getCTP().getPPr().getRPr().isSetSz()) paragraph.getCTP().getPPr().getRPr().addNewSz();
        if (!paragraph.getCTP().getPPr().getRPr().isSetSzCs()) paragraph.getCTP().getPPr().getRPr().addNewSzCs();
        paragraph.getCTP().getPPr().getRPr().getRFonts().setAscii(fontFamily);
        paragraph.getCTP().getPPr().getRPr().getRFonts().setHAnsi(fontFamily);
        paragraph.getCTP().getPPr().getRPr().getSz().setVal(BigInteger.valueOf(fontSize*2)); //measurement unit is half pt
        paragraph.getCTP().getPPr().getRPr().getSzCs().setVal(BigInteger.valueOf(fontSize*2)); //measurement unit is half pt
    }

    private void insertNumberedList(XWPFDocument document, int paragraphIndex, List<String> values) {
        if(values != null && !values.isEmpty()) {
            XWPFNumbering numbering = document.getNumbering();
            XWPFAbstractNum numAbstract =  numbering.getAbstractNum(BigInteger.ONE);
            BigInteger numId = numbering.addNum(numAbstract.getAbstractNum().getAbstractNumId());
            XWPFNum num = numbering.getNum(numId);
            CTNumLvl lvloverride = num.getCTNum().addNewLvlOverride();
            lvloverride.setIlvl(BigInteger.ZERO);
            CTDecimalNumber number = lvloverride.addNewStartOverride();
            number.setVal(BigInteger.ONE);

            int indentLeft = 650;
            int spacingAfter = 0;
            for (String value : values) {
                XmlCursor cursor = document.getParagraphs().get(paragraphIndex).getCTP().newCursor();
                XWPFParagraph newParagraph = document.insertNewParagraph(cursor);
                newParagraph.setIndentationLeft(indentLeft);
                newParagraph.setSpacingAfter(spacingAfter);
                newParagraph.setNumID(numId);

                CTPPr pPr = newParagraph.getCTP().isSetPPr() ? newParagraph.getCTP().getPPr() : newParagraph.getCTP().addNewPPr();
                pPr.set(document.getParagraphs().get(paragraphIndex).getCTP().getPPr());
                XWPFRun run = newParagraph.createRun();
                setParagraphRunProperties(newParagraph, FONT_ARIAL, FONT_SIZE);
                run.setFontFamily(FONT_ARIAL);
                run.setFontSize(FONT_SIZE);
                run.setText(value);
                paragraphIndex++;
            }
        }

    }

    private void updateICMeetingProtocolAttendees(XWPFDocument document, List<EmployeeDto> attendees){
        if(attendees != null && !attendees.isEmpty()){
            List<XWPFParagraph> paragraphs = document.getParagraphs();
            int index = 0;
            for(XWPFParagraph paragraph: paragraphs) {
                if(paragraph.getText().startsWith(IC_PROTOCOL_ATTENDEE_PLACEHOLDER)){
                    break;
                }
                index++;
            }
            if(index > 0) {
                List<String> values = new ArrayList<>();
                if (attendees != null) {
                    attendees.forEach((attendee -> {
                        String fullNameInitials = attendee.getFullNameInitialsRu();
                        fullNameInitials = fullNameInitials == null ? attendee.getLastName() : fullNameInitials;
                        String position = attendee.getFullPositionRu() != null ? attendee.getFullPositionRu() : null;
                        values.add(fullNameInitials + (position != null ? "/" + position : ""));
                    }));
                }
                insertNumberedList(document, index, values);
            }
        }
        removePlaceholders(document, IC_PROTOCOL_ATTENDEE_PLACEHOLDER);
    }

    private void updateICMeetingProtocolAbsentees(XWPFDocument document, List<EmployeeDto> absentees){
        if(absentees != null && !absentees.isEmpty()){
            List<XWPFParagraph> paragraphs = document.getParagraphs();
            int index = 0;
            for(XWPFParagraph paragraph: paragraphs) {
                if(paragraph.getText().startsWith(IC_PROTOCOL_ABSENTEE_PLACEHOLDER)){
                    break;
                }
                index++;
            }
            List<String> values = new ArrayList<>();
            if(absentees != null){
                absentees.forEach((attendee ->{
                    String fullNameInitials = attendee.getFullNameInitialsRu();
                    fullNameInitials = fullNameInitials == null ? attendee.getLastName() : fullNameInitials;
                    String position = attendee.getFullPositionRu() != null ?attendee.getFullPositionRu() : null;
                    values.add(fullNameInitials + (position != null ? "/" + position : ""));
                }));
            }
            insertNumberedList(document, index, values);
        }
        removePlaceholders(document, IC_PROTOCOL_ABSENTEE_PLACEHOLDER);
    }

    private void updateICMeetingProtocolInvitees(XWPFDocument document, List<EmployeeDto> invitees){
        if(invitees != null && !invitees.isEmpty()){
            List<XWPFParagraph> paragraphs = document.getParagraphs();
            int index = 0;
            for(XWPFParagraph paragraph: paragraphs) {
                if(paragraph.getText().startsWith(IC_PROTOCOL_INVITEE_PLACEHOLDER)){
                    break;
                }
                index++;
            }
            List<String> values = new ArrayList<>();
            if(invitees != null){
                invitees.forEach((attendee ->{
                    String fullNameInitials = attendee.getFullNameInitialsRu();
                    fullNameInitials = fullNameInitials == null ? attendee.getLastName() : fullNameInitials;
                    String position = attendee.getFullPositionRu() != null ?attendee.getFullPositionRu() : null;
                    values.add(fullNameInitials + (position != null ? "/" + position : ""));
                }));
            }
            insertNumberedList(document, index, values);
        }
        removePlaceholders(document, IC_PROTOCOL_INVITEE_PLACEHOLDER);
    }

    private void updateICMeetingProtocolTopics(XWPFDocument document, List<ICMeetingTopicDto> topics){
        if(topics != null && !topics.isEmpty()){
            List<XWPFParagraph> paragraphs = document.getParagraphs();
            int index = 0;
            for(XWPFParagraph paragraph: paragraphs) {
                if(paragraph.getText().startsWith(IC_PROTOCOL_QUESTION_PLACEHOLDER)){
                    break;
                }
                index++;
            }
            List<String> values = new ArrayList<>();
            if(topics != null){
                topics.forEach((topic ->{
                    values.add(topic.getPublishedNameUpd());
                }));
            }
            insertNumberedList(document, index, values);
        }
        removePlaceholders(document, IC_PROTOCOL_QUESTION_PLACEHOLDER);
    }

    private void updateICMeetingBulletin(XWPFDocument document, List<ICMeetingTopicDto> topics, String name){
        if(topics != null && !topics.isEmpty()){
            List<XWPFParagraph> paragraphs = document.getParagraphs();
            int index = 0;
            for(XWPFParagraph paragraph: paragraphs) {
                if(paragraph.getText().contains(IC_BULLETIN_NAME_PLACEHOLDER)){
                    //set name
                    List<XWPFRun> runs = paragraph.getRuns();
                    if (runs != null) {
                        for (XWPFRun r : runs) {
                            String text = r.getText(0);
                            if (text != null && text.contains("NAME")) {
                                if(name != null) {
                                    text = text.replace("NAME", name);
                                }else{
                                    text = text.replace("NAME", "");
                                }
                                r.setText(text, 0);
                            }
                        }
                    }
                }else if(paragraph.getText().startsWith(IC_BULLETIN_DECISION_PLACEHOLDER)){
                    break;
                }
                index++;
            }
            List<String> values = new ArrayList<>();
            if(topics != null){
                topics.forEach((topic ->{
                    values.add(topic.getPublishedDecisionUpd());
                }));
            }
            insertNumberedList(document, index, values);
            index += values.size();
            // add table rows
            insertBulletinTableRows(document, topics.size());
        }

        removePlaceholders(document, IC_BULLETIN_DECISION_PLACEHOLDER);
    }

    private void insertProtocolVotingTableRows(XWPFDocument document, int index, int tableIndex, String[] votes){
        XWPFTable tableTemplate = document.getTableArray(0);
        CTTbl cTTblTemplate = tableTemplate.getCTTbl();

        XmlCursor cursor= document.getParagraphs().get(index).getCTP().newCursor();
        XWPFTable table = document.insertNewTbl(cursor);

        XWPFTable tableCopy = new XWPFTable((CTTbl)cTTblTemplate.copy(), document); //copy the template table

        //insert table rows
        XWPFTableRow row1 = tableCopy.getRow(0);
        XWPFTableCell cell = row1.getTableCells().get(1);
        XWPFRun run1 = cell.getParagraphs().get(0).getRuns() != null && !cell.getParagraphs().get(0).getRuns().isEmpty() ?
                cell.getParagraphs().get(0).getRuns().get(0) : cell.getParagraphs().get(0).createRun();
        run1.setFontFamily(FONT_ARIAL);
        run1.setFontSize(FONT_SIZE);
        run1.setItalic(true);
        run1.setText( votes[1], 0);

        XWPFTableRow row2 = tableCopy.getRow(1);
        cell = row2.getTableCells().get(1);
        XWPFRun run2 = cell.getParagraphs().get(0).getRuns() != null && !cell.getParagraphs().get(0).getRuns().isEmpty() ?
                cell.getParagraphs().get(0).getRuns().get(0) : cell.getParagraphs().get(0).createRun();
        run2.setFontFamily(FONT_ARIAL);
        run2.setFontSize(FONT_SIZE);
        run2.setItalic(true);
        run2.setText( votes[2], 0);

        XWPFTableRow row3 = tableCopy.getRow(2);
        cell = row3.getTableCells().get(1);
        XWPFRun run3 = cell.getParagraphs().get(0).getRuns() != null && !cell.getParagraphs().get(0).getRuns().isEmpty() ?
                cell.getParagraphs().get(0).getRuns().get(0) : cell.getParagraphs().get(0).createRun();
        run3.setFontFamily(FONT_ARIAL);
        run3.setFontSize(FONT_SIZE);
        run3.setItalic(true);
        run3.setText( votes[3], 0);

        document.setTable(tableIndex, tableCopy); //set tableCopy at position t instead of table
    }

    private void insertBulletinTableRows(XWPFDocument document, int topicNum){
        XWPFTable table = document.getTableArray(0);

        //insert new row, which is a copy of row 2, as new row 3:
        XWPFTableRow oldRow = table.getRow(1);
        CTRow ctrow = null;
        try {
            ctrow = CTRow.Factory.parse(oldRow.getCtRow().newInputStream());
            int move = 0;
            for(int j = 2; j <= topicNum; j++) {
                XWPFTableRow newRow = new XWPFTableRow(ctrow, table);
                int i = 1;
                for (XWPFTableCell cell : newRow.getTableCells()) {
                    for (XWPFParagraph paragraph : cell.getParagraphs()) {
                        if(i == 1){
                            paragraph.getRuns().get(0).setText(j + "", 0);
                        }
                        i++;
                    }
                }
                table.addRow(newRow, 2 + move);
                move++;
            }
        } catch (XmlException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateICMeetingProtocolTopicsMaterials(XWPFDocument document, List<ICMeetingTopicDto> topics){
        if(topics != null && !topics.isEmpty()){
            List<XWPFParagraph> paragraphs = document.getParagraphs();
            int index = 0;
            for(XWPFParagraph paragraph: paragraphs) {
                if(paragraph.getText().startsWith(IC_PROTOCOL_QUESTION_MATERIAL_HEADER_PLACEHOLDER)){
                    break;
                }
                index++;
            }
            for(int i = 0; i < topics.size(); i++){
                if(topics.get(i).getMaterials() != null && !topics.get(i).getMaterials().isEmpty()) {
                    // insert header
                    XmlCursor cursor = paragraphs.get(index + 1).getCTP().newCursor();
                    XWPFParagraph headerParagraph = document.insertNewParagraph(cursor);
                    headerParagraph.setSpacingAfter(0);
                    XWPFRun run = headerParagraph.createRun();
                    run.addBreak();
                    run = headerParagraph.createRun();
                    run.setFontFamily(FONT_ARIAL);
                    run.setFontSize(FONT_SIZE);
                    run.setBold(true);
                    run.setText(getMaterialsHeader(i + 1));
                    index++;

                    // insert materials
                    insertICMeetingProtocolTopicsMaterials(document, index + 1, topics.get(i));
                    index = index + topics.get(i).getMaterials().size();
                }
            }
        }
        removePlaceholders(document, IC_PROTOCOL_QUESTION_MATERIAL_HEADER_PLACEHOLDER);
    }

    private void insertICMeetingProtocolTopicsMaterials(XWPFDocument document, int paragraphIndex, ICMeetingTopicDto topic){
        List<XWPFParagraph> paragraphs = document.getParagraphs();
        if(paragraphs != null && !paragraphs.isEmpty() && topic != null && !topic.getMaterials().isEmpty()){
            List<String> values = new ArrayList<>();
            topic.getMaterials().forEach((material ->{
                values.add(material.getName());
            }));

            insertNumberedList(document, paragraphIndex, values);
        }
    }

    private void updateICMeetingProtocolTopicsDiscussions(XWPFDocument document, List<ICMeetingTopicDto> topics){
        if(topics != null && !topics.isEmpty()){
            List<XWPFParagraph> paragraphs = document.getParagraphs();
            int index = 0;
            for(XWPFParagraph paragraph: paragraphs) {
                if(paragraph.getText().startsWith(IC_PROTOCOL_QUESTION_DISCUSSION_PLACEHOLDER)){
                    break;
                }
                index++;
            }
            for(int i = 0; i < topics.size(); i++){
                // insert header
                XmlCursor cursor = paragraphs.get(index + 1).getCTP().newCursor();
                XWPFParagraph headerParagraph = document.insertNewParagraph(cursor);
                headerParagraph.setSpacingAfter(0);
                XWPFRun run = headerParagraph.createRun();
                run.setFontFamily(FONT_ARIAL);
                run.setFontSize(FONT_SIZE);
                run.setBold(true);
                run.setText("По " + getNumberRussian(i + 1) + " вопросу ");

                run = headerParagraph.createRun();
                run.setFontFamily(FONT_ARIAL);
                run.setFontSize(FONT_SIZE);
                //run.setBold(true);
                String speaker = topics.get(i).getSpeaker() != null ? topics.get(i).getSpeaker().getFullNameInitialsRu() : null;
                run.setText(getDiscussionsHeader(speaker, topics.get(i).getPublishedNameUpd()));
                run.addBreak();
                index++;

                //insert decision
                cursor = paragraphs.get(index + 1).getCTP().newCursor();
                XWPFParagraph decisionParagraph = document.insertNewParagraph(cursor);
                XWPFRun runDecisionHeader = decisionParagraph.createRun();
                runDecisionHeader.setFontFamily(FONT_ARIAL);
                runDecisionHeader.setFontSize(FONT_SIZE);
                runDecisionHeader.setBold(true);
                runDecisionHeader.setText(getDecisionHeader(i + 1));
                runDecisionHeader.addBreak();
                //index++;

                XWPFRun runDecision = decisionParagraph.createRun();
                String decision = topics.get(i).getPublishedDecisionUpd() != null ? topics.get(i).getPublishedDecisionUpd() : "";
                runDecision.setFontFamily(FONT_ARIAL);
                runDecision.setFontSize(FONT_SIZE);
                //run.setBold(true);
                runDecision.setText("«" + decision + "»");
                //runDecision.addBreak();
                index++;
            }
        }
        removePlaceholders(document, IC_PROTOCOL_QUESTION_DISCUSSION_PLACEHOLDER);
    }

    private List<String[]> getVotesCombined(List<ICMeetingTopicDto> topics){
        List<String[]> combinedVotes = new ArrayList<String[]>();
        if(topics != null){
            List<List<String>> result = new ArrayList<>();
            for(ICMeetingTopicDto topic: topics){
                List<String> votesList = new ArrayList<>();
                votesList.add(getVotesAsText(topic, "YES"));
                votesList.add(getVotesAsText(topic, "NO"));
                votesList.add(getVotesAsText(topic, "-"));
                result.add(votesList);
            }
            Set<Integer> processedIndices = new HashSet<>();
            for(int i = 0; i < result.size(); i++){
                if(processedIndices.contains(i)){
                    continue;
                }
                List<String> topicVotes = result.get(i);
                String header = "По вопросам " + (i + 1);
                for(int j = i + 1; j < result.size(); j++){
                    if(processedIndices.contains(j)){
                        continue;
                    }
                    if(result.get(i).get(0).equalsIgnoreCase(result.get(j).get(0)) &&
                            result.get(i).get(1).equalsIgnoreCase(result.get(j).get(1)) &&
                            result.get(i).get(2).equalsIgnoreCase(result.get(j).get(2))){
                        header += ", " + (j + 1);
                        processedIndices.add(j);
                    }
                }
                String[] combinedTopicVote = {header, result.get(i).get(0), result.get(i).get(1), result.get(i).get(2)};
                combinedVotes.add(combinedTopicVote);
            }
        }
        return combinedVotes;
    }

    private String getVotesAsText(ICMeetingTopicDto topic,String vote){
        String votes ="";
        List<ICMeetingTopicsVoteDto> topicVotes = topic.getVotes();
        if(topicVotes != null){
            int voteCount = 0;
            for(ICMeetingTopicsVoteDto topicsVoteDto: topicVotes){
                if(topicsVoteDto.getVote() != null && topicsVoteDto.getVote().equalsIgnoreCase(vote)){
                    if(votes.length() > 0){
                        votes += ", " + topicsVoteDto.getEmployee().getFullNameInitialsRu();
                    }else{
                        votes += topicsVoteDto.getEmployee().getFullNameInitialsRu();
                    }
                    voteCount++;
                }
            }
            if(voteCount == 0){
                return "0";
            }else {
                return voteCount + " (" + votes + ")";
            }
        }
        return "";
    }

    private void updateICMeetingProtocolVoting(XWPFDocument document, List<ICMeetingTopicDto> topics){
        if(topics != null && !topics.isEmpty()){
            List<XWPFParagraph> paragraphs = document.getParagraphs();
            int index = 0;
            int decisionsIndex = 0;
            for(XWPFParagraph paragraph: paragraphs) {
                if(paragraph.getText().startsWith(IC_PROTOCOL_DECISION_PLACEHOLDER)){
                    decisionsIndex = index;
                }
                if(paragraph.getText().startsWith(IC_PROTOCOL_VOTING_PLACEHOLDER)){
                    break;
                }
                index++;
            }

            // Insert decisions
            List<String> values = new ArrayList<>();
            topics.forEach(topic->{
                values.add(topic.getPublishedDecisionUpd());
            });
            insertNumberedList(document, decisionsIndex, values);
            index += values.size();

            List<String[]> votesCombined = getVotesCombined(topics);

            for(int i = 0; i < votesCombined.size(); i++){
                // insert header
                XmlCursor cursor = paragraphs.get(index + 1).getCTP().newCursor();
                XWPFParagraph headerParagraph = document.insertNewParagraph(cursor);
                headerParagraph.setSpacingAfter(0);
                XWPFRun run = headerParagraph.createRun();
                run.setFontFamily(FONT_ARIAL);
                run.setFontSize(FONT_SIZE);
                run.setItalic(true);
                run.setText(votesCombined.get(i)[0]);
                index++;

                // insert vote results
                insertProtocolVotingTableRows(document, index + 1, i + 1, votesCombined.get(i));
                //index++;
            }
        }

        int tableTemplatePosition = document.getPosOfTable(document.getTableArray(0));
        document.removeBodyElement(tableTemplatePosition);

        removePlaceholders(document, IC_PROTOCOL_VOTING_PLACEHOLDER);
        removePlaceholders(document, IC_PROTOCOL_DECISION_PLACEHOLDER);
    }

    private void removePlaceholders(XWPFDocument document, String placeholder){
        List<XWPFParagraph> paragraphs = document.getParagraphs();
        int paragraphCount = paragraphs.size();
        for(int i = 0; i < paragraphCount; i++){
            XWPFParagraph paragraph = paragraphs.get(i);
            if(paragraph.getText().startsWith(placeholder)){
                int pPos = document.getPosOfParagraph(paragraph);
                document.removeBodyElement(pPos);
                i--;
                paragraphCount--;
            }
        }
    }

    @Override
    public FilesDto getICMeetingProtocolFileStream(Long icMeetingId, String username){

        FilesDto filesDto = new FilesDto();
        ICMeetingDto icMeeting = getICMeeting(icMeetingId, username);

        final String exportFileTemplatePath = "export_template/corp_meetings/IC_PROTOCOL_TEMPLATE.docx";
        Resource resource = new ClassPathResource(exportFileTemplatePath);
        InputStream wordFileToRead = null;
        try {
            wordFileToRead = resource.getInputStream();
        } catch (IOException e) {
            logger.error("IC Meeting: Export file template not found: '" + exportFileTemplatePath + "'");
            e.printStackTrace();
            return null;
        }

        try {
            XWPFDocument document = new XWPFDocument(wordFileToRead);
            List<XWPFParagraph> paragraphs = document.getParagraphs();
            document.createNumbering();
            if(paragraphs != null){
                // update general info
                updateICMeetingProtocolGeneralInfo(paragraphs, icMeeting);

                // update list of IC Attendees
                List<EmployeeDto> attendees = new ArrayList<>();
                List<EmployeeDto> absentees = new ArrayList<>();
                if(icMeeting.getAttendees() != null){
                    icMeeting.getAttendees().forEach((attendeesDto)->{
                        if(attendeesDto.isPresent()){
                            attendees.add(attendeesDto.getEmployee());
                        }else{
                            absentees.add(attendeesDto.getEmployee());
                        }
                    });
                }
                updateICMeetingProtocolAttendees(document, attendees);
                updateICMeetingProtocolAbsentees(document, absentees);

                // update list of IC Invitees
                updateICMeetingProtocolInvitees(document, icMeeting.getInvitees());

                // update list of questions
                updateICMeetingProtocolTopics(document, icMeeting.getTopics());
                updateICMeetingProtocolTopicsMaterials(document, icMeeting.getTopics());

                //discussions
                updateICMeetingProtocolTopicsDiscussions(document, icMeeting.getTopics());

                // voting
                updateICMeetingProtocolVoting(document, icMeeting.getTopics());

                File tmpDir = new File(this.rootDirectory + "/tmp/corp_meetings");

                // write to new
                String filePath = tmpDir + "/IC_PROTOCOL_" + MathUtils.getRandomNumber(0, 10000) + ".docx";
                try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                    document.write(outputStream);
                }

                InputStream inputStream = new FileInputStream(filePath);
                filesDto.setInputStream(inputStream);
                filesDto.setFileName(filePath);
                return filesDto;
            }

        } catch (IOException e) {
            logger.error("IO Exception when exporting IC Meeting Protocol", e);
            //e.printStackTrace();
        }

        return null;
    }

    @Override
    public FilesDto getICMeetingBulletinFileStream(Long icMeetingId, String username){

        FilesDto filesDto = new FilesDto();
        ICMeetingDto icMeeting = getICMeeting(icMeetingId, username);

        final String exportFileTemplatePath = "export_template/corp_meetings/IC_BULLETIN_TEMPLATE.docx";
        Resource resource = new ClassPathResource(exportFileTemplatePath);
        InputStream wordFileToRead = null;
        try {
            wordFileToRead = resource.getInputStream();
        } catch (IOException e) {
            logger.error("IC Meeting: Export file template not found: '" + exportFileTemplatePath + "'");
            e.printStackTrace();
            return null;
        }

        try {
            XWPFDocument document = new XWPFDocument(wordFileToRead);
            List<XWPFParagraph> paragraphs = document.getParagraphs();
            document.createNumbering();
            if(paragraphs != null){
                EmployeeDto employeeDto = this.employeeService.findByUsername(username);
                if(employeeDto == null){
                    logger.error("IO Exception when exporting IC Meeting Bulletin: user not found "+ username);
                    return null;
                }
                String name = employeeDto.getFullNamePossessiveInitialsRu();
                updateICMeetingBulletin(document, icMeeting.getTopics(), name);

                File tmpDir = new File(this.rootDirectory + "/tmp/corp_meetings");

                // write to new
                String filePath = tmpDir + "/IC_BULLETIN_" + MathUtils.getRandomNumber(0, 10000) + ".docx";
                try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                    document.write(outputStream);
                }

                InputStream inputStream = new FileInputStream(filePath);
                filesDto.setInputStream(inputStream);
                filesDto.setFileName(filePath);
                return filesDto;
            }

        } catch (IOException e) {
            logger.error("IO Exception when exporting IC Meeting Bulletin", e);
            //e.printStackTrace();
        }

        return null;
    }

    private void updateICMeetingProtocolGeneralInfo(List<XWPFParagraph> paragraphs, ICMeetingDto icMeeting){
        for(XWPFParagraph paragraph: paragraphs){
            List<XWPFRun> runs = paragraph.getRuns();
            if (runs != null) {
                for (XWPFRun r : runs) {
                    String text = r.getText(0);
                    if (text != null && text.contains("DATEIC")) {
                        if(icMeeting.getDate() != null) {
                            text = text.replace("DATEIC", DateUtils.getDateRussianTextualDate(icMeeting.getDate()));
                        }else{
                            text = text.replace("DATEIC", "");
                        }
                        r.setText(text, 0);
                    }else if (text != null && text.contains("NUMIC")) {
                        if(icMeeting.getTime() != null) {
                            text = text.replace("NUMIC", icMeeting.getNumber());
                        }else{
                            text = text.replace("NUMIC", "");
                        }
                        r.setText(text, 0);
                    }
                }
            }
        }
    }

    @Deprecated
    private int populateICMeetingProtocolAttendeesPlaceholders(XWPFDocument document, int attendeesSize,
                                                               String attendeePlaceholder, String startPlaceholder){
        int insertIndex = 0;
        if(attendeesSize > 1) { // one placeholder already exists in the template
            List<XWPFParagraph> paragraphs = document.getParagraphs();
            int paragraphCount = paragraphs.size();
            boolean attendeesBlock = false;
            for (int i = 0; i < paragraphCount && attendeesSize > 0; i++) {
                XWPFParagraph paragraph = paragraphs.get(i);
                if (attendeesBlock) {
                    if (paragraph.getNumID() != null) {
                        if (paragraph.getText().startsWith(attendeePlaceholder)) {
                            insertIndex = i;
                            break;
                        }
                    }
                } else if (paragraph.getText().startsWith(startPlaceholder)) { // TODO: Refactor?
                    attendeesBlock = true;
                }
            }
            if(insertIndex > 0){
                if (insertIndex + 1 < paragraphCount) {
                    int added = 1;
                    while (added < attendeesSize) {
                        XmlCursor cursor = paragraphs.get(insertIndex + 1).getCTP().newCursor();
                        XWPFParagraph newParagraph = document.insertNewParagraph(cursor);
                        CTPPr pPr = newParagraph.getCTP().isSetPPr() ? newParagraph.getCTP().getPPr() : newParagraph.getCTP().addNewPPr();
                        pPr.set(paragraphs.get(insertIndex).getCTP().getPPr());
                        for (XWPFRun r : paragraphs.get(insertIndex).getRuns()) {
                            XWPFRun nr = newParagraph.createRun();
                            CTRPr rPr = nr.getCTR().isSetRPr() ? nr.getCTR().getRPr() : nr.getCTR().addNewRPr();
                            rPr.set(r.getCTR().getRPr());
                            nr.setText(attendeePlaceholder);
                        }
                        added++;
                    }
                }
            }
        }
        return insertIndex;
    }
    @Deprecated
    private void updateICMeetingProtocolAttendees_(XWPFDocument document, List<ICMeetingAttendeesDto> attendees){

        List<EmployeeDto> attending = new ArrayList<>();
        List<EmployeeDto> absent = new ArrayList<>();
        if(attendees != null){
            attendees.forEach((attendee ->{
                if(attendee.isPresent()){
                    attending.add(attendee.getEmployee());
                }else{
                    absent.add(attendee.getEmployee());
                }
            }));
        }

        List<XWPFParagraph> paragraphs = document.getParagraphs();

        int attendingParagraphIndex = populateICMeetingProtocolAttendeesPlaceholders(document,
                attending.size(), IC_PROTOCOL_ATTENDEE_PLACEHOLDER, IC_PROTOCOL_ATTENDEE_START_PLACEHOLDER);
        int attendeeIndex = 0;
        int updated = 0;
        while(attending != null && updated != attending.size()){
            XWPFParagraph paragraph = paragraphs.get(attendingParagraphIndex);
            if (paragraph.getNumID() != null) {
                List<XWPFRun> runs = paragraph.getRuns();
                if (runs != null && !runs.isEmpty()) {
                    XWPFRun r = runs.get(0);
                    String text = r.getText(0);
                    if (attendeeIndex < attending.size()) {
                        String fullNameInitials = attending.get(attendeeIndex) != null &&
                                attending.get(attendeeIndex).getFullNameInitialsRu() != null ? attending.get(attendeeIndex).getFullNameInitialsRu() : null;
                        fullNameInitials = fullNameInitials == null ? attending.get(attendeeIndex).getLastName() : fullNameInitials;

                        String position = attending.get(attendeeIndex) != null &&
                                attending.get(attendeeIndex).getFullPositionRu() != null ? attending.get(attendeeIndex).getFullPositionRu() : null;
                        if(fullNameInitials != null) {
                            text = text.startsWith(IC_PROTOCOL_ATTENDEE_PLACEHOLDER) ?
                                    text.replace(IC_PROTOCOL_ATTENDEE_PLACEHOLDER, fullNameInitials + (position != null ? "/" + position: "")) :
                                    fullNameInitials + (position != null ? "/" + position: "");
                            r.setText(text, 0);
                        }
                        attendeeIndex++;
                        attendingParagraphIndex++;
                        updated++;
                    }
                }
            }
        }


        int absentParagraphIndex = populateICMeetingProtocolAttendeesPlaceholders(document, absent.size(),
                IC_PROTOCOL_ABSENTEE_PLACEHOLDER, IC_PROTOCOL_ABSENTEE_START_PLACEHOLDER);
        paragraphs = document.getParagraphs();
        int absenteeIndex = 0;
        updated = 0;
        while (absent != null && updated != absent.size()) {
            XWPFParagraph paragraph = paragraphs.get(absentParagraphIndex);
            if (paragraph.getNumID() != null) {
                List<XWPFRun> runs = paragraph.getRuns();
                if (runs != null && !runs.isEmpty()) {
                    XWPFRun r = runs.get(0);
                    String text = r.getText(0);
                    if (absenteeIndex < absent.size()) {
                        String fullNameInitials = absent.get(absenteeIndex) != null &&
                                absent.get(absenteeIndex).getFullNameInitialsRu() != null ? absent.get(absenteeIndex).getFullNameInitialsRu() : null;
                        fullNameInitials = fullNameInitials == null ? absent.get(absenteeIndex).getLastName() : fullNameInitials;

                        String position = absent.get(absenteeIndex) != null &&
                                absent.get(absenteeIndex).getFullPositionRu() != null ? absent.get(absenteeIndex).getFullPositionRu() : null;
                        if (fullNameInitials != null) {
                            text = text.startsWith(IC_PROTOCOL_ABSENTEE_PLACEHOLDER) ?
                                    text.replace(IC_PROTOCOL_ABSENTEE_PLACEHOLDER, fullNameInitials + (position != null ? "/" + position : "")) :
                                    fullNameInitials + (position != null ? "/" + position : "");
                            r.setText(text, 0);
                        }
                        absenteeIndex++;
                        absentParagraphIndex++;
                        updated++;
                    }
                }
            }
        }

        // Remove placeholders
        paragraphs = document.getParagraphs();
        int paragraphCount = paragraphs.size();
        for(int i = 0; i < paragraphCount; i++){
            XWPFParagraph paragraph = paragraphs.get(i);
            if(paragraph.getNumID() != null && (paragraph.getText().startsWith(IC_PROTOCOL_ATTENDEE_PLACEHOLDER) ||
                    paragraph.getText().startsWith(IC_PROTOCOL_ABSENTEE_PLACEHOLDER))){
                XWPFDocument doc = paragraph.getDocument();
                int pPos = doc.getPosOfParagraph(paragraph);
                doc.removeBodyElement(pPos);
                i--;
                paragraphCount--;
            }
        }
    }

    @Deprecated
    private int populateICMeetingProtocolInviteesPlaceholders(XWPFDocument document, int inviteesSize){
        int insertIndex = 0;
        if(inviteesSize > 1) { // one placeholder already exists in the template
            List<XWPFParagraph> paragraphs = document.getParagraphs();
            int paragraphCount = paragraphs.size();
            boolean inviteesBlock = false;
            for (int i = 0; i < paragraphCount && inviteesSize > 0; i++) {
                XWPFParagraph paragraph = paragraphs.get(i);
                if (inviteesBlock) {
                    if (paragraph.getNumID() != null) {
                        if (paragraph.getText().startsWith(IC_PROTOCOL_INVITEE_PLACEHOLDER)) {
                            insertIndex = i;
                            break;
                        }
                    }
                } else if (paragraph.getText().startsWith(IC_PROTOCOL_INVITEE_START_PLACEHOLDER)) {
                    inviteesBlock = true;
                }
            }
            if(insertIndex > 0){
                if (insertIndex + 1 < paragraphCount) {
                    int added = 1;
                    while (added < inviteesSize) {
                        XmlCursor cursor = paragraphs.get(insertIndex + 1).getCTP().newCursor();
                        XWPFParagraph newParagraph = document.insertNewParagraph(cursor);
                        CTPPr pPr = newParagraph.getCTP().isSetPPr() ? newParagraph.getCTP().getPPr() : newParagraph.getCTP().addNewPPr();
                        pPr.set(paragraphs.get(insertIndex).getCTP().getPPr());
                        for (XWPFRun r : paragraphs.get(insertIndex).getRuns()) {
                            XWPFRun nr = newParagraph.createRun();
                            CTRPr rPr = nr.getCTR().isSetRPr() ? nr.getCTR().getRPr() : nr.getCTR().addNewRPr();
                            rPr.set(r.getCTR().getRPr());
                            nr.setText(IC_PROTOCOL_INVITEE_PLACEHOLDER);
                        }
                        added++;
                    }
                }
            }
        }
        return insertIndex;
    }

    @Deprecated
    private void updateICMeetingProtocolInvitees_(XWPFDocument document, List<EmployeeDto> invitees){

        if(invitees != null && !invitees.isEmpty()) {
            int insertIndex = populateICMeetingProtocolInviteesPlaceholders(document, invitees.size());

            List<XWPFParagraph> paragraphs = document.getParagraphs();
            int inviteeIndex = 0;
            int updated = 0;
            int j = insertIndex;
            while (invitees != null && updated != invitees.size()) {
                XWPFParagraph paragraph = paragraphs.get(j);
                if (paragraph.getNumID() != null) {
                    List<XWPFRun> runs = paragraph.getRuns();
                    if (runs != null && !runs.isEmpty()) {
                        XWPFRun r = runs.get(0);
                        String text = r.getText(0);
                        if (inviteeIndex < invitees.size()) {
                            String fullNameInitials = invitees.get(inviteeIndex) != null &&
                                    invitees.get(inviteeIndex).getFullNameInitialsRu() != null ? invitees.get(inviteeIndex).getFullNameInitialsRu() : null;
                            fullNameInitials = fullNameInitials == null ? invitees.get(inviteeIndex).getLastName() : fullNameInitials;

                            String position = invitees.get(inviteeIndex) != null &&
                                    invitees.get(inviteeIndex).getFullPositionRu() != null ? invitees.get(inviteeIndex).getFullPositionRu() : null;
                            if (fullNameInitials != null) {
                                text = text.startsWith(IC_PROTOCOL_INVITEE_PLACEHOLDER) ?
                                        text.replace(IC_PROTOCOL_INVITEE_PLACEHOLDER, fullNameInitials + (position != null ? "/" + position : "")) :
                                        fullNameInitials + (position != null ? "/" + position : "");
                                r.setText(text, 0);
                            }
                            inviteeIndex++;
                            j++;
                            updated++;
                        }

                    }
                }
            }
        }else{
            // Remove placeholder when there no invitees
            List<XWPFParagraph> paragraphs = document.getParagraphs();
            int paragraphCount = paragraphs.size();
            for(int i = 0; i < paragraphCount; i++){
                XWPFParagraph paragraph = paragraphs.get(i);
                if(paragraph.getNumID() != null && paragraph.getText().startsWith(IC_PROTOCOL_INVITEE_PLACEHOLDER)){
                    XWPFDocument doc = paragraph.getDocument();
                    int pPos = doc.getPosOfParagraph(paragraph);
                    doc.removeBodyElement(pPos);
                    i--;
                    paragraphCount--;
                }
            }
        }
    }

    @Deprecated
    private int populateICMeetingProtocolQuestionsPlaceholders(XWPFDocument document, List<ICMeetingTopicDto> questions){
        int insertIndex = 0;
        if(questions != null &&questions.size() > 1) { // one placeholder already exists in the template
            List<XWPFParagraph> paragraphs = document.getParagraphs();
            int paragraphCount = paragraphs.size();
            boolean questionsBlock = false;
            for (int i = 0; i < paragraphCount && questions != null && !questions.isEmpty(); i++) {
                XWPFParagraph paragraph = paragraphs.get(i);
                if (questionsBlock) {
                    if (paragraph.getNumID() != null) {
                        if (paragraph.getText().startsWith(IC_PROTOCOL_QUESTION_PLACEHOLDER)) {
                            insertIndex = i;
                            break;
                        }
                    }
                } else if (paragraph.getText().startsWith(IC_PROTOCOL_QUESTION_START_PLACEHOLDER)) { // TODO: Refactor?
                    questionsBlock = true;
                }
            }
            if(insertIndex > 0){
                if (insertIndex + 1 < paragraphCount) {
                    int added = 1;
                    while (added < questions.size()) {
                        XmlCursor cursor = paragraphs.get(insertIndex + 1).getCTP().newCursor();
                        XWPFParagraph newParagraph = document.insertNewParagraph(cursor);
                        CTPPr pPr = newParagraph.getCTP().isSetPPr() ? newParagraph.getCTP().getPPr() : newParagraph.getCTP().addNewPPr();
                        pPr.set(paragraphs.get(insertIndex).getCTP().getPPr());
                        for (XWPFRun r : paragraphs.get(insertIndex).getRuns()) {
                            XWPFRun nr = newParagraph.createRun();
                            CTRPr rPr = nr.getCTR().isSetRPr() ? nr.getCTR().getRPr() : nr.getCTR().addNewRPr();
                            rPr.set(r.getCTR().getRPr());
                            nr.setText(IC_PROTOCOL_QUESTION_PLACEHOLDER); // TODO: remove unnecessary
                        }
                        added++;
                    }
                }
            }
        }
        return insertIndex;
    }

    @Deprecated
    private void updateICMeetingProtocolQuestions_(XWPFDocument document, List<ICMeetingTopicDto> questions){
        if(questions != null && !questions.isEmpty()) {
            int insertIndex = populateICMeetingProtocolQuestionsPlaceholders(document, questions);

            List<XWPFParagraph> paragraphs = document.getParagraphs();
            int questionIndex = 0;
            int updated = 0;
            int j = insertIndex;
            while (questions != null && updated != questions.size()) {
                XWPFParagraph paragraph = paragraphs.get(j);
                if (paragraph.getNumID() != null) {
                    List<XWPFRun> runs = paragraph.getRuns();
                    if (runs != null && !runs.isEmpty()) {
                        XWPFRun r = runs.get(0);
                        String text = r.getText(0);
                        if (questionIndex < questions.size()) {
                            String questionName = questions.get(questionIndex).getPublishedNameUpd() != null ? questions.get(questionIndex).getPublishedNameUpd() : "";
                            if (text.startsWith(IC_PROTOCOL_QUESTION_PLACEHOLDER)) {
                                text = text.replace(IC_PROTOCOL_QUESTION_PLACEHOLDER, questionName);
                                r.setText(text, 0);
                            } else {
                                r.setText(questionName, 0);
                            }
                            questionIndex++;
                            j++;
                            updated++;
                        }
                    }
                }
            }
        }else{
            // Remove placeholder when there are no questions
            List<XWPFParagraph> paragraphs = document.getParagraphs();
            int paragraphCount = paragraphs.size();
            for(int i = 0; i < paragraphCount; i++){
                XWPFParagraph paragraph = paragraphs.get(i);
                if(paragraph.getNumID() != null && paragraph.getText().startsWith(IC_PROTOCOL_QUESTION_PLACEHOLDER)){
                    XWPFDocument doc = paragraph.getDocument();
                    int pPos = doc.getPosOfParagraph(paragraph);
                    doc.removeBodyElement(pPos);
                    i--;
                    paragraphCount--;
                }
            }
        }
    }

    @Deprecated
    private int populateICMeetingProtocolMaterialsPlaceholders_(XWPFDocument document, List<ICMeetingTopicDto> questions){

        int insertIndex = 0;
        if(questions != null && !questions.isEmpty()) { // one placeholder already exists in the template
            List<XWPFParagraph> paragraphs = document.getParagraphs();
            int paragraphCount = paragraphs.size();
            for (int i = 0; i < paragraphCount && questions != null && !questions.isEmpty(); i++) {
                XWPFParagraph paragraph = paragraphs.get(i);
                if (paragraph.getText().startsWith(IC_PROTOCOL_QUESTION_MATERIAL_HEADER_PLACEHOLDER)) {
                    insertIndex = i;
                    break;
                }
            }
            if(insertIndex > 0){
                if (insertIndex + 2 < paragraphCount) {
                    for (int i = questions.size() - 1; i >= 0 ; i--) {
                        int materials = questions.get(i).getMaterialsCount();
                        if(materials == 0){
                            continue;
                        }
                        XmlCursor cursor = paragraphs.get(insertIndex + 2).getCTP().newCursor();
                        XWPFParagraph newParagraph = document.insertNewParagraph(cursor);
                        CTPPr pPr = newParagraph.getCTP().isSetPPr() ? newParagraph.getCTP().getPPr() : newParagraph.getCTP().addNewPPr();
                        pPr.set(paragraphs.get(insertIndex).getCTP().getPPr());
                        for (XWPFRun r : paragraphs.get(insertIndex).getRuns()) {
                            XWPFRun nr = newParagraph.createRun();
                            CTRPr rPr = nr.getCTR().isSetRPr() ? nr.getCTR().getRPr() : nr.getCTR().addNewRPr();
                            rPr.set(r.getCTR().getRPr());
                            nr.setText(IC_PROTOCOL_QUESTION_MATERIAL_HEADER_PLACEHOLDER);
                        }

                        int addedMaterial = 0;
                        while(materials > 0) {
                            XmlCursor cursor2 = paragraphs.get(insertIndex + 3 + addedMaterial).getCTP().newCursor();
                            XWPFParagraph newParagraph2 = document.insertNewParagraph(cursor2);

                            CTPPr pPr2 = newParagraph2.getCTP().isSetPPr() ? newParagraph2.getCTP().getPPr() : newParagraph2.getCTP().addNewPPr();
                            pPr2.set(paragraphs.get(insertIndex + 1).getCTP().getPPr());
                            for (XWPFRun r : paragraphs.get(insertIndex + 1).getRuns()) {
                                XWPFRun nr = newParagraph2.createRun();
                                CTRPr rPr = nr.getCTR().isSetRPr() ? nr.getCTR().getRPr() : nr.getCTR().addNewRPr();
                                rPr.set(r.getCTR().getRPr());
                                nr.setText(IC_PROTOCOL_QUESTION_MATERIAL_PLACEHOLDER);
                            }
                            materials--;
                            addedMaterial++;
                        }
                        //added++;
                    }
                }
            }
        }
        return insertIndex;
    }

    private String getMaterialsHeader(int number){
        return "Перечень документов, предоставленных по " + getNumberRussian(number) + " вопросу:";
    }

    private String getNumberRussian(int number){
        // TODO: refactor
        if(number == 1){
            return "первому";
        }else if(number == 2){
            return "второму";
        }else if(number == 3){
            return "третьему";
        }else if(number == 4){
            return "чевертому";
        }else if(number == 5){
            return "пятому";
        }else if(number == 6){
            return "шестому";
        }else if(number == 7){
            return "седьмому";
        }else if(number == 8){
            return "восьмому";
        }else if(number == 9){
            return "девятому";
        }else if(number == 10){
            return "десятому";
        }else{
            return number + "";
        }
    }

    private String getDiscussionsHeader(String speakerName, String topicName){
        String header = "выступил ";
        if(speakerName != null ){
            header +=  speakerName + ", который предоставил Комитету информацию ";
            if(topicName != null){
                header +=  topicName;
            }
        }
        return header;
    }

    private String getDecisionHeader(int number){
        return "Комитет по " + getNumberRussian(number) + " вопросу поставновил:";
    }

    @Deprecated
    private void updateICMeetingProtocolMaterials_(XWPFDocument document, List<ICMeetingTopicDto> questions){
        if(questions != null && !questions.isEmpty()) {
            int insertIndex = populateICMeetingProtocolMaterialsPlaceholders_(document, questions);

            List<XWPFParagraph> paragraphs = document.getParagraphs();
            int j = insertIndex + 2;
            for (int i = 0;  questions != null && i < questions.size(); i++) {
                if(questions.get(i).getMaterialsCount() == 0){
                    continue;
                }
                XWPFParagraph paragraph = paragraphs.get(j);
                List<XWPFRun> runs = paragraph.getRuns();
                if (runs != null && !runs.isEmpty()) {
                    XWPFRun r = runs.get(0);
                    String text = r.getText(0);
                    String materialsHeader = getMaterialsHeader(i + 1);
                    if (text.startsWith(IC_PROTOCOL_QUESTION_MATERIAL_HEADER_PLACEHOLDER)) {
                        text = text.replace(IC_PROTOCOL_QUESTION_MATERIAL_HEADER_PLACEHOLDER, materialsHeader);
                        r.setText(text, 0);
                    } else {
                        r.setText(materialsHeader, 0);
                    }
                    j++;


                    int materialsIndex = 0;
                    while(materialsIndex < questions.get(i).getMaterials().size()){
                        XWPFParagraph paragraphMaterials = paragraphs.get(j);
                        if(paragraphMaterials.getNumID() !=null) {
                            List<XWPFRun> runsMaterials = paragraphMaterials.getRuns();
                            if (runsMaterials != null && !runsMaterials.isEmpty()) {
                                XWPFRun rMaterials = runsMaterials.get(0);
                                String textMaterials = rMaterials.getText(0);
                                textMaterials = textMaterials.replace(IC_PROTOCOL_QUESTION_MATERIAL_PLACEHOLDER, questions.get(i).getMaterials().get(materialsIndex).getName());
                                rMaterials.setText(textMaterials, 0);
                                j++;
                            }
                        }else{
                            break;
                        }
                        materialsIndex++;
                    }
                    //questionIndex++;
                    //updated++;

                }
            }
        }
        // Remove placeholder when there are no materials
        List<XWPFParagraph> paragraphs = document.getParagraphs();
        int paragraphCount = paragraphs.size();
        for(int i = 0; i < paragraphCount; i++){
            XWPFParagraph paragraph = paragraphs.get(i);
            if(paragraph.getText().startsWith(IC_PROTOCOL_QUESTION_MATERIAL_HEADER_PLACEHOLDER) ||
                    (paragraph.getNumID() != null && paragraph.getText().startsWith(IC_PROTOCOL_QUESTION_MATERIAL_PLACEHOLDER))){
                XWPFDocument doc = paragraph.getDocument();
                int pPos = doc.getPosOfParagraph(paragraph);
                doc.removeBodyElement(pPos);
                i--;
                paragraphCount--;
            }
        }
    }

    @Override
    public boolean unlockICMeetingForFinalize(Long id, String username){
        if(id != null){
            ICMeetingDto dto = getICMeeting(id, username);
            if(checkICMeetingUnlockableForFinalize(dto, username)){
                ICMeeting entity = this.icMeetingsRepository.findOne(id);
                entity.setUnlockedForFinalize(true);
                this.icMeetingsRepository.save(entity);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean saveICMeetingVotes(ICMeetingVoteDto votes, String username){
        if(votes != null && votes.getIcMeetingId() != null){
            ICMeetingDto dto = getICMeeting( votes.getIcMeetingId(), username);
            if(dto != null && checkICMeetingCanVote(dto, username)){
                EmployeeDto employeeDto = this.employeeService.findByUsername(username);
                if(employeeDto != null) {
                    //List<ICMeetingVote> voteEntities = new ArrayList<>();
                    for(ICMeetingTopicsVoteDto voteDto: votes.getVotes()) {
                        ICMeetingVote vote = new ICMeetingVote();
                        vote.setEmployee(new Employee(employeeDto.getId()));
                        vote.setIcMeetingTopic(new ICMeetingTopic(voteDto.getIcMeetingTopicId()));
                        ICMeetingVoteType voteType = this.lookupService.findByTypeAndCode(ICMeetingVoteType.class, voteDto.getVote());
                        if(voteType != null) {
                            vote.setVote(voteType);
                            //voteEntities.add(vote);
                        }else{
                            return false;
                        }
                        this.icMeetingVoteRepository.deleteByTopicIdAndUserId(voteDto.getIcMeetingTopicId(), employeeDto.getId());
                        this.icMeetingVoteRepository.save(vote);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean closeICMeeting(Long id, String username) {
        try {
            ICMeetingDto dto = getICMeeting(id, username);
            if(dto == null || (dto.getDeleted() != null && dto.getDeleted().booleanValue())){
                String errorMessage = "Error closing IC Meeting with id + " + id.longValue() + ": entity not closable";
                logger.error(errorMessage);
                return false;
            }
            ICMeeting entity = icMeetingsRepository.findOne(id);
            entity.setClosed(true);
            this.icMeetingsRepository.save(entity);
            logger.info("IC meeting closed with id=" + id.longValue() + " [user]=" + username);
            return true;
        }catch(Exception ex){
            logger.error("Error closing IC meeting: id=" + id.longValue() + ", ]user]=" + username, ex);
        }
        return false;
    }

    @Override
    public boolean reopenICMeeting(Long id, String username) {
        try {
            ICMeetingDto dto = getICMeeting(id, username);
            if(dto == null || (dto.getDeleted() != null && dto.getDeleted().booleanValue())){
                String errorMessage = "Error reopening IC Meeting with id + " + id.longValue() + ": entity not closable";
                logger.error(errorMessage);
                return false;
            }
            ICMeeting entity = icMeetingsRepository.findOne(id);
            entity.setClosed(false);
            this.icMeetingsRepository.save(entity);
            logger.info("IC meeting reopened with id=" + id.longValue() + " [user]=" + username);
            return true;
        }catch(Exception ex){
            logger.error("Error reopneing IC meeting: id=" + id.longValue() + ", ]user]=" + username, ex);
        }
        return false;
    }


}
