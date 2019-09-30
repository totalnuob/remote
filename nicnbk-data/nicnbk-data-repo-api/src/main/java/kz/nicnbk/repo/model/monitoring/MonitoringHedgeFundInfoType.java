package kz.nicnbk.repo.model.monitoring;

import kz.nicnbk.repo.model.base.BaseTypeEntityImpl;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by magzumov
 */

@Entity
@Table(name="monitoring_hf_fund_info_type")
public class MonitoringHedgeFundInfoType extends BaseTypeEntityImpl {

    public MonitoringHedgeFundInfoType(){}

    public MonitoringHedgeFundInfoType(Integer id){
        this.setId(id);
    }
}
