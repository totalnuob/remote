import {Component, OnInit} from "@angular/core";
import {CommonFormViewComponent} from "../common/common.component";
import { Http, Response } from '@angular/http';

import {Router, ActivatedRoute} from '@angular/router';
import {PeriodicReportService} from "./periodic.report.service";
import {PeriodicReport} from "./model/periodic.report";
import {ErrorResponse} from "../common/error-response";
import {Subscription} from "rxjs/Subscription";
import {CommonNBReportingComponent} from "./common.nb.reporting.component";
import {OKResponse} from "../common/ok-response";
import {SaveResponse} from "../common/save-response";

declare var $:any

@Component({
    selector: 'lookup-values-nb-reporting',
    templateUrl: 'view/lookup.values.nb.reporting.component.html',
    styleUrls: [],
    providers: [],
})
export class LookupValuesNBReportingComponent extends CommonNBReportingComponent implements OnInit{

    constructor(
        private router: Router,
        private route: ActivatedRoute,
        private periodicReportService: PeriodicReportService
    ){
        super(router, route, periodicReportService);
    }

    busy: Subscription;

    ngOnInit():void {
        //this.busy = this.periodicReportService.loadAll()
        //    .subscribe(
        //        response  => {
        //            this.reportList = response;
        //        },
        //        (error: ErrorResponse) => {
        //            this.processErrorResponse(error);
        //        }
        //    )
    }

}