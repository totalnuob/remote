import {UpdatedEntity} from "../../common/model/updated-entity";
import {MonitoringHFDataOverall} from "./monitoring-hf-data-overall";
import {MonitoringHFDataClassA} from "./monitoring-hf-data-classA";
import {MonitoringHFDataClassB} from "./monitoring-hf-data-classB";
import {MonitoringHFData} from "./monitoring-hf-data";

export class MonitoringHFDataHolder {
    id: number;

    date: string;
    monitoringData: MonitoringHFData;
}