package kz.nicnbk.repo.model.lookup;

/**
 * Created by magzumov on 18.07.2016.
 */
public enum BenchmarkLookup {

    S_AND_P("S_AND_P"),
    T_BILLS("T_BILLS");

    private String code;

    BenchmarkLookup(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
