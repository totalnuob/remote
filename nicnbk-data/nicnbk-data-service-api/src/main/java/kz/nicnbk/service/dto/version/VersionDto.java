package kz.nicnbk.service.dto.version;

import java.util.List;

/**
 * Created by Pak on 08.11.2019.
 */

public class VersionDto implements Comparable<VersionDto> {

    private List<String> numFmt;

    private List<String> description;

    public VersionDto(List<String> numFmt, List<String> description) {
        this.numFmt = numFmt;
        this.description = description;
    }

    public List<String> getNumFmt() {
        return numFmt;
    }

    public void setNumFmt(List<String> numFmt) {
        this.numFmt = numFmt;
    }

    public List<String> getDescription() {
        return description;
    }

    public void setDescription(List<String> description) {
        this.description = description;
    }

    @Override
    public int compareTo(VersionDto dto) {return this.description.get(0).compareTo(dto.getDescription().get(0)); }
}
