package kz.nicnbk.repo.model.lookup.monitoring;

/**
 * Created by magzumov on 18.07.2016.
 */
public enum MonitoringHedgeFundFundInfoTypeLookup {

    POSITIVE_CONTRIBUTORS(1, "POS_CONTRB"),
    NEGATIVE_CONTRIBUTORS(2, "NEG_CONTRB"),
    FUND_ALLOCATIONS(3, "FUND_ALLOC");

    private String code;
    private Integer id;

    MonitoringHedgeFundFundInfoTypeLookup(Integer id, String code) {
        this.id = id;
        this.code = code;
    }

    public Integer getId(){
        return id;
    }

    public String getCode() {
        return code;
    }

}
