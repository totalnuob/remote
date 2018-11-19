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

declare var $:any

@Component({
    selector: 'hf-fund-search',
    templateUrl: 'view/hf.screening.list.component.html',
    styleUrls: [
        //'../../../public/css/...',
        '../../../node_modules/angular2-busy/build/style/busy.css'
    ],
    providers: []
})
export class HFScreeningListComponent extends CommonFormViewComponent{


    screenings: HedgeFundScreening[];
    searchParams = new HedgeFundScreeningSearchParams();
    searchResult = new HedgeFundScreeningSearchResults();

    private moduleAccessChecker: ModuleAccessCheckerService;

    public sub: any;
    busy: Subscription;

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
                if (params['params'] != null) {
                    this.searchParams = JSON.parse(params['params']);
                    //console.log(this.searchParams);
                    this.busy = this.screeningService.search(this.searchParams)
                        .subscribe(
                            searchResult  => {
                                //console.log(searchResult);
                                this.screenings = searchResult.screenings;
                                this.searchResult = searchResult;
                            },
                            error =>  {
                                this.errorMessage = "Failed to search screening list"
                            }
                        );
                } else {
                    this.searchParams = new HedgeFundScreeningSearchParams();
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

        this.busy = this.screeningService.search(this.searchParams)
            .subscribe(
                searchResult  => {
                    //console.log(searchResult);
                    this.screenings = searchResult.screenings;
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
       this.searchParams = new HedgeFundScreeningSearchParams();

    }

    navigate(screeningId){
        this.searchParams.path = '/hf/screening';
        let params = JSON.stringify(this.searchParams);
        //console.log(this.searchParams);
        this.router.navigate(['/hf/screening/edit/', screeningId, { params }]);
    }

}