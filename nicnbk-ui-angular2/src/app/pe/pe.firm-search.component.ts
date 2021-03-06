import {Component, OnInit} from '@angular/core';
import {Router, ActivatedRoute} from '@angular/router';
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
    public sub: any;

    private moduleAccessChecker: ModuleAccessCheckerService;

    constructor(
        private firmService: PEFirmService,
        private router: Router,
        private route: ActivatedRoute
    ){
        super(router);

        this.moduleAccessChecker = new ModuleAccessCheckerService;

        if(!this.moduleAccessChecker.checkAccessPrivateEquity()){
            this.router.navigate(['accessDenied']);
        }

        //this.searchParams.name = '';
        //this.search(0);

        this.sub = this.route
            .params
            .subscribe(params => {
                console.log(params['params'] != null);
                if(params['params'] != null && JSON.parse(params['params'])['path'] == null){
                    this.searchParams = JSON.parse(params['params']);
                    this.busy = this.firmService.search(this.searchParams)
                        .subscribe(
                            searchResult => {
                                this.foundEntities = searchResult.firms;
                                this.searchResult = searchResult;
                            },
                            error => this.errorMessage = "Failed to search"
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

    navigate(path, firmId){
        let params = JSON.stringify(this.searchParams);
        this.router.navigate([path, firmId, { params }]);
    }


}