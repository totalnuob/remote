package kz.nicnbk.service.dto.reporting;

/**
 * Created by magzumov on 25.10.2017.
 */
public class ConsolidatedKZTForm22RecordDto extends ConsolidatedBalanceFormRecordDto {
    private Double turnover;

    public ConsolidatedKZTForm22RecordDto(){}

    public ConsolidatedKZTForm22RecordDto(String name, Integer lineNumber){
        super(name, lineNumber);
    }

    public Double getTurnover() {
        return turnover;
    }

    public void setTurnover(Double turnover) {
        this.turnover = turnover;
    }
}
