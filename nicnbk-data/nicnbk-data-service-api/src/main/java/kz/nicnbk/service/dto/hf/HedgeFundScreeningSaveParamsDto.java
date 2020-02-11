package kz.nicnbk.service.dto.hf;
import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.common.service.model.BaseDto;
import kz.nicnbk.common.service.model.CreateUpdateBaseEntityDto;

import java.util.Date;


public class HedgeFundScreeningSaveParamsDto implements BaseDto {

    private Long filteredResultId;
    private int lookbackReturn;
    private int lookbackAUM;

    public Long getFilteredResultId() {
        return filteredResultId;
    }

    public void setFilteredResultId(Long filteredResultId) {
        this.filteredResultId = filteredResultId;
    }

    public int getLookbackReturn() {
        return lookbackReturn;
    }

    public void setLookbackReturn(int lookbackReturn) {
        this.lookbackReturn = lookbackReturn;
    }

    public int getLookbackAUM() {
        return lookbackAUM;
    }

    public void setLookbackAUM(int lookbackAUM) {
        this.lookbackAUM = lookbackAUM;
    }
}
