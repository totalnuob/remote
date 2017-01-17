package kz.nicnbk.service.impl.tripmemo;

import kz.nicnbk.common.service.util.PaginationUtils;
import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.repo.api.tripmemo.TripMemoFilesRepository;
import kz.nicnbk.repo.api.tripmemo.TripMemoRepository;
import kz.nicnbk.repo.model.lookup.FileTypeLookup;
import kz.nicnbk.repo.model.tripmemo.TripMemo;
import kz.nicnbk.repo.model.tripmemo.TripMemoFiles;
import kz.nicnbk.service.api.files.FileService;
import kz.nicnbk.service.api.tripmemo.TripMemoService;
import kz.nicnbk.service.converter.tripmemo.TripMemoEntityConverter;
import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.tripmemo.TripMemoDto;
import kz.nicnbk.service.dto.tripmemo.TripMemoPagedSearchResult;
import kz.nicnbk.service.dto.tripmemo.TripMemoSearchParamsDto;
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
public class TripMemoServiceImpl implements TripMemoService {

    @Autowired
    private TripMemoRepository tripMemoRepository;
    @Autowired
    private TripMemoEntityConverter tripMemoConverter;
    @Autowired
    private FileService fileService;
    @Autowired
    private TripMemoFilesRepository tripMemoFilesRepository;


    @Override
    public Long save(TripMemoDto tripMemoDto) {
        TripMemo tripMemo = tripMemoConverter.assemble(tripMemoDto);
        Long id = tripMemoRepository.save(tripMemo).getId();
        return id;
    }

    @Override
    public TripMemoDto get(Long id) {
        TripMemo tripMemo = tripMemoRepository.findOne(id);
        TripMemoDto tripMemoDto = tripMemoConverter.disassemble(tripMemo);
        tripMemoDto.setFiles(getAttachments(id));
        return tripMemoDto;
    }

    @Override
    public TripMemoPagedSearchResult search(TripMemoSearchParamsDto searchParams) {
        Page<TripMemo> tripMemoPage = null;
        int page = 0;
        if(searchParams == null || searchParams.isEmpty()) {
            int pageSize = searchParams != null && searchParams.getPageSize() > 0 ? searchParams.getPageSize() : DEFAULT_PAGE_SIZE;
            page = searchParams != null && searchParams.getPage() > 0 ? searchParams.getPage() - 1 : 0;
            tripMemoPage = tripMemoRepository.findAllByOrderByCreationDateDesc(new PageRequest(page, pageSize, new Sort(Sort.Direction.DESC, "creationDate", "id")));
        } else {
            page = searchParams.getPage() > 0 ? searchParams.getPage() - 1 : 0;
            if(searchParams.getFromDate() == null && searchParams.getToDate() == null){
                tripMemoPage = tripMemoRepository.findWithoutDates(StringUtils.isValue(searchParams.getTripType()) ? searchParams.getTripType() : null,
                        searchParams.getOrganization(), searchParams.getLocation(),
                        StringUtils.isValue(searchParams.getStatus()) ? searchParams.getStatus() : null,
                        new PageRequest(page, searchParams.getPageSize(), new Sort(Sort.Direction.DESC, "meetingDateStart")));
            }else if(searchParams.getFromDate() != null && searchParams.getToDate() != null){
                tripMemoPage = tripMemoRepository.findBothDates(StringUtils.isValue(searchParams.getTripType()) ? searchParams.getTripType() : null,
                        searchParams.getOrganization(),  searchParams.getFromDate(), searchParams.getToDate(), searchParams.getLocation(),
                        StringUtils.isValue(searchParams.getStatus()) ? searchParams.getStatus() : null,
                        new PageRequest(page, searchParams.getPageSize(), new Sort(Sort.Direction.DESC, "meetingDateStart")));
            }else if(searchParams.getFromDate() != null){
                tripMemoPage = tripMemoRepository.findDateFrom(StringUtils.isValue(searchParams.getTripType()) ? searchParams.getTripType() : null,
                        searchParams.getOrganization(), searchParams.getFromDate(), searchParams.getLocation(),StringUtils.isValue(searchParams.getStatus()) ? searchParams.getStatus() : null,
                        new PageRequest(page, searchParams.getPageSize(), new Sort(Sort.Direction.DESC, "meetingDateStart")));

            }else {
                tripMemoPage = tripMemoRepository.findDateTo(StringUtils.isValue(searchParams.getTripType()) ? searchParams.getTripType() : null,
                        searchParams.getOrganization(), searchParams.getToDate(), searchParams.getLocation(),StringUtils.isValue(searchParams.getStatus()) ? searchParams.getStatus() : null,
                        new PageRequest(page, searchParams.getPageSize(), new Sort(Sort.Direction.DESC, "meetingDateStart"))); }
        }

        TripMemoPagedSearchResult result = new TripMemoPagedSearchResult();
        if(tripMemoPage != null) {
            result.setTotalElements(tripMemoPage.getTotalElements());
            if(tripMemoPage.getTotalElements() > 0) {
                result.setShowPageFrom(PaginationUtils.getShowPageFrom(DEFAULT_PAGES_PER_VIEW, page));
                result.setShowPageTo(PaginationUtils.getShowPageTo(DEFAULT_PAGES_PER_VIEW,
                        page, result.getShowPageFrom(), tripMemoPage.getTotalPages()));
            }
            result.setTotalPages(tripMemoPage.getTotalPages());
            result.setCurrentPage(page + 1);
            if(searchParams != null) {
                result.setSearchParams(searchParams.getSearchParamsAsString());
            }
            result.setTripMemos(tripMemoConverter.disassembleList(tripMemoPage.getContent()));
        }
        return result;
    }

