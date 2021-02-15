package kz.nicnbk.service.api.corpmeetings;

import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.common.EntitySaveResponseDto;
import kz.nicnbk.service.dto.corpmeetings.*;
import kz.nicnbk.service.dto.employee.EmployeeDto;
import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.files.NamedFilesDto;

import java.util.Date;
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

    public static final int IC_MEETING_DEADLINE_DAYS = 1;
    public static final String IC_MEETING_DEADLINE_HOURS = "15:00";


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
    EntitySaveResponseDto saveICMeetingTopic(ICMeetingTopicDto dto, FilesDto explanatoryNote, List<FilesDto> filesDtoSet,  String updater);

    //EntitySaveResponseDto saveICMeetingTopicUpdate(ICMeetingTopicUpdateDto dto, FilesDto explanatoryNote, List<FilesDto> filesDtoSet,  String updater);

    ICMeetingTopicDto getICMeetingTopic(Long id, String username);

    EntitySaveResponseDto approveICMeetingTopic(Long id, String username);

    EntitySaveResponseDto cancelApproveICMeetingTopic(Long id, String username);

    List<EmployeeDto> getAvailableApproveList();

    ICMeetingTopicDto getICMeetingTopicByExplanatoryFileId(Long id);

    ICMeetingTopicDto getICMeetingTopicByMaterialFileId(Long id);

    ICMeetingTopicsPagedSearchResult searchICMeetingTopics(ICMeetingTopicsSearchParamsDto searchParams);

    List<ICMeetingTopicDto> getICMeetingTopicsByMeetingId(Long id, String username);

    List<ICMeetingTopicDto> getLimitedICMeetingTopicsByMeetingId(Long id);

    //Set<FilesDto> saveICMeetingTopicAttachments(Long topicId, Set<FilesDto> attachments, String username);

    List<NamedFilesDto> getICMeetingTopicAttachments(Long id/*, Boolean update*/);

    boolean checkViewICMeetingTopicByTopicIdAndUsername(Long id, String username);

    boolean safeDeleteICMeetingTopic(Long id, String username);

    boolean safeDeleteICMeetingTopicAttachment(Long topicId, Long fileId, String username);

    boolean deleteICMeetingTopicExplanatoryNote(Long topicId, String username);

    //boolean deleteICMeetingTopicExplanatoryNoteUpd(Long topicId, String username);

    @Deprecated
    boolean checkUserRolesForICMeetingTopicByTypeAndUsername(String type, String username, boolean editing);

    /* IC MEETING *****************************************************************************************************/

    EntitySaveResponseDto saveICMeeting(ICMeetingDto icMeetingDto, FilesDto agendaFile,  FilesDto protocolFile,
                                        FilesDto bulletinFile, String updater);

    Set<FilesDto> saveICMeetingProtocol(Long meetingId, Set<FilesDto> attachments, String username);

    ICMeetingDto getICMeeting(Long id);

    boolean safeDeleteICMeeting(Long id, String username);

    boolean closeICMeeting(Long id, String username);

    boolean reopenICMeeting(Long id, String username);

    boolean unlockICMeetingForFinalize(Long id, String username);

    boolean saveICMeetingVotes(ICMeetingVoteDto votes, String username);

    ICMeetingsPagedSearchResult searchICMeetings(ICMeetingsSearchParamsDto searchParams);

    List<ICMeetingDto> getAllICMeetingsShort();

    List<ICMeetingDto> getICMeetingsWithDeadline(Date date);

    boolean safeDeleteICMeetingProtocolAttachment(Long meetingId, Long fileId, String username);

    Set<FilesDto> getICMeetingAttachments(Long id);

    FilesDto getICMeetingAgendaFileStream(Long icMeetingId, String username);
    FilesDto getICMeetingProtocolFileStream(Long icMeetingId, String username);
    FilesDto getICMeetingBulletinFileStream(Long icMeetingId, String username);

    FilesDto getICMeetingProtocolRegistryFileStream(String username);

    FilesDto getICMeetingTopicApproveListFileStream(Long icMeetingId, String username);


    boolean deleteICMeetingAgenda(Long icMeetingId, String username);
    boolean deleteICMeetingProtocol(Long icMeetingId, String username);
    boolean deleteICMeetingBulletin(Long icMeetingId, String username);

    List<CorpMeetingUpcomingEventDto> getUpcomingEvents(String username);
}
