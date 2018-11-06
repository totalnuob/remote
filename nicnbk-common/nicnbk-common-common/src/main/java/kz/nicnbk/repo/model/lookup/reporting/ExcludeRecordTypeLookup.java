package kz.nicnbk.repo.model.lookup.reporting;

/**
 * Created by magzumov on 18.07.2016.
 */
public enum ExcludeRecordTypeLookup {

    TERRA("TERRA"),
    TARRAGON("TARRAGON"),
    SINGULARITY("SINGULARITY");

    private String code;

    ExcludeRecordTypeLookup(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
