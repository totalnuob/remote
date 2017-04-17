package kz.nicnbk.service.impl.tripmemo;

import kz.nicnbk.common.service.util.PaginationUtils;
import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.repo.api.employee.EmployeeRepository;
import kz.nicnbk.repo.api.tripmemo.TripMemoFilesRepository;
import kz.nicnbk.repo.api.tripmemo.TripMemoRepository;
import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.repo.model.lookup.FileTypeLookup;
import kz.nicnbk.repo.model.tripmemo.TripMemo;
import kz.nicnbk.repo.model.tripmemo.TripMemoFiles;
import kz.nicnbk.service.api.authentication.TokenService;
import kz.nicnbk.service.api.employee.EmployeeService;
import kz.nicnbk.service.api.files.FileService;
import kz.nicnbk.service.api.tripmemo.TripMemoService;
import kz.nicnbk.service.converter.tripmemo.TripMemoEntityConverter;
import kz.nicnbk.service.dto.authentication.TokenUserInfo;
import kz.nicnbk.service.dto.employee.EmployeeDto;
import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.tripmemo.TripMemoDto;
import kz.nicnbk.service.dto.tripmemo.TripMemoPagedSearchResult;
import kz.nicnbk.service.dto.tripmemo.TripMemoSearchParamsDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by zhambyl on 04-Aug-16.
 */
@Service
public class TripMemoServiceImpl implements TripMemoService {

    private static final Logger logger = LoggerFactory.getLogger(TripMemoServiceImpl.class);

    @Autowired
    private TripMemoRepository tripMemoRepository;
    @Autowired
    private TripMemoEntityConverter tripMemoConverter;
    @Autowired
    private FileService fileService;
    @Autowired
    private TripMemoFilesRepository tripMemoFilesRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private EmployeeRepository employeeRepository;


    @Override
    public Long save(TripMemoDto tripMemoDto, String updater) {
        try {
            TripMemo entity = tripMemoConverter.assemble(tripMemoDto);
            if(tripMemoDto.getId() == null){ // CREATE
                Employee employee = this.employeeRepository.findByUsername(tripMemoDto.getOwner());
                // set creator
                entity.setCreator(employee);
            }else{ // UPDATE
                // set creator
                Employee employee = this.tripMemoRepository.findOne(tripMemoDto.getId()).getCreator();
                entity.setCreator(employee);
                // set creation date
                Date creationDate = tripMemoRepository.findOne(tripMemoDto.getId()).getCreationDate();
                entity.setCreationDate(creationDate);
                // set update date
                entity.setUpdateDate(new Date());
                // set updater
                Employee updatedby = this.employeeRepository.findByUsername(updater);
                entity.setUpdater(updatedby);
            }

            Long id = tripMemoRepository.save(entity).getId();
            logger.info(tripMemoDto.getId() == null ? "Trip memo created: " + id + ", by " + entity.getCreator().getUsername() :
                    "Trip memo updated: " + id + ", by " + updater);
            return id;
        }catch (Exception ex){
            logger.error("Error saving trip memo: " + (tripMemoDto != null && tripMemoDto.getId() != null ? tripMemoDto.getId() : "new") ,ex);
            return null;
        }
    }

    @Override
    public TripMemoDto get(Long id) {
        try {
            TripMemo tripMemo = tripMemoRepository.findOne(id);
            TripMemoDto tripMemoDto = tripMemoConverter.disassemble(tripMemo);
            tripMemoDto.setFiles(getAttachments(id));
            return tripMemoDto;
        }catch(Exception ex){
            logger.error("Error loading trip memo: " + id, ex);
        }
        return null;
    }

