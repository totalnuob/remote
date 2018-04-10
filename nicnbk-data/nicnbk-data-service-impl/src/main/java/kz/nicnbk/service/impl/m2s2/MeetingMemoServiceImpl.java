package kz.nicnbk.service.impl.m2s2;

import kz.nicnbk.common.service.util.PaginationUtils;
import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.repo.api.m2s2.*;
import kz.nicnbk.repo.model.lookup.FileTypeLookup;
import kz.nicnbk.repo.model.m2s2.MeetingMemo;
import kz.nicnbk.repo.model.m2s2.MemoFiles;
import kz.nicnbk.service.api.files.FileService;
import kz.nicnbk.service.api.m2s2.MeetingMemoService;
import kz.nicnbk.service.converter.m2s2.MeetingMemoEntityConverter;
import kz.nicnbk.service.dto.common.StatusResultType;
import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.m2s2.*;
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
 * Created by magzumov on 18.07.2016.
 */
@Service
public class MeetingMemoServiceImpl implements MeetingMemoService {

    private static final Logger logger = LoggerFactory.getLogger(MeetingMemoServiceImpl.class);

    @Autowired
    private MeetingMemoEntityConverter memoConverter;

    @Autowired
    private MeetingMemoRepository memoRepository;

    @Autowired
    private MemoFilesRepository memoFilesRepository;

    @Autowired
    private FileService fileService;

    @Autowired
    private PEMeetingMemoRepository peMeetingMemoRepository;

    @Autowired
    private HFMeetingMemoRepository hfMeetingMemoRepository;

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
    public int getMemoType(Long id) {
        MeetingMemo memo = this.memoRepository.findOne(id);
        if(memo != null){
            return memo.getMemoType();
        }
        return 0;
    }

    @Override
    public MemoPagedSearchResult search(MemoSearchParamsExtended searchParams, String username) {

        try {
            Page<MeetingMemo> memoPage = null;
            int page = 0;
            int pageSize = searchParams != null && searchParams.getPageSize() > 0 ? searchParams.getPageSize() : DEFAULT_PAGE_SIZE;
            if (searchParams == null || searchParams.isEmpty()) {
                page = searchParams != null && searchParams.getPage() > 0 ? searchParams.getPage() - 1 : 0;
                memoPage = memoRepository.findAllByOrderByMeetingDateDesc(new PageRequest(page, pageSize, new Sort(Sort.Direction.DESC, "meetingDate", "id")));
            } else {
                String user = searchParams.isOnlyMyOwn() ? username : null;
                page = searchParams.getPage() > 0 ? searchParams.getPage() - 1 : 0;
                if (searchParams.getFromDate() == null && searchParams.getToDate() == null) {
                    memoPage = memoRepository.findWithoutDates(StringUtils.isValue(searchParams.getMeetingType()) ? searchParams.getMeetingType() : null,
                            searchParams.getMemoType(),
                            searchParams.getFirmName(),
                            searchParams.getFundName(),
                            user,
                            new PageRequest(page, pageSize, new Sort(Sort.Direction.DESC, "meetingDate", "id")));
                } else if (searchParams.getFromDate() != null && searchParams.getToDate() != null) {
                    memoPage = memoRepository.findBothDates(StringUtils.isValue(searchParams.getMeetingType()) ? searchParams.getMeetingType() : null,
                            //StringUtils.isValue(searchParams.getMemoType()) ? searchParams.getMemoType() : null,
                            searchParams.getMemoType(),
                            searchParams.getFirmName(), searchParams.getFundName(), searchParams.getFromDate(), searchParams.getToDate(),
                            user,
                            new PageRequest(page, pageSize, new Sort(Sort.Direction.DESC, "meetingDate", "id")));
                } else if (searchParams.getFromDate() != null) {
                    memoPage = memoRepository.findDateFrom(StringUtils.isValue(searchParams.getMeetingType()) ? searchParams.getMeetingType() : null,
                            //StringUtils.isValue(searchParams.getMemoType()) ? searchParams.getMemoType() : null,
                            searchParams.getMemoType(),
                            searchParams.getFirmName(), searchParams.getFundName(), searchParams.getFromDate(),
                            user,
                            new PageRequest(page, pageSize, new Sort(Sort.Direction.DESC, "meetingDate", "id")));

                } else {
                    memoPage = memoRepository.findDateTo(StringUtils.isValue(searchParams.getMeetingType()) ? searchParams.getMeetingType() : null,
                            //StringUtils.isValue(searchParams.getMemoType()) ? searchParams.getMemoType() : null,
                            searchParams.getMemoType(),
                            searchParams.getFirmName(), searchParams.getFundName(), searchParams.getToDate(),
                            user,
                            new PageRequest(page, pageSize, new Sort(Sort.Direction.DESC, "meetingDate", "id")));
                }
            }
            MemoPagedSearchResult result = new MemoPagedSearchResult();
            if (memoPage != null) {
                result.setTotalElements(memoPage.getTotalElements());
                if (memoPage.getTotalElements() > 0) {
                    result.setShowPageFrom(PaginationUtils.getShowPageFrom(DEFAULT_PAGES_PER_VIEW, page + 1));
                    result.setShowPageTo(PaginationUtils.getShowPageTo(DEFAULT_PAGES_PER_VIEW,
                            page + 1, result.getShowPageFrom(), memoPage.getTotalPages()));
                }
                result.setTotalPages(memoPage.getTotalPages());
                result.setCurrentPage(page + 1);
                if (searchParams != null) {
                    result.setSearchParams(searchParams.getSearchParamsAsString());
                }
                result.setMemos(memoConverter.disassembleList(memoPage.getContent()));


                // TODO: temp, need a new DB structure
                // firm and fund names
                for (MeetingMemoDto memoDto : result.getMemos()) {
                    if (memoDto.getMemoType() == 2) {
                        // PE memo
                        String firmName = this.peMeetingMemoRepository.getFirmNameByMemoId(memoDto.getId());
                        String fundName = this.peMeetingMemoRepository.getFundNameByMemoId(memoDto.getId());
                        memoDto.setFirmName(firmName);
                        memoDto.setFundName(fundName);
                    } else if (memoDto.getMemoType() == 3) {
                        // HF memo
                        String firmName = this.hfMeetingMemoRepository.getManagerNameByMemoId(memoDto.getId());
                        String fundName = this.hfMeetingMemoRepository.getFundNameByMemoId(memoDto.getId());
                        memoDto.setFirmName(firmName);
                        memoDto.setFundName(fundName);
                    }
                }
            }
            return result;
        }catch(Exception ex){
            // TODO: log search params
            logger.error("Error searching memos", ex);
        }
        return null;
    }

