package kz.nicnbk.service.impl.corpmeetings;

import kz.nicnbk.common.service.util.PaginationUtils;
import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.repo.api.corpmeetings.*;
import kz.nicnbk.repo.model.corpmeetings.*;
import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.repo.model.lookup.FileTypeLookup;
import kz.nicnbk.service.api.authentication.TokenService;
import kz.nicnbk.service.api.corpmeetings.CorpMeetingService;
import kz.nicnbk.service.api.employee.EmployeeService;
import kz.nicnbk.service.api.files.FileService;
import kz.nicnbk.service.converter.corpmeetings.CorpMeetingsEntityConverter;
import kz.nicnbk.service.converter.corpmeetings.ICMeetingTopicEntityConverter;
import kz.nicnbk.service.converter.corpmeetings.ICMeetingsEntityConverter;
import kz.nicnbk.service.dto.common.EntitySaveResponseDto;
import kz.nicnbk.service.dto.common.ResponseStatusType;
import kz.nicnbk.service.dto.corpmeetings.*;
import kz.nicnbk.service.dto.employee.EmployeeDto;
import kz.nicnbk.service.dto.files.FilesDto;
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


    @Autowired
    private FileService fileService;

    @Autowired
    private CorpMeetingsRepository corpMeetingsRepository;

    @Autowired
    private ICMeetingTopicRepository icMeetingTopicRepository;

    @Autowired
    private CorpMeetingFilesRepository corpMeetingFilesRepository;

    @Autowired
    private ICMeetingFilesRepository icMeetingFilesRepository;

    @Autowired
    private CorpMeetingsEntityConverter corpMeetingsEntityConverter;

    @Autowired
    private ICMeetingTopicEntityConverter icMeetingTopicEntityConverter;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private ICMeetingsRepository icMeetingsRepository;

    @Autowired
    private ICMeetingsEntityConverter icMeetingsEntityConverter;


    @Deprecated
    @Override
    public EntitySaveResponseDto save(CorpMeetingDto corpMeetingDto, String updater) {
        try {
            CorpMeeting entity = corpMeetingsEntityConverter.assemble(corpMeetingDto);
            if(corpMeetingDto.getId() == null){ // CREATE
                EmployeeDto employee = this.employeeService.findByUsername(updater);
                // set creator
                entity.setCreator(new Employee(employee.getId()));
            }else{ // UPDATE
                // set creator
                CorpMeeting currentEntity = this.corpMeetingsRepository.findOne(corpMeetingDto.getId());
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

            entity = corpMeetingsRepository.save(entity);
            logger.info(corpMeetingDto.getId() == null ? "Corp meeting created: " + entity.getId() + ", by " + entity.getCreator().getUsername() :
                    "Corp meeting updated: " + entity.getId() + ", by " + updater);

            EntitySaveResponseDto saveResponseDto = new EntitySaveResponseDto();
            saveResponseDto.setEntityId(entity.getId());
            saveResponseDto.setStatus(ResponseStatusType.SUCCESS);
            return saveResponseDto;
        }catch (Exception ex){
            logger.error("Error saving Corp meeting: " + (corpMeetingDto != null && corpMeetingDto.getId() != null ? corpMeetingDto.getId() : "new") ,ex);
            EntitySaveResponseDto saveResponseDto = new EntitySaveResponseDto();
            saveResponseDto.setErrorMessageEn("Error saving Corp meeting: " + (corpMeetingDto != null && corpMeetingDto.getId() != null ? corpMeetingDto.getId() : "new"));
            return saveResponseDto;
        }
    }

    @Deprecated
    @Override
    public CorpMeetingDto get(Long id) {
        try {
            CorpMeeting entity = corpMeetingsRepository.findOne(id);
            CorpMeetingDto dto = corpMeetingsEntityConverter.disassemble(entity);

            dto.setFiles(getAttachments(id));
            return dto;
        }catch(Exception ex){
            logger.error("Error loading corp meeting: " + id, ex);
        }
        return null;
    }

    @Override
    public EntitySaveResponseDto saveICMeetingTopic(ICMeetingTopicDto dto, String updater) {
        try {
            ICMeetingTopic entity = icMeetingTopicEntityConverter.assemble(dto);
            if(dto.getId() == null){ // CREATE
                EmployeeDto employee = this.employeeService.findByUsername(updater);
                // set creator
                entity.setCreator(new Employee(employee.getId()));
            }else{ // UPDATE
                // set creator
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

            entity = icMeetingTopicRepository.save(entity);
            logger.info(dto.getId() == null ? "IC meeting topic created: " + entity.getId() + ", by " + entity.getCreator().getUsername() :
                    "IC meeting topic updated: " + entity.getId() + ", by " + updater);

            EntitySaveResponseDto saveResponseDto = new EntitySaveResponseDto();
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
                if(!checkEditableICMeeting(currentEntity.getId())){
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

            List<ICMeeting> existingNumberEntities = this.icMeetingsRepository.findByNumber(entity.getNumber());
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
    public ICMeetingTopicDto getICMeetingTopic(Long id) {
        try {
            ICMeetingTopic entity = icMeetingTopicRepository.findOne(id);
            ICMeetingTopicDto dto = icMeetingTopicEntityConverter.disassemble(entity);

            // TODO: set files

            dto.setMaterials(getICMeetingAttachments(id));
            return dto;
        }catch(Exception ex){
            logger.error("Error loading corp meeting: " + id, ex);
        }
        return null;
    }

    @Override
    public ICMeetingDto getICMeeting(Long id) {
        try {
            ICMeeting entity = icMeetingsRepository.findOne(id);
            ICMeetingDto dto = icMeetingsEntityConverter.disassemble(entity);

            dto.setMaterials(getICMeetingAttachments(id));
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
    public boolean safeDelete(Long id, String username) {
        try {
            CorpMeeting entity = corpMeetingsRepository.findOne(id);
            entity.setDeleted(true);

            this.corpMeetingsRepository.save(entity);
            logger.info("Corp meeting deleted with id=" + id + " [user]=" + username);
            return true;
        }catch(Exception ex){
            logger.error("Error deleting corp meeting: id=" + id + ", ]user]=" + username, ex);
        }
        return false;
    }

    @Override
    public boolean deleteICMeeting(Long id, String username) {
        try {
            // TODO: Check
            ICMeeting entity = icMeetingsRepository.findOne(id);
            if(!checkEditableICMeeting(id)){
                String errorMessage = "Error deleting IC Meeting with id + " + id + ": entity not editable";
                logger.error(errorMessage);
                return false;
            }
            this.icMeetingsRepository.delete(entity);
            logger.info("IC meeting deleted with id=" + id + " [user]=" + username);
            return true;
        }catch(Exception ex){
            logger.error("Error deleting IC meeting: id=" + id + ", ]user]=" + username, ex);
        }
        return false;
    }

    @Override
    public boolean safeDeleteICMeetingTopic(Long id, String username) {
        try {
            ICMeetingTopic entity = icMeetingTopicRepository.findOne(id);
            if(!checkEditableICMeetingTopic(id)){
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

    private boolean checkEditableICMeetingTopic(Long id){
        try {
            ICMeetingTopic entity = icMeetingTopicRepository.findOne(id);
            if(entity != null){
                if(entity.getIcMeeting() != null && entity.getIcMeeting().getClosed().booleanValue()){
                    return false;
                }

                // TODO: check connected entities
                return true;
            }
        }catch(Exception ex){
            logger.error("Error checking IC meeting topic deletable: id =" + id, ex);
        }
        return false;
    }

    private boolean checkEditableICMeeting(Long id){
        try {
            ICMeeting entity = icMeetingsRepository.findOne(id);
            if(entity != null){
                if(entity.getClosed() != null && entity.getClosed().booleanValue()){
                    return false;
                }

                // TODO: check connected entities
                return false;
            }
        }catch(Exception ex){
        }
        return false;
    }

    @Override
    public CorpMeetingsPagedSearchResult search(CorpMeetingsSearchParamsDto searchParams) {
        try {
            Page<CorpMeeting> entitiesPage = null;
            int page = 0;
            if (searchParams == null || searchParams.isEmpty()) {
                int pageSize = searchParams != null && searchParams.getPageSize() > 0 ? searchParams.getPageSize() : DEFAULT_PAGE_SIZE;
                page = searchParams != null && searchParams.getPage() > 0 ? searchParams.getPage() - 1 : 0;
                entitiesPage = corpMeetingsRepository.findAllByOrderByDateDesc(new PageRequest(page, pageSize, new Sort(Sort.Direction.DESC, "date", "id")));
            } else {
                page = searchParams.getPage() > 0 ? searchParams.getPage() - 1 : 0;

                entitiesPage = corpMeetingsRepository.searchMeetings(StringUtils.isValue(searchParams.getType()) ? searchParams.getType() : null, searchParams.getNumber(),
                        searchParams.getSearchText(), searchParams.getDateFrom(), searchParams.getDateTo(),
                        new PageRequest(page, searchParams.getPageSize(), new Sort(Sort.Direction.DESC, "date", "id")));

            }

            CorpMeetingsPagedSearchResult result = new CorpMeetingsPagedSearchResult();
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
                result.setCorpMeetings(corpMeetingsEntityConverter.disassembleList(entitiesPage.getContent()));
            }
            return result;
        }catch(Exception ex){
            // TODO: log search params
            logger.error("Error searching corp meetings", ex);
        }
        return null;
    }

    @Override
    public ICMeetingTopicsPagedSearchResult searchICMeetingTopics(ICMeetingTopicsSearchParamsDto searchParams) {
        try {
            Page<ICMeetingTopic> entitiesPage = null;
            int page = 0;
            if (searchParams == null || searchParams.isEmpty()) {
                int pageSize = searchParams != null && searchParams.getPageSize() > 0 ? searchParams.getPageSize() : DEFAULT_PAGE_SIZE;
                page = searchParams != null && searchParams.getPage() > 0 ? searchParams.getPage() - 1 : 0;
                entitiesPage = icMeetingTopicRepository.searchAll(new PageRequest(page, pageSize, new Sort(Sort.Direction.DESC, "id")));
            } else {
                page = searchParams.getPage() > 0 ? searchParams.getPage() - 1 : 0;

                entitiesPage = icMeetingTopicRepository.search(searchParams.getDateFrom(), searchParams.getDateTo(), searchParams.getSearchText(),
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
    public ICMeetingsPagedSearchResult searchICMeetings(ICMeetingsSearchParamsDto searchParams) {
        try {
            Page<ICMeeting> entitiesPage = null;
            int page = 0;
            if (searchParams == null || searchParams.isEmpty()) {
                int pageSize = searchParams != null && searchParams.getPageSize() > 0 ? searchParams.getPageSize() : DEFAULT_PAGE_SIZE;
                page = searchParams != null && searchParams.getPage() > 0 ? searchParams.getPage() - 1 : 0;
                entitiesPage = icMeetingsRepository.findAll(new PageRequest(page, pageSize));
            } else {
                page = searchParams.getPage() > 0 ? searchParams.getPage() - 1 : 0;

                entitiesPage = icMeetingsRepository.search(searchParams.getNumber(), searchParams.getDateFrom(), searchParams.getDateTo(),
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
                Collections.sort(result.getIcMeetings());
                if(result.getIcMeetings() != null){
                    for(ICMeetingDto dto: result.getIcMeetings()){
                        dto.setEditable(checkEditableICMeeting(dto.getId()));
                    }
                }
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
        this.icMeetingsRepository.findAll().forEach(entity->{
            dtoList.add(this.icMeetingsEntityConverter.disassemble(entity));
        });
        Collections.sort(dtoList);
        return dtoList;
    }

    @Override
    public Set<FilesDto> saveAttachments(Long meetingId, Set<FilesDto> attachments) {
        try {
            Set<FilesDto> dtoSet = new HashSet<>();
            if (attachments != null) {
                Iterator<FilesDto> iterator = attachments.iterator();
                while (iterator.hasNext()) {
                    FilesDto filesDto = iterator.next();
                    if (filesDto.getId() == null) {
                        Long fileId = fileService.save(filesDto, FileTypeLookup.CORP_MEETING_MATERIALS.getCatalog());
                        logger.info("Saved corp meeting attachment files: meeting id=" + meetingId + ", file=" + fileId);

                        CorpMeetingFiles corpMeetingFiles = new CorpMeetingFiles(meetingId, fileId);
                        corpMeetingFilesRepository.save(corpMeetingFiles);
                        logger.info("Saved corp meeting attachment information to DB: meeting id=" + meetingId + ", file=" + fileId);

                        FilesDto newFileDto = new FilesDto();
                        newFileDto.setId(fileId);
                        newFileDto.setFileName(filesDto.getFileName());
                        dtoSet.add(newFileDto);
                    }
                }
            }
            return dtoSet;
        }catch (Exception ex){
            logger.error("Error saving corp meeting attachments: corp meeting id=" + meetingId, ex);
        }
        return null;
    }

    @Override
    public Set<FilesDto> getAttachments(Long meetingId) {
        try {
            List<CorpMeetingFiles> entities = corpMeetingFilesRepository.getFilesByMeetingId(meetingId);
            Set<FilesDto> files = new HashSet<>();
            if (entities != null) {
                for (CorpMeetingFiles corpMeetingFiles : entities) {
                    FilesDto fileDto = new FilesDto();
                    fileDto.setId(corpMeetingFiles.getFile().getId());
                    fileDto.setFileName(corpMeetingFiles.getFile().getFileName());
                    files.add(fileDto);
                }
            }
            return files;
        }catch(Exception ex){
            logger.error("Error getting corp meeting attachments: corp meeting id=" + meetingId, ex);
        }
        return null;
    }

    @Override
    public Set<FilesDto> saveICMeetingAttachments(Long meetingId, Set<FilesDto> attachments) {
        try {
            Set<FilesDto> dtoSet = new HashSet<>();
            if (attachments != null) {
                Iterator<FilesDto> iterator = attachments.iterator();
                while (iterator.hasNext()) {
                    FilesDto filesDto = iterator.next();
                    if (filesDto.getId() == null) {
                        Long fileId = fileService.save(filesDto, FileTypeLookup.IC_MATERIALS.getCatalog()); // CATALOG IS ONE
                        logger.info("Saved IC meeting attachment files: meeting id=" + meetingId + ", file=" + fileId);

                        ICMeetingFiles icMeetingFiles = new ICMeetingFiles(meetingId, fileId);
                        icMeetingFilesRepository.save(icMeetingFiles);
                        logger.info("Saved IC meeting attachment information to DB: meeting id=" + meetingId + ", file=" + fileId);

                        FilesDto newFileDto = new FilesDto();
                        newFileDto.setId(fileId);
                        newFileDto.setFileName(filesDto.getFileName());
                        dtoSet.add(newFileDto);
                    }
                }
            }
            return dtoSet;
        }catch (Exception ex){
            logger.error("Error saving IC meeting attachments: IC meeting id=" + meetingId, ex);
        }
        return null;
    }

    @Override
    public boolean safeDeleteICMeetingAttachment(Long meetingId, Long fileId, String username) {
        try {
            ICMeetingFiles entity = icMeetingFilesRepository.getFilesByFileId(fileId);
            if (entity != null && entity.getIcMeeting().getId().longValue() == meetingId) {
                boolean deleted = fileService.safeDelete(fileId);
                if(!deleted){
                    logger.error("Failed to delete(safe) IC meeting attachment: IC meeting =" + meetingId + ", file=" + fileId + ", by " + username);
                }else{
                    logger.info("Deleted(safe) IC meeting  attachment: IC meeting =" + meetingId + ", file=" + fileId + ", by " + username);
                }
                return deleted;
            }
        }catch (Exception e){
            logger.error("Failed to delete(safe) IC meeting  attachment with error: IC meeting =" + meetingId + ", file=" + fileId + ", by " + username, e);
        }
        return false;
    }


}
