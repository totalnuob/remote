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


}