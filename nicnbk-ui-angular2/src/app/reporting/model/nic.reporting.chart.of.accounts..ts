import {BaseDictionary} from "../../common/model/base-dictionary";
export class NICReportingChartOfAccounts extends BaseDictionary{

    nbchartOfAccounts: BaseDictionary;

    constructor(){
        super();
        this.nbchartOfAccounts = new BaseDictionary();
    }
}