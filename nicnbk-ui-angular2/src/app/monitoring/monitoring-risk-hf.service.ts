import {CommonService} from "../common/common.service";
import {Injectable} from "@angular/core";
import {DATA_APP_URL} from "../common/common.service.constants";
import {FileUploadService} from "../upload/file.upload.service";
import {Http} from "@angular/http";

@Injectable()
export class MonitoringRiskHedgeFundService extends CommonService {
    private MONITORING_RISK_BASE_URL = DATA_APP_URL + "monitoring/risk/";
    private MONITORING_RISK_MONTHLY_HF_GET_URL = this.MONITORING_RISK_BASE_URL + "hfMonthly/";
    private MONITORING_RISK_MONTHLY_HF_AVAILABLE_DATES_GET_URL = this.MONITORING_RISK_BASE_URL + "dateList/";

    constructor(
        private uploadService: FileUploadService,
        private http: Http)
    {
        super();
    }

    getMonthlyHFRiskReport(selectedDate) {
        var body = JSON.stringify({"date": selectedDate});
        return this.http.post(this.MONITORING_RISK_MONTHLY_HF_GET_URL, body, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    getAvailableDates(){
        return this.http.get(this.MONITORING_RISK_MONTHLY_HF_AVAILABLE_DATES_GET_URL, this.getOptionsWithCredentials())
                .map(this.extractData)
                .catch(this.handleErrorResponse);
    }
}
