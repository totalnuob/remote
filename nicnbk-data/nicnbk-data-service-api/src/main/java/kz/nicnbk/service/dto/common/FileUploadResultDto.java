package kz.nicnbk.service.dto.common;

/**
 * Created by magzumov on 08.07.2016.
 */
public class FileUploadResultDto extends StatusResultDto {

    private Long fileId;

    public FileUploadResultDto(){}

    public FileUploadResultDto(StatusResultType status, String messageRu, String messageEn, String messageKz){
        super(status, messageRu, messageEn, messageKz);
    }

    public FileUploadResultDto(Long fileId, StatusResultType status, String messageRu, String messageEn, String messageKz){
        super(status, messageRu, messageEn, messageKz);
        this.fileId = fileId;
    }

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }
}
