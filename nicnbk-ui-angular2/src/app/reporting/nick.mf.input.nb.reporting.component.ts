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

declare var $:any

@Component({
    selector: 'mick-mf-input-nb-reporting',
    templateUrl: 'view/nick.mf..input.nb.reporting.component.html',
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

        this.loadLookups();

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
                                    this.data.records = response.records;
                                    for(var i = 0; i < this.data.records.length; i++){
                                        this.nbChartOfAccountsChanged(this.data.records[i]);

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
    }

    loadLookups(){
        this.lookupService.getNBChartOfAccounts()
            .subscribe(
                data => {
                    this. nbChartOfAccounts = data;
                },
                (error: ErrorResponse) => {
                    this.errorMessage = "Error loading lookups";
                    if(error && !error.isEmpty()){
                        this.processErrorMessage(error);
                    }
                    this.postAction(null, null);
                }
            );

        this.lookupService.getNICReportingChartOfAccounts(null)
            .subscribe(
                data => {
                    this.nicReportingChartOfAccounts = data;
                },
                (error: ErrorResponse) => {
                    this.errorMessage = "Error loading lookups";
                    if(error && !error.isEmpty()){
                        this.processErrorMessage(error);
                    }
                    this.postAction(null, null);
                }
            );
    }


    save(){
        this.periodicReportService.saveNICKMFReportingInfo(this.data)
            .subscribe(
                response  => {
                    var creationDate = response.creationDate;

                    this.successMessage = "Successfully saved information";
                    this.errorMessage = null;
                },
                (error: ErrorResponse) => {
                    this.successMessage = null;
                    this.errorMessage = "Error saving information";
                    if(error && !error.isEmpty()){
                        this.processErrorMessage(error);
                    }
                    this.postAction(null, null);
                }
            )
    }


    nbChartOfAccountsChanged(record){
        record.matchingNICChartOfAccounts = [];
        for(var i = 0; i < this.nicReportingChartOfAccounts.length; i++){
            if(this.nicReportingChartOfAccounts[i].nbchartOfAccounts.code === record.nbChartOfAccountsCode){
                record.matchingNICChartOfAccounts.push(new BaseDictionary(this.nicReportingChartOfAccounts[i].code,
                    this.nicReportingChartOfAccounts[i].nameEn,
                    this.nicReportingChartOfAccounts[i].nameRu,
                    this.nicReportingChartOfAccounts[i].nameKz));
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

    checkTotal(){
        var sum = this.calculateTotal();
        return (sum > -1 && sum < 1);
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
                            this.nbChartOfAccountsChanged(response.records[i]);
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
                    this.postAction(null, null);
                }
            );
    }
}