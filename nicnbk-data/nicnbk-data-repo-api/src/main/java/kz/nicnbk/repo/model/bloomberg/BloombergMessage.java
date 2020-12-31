package kz.nicnbk.repo.model.bloomberg;

import kz.nicnbk.repo.model.base.CreateUpdateBaseEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "bloomberg_message")
public class BloombergMessage extends CreateUpdateBaseEntity {
}
