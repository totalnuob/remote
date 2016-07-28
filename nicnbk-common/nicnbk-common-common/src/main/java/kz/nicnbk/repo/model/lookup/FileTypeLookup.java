package kz.nicnbk.repo.model.lookup;

/**
 * Created by magzumov on 18.07.2016.
 */
public enum FileTypeLookup {

    MEMO_ATTACHMENT("MEMO_ATT", "memo");

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
