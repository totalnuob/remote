import {Component} from "@angular/core";
import {CommonFormViewComponent} from "../common/common.component";
import {Router, ActivatedRoute, NavigationEnd} from '@angular/router';
import {InputFilesNBReport} from "./model/input.files.nb.report";
import {Subscription} from "rxjs/Subscription";
import {PeriodicReportService} from "./periodic.report.service";
import {ErrorResponse} from "../common/error-response";
import {DATA_APP_URL} from "../common/common.service.constants";
import {PeriodicReportRecord} from "./model/periodic.report.record";
import {PeriodicReportRecordHolder} from "./model/periodic.report.record.holder";
import {CommonNBReportingComponent} from "./common.nb.reporting.component";
import {PeriodicReport} from "./model/periodic.report";
import {TerraCombinedRecordHolder} from "./model/terra.combined.record.holder";

@Component({
    selector: 'terra.combined.nb.reporting',
    templateUrl: 'view/terra.combined.nb.reporting.component.html',
    styleUrls: [],
    providers: [],
})
export class TerraCombinedNBReportingComponent extends CommonNBReportingComponent {

    private sub: any;
    private reportId;

    recordsHolder: TerraCombinedRecordHolder;
    report;
    prevPageSource;


    busy: Subscription;

    constructor(
        private router: Router,
        private route: ActivatedRoute,
        private periodicReportService: PeriodicReportService
    ){
        super(router, route, periodicReportService);

        this.recordsHolder = new TerraCombinedRecordHolder;

        this.sub = this.route
            .params
            .subscribe(params => {
                this.reportId = +params['id'];
                this.prevPageSource = params['prevPageSource'];

                if(this.reportId > 0){
                    // load report data

                    this.busy = this.periodicReportService.getTerraCombined(this.reportId)
                        .subscribe(
                            response  => {
                                this.recordsHolder = response;
                                console.log(response);
                                this.report = response.report;
                            },
                            (error: ErrorResponse) => {
                                this.successMessage = null;
                                this.errorMessage = "Error loading Terra Combined records";
                                if(error && !error.isEmpty()){
                                    this.processErrorMessage(error);
                                }
                                this.postAction(null, null);
                            }
                        );
                }else{
                    // TODO: ??
                }
            });
    }

}