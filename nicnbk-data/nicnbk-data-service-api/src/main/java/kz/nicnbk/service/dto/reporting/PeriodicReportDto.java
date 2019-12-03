package kz.nicnbk.service.dto.reporting;

import kz.nicnbk.common.service.model.CreateUpdateBaseEntityDto;
import kz.nicnbk.repo.model.reporting.PeriodicReport;

import java.util.Date;

/**
 * Created by magzumov on 20.04.2017.
 */
public class PeriodicReportDto extends CreateUpdateBaseEntityDto<PeriodicReport> {

    private Long id;
    private Date reportDate;
    private String type;
    private String status;

    public PeriodicReportDto(){}

    public PeriodicReportDto(Long id){
        this.id = id;
    }

    public PeriodicReportDto(Long id, Date reportDate, String type, String status){
        this.id = id;
        this.reportDate = reportDate;
        this.type = type;
        this.status = status;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Date getReportDate() {
        return reportDate;
    }

    public void setReportDate(Date reportDate) {
        this.reportDate = reportDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
