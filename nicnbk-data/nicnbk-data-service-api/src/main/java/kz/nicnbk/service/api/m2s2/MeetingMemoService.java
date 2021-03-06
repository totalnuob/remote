package kz.nicnbk.service.api.m2s2;

import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.m2s2.MemoDeleteResultDto;
import kz.nicnbk.service.dto.m2s2.MemoPagedSearchResult;
import kz.nicnbk.service.dto.m2s2.MemoSearchParams;
import kz.nicnbk.service.dto.m2s2.MemoSearchParamsExtended;

import java.util.Set;

/**
 * Created by magzumov on 11.07.2016.
 */
public interface MeetingMemoService extends BaseService {

    /* Number of pages per view */
    int DEFAULT_PAGES_PER_VIEW = 5;

    /* Number of elements per page */
    int DEFAULT_PAGE_SIZE = 20;


    //Long save(MeetingMemoDto memoDto);

    //MeetingMemoDto get(Long id);

    int getMemoType(Long id);

    MemoPagedSearchResult search(MemoSearchParamsExtended searchParams, String username);

    boolean deleteAttachment(Long memoId, Long fileId, String username);

    boolean safeDeleteAttachment(Long memoId, Long fileId, String username);

    Set<FilesDto> getAttachments(Long memoId);

    Set<FilesDto> saveAttachments(Long memoId, Set<FilesDto> attachments);

    MemoDeleteResultDto safeDelete(Long memoId, String username);
}
