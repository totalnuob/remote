package kz.nicnbk.service.api.hf;

import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.hf.HFResearchPageDto;
import kz.nicnbk.service.dto.m2s2.MemoDeleteResultDto;

import java.util.List;
import java.util.Set;

/**
 * Created by zhambyl on 12/11/2018.
 */
public interface HFResearchPageService extends BaseService {

    Long save(HFResearchPageDto researchPageDto, String updater);

    HFResearchPageDto get(Long id);

    List<HFResearchPageDto> loadResearch(Long managerId);

    boolean safeDeleteAttachment(Long researchPageId, Long fileId, String username);

    Set<FilesDto> getAttachments(Long researchPageId);

    Set<FilesDto> saveAttachments(Long researchPageId, Set<FilesDto> attachments);

    MemoDeleteResultDto delete(Long pageId, String username);
}
