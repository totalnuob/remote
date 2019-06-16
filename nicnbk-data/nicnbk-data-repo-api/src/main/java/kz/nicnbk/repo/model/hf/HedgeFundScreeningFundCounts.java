package kz.nicnbk.repo.model.hf;


/**
 * Created by timur on 19.10.2016.
 */
public class HedgeFundScreeningFundCounts {

    private Long fundId;
    private Integer count;

    public HedgeFundScreeningFundCounts(){}

    public HedgeFundScreeningFundCounts(Long fundId, Integer count){
        this.fundId = fundId;
        this.count = count;
    }

    public Long getFundId() {
        return fundId;
    }

    public void setFundId(Long fundId) {
        this.fundId = fundId;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}


