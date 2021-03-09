package kz.nicnbk.service.impl.corpmeetings;

import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.common.service.util.*;
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
import kz.nicnbk.service.api.notification.NotificationService;
import kz.nicnbk.service.api.tag.TagService;
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
import kz.nicnbk.service.dto.notification.NotificationDto;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
    public static final String IC_PROTOCOL_ICADMIN_PLACEHOLDER = "ICADMIN";

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

    //@Autowired
    //private ICMeetingTopicTagsRepository topicTagsRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private TagService tagService;

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

    private EntitySaveResponseDto checkICMeetingTopicRequiredFields(ICMeetingTopicDto dto){
        EntitySaveResponseDto saveResponseDto = new EntitySaveResponseDto();
        if(StringUtils.isEmpty(dto.getName())){
            String errorMessage = "Error saving IC Meeting Topic: name required";
            logger.error(errorMessage);
            saveResponseDto.setErrorMessageEn(errorMessage);
            return saveResponseDto;
        }
        if(StringUtils.isEmpty(dto.getDecision())){
            String errorMessage = "Error saving IC Meeting Topic: name decision";
            logger.error(errorMessage);
            saveResponseDto.setErrorMessageEn(errorMessage);
            return saveResponseDto;
        }
        if(dto.getIcMeeting() != null && dto.getIcMeeting().getId() != null){
            if(dto.getSpeaker() == null){
                String errorMessage = "Error saving IC meeting topic: When IC Meeting selected, Speaker is required";
                logger.error(errorMessage);
                saveResponseDto = new EntitySaveResponseDto();
                saveResponseDto.setErrorMessageEn(errorMessage);
                return saveResponseDto;
            }
        }
        saveResponseDto.setStatus(ResponseStatusType.SUCCESS);
        return saveResponseDto;
    }

    /* IC MEETING TOPIC ***********************************************************************************************/
    //@Transactional
    @Override
    public EntitySaveResponseDto saveICMeetingTopic(ICMeetingTopicDto dto, FilesDto explanatoryNote,
                                                    List<FilesDto> filesDtoSet, String updater) {
        try {
            // Check required fields
            EntitySaveResponseDto saveResponseDto = checkICMeetingTopicRequiredFields(dto);
            if(!saveResponseDto.isStatusOK()){
                return saveResponseDto;
            }

            ICMeetingTopic entity = icMeetingTopicEntityConverter.assemble(dto);
            boolean resetApprovals = false;
            Set<EmployeeApproveDto> addedNewApproves = new HashSet<>();
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
                boolean icOrderReset = entity.getIcMeeting() == null || (entity.getIcMeeting() != null && currentEntity.getIcMeeting() != null &&
                        entity.getIcMeeting().getId().longValue() != currentEntity.getIcMeeting().getId().longValue());
                if(icOrderReset){
                    entity.setIcOrder(null);
                }
                entity.setUpdateDate(new Date());
                EmployeeDto updatedby = this.employeeService.findByUsername(updater);
                entity.setUpdater(new Employee(updatedby.getId()));

                if(!dto.isToPublish() && currentEntity.getPublished() != null && currentEntity.getPublished().booleanValue()){
                    resetApprovals = true;
                }
                // check if new employees added to approve list
                if(dto.getApproveList() != null && !dto.getApproveList().isEmpty()){
                    for(EmployeeApproveDto approveDto: dto.getApproveList()){
                        boolean found = false;
                        if(currentEntity.getApproveList() != null && !currentEntity.getApproveList().isEmpty()) {
                            for (ICMeetingTopicApproval approval : currentEntity.getApproveList()) {
                                if (approval.getEmployee() != null && approveDto.getEmployee() != null &&
                                        approval.getEmployee().getId() != null && approveDto.getEmployee().getId() != null &&
                                        approval.getEmployee().getId().longValue() == approveDto.getEmployee().getId().longValue()) {
                                    found = true;
                                    break;
                                }
                            }
                        }
                        if(!found){
                            addedNewApproves.add(approveDto);
                        }
                    }
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

            if(entity.getIcMeeting().getUnlockedForFinalize() != null && entity.getIcMeeting().getUnlockedForFinalize().booleanValue()){
                // Unlocked for finalize
                entity.setPublishedUpd(true);
            }

            if(entity.getIcMeeting() != null){
                ICMeetingDto icMeetingDto = getICMeeting(entity.getIcMeeting().getId());
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
                    if(!StringUtils.isEqualWithoutSpaces(existingDecision.trim(), currentDecision.trim())){
                        resetApprovals = true;
                    }else {
                        String existingName = existingEntity.getName() != null ? existingEntity.getName() : "";
                        String currentName = dto.getName() != null ? dto.getName() : "";
                        if (!StringUtils.isEqualWithoutSpaces(existingName.trim(), currentName.trim())) {
                            resetApprovals = true;
                        }
                    }
                }
            };

            entity = icMeetingTopicRepository.save(entity);
            logger.info(dto.getId() == null ? "IC meeting topic created: " + entity.getId() + ", by " + entity.getCreator().getUsername() :
                    "IC meeting topic updated: " + entity.getId() + ", by " + updater);

            // TODO: Check Transactional behavior !!

            if((dto.getStatus() == null || !dto.getStatus().equalsIgnoreCase(ICMeetingTopicDto.FINALIZED)) &&
                    entity.getPublishedUpd() != null && entity.getPublishedUpd().booleanValue() &&
                    entity.getIcMeeting().getUnlockedForFinalize() != null && entity.getIcMeeting().getUnlockedForFinalize().booleanValue()){
                // Unlocked for finalize
                if(entity.getIcMeeting() != null && entity.getIcMeeting().getId() != null) {
                    sendNotificationsIfAllTopicsFinalized(entity.getIcMeeting().getId());
                }
            }

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
                boolean saved = saveICMeetingTopicAttachments(entity.getId(), uploadMaterials, /*false,*/ updater);
                if(!saved) {
                    String errorMessage = "Error saving IC meeting topic: failed to save attachment files";
                    logger.error(errorMessage);
                    saveResponseDto = new EntitySaveResponseDto();
                    saveResponseDto.setErrorMessageEn(errorMessage);
                    return saveResponseDto;
                }

                // RESET APPROVALS
                resetApprovals = true;
            }
            // uploaded/existing files renaming
            boolean saved = saveICMeetingTopicAttachments(entity.getId(),
                    (filesDtoSet != null && !filesDtoSet.isEmpty() ? existingMaterials : dto.getMaterials()), /*false,*/ updater);
            if(!saved) {
                String errorMessage = "Error saving IC meeting topic: failed to update attachment files";
                logger.error(errorMessage);
                saveResponseDto = new EntitySaveResponseDto();
                saveResponseDto.setErrorMessageEn(errorMessage);
                return saveResponseDto;
            }

            if(resetApprovals){
                resetICMeetingTopicApprovals(entity.getId());
                resetICMeetingTopicVoting(entity.getId());
            }

            // approve list notifications
            if(dto.getId() == null && dto.getApproveList() != null && !dto.getApproveList().isEmpty()){
                addedNewApproves = dto.getApproveList();
            }
            if(!addedNewApproves.isEmpty()){
                for(EmployeeApproveDto approveDto: addedNewApproves){
                    NotificationDto notificationDto = new NotificationDto();
                    notificationDto.setEmailName("IC Module: You have been added to approve list for topic '" + dto.getName() +
                            "' by " + updater + ". https://unic.nicnbk.kz/#/corpMeetings/edit/" + entity.getId().longValue());
                    notificationDto.setInAppName("IC Module: You have been added to approve list for topic '" + dto.getName() +
                            "' by " + updater);
                    notificationDto.setEmployee(approveDto.getEmployee());
                    this.notificationService.createInAppAndEmailNotification(notificationDto);
                }
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

    private void sendNotificationsIfAllTopicsFinalized(Long meetingId){
        if(meetingId == null){
            return;
        }
        ICMeetingDto icMeetingDto = getICMeeting(meetingId);
        if(icMeetingDto != null && icMeetingDto.getTopics() != null && !icMeetingDto.getTopics().isEmpty()){
            boolean finalized = true;
            for(ICMeetingTopicDto topicDto: icMeetingDto.getTopics()){
                if(topicDto.getStatus() == null || !topicDto.getStatus().equalsIgnoreCase(ICMeetingTopicDto.FINALIZED)){
                    // if not finalized
                    finalized = false;
                }
            }
            if(finalized){
                List<EmployeeDto> icMembers = this.employeeService.findICMembers();
                for(EmployeeDto icMember: icMembers){
                    NotificationDto notificationDto = new NotificationDto();
                    notificationDto.setEmailName("IC Module: All topics for IC #" + icMeetingDto.getNumber() + " (" + DateUtils.getDateFormatted(icMeetingDto.getDate()) + ") " +
                            " have been finalized. Please vote. https://unic.nicnbk.kz/#/corpMeetings/ic/edit/" + meetingId.longValue());
                    notificationDto.setInAppName("IC Module: All topics for IC #" + icMeetingDto.getNumber() + " (" + DateUtils.getDateFormatted(icMeetingDto.getDate()) + ") " +
                            " have been finalized. Please vote.");
                    notificationDto.setEmployee(icMember);
                    this.notificationService.createInAppAndEmailNotification(notificationDto);
                }
                List<EmployeeDto> icAdmins = this.employeeService.findUsersWithRole(UserRoles.IC_ADMIN.getCode());
                if(icAdmins != null){
                    for(EmployeeDto icAdmin: icAdmins){
                        if(icAdmin.getActive() != null && icAdmin.getActive().booleanValue()) {
                            NotificationDto notificationDto = new NotificationDto();
                            notificationDto.setEmailName("IC Module: All topics for IC #" + icMeetingDto.getNumber() +
                                    " (" + DateUtils.getDateFormatted(icMeetingDto.getDate()) + ") " +
                                    " have been finalized. https://unic.nicnbk.kz/#/corpMeetings/ic/edit/" + meetingId.longValue());
                            notificationDto.setInAppName("IC Module: All topics for IC #" + icMeetingDto.getNumber() +
                                    " (" + DateUtils.getDateFormatted(icMeetingDto.getDate()) + ") have been finalized.");
                            notificationDto.setEmployee(icAdmin);
                            this.notificationService.createInAppAndEmailNotification(notificationDto);
                        }
                    }
                }
            }
        }
    }

    /* IC MEETING TOPIC ***********************************************************************************************/
//    @Override
//    public EntitySaveResponseDto saveICMeetingTopicUpdate(ICMeetingTopicUpdateDto dto, FilesDto explanatoryNoteUpd,
//                                                          List<FilesDto> materialsUpd, String updater) {
//        try {
//            EntitySaveResponseDto saveResponseDto = new EntitySaveResponseDto();
//            if(StringUtils.isEmpty(dto.getNameUpd())){ // Name
//                String errorMessage = "Error saving IC Meeting Topic update: name required";
//                logger.error(errorMessage);
//                saveResponseDto.setErrorMessageEn(errorMessage);
//                return saveResponseDto;
//            }
//            if(StringUtils.isEmpty(dto.getDecisionUpd())){ // Decision
//                String errorMessage = "Error saving IC Meeting Topic update: decision required";
//                logger.error(errorMessage);
//                saveResponseDto.setErrorMessageEn(errorMessage);
//                return saveResponseDto;
//            }
//            if(dto.getId() == null){
//                String errorMessage = "Error saving IC Meeting Topic update: need to pass IC Meeting topic id";
//                logger.error(errorMessage);
//                saveResponseDto.setErrorMessageEn(errorMessage);
//                return saveResponseDto;
//            }
//            boolean resetVoting = false;
//            ICMeetingTopic entity = this.icMeetingTopicRepository.findOne(dto.getId());
//            if(entity != null){
//
//                if(!checkEditableICMeetingTopicUpdateByTopicIdAndUsername(dto.getId(), updater)){
//                    String errorMessage = "Error saving IC Meeting Topic update: entity is not editable";
//                    logger.error(errorMessage);
//                    saveResponseDto.setErrorMessageEn(errorMessage);
//                    return saveResponseDto;
//                }
//
//                if(entity.getIcMeeting() == null){
//                    String errorMessage = "Error saving IC Meeting Topic update: IC Meeting is not specified";
//                    logger.error(errorMessage);
//                    saveResponseDto.setErrorMessageEn(errorMessage);
//                    return saveResponseDto;
//                }
//                if(isICMeetingLockedByDeadline(entity.getIcMeeting())){
//                    if(entity.getIcMeeting().getUnlockedForFinalize() == null || !entity.getIcMeeting().getUnlockedForFinalize().booleanValue()){
//                        String errorMessage = "Error saving IC Meeting Topic update: IC Meeting is locked by deadline";
//                        logger.error(errorMessage);
//                        saveResponseDto.setErrorMessageEn(errorMessage);
//                        return saveResponseDto;
//                    }
//                }else{
//                    String errorMessage = "Error saving IC Meeting Topic update: IC Meeting deadline has not passed yet";
//                    logger.error(errorMessage);
//                    saveResponseDto.setErrorMessageEn(errorMessage);
//                    return saveResponseDto;
//                }
//
//            }else{
//                String errorMessage = "Error saving IC Meeting Topic update: IC Meeting topic not found with id: " + dto.getId().longValue();
//                logger.error(errorMessage);
//                saveResponseDto.setErrorMessageEn(errorMessage);
//                return saveResponseDto;
//            }
//
//            // Explanatory notes
//            if(explanatoryNoteUpd != null){
//                if(entity.getExplanatoryNoteUpd() != null){
//                    String errorMessage = "Error saving IC meeting topic update: explanatory note exists, " +
//                            "please delete the current file before uploading new one.";
//                    logger.error(errorMessage);
//                    saveResponseDto = new EntitySaveResponseDto();
//                    saveResponseDto.setErrorMessageEn(errorMessage);
//                    return saveResponseDto;
//                }
//                Long fileId = fileService.save(explanatoryNoteUpd, FileTypeLookup.IC_EXPLANATORY_NOTE.getCatalog());
//                entity.setExplanatoryNoteUpd(new Files(fileId));
//                resetVoting = true;
//            }
//            // RESET APPROVAL: Check decision or name change
//            if(!resetVoting && dto.getId() != null) {
//                ICMeetingTopic existingEntity = this.icMeetingTopicRepository.findOne(dto.getId());
//                if(existingEntity != null){
//                    String existingDecision = existingEntity.getDecision() != null ? existingEntity.getDecision() : "";
//                    String currentDecision = dto.getDecisionUpd() != null ? dto.getDecisionUpd() : "";
//                    if(!StringUtils.isEqualWithoutSpaces(existingDecision, currentDecision)){
//                        resetVoting = true;
//                    }else {
//                        String existingName = existingEntity.getName() != null ? existingEntity.getName() : "";
//                        String currentName = dto.getNameUpd() != null ? dto.getNameUpd() : "";
//                        if (!StringUtils.isEqualWithoutSpaces(existingName, currentName)) {
//                            resetVoting = true;
//                        }
//                    }
//                }
//            }
//            // set update date
//            entity.setUpdateDate(new Date());
//            // set updater
//            EmployeeDto updatedby = this.employeeService.findByUsername(updater);
//            entity.setUpdater(new Employee(updatedby.getId()));
//
//            entity.setName(dto.getNameUpd());
//            entity.setDecision(dto.getDecisionUpd());
//            entity.setPublishedUpd(dto.getPublishedUpd());
//
//            entity = icMeetingTopicRepository.save(entity);
//            logger.info("IC meeting topic update saved: topic id=" + entity.getId().longValue() + ", by " + updater);
//
//            // Materials
//            List<NamedFilesDto> existingMaterials = new ArrayList<>();
//            if(dto.getUploadMaterialsUpd() != null && !dto.getUploadMaterialsUpd().isEmpty() && materialsUpd != null &&
//                    !materialsUpd.isEmpty()){
//                List<NamedFilesDto> uploadMaterials = new ArrayList<>();
//                for(NamedFilesDto namedFilesDto: dto.getUploadMaterialsUpd()){
//                    boolean newFile = false;
//                    for(FilesDto filesDto: materialsUpd){
//                        if(namedFilesDto.getFile().getFileName().equals(filesDto.getFileName())){
//                            NamedFilesDto uploadMaterial = new NamedFilesDto(filesDto, namedFilesDto.getName());
//                            uploadMaterials.add(uploadMaterial);
//                            newFile = true;
//                        }
//                    }
//                    if(!newFile){
//                        existingMaterials.add(namedFilesDto);
//                    }
//                }
//                saveICMeetingTopicAttachments(entity.getId(), uploadMaterials, true, updater);
//                resetVoting = true;
//            }
//
//            saveICMeetingTopicAttachments(entity.getId(),
//                    (materialsUpd != null && !materialsUpd.isEmpty() ? existingMaterials : dto.getUploadMaterialsUpd()), true, updater); // uploaded files renaming
//
//            if(resetVoting){
//                resetICMeetingTopicVoting(entity.getId());
//            }
//
//            // TODO: error when updating? Transaction?
//
//            saveResponseDto.setEntityId(entity.getId());
//            saveResponseDto.setStatus(ResponseStatusType.SUCCESS);
//            return saveResponseDto;
//        }catch (Exception ex){
//            logger.error("Error saving IC meeting topic: " + (dto != null && dto.getId() != null ? dto.getId() : "new") ,ex);
//            EntitySaveResponseDto saveResponseDto = new EntitySaveResponseDto();
//            saveResponseDto.setErrorMessageEn("Error saving IC meeting topic: " + (dto != null && dto.getId() != null ? dto.getId() : "new"));
//            return saveResponseDto;
//        }
//    }

    private boolean isICMeetingLockedByDeadline(ICMeeting icMeeting){
        return ICMeetingDto.isICMeetingLockedByDeadline(icMeeting.getDate());
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

//    @Deprecated
//    private void saveApproveList(Long icMeetingTopicId, Set<EmployeeApproveDto> approveList){
//        if (approveList != null && !approveList.isEmpty()) {
//            List<ICMeetingTopicApproval> updatedApprovals = new ArrayList<>();
//            List<ICMeetingTopicApproval> existingApprovals = this.icMeetingTopicApprovalRepository.findByIcMeetingTopicId(icMeetingTopicId);
//            if (existingApprovals != null && !existingApprovals.isEmpty()) {
//                for (EmployeeApproveDto anApproval : approveList) {
//                    if (anApproval.getEmployee() == null || anApproval.getEmployee().getId() == null) {
//                        continue;
//                    }
//                    boolean found = false;
//                    for (ICMeetingTopicApproval existingApproval : existingApprovals) {
//                        if (existingApproval.getEmployee().getId().longValue() == anApproval.getEmployee().getId().longValue()) {
//                            found = true;
//                            updatedApprovals.add(existingApproval);
//                            break;
//                        }
//                    }
//                    if (!found) {
//                        // add new
//                        updatedApprovals.add(new ICMeetingTopicApproval(icMeetingTopicId,
//                                anApproval.getEmployee().getId(), false));
//                    }
//                }
//            } else {
//                // no existing, save all new
//                for (EmployeeApproveDto anApproval : approveList) {
//                    if (anApproval.getEmployee() != null && anApproval.getEmployee().getId() != null) {
//                        updatedApprovals.add(new ICMeetingTopicApproval(icMeetingTopicId,
//                                anApproval.getEmployee().getId(), false));
//                    }
//                }
//            }
//            this.icMeetingTopicApprovalRepository.save(updatedApprovals);
//        } else {
//            this.icMeetingTopicApprovalRepository.deleteByICMeetingTopicId(icMeetingTopicId);
//        }
//    }

    @Override
    public ICMeetingTopicDto getICMeetingTopic(Long id, String username) {
        try {
            if(!checkViewICMeetingTopicByTopicIdAndUsername(id, username)){
                return null;
            }
            ICMeetingTopic entity = icMeetingTopicRepository.findOne(id);
            ICMeetingTopicDto dto = icMeetingTopicEntityConverter.disassemble(entity);

            // set files
            dto.setMaterials(getICMeetingTopicAttachments(id/*, false*/));
            Collections.sort(dto.getMaterials());

            //dto.setMaterialsUpd(getICMeetingTopicAttachments(id/*, true*/));

            // set approvals
            List<ICMeetingTopicApproval> approvals = this.icMeetingTopicApprovalRepository.findByIcMeetingTopicId(id);
            if(approvals != null && !approvals.isEmpty()){
                Set<EmployeeApproveDto> approveList = new HashSet<>();
                for(ICMeetingTopicApproval anApproval: approvals){
                    EmployeeDto employeeDto = this.employeeService.getEmployeeById(anApproval.getEmployee().getId());
                    approveList.add(new EmployeeApproveDto(employeeDto, anApproval.getApproved(), anApproval.getApproveDate(), anApproval.getHash()));
                }
                dto.setApproveList(approveList);
            }
            // set tags
//            List<ICMeetingTopicTags> topicTags = this.topicTagsRepository.findByIcMeetingTopicId(id);
//            if(topicTags != null && !topicTags.isEmpty()){
//                if(dto.getTags() == null){
//                    dto.setTags(new ArrayList<>());
//                }
//                for(ICMeetingTopicTags topicTag: topicTags){
//                    dto.getTags().add(topicTag.getTag().getName());
//                }
//            }

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
    public List<NamedFilesDto> getICMeetingTopicAttachments(Long id/*, Boolean update*/){
        try {
            List<ICMeetingTopicFiles> entities = icMeetingTopicFilesRepository.getFilesByMeetingId(id);

            List<NamedFilesDto> files = new ArrayList<>();
            if (entities != null) {
                for (ICMeetingTopicFiles icMeetingTopicFiles : entities) {
                    //if(update == null || icMeetingTopicFiles.isUpdate() == update) {
                    FilesDto fileDto = new FilesDto();
                    fileDto.setId(icMeetingTopicFiles.getFile().getId());
                    fileDto.setFileName(icMeetingTopicFiles.getFile().getFileName());
                    fileDto.setMimeType(icMeetingTopicFiles.getFile().getMimeType());
                    NamedFilesDto namedFilesDto = new NamedFilesDto();
                    namedFilesDto.setFile(fileDto);
                    namedFilesDto.setName(icMeetingTopicFiles.getCustomName());
                    namedFilesDto.setTopicOrder(icMeetingTopicFiles.getTopicOrder());
                    files.add(namedFilesDto);
                    //}
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
                        }else if(approval.getEmployee().getPosition() != null && approval.getEmployee().getPosition().getDepartment() != null &&
                                approval.getEmployee().getPosition().getDepartment().getId().longValue() == editorDeptId){
                            // dept from approve list
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

                if(topicDto.getIcMeeting() != null && topicDto.getIcMeeting().isLockedByDeadline()) {
                    // LOCKED BY DEADLINE
                    if (topicDto.getIcMeeting().getUnlockedForFinalize() != null && topicDto.getIcMeeting().getUnlockedForFinalize().booleanValue()) {
                        //UNLOCKED FOR FINALIZE
                        // check deadline
                        Date icDate = topicDto.getIcMeeting().getDate();
                        if (icDate != null) {
                            String time = topicDto.getIcMeeting().getTime() != null ? topicDto.getIcMeeting().getTime() : "9:00";
                            Date icDateWithTime = DateUtils.getDateWithTime(icDate, time);
                            if (icDateWithTime != null) {
                                if (DateUtils.moveDateByDays(icDateWithTime, 1, true).before(new Date())) {
                                    return false;
                                }
                            }
                        }
                        return true;
                    }
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

    private boolean checkEditableICMeetingTopicMaterialsByTopicIdAndUsername(Long id, String username){
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
                    if(topicDto.getIcMeeting() != null && topicDto.getIcMeeting().getUnlockedForFinalize() != null &&
                    topicDto.getIcMeeting().getUnlockedForFinalize().booleanValue()){
                        return true;
                    }else {
                        return false;
                    }
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
                // check deadline
                Date icDate = topicDto.getIcMeeting().getDate();
                if(icDate != null){
                    String time = topicDto.getIcMeeting().getTime() != null ? topicDto.getIcMeeting().getTime() : "9:00";
                    Date icDateWithTime = DateUtils.getDateWithTime(icDate, time);
                    if(icDateWithTime != null){
                        if(DateUtils.moveDateByDays(icDateWithTime, 1, true).before(new Date())){
                            return false;
                        }
                    }
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

    private void createNotificationICMeetingTopicEdited(ICMeetingTopicDto topic){

        // TODO: check what type of changes? (if not important then skip)
        // TODO: check if user has unread similar notifications?

        if(topic != null && topic.getPublished() != null && topic.getPublished().booleanValue() && topic.getIcMeeting() != null){
            // published with IC Meeting
            // 1. IC Members

            // 2. Approve list
        }
    }

    private void createNotificationICMeetingTopicApproval(ICMeetingTopicDto topic, String approvedBy, boolean approved){
        if(topic != null && topic.getPublished() != null && topic.getPublished().booleanValue() && topic.getIcMeeting() != null){
            // published with IC Meeting
            EmployeeDto executor = topic.getExecutor();
            EmployeeDto speaker = topic.getSpeaker();
            String topicName = StringUtils.isNotEmpty(topic.getName()) ?
                    ("'" + topic.getName().substring(0, Math.min(30,topic.getName().length())) + (topic.getName().length() > 30 ? "...'": "'")) : "";
            String notificationMessage = "IC Meeting Topic " + topicName + " has been " + (approved ? "approved": "dis-approved") + " by " + approvedBy
                    +" [" + DateUtils.getDateFormattedWithTime(new Date()) +"]";
            if(executor != null) {
                NotificationDto notificationDto = new NotificationDto();
                notificationDto.setInAppName(notificationMessage);
                notificationDto.setEmployee(executor);
                this.notificationService.save(notificationDto);
            }
            if(speaker != null) {
                NotificationDto notificationDto = new NotificationDto();
                notificationDto.setInAppName(notificationMessage);
                notificationDto.setEmployee(speaker);
                this.notificationService.save(notificationDto);
            }
        }
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
                            approvalEntity.setApproveDate(new Date());
                            approvalEntity.setHash(HashUtils.generateRandomText());
                            this.icMeetingTopicApprovalRepository.save(approvalEntity);
                            // notification
                            createNotificationICMeetingTopicApproval(icMeetingTopic, username, true);

                            logger.info("IC Meeting topic with id=" + icMeetingTopic.getId().longValue() + " has been approved by " + username);
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
    public EntitySaveResponseDto cancelApproveICMeetingTopic(Long id, String username){
        EntitySaveResponseDto responseDto = new EntitySaveResponseDto();
        EmployeeDto employeeDto = this.employeeService.findByUsername(username);

        if(employeeDto != null){
            if(!checkApprovableICMeetingTopicByTopicIdAndUsername(id, username)){
                String errorMessage = "Error cancelling approve IC Meeting Topic: forbidden. ";
                logger.error(errorMessage + "Topic id=" + id.longValue() + ", username=[" + username + "]");
                responseDto.setErrorMessageEn(errorMessage);
                return responseDto;
            }

            ICMeetingTopicDto icMeetingTopic = getICMeetingTopic(id, username);
            if(icMeetingTopic.getApproveList() != null){
                for(EmployeeApproveDto approval: icMeetingTopic.getApproveList()){
                    if(approval.getEmployee() != null && approval.getEmployee().getId().longValue() ==
                            employeeDto.getId().longValue()){
                        //approve
                        ICMeetingTopicApproval approvalEntity =
                                this.icMeetingTopicApprovalRepository.findByIcMeetingTopicIdAndEmployeeId(id, employeeDto.getId());
                        if(approvalEntity != null){
                            approvalEntity.setApproved(false);
                            approvalEntity.setApproveDate(null);
                            approvalEntity.setHash(null);
                            this.icMeetingTopicApprovalRepository.save(approvalEntity);
                            // notification
                            createNotificationICMeetingTopicApproval(icMeetingTopic, username, false);

                            logger.info("IC Meeting topic with id=" + icMeetingTopic.getId().longValue() + " has been dis-approved by " + username);
                            responseDto.setStatus(ResponseStatusType.SUCCESS);
                            return responseDto;
                        }else{
                            String errorMessage = "Error dis-approving IC Meeting Topic: employee not found in approval list. ";
                            logger.error(errorMessage + "Topic id=" + id.longValue() + ", username=[" + username + "]");
                            responseDto.setErrorMessageEn(errorMessage);
                            return responseDto;
                        }
                    }
                }
            }

        }else{
            responseDto.setErrorMessageEn("Failed to dis-approve IC Meeting Topic: user not found '" + username + "'");
            return responseDto;
        }
        String errorMessage = "Error dis-approving IC Meeting Topic: employee not found in approval list. ";
        logger.error(errorMessage + "Topic id=" + id.longValue() + ", username=[" + username + "]");
        responseDto.setErrorMessageEn(errorMessage);
        return responseDto;
    }

    @Override
    public List<EmployeeDto> getAvailableApproveList(){
        String[] roles = {UserRoles.IC_TOPIC_RESTR.getCode(), UserRoles.IC_ADMIN.getCode(), UserRoles.IC_MEMBER.getCode()};
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
                    List<NamedFilesDto> materials = getICMeetingTopicAttachments(topic.getId()/*, null*/);
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
                voteDto.setComment(vote.getComment());
                votes.add(voteDto);
            }
        }
        return votes;
    }

    private void limitFieldsICMeetingTopics(List<ICMeetingTopicDto> topics){
        if(topics != null){
            for(ICMeetingTopicDto topic: topics){
                //topic.setDecision(null);
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

    private boolean saveICMeetingTopicAttachments(Long topicId, List<NamedFilesDto> attachments/*, boolean update*/, String username) {
        try {
            if (attachments != null && !attachments.isEmpty()) {
                //Iterator<NamedFilesDto> iterator = attachments.iterator();
                //while (iterator.hasNext()) {
                    //NamedFilesDto namedFilesDto = iterator.next();
                int i = 1;
                for(NamedFilesDto namedFilesDto: attachments){
                    if(StringUtils.isEmpty(namedFilesDto.getName())){
                        logger.error("Failed to save IC meeting topic attachment: file name required (topic id=" + topicId.longValue() + ") [user]=" + username);
                        return false;
                    }
                    if (namedFilesDto.getFile().getId() == null) {
                        Long fileId = fileService.save(namedFilesDto.getFile(), FileTypeLookup.IC_MATERIALS.getCatalog()); //
                        logger.info("Saved IC meeting topic attachment files: topic id=" + topicId.longValue() + ", file=" + fileId.longValue());

                        ICMeetingTopicFiles icMeetingTopicFiles = new ICMeetingTopicFiles(topicId, fileId, namedFilesDto.getName()/*, update*/);
                        icMeetingTopicFilesRepository.save(icMeetingTopicFiles);
                        logger.info("Saved IC meeting topic attachment information to DB: topic id=" + topicId.longValue() + ", file=" + fileId.longValue() + " [user]=" + username);
                    }else{
                        ICMeetingTopicFiles icMeetingTopicFiles = icMeetingTopicFilesRepository.getFilesByFileId(namedFilesDto.getFile().getId());
                        if(icMeetingTopicFiles != null){
                            icMeetingTopicFiles.setCustomName(namedFilesDto.getName());
                            icMeetingTopicFiles.setTopicOrder(i);
                            i++;
                            icMeetingTopicFilesRepository.save(icMeetingTopicFiles);
                        }
                    }
                }
            }
            return true;
        }catch (Exception ex){
            logger.error("Error saving IC meeting topic attachments: IC meeting topic id=" + topicId.longValue(), ex);
        }
        return false;
    }

    @Override
    public boolean safeDeleteICMeetingTopicAttachment(Long topicId, Long fileId, String username) {
        try {
            ICMeetingTopicFiles entity = icMeetingTopicFilesRepository.getFilesByFileId(fileId);
            if (entity != null && entity.getIcMeetingTopic().getId().longValue() == topicId) {
//                if(entity.isUpdate()){
//                    if (!checkEditableICMeetingTopicUpdateByTopicIdAndUsername(topicId, username)) {
//                        String errorMessage = "Error deleting IC Meeting Topic Attachment Update: entity not editable";
//                        logger.error(errorMessage);
//                        return false;
//                    }
//                }else {
                if (!checkEditableICMeetingTopicMaterialsByTopicIdAndUsername(topicId, username)) {
                    String errorMessage = "Error deleting IC Meeting Topic Attachment: entity not editable";
                    logger.error(errorMessage);
                    return false;
                }
//               }

                boolean deleted = fileService.safeDelete(fileId);
                if(!deleted){
                    logger.error("Failed to delete(safe) IC meeting topic attachment: IC topic =" + topicId.longValue() + ", file=" + fileId.longValue() + ", by " + username);
                }else{
                    logger.info("Deleted(safe) IC meeting topic attachment: IC topic =" + topicId.longValue() + ", file=" + fileId.longValue() + ", by " + username);
                    //if(entity.isUpdate()){
                        resetICMeetingTopicVoting(topicId);
                    //}else{
                        resetICMeetingTopicApprovals(topicId);
                    //}
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
                if(entity.getIcMeeting() != null && entity.getIcMeeting().getDeleted() != null &&
                        entity.getIcMeeting().getDeleted().booleanValue()){
                    return false;
                }
                if(entity.getClosed() != null && entity.getClosed().booleanValue()){
                    return false;
                }
                if(entity.getDeleted() != null && entity.getDeleted().booleanValue()){
                    return false;
                }
                if(entity.getIcMeeting() != null && isICMeetingLockedByDeadline(entity.getIcMeeting())){
                    if(entity.getIcMeeting().getUnlockedForFinalize() != null && entity.getIcMeeting().getUnlockedForFinalize().booleanValue()){
                        // unlocked for finalize
                        // OK, check approve list
                    }else {
                        return false;
                    }
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

//    @Override
//    public boolean deleteICMeetingTopicExplanatoryNoteUpd(Long topicId, String username) {
//        try {
//            if(!checkEditableICMeetingTopicUpdateByTopicIdAndUsername(topicId, username)){
//                String errorMessage = "Error deleting IC Meeting Topic Explanatory Note Upd: entity not editable";
//                logger.error(errorMessage);
//                return false;
//            }
//            ICMeetingTopic entity = icMeetingTopicRepository.findOne(topicId);
//            if (entity != null && entity.getExplanatoryNoteUpd() != null) {
//                boolean deleted = fileService.safeDelete(entity.getExplanatoryNoteUpd().getId());
//                if(deleted) {
//                    long fileId = entity.getExplanatoryNoteUpd().getId().longValue();
//                    entity.setExplanatoryNoteUpd(null);
//                    this.icMeetingTopicRepository.save(entity);
//                    logger.info("Deleted(safe) IC meeting topic explanatory note update: IC topic =" + topicId + ", file=" +
//                            fileId + ", by " + username);
//
//                    //resetICMeetingTopicApprovals(topicId);
//                    resetICMeetingTopicVoting(topicId);
//                }else{
//                    logger.error("Failed to delete IC meeting topic explanatory note");
//                }
//                return deleted;
//            }else{
//                logger.error("Error save deleting IC meeting topic explanatory note: topic not found with id=" + topicId);
//            }
//        }catch (Exception e){
//            logger.error("Failed to delete(safe) IC meeting topic explanatory note with error: IC topic =" + topicId + ", by " + username, e);
//        }
//        return false;
//    }


    /* IC MEETING *****************************************************************************************************/
    @Transactional
    @Override
    public EntitySaveResponseDto saveICMeeting(ICMeetingDto icMeetingDto, FilesDto agendaFile, FilesDto protocolFile,
                                               FilesDto bulletinFile, String updater) {
        EntitySaveResponseDto saveResponseDto = new EntitySaveResponseDto();
        try {
            ICMeeting entity = icMeetingsEntityConverter.assemble(icMeetingDto);
            if(icMeetingDto.getId() == null){ // CREATE
                EmployeeDto employee = this.employeeService.findByUsername(updater);
                entity.setCreator(new Employee(employee.getId()));
            }else{ // UPDATE
                if(icMeetingDto != null && !checkEditableICMeeting(icMeetingDto, updater)){
                    String errorMessage = "Error saving IC Meeting with id " + icMeetingDto.getId().longValue() + ": entity not editable";
                    logger.error(errorMessage);
                    saveResponseDto.setErrorMessageEn(errorMessage);
                    return saveResponseDto;
                }
                ICMeetingDto existing = getICMeeting(icMeetingDto.getId());
                if(existing.getCreator() != null) {
                    EmployeeDto creator = this.employeeService.findByUsername(existing.getCreator());
                    entity.setCreator(new Employee(creator.getId()));
                }
                entity.setCreationDate(existing.getCreationDate());
                entity.setUpdateDate(new Date());
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

            if(protocolFile != null){
                if(entity.getProtocol() != null){
                    String errorMessage = "Error saving IC meeting : protocol exists, please delete the current file before uploading new one.";
                    logger.error(errorMessage);
                    saveResponseDto = new EntitySaveResponseDto();
                    saveResponseDto.setErrorMessageEn(errorMessage);
                    return saveResponseDto;
                }
                Long fileId = fileService.save(protocolFile, FileTypeLookup.IC_PROTOCOL.getCatalog());
                entity.setProtocol(new Files(fileId));
            }

            if(bulletinFile != null){
                if(entity.getBulletin() != null){
                    String errorMessage = "Error saving IC meeting : bulletin exists, please delete the current file before uploading new one.";
                    logger.error(errorMessage);
                    saveResponseDto = new EntitySaveResponseDto();
                    saveResponseDto.setErrorMessageEn(errorMessage);
                    return saveResponseDto;
                }
                Long fileId = fileService.save(bulletinFile, FileTypeLookup.IC_BULLETIN.getCatalog());
                entity.setBulletin(new Files(fileId));
            }

            entity = icMeetingsRepository.save(entity);


            // TODO: Transactional behavior !!!!! CHECK !

            // save attendees
            saveResponseDto = saveICMeetingAttendees(icMeetingDto.getAttendees(), entity.getId());
            if(!saveResponseDto.isStatusOK()){
                return saveResponseDto;
            }


            // save invitees
            saveICMeetingInvitees(icMeetingDto.getInvitees(), entity.getId());

            // Update ic topics order
            updateICMeetingTopicsOrder(icMeetingDto.getTopics());

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

    private void updateICMeetingTopicsOrder(List<ICMeetingTopicDto> topics){
        if(topics != null && !topics.isEmpty()){
            int i = 1;
            for(ICMeetingTopicDto topicDto: topics){
                // update order
                if(topicDto.getId() != null) {
                    ICMeetingTopic topic = this.icMeetingTopicRepository.findOne(topicDto.getId());
                    if (topic != null) {
                        topic.setIcOrder(i);
                        i++;
                        this.icMeetingTopicRepository.save(topic);
                    }
                    //this.icMeetingTopicRepository.updateICOrder(topicDto.getId(), i);
                    //i++;
                }
            }
        }
    }

    private void saveICMeetingInvitees(List<EmployeeDto> invitees, Long icMeetingId){
        if(invitees != null && !invitees.isEmpty()){
            List<ICMeetingInvitees> entities = new ArrayList<>();
            for(EmployeeDto employeeDto: invitees){
                if(employeeDto.getId() != null) {
                    ICMeetingInvitees entity = new ICMeetingInvitees();
                    entity.setIcMeeting(new ICMeeting(icMeetingId));
                    entity.setEmployee(new Employee(employeeDto.getId()));
                    entities.add(entity);
                }
            }
            this.icMeetingInviteesRepository.deleteByICMeetingId(icMeetingId);
            this.icMeetingInviteesRepository.save(entities);
        }else{
            // clear all existing
            this.icMeetingInviteesRepository.deleteByICMeetingId(icMeetingId);
        }

    }

    private EntitySaveResponseDto saveICMeetingAttendees(List<ICMeetingAttendeesDto> attendees, Long icMeetingId){
        EntitySaveResponseDto saveResponseDto = new EntitySaveResponseDto();
        if(attendees != null && !attendees.isEmpty()){
            List<ICMeetingAttendees> entities = new ArrayList<>();
            for(ICMeetingAttendeesDto dto: attendees){
                ICMeetingAttendees entity = new ICMeetingAttendees();
                entity.setIcMeeting(new ICMeeting(icMeetingId));
                entity.setEmployee(new Employee(dto.getEmployee().getId()));
                entity.setPresent(dto.isPresent());
                if(!dto.isPresent()) {
                    if (dto.getAbsenceType() != null && dto.getAbsenceType() != null) {
                        ICMeetingAttendeeAbsenceType absenceType = this.lookupService.findByTypeAndCode(ICMeetingAttendeeAbsenceType.class, dto.getAbsenceType());
                        if(absenceType == null) {
                            // TODO: check transactions
                            saveResponseDto.setErrorMessageEn("Failed to save IC Meeting: attendees not present must have reason specified");
                            return saveResponseDto;
                        }
                        entity.setAbsenceType(absenceType);
                    }
                }else{
                    entity.setAbsenceType(null);
                }
                entities.add(entity);
            }
            this.icMeetingAttendeesRepository.deleteByICMeetingId(icMeetingId);
            this.icMeetingAttendeesRepository.save(entities);
        }else{
            // clear all existing
            this.icMeetingAttendeesRepository.deleteByICMeetingId(icMeetingId);
        }
        saveResponseDto.setStatus(ResponseStatusType.SUCCESS);
        return saveResponseDto;
    }

    @Override
    public boolean deleteICMeetingAgenda(Long icMeetingId, String username) {
        try {
            ICMeetingDto icMeeting = getICMeeting(icMeetingId);
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
    public boolean deleteICMeetingProtocol(Long icMeetingId, String username) {
        try {
            ICMeetingDto icMeeting = getICMeeting(icMeetingId);
            if(icMeeting != null && !checkEditableICMeeting(icMeeting, username)){
                String errorMessage = "Error saving IC Meeting with id " + icMeeting.getId().longValue() + ": entity not editable";
                logger.error(errorMessage);
                return false;
            }
            ICMeeting entity = this.icMeetingsRepository.findOne(icMeetingId);
            if (entity != null && entity.getProtocol() != null) {
                boolean deleted = fileService.safeDelete(entity.getProtocol().getId());
                long fileId = entity.getProtocol().getId().longValue();
                entity.setProtocol(null);
                this.icMeetingsRepository.save(entity);
                logger.info("Deleted(safe) IC meeting protocol: IC id =" + icMeetingId.longValue() + ", file=" +
                        fileId + ", by " + username);
                return deleted;
            }else{
                logger.error("Error save deleting IC meeting protocol: ic meeting not found with id=" + icMeetingId.longValue());
            }
        }catch (Exception e){
            logger.error("Failed to delete(safe) IC meeting protocol with error: IC id =" + icMeetingId.longValue() + ", by " + username, e);
        }
        return false;
    }

    @Override
    public boolean deleteICMeetingBulletin(Long icMeetingId, String username) {
        try {
            ICMeetingDto icMeeting = getICMeeting(icMeetingId);
            if(icMeeting != null && !checkEditableICMeeting(icMeeting, username)){
                String errorMessage = "Error saving IC Meeting with id " + icMeeting.getId().longValue() + ": entity not editable";
                logger.error(errorMessage);
                return false;
            }
            ICMeeting entity = this.icMeetingsRepository.findOne(icMeetingId);
            if (entity != null && entity.getBulletin() != null) {
                boolean deleted = fileService.safeDelete(entity.getBulletin().getId());
                long fileId = entity.getBulletin().getId().longValue();
                entity.setBulletin(null);
                this.icMeetingsRepository.save(entity);
                logger.info("Deleted(safe) IC meeting bulletin: IC id =" + icMeetingId.longValue() + ", file=" +
                        fileId + ", by " + username);
                return deleted;
            }else{
                logger.error("Error save deleting IC meeting bulletin: ic meeting not found with id=" + icMeetingId.longValue());
            }
        }catch (Exception e){
            logger.error("Failed to delete(safe) IC meeting bulletin with error: IC id =" + icMeetingId.longValue() + ", by " + username, e);
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
    public ICMeetingDto getICMeeting(Long id) {
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

            // closeable
            boolean closeable = true;
            // TODO: cannot close if not everyone voted?
            // TODO: NOTE, CEO auto added as voted, but does not vote
//            if(dto.getTopics() != null && !dto.getTopics().isEmpty()){
//                for(ICMeetingTopicDto topic: dto.getTopics()){
//                    int attendingICMembersNum = 0;
//                    if(dto.getAttendees() != null && !dto.getAttendees().isEmpty()){
//                        for(ICMeetingAttendeesDto attendeesDto: dto.getAttendees()){
//                            attendingICMembersNum += (attendeesDto.isPresent() ? 1 : 0);
//                        }
//                    }
//                    if(topic.getVotes() == null || topic.getVotes().isEmpty() || topic.getVotes().size() != attendingICMembersNum){
//                        closeable = false;
//                        break;
//                    }
//                }
//            }else{
//                // TODO: closeable ?
//                //closeable = true;
//            }
            dto.setCloseable(closeable);

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
            ICMeetingDto dto = getICMeeting(id);
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
                    ICMeetingDto existingDto = getICMeeting(dto.getId());
                    if(existingDto != null){
                        if(existingDto.isLockedByDeadline() && (existingDto.getUnlockedForFinalize() == null ||
                                !existingDto.getUnlockedForFinalize().booleanValue())) {
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
    public List<ICMeetingDto> getICMeetingsWithDeadline(Date date) {
        List<ICMeetingDto> dtoList = new ArrayList<>();
        Date dateFrom = DateUtils.getDateWithTime(DateUtils.moveDateByDays(date, CorpMeetingService.IC_MEETING_DEADLINE_DAYS + 1, true), "00:00");
        Date dateTo = DateUtils.getDateWithTime(DateUtils.moveDateByDays(dateFrom, CorpMeetingService.IC_MEETING_DEADLINE_DAYS + 1, true), "23:59");
        List<ICMeeting> icMeetings = this.icMeetingsRepository.getOpenICMeetingsWithinDates(dateFrom, dateTo);
        if(icMeetings != null && !icMeetings.isEmpty()){
            for(ICMeeting entity: icMeetings){
                //ICMeetingDto dto = this.icMeetingsEntityConverter.disassemble(entity);
                ICMeetingDto dto = new ICMeetingDto();
                dto.setId(entity.getId());
                dto.setDate(entity.getDate());
                dto.setNumber(entity.getNumber());

                // set topics
                List<ICMeetingTopic> topicEntities = icMeetingTopicRepository.findByICMeetingIdNotDeleted(entity.getId());
                if(topicEntities != null) {
                    List<ICMeetingTopicDto> topics = new ArrayList<>();
                    for(ICMeetingTopic topicEntity: topicEntities) {
                        ICMeetingTopicDto topic = new ICMeetingTopicDto();
                        topic.setName(topicEntity.getName());
                        if(topicEntity.getSpeaker() != null) {
                            EmployeeDto speaker = new EmployeeDto();
                            speaker.setId(topicEntity.getSpeaker().getId());
                            topic.setSpeaker(speaker);
                        }
                        if(topicEntity.getExecutor() != null) {
                            EmployeeDto executor = new EmployeeDto();
                            executor.setId(topicEntity.getExecutor().getId());
                            topic.setExecutor(executor);
                        }
                        topics.add(topic);
                    }
                    dto.setTopics(topics);
                }

                dtoList.add(dto);
            }
        }
        //Collections.sort(dtoList);
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
    public FilesDto getICMeetingProtocolRegistryFileStream(String username){
        FilesDto filesDto = new FilesDto();

        final String exportFileTemplatePath = "export_template/corp_meetings/IC_PROTOCOL_REGISTRY_TEMPLATE.xlsx";
        Resource resource = new ClassPathResource(exportFileTemplatePath);
        InputStream excelFileToRead = null;
        try {
            excelFileToRead = resource.getInputStream();
        } catch (IOException e) {
            logger.error("IC Meeting: Export file template not found: '" + exportFileTemplatePath + "'");
            e.printStackTrace();
            return null;
        }

        try {
            ICMeetingsPagedSearchResult icMeetingsResult = searchICMeetings(null);
            List<ICMeetingDto> icMeetings = icMeetingsResult != null ? icMeetingsResult.getIcMeetings() : new ArrayList<>();
            XSSFWorkbook workbook = new XSSFWorkbook(excelFileToRead);
            XSSFSheet sheet = workbook.getSheetAt(0);

            final int templateRowIndex = 2;
            final int columnNum = 5;
            int added = 0;
            if(icMeetings != null && !icMeetings.isEmpty()){
                sheet.shiftRows(templateRowIndex + 1, sheet.getLastRowNum(), icMeetings.size() - 1);
                for(ICMeetingDto ic: icMeetings) {
                    Row newRow = sheet.createRow(templateRowIndex + 1 + added);
                    added++;

                    for(int i = 0; i < columnNum; i++){
                        newRow.createCell(i);
                    }
                    newRow.getCell(0).setCellValue(ic.getNumber());
                    newRow.getCell(1).setCellValue(DateUtils.getDateFormatted(ic.getDate()));
                    if(ic.getTopics() != null && !ic.getTopics().isEmpty()){
                        int i = 1;
                        String topics = "";
                        String speakers = "";
                        String decisions = "";
                        for(ICMeetingTopicDto topic: ic.getTopics()){
                            topics += (i > 1 ? "\n" : "") + i + "." + topic.getName();

                            if(topic.getSpeaker() != null){
                                speakers += (i > 1 ? "\n" : "") + i + "." + topic.getSpeaker().getFullNameInitialsRu();
                            }else{
                                // department
                                if(topic.getDepartment() != null){
                                    speakers += (i > 1 ? "\n" : "") + i + "." + topic.getDepartment().getNameRu();
                                }
                            }
                            decisions += (i > 1 ? "\n" : "") + i + "." + topic.getDecision();
                            i++;
                        }
                        newRow.getCell(2).setCellValue(topics);
                        newRow.getCell(3).setCellValue(speakers);
                        newRow.getCell(4).setCellValue(decisions);
                    }

                    // set styles
                    for(int i = 0; i < columnNum; i++){
                        if(newRow.getCell(i) != null && sheet.getRow(templateRowIndex) != null &&
                                sheet.getRow(templateRowIndex).getCell(i) != null){
                            newRow.getCell(i).setCellStyle(sheet.getRow(templateRowIndex).getCell(i).getCellStyle());
                        }
                    }

                    // TODO: bold
                }
                sheet.shiftRows(templateRowIndex + 1, sheet.getLastRowNum(), -1);
            }

            File tmpDir = new File(this.rootDirectory + "/tmp/corp_meetings");

            // write to new
            String filePath = tmpDir + "/IC_PROTOCOL_REGISTRY_" + MathUtils.getRandomNumber(0, 10000) + ".xlsx";
            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }

            InputStream inputStream = new FileInputStream(filePath);
            filesDto.setInputStream(inputStream);
            filesDto.setFileName(filePath);
            return filesDto;
        } catch (IOException e) {
            logger.error("IO Exception when exporting IC Meeting Protocol Registry", e);
            //e.printStackTrace();
        }

        return null;
    }

    @Override
    public FilesDto getICMeetingAgendaFileStream(Long icMeetingId, String username){

        FilesDto filesDto = new FilesDto();
        ICMeetingDto icMeeting = getICMeeting(icMeetingId);

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

    @Override
    public FilesDto getICMeetingTopicApproveListFileStream(Long topicId, String username){

        FilesDto filesDto = new FilesDto();
        ICMeetingTopicDto icMeetingTopic = getICMeetingTopic(topicId, username);

        final String exportFileTemplatePath = "export_template/corp_meetings/IC_APPROVE_LIST_TEMPLATE.docx";
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
            if(icMeetingTopic.getApproveList() != null && !icMeetingTopic.getApproveList().isEmpty()){
                int i = 1;
                for(EmployeeApproveDto approveDto: icMeetingTopic.getApproveList()){
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
                    if(approveDto.getEmployee() != null) {
                        run1.setText(approveDto.getEmployee().getFullPositionRu());
                    }

                    newRow.getCell(1).removeParagraph(0);
                    XWPFParagraph paragraph2 = newRow.getCell(1).addParagraph();
                    XWPFRun run2 = paragraph2.createRun();
                    run2.getCTR().addNewRPr().addNewRFonts().setHAnsi("Arial");
                    run2.setFontFamily("Arial");
                    run2.setFontSize(11);
                    if(approveDto.getEmployee() != null) {
                        run2.setText(approveDto.getEmployee().getFullNameInitialsRu());
                    }

                    String signatureTimestamp = approveDto.isApproved() ?
                            "согласовано в UNIC" /*+ ( approveDto.getApproveDate() != null ?
                                    " " + DateUtils.getDateFormattedWithTime(approveDto.getApproveDate()) : "")*/
                                    + (approveDto.getHash() != null ? " [" + approveDto.getHash() + "]": "") :
                            "ожидает согласования";
                    newRow.getCell(2).removeParagraph(0);
                    XWPFParagraph paragraph3 = newRow.getCell(2).addParagraph();
                    XWPFRun run3 = paragraph3.createRun();
                    run3.getCTR().addNewRPr().addNewRFonts().setHAnsi("Arial");
                    run3.setFontFamily("Arial");
                    run3.setItalic(true);
                    run3.setFontSize(9);
                    run3.setText(signatureTimestamp);

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
                            if (text != null && text.contains("DECISION")) {
                                if(icMeetingTopic.getDecision() != null) {
                                    text = text.replace("DECISION",icMeetingTopic.getDecision());
                                }else{
                                    text = text.replace("DECISION", "");
                                }
                                r.setText(text, 0);
                            }else if (text != null && text.contains("QUESTION")) {
                                if(icMeetingTopic.getName() != null) {
                                    text = text.replace("QUESTION",icMeetingTopic.getName());
                                }else{
                                    text = text.replace("QUESTION", "");
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
                newParagraph.setAlignment(ParagraphAlignment.BOTH);
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
                    values.add(topic.getName());
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
                    values.add(topic.getDecision());
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
                run.setText(getDiscussionsHeader(speaker, topics.get(i).getName()));
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
                String decision = topics.get(i).getDecision() != null ? topics.get(i).getDecision() : "";
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
                values.add(topic.getDecision());
            });
            insertNumberedList(document, decisionsIndex, values);
            index += values.size();

            // Add CEO as voted YES, if missing
            if(topics != null && !topics.isEmpty()){
                for(ICMeetingTopicDto topic:topics){
                    boolean ceoExists = false;
                    if(topic.getVotes() != null){
                        for(ICMeetingTopicsVoteDto voteDto: topic.getVotes()){
                            if(voteDto.getEmployee() != null && voteDto.getEmployee().getPosition() != null &&
                            voteDto.getEmployee().getPosition().getCode().equalsIgnoreCase("CEO")){
                                // TODO: Refactor
                                ceoExists = true;
                                break;
                            }
                        }
                    }else{
                        topic.setVotes(new ArrayList<ICMeetingTopicsVoteDto>());
                    }
                    if(!ceoExists){
                        // insert ceo vote "YES:
                        List<EmployeeDto> execs = this.employeeService.findExecutivesAndActive();
                        if(execs != null && !execs.isEmpty()){
                            for(EmployeeDto exec: execs){
                                if(exec.getPosition() != null && exec.getPosition().getCode().equalsIgnoreCase("CEO")){
                                    ICMeetingTopicsVoteDto voteDto = new ICMeetingTopicsVoteDto();
                                    voteDto.setEmployee(exec);
                                    voteDto.setIcMeetingTopicId(topic.getId());
                                    voteDto.setVote("YES");
                                    topic.getVotes().add(voteDto);
                                    break;
                                }
                            }
                        }

                    }

                }
            }
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
        ICMeetingDto icMeeting = getICMeeting(icMeetingId);

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
        ICMeetingDto icMeeting = getICMeeting(icMeetingId);

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
                    }else if (text != null && text.contains(IC_PROTOCOL_ICADMIN_PLACEHOLDER)) {
                        // get IC ADMIN user
                        String[] roles = {UserRoles.IC_ADMIN.getCode()};
                        List<EmployeeDto> icAdmins = this.employeeService.findEmployeesByRoleCodes(roles);
                        if(icAdmins != null && !icAdmins.isEmpty() && icAdmins.get(0).getFullNameInitialsRu() != null) {
                            text = text.replace(IC_PROTOCOL_ICADMIN_PLACEHOLDER, icAdmins.get(0).getFullNameInitialsRu());

                        }else{
                            text = text.replace(IC_PROTOCOL_ICADMIN_PLACEHOLDER, "");
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
                            String questionName = questions.get(questionIndex).getName() != null ? questions.get(questionIndex).getName() : "";
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
            return "четвертому";
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
        }else if(number == 11){
            return "одиннадцатому";
        }else if(number == 12){
            return "двенадцатому";
        }else if(number == 13){
            return "тринадцатому";
        }else if(number == 14){
            return "четырнадцатому";
        }else if(number == 15){
            return "пятнадцатому";
        }else{
            return number + "";
        }
    }

    private String getDiscussionsHeader(String speakerName, String topicName){
        String header = "выступил(-а) ";
        if(speakerName != null ){
            header +=  speakerName + ", который предоставил Комитету информацию ";
            if(topicName != null){
                header +=  topicName;
            }
        }
        return header;
    }

    private String getDecisionHeader(int number){
        return "Комитет по " + getNumberRussian(number) + " вопросу постановил:";
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
            ICMeetingDto dto = getICMeeting(id);
            if(checkICMeetingUnlockableForFinalize(dto, username)){
                ICMeeting entity = this.icMeetingsRepository.findOne(id);
                entity.setUnlockedForFinalize(true);
                this.icMeetingsRepository.save(entity);
                return true;
            }
        }
        return false;
    }

    // TODO: @Transactional
    @Override
    public boolean saveICMeetingVotes(ICMeetingVoteDto votes, String username){
        if(votes != null && votes.getIcMeetingId() != null){
            ICMeetingDto dto = getICMeeting( votes.getIcMeetingId());
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
                        vote.setComment(voteDto.getComment());
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
            ICMeetingDto dto = getICMeeting(id);
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
            ICMeetingDto dto = getICMeeting(id);
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

    @Override
    public List<CorpMeetingUpcomingEventDto> getUpcomingEvents(String username){

        // TODO: based on username?

        List<CorpMeetingUpcomingEventDto> events = new ArrayList<>();
        // get IC Meetings
        ICMeetingsSearchParamsDto searchParams = new ICMeetingsSearchParamsDto();
        searchParams.setDateFrom(DateUtils.getFirstDayOfCurrentMonth(new Date()));
        searchParams.setDateTo(DateUtils.getLastDayOfCurrentMonth(new Date()));
        searchParams.setPage(0);
        searchParams.setPageSize(100);
        ICMeetingsPagedSearchResult searchResult = searchICMeetings(searchParams);
        if(searchResult != null && searchResult.getIcMeetings() != null && !searchResult.getIcMeetings().isEmpty()){
            for(ICMeetingDto icMeetingDto: searchResult.getIcMeetings()){
                CorpMeetingUpcomingEventDto event = new CorpMeetingUpcomingEventDto("IC # " + icMeetingDto.getNumber(), "");
                event.setDate(icMeetingDto.getDate());
                event.setAlertType("INFO"); // TODO: refactor
                events.add(event);

                Date deadlineDate = DateUtils.moveDateByDays(icMeetingDto.getDate(), -CorpMeetingService.IC_MEETING_DEADLINE_DAYS,true);
                CorpMeetingUpcomingEventDto eventDeadline = new CorpMeetingUpcomingEventDto("Deadline for IC # " + icMeetingDto.getNumber(), "");
                eventDeadline.setDescription("Deadline for submission - IC # " + icMeetingDto.getNumber() + " on " + DateUtils.getDateFormatted(deadlineDate));
                eventDeadline.setDate(deadlineDate);
                eventDeadline.setAlertType("WARNING");// TODO: refactor
                events.add(eventDeadline);
            }
        }

        // get Deadlines

        return events;
    }


}
