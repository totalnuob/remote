package kz.nicnbk.repo.api.corpmeetings;

import kz.nicnbk.repo.model.corpmeetings.CorpMeetingFiles;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by zhambyl on 04-Aug-16.
 */
public interface CorpMeetingFilesRepository extends PagingAndSortingRepository<CorpMeetingFiles, Long> {

    /**
     * return files entities by tripMemoId
     *
     * @param tripMemoId
     * @return
     */
    @Query("select m from corp_mmeting_files m where m.corpMeeting.id=?1 and (m.file.deleted is null or m.file.deleted=false)")
    List<CorpMeetingFiles> getFilesByMeetingId(Long tripMemoId);

    /**
     * Return entities by fileId
     *
     * @param fileId
     * @return
     */
    @Query("select m from corp_mmeting_files m where m.file.id=?1  and (m.file.deleted is null or m.file.deleted=false)")
    CorpMeetingFiles getFilesByFileId(Long fileId);
}
