package kz.nicnbk.repo.model.lookup;

/**
 * Created by magzumov on 18.07.2016.
 */
public enum NewsTypeLookup {

    GENERAL("GENERAL", 1),
    PRIVATE_EQUITY("PE", 2),
    HEDGE_FUNDS("HF", 3),
    SOVEREIGN_WEALTH_FUNDS("SWF", 4),
    REAL_ESTATE("RE", 5),
    RISK_MANAGEMENT("RM", 6),
    NBRK("NBRK", 7);

    private String code;
    private int id;

    NewsTypeLookup(String code, int id) {
        this.code = code;
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
