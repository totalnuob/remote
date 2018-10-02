package kz.nicnbk.service.dto.reporting;

import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.repo.model.reporting.NICReportingChartOfAccounts;

/**
 * Created by magzumov on 28.09.2017.
 */
public class NICReportingChartOfAccountsDto extends BaseDictionaryDto implements Comparable{

    private BaseDictionaryDto NBChartOfAccounts;

    public NICReportingChartOfAccountsDto(){}

    public NICReportingChartOfAccountsDto(BaseDictionaryDto dto){
        super.setId(dto.getId());
        super.setCode(dto.getCode());
        super.setNameEn(dto.getNameEn());
        super.setNameRu(dto.getNameRu());
        super.setNameKz(dto.getNameKz());
    }

    public NICReportingChartOfAccountsDto(NICReportingChartOfAccounts entity){
        super.setCode(entity.getCode());
        super.setNameEn(entity.getNameEn());
        super.setNameRu(entity.getNameRu());
        super.setNameKz(entity.getNameKz());
        if(entity.getNbChartOfAccounts() != null){
            BaseDictionaryDto nbChartAccounts = new BaseDictionaryDto(entity.getNbChartOfAccounts().getCode(),
                    entity.getNbChartOfAccounts().getNameEn(), entity.getNbChartOfAccounts().getNameRu(), entity.getNbChartOfAccounts().getNameKz());
            setNBChartOfAccounts(nbChartAccounts);
        }
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
