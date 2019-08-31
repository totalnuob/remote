package kz.nicnbk.service.dto.hr;

import kz.nicnbk.service.dto.common.ResponseDto;
import kz.nicnbk.service.dto.common.ResponseStatusType;
import kz.nicnbk.service.dto.files.FilesDto;

import java.util.List;

/**
 * Created by Pak on 28.08.2019.
 */

public class DocsResultDto extends ResponseDto {

    private List<FilesDto> filesDtoList;

    public DocsResultDto(ResponseStatusType status, String messageRu, String messageEn, String messageKz, List<FilesDto> filesDtoList) {
        super(status, messageRu, messageEn, messageKz);
        this.filesDtoList = filesDtoList;
    }

    public List<FilesDto> getFilesDtoList() {
        return filesDtoList;
    }

    public void setFilesDtoList(List<FilesDto> filesDtoList) {
        this.filesDtoList = filesDtoList;
    }
}
