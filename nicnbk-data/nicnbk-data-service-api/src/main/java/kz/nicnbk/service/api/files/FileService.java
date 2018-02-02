package kz.nicnbk.service.api.files;

import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.files.FilesDto;

import java.io.InputStream;

/**
 * Created by magzumov on 18.07.2016.
 */
public interface FileService extends BaseService {

    Long save (FilesDto metaData, String fileType);

    FilesDto getFileInfo(Long fileId);

    boolean delete(Long id);

    boolean safeDelete(Long id);

    InputStream getFileInputStream(Long fileId, String fileType);

    String getCatalogByFileCode(String code);
}
