package kz.nicnbk.repo.model.m2s2;

import kz.nicnbk.repo.model.base.BaseTypeEntityImpl;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by magzumov on 04.07.2016.
 */


@Deprecated
@Entity
@Table(name="memo_type")
public class MemoType extends BaseTypeEntityImpl {

    // TODO: refactor
    public static final String GENERAL = "GENERAL";
}
