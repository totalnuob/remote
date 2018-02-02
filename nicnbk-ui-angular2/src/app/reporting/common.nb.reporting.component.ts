import {Component} from "@angular/core";
import {CommonFormViewComponent} from "../common/common.component";
import {Router, ActivatedRoute} from '@angular/router';
import {InputFilesNBReport} from "./model/input.files.nb.report";
import {Subscription} from "rxjs/Subscription";
import {PeriodicReportService} from "./periodic.report.service";
import {ErrorResponse} from "../common/error-response";
import {DATA_APP_URL} from "../common/common.service.constants";
import {PeriodicReportRecord} from "./model/periodic.report.record";
import {PeriodicReportRecordHolder} from "./model/periodic.report.record.holder";

@Component({
    selector: 'statement.balance.operations.nb.reporting',
    templateUrl: 'view/statement.balance.operations.nb.reporting.component.html',
    styleUrls: [],
    providers: [],
})
export class CommonNBReportingComponent extends CommonFormViewComponent {

    constructor(
        private router_: Router,
        private route_: ActivatedRoute,
        private periodicReportService_: PeriodicReportService
    ){
        super(router_);

    }

    public getDoubleValueFormatted(value){
        return value > 0 + '' ? value : value == 0 ? '-' : '(' + (-value) +')';
    }

    public getReportDateShortFormatted(reportDate){
        if(reportDate){
            var monthNum = reportDate.split("-")[1];
            var yearNum = reportDate.split("-")[2];
            if(monthNum === '01'){
                return 'JAN ' + yearNum;
            }else if(monthNum === '02'){
                return 'FEB ' + yearNum;
            }else if(monthNum === '03'){
                return 'MAR ' + yearNum;
            }else if(monthNum === '04'){
                return 'APR ' + yearNum;
            }else if(monthNum === '05'){
                return 'MAY ' + yearNum;
            }else if(monthNum === '06'){
                return 'JUN ' + yearNum;
            }else if(monthNum === '07'){
                return 'JUL ' + yearNum;
            }else if(monthNum === '08'){
                return 'AUG ' + yearNum;
            }else if(monthNum === '09'){
                return 'SEP ' + yearNum;
            }else if(monthNum === '10'){
                return 'OCT ' + yearNum;
            }else if(monthNum === '11'){
                return 'NOV ' + yearNum;
            }else if(monthNum === '12'){
                return 'DEC ' + yearNum;
            }
        }
        return "";
    }


}