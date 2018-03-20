package kz.nicnbk.service.impl.reporting.lookup;

/**
 * Created by magzumov on 18.07.2016.
 */
public enum NICChartAccountsLookup {

    CURRENT_ACCOUNT_CASH(1L, "1033.010.a", "1033.010", "Деньги на текущих счетах"),
    SHAREHOLDER_EQUITY_NBRK(5L, "5021.010.a", "5021.010", "Уставный капитал, внесенный НБРК"),
    ORG_COST_NICKMF(2L, "2923.010.a", "2923.010", "Организационные расходы NICK MF"),
    AMORTIZATION_ORG_COSTS_NICKMF(3L, "2923.010.b", "2923.010", "Начисленная амортизация - Организационные расходы NICK MF"),
    ORG_COSTS_SINGULARITY(15L, "2923.010.c", "2923.010", "Организационные расходы Singularity"),
    AMORTIZATION_ORG_COSTS_SINGULARITY(16L, "2923.010.d", "2923.010", "Начисленная амортизация - Организационные расходы Singularity");

    private String code;
    private String codeNBRK;
    private String nameRu;
    private Long id;


    NICChartAccountsLookup(Long id, String code, String codeNBRK, String nameRu) {
        this.id = id;
        this.code = code;
        this.codeNBRK = codeNBRK;
        this.nameRu = nameRu;
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getNameRu() {
        return nameRu;
    }

    public String getCodeNBRK() {
        return codeNBRK;
    }
}
