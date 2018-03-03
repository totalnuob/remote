import {Component, OnInit} from "@angular/core";
import {Router, ActivatedRoute} from '@angular/router';
import {Subscription} from "rxjs/Subscription";
import {ErrorResponse} from "../common/error-response";
import {PeriodicReportService} from "./periodic.report.service";
import {CommonNBReportingComponent} from "./common.nb.reporting.component";
import {DATA_APP_URL} from "../common/common.service.constants";
import {ConsolidatedKZTForm8Record} from "./model/consolidated.kzt.form.8.record";
import {ConsolidatedIncomeExpenseFormRecord} from "./model/consolidated.income.expense.form.record";
import {ConsolidatedBalanceForm19Record} from "./model/consolidated.balance.form.19.record";
import {ConsolidatedBalanceForm22Record} from "./model/consolidated.balance.form.22.record";


declare var $:any

var fileSaver = require("file-saver");

@Component({
    selector: 'consolidated-kzt-form-22-nb-reporting',
    templateUrl: 'view/consolidated.kzt.form.22.nb.reporting.component.html',
    styleUrls: [],
    providers: [],
})
export class ConsolidatedKZTForm22NBReportingComponent extends CommonNBReportingComponent implements OnInit {

    private sub: any;
    private reportId;

    busy:Subscription;
    busyExport:Subscription;

    records: ConsolidatedBalanceForm22Record[];

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

                    this.busy = this.periodicReportService.getConsolidatedKZTForm22(this.reportId)
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
        this.busyExport = this.periodicReportService.makeFileRequest(DATA_APP_URL + `periodicReport/export/${this.reportId}/${'KZT_FORM_22'}`, 'РПДХ-22')
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