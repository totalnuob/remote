package kz.nicnbk.repo.api.hf;

import kz.nicnbk.repo.model.hf.HFResearchPageFiles;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by zhambyl on 13/11/2018.
 */
public interface HFResearchPageFilesReposiotry extends PagingAndSortingRepository<HFResearchPageFiles, Long> {

    @Query("SELECT m from hf_research_page_files m where m.researchPage.id=?1 and (m.file.deleted is null or m.file.deleted=false)")
    List<HFResearchPageFiles> getFilesByMemoId(Long memoId);

    @Query("SELECT m from hf_research_page_files m where m.file.id=?1 and (m.file.deleted is null or m.file.deleted=false)")
    HFResearchPageFiles getFilesByFileId(Long fileId);

}
