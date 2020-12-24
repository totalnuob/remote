package kz.nicnbk.repo.model.risk;

import kz.nicnbk.repo.model.base.CreateUpdateBaseEntity;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@Entity
@Audited
@Table(name = "portfolio_var_value")
public class PortfolioVarValue extends CreateUpdateBaseEntity {
    private PortfolioVar portfolioVar;
    private Date date;
    private Double value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_var_id", nullable = false)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    public PortfolioVar getPortfolioVar(){
        return portfolioVar;
    }

    public void setPortfolioVar(PortfolioVar portfolioVar){
        this.portfolioVar = portfolioVar;
    }

    @Column(name="asof_date", nullable = false)
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern="dd-MM-yyyy")
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Column(name = "value")
    public Double getValue(){
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
