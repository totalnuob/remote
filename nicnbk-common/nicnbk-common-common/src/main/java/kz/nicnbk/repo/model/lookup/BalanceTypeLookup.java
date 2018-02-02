package kz.nicnbk.repo.model.lookup;

/**
 * Created by magzumov on 18.07.2016.
 */
public enum BalanceTypeLookup {

    ASSETS("ASSETS"),
    LIABILITIES("LIABLTY"),
    PARTNERS_CAPITAL("PRTN_CAP"),
    LIABILITIES_AND_PARTNERS_CAPITAL("L_AND_PC");


    private String code;

    BalanceTypeLookup(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
