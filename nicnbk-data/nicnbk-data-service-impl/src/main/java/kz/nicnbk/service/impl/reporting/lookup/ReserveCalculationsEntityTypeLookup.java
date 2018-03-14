package kz.nicnbk.service.impl.reporting.lookup;

/**
 * Created by magzumov on 18.07.2016.
 */
public enum ReserveCalculationsEntityTypeLookup {

    NIC("NIC"),
    NBRK("NBRK"),
    NICKMF("NICKMF"),
    TARRAGON_A("TARR_A"),
    TARRAGON_B("TARR_B"),
    SINGULARITY("SING"),
    SINGULARITY_A("SING_A"),
    SINGULARITY_B("SING_B");

    private String code;


    ReserveCalculationsEntityTypeLookup(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
