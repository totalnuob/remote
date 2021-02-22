package kz.nicnbk.repo.model.lookup.reporting;

/**
 * Created by magzumov on 18.07.2016.
 */
public enum NICChartAccountsTypeLookup {

    NOMATCH("NOMATCH", "NO MATCH (EXCLUDE)");


    private String code;
    private String name;

    NICChartAccountsTypeLookup(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName(){
        return name;
    }
}
