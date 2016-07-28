package kz.nicnbk.repo.model.lookup;

/**
 * Created by magzumov on 18.07.2016.
 */
public enum NewsTypeLookup {

    GENERAL("GENERAL"),
    HEDGE_FUNDS("HF"),
    PRIVATE_EQUITY("PE"),
    SOVEREIGN_WEALTH_FUNDS("SWF"),
    REAL_ESTATE("RE"),
    RISK_MANAGEMENT("RM");

    private String code;

    NewsTypeLookup(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
