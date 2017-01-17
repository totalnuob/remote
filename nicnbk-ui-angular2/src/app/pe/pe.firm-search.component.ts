import {Component, OnInit} from '@angular/core';
import {PESearchParams} from "./model/pe.search-params";
import {PeFirmService} from "./pe.firm.service";
import {LookupService} from "../common/lookup.service";
import {PeFirm} from "./model/pe.firm";
import {CommonFormViewComponent} from "../common/common.component";

@Component({
    selector: 'pe-firm-search',
    templateUrl: 'view/pe.firm-search.component.html',
    styleUrls: [],
    providers: [PeFirmService]
})
export class PeFirmSearchComponent extends CommonFormViewComponent implements OnInit{

    foundEntities: PeFirm[];
    searchParams = new PESearchParams();

    constructor(
        private firmService: PeFirmService
    ){
        super();
        this.searchParams.name = '';
        this.search();
    }

    ngOnInit():any{

    }

    search(){
        this.firmService.search(this.searchParams)
            .subscribe(
                searchResult => {
                    this.foundEntities = searchResult;
                },
                error => this.errorMessage = "Failed to search"
            );
    }



}