package kz.nicnbk.service.impl.reporting.lookup;

/**
 * Created by magzumov on 18.07.2016.
 */
public enum NICChartAccountsLookup {

    ORG_COST_NICKMF(2L, "2923.010.a", "Организационные расходы NICK MF"),
    AMORTIZATION_ORG_COSTS_NICKMF(3L, "2923.010.b", "Начисленная амортизация - Организационные расходы NICK MF"),
    ORG_COSTS_SINGULARITY(15L, "2923.010.c", "Организационные расходы Singularity"),
    AMORTIZATION_ORG_COSTS_SINGULARITY(16L, "2923.010.d", "Начисленная амортизация - Организационные расходы Singularity");

    private String code;
    private String nameRu;
    private Long id;


    NICChartAccountsLookup(Long id, String code, String nameRu) {
        this.id = id;
        this.code = code;
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
}
