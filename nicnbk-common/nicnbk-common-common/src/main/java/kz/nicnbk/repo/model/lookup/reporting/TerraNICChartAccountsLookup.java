package kz.nicnbk.repo.model.lookup.reporting;

/**
 * Created by magzumov on 18.07.2016.
 */
public enum TerraNICChartAccountsLookup {

    SECURITY("Security"),
    UNREALIZED_GAIN("Unrealised Gain");


    private String nameEn;

    TerraNICChartAccountsLookup(String nameEn) {
        this.nameEn = nameEn;
    }

    public String getNameEn() {
        return nameEn;
    }
}
