import {Component, OnInit} from "@angular/core";
import {Router, ActivatedRoute} from '@angular/router';
import {Subscription} from "rxjs/Subscription";
import {ErrorResponse} from "../common/error-response";
import {PeriodicReportService} from "./periodic.report.service";
import {OtherInfoNBReporting} from "./model/other.info.nb.reporting";
import {CommonNBReportingComponent} from "./common.nb.reporting.component";
import {DATA_APP_URL} from "../common/common.service.constants";
import {NICKMFReportingInfo} from "./model/nick.mf.reporting.info.nb.reporting";
import {LookupService} from "../common/lookup.service";
import {BaseDictionary} from "../common/model/base-dictionary";
import {NICReportingChartOfAccounts} from "./model/nic.reporting.chart.of.accounts.";
import {NICKMFReportingInfoHolder} from "./model/nick.mf.reporting.info.holder.nb.reporting";
import {PeriodicReport} from "./model/periodic.report";
import {isNumeric} from "rxjs/util/isNumeric";

import {Observable} from 'rxjs/Observable';
import 'rxjs/add/observable/forkJoin';

declare var $:any

@Component({
    selector: 'mick-mf-input-nb-reporting',
    templateUrl: 'view/nick.mf.input.nb.reporting.component.html',
    styleUrls: [],
    providers: [],
})
export class NICKMFInputNBReportingComponent extends CommonNBReportingComponent implements OnInit {

    private sub: any;
    private reportId;

    busy: Subscription;

    private nbChartOfAccounts: BaseDictionary[];
    private nicReportingChartOfAccounts: NICReportingChartOfAccounts[];

    private data: NICKMFReportingInfoHolder;

    constructor(
        private router: Router,
        private route: ActivatedRoute,
        private lookupService: LookupService,
        private periodicReportService: PeriodicReportService
    ){
        super(router, route, periodicReportService);

        this.data = new NICKMFReportingInfoHolder();
        this.data.records = [];

        Observable.forkJoin(
            // Load lookups
            this.lookupService.getNBChartOfAccounts(),
            this.lookupService.getNICReportingChartOfAccounts(null)
            )
            .subscribe(
                ([data1, data2]) => {
                    this. nbChartOfAccounts = data1;
                    this.nicReportingChartOfAccounts = data2;

                    // load data
                    this.sub = this.route
                        .params
                        .subscribe(params => {
                            this.reportId = +params['id'];
                            if(this.reportId > 0){
                                // load report data

                                this.data.report = new PeriodicReport();
                                this.data.report.id = this.reportId;

                                this.busy = this.periodicReportService.getNICKMFReportingInfo(this.reportId)
                                    .subscribe(
                                        response  => {
                                            if(response){
                                                this.data = response;
                                                if(this.data.records){
                                                    for(var i = 0; i < this.data.records.length; i++) {
                                                        this.nbChartOfAccountsChanged(this.data.records[i], true);
                                                    }

                                                }
                                                //console.log(this.data);
                                            }
                                        },
                                        (error: ErrorResponse) => {
                                            this.successMessage = null;
                                            this.errorMessage = "Error loading NICK MF data";
                                            if(error && !error.isEmpty()){
                                                this.processErrorMessage(error);
                                            }
                                            this.postAction(this.successMessage, this.errorMessage);
                                        }
                                    );
                            }else{
                                // TODO: ??
                                console.log("No report id")
                            }
                        });

                },
                (error: ErrorResponse) => {
                    this.errorMessage = "Error loading lookups";
                    if(error && !error.isEmpty()){
                        this.processErrorMessage(error);
                    }
                    this.postAction(null, this.errorMessage);
                }
            );

    }

    ngOnInit(): any {
    }

    save(){

        if(this.checkRecords()) {

            this.periodicReportService.saveNICKMFReportingInfo(this.data)
                .subscribe(
                    response => {
                        var creationDate = response.creationDate;

                        this.successMessage = "Successfully saved information";
                        this.errorMessage = null;
                        this.postAction(this.successMessage, this.errorMessage);
                    },
                    (error:ErrorResponse) => {
                        this.successMessage = null;
                        this.errorMessage = "Error saving information";
                        if (error && !error.isEmpty()) {
                            this.processErrorMessage(error);
                        }
                        this.postAction(this.successMessage, this.errorMessage);
                    }
                )
        }
    }

    private checkRecords(){
        if(this.data.records){
            let codes = new Set();
            for(var i=0; i < this.data.records.length; i++){
                if(codes.has(this.data.records[i].nicChartOfAccountsCode)){
                    this.postAction(null, "Duplicate chart of accounts for '" + this.data.records[i].nbChartOfAccountsCode + "'");
                    return false;
                }
                codes.add(this.data.records[i].nicChartOfAccountsCode);
            }
        }

        return true;
    }


