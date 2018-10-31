package kz.nicnbk.repo.model.lookup.corpmeetings;

/**
 * Created by magzumov on 18.07.2016.
 */
public enum ICMeetingTopicTypeLookup {

    PRIVATE_EQUITY("PE"),
    HEDGE_FUNDS("HF"),
    REAL_ESTATE("RE"),
    STRATEGY_RISKS("SRM"),
    REPORTING("REP");


    private String code;

    ICMeetingTopicTypeLookup(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
