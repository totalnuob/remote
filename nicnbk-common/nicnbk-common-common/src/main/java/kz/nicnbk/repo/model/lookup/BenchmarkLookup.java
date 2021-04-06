package kz.nicnbk.repo.model.lookup;

/**
 * Created by magzumov on 18.07.2016.
 */
public enum BenchmarkLookup {

    SNP_500_SPTR("SP500_SPTR"),
    SNP_500_SPX("SP500_SPX"),
    T_BILLS("T_BILLS"),
    US_HIGH_YIELDS("US_HIGHYLD"),
    MSCI_WORLD("MSCI_WRLD"),
    MSCI_ACWI_IMI("MSCIACWIMI"),
    US_IG_CREDIT("US_IG_CRED"),
    MSCI_EM("MSCI_EM"),
    OIL("OIL"),
    DOLLAR("USD"),
    GOLD("GOLD"),
    //GLOBAL_FI("GLOBAL_FI"),
    LEGATRUH("LEGATRUH"),
    //GLOBAL_FI_U("GLOBAL_FI_U"),
    LEGATRUU("LEGATRUU"),
    //BARCLAYS_GLOBAL_AGG("BRCL_G_AGG"),
    //HFRI("HFRI"),
    HFRIFOF("HFRI_FOF"),
    EM_DEBT("EM_DEBT"),
    HFRIAWC("HFRI_AWC");

    private String code;

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
