package kz.nicnbk.repo.model.lookup;

/**
 * Created by magzumov on 18.07.2016.
 */
public enum FileTypeLookup {

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
