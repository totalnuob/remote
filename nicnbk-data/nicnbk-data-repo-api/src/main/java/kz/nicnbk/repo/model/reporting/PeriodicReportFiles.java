package kz.nicnbk.repo.model.reporting;

import kz.nicnbk.repo.model.base.CreatorBaseEntity;
import kz.nicnbk.repo.model.files.Files;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Created by magzumov on 21.04.2017.
 */

@Entity(name="periodic_report_files")
public class PeriodicReportFiles extends CreatorBaseEntity {

    private PeriodicReport periodicReport;
    private Files file;

    public PeriodicReportFiles(){}

    public PeriodicReportFiles(Long reportId, Long fileId){
        PeriodicReport periodicReport = new PeriodicReport();
        periodicReport.setId(reportId);
        Files files = new Files();
        files.setId(fileId);
        this.periodicReport = periodicReport;
        this.file = files;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="report_id", nullable = false)
    public PeriodicReport getPeriodicReport() {
        return periodicReport;
    }

    public void setPeriodicReport(PeriodicReport periodicReport) {
        this.periodicReport = periodicReport;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="file_id", nullable = false)
    public Files getFile() {
        return file;
    }

    public void setFile(Files file) {
        this.file = file;
    }
}
