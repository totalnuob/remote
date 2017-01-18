import {Component, OnInit} from '@angular/core';
import {PESearchParams} from "./model/pe.search-params";
import {PEFirmService} from "./pe.firm.service";
import {LookupService} from "../common/lookup.service";
import {PEFirm} from "./model/pe.firm";
import {CommonFormViewComponent} from "../common/common.component";
import {PESearchResults} from "./model/pe.search-results";

import {Subscription} from 'rxjs';

@Component({
    selector: 'pe-firm-search',
    templateUrl: 'view/pe.firm-search.component.html',
    styleUrls: [],
    providers: [PEFirmService]
})
export class PEFirmSearchComponent extends CommonFormViewComponent {

    foundEntities: PEFirm[];
    searchParams = new PESearchParams();
    searchResult = new PESearchResults();
    busy: Subscription;

    constructor(
        private firmService: PEFirmService
    ){
        super();
        this.searchParams.name = '';
        this.search(0);
    }

    search(page){

        this.searchParams.pageSize = 10;

        if(page > 0) {
            this.searchParams.page = page;
        }

        console.log(this.searchParams);

        this.busy = this.firmService.search(this.searchParams)
            .subscribe(
                searchResult => {
                    this.foundEntities = searchResult.firms;
                    this.searchResult = searchResult;
                },
                error => this.errorMessage = "Failed to search"
            );
    }



}