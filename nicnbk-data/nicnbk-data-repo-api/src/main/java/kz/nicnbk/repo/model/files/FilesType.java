package kz.nicnbk.repo.model.files;

import kz.nicnbk.repo.model.base.BaseTypeEntityImpl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by magzumov on 04.07.2016.
 */

@Entity
@Table(name="files_type")
public class FilesType extends BaseTypeEntityImpl {

    private String catalog;

    @Column(name = "catalog")
    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }
}
