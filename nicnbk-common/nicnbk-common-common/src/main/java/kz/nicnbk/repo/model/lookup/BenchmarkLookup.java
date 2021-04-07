package kz.nicnbk.repo.model.lookup;

public enum BenchmarkLookup {

    // TODO: Bloomberg codes

    HFRIFOF("HFRI_FOF"),
    HFRIAWC("HFRI_AWC"),
    MSCI_WORLD("MSCI_WRLD"),
    MSCI_ACWI_IMI("MSCIACWIIM"),
    MSCI_EM("MSCI_EM"),
    US_HIGH_YIELDS("US_HIGHYLD"),
    SNP_500_SPX("SP500_SPX"),
    GLOBAL_FI("GLOBAL_FI"),

    SNP_500_SPTR("SP500_SPTR"),
    T_BILLS("T_BILLS"),
    US_IG_CREDIT("US_IG_CRED"),
    OIL("OIL"),
    DOLLAR("USD"),
    GOLD("GOLD"),
    GLOBAL_FI_U("GLOBAL_FI_U"),
    BARCLAYS_GLOBAL_AGG("BRCL_G_AGG"),
    HFRI("HFRI"),
    EM_DEBT("EM_DEBT");

    private String code;

    // TODO: Bloomberg codes
    private String codeBloomberg;

    BenchmarkLookup(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
