package kz.nicnbk.repo.model.files;

import kz.nicnbk.repo.model.base.CreateUpdateBaseEntity;
import kz.nicnbk.repo.model.base.TypedEntity;

import javax.persistence.*;

/**
 * Files entity.
 *
 * Created by magzumov on 19.05.2016.
 */

@Entity
public class Files extends CreateUpdateBaseEntity  implements TypedEntity<FilesType> {

    private FilesType type;

    private String fileName;

    private Long size;

    private String mimeType;


    @Override
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id")
    public FilesType getType() {
        return type;
    }

    @Override
    public void setType(FilesType type) {
        this.type = type;
    }

    @Column(name="filename")
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Column(name="size")
    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    @Column(name="mimetype")
    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
}
