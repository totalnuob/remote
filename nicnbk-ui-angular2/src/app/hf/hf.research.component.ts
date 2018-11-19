import { Component } from '@angular/core';
import {HFManagerService} from "./hf.manager.service";
import {ActivatedRoute, Router} from '@angular/router';
import {HFManager} from "./model/hf.manager";
import {HFManagerSearchResults} from "./model/hf.manager-search-results";
import {ErrorResponse} from "../common/error-response";
import {CommonFormViewComponent} from "../common/common.component";
import {Subscription} from 'rxjs';
import {ModuleAccessCheckerService} from "../authentication/module.access.checker.service";
import {HFManagerSearchParams} from "./model/hf.manager-search-params";

@Component({
    selector: 'hf-research',
    templateUrl: 'view/hf.research.component.html',
    styleUrls: [
        //'../../../public/css/...',
        '../../../node_modules/angular2-busy/build/style/busy.css'
    ],
    providers: []
})

export class HFResearchComponent extends CommonFormViewComponent {

    foundEntities: HFManager[];
    successMessage: string;
    errorMessage: string;
    public sub: any;
    busy: Subscription;

    private moduleAccessChecker: ModuleAccessCheckerService;

    constructor(
        private managerService: HFManagerService,
        private route: ActivatedRoute,
        private router: Router
    ){
        super(router);

        this.moduleAccessChecker = new ModuleAccessCheckerService;

        if(!this.moduleAccessChecker.checkAccessHedgeFunds()){
            this.router.navigate(['accessDenied']);
        }

        this.search();

    }

    search(){
        this.busy = this.managerService.findInvestedFunds()
            .subscribe(
                searchResult => {
                    this.foundEntities = searchResult;
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
        this.router.navigate([path, id]);
    }

    public onNumberChange(value){
        if(value != null && value != 'undefined' && value.toString().length > 0) {
            if(value.toString()[value.toString().length - 1] != '.' || value.toString().split('.').length > 2){
                value = value.toString().replace(/,/g , '');
                value = parseFloat(value).toLocaleString('en', {maximumFractionDigits: 2});
                return "$ " + value;
            }
        }
    }
    
}