package kz.nicnbk.service.api.hr;

import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.hr.DocsResultDto;

import java.util.Set;

/**
 * Created by Pak on 28.08.2019.
 */

public interface DocsService {

    DocsResultDto get();

    DocsResultDto upload(Set<FilesDto> filesDtoSet, String username);

    DocsResultDto deleteDocument(Long fileId);
}
