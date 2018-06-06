package kz.nicnbk.repo.api.reporting;

import kz.nicnbk.repo.model.m2s2.MemoFiles;
import kz.nicnbk.repo.model.reporting.ReserveCalculationFiles;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by magzumov on 05.07.2016.
 */
public interface ReserveCalculationFilesRepository extends PagingAndSortingRepository<ReserveCalculationFiles, Long> {

    @Query("SELECT m from reserve_calc_files m where m.entity.id=?1 and (m.file.deleted is null or m.file.deleted=false)")
    List<ReserveCalculationFiles> getFilesByEntityId(Long entityId);

    @Query("SELECT m from reserve_calc_files m where m.file.id=?1 and (m.file.deleted is null or m.file.deleted=false)")
    ReserveCalculationFiles getFilesByFileId(Long fileId);
}
