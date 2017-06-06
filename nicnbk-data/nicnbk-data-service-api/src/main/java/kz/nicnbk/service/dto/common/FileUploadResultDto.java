package kz.nicnbk.service.dto.common;

/**
 * Created by magzumov on 08.07.2016.
 */
public class FileUploadResultDto extends StatusResultDto {

    public FileUploadResultDto(){}

    public FileUploadResultDto(StatusResultType status, String messageRu, String messageEn, String messageKz){
        super(status, messageRu, messageEn, messageKz);
    }
}