    nbChartOfAccountsChanged(record, loaded){
        record.matchingNICChartOfAccounts = [];
        if(this.nicReportingChartOfAccounts) {
            for (var i = 0; i < this.nicReportingChartOfAccounts.length; i++) {
                if (this.nicReportingChartOfAccounts[i].nbchartOfAccounts.code === record.nbChartOfAccountsCode) {
                    var alreadyAdded = false;
                    for(var j = 0; !loaded && j < this.data.records.length; j++){
                        if(this.data.records[j].nicChartOfAccountsCode === this.nicReportingChartOfAccounts[i].code){
                            alreadyAdded = true;
                        }
                    }

                    if(!alreadyAdded){
                        record.matchingNICChartOfAccounts.push(new BaseDictionary(this.nicReportingChartOfAccounts[i].code,
                            this.nicReportingChartOfAccounts[i].nameEn,
                            this.nicReportingChartOfAccounts[i].nameRu,
                            this.nicReportingChartOfAccounts[i].nameKz));
                    }
                }
            }
        }

    }

    nicChartOfAccountsChanged(record){
        if(this.data.records != null && this.data.records.length > 0){
            for(var i = 0; i < this.nicReportingChartOfAccounts.length; i++){
            }
        }
    }

    addRecord(){
        let newItem = new NICKMFReportingInfo();
        this.data.records.push(newItem);
        this.successMessage = null;
    }

    removeRecord(record){
        var confirmed = window.confirm("Are you sure want to delete record?");
        if(confirmed) {
            if (this.data.records) {
                for (var i = this.data.records.length; i--;) {
                    if (this.data.records[i] === record) {
                        this.data.records.splice(i, 1);
                    }
                }
            }
        }
    }

    showCalculateSumButton(record){
        if(record.nbChartOfAccountsCode === '2923.010' && record.nicChartOfAccountsName === 'Начисленная амортизация - Организационные расходы NICK MF'){
            return true;
        }else if(record.nbChartOfAccountsCode === '3393.020' && record.nicChartOfAccountsName === 'Комиссия за администрирование к оплате NICK MF'){
            return true;
        }else if(record.nbChartOfAccountsCode === '7473.080' && record.nicChartOfAccountsName === 'Расходы за администрирование NICK MF'){
            return true;
        }else if(record.nbChartOfAccountsCode === '7473.080' && record.nicChartOfAccountsName === 'Амортизация организационных расходов NICK MF'){
            return true;
        }else{
            return false;
        }
    }

    calculateSum(record){
        if(record.nbChartOfAccountsCode === '2923.010' && record.nicChartOfAccountsName === 'Начисленная амортизация - Организационные расходы NICK MF'){
            if(this.data.report.reportDate != null) {
                var month = Number(this.data.report.reportDate.split('-')[1]);
                var sum = Number(0 - 4238 - (14963 / 60 * month)).toFixed(2);
                record.accountBalance = sum;
            }
        }else if(record.nbChartOfAccountsCode === '3393.020' && record.nicChartOfAccountsName === 'Комиссия за администрирование к оплате NICK MF'){
            if(this.data.report.reportDate != null) {
                var month = Number(this.data.report.reportDate.split('-')[1]);
                var sum = Number(0 - (40000/12 * month) + 9999.99 + 9999.99).toFixed(2);
                record.accountBalance = sum;
            }
        }else if(record.nbChartOfAccountsCode === '7473.080' && record.nicChartOfAccountsName === 'Расходы за администрирование NICK MF'){
            if(this.data.report.reportDate != null) {
                var month = Number(this.data.report.reportDate.split('-')[1]);
                var sum = Number(40000/12 * month).toFixed(2);
                record.accountBalance = sum;
            }
        }else if(record.nbChartOfAccountsCode === '7473.080' && record.nicChartOfAccountsName === 'Амортизация организационных расходов NICK MF'){
            if(this.data.report.reportDate != null) {
                var month = Number(this.data.report.reportDate.split('-')[1]);
                var sum = Number(14963/60 * month).toFixed(2);
                record.accountBalance = sum;
            }
        }else{
            alert("No formula exists for this account number and name");
        }
    }

    checkTotal(){
        var sum = this.calculateTotal();
        return (sum > -2 && sum < 2);
    }

    calculateTotal(){
        if(this.data.records){
            var sum = 0.0;
            for(var i = 0; i < this.data.records.length; i++){
                if(isNumeric(this.data.records[i].accountBalance)){
                    sum += Number(this.data.records[i].accountBalance);
                }else{
                }
            }
            return sum;
        }
        return 0;
    }

    copyFromPrevious(){
        this.busy = this.periodicReportService.getNICKMFReportingInfoPreviousMonth(this.reportId)
            .subscribe(
                response  => {
                    if(response && response.records && response.records.length > 0) {
                        for (var i = 0; i < response.records.length; i++) {
                            //console.log(response.records[i]);
                            this.data.records.push(response.records[i]);
                            this.nbChartOfAccountsChanged(response.records[i], true);
                        }
                        this.postAction("Successfully loaded " + response.records.length + " records from previous month", null);
                    }else {
                        this.postAction("No records were loaded from previous month", null);
                    }
                },
                (error: ErrorResponse) => {
                    this.successMessage = null;
                    this.errorMessage = "Error loading NICK MF data from previous month";
                    if(error && !error.isEmpty()){
                        this.processErrorMessage(error);
                    }
                    this.postAction(this.successMessage, this.errorMessage);
                }
            );
    }

    public showNextButton(){
        return this.checkTotal();
    }
}