    @Override
    public boolean deleteAttachment(Long memoId, Long fileId, String username) {

        // TODO: transactions
        // TODO: get boolean result

        try {
            // delete memo file association
            MemoFiles entity = memoFilesRepository.getFilesByFileId(fileId);

            // delete files entry

            if (entity != null && entity.getMemo().getId().longValue() == memoId) {
                memoFilesRepository.delete(entity);
                // delete file
                boolean deleted = fileService.delete(fileId);
                if(!deleted){
                    logger.error("Failed to delete meeting memo attachment: memo=" + memoId + ", file=" + fileId + ", by " + username);
                }else{
                    logger.info("Deleted meeting memo attachment: memo=" + memoId + ", file=" + fileId + ", by " + username);
                }
                return deleted;
            }else{
                logger.error("Failed to delete meeting memo attachment: memo=" + memoId + ", file=" + fileId + ", by " + username +
                        ". Memo-File entity not found");
            }
        }catch (Exception e){
            logger.error("Failed to delete attachment with exception: memo=" + memoId + ", file=" + fileId + ", by " + username, e);
        }
        return false;
    }

    @Override
    public boolean safeDeleteAttachment(Long memoId, Long fileId, String username) {
        try{
            MemoFiles entity = memoFilesRepository.getFilesByFileId(fileId);
            if (entity != null && entity.getMemo().getId().longValue() == memoId) {
                boolean deleted = fileService.safeDelete(fileId);
                if(!deleted){
                    logger.error("Failed to delete(safe) meeting memo attachment: memo=" + memoId + ", file=" + fileId + ", by " + username);
                }else{
                    logger.info("Deleted(safe) meeting memo attachment: memo=" + memoId + ", file=" + fileId + ", by " + username);
                }
                return true;
            }else{
                logger.error("Failed to delete(safe) meeting memo attachment: memo=" + memoId + ", file=" + fileId + ", by " + username +
                        ". Memo-File entity not found");
            }
        }catch (Exception ex){
            logger.error("Failed to delete(safe) attachment with exception: memo=" + memoId + ", file=" + fileId + ", by " + username, ex);
        }
        return false;
    }

    @Override
    public Set<FilesDto> getAttachments(Long memoId){
        try {
            List<MemoFiles> entities = memoFilesRepository.getFilesByMemoId(memoId);
            Set<FilesDto> files = new HashSet<>();
            if (entities != null) {
                for (MemoFiles memoFile : entities) {
                    FilesDto fileDto = new FilesDto();
                    fileDto.setId(memoFile.getFile().getId());
                    fileDto.setFileName(memoFile.getFile().getFileName());
                    files.add(fileDto);
                }
            }
            return files;
        }catch(Exception ex){
            logger.error("Error getting meeting memo attachments: memo=" + memoId, ex);
        }
        return null;
    }

    @Override
    public Set<FilesDto> saveAttachments(Long memoId, Set<FilesDto> attachments) {
        try {
            Set<FilesDto> dtoSet = new HashSet<>();
            if (attachments != null) {
                Iterator<FilesDto> iterator = attachments.iterator();
                while (iterator.hasNext()) {
                    FilesDto filesDto = iterator.next();
                    if (filesDto.getId() == null) {
                        Long fileId = fileService.save(filesDto, FileTypeLookup.MEMO_ATTACHMENT.getCatalog());
                        logger.info("Saved memo attachment file: memo=" + memoId + ", file=" + fileId);
                        MemoFiles memoFiles = new MemoFiles(memoId, fileId);
                        memoFilesRepository.save(memoFiles);
                        logger.info("Saved memo attachment info: memo=" + memoId + ", file=" + fileId);

                        FilesDto newFileDto = new FilesDto();
                        newFileDto.setId(fileId);
                        newFileDto.setFileName(filesDto.getFileName());
                        dtoSet.add(newFileDto);
                    }
                }
            }
            return dtoSet;
        }catch (Exception ex){
            logger.error("Error saving memo attachments: memo=" + memoId, ex);
        }
        return null;
    }

    @Override
    public MemoDeleteResultDto safeDelete(Long memoId) {
        try {
            if (memoId % 2 == 0) {
                return new MemoDeleteResultDto("Done!", StatusResultType.SUCCESS, "", "Successfully deleted memo", "");
            } else {
                return new MemoDeleteResultDto("Not done!", StatusResultType.FAIL, "", "Error deleting memo", "");
            }
        } catch (Exception ex){
            logger.error("Error deleting memo: memo=" + memoId, ex);
            return new MemoDeleteResultDto("Not done!", StatusResultType.FAIL, "", "Error deleting memo", "");
        }
    }
}
