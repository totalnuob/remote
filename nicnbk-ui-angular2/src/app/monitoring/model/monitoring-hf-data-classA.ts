import {UpdatedEntity} from "../../common/model/updated-entity";
import {MonitoringHFDataNameTextValue} from "./monitoring-hf-data-name-text-value";
import {MonitoringHFDataNameNumericValue} from "./monitoring-hf-data-name-numeric-value";
import {MonitoringHFDataNameDateValue} from "./monitoring-hf-data-name-date-value";
import {MonitoringHFDataTopFundInfo} from "./monitoring-hf-data-top-fund-info";

export class MonitoringHFDataClassA extends UpdatedEntity{
    generalInformation: MonitoringHFDataNameTextValue[];
    allocationByStrategy: MonitoringHFDataNameNumericValue[];

    positiveContributors: MonitoringHFDataTopFundInfo[];
    negativeContributors: MonitoringHFDataTopFundInfo[];
    fundAllocations: MonitoringHFDataTopFundInfo[];

    returns: MonitoringHFDataNameDateValue[];
}