package kz.nicnbk.service.dto.reporting.realestate;

import kz.nicnbk.service.dto.reporting.GeneratedGeneralLedgerFormDto;

/**
 * Created by magzumov on 15.06.2018.
 */
public class TerraGeneratedGeneralLedgerFormDto extends GeneratedGeneralLedgerFormDto {

    private Boolean excludeFromTerraCalculation;
    private String type;


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