    @Override
    public Set<FilesDto> getAttachments(Long tripMemoId) {
        List<TripMemoFiles> entities = tripMemoFilesRepository.getFilesByTripMemoId(tripMemoId);
        Set<FilesDto> files = new HashSet<>();
        if(entities != null) {
            for(TripMemoFiles tripMemoFile:entities) {
                FilesDto fileDto = new FilesDto();
                fileDto.setId(tripMemoFile.getFile().getId());
                fileDto.setFileName(tripMemoFile.getFile().getFileName());
                files.add(fileDto);
            }
        }
        return files;
    }

    @Override
    public Set<FilesDto> saveAttachments(Long tripMemoId, Set<FilesDto> attachments) {
        Set<FilesDto> dtoSet = new HashSet<>();
        if(attachments != null){
            Iterator<FilesDto> iterator = attachments.iterator();
            while(iterator.hasNext()){
                FilesDto filesDto = iterator.next();
                if(filesDto.getId() == null){
                    Long fileId = fileService.save(filesDto, FileTypeLookup.MEMO_ATTACHMENT.getCatalog());
                    TripMemoFiles tripMemoFiles = new TripMemoFiles(tripMemoId, fileId);
                    tripMemoFilesRepository.save(tripMemoFiles);

                    FilesDto newFileDto = new FilesDto();
                    newFileDto.setId(fileId);
                    newFileDto.setFileName(filesDto.getFileName());
                    dtoSet.add(newFileDto);
                }
            }
        }
        return dtoSet;
    }

    @Override
    public boolean deleteAttachment(Long tripMemoId, Long fileId) {
        TripMemoFiles entity = tripMemoFilesRepository.getFilesByFileId(fileId);
        if(entity != null &&  entity.getTripMemo().getId().longValue() == tripMemoId) {
            tripMemoFilesRepository.delete(entity);

            boolean deleted = fileService.delete(fileId);

            return deleted;
        }
        return false;
    }

    @Override
    public boolean saveTripMemoFileInfo(Long tripMemoId, Long fileId) {
        TripMemoFiles tripMemoFiles = new TripMemoFiles(tripMemoId, fileId);
        tripMemoFilesRepository.save(tripMemoFiles);
        return true;
    }
}
