package kz.nicnbk.service.dto.version;

/**
 * Created by Pak on 08.11.2019.
 */

public class VersionDto implements Comparable<VersionDto> {

    private String version;
    private String description;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int compareTo(VersionDto dto) {return this.version.compareTo(dto.getVersion()); }
}
