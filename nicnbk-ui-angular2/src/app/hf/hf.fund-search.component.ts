import { Component } from '@angular/core';
import {HedgeFundService} from "./hf.fund.service";
import {HedgeFund} from "./model/hf.fund";
import {CommonFormViewComponent} from "../common/common.component";
import {HedgeFundSearchParams} from "./model/hf.search-params";
import {HedgeFundSearchResults} from "./model/fund-search-results";

@Component({
    selector: 'hf-fund-search',
    templateUrl: 'view/hf.fund-search.component.html',
    styleUrls: [
        //'../../../public/css/...',
    ],
    providers: []
})
export class HFFundSearchComponent extends CommonFormViewComponent{

    foundEntities: HedgeFund[];
    searchParams = new HedgeFundSearchParams;
    searchResult = new HedgeFundSearchResults();

    constructor(
        private fundService: HedgeFundService
    ){
        super();
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

        this.fundService.search(this.searchParams)
            .subscribe(
                searchResult  => {
                    //console.log(searchResult);
                    this.foundEntities = searchResult.funds;
                    this.searchResult = searchResult;
                },
                error =>  {
                    this.errorMessage = "Failed to search funds"
                }
            );
    }
}