package kz.nicnbk.service.impl.corpmeetings;

import kz.nicnbk.common.service.util.PaginationUtils;
import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.repo.api.corpmeetings.CorpMeetingFilesRepository;
import kz.nicnbk.repo.api.corpmeetings.CorpMeetingsRepository;
import kz.nicnbk.repo.model.corpmeetings.CorpMeeting;
import kz.nicnbk.repo.model.corpmeetings.CorpMeetingFiles;
import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.repo.model.lookup.FileTypeLookup;
import kz.nicnbk.service.api.authentication.TokenService;
import kz.nicnbk.service.api.corpmeetings.CorpMeetingService;
import kz.nicnbk.service.api.employee.EmployeeService;
import kz.nicnbk.service.api.files.FileService;
import kz.nicnbk.service.converter.corpmeetings.CorpMeetingsEntityConverter;
import kz.nicnbk.service.dto.common.EntitySaveResponseDto;
import kz.nicnbk.service.dto.common.ResponseStatusType;
import kz.nicnbk.service.dto.corpmeetings.CorpMeetingDto;
import kz.nicnbk.service.dto.corpmeetings.CorpMeetingsPagedSearchResult;
import kz.nicnbk.service.dto.corpmeetings.CorpMeetingsSearchParamsDto;
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
    private CorpMeetingFilesRepository corpMeetingFilesRepository;

    @Autowired
    private CorpMeetingsEntityConverter corpMeetingsEntityConverter;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private EmployeeService employeeService;


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
    public boolean safeDeleteAttachment(Long meetingId, Long fileId, String username) {
        try {
            CorpMeetingFiles entity = corpMeetingFilesRepository.getFilesByFileId(fileId);
            if (entity != null && entity.getCorpMeeting().getId().longValue() == meetingId) {
                boolean deleted = fileService.safeDelete(fileId);
                if(!deleted){
                    logger.error("Failed to delete(safe) corp meeting attachment: corp meeting =" + meetingId + ", file=" + fileId + ", by " + username);
                }else{
                    logger.info("Deleted(safe) corp meeting  attachment: corp meeting =" + meetingId + ", file=" + fileId + ", by " + username);
                }
                return deleted;
            }
        }catch (Exception e){
            logger.error("Failed to delete(safe) corp meeting  attachment with error: corp meeting =" + meetingId + ", file=" + fileId + ", by " + username, e);
        }
        return false;
    }


}