    @Override
    public TripMemoPagedSearchResult search(TripMemoSearchParamsDto searchParams) {
        try {
            Page<TripMemo> tripMemoPage = null;
            int page = 0;
            if (searchParams == null || searchParams.isEmpty()) {
                int pageSize = searchParams != null && searchParams.getPageSize() > 0 ? searchParams.getPageSize() : DEFAULT_PAGE_SIZE;
                page = searchParams != null && searchParams.getPage() > 0 ? searchParams.getPage() - 1 : 0;
                tripMemoPage = tripMemoRepository.findAllByOrderByCreationDateDesc(new PageRequest(page, pageSize, new Sort(Sort.Direction.DESC, "meetingDateStart", "id")));
            } else {
                page = searchParams.getPage() > 0 ? searchParams.getPage() - 1 : 0;
                if (searchParams.getFromDate() == null && searchParams.getToDate() == null) {
                    tripMemoPage = tripMemoRepository.findWithoutDates(StringUtils.isValue(searchParams.getTripType()) ? searchParams.getTripType() : null,
                            searchParams.getOrganization(), searchParams.getLocation(),
                            StringUtils.isValue(searchParams.getStatus()) ? searchParams.getStatus() : null,
                            new PageRequest(page, searchParams.getPageSize(), new Sort(Sort.Direction.DESC, "meetingDateStart", "id")));
                } else if (searchParams.getFromDate() != null && searchParams.getToDate() != null) {
                    tripMemoPage = tripMemoRepository.findBothDates(StringUtils.isValue(searchParams.getTripType()) ? searchParams.getTripType() : null,
                            searchParams.getOrganization(), searchParams.getFromDate(), searchParams.getToDate(), searchParams.getLocation(),
                            StringUtils.isValue(searchParams.getStatus()) ? searchParams.getStatus() : null,
                            new PageRequest(page, searchParams.getPageSize(), new Sort(Sort.Direction.DESC, "meetingDateStart", "id")));
                } else if (searchParams.getFromDate() != null) {
                    tripMemoPage = tripMemoRepository.findDateFrom(StringUtils.isValue(searchParams.getTripType()) ? searchParams.getTripType() : null,
                            searchParams.getOrganization(), searchParams.getFromDate(), searchParams.getLocation(), StringUtils.isValue(searchParams.getStatus()) ? searchParams.getStatus() : null,
                            new PageRequest(page, searchParams.getPageSize(), new Sort(Sort.Direction.DESC, "meetingDateStart", "id")));

                } else {
                    tripMemoPage = tripMemoRepository.findDateTo(StringUtils.isValue(searchParams.getTripType()) ? searchParams.getTripType() : null,
                            searchParams.getOrganization(), searchParams.getToDate(), searchParams.getLocation(), StringUtils.isValue(searchParams.getStatus()) ? searchParams.getStatus() : null,
                            new PageRequest(page, searchParams.getPageSize(), new Sort(Sort.Direction.DESC, "meetingDateStart", "id")));
                }
            }

            TripMemoPagedSearchResult result = new TripMemoPagedSearchResult();
            if (tripMemoPage != null) {
                result.setTotalElements(tripMemoPage.getTotalElements());
                if (tripMemoPage.getTotalElements() > 0) {
                    result.setShowPageFrom(PaginationUtils.getShowPageFrom(DEFAULT_PAGES_PER_VIEW, page));
                    result.setShowPageTo(PaginationUtils.getShowPageTo(DEFAULT_PAGES_PER_VIEW,
                            page, result.getShowPageFrom(), tripMemoPage.getTotalPages()));
                }
                result.setTotalPages(tripMemoPage.getTotalPages());
                result.setCurrentPage(page + 1);
                if (searchParams != null) {
                    result.setSearchParams(searchParams.getSearchParamsAsString());
                }
                result.setTripMemos(tripMemoConverter.disassembleList(tripMemoPage.getContent()));
            }
            return result;
        }catch(Exception ex){
            // TODO: log search params
            logger.error("Error searching trip memos", ex);
        }
        return null;
    }

    @Override
    public Set<FilesDto> getAttachments(Long tripMemoId) {
        try {
            List<TripMemoFiles> entities = tripMemoFilesRepository.getFilesByTripMemoId(tripMemoId);
            Set<FilesDto> files = new HashSet<>();
            if (entities != null) {
                for (TripMemoFiles tripMemoFile : entities) {
                    FilesDto fileDto = new FilesDto();
                    fileDto.setId(tripMemoFile.getFile().getId());
                    fileDto.setFileName(tripMemoFile.getFile().getFileName());
                    files.add(fileDto);
                }
            }
            return files;
        }catch(Exception ex){
            logger.error("Error getting trip memo attachments: trip memo=" + tripMemoId, ex);
        }
        return null;
    }

