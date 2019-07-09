import {UpdatedEntity} from "../../common/model/updated-entity";
import {MonitoringHFDataNameTextValue} from "./monitoring-hf-data-name-text-value";
import {MonitoringHFDataNameNumericValue} from "./monitoring-hf-data-name-numeric-value";
import {MonitoringHFDataNameDateValue} from "./monitoring-hf-data-name-date-value";

export class MonitoringHFDataOverall extends UpdatedEntity{
    generalInformation: MonitoringHFDataNameTextValue[];
    contributionToReturn: MonitoringHFDataNameNumericValue[];
    allocationByStrategy: MonitoringHFDataNameNumericValue[];

    //returns: MonitoringHFDataNameDateValue[];
}