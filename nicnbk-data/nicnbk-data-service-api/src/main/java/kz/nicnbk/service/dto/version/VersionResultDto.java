package kz.nicnbk.service.dto.version;

import kz.nicnbk.service.dto.common.ResponseDto;
import kz.nicnbk.service.dto.common.ResponseStatusType;

import java.util.List;

/**
 * Created by Pak on 08.11.2019.
 */

public class VersionResultDto extends ResponseDto {

    private List<VersionDto> versionDtoList;

    public VersionResultDto(ResponseStatusType status, String messageRu, String messageEn, String messageKz, List<VersionDto> versionDtoList) {
        super(status, messageRu, messageEn, messageKz);
        this.versionDtoList = versionDtoList;
    }

    public List<VersionDto> getVersionDtoList() {
        return versionDtoList;
    }

    public void setVersionDtoList(List<VersionDto> versionDtoList) {
        this.versionDtoList = versionDtoList;
    }
}
