package kz.nicnbk.service.dto.hf;

import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.common.service.model.BaseEntityDto;

/**
 * Created by magzumov on 14.12.2016.
 */
public class HedgeFundSubstrategyDto extends BaseEntityDto<kz.nicnbk.repo.model.hf.HedgeFundSubstrategy> {

    private HedgeFundDto fund;
    private BaseDictionaryDto substrategy;
    private Double value;

    public HedgeFundDto getFund() {
        return fund;
    }

    public void setFund(HedgeFundDto fund) {
        this.fund = fund;
    }

    public BaseDictionaryDto getSubstrategy() {
        return substrategy;
    }

    public void setSubstrategy(BaseDictionaryDto substrategy) {
        this.substrategy = substrategy;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
