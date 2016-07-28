package kz.nicnbk.repo.model.news;

import kz.nicnbk.repo.model.base.BaseTypeEntityImpl;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by magzumov on 04.07.2016.
 */

@Entity
@Table(name="news_type")
public class NewsType extends BaseTypeEntityImpl {
}
