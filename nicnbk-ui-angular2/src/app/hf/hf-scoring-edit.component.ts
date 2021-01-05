import {Component,NgModule, OnInit, ViewChild} from "@angular/core";
import {ActivatedRoute} from "@angular/router";
import {SelectComponent} from "ng2-select";
import {CommonFormViewComponent} from "../common/common.component";
import {EmployeeService} from "../employee/employee.service";
import {Subscription} from 'rxjs';
import {ModuleAccessCheckerService} from "../authentication/module.access.checker.service";
import {ErrorResponse} from "../common/error-response";
import {Router} from '@angular/router';
import {Observable} from 'rxjs/Observable';
import 'rxjs/add/observable/forkJoin';
import {HedgeFundScoring} from "./model/hf.scoring";
import {HedgeFundScoringSearchParams} from "./model/hf.scoring-search-params";
import {HedgeFundScoringService} from "./hf.fund.scoring.service";
import {HedgeFundScreeningService} from "./hf.fund.screening.service";
import 'rxjs/add/observable/forkJoin';
import {HedgeFundScreeningSearchParams} from "./model/hf.screening-search-params";
import {HedgeFundScreening} from "./model/hf.screening";
import {HedgeFundScreeningFilteredResult} from "./model/hf.screening.filtered.result";
import {HedgeFundScoringFund} from "./model/hf.scoring.fund";
import {HedgeFundScoringFundParams} from "./model/hf.scoring.fund,params";

declare var $:any
declare var Chart: any;

@Component({
    selector: 'hf-scoring-edit',
    templateUrl: 'view/hf-scoring-edit.component.html',
    styleUrls: [],
    providers: [],
})
@NgModule({
    imports: []
})
export class HFScoringEditComponent extends CommonFormViewComponent implements OnInit{

    private sub: any;

    screenings: HedgeFundScreening[];
    filters: HedgeFundScreeningFilteredResult[];

    selectedScreeningId;
    selectedFilterId;

    lookbackAUMList;
    lookbackReturnList;

    selectedLookbackAUM;
    selectedLookbackReturn;

    qualifiedFunds: HedgeFundScoringFund[];

    private scoringId: number;
    busy: Subscription;
    scoring = new HedgeFundScoring;

    private breadcrumbParams: string;
    private searchParams = new HedgeFundScoringSearchParams();

    private moduleAccessChecker: ModuleAccessCheckerService;

    constructor(
        private route: ActivatedRoute,
        private scoringService: HedgeFundScoringService,
        private screeningService: HedgeFundScreeningService,
        private router: Router
    ){
        super(router);
        this.moduleAccessChecker = new ModuleAccessCheckerService;

        Observable.forkJoin(
            // Load lookups
            this.screeningService.getALlScreenings()
            )
            .subscribe(
                ([data]) => {

                    this.screenings = data;

                    this.sub = this.route
                        .params
                        .subscribe(params => {
                            this.scoringId +params['id'];
                            this.breadcrumbParams = params['params'];
                            if(this.scoringId > 0) {
                                this.busy = this.scoringService.get(this.scoringId)
                                    .subscribe(
                                        entity => {
                                            this.scoring = entity;
                                            //console.log(this.screening);
                                        },
                                        (error: ErrorResponse) => {
                                            this.errorMessage = "Error loading screening";
                                            if(error && !error.isEmpty()){
                                                this.processErrorMessage(error);
                                            }
                                            this.postAction(null, this.errorMessage);
                                        }
                                    );
                            }else{
                            }
                        });

                },
                (error) => {
                    this.errorMessage = "Error loading screenings list.";
                    this.successMessage = null;
                });




    }


    ngOnInit(): any {

        this.postAction(null, null);

        // TODO: exclude jQuery
        // datetimepicker
        $('#scoringDateDiv').datetimepicker({
            defaultDate: new Date(),
            format: 'DD-MM-YYYY'
        });
    }

    selectScreening(id){
        this.selectedScreeningId = id;
        this.busy = this.screeningService.findAllFilteredResults(id)
            .subscribe(
                result  => {
                    //console.log(result);
                    this.filters = result;
                },
                error =>  {
                    this.postAction(null, "Failed to load screening filtered results");
                }
            );
    }

    selectFilter(id){
        this.selectedFilterId = id;
        this.scoring = new HedgeFundScoring();

        var filter = null;
        for(var i = 0; i < this.filters.length; i++){
            if(this.filters[i].id == id){
                filter = this.filters[i];
            }
        }

        this.lookbackAUMList = [];
        for(var i = 0; i <= filter.lookbackAUM; i++){
            this.lookbackAUMList.push(i);
        }

        this.lookbackReturnList = [];
        for(var i = 0; i <= filter.lookbackReturns; i++){
            this.lookbackReturnList.push(i);
        }

    }

    loadFunds(){
        var params = new HedgeFundScoringFundParams();
        params.filteredResultId = this.selectedFilterId;
        params.lookbackAUM = this.selectedLookbackAUM;
        params.lookbackReturn = this.selectedLookbackReturn;

        this.busy = this.scoringService.getCalculatedScoring(params)
            .subscribe(
                result => {
                    //console.log(result);
                    this.qualifiedFunds = result;

                },
                error => {
                    this.errorMessage = "Failed to load fund scoring results";
                    this.successMessage = null;
                }
            );
    }

    save() {
        this.scoring.date = $('#scoringDate').val();

        this.busy = this.scoringService.save(this.scoring)
            .subscribe(
                response => {
                    this.scoring.id = response.entityId;
                    this.scoringId = this.scoring.id;

                    this.postAction("Successfully saved scoring.", null);
                },
                (error: ErrorResponse) => {
                    this.errorMessage = "Error saving scoring";
                    if(error && !error.isEmpty()){
                        this.processErrorMessage(error);
                    }
                    this.postAction(null, this.errorMessage);
                }
            );
    }

    getFilterShort(filter: HedgeFundScreeningFilteredResult){
        return super.getAmountShort(filter.fundAUM) + " - " + super.getAmountShort(filter.managerAUM) + " - " +
                filter.trackRecord + " - " + filter.lookbackAUM + " - " + filter.lookbackReturns;
    }


    public canEdit() {
        return this.moduleAccessChecker.checkAccessHedgeFundsEditor();
    }

}