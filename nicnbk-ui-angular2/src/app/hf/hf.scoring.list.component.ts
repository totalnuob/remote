import { Component } from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {HedgeFundService} from "./hf.fund.service";
import {CommonFormViewComponent} from "../common/common.component";
import {Subscription} from 'rxjs';
import {ModuleAccessCheckerService} from "../authentication/module.access.checker.service";
import {ErrorResponse} from "../common/error-response";
import {HedgeFundScoringService} from "./hf.fund.scoring.service";
import {HedgeFundScoring} from "./model/hf.scoring";
import {HedgeFundScoringSearchParams} from "./model/hf.scoring-search-params";
import {HedgeFundScoringSearchResults} from "./model/hf-scoring-search-results";

declare var $:any

@Component({
    selector: 'hf-fund-search',
    templateUrl: 'view/hf.scoring.list.component.html',
    styleUrls: [
        //'../../../public/css/...',
        '../../../node_modules/angular2-busy/build/style/busy.css'
    ],
    providers: []
})
export class HFScoringListComponent extends CommonFormViewComponent{


    scorings: HedgeFundScoring[];
    searchParams = new HedgeFundScoringSearchParams();
    searchResult = new HedgeFundScoringSearchResults();

    private moduleAccessChecker: ModuleAccessCheckerService;

    public sub: any;
    busy: Subscription;

    constructor(
        private scoringgService: HedgeFundScoringService,
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
                if (params['params'] != null) {
                    this.searchParams = JSON.parse(params['params']);
                    //console.log(this.searchParams);
                    this.busy = this.scoringgService.search(this.searchParams)
                        .subscribe(
                            searchResult  => {
                                //console.log(searchResult);
                                this.scorings = searchResult.scorings;
                                this.searchResult = searchResult;
                            },
                            error =>  {
                                this.errorMessage = "Failed to search scoring list"
                            }
                        );
                } else {
                    this.searchParams = new HedgeFundScoringSearchParams();
                    this.search(0);
                }
            });
    }

    ngOnInit():any {
        $('#fromDateDTPickeer').datetimepicker({
            //defaultDate: new Date(),
            format: 'DD-MM-YYYY'
        });
        $('#untilDateDTPickeer').datetimepicker({
            //defaultDate: new Date(),
            format: 'DD-MM-YYYY'
        });

    }



    search(page){

        this.searchParams.pageSize = 10;
        this.searchParams.page = page;

        this.searchParams.dateFrom = $('#dateFrom').val();
        this.searchParams.dateTo = $('#dateTo').val();

        this.busy = this.scoringgService.search(this.searchParams)
            .subscribe(
                searchResult  => {
                    //console.log(searchResult);
                    this.scorings = searchResult.screenings;
                    this.searchResult = searchResult;
                },
                (error: ErrorResponse) => {
                    this.errorMessage = "Error searching screening list";
                    if(error && !error.isEmpty()){
                        this.processErrorMessage(error);
                    }
                    this.postAction(null, null);
                }
            );
    }

    clearSearchForm(){
       this.searchParams = new HedgeFundScoringSearchParams();

    }

    navigate(screeningId){
        this.searchParams.path = '/hf/scoring';
        let params = JSON.stringify(this.searchParams);
        //console.log(this.searchParams);
        this.router.navigate(['/hf/scoring/edit/', screeningId, { params }]);
    }

}