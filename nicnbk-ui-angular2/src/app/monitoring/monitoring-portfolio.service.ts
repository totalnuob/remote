import {CommonService} from "../common/common.service";
import {Injectable} from "@angular/core";
import {DATA_APP_URL} from "../common/common.service.constants";
import {FileUploadService} from "../upload/file.upload.service";
import {Http} from "@angular/http";

@Injectable()
export class MonitoringPortfolioService extends CommonService {
    private MONITORING_PORTFOLIO_BASE_URL = DATA_APP_URL + "monitoring/portfolio/";
    private MONITORING_PORTFOLIO_GET_URL = this.MONITORING_PORTFOLIO_BASE_URL + "get/";

    constructor(
        private uploadService: FileUploadService,
        private http: Http)
    {
        super();
    }

    get() {
        return this.http.get(this.MONITORING_PORTFOLIO_GET_URL, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }
}
