package kz.nicnbk.service.dto.reporting.privateequity;

import kz.nicnbk.service.dto.reporting.GeneratedGeneralLedgerFormDto;

/**
 * Created by magzumov on 15.06.2018.
 */
public class TarragonGeneratedGeneralLedgerFormDto extends GeneratedGeneralLedgerFormDto {

    private Boolean excludeFromTarragonCalculation;
    private String type;


    public TarragonGeneratedGeneralLedgerFormDto(){}

    public TarragonGeneratedGeneralLedgerFormDto(TarragonGeneratedGeneralLedgerFormDto other){
        super(other);

        this.excludeFromTarragonCalculation = other.getExcludeFromTarragonCalculation();
        this.type = other.getType();
    }

    public Boolean getExcludeFromTarragonCalculation() {
        return excludeFromTarragonCalculation;
    }

    public void setExcludeFromTarragonCalculation(Boolean excludeFromTarragonCalculation) {
        this.excludeFromTarragonCalculation = excludeFromTarragonCalculation;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
