package kz.nicnbk.service.dto.reporting;

import kz.nicnbk.common.service.model.BaseDto;

import java.util.List;

/**
 * Created by magzumov on 25.10.2017.
 */
public class ReportingFundRenamePairDto implements BaseDto {

    public static final String TYPE_PE = "PE";
    public static final String TYPE_HF = "HF";
    public static final String TYPE_RE = "RE";

    private String currentFundName;
    private String previousFundName;
    private Boolean usePreviousFundName;
    private String type;

    public ReportingFundRenamePairDto(){}

    public ReportingFundRenamePairDto(String currentFundName, String previousFundName, String type, Boolean usePreviousFundName){
        this.currentFundName = currentFundName;
        this.previousFundName = previousFundName;
        this.type = type;
        this.usePreviousFundName = usePreviousFundName;
    }

    public String getCurrentFundName() {
        return currentFundName;
    }

    public void setCurrentFundName(String currentFundName) {
        this.currentFundName = currentFundName;
    }

    public String getPreviousFundName() {
        return previousFundName;
    }

    public void setPreviousFundName(String previousFundName) {
        this.previousFundName = previousFundName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isPrivateEquityFund(){
        return this.type != null && this.type.equalsIgnoreCase("PE");
    }

    public boolean isHedgeFund(){
        return this.type != null && this.type.equalsIgnoreCase("HF");
    }

    public boolean isRealEstateFund(){
        return this.type != null && this.type.equalsIgnoreCase("RE");
    }

    public Boolean getUsePreviousFundName() {
        return usePreviousFundName;
    }

    public void setUsePreviousFundName(Boolean usePreviousFundName) {
        this.usePreviousFundName = usePreviousFundName;
    }

    public boolean isUsePreviousFundName(){
        return this.usePreviousFundName != null && this.usePreviousFundName.booleanValue();
    }
}
