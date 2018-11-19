package kz.nicnbk.service.dto.hf;

import kz.nicnbk.common.service.model.BaseEntityDto;
import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.repo.model.base.BaseEntity;


/**
 * Created by timur on 19.10.2016.
 */
public class HedgeFundScreeningParsedDataDto extends BaseEntityDto {

    private HedgeFundScreeningDto screening;

    private Long fundId;
    private String fundName;
    private String investmentManager;
    private String mainStrategy;

    private Double fundAUM;
    private Double managerAUM;

    public HedgeFundScreeningDto getScreening() {
        return screening;
    }

    public void setScreening(HedgeFundScreeningDto screening) {
        this.screening = screening;
    }

    public Long getFundId() {
        return fundId;
    }

    public void setFundId(Long fundId) {
        this.fundId = fundId;
    }

    public String getFundName() {
        return fundName;
    }

    public void setFundName(String fundName) {
        this.fundName = fundName;
    }

    public String getInvestmentManager() {
        return investmentManager;
    }

    public void setInvestmentManager(String investmentManager) {
        this.investmentManager = investmentManager;
    }

    public String getMainStrategy() {
        return mainStrategy;
    }

    public void setMainStrategy(String mainStrategy) {
        this.mainStrategy = mainStrategy;
    }

    public Double getManagerAUM() {
        return managerAUM;
    }

    public void setManagerAUM(Double managerAUM) {
        this.managerAUM = managerAUM;
    }

    public Double getFundAUM() {
        return fundAUM;
    }

    public void setFundAUM(Double fundAUM) {
        this.fundAUM = fundAUM;
    }

    public boolean isEmpty(){
        return this.fundId == null && StringUtils.isEmpty(this.fundName) &&
                StringUtils.isEmpty(this.investmentManager) && StringUtils.isEmpty(this.mainStrategy) &&
                this.fundAUM == null && this.managerAUM == null;
    }
}


