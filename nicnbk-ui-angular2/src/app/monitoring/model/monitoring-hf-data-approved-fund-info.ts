import {UpdatedEntity} from "../../common/model/updated-entity";

export class MonitoringHFDataApprovedFundInfo extends UpdatedEntity{
    fundName: string;
    managerName: string;
    strategy: string;

    protocol: string;
    approveDate: string;
    limits: string;

}