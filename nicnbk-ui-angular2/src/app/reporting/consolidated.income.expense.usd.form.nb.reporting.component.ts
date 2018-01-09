import {Component, OnInit} from "@angular/core";
import {Router, ActivatedRoute} from '@angular/router';
import {Subscription} from "rxjs/Subscription";
import {ErrorResponse} from "../common/error-response";
import {PeriodicReportService} from "./periodic.report.service";
import {CommonNBReportingComponent} from "./common.nb.reporting.component";
import {ConsolidatedBalanceFormRecord} from "./model/consolidated.balance.form.record";
import {ConsolidatedIncomeExpenseFormRecord} from "./model/consolidated.income.expense.form.record";
import {DATA_APP_URL} from "../common/common.service.constants";


declare var $:any

var fileSaver = require("file-saver");

@Component({
    selector: 'consolidated-income-expense-usd-form-nb-reporting',
    templateUrl: 'view/consolidated.income.expense.usd.form.nb.reporting.component.html',
    styleUrls: [],
    providers: [],
})
export class ConsolidatedIncomeExpenseUSDFormNBReportingComponent extends CommonNBReportingComponent implements OnInit {

    private sub: any;
    private reportId;

    busy: Subscription;
    busyExport: Subscription;

    records: ConsolidatedIncomeExpenseFormRecord[];

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

                    this.busy = this.periodicReportService.getConsolidatedIncomeExpenseUSDForm(this.reportId)
                        .subscribe(
                            response  => {
                                if(response){
                                    this.records = response;
                                    this.checkSums();
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
        this.busyExport = this.periodicReportService.makeFileRequest(DATA_APP_URL + `periodicReport/export/${this.reportId}/${'INCOME_EXP_USD'}`, 'опиу_USD')
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

    checkSums(){
        //if(this.records != null){
        //    for(var i = 0; i < this.records.length; i++) {
        //        if (this.records[i].accountNumber == null && this.records[i].lineNumber == 26) {
        //            var assets = new Number(this.records[i].currentAccountBalance).toFixed(2);
        //        } else if (this.records[i].accountNumber == null && this.records[i].lineNumber == 52) {
        //            var liabilitiesAndCapital = new Number(this.records[i].currentAccountBalance).toFixed(2);
        //        }
        //    }
        //    var diff = assets - liabilitiesAndCapital;
        //    if(diff > 2 || diff < -2){
        //        this.postAction(null, "#26  = " + assets + ", #52 = " + liabilitiesAndCapital + " (difference = " + diff.toFixed(2));
        //    }
        //}
    }
}