    @Override
    public Set<FilesDto> saveAttachments(Long tripMemoId, Set<FilesDto> attachments) {
        try {
            Set<FilesDto> dtoSet = new HashSet<>();
            if (attachments != null) {
                Iterator<FilesDto> iterator = attachments.iterator();
                while (iterator.hasNext()) {
                    FilesDto filesDto = iterator.next();
                    if (filesDto.getId() == null) {
                        Long fileId = fileService.save(filesDto, FileTypeLookup.MEMO_ATTACHMENT.getCatalog());
                        logger.info("Saved trip memo attachment file: trip memo=" + tripMemoId + ", file=" + fileId);

                        TripMemoFiles tripMemoFiles = new TripMemoFiles(tripMemoId, fileId);
                        tripMemoFilesRepository.save(tripMemoFiles);
                        logger.info("Saved trip memo attachment info: trip memo=" + tripMemoId + ", file=" + fileId);

                        FilesDto newFileDto = new FilesDto();
                        newFileDto.setId(fileId);
                        newFileDto.setFileName(filesDto.getFileName());
                        dtoSet.add(newFileDto);
                    }
                }
            }
            return dtoSet;
        }catch (Exception ex){
            logger.error("Error saving trip memo attachments: trip memo=" + tripMemoId, ex);
        }
        return null;
    }

    @Override
    public boolean deleteAttachment(Long tripMemoId, Long fileId, String username) {
        try {
            TripMemoFiles entity = tripMemoFilesRepository.getFilesByFileId(fileId);
            if (entity != null && entity.getTripMemo().getId().longValue() == tripMemoId) {
                tripMemoFilesRepository.delete(entity);

                boolean deleted = fileService.delete(fileId);
                if(!deleted){
                    logger.error("Failed to delete trip memo attachment: trip memo=" + tripMemoId + ", file=" + fileId + ", by " + username);
                }else{
                    logger.info("Deleted trip memo attachment: trip memo=" + tripMemoId + ", file=" + fileId + ", by " + username);
                }
                return deleted;
            }
        }catch (Exception e){
            logger.error("Failed to delete trip memo attachment with error: trip memo=" + tripMemoId + ", file=" + fileId + ", by " + username, e);
        }
        return false;
    }

    @Override
    public boolean safeDeleteAttachment(Long tripMemoId, Long fileId, String username) {
        try {
            TripMemoFiles entity = tripMemoFilesRepository.getFilesByFileId(fileId);
            if (entity != null && entity.getTripMemo().getId().longValue() == tripMemoId) {
                boolean deleted = fileService.safeDelete(fileId);
                if(!deleted){
                    logger.error("Failed to delete(safe) trip memo attachment: trip memo=" + tripMemoId + ", file=" + fileId + ", by " + username);
                }else{
                    logger.info("Deleted(safe) trip memo attachment: trip memo=" + tripMemoId + ", file=" + fileId + ", by " + username);
                }
                return deleted;
            }
        }catch (Exception e){
            logger.error("Failed to delete(safe) trip memo attachment with error: trip memo=" + tripMemoId + ", file=" + fileId + ", by " + username, e);
        }
        return false;
    }

//    @Override
//    public boolean saveTripMemoFileInfo(Long tripMemoId, Long fileId) {
//        TripMemoFiles tripMemoFiles = new TripMemoFiles(tripMemoId, fileId);
//        tripMemoFilesRepository.save(tripMemoFiles);
//        return true;
//    }

    @Override
    public boolean checkOwner(String token, Long entityId) {
        TokenUserInfo tokenUserInfo = this.tokenService.decode(token);
        // check admin role
        for(String role: tokenUserInfo.getRoles()){
            if(role.equalsIgnoreCase("ROLE_ADMIN")){
                return true;
            }
        }
        if(tokenUserInfo != null){
            EmployeeDto employeeDto = this.employeeService.findByUsername(tokenUserInfo.getUsername());
            if(employeeDto != null){
                TripMemo memo = this.tripMemoRepository.findOne(entityId);
                if(memo != null){
                    return memo.getCreator() == null || (employeeDto.getId() == memo.getCreator().getId());
                }
            }
        }
        return false;
    }
}
