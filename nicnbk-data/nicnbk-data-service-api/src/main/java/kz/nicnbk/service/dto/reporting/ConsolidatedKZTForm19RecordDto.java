package kz.nicnbk.service.dto.reporting;

/**
 * Created by magzumov on 25.10.2017.
 */
public class ConsolidatedKZTForm19RecordDto extends ConsolidatedBalanceFormRecordDto {
    private Double turnover;

    public ConsolidatedKZTForm19RecordDto(){}

    public ConsolidatedKZTForm19RecordDto(String name, Integer lineNumber){
        super(name, lineNumber);
    }

    public Double getTurnover() {
        return turnover;
    }

    public void setTurnover(Double turnover) {
        this.turnover = turnover;
    }
}
