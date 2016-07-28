package kz.nicnbk.service.dto.files;

import kz.nicnbk.common.service.model.BaseEntityDto;
import kz.nicnbk.repo.model.files.Files;

/**
 * Created by magzumov on 08.07.2016.
 */
public class FilesDto extends BaseEntityDto<Files> {

    private String type;
    private String fileName;
    private String mimeType;
    private Long size;
    private byte[] bytes;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }
}
