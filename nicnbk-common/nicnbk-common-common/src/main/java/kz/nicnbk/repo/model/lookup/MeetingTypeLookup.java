package kz.nicnbk.repo.model.lookup;

/**
 * Created by magzumov on 19.07.2016.
 */
public enum MeetingTypeLookup {

    MEETING("MEETING", 1),
    CALL("CALL", 2);


    private String code;
    private int id;

    MeetingTypeLookup(String code, int id) {
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
