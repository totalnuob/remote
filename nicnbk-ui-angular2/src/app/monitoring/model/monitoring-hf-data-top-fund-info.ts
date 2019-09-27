import {UpdatedEntity} from "../../common/model/updated-entity";

export class MonitoringHFDataTopFundInfo extends UpdatedEntity{
    fundName: string;
    strategy: string;
    ytd: number;
    allocation: number;

}