import {Component, OnInit} from "@angular/core";
import {Router, ActivatedRoute} from '@angular/router';
import {Subscription} from "rxjs/Subscription";
import {ErrorResponse} from "../common/error-response";
import {PeriodicReportService} from "./periodic.report.service";
import {OtherInfoNBReporting} from "./model/other.info.nb.reporting";
import {CommonNBReportingComponent} from "./common.nb.reporting.component";
import {DATA_APP_URL} from "../common/common.service.constants";


declare var $:any

@Component({
    selector: 'generate-reports-input-nb-reporting',
    templateUrl: 'view/generate.reports.nb.reporting.component.html',
    styleUrls: [],
    providers: [],
})
export class GenerateReportsNBReportingComponent extends CommonNBReportingComponent implements OnInit {

    private sub: any;
    private reportId;

    busy: Subscription;

    constructor(
        private router: Router,
        private route: ActivatedRoute,
        private periodicReportService: PeriodicReportService
    ){
        super(router, route, periodicReportService);

        this.sub = this.route
            .params
            .subscribe(params => {
                this.reportId = +params['id'];
                console.log(this.reportId);
                if(this.reportId > 0){
                    // load report data

                    //this.busy = this.periodicReportService.getGeneratedReports(this.reportId)
                    //    .subscribe(
                    //        response  => {
                    //            if(response){
                    //                console.log(response);
                    //                this.otherInfo = response;
                    //                console.log(this.otherInfo);
                    //            }
                    //        },
                    //        (error: ErrorResponse) => {
                    //            this.successMessage = null;
                    //            this.errorMessage = "Error loading Other Info";
                    //            if(error && !error.isEmpty()){
                    //                this.processErrorMessage(error);
                    //            }
                    //            this.postAction(null, null);
                    //        }
                    //    );
                }else{
                    // TODO: ??
                    console.log("No report id")
                }
            });
    }

    ngOnInit(): any {
        this.postAction(null, null);
    }
}