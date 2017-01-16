package kz.nicnbk.repo.model.lookup;

/**
 * Created by magzumov on 18.07.2016.
 */
public enum MeetingArrangedByLookup {

    NIC("NIC", 1),
    GP("GP", 2),
    OTHER("OTHER", 3);

    private String code;
    private int id;

    MeetingArrangedByLookup(String code, int id) {
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
