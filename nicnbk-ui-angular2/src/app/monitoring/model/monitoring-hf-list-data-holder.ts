import {UpdatedEntity} from "../../common/model/updated-entity";
import {MonitoringHFDataOverall} from "./monitoring-hf-data-overall";
import {MonitoringHFDataClassA} from "./monitoring-hf-data-classA";
import {MonitoringHFDataClassB} from "./monitoring-hf-data-classB";
import {MonitoringHFData} from "./monitoring-hf-data";
import {MonitoringHFDataHolder} from "./monitoring-hf-data-holder";
import {MonitoringHFDataNameDateValue} from "./monitoring-hf-data-name-date-value";

export class MonitoringHFListDataHolder {

    data: MonitoringHFDataHolder[];
    returnsHFRI: MonitoringHFDataNameDateValue[];
}