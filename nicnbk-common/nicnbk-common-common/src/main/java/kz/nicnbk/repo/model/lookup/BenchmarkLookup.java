package kz.nicnbk.repo.model.lookup;

public enum BenchmarkLookup {

    //HFRI("HFRI"),
    HFRIFOF("HFRI_FOF", "HFRIFOF"),
    HFRIAWC("HFRI_AWC", "HFRIAWC"),
    SNP_500_SPTR("SP500_SPTR", "SPTR"),
    SNP_500_SPX("SP500_SPX", "SPX"),
    T_BILLS("T_BILLS", "G0O1"),
    MSCI_WORLD("MSCI_WRLD", "MXWO"),
    MSCI_ACWI_IMI("MSCIACWIMI", "MXWDIM"),
    MSCI_EM("MSCI_EM", "MXEF"),
    US_HIGH_YIELDS("US_HIGHYLD", "H0A0"),
    US_IG_CREDIT("US_IG_CRED", "C0A0"),
    EM_DEBT("EM_DEBT", "GBIEMCOR"),
    //GLOBAL_FI("GLOBAL_FI"),
    LEGATRUH("LEGATRUH","LEGATRUH"),
    //GLOBAL_FI_U("GLOBAL_FI_U"),
    LEGATRUU("LEGATRUU", "LEGATRUU"),
    //BARCLAYS_GLOBAL_AGG("BRCL_G_AGG"),
    OIL("OIL", null),
    DOLLAR("USD", null),
    GOLD("GOLD", null);

    private String code;
    private String codeBloomberg;

    BenchmarkLookup(String code, String codeBloomberg) {
        this.code = code; this.codeBloomberg = codeBloomberg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCodeBloomberg() {
        return codeBloomberg;
    }

    public void setCodeBloomberg(String codeBloomberg) {
        this.codeBloomberg = codeBloomberg;
    }
}
