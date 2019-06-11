package kz.nicnbk.service.dto.authentication;

/**
 * Created by magzumov on 21.02.2017.
 */
public enum UserRoles {
    ADMIN("ADMIN"),
    HEDGE_FUNDS_EDIT("HF_EDIT"),
    HEDGE_FUNDS_VIEW("HF_VIEW"),
    PRIVATE_EQUITY_EDIT("PE_EDIT"),
    PRIVATE_EQUITY_VIEW("PE_VIEW"),
    REAL_ESTATE_EDIT("RE_EDIT"),
    REAL_ESTATE_VIEW("RE_VIEW"),
    REPORTING_EDIT("RA_EDIT"),
    REPORTING_VIEW("RA_VIEW"),
    STRATEGY_RISK_EDIT("SRM_EDIT"),
    STRATEGY_RISK_VIEW("SRM_VIEW"),
    MACRO_MONITOR_EDIT("MM_EDIT"),

    CORP_MEETING_EDIT("CM_EDIT"),
//    CORP_MEETING_VIEW("CM_VIEW"),

    IC_MEMBER("IC_MEMBR"),

    NEWS_EDIT("NEWS_EDIT");

    UserRoles(String code){
        this.code = code;
    }

    private String code;

    public String getCode() {
        return code;
    }
}
