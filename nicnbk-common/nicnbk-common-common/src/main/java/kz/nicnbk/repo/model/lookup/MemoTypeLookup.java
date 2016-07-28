package kz.nicnbk.repo.model.lookup;

/**
 * Created by magzumov on 19.07.2016.
 */

@Deprecated
public enum MemoTypeLookup {

    PRIVATE_EQUITY("PE"),
    HEDGE_FUNDS("HF");

    private String code;

    MemoTypeLookup(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
