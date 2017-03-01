import {Component, OnInit} from '@angular/core';
import {PESearchParams} from "./model/pe.search-params";
import {PEFirmService} from "./pe.firm.service";
import {LookupService} from "../common/lookup.service";
import {PEFirm} from "./model/pe.firm";
import {CommonFormViewComponent} from "../common/common.component";
import {PESearchResults} from "./model/pe.search-results";
import {Router, ActivatedRoute} from '@angular/router';
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
    public sub: any;

    constructor(
        private firmService: PEFirmService,
        private router: Router,
        private route: ActivatedRoute
    ){
        super();
        this.sub = this.route
            .params
            .subscribe(params => {
                console.log(params['params'] != null);
                if(params['params'] != null){
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

    navigate(path, firmId){
        let params = JSON.stringify(this.searchParams);
        this.router.navigate([path, firmId, { params }]);
    }


}