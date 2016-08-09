package kz.nicnbk.service.impl.m2s2;

import kz.nicnbk.common.service.util.PaginationUtils;
import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.repo.api.m2s2.MeetingMemoRepository;
import kz.nicnbk.repo.api.m2s2.MemoFilesRepository;
import kz.nicnbk.repo.model.lookup.FileTypeLookup;
import kz.nicnbk.repo.model.m2s2.MeetingMemo;
import kz.nicnbk.repo.model.m2s2.MemoFiles;
import kz.nicnbk.service.api.files.FileService;
import kz.nicnbk.service.api.m2s2.MeetingMemoService;
import kz.nicnbk.service.converter.m2s2.MeetingMemoEntityConverter;
import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.m2s2.MeetingMemoDto;
import kz.nicnbk.service.dto.m2s2.MemoPagedSearchResult;
import kz.nicnbk.service.dto.m2s2.MemoSearchParams;
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
 * Created by magzumov on 18.07.2016.
 */
@Service
public class MeetingMemoServiceImpl implements MeetingMemoService {

    @Autowired
    private MeetingMemoEntityConverter memoConverter;

    @Autowired
    private MeetingMemoRepository memoRepository;

    @Autowired
    private MemoFilesRepository memoFilesRepository;

    @Autowired
    private FileService fileService;

    //@Override
    private Long save(MeetingMemoDto memoDto) {
        // save memo
        MeetingMemo memo = memoConverter.assemble(memoDto);
        Long memoId = memoRepository.save(memo).getId();

        // save files
//        if(memoDto.getFiles() != null && !memoDto.getFiles().isEmpty()){
//            for(FilesDto filesDto: memoDto.getFiles()){
//                Long fileId = fileService.save(filesDto, FileTypeLookup.MEMO_ATTACHMENT.getCode());
//                MemoFiles memoFiles = new MemoFiles(memoId, fileId);
//                memoFilesRepository.save(memoFiles);
//            }
//        }

        return memoId;
    }

    //@Override
    private MeetingMemoDto get(Long id) {
        MeetingMemo memo = memoRepository.findOne(id);
        MeetingMemoDto memoDto = memoConverter.disassemble(memo);
        memoDto.setFiles(getAttachments(id));
        return memoDto;
    }

