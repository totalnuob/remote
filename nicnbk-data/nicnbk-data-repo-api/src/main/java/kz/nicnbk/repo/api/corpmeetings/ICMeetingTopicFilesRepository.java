package kz.nicnbk.repo.api.corpmeetings;

import kz.nicnbk.repo.model.corpmeetings.ICMeetingTopicFiles;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by zhambyl on 04-Aug-16.
 */
public interface ICMeetingTopicFilesRepository extends PagingAndSortingRepository<ICMeetingTopicFiles, Long> {

    /**
     * return files entities by tripMemoId
     *
     * @param tripMemoId
     * @return
     */
    @Query("select m from ic_mmeting_topic_files m where m.icMeetingTopic.id=?1 and (m.file.deleted is null or m.file.deleted=false) " +
            " ORDER BY m.id ASC")
    List<ICMeetingTopicFiles> getFilesByMeetingId(Long tripMemoId);

    /**
     * Return entities by fileId
     *
     * @param fileId
     * @return
     */
    @Query("select m from ic_mmeting_topic_files m where m.file.id=?1  and (m.file.deleted is null or m.file.deleted=false)" +
            " ORDER BY m.id ASC")
    ICMeetingTopicFiles getFilesByFileId(Long fileId);
}
