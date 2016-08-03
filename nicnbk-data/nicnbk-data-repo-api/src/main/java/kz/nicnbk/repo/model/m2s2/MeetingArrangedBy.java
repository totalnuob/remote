package kz.nicnbk.repo.model.m2s2;

import kz.nicnbk.repo.model.base.BaseTypeEntityImpl;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by magzumov on 04.07.2016.
 */

@Entity
@Table(name="meeting_arrangedby")
public class MeetingArrangedBy extends BaseTypeEntityImpl {

    // TODO: refactor
    public static final String BY_NIC = "NIC";
    public static final String BY_GP = "GP";
    public static final String BY_OTHER = "OTHER";

}
