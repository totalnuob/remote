package kz.nicnbk.repo.api.corpmeetings;

import kz.nicnbk.repo.model.corpmeetings.ICMeetingFiles;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by zhambyl on 04-Aug-16.
 */
public interface ICMeetingFilesRepository extends PagingAndSortingRepository<ICMeetingFiles, Long> {

    /**
     * return files entities by tripMemoId
     *
     * @param meetingId
     * @return
     */
    @Query("select m from ic_meeting_files m where m.icMeeting.id=?1 and (m.file.deleted is null or m.file.deleted=false)")
    List<ICMeetingFiles> getFilesByMeetingId(Long meetingId);

    /**
     * Return entities by fileId
     *
     * @param fileId
     * @return
     */
    @Query("select m from ic_meeting_files m where m.file.id=?1  and (m.file.deleted is null or m.file.deleted=false)")
    ICMeetingFiles getFilesByFileId(Long fileId);
}
