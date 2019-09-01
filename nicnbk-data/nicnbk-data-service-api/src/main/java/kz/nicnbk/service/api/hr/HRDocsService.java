package kz.nicnbk.service.api.hr;

import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.hr.HRDocsResultDto;

import java.util.Set;

/**
 * Created by Pak on 28.08.2019.
 */

public interface HRDocsService {

    HRDocsResultDto get();

    HRDocsResultDto upload(Set<FilesDto> filesDtoSet, String username);

    HRDocsResultDto deleteDocument(Long fileId);
}
