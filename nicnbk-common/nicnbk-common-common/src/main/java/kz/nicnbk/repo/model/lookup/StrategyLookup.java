package kz.nicnbk.repo.model.lookup;

/**
 * Created by magzumov on 01.08.2016.
 */
public enum StrategyLookup {

    MEGA_CUP("MEGA_CUP"),
    MID_CUP("MID_CUP"),
    SM_CUP("SM_CUP"),
    MULTI("MULTI"),
    CREDIT("CREDIT"),
    OTHER("OTHER");

    private String code;

    StrategyLookup(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
