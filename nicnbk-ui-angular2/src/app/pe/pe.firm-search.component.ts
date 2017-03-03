import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {PESearchParams} from "./model/pe.search-params";
import {PEFirmService} from "./pe.firm.service";
import {LookupService} from "../common/lookup.service";
import {PEFirm} from "./model/pe.firm";
import {CommonFormViewComponent} from "../common/common.component";
import {PESearchResults} from "./model/pe.search-results";

import {Subscription} from 'rxjs';
import {ModuleAccessCheckerService} from "../authentication/module.access.checker.service";
import {ErrorResponse} from "../common/error-response";

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

    private moduleAccessChecker: ModuleAccessCheckerService;

    constructor(
        private firmService: PEFirmService,
        private router: Router
    ){
        super();

        this.moduleAccessChecker = new ModuleAccessCheckerService;

        if(!this.moduleAccessChecker.checkAccessPrivateEquity()){
            this.router.navigate(['accessDenied']);
        }

        this.searchParams.name = '';
        this.search(0);
    }

    search(page){

        this.searchParams.pageSize = 10;

        if(page > 0) {
            this.searchParams.page = page;
        }

        //console.log(this.searchParams);

        this.busy = this.firmService.search(this.searchParams)
            .subscribe(
                searchResult => {
                    this.foundEntities = searchResult.firms;
                    this.searchResult = searchResult;
                },
                (error: ErrorResponse) => {
                    this.errorMessage = "Error searching firms";
                    if(error && !error.isEmpty()){
                        this.processErrorMessage(error);
                        console.log(error);
                    }
                    this.postAction(null, null);
                }
            );
    }



}