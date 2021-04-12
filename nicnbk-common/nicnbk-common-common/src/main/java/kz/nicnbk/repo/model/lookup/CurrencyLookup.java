package kz.nicnbk.repo.model.lookup;

/**
 * Created by magzumov on 18.07.2016.
 */
public enum CurrencyLookup {

    USD("USD", 1),
    EUR("EUR", 2),
    JPY("JPY", 4),
    SGD("SGD", 5),
    CNY("CNY", 6),
    CHF("CHF", 7),
    CAD("CAD", 9),
    GBP("GBP", 3),
    KRW("KRW", 8),
    AUD("AUD", 10),
    SEK("SEK", 11),
    NOK("NOK", 12),
    ZAR("ZAR", 13),
    DKK("DKK", 14);

    private String code;
    private int id;

    CurrencyLookup(String code, int id) {
        this.code = code;
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
