import {Component, OnInit} from "@angular/core";
import {Router, ActivatedRoute} from '@angular/router';
import {Subscription} from "rxjs/Subscription";
import {ErrorResponse} from "../common/error-response";
import {PeriodicReportService} from "./periodic.report.service";
import {CommonNBReportingComponent} from "./common.nb.reporting.component";
import {ConsolidatedBalanceFormRecord} from "./model/consolidated.balance.form.record";
import {ConsolidatedIncomeExpenseFormRecord} from "./model/consolidated.income.expense.form.record";
import {ConsolidatedTotalIncomeFormRecord} from "./model/consolidated.total.income.form.record";
import {DATA_APP_URL} from "../common/common.service.constants";


declare var $:any

var fileSaver = require("file-saver");

@Component({
    selector: 'consolidated-total-income-usd-form-nb-reporting',
    templateUrl: 'view/consolidated.total.income.usd.form.nb.reporting.component.html',
    styleUrls: [],
    providers: [],
})
export class ConsolidatedTotalIncomeUSDFormNBReportingComponent extends CommonNBReportingComponent implements OnInit {

    private sub: any;
    private reportId;

    busy:Subscription;
    busyExport:Subscription;

    records: ConsolidatedTotalIncomeFormRecord[];

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

                    this.busy = this.periodicReportService.getConsolidatedTotalIncomeUSDForm(this.reportId)
                        .subscribe(
                            response  => {
                                if(response){
                                    this.records = response;
                                    console.log(response);
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
        this.busyExport = this.periodicReportService.makeFileRequest(DATA_APP_URL + `periodicReport/export/${this.reportId}/${'TOTAL_INCOME_USD'}`, 'опсд_USD')
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