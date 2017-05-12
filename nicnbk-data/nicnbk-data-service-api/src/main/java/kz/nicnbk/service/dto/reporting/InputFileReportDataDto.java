package kz.nicnbk.service.dto.reporting;

import kz.nicnbk.common.service.model.BaseDto;
import kz.nicnbk.service.dto.files.FilesDto;

/**
 * Created by magzumov on 10.05.2017.
 */
public class InputFileReportDataDto implements BaseDto {

    private FilesDto filesDto;
    private PeriodicReportDto reportDto;

    public FilesDto getFilesDto() {
        return filesDto;
    }

    public void setFilesDto(FilesDto filesDto) {
        this.filesDto = filesDto;
    }

    public PeriodicReportDto getReportDto() {
        return reportDto;
    }

    public void setReportDto(PeriodicReportDto reportDto) {
        this.reportDto = reportDto;
    }
}
