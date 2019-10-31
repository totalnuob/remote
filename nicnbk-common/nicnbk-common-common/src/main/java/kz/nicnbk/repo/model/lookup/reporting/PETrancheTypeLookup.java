package kz.nicnbk.repo.model.lookup.reporting;

public enum PETrancheTypeLookup {

    TARRAGON_A("TARR_A", "Tarragon A"),
    TARRAGON_B("TARR_B", "Tarragon B"),
    TARRAGON_A2("TARR_A2", "Tarragon A-2"),
    TARRAGON_B2("TARR_B2", "Tarragon B-2");


    private String code;
    private String nameEn;

    PETrancheTypeLookup(String code, String nameEn) {
        this.code = code;
        this.nameEn = nameEn;
    }


    public String getCode() {
        return code;
    }

    public String getNameEn() {
        return nameEn;
    }
}
