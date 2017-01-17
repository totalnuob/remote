package kz.nicnbk.repo.api.tripMemo;

import kz.nicnbk.repo.model.tripmemo.TripMemoFiles;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by zhambyl on 04-Aug-16.
 */
public interface TripMemoFilesRepository extends PagingAndSortingRepository<TripMemoFiles, Long> {

    /**
     * return files entities by tripMemoId
     *
     * @param tripMemoId
     * @return
     */
    @Query("select m from trip_memo_files m where m.tripMemo.id=?1")
    List<TripMemoFiles> getFilesByTripMemoId(Long tripMemoId);

    /**
     * Return entities by fileId
     *
     * @param fileId
     * @return
     */
    @Query("select m from trip_memo_files m where m.file.id=?1")
    TripMemoFiles getFilesByFileId(Long fileId);
}
