package kz.nicnbk.repo.model.lookup;

public enum BloombergStationLookup {

    YAO("http://10.10.165.123:8080", 1),
    RISK("http://10.10.165.?:8080", 2),
    DAI_1("http://10.10.165.?:8080", 3),
    DAI_2("http://10.10.165.?:8080", 4);

    private String code;
    private int id;

    BloombergStationLookup(String code, int id) {
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
