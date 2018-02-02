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
import {GeneratedGLFormRecord} from "./model/generated.form.record";
import {TarragonNICReportingChartOfAccounts} from "./model/tarragon,.nic.reporting.chart.of.accounts.";
import {PEGeneralLedgerFormDataHolder} from "./model/pe.general.ledger.form.data.holder.nb.reporting";
import {PEGeneralLedgerFormDataRecord} from "./model/pe.general.ledger.form.data.record";

declare var $:any

@Component({
    selector: 'tarragon-generated-form-view-nb-reporting',
    templateUrl: 'view/tarragon.generated.form.view.nb.reporting.component.html',
    styleUrls: [],
    providers: [],
})
export class TarragonGeneratedFormViewNBReportingComponent extends CommonNBReportingComponent implements OnInit {

    private sub: any;
    private reportId;

    busy: Subscription;

    private records: GeneratedGLFormRecord[];
    private report: PeriodicReport;

    totalAssetsSum = 0.0;
    totalOtherSum = 0.0;

    constructor(
        private router: Router,
        private route: ActivatedRoute,
        private periodicReportService: PeriodicReportService
    ){
        super(router, route, periodicReportService);

        this.records = [];

        this.sub = this.route
            .params
            .subscribe(params => {
                this.reportId = +params['id'];
                if(this.reportId > 0){
                    // load report data

                this.busy = this.periodicReportService.getGeneratedTarragonForm(this.reportId)
                    .subscribe(
                        response  => {
                            if(response){
                                this.records = response.records;
                                this.calculateSums();
                            }
                        },
                        (error: ErrorResponse) => {
                            this.successMessage = null;
                            this.errorMessage = "Error loading data";
                            if(error && !error.isEmpty()){
                                this.processErrorMessage(error);
                            }

                            this.postAction(null, this.errorMessage);
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

    calculateSums(){
        if(this.records != null){
            var assetsSum = 0.0;
            var otherSum = 0.0;
            for(var i = 0; i < this.records.length; i++){
                if(isNumeric(this.records[i].glaccountBalance) && this.records[i].financialStatementCategory === 'A'){
                    assetsSum += Number(this.records[i].glaccountBalance);
                }else{
                    otherSum += Number(this.records[i].glaccountBalance);
                }
            }

            this.totalAssetsSum = assetsSum;
            this.totalOtherSum = otherSum;
        }
    }

}