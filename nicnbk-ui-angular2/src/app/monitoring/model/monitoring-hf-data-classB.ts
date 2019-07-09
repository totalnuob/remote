import {UpdatedEntity} from "../../common/model/updated-entity";
import {MonitoringHFDataNameTextValue} from "./monitoring-hf-data-name-text-value";
import {MonitoringHFDataNameNumericValue} from "./monitoring-hf-data-name-numeric-value";
import {MonitoringHFDataTopFundInfo} from "./monitoring-hf-data-top-fund-info";
import {MonitoringHFDataNameDateValue} from "./monitoring-hf-data-name-date-value";

export class MonitoringHFDataClassB extends UpdatedEntity{
    generalInformation: MonitoringHFDataNameTextValue[];
    allocationByStrategy: MonitoringHFDataNameNumericValue[];


    fundAllocations: MonitoringHFDataTopFundInfo[];

    //returns: MonitoringHFDataNameDateValue[];
}