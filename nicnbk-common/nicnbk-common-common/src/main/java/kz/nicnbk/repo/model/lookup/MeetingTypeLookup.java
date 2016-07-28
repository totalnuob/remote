package kz.nicnbk.repo.model.lookup;

/**
 * Created by magzumov on 19.07.2016.
 */
public enum MeetingTypeLookup {

    MEETING("MEETING"),
    CALL("CALL");


    private String code;

    MeetingTypeLookup(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
