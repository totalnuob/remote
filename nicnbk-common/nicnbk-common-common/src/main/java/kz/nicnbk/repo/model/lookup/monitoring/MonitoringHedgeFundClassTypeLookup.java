package kz.nicnbk.repo.model.lookup.monitoring;

/**
 * Created by magzumov on 18.07.2016.
 */
public enum MonitoringHedgeFundClassTypeLookup {

    OVERALL(1, "OVERALL"),
    CLASS_A(2, "CLASS_A"),
    CLASS_B(3, "CLASS_B");

    private String code;
    private Integer id;

    MonitoringHedgeFundClassTypeLookup(Integer id, String code) {
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
