package kz.nicnbk.service.dto.reporting;

import kz.nicnbk.common.service.model.BaseDto;
import kz.nicnbk.service.dto.files.FilesDto;

import java.util.List;

/**
 * Created by magzumov on 07.11.2017.
 */
public class PeriodicReportInputFilesHolder implements BaseDto {
    private List<FilesDto> files;
    private PeriodicReportDto report;

    public List<FilesDto> getFiles() {
        return files;
    }

    public void setFiles(List<FilesDto> files) {
        this.files = files;
    }

    public PeriodicReportDto getReport() {
        return report;
    }

    public void setReport(PeriodicReportDto report) {
        this.report = report;
    }
}
