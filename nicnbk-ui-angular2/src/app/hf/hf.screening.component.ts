import { Component } from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {HedgeFundService} from "./hf.fund.service";
import {HedgeFund} from "./model/hf.fund";
import {CommonFormViewComponent} from "../common/common.component";
import {HedgeFundSearchParams} from "./model/hf.search-params";
import {HedgeFundSearchResults} from "./model/fund-search-results";
import {Subscription} from 'rxjs';
import {ModuleAccessCheckerService} from "../authentication/module.access.checker.service";
import {ErrorResponse} from "../common/error-response";

@Component({
    selector: 'hf-fund-search',
    templateUrl: 'view/hf.screening.component.html',
    styleUrls: [
        //'../../../public/css/...',
        '../../../node_modules/angular2-busy/build/style/busy.css'
    ],
    providers: []
})
export class HFScreeningComponent extends CommonFormViewComponent{


    records: any[];
    searchParams;
    searchResult;

    private moduleAccessChecker: ModuleAccessCheckerService;

    public sub: any;
    busy: Subscription;

    constructor(
        private fundService: HedgeFundService,
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
                    this.busy = this.fundService.search(this.searchParams)
                        .subscribe(
                            searchResult  => {
                                //console.log(searchResult);
                                this.records = searchResult.records;
                                this.searchResult = searchResult;
                            },
                            error =>  {
                                this.errorMessage = "Failed to search screening list"
                            }
                        );
                } else {
                    this.searchParams.name = '';
                    this.search(0);
                }
            });
    }
    search(page){
        this.searchParams.pageSize = 10;
        this.searchParams.page = page;

        this.busy = this.fundService.search(this.searchParams)
            .subscribe(
                searchResult  => {
                    //console.log(searchResult);
                    this.records = searchResult.records;
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

}