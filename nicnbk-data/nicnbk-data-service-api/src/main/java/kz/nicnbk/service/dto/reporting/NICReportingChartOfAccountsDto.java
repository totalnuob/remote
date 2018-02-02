package kz.nicnbk.service.dto.reporting;

import kz.nicnbk.common.service.model.BaseDictionaryDto;

/**
 * Created by magzumov on 28.09.2017.
 */
public class NICReportingChartOfAccountsDto extends BaseDictionaryDto implements Comparable{

    private BaseDictionaryDto NBChartOfAccounts;

    public NICReportingChartOfAccountsDto(){}

    public NICReportingChartOfAccountsDto(BaseDictionaryDto parent){
        super.setCode(parent.getCode());
        super.setNameEn(parent.getNameEn());
        super.setNameRu(parent.getNameRu());
        super.setNameKz(parent.getNameKz());
    }

    public BaseDictionaryDto getNBChartOfAccounts() {
        return NBChartOfAccounts;
    }

    public void setNBChartOfAccounts(BaseDictionaryDto NBChartOfAccounts) {
        this.NBChartOfAccounts = NBChartOfAccounts;
    }

    @Override
    public int compareTo(Object o) {
        NICReportingChartOfAccountsDto other = (NICReportingChartOfAccountsDto) o;
        if(this.getCode() != null && other.getCode() != null){
            return this.getCode().compareTo(other.getCode());
        }else{
            return 0;
        }
    }
}
