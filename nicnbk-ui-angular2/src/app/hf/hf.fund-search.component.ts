import { Component } from '@angular/core';
import {HedgeFundService} from "./hf.fund.service";
import {HedgeFund} from "./model/hf.fund";
import {CommonFormViewComponent} from "../common/common.component";
import {HedgeFundSearchParams} from "./model/hf.search-params";
import {HedgeFundSearchResults} from "./model/fund-search-results";
import {ActivatedRoute, Router} from '@angular/router';
import {Subscription} from 'rxjs';

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
    public sub: any;
    busy: Subscription;

    constructor(
        private fundService: HedgeFundService,
        private route: ActivatedRoute,
        private router: Router
    ){
        super();

        this.sub = this.route
            .params
            .subscribe(params => {
                if (params['params'] != null) {
                    this.searchParams = JSON.parse(params['params']);
                    this.busy = this.fundService.search(this.searchParams)
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
                error =>  {
                    this.errorMessage = "Failed to search funds"
                }
            );
    }

    navigate(path, id) {
        let params = JSON.stringify(this.searchParams);
        this.router.navigate([path, id, { params }]);
    }

}