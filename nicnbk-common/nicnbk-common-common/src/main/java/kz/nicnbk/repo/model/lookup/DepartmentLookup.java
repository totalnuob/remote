package kz.nicnbk.repo.model.lookup;

public enum DepartmentLookup {

    DAI_1(1),
    DAI_2(2),
    DEV(3),
    LEGAL(4),
    TREASURY(5),
    STRTGY(6),
    REP(7),
    RISK(8),
    OPERS(9),
    ACCOUNT(10),
    HR(11);

    private int code;

    DepartmentLookup(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
