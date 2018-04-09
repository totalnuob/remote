package kz.nicnbk.service.api.corpmeetings;

import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.common.EntitySaveResponseDto;
import kz.nicnbk.service.dto.corpmeetings.CorpMeetingDto;
import kz.nicnbk.service.dto.corpmeetings.CorpMeetingsPagedSearchResult;
import kz.nicnbk.service.dto.corpmeetings.CorpMeetingsSearchParamsDto;
import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.tripmemo.TripMemoDto;
import kz.nicnbk.service.dto.tripmemo.TripMemoPagedSearchResult;
import kz.nicnbk.service.dto.tripmemo.TripMemoSearchParamsDto;

import java.util.Set;

/**
 * Created by magzumov on 04-Aug-16.
 */
public interface CorpMeetingService extends BaseService {

    /* Number of pages per view */
    int DEFAULT_PAGES_PER_VIEW = 5;

    /* Number of elements per page */
    int DEFAULT_PAGE_SIZE = 20;

    /**
     * Save corp meeting and return id
     *
     * @param corpMeetingDto
     * @param updater - user updating memo
     * @return - id
     */
    EntitySaveResponseDto save(CorpMeetingDto corpMeetingDto, String updater);

    /**
     * get CorpMeeting dto by id
     *
     * @param id
     * @return CorpMeeting dto
     */
    CorpMeetingDto get(Long id);


    /**
     * Return found corp meetings as search result
     *
     * @param searchParams
     * @return corp meetings
     */
    CorpMeetingsPagedSearchResult search(CorpMeetingsSearchParamsDto searchParams);

}
