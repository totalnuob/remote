package kz.nicnbk.repo.api.m2s2;

import kz.nicnbk.repo.model.m2s2.MemoFiles;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by magzumov on 05.07.2016.
 */
public interface MemoFilesRepository extends PagingAndSortingRepository<MemoFiles, Long> {

    @Query("SELECT m from memo_files m where m.memo.id=?1 and (m.file.deleted is null or m.file.deleted=false)")
    List<MemoFiles> getFilesByMemoId(Long memoId);

    @Query("SELECT m from memo_files m where m.file.id=?1 and (m.file.deleted is null or m.file.deleted=false)")
    MemoFiles getFilesByFileId(Long fileId);
}
