package kz.nicnbk.service.impl.reporting.lookup;

/**
 * Created by magzumov on 18.07.2016.
 */
public enum ReserveCalculationsExpenseTypeLookup {

    ADD("ADD"),
    RETURN("RETURN"),
    ADMINISTRATION_FEES("ADM_FEE"),
    NICK_MF_EXPENSES("NMF_EXP");

    private String code;


    ReserveCalculationsExpenseTypeLookup(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
