import { Component } from '@angular/core';
import {HFManager} from "./model/hf.manager";
import {LegalEntity} from "../common/model/legal-entity";
import {HedgeFundService} from "./hf.fund.service";
import {HedgeFund} from "./model/hf.fund";
import {CommonFormViewComponent} from "../common/common.component";
import {HedgeFundSearchParams} from "./model/hf.search-params";

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

    constructor(
        private fundService: HedgeFundService
    ){
        super();
        this.searchParams.name = '';
        this.search();
    }

    search(){
        //alert(this.name);
        this.fundService.search(this.searchParams)
            .subscribe(
                searchResult  => {
                    this.foundEntities = searchResult;
                },
                error =>  this.errorMessage = "Failed to search memos."
            );
    }
}