    @Override
    public MemoPagedSearchResult search(MemoSearchParams searchParams) {
        Page<MeetingMemo> memoPage = null;
        int page = 0;
        int pageSize = searchParams != null && searchParams.getPageSize() > 0 ? searchParams.getPageSize() : DEFAULT_PAGE_SIZE;
        if(searchParams == null || searchParams.isEmpty()){
            page = searchParams != null && searchParams.getPage() > 0 ? searchParams.getPage() - 1 : 0;
            memoPage = memoRepository.findAllByOrderByMeetingDateDesc(new PageRequest(page, pageSize));
        }else {
            page = searchParams.getPage() > 0 ? searchParams.getPage() - 1 : 0;
            if (searchParams.getFromDate() == null && searchParams.getToDate() == null) {
                memoPage = memoRepository.findWithoutDates(StringUtils.isValue(searchParams.getMeetingType()) ? searchParams.getMeetingType() : null,
                        searchParams.getMemoType(),
                        searchParams.getFirmName(),
                        searchParams.getFundName(),
                        new PageRequest(page, pageSize, new Sort(Sort.Direction.DESC, "meetingDate")));
            } else if (searchParams.getFromDate() != null && searchParams.getToDate() != null) {
                memoPage = memoRepository.findBothDates(StringUtils.isValue(searchParams.getMeetingType()) ? searchParams.getMeetingType() : null,
                        //StringUtils.isValue(searchParams.getMemoType()) ? searchParams.getMemoType() : null,
                        searchParams.getMemoType(),
                        searchParams.getFirmName(), searchParams.getFundName(), searchParams.getFromDate(), searchParams.getToDate(),
                        new PageRequest(page, pageSize, new Sort(Sort.Direction.DESC, "meetingDate")));
            } else if (searchParams.getFromDate() != null) {
                memoPage = memoRepository.findDateFrom(StringUtils.isValue(searchParams.getMeetingType()) ? searchParams.getMeetingType() : null,
                        //StringUtils.isValue(searchParams.getMemoType()) ? searchParams.getMemoType() : null,
                        searchParams.getMemoType(),
                        searchParams.getFirmName(), searchParams.getFundName(), searchParams.getFromDate(), new PageRequest(page, pageSize, new Sort(Sort.Direction.DESC, "meetingDate")));

            } else {
                memoPage = memoRepository.findDateTo(StringUtils.isValue(searchParams.getMeetingType()) ? searchParams.getMeetingType() : null,
                        //StringUtils.isValue(searchParams.getMemoType()) ? searchParams.getMemoType() : null,
                        searchParams.getMemoType(),
                        searchParams.getFirmName(), searchParams.getFundName(), searchParams.getToDate(), new PageRequest(page, pageSize, new Sort(Sort.Direction.DESC, "meetingDate")));
            }
        }
        MemoPagedSearchResult result = new MemoPagedSearchResult();
        if(memoPage != null) {
            result.setTotalElements(memoPage.getTotalElements());
            if(memoPage.getTotalElements() > 0) {
                result.setShowPageFrom(PaginationUtils.getShowPageFrom(DEFAULT_PAGES_PER_VIEW, page + 1));
                result.setShowPageTo(PaginationUtils.getShowPageTo(DEFAULT_PAGES_PER_VIEW,
                        page + 1, result.getShowPageFrom(), memoPage.getTotalPages()));
            }
            result.setTotalPages(memoPage.getTotalPages());
            result.setCurrentPage(page + 1);
            if(searchParams != null) {
                result.setSearchParams(searchParams.getSearchParamsAsString());
            }
            result.setMemos(memoConverter.disassembleList(memoPage.getContent()));
        }
        return result;
    }

    @Override
    public boolean deleteAttachment(Long memoId, Long fileId) {

        // TODO: transactions
        // TODO: get boolean result

        // delete memo file association
        MemoFiles entity = memoFilesRepository.getFilesByFileId(fileId);

        if(entity != null && entity.getMemo().getId().longValue() == memoId){
            memoFilesRepository.delete(entity);
            // delete file
            boolean deleted = fileService.delete(fileId);

            return deleted;
        }
        return false;
    }

    @Override
    public Set<FilesDto> getAttachments(Long memoId){
        List<MemoFiles> entities = memoFilesRepository.getFilesByMemoId(memoId);
        Set<FilesDto> files = new HashSet<>();
        if(entities != null){
            for(MemoFiles memoFile: entities){
                FilesDto fileDto = new FilesDto();
                fileDto.setId(memoFile.getFile().getId());
                fileDto.setFileName(memoFile.getFile().getFileName());
                files.add(fileDto);
            }
        }
        return files;
    }

    @Override
    public Set<FilesDto> saveAttachments(Long memoId, Set<FilesDto> attachments) {
        Set<FilesDto> dtoSet = new HashSet<>();
        if(attachments != null){
            Iterator<FilesDto> iterator = attachments.iterator();
            while(iterator.hasNext()){
                FilesDto filesDto = iterator.next();
                if(filesDto.getId() == null){
                    Long fileId = fileService.save(filesDto, FileTypeLookup.MEMO_ATTACHMENT.getCatalog());
                    MemoFiles memoFiles = new MemoFiles(memoId, fileId);
                    memoFilesRepository.save(memoFiles);

                    FilesDto newFileDto = new FilesDto();
                    newFileDto.setId(fileId);
                    newFileDto.setFileName(filesDto.getFileName());
                    dtoSet.add(newFileDto);
                }
            }
        }
        return dtoSet;
    }
}
