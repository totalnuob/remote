import {Component, OnInit} from "@angular/core";
import {Router, ActivatedRoute} from '@angular/router';
import {Subscription} from "rxjs/Subscription";
import {ErrorResponse} from "../common/error-response";
import {PeriodicReportService} from "./periodic.report.service";
import {CommonNBReportingComponent} from "./common.nb.reporting.component";
import {DATA_APP_URL} from "../common/common.service.constants";
import {ConsolidatedKZTForm7Record} from "./model/consolidated.kzt.form.7.record";


declare var $:any

var fileSaver = require("file-saver");

@Component({
    selector: 'consolidated-kzt-form-7-nb-reporting',
    templateUrl: 'view/consolidated.kzt.form.7.nb.reporting.component.html',
    styleUrls: [],
    providers: [],
})
export class ConsolidatedKZTForm7NBReportingComponent extends CommonNBReportingComponent implements OnInit {

    private sub: any;
    private reportId;

    busy:Subscription;
    busyExport:Subscription;

    records: ConsolidatedKZTForm7Record[];

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
                if(this.reportId > 0){
                    // load report data

                    this.records = [];

                    this.busy = this.periodicReportService.getConsolidatedKZTForm7(this.reportId)
                        .subscribe(
                            response  => {
                                if(response){
                                    this.records = response;
                                    console.log(this.records);
                                }
                            },
                            (error: ErrorResponse) => {
                                this.successMessage = null;
                                this.errorMessage = "Error loading data";
                                if(error && !error.isEmpty()){
                                    this.processErrorMessage(error);
                                }
                                this.postAction(null, null);
                            }
                        );
                }else{
                    // TODO: ??
                    console.log("No report id")
                }
            });
    }

    ngOnInit(): any {
        this.postAction(null, null);
    }


    export() {
        this.busyExport = this.periodicReportService.makeFileRequest(DATA_APP_URL + `periodicReport/export/${this.reportId}/${'KZT_FORM_7'}`, 'РФИ-7')
            .subscribe(
                response  => {
                    //console.log("ok");
                },
                error => {
                    //console.log("fails")
                    this.postAction(null, "Error exporting data");
                }
            );
    }
}