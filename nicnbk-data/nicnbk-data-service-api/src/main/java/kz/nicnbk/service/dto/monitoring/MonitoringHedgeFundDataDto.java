package kz.nicnbk.service.dto.monitoring;

import kz.nicnbk.common.service.model.BaseDto;
import java.util.Date;

/**
 * Created by magzumov on 07.07.2016.
 */
public class MonitoringHedgeFundDataDto implements BaseDto{

    private MonitoringHedgeFundDataOverallDto overall;
    private MonitoringHedgeFundDataClassADto classA;
    private MonitoringHedgeFundDataOverallDto classB;

    public MonitoringHedgeFundDataOverallDto getOverall() {
        return overall;
    }

    public void setOverall(MonitoringHedgeFundDataOverallDto overall) {
        this.overall = overall;
    }

    public MonitoringHedgeFundDataClassADto getClassA() {
        return classA;
    }

    public void setClassA(MonitoringHedgeFundDataClassADto classA) {
        this.classA = classA;
    }

    public MonitoringHedgeFundDataOverallDto getClassB() {
        return classB;
    }

    public void setClassB(MonitoringHedgeFundDataOverallDto classB) {
        this.classB = classB;
    }
}
