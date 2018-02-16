package kz.nicnbk.service.impl.reporting.lookup;

/**
 * Created by magzumov on 18.07.2016.
 */
public enum PeriodicDataTypeLookup {

    NET_PROFIT("NET_PROFIT", "Чистая прибыль"),
    RESERVE_REVALUATION("RSRV_REVAL", "Резерв по переоценке");

    private String code;
    private String name;


    PeriodicDataTypeLookup(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
