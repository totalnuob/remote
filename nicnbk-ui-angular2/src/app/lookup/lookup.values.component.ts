import {Component, OnInit} from "@angular/core";
import {CommonFormViewComponent} from "../common/common.component";
import { Http, Response } from '@angular/http';

import {Router, ActivatedRoute} from '@angular/router';
import {PeriodicReportService} from "../reporting/periodic.report.service";
import {PeriodicReport} from "../reporting/model/periodic.report";
import {ErrorResponse} from "../common/error-response";
import {Subscription} from "rxjs/Subscription";
import {CommonNBReportingComponent} from "../reporting/common.nb.reporting.component";
import {OKResponse} from "../common/ok-response";
import {SaveResponse} from "../common/save-response";

declare var $:any

@Component({
    selector: 'lookup-values-nb-reporting',
    templateUrl: 'view/lookup.values.component.html',
    styleUrls: [],
    providers: [],
})
export class LookupValuesComponent extends CommonNBReportingComponent implements OnInit{

    constructor(
        private router: Router,
        private route: ActivatedRoute,
        private periodicReportService: PeriodicReportService
    ){
        super(router, route, periodicReportService);
    }

    busy: Subscription;

    ngOnInit():void {

    }

}