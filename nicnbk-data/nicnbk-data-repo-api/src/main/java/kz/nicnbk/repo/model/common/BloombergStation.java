package kz.nicnbk.repo.model.common;

import kz.nicnbk.repo.model.base.BaseTypeEntityImpl;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "bloomberg_station")
public class BloombergStation extends BaseTypeEntityImpl{

    // TODO: refactor
    public static final String YAO = "10.10.165.123";
    public static final String DRM = "";
}