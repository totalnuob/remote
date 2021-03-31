package kz.nicnbk.repo.model.lookup.reporting;

public enum MonitoringRiskHFPortfolioTypeLookup {

    SINGULAR_A(1,"SING_A"),
    SINGULAR_B(2,"SING_B"),
    SINGULAR_CONS(3, "SING_CONS");

    private Integer id;
    private String code;

    MonitoringRiskHFPortfolioTypeLookup(Integer id, String code) {
        this.id = id;
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public static MonitoringRiskHFPortfolioTypeLookup getLookupByCode(String code){
        if(code.equalsIgnoreCase(SINGULAR_A.code)){
            return SINGULAR_A;
        }else if(code.equalsIgnoreCase(SINGULAR_B.code)){
            return SINGULAR_B;
        }else if(code.equalsIgnoreCase(SINGULAR_CONS.code)){
            return SINGULAR_CONS;
        }
        return null;
    }
}
