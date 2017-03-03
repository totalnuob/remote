package kz.nicnbk.service.api.tripmemo;

import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.tripmemo.TripMemoDto;
import kz.nicnbk.service.dto.tripmemo.TripMemoPagedSearchResult;
import kz.nicnbk.service.dto.tripmemo.TripMemoSearchParamsDto;

import java.util.Set;

/**
 * Created by zhambyl on 04-Aug-16.
 */
public interface TripMemoService extends BaseService {

    /* Number of pages per view */
    int DEFAULT_PAGES_PER_VIEW = 5;

    /* Number of elements per page */
    int DEFAULT_PAGE_SIZE = 20;

    /**
     * Save tripMemo and return tripMemo id
     *
     * @param tripMemoDto
     * @return - id
     */
    Long save(TripMemoDto tripMemoDto);

    /**
     * get TripMemo dto by id
     *
     * @param id
     * @return TripMemo dto
     */
    TripMemoDto get(Long id);


    /**
     * Return found trip memos as search result
     *
     * @param searchParams
     * @return trip memos
     */
    TripMemoPagedSearchResult search(TripMemoSearchParamsDto searchParams);

    /**
     * Return memo attachments.
     *
     * @param tripMemoId - memo id
     * @return - attachment list
     */
    Set<FilesDto> getAttachments(Long tripMemoId);

    Set<FilesDto> saveAttachments(Long memoId, Set<FilesDto> attachments);

    /**
     * Delete memo attachment file.
     * Return true on success, false otherwise.
     *
     * @param fileId - file id
     * @return
     */
    boolean deleteAttachment(Long tripMemoId, Long fileId);

    /**
     * Save memo file info.
     * Return true on success, false otherwise.
     *
     * @param tripMemoId - memo id
     * @param fileId - file id
     * @return - true/false
     */
    public boolean saveTripMemoFileInfo(Long tripMemoId, Long fileId);


    public boolean checkAccess(String token, Long entityId);

}
