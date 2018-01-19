package kz.nicnbk.service.dto.common;


/**
 * Created by magzumov on 08.07.2016.
 */
public class FileUploadResultDto extends ResponseDto {

    private Long fileId;
    private String fileName;

    public FileUploadResultDto(){}

    public FileUploadResultDto(ResponseStatusType status, String messageRu, String messageEn, String messageKz){
        super(status, messageRu, messageEn, messageKz);
    }

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
