import { Component } from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {HedgeFundService} from "./hf.fund.service";
import {CommonFormViewComponent} from "../common/common.component";
import {Subscription} from 'rxjs';
import {ModuleAccessCheckerService} from "../authentication/module.access.checker.service";
import {ErrorResponse} from "../common/error-response";
import {HedgeFundScreeningSearchParams} from "./model/hf.screening-search-params";
import {HedgeFundScreeningSearchResults} from "./model/hf-screening-search-results";
import {HedgeFundScreening} from "./model/hf.screening";
import {HedgeFundScreeningService} from "./hf.fund.screening.service";
import {HedgeFundScreeningFilteredResult} from "./model/hf.screening.filtered.result";
import {HedgeFundScreeningFilteredResultStatistics} from "./model/hf.screening.filtered.result.statistics";

declare var $:any

@Component({
    selector: 'hf-fund-search',
    templateUrl: 'view/hf.screening.filters.edit.component.html',
    styleUrls: [
        //'../../../public/css/...',
        '../../../node_modules/angular2-busy/build/style/busy.css'
    ],
    providers: []
})
export class HFScreeningFilteredResultsEditComponent extends CommonFormViewComponent{

    screeningId;
    id;
    filteredResult = new HedgeFundScreeningFilteredResult;
    filteredFundList;

    private moduleAccessChecker: ModuleAccessCheckerService;

    public sub: any;
    busyGet : Subscription;
    busyStats : Subscription;

    constructor(
        private screeningService: HedgeFundScreeningService,
        private router: Router,
        private route: ActivatedRoute
    ){
        super(router);

        this.moduleAccessChecker = new ModuleAccessCheckerService;

        if(!this.moduleAccessChecker.checkAccessHedgeFunds()){
            this.router.navigate(['accessDenied']);
        }

        this.sub = this.route
            .params
            .subscribe(params => {
                this.screeningId = +params['screeningId'];
                this.id = +params['id'];
                if(this.screeningId > 0) {
                    if (this.id > 0) {
                        this.busyGet = this.screeningService.getFilteredResult(this.id)
                            .subscribe(
                                result => {
                                    console.log(result);
                                    this.filteredResult = result;
                                },
                                error => {
                                    console.log(error);
                                    this.postAction(null, "Failed to load screening filtered results");
                                }
                            );
                    } else {
                        this.filteredResult = new HedgeFundScreeningFilteredResult();
                        this.filteredResult.screeningId = this.screeningId;
                    }
                }
            });
    }

    ngOnInit():any {
        $('#startDateTPickeer').datetimepicker({
            //defaultDate: new Date(),
            format: 'DD-MM-YYYY'
        });
    }

    save() {
        this.filteredResult.startDate= $('#startDate').val();

        console.log(this.filteredResult);
        this.busyGet = this.screeningService.saveFilteredResult(this.filteredResult)
            .subscribe(
                response => {
                    this.filteredResult.id = response.entityId;
                    this.postAction("Successfully saved filters.", null);
                },
                (error: ErrorResponse) => {
                    this.errorMessage = "Error saving filters";
                    if(error && !error.isEmpty()){
                        this.processErrorMessage(error);
                    }
                    this.postAction(null, this.errorMessage);
                }
            );
    }


    applyFilters(){

        this.filteredResult.startDate= $('#startDate').val();

        // TODO: check required parameters

        this.busyStats = this.screeningService.getFilteredResultStatistics(this.filteredResult)
            .subscribe(
                result => {
                    console.log(result);
                    this.filteredResult.filteredResultStatistics = result;
                },
                error => {
                    this.postAction(null, "Failed to load screening filtered results statistics");
                }
            );
    }

    showFunds(lookbackReturn, lookbackAUM){

        this.filteredResult.startDate= $('#startDate').val();

        var params = new HedgeFundScreeningFilteredResult();
        params.screeningId = this.filteredResult.screeningId;

        params.fundAUM = this.filteredResult.fundAUM;
        params.managerAUM = this.filteredResult.managerAUM;
        params.startDate = this.filteredResult.startDate;
        params.trackRecord = this.filteredResult.trackRecord;
        params.lookbackReturns = lookbackReturn;
        params.lookbackAUM = lookbackAUM;

        this.filteredFundList = [];

        this.busyStats = this.screeningService.getFilteredResultQualifiedFundList(params)
            .subscribe(
                result => {
                    this.filteredFundList = result;
                },
                error => {
                    this.postAction(null, "Failed to load screening filtered results statistics");
                }
            );
    }

}