import { Component } from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {HedgeFundService} from "./hf.fund.service";
import {HedgeFund} from "./model/hf.fund";
import {CommonFormViewComponent} from "../common/common.component";

import {Subscription} from 'rxjs';
import {HFManager} from "./model/hf.manager";
import {HFManagerSearchParams} from "./model/hf.manager-search-params";
import {HFManagerSearchResults} from "./model/hf.manager-search-results";
import {HFManagerService} from "./hf.manager.service";
import {ModuleAccessCheckerService} from "../authentication/module.access.checker.service";
import {ErrorResponse} from "../common/error-response";

@Component({
    selector: 'hf-fund-search',
    templateUrl: 'view/hf.manager-search.component.html',
    styleUrls: [
        //'../../../public/css/...',
        '../../../node_modules/angular2-busy/build/style/busy.css'
    ],
    providers: []
})
export class HFManagerSearchComponent extends CommonFormViewComponent{

    foundEntities: HFManager[];
    searchParams = new HFManagerSearchParams;
    searchResult = new HFManagerSearchResults();
    public sub: any;
    busy: Subscription;

    private moduleAccessChecker: ModuleAccessCheckerService;

    constructor(
        private managerService: HFManagerService,
        private route: ActivatedRoute,
        private router: Router
    ){
        super();

        this.moduleAccessChecker = new ModuleAccessCheckerService;

        if(!this.moduleAccessChecker.checkAccessHedgeFunds()){
            this.router.navigate(['accessDenied']);
        }

        this.searchParams.name = '';
        this.search(0);

        this.sub = this.route
            .params
            .subscribe(params => {
                if (params['params'] != null && JSON.parse(params['params'])['path'] == null) {
                    this.searchParams = JSON.parse(params['params']);
                    this.busy = this.managerService.search(this.searchParams)
                        .subscribe(
                            searchResult  => {
                                //console.log(searchResult);
                                this.foundEntities = searchResult.managers;
                                this.searchResult = searchResult;
                            },
                            error =>  {
                                this.errorMessage = "Failed to search funds"
                            }
                        );
                } else {
                    this.searchParams.name = '';
                    this.search(0);
                }
        });
    }

    search(page){
        //alert(this.name);

        // TODO: as parameter?
        this.searchParams.pageSize = 10;
        this.searchParams.page = page;

        this.busy = this.managerService.search(this.searchParams)
            .subscribe(
                searchResult  => {
                    //console.log(searchResult);
                    this.foundEntities = searchResult.managers;
                    this.searchResult = searchResult;
                },
                (error: ErrorResponse) => {
                    this.errorMessage = "Error searching managers";
                    if(error && !error.isEmpty()){
                        this.processErrorMessage(error);
                    }
                    this.postAction(null, null);
                }
            );
    }
    navigate(path, id) {
        let params = JSON.stringify(this.searchParams);
        this.router.navigate([path, id, { params }]);
    }
}