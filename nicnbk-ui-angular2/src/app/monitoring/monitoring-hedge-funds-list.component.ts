import { Component } from '@angular/core';
import {HFManagerService} from "../hf/hf.manager.service"
import {ActivatedRoute, Router} from '@angular/router';
import {HFManager} from "../hf/model/hf.manager";
import {HFManagerSearchResults} from "../hf/model/hf.manager-search-results";
import {ErrorResponse} from "../common/error-response";
import {CommonFormViewComponent} from "../common/common.component";
import {Subscription} from 'rxjs';
import {ModuleAccessCheckerService} from "../authentication/module.access.checker.service";
import {HFManagerSearchParams} from "../hf/model/hf.manager-search-params";

@Component({
    selector: 'monitoring-hf-list',
    templateUrl: 'view/monitoring-hedge-funds-list.component.html',
    styleUrls: [
        //'../../../public/css/...',
        '../../../node_modules/angular2-busy/build/style/busy.css'
    ],
    providers: []
})

export class MonitoringHFListComponent extends CommonFormViewComponent {

    foundEntities: HFManager[];
    successMessage: string;
    errorMessage: string;
    public sub: any;
    busy: Subscription;

    constructor(
        private managerService: HFManagerService,
        private route: ActivatedRoute,
        private router: Router
    ){
        super(router);

        this.search();

    }

    search(){
        this.busy = this.managerService.findInvestedFunds()
            .subscribe(
                searchResult => {
                    this.foundEntities = searchResult;
                    console.log(this.foundEntities);
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
                return "$ "+value;
            }
        }
    }

}