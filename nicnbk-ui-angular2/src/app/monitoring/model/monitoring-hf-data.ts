import {UpdatedEntity} from "../../common/model/updated-entity";
import {MonitoringHFDataOverall} from "./monitoring-hf-data-overall";
import {MonitoringHFDataClassA} from "./monitoring-hf-data-classA";
import {MonitoringHFDataClassB} from "./monitoring-hf-data-classB";
import {MonitoringHFDataNameDateValue} from "./monitoring-hf-data-name-date-value";
import {MonitoringHFDataApprovedFundInfo} from "./monitoring-hf-data-approved-fund-info";

export class MonitoringHFData extends UpdatedEntity{
    overall: MonitoringHFDataOverall;
    classA: MonitoringHFDataClassA;
    classB: MonitoringHFDataClassB;
    approvedFunds: MonitoringHFDataApprovedFundInfo[];
}