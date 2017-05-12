package kz.nicnbk.repo.model.lookup;

/**
 * Created by magzumov on 18.07.2016.
 */
public enum FileTypeLookup {

    MEMO_ATTACHMENT("MEMO_ATT", "memo"),

    NB_REP_T1("NB_REP_T1", "reporting/nbrk"),
    NB_REP_T2("NB_REP_T2", "reporting/nbrk"),
    NB_REP_T3("NB_REP_T3", "reporting/nbrk"),
    NB_REP_T4("NB_REP_T4", "reporting/nbrk"),
    NB_REP_S1A("NB_REP_S1A", "reporting/nbrk"),
    NB_REP_S2A("NB_REP_S2A", "reporting/nbrk"),
    NB_REP_S3A("NB_REP_S3A", "reporting/nbrk"),
    NB_REP_S4A("NB_REP_S4A", "reporting/nbrk"),
    NB_REP_S1B("NB_REP_S1B", "reporting/nbrk"),
    NB_REP_S2B("NB_REP_S2B", "reporting/nbrk"),
    NB_REP_S3B("NB_REP_S3B", "reporting/nbrk"),
    NB_REP_S4B("NB_REP_S4B", "reporting/nbrk");

    private String code;
    private String catalog;


    FileTypeLookup(String code, String catalog) {
        this.code = code;
        this.catalog = catalog;
    }


    public String getCode() {
        return code;
    }

    public String getCatalog() {
        return catalog;
    }
}
