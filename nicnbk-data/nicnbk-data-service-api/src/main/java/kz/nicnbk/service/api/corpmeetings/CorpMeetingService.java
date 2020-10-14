package kz.nicnbk.service.api.corpmeetings;

import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.common.EntitySaveResponseDto;
import kz.nicnbk.service.dto.corpmeetings.*;
import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.files.NamedFilesDto;

import java.util.List;
import java.util.Set;

/**
 * Created by magzumov on 04-Aug-16.
 */
public interface CorpMeetingService extends BaseService {

    /* Number of pages per view */
    int DEFAULT_PAGES_PER_VIEW = 5;

    /* Number of elements per page */
    int DEFAULT_PAGE_SIZE = 20;


//    @Deprecated
//    EntitySaveResponseDto save(CorpMeetingDto corpMeetingDto, String updater);
//    @Deprecated
//    CorpMeetingDto get(Long id);
//    @Deprecated
//    boolean safeDelete(Long id, String username);
//    @Deprecated
//    CorpMeetingsPagedSearchResult search(CorpMeetingsSearchParamsDto searchParams);
//    @Deprecated
//    Set<FilesDto> saveAttachments(Long meetingId, Set<FilesDto> attachments);
//    @Deprecated
//    Set<FilesDto> getAttachments(Long meetingId);
//    @Deprecated
//    boolean safeDeleteICMeetingAttachment(Long meetingId, Long fileId, String username);


    /* IC MEETING TOPIC ***********************************************************************************************/
    EntitySaveResponseDto saveICMeetingTopic(ICMeetingTopicDto dto, String updater);

    ICMeetingTopicDto getICMeetingTopic(Long id);

    ICMeetingTopicsPagedSearchResult searchICMeetingTopics(ICMeetingTopicsSearchParamsDto searchParams, String username);

    Set<FilesDto> saveICMeetingTopicAttachments(Long topicId, Set<FilesDto> attachments, String username);

    Set<NamedFilesDto> getICMeetingTopicAttachments(Long id);

    boolean safeDeleteICMeetingTopic(Long id, String username);

    boolean safeDeleteICMeetingTopicAttachment(Long topicId, Long fileId, String username);

    @Deprecated
    boolean checkUserRolesForICMeetingTopicByTypeAndUsername(String type, String username, boolean editing);

    /* IC MEETING *****************************************************************************************************/

    EntitySaveResponseDto saveICMeeting(ICMeetingDto icMeetingDto, String updater);

    Set<FilesDto> saveICMeetingProtocol(Long meetingId, Set<FilesDto> attachments, String username);

    ICMeetingDto getICMeeting(Long id);

    boolean safeDeleteICMeeting(Long id, String username);

    ICMeetingsPagedSearchResult searchICMeetings(ICMeetingsSearchParamsDto searchParams);

    List<ICMeetingDto> getAllICMeetings();

    boolean safeDeleteICMeetingProtocolAttachment(Long meetingId, Long fileId, String username);

    Set<FilesDto> getICMeetingAttachments(Long id);

}
