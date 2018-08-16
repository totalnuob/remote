package kz.nicnbk.service.dto.reporting;

import kz.nicnbk.common.service.model.BaseDto;
import kz.nicnbk.common.service.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by magzumov on 25.10.2017.
 */
public class ReportingFundRenameInfoDto implements BaseDto {
    private PeriodicReportDto report;
    private List<ReportingFundRenamePairDto> fundRenames;

    public PeriodicReportDto getReport() {
        return report;
    }

    public void setReport(PeriodicReportDto report) {
        this.report = report;
    }

    public List<ReportingFundRenamePairDto> getFundRenames() {
        return fundRenames;
    }

    public void setFundRenames(List<ReportingFundRenamePairDto> fundRenames) {
        this.fundRenames = fundRenames;
    }

    public void addFundRenamePair(String current, String previous, String type){
        if(fundRenames == null){
            this.fundRenames = new ArrayList<>();
        }
        this.fundRenames.add(new ReportingFundRenamePairDto(current, previous, type));
    }

    public boolean isEmpty(){
        return this.fundRenames == null || this.fundRenames.isEmpty();
    }
}
