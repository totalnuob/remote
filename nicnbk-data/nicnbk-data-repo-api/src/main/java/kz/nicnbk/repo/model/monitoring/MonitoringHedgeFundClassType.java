package kz.nicnbk.repo.model.monitoring;

import kz.nicnbk.repo.model.base.BaseTypeEntityImpl;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by magzumov
 */

@Entity
@Table(name="monitoring_hf_class_type")
public class MonitoringHedgeFundClassType extends BaseTypeEntityImpl {

    public MonitoringHedgeFundClassType(){}
    public MonitoringHedgeFundClassType(Integer id){
        this.setId(id);
    }
}
