import {Component, OnInit} from "@angular/core";
import {Router, ActivatedRoute} from '@angular/router';
import {Subscription} from "rxjs/Subscription";
import {ErrorResponse} from "../common/error-response";
import {PeriodicReportService} from "./periodic.report.service";
import {CommonNBReportingComponent} from "./common.nb.reporting.component";
import {DATA_APP_URL} from "../common/common.service.constants";
import {ConsolidatedKZTForm14Record} from "./model/consolidated.kzt.form.14.record";


declare var $:any

var fileSaver = require("file-saver");

@Component({
    selector: 'consolidated-kzt-form-14-nb-reporting',
    templateUrl: 'view/consolidated.kzt.form.14.nb.reporting.component.html',
    styleUrls: [],
    providers: [],
})
export class ConsolidatedKZTForm14NBReportingComponent extends CommonNBReportingComponent implements OnInit {

    private sub: any;
    private reportId;

    busy:Subscription;
    busyExport:Subscription;

    records: ConsolidatedKZTForm14Record[];

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

                    this.busy = this.periodicReportService.getConsolidatedKZTForm14(this.reportId)
                        .subscribe(
                            response  => {
                                if(response){
                                    if (response.status === 'FAIL') {
                                        if(response.message != null){
                                            this.errorMessage = response.message.nameEn ? response.message.nameEn :
                                                response.message.nameKz ? response.message.nameKz : response.message.nameRu ? response.message.nameRu : null;
                                        }
                                        if(this.errorMessage == null){
                                            this.errorMessage = "Error loading KZT Form 7";
                                        }
                                        this.records = response.records;
                                        this.postAction(null, this.errorMessage);
                                    }else {
                                        this.records = response.records;
                                    }
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
        this.busyExport = this.periodicReportService.makeFileRequest(DATA_APP_URL + `periodicReport/export/${this.reportId}/${'KZT_FORM_14'}`, 'РТПКЗ-14')
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