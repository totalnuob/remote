package kz.nicnbk.service.api.reporting;

import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.common.FileUploadResultDto;
import kz.nicnbk.service.dto.files.FilesDto;

/**
 * Created by magzumov on 18.01.2018.
 */
public interface PeriodicReportFileParseService extends BaseService {

    FileUploadResultDto parseFile(String fileType, FilesDto filesDto, Long reportId);
}
