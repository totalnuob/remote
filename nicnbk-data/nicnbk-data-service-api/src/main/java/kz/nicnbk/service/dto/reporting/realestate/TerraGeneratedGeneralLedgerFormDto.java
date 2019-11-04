package kz.nicnbk.service.dto.reporting.realestate;

import kz.nicnbk.service.dto.reporting.GeneratedGeneralLedgerFormDto;

/**
 * Created by magzumov on 15.06.2018.
 */
public class TerraGeneratedGeneralLedgerFormDto extends GeneratedGeneralLedgerFormDto {

    private Boolean excludeFromTerraCalculation;
    private String type;

    public TerraGeneratedGeneralLedgerFormDto(){}

    public TerraGeneratedGeneralLedgerFormDto(TerraGeneralLedgerBalanceRecordDto recordDto){
        setAcronym(recordDto.getAcronym());
        setBalanceDate(recordDto.getBalanceDate());
        setFinancialStatementCategory(recordDto.getFinancialStatementCategory());
        setChartAccountsLongDescription(recordDto.getGlSubclass());
        setShortName(recordDto.getPortfolioFund());
        setGLAccountBalance(recordDto.getAccountBalanceNICKMF());
    }



    public Boolean getExcludeFromTerraCalculation() {
        return excludeFromTerraCalculation;
    }

    public void setExcludeFromTerraCalculation(Boolean excludeFromTerraCalculation) {
        this.excludeFromTerraCalculation = excludeFromTerraCalculation;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
