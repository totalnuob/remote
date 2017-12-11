import {Component, OnInit} from "@angular/core";
import {Router, ActivatedRoute} from '@angular/router';
import {Subscription} from "rxjs/Subscription";
import {ErrorResponse} from "../common/error-response";
import {PeriodicReportService} from "./periodic.report.service";
import {CommonNBReportingComponent} from "./common.nb.reporting.component";
import {ConsolidatedBalanceFormRecord} from "./model/consolidated.balance.form.record";
import {DATA_APP_URL} from "../common/common.service.constants";


declare var $:any

var fileSaver = require("file-saver");

@Component({
    selector: 'consolidated-balance-usd-form-nb-reporting',
    templateUrl: 'view/consolidated.balance.usd.form.nb.reporting.component.html',
    styleUrls: [],
    providers: [],
})
export class ConsolidatedBalanceUSDFormNBReportingComponent extends CommonNBReportingComponent implements OnInit {

    private sub: any;
    private reportId;

    busy: Subscription;

    records: ConsolidatedBalanceFormRecord[];

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

                    this.busy = this.periodicReportService.getConsolidatedBalanceUSDForm(this.reportId)
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

    checkSums(){
        if(this.records != null){
            for(var i = 0; i < this.records.length; i++) {
                if (this.records[i].accountNumber == null && this.records[i].lineNumber == 26) {
                    var assets = new Number(this.records[i].currentAccountBalance).toFixed(2);
                } else if (this.records[i].accountNumber == null && this.records[i].lineNumber == 52) {
                    var liabilitiesAndCapital = new Number(this.records[i].currentAccountBalance).toFixed(2);
                }
            }
            var diff = assets - liabilitiesAndCapital;
            if(diff > 2 || diff < -2){
                this.postAction(null, "#26  = " + assets + ", #52 = " + liabilitiesAndCapital + " (difference = " + diff.toFixed(2) + ")");
            }
        }
    }

    export(){
        alert("Export");

        // TODO: separate REST Service for file downloads etc and separate service

        // Xhr creates new context so we need to create reference to this
        let self = this;

        // Status flag used in the template.
        //this.pending = true;

        // Create the Xhr request object
        let xhr = new XMLHttpRequest();
        xhr.withCredentials = true; // send auth token with the request
        // TODO: url const
        let url =  DATA_APP_URL + `periodicReport/export/${this.reportId}/${'CONS_BALANCE_USD'}`;
        xhr.open('GET', url, true);
        xhr.responseType = 'blob';

        // Xhr callback when we get a result back
        // We are not using arrow function because we need the 'this' context
        xhr.onreadystatechange = function() {

            // We use setTimeout to trigger change detection in Zones
            setTimeout( () => {
                //self.pending = false;
            }, 0);

            // If we get an HTTP status OK (200), save the file using fileSaver
            if(xhr.readyState === 4 && xhr.status === 200) {
                var blob = new Blob([this.response], {type: this.response.type});
                fileSaver.saveAs(blob, 'Конс_баланс_USD');

            }
        };

        // Start the Ajax request
        xhr.send();
    }
}