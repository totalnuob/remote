package kz.nicnbk.repo.model.hf;


/**
 * Created by timur on 19.10.2016.
 */
public class HedgeFundScreeningFundCounts {

    private Long fundId;
    private Long count;

    public HedgeFundScreeningFundCounts(){}

    public HedgeFundScreeningFundCounts(Long fundId, Long count){
        this.fundId = fundId;
        this.count = count;
    }

    public Long getFundId() {
        return fundId;
    }

    public void setFundId(Long fundId) {
        this.fundId = fundId;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}


