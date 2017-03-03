import { Injectable } from '@angular/core';

import {CommonService} from "../common/common.service";
import { Http, Response, Headers, RequestOptions } from '@angular/http';
import { Observable }     from 'rxjs/Observable';

import {DATA_APP_URL} from "../common/common.service.constants";
import {TableChartDto} from "../google-chart/table-chart.dto";


@Injectable()
export class RiskManagementReportService extends CommonService{

    private RM_REPORT_BASE_URL = DATA_APP_URL + "riskManagement/";
    private RM_LIQUID_REPORT_URL = this.RM_REPORT_BASE_URL + "liquidPortfolioReport/";

    constructor (
        private http: Http)
    {
        super();
    }


    getPortfolioReport(): Observable<TableChartDto> {
        return this.http.get(this.RM_LIQUID_REPORT_URL, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleError);
    }

    getPortfolioReportStatic(){

    }


}