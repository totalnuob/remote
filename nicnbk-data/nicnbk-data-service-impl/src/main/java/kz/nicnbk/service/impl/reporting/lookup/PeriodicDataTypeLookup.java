package kz.nicnbk.service.impl.reporting.lookup;

/**
 * Created by magzumov on 18.07.2016.
 */
public enum PeriodicDataTypeLookup {

    NET_PROFIT("NET_PROFIT"),
    RESERVE_REVALUATION("RSRV_REVAL");

    private String code;


    PeriodicDataTypeLookup(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
