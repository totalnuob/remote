package kz.nicnbk.repo.model.lookup;

/**
 * Created by magzumov on 18.07.2016.
 */
public enum FileTypeLookup {

    MEMO_ATTACHMENT("MEMO_ATT", "memo"),

    NB_REP_TARR_SCHED_INVEST("NB_REP_T1", "reporting/nbrk"),
    NB_REP_TARR_STMT_BALANCE_OPERATIONS("NB_REP_T2", "reporting/nbrk"),
    NB_REP_TARR_STMT_CASHFLOW("NB_REP_T3", "reporting/nbrk"),
    NB_REP_TARR_STMT_CHANGES("NB_REP_T4", "reporting/nbrk"),
    NB_REP_S1A("NB_REP_S1A", "reporting/nbrk"),
    NB_REP_S2A("NB_REP_S2A", "reporting/nbrk"),
    NB_REP_S3A("NB_REP_S3A", "reporting/nbrk"),
    NB_REP_S4A("NB_REP_S4A", "reporting/nbrk"),
    NB_REP_S1B("NB_REP_S1B", "reporting/nbrk"),
    NB_REP_S2B("NB_REP_S2B", "reporting/nbrk"),
    NB_REP_S3B("NB_REP_S3B", "reporting/nbrk"),
    NB_REP_S4B("NB_REP_S4B", "reporting/nbrk"),
    NB_REP_SINGULAR_GENERAL_LEDGER("NB_REP_SGL", "reporting/nbrk"),
    NB_REP_SN_TRANCHE_A("NB_REP_SNA", "reporting/nbrk"),
    NB_REP_SN_TRANCHE_B("NB_REP_SNB", "reporting/nbrk"),
    NB_REP_MONTHLY_CASH_STATEMENT("NB_REP_MCS", "reporting/nbrk");
    MEMO_ATTACHMENT("MEMO_ATT", "memo"),

    PE_FIRM_LOGO("PE_LOGO", "pe_logo");

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
