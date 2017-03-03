import { Component } from '@angular/core';
import {Router} from '@angular/router';
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
    templateUrl: 'view/hf.fund-search.component.html',
    styleUrls: [
        //'../../../public/css/...',
        '../../../node_modules/angular2-busy/build/style/busy.css'
    ],
    providers: []
})
export class HFFundSearchComponent extends CommonFormViewComponent{

    foundEntities: HedgeFund[];
    searchParams = new HedgeFundSearchParams;
    searchResult = new HedgeFundSearchResults();

    private moduleAccessChecker: ModuleAccessCheckerService;

    busy: Subscription;

    constructor(
        private fundService: HedgeFundService,
        private router: Router
    ){
        super();

        this.moduleAccessChecker = new ModuleAccessCheckerService;

        if(!this.moduleAccessChecker.checkAccessHedgeFunds()){
            this.router.navigate(['accessDenied']);
        }
        this.searchParams.name = '';
        this.search(0);
    }


    search(page){
        //alert(this.name);

        // TODO: as parameter?
        this.searchParams.pageSize = 10;

        if(page > 0) {
            this.searchParams.page = page;
        }

        this.busy = this.fundService.search(this.searchParams)
            .subscribe(
                searchResult  => {
                    //console.log(searchResult);
                    this.foundEntities = searchResult.funds;
                    this.searchResult = searchResult;
                },
                (error: ErrorResponse) => {
                    this.errorMessage = "Error searching funds";
                    if(error && !error.isEmpty()){
                        this.processErrorMessage(error);
                    }
                    this.postAction(null, null);
                }
            );
    }
}