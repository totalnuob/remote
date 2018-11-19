import {CommonFormViewComponent} from "../common/common.component";
import { Component } from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {ErrorResponse} from "../common/error-response";
import {CommonFormViewComponent} from "../common/common.component";
import {ModuleAccessCheckerService} from "../authentication/module.access.checker.service";
import {Subscription} from 'rxjs';
//import {HFResearchService} from "./hf.research.service";
import {HFResearch} from "../hf/model/hf.research-form"
import {HFManagerService} from "../hf/hf.manager.service";
import {HFManager} from "../hf/model/hf.manager";
import {HFResearchPage} from "../hf/model/hf.research-page";

@Component({
    selector: 'monitoring-hf-research',
    templateUrl: 'view/monitoring-hedge-funds-research.component.html',
    styleUrls: [
        //'../../../public/css/...',
        '../../../node_modules/angular2-busy/build/style/busy.css'
    ],
    providers: []
})

export class MonitoringHFResearchComponent extends CommonFormViewComponent {

    successMessage: string;
    errorMessage: string;
    public sub: any;
    busy: Subscription;
    public managerIdParam: number;

    researchForm = new HFResearch;

    private breadcrumbParams: string;

    constructor(
        private service: HFManagerService,
        private route: ActivatedRoute,
        private router: Router
    ){
        super(router);
        this.researchForm.manager = new HFManager();

        this.sub = this.route
            .params
            .subscribe(params => {
                this.managerIdParam = +params['id'];
                if(this.managerIdParam > 0){
                    this.busy = this.service.getResearch(this.managerIdParam)
                        .subscribe(
                            data => {
                                this.researchForm = data;
                                console.log(this.researchForm);
                            },
                            (error: ErrorResponse) => {
                                this.errorMessage = "Error loading manager research form";
                                if(error && !error.isEmpty()){
                                    this.processErrorMessage(error);
                                }
                                this.postAction(null, null);
                            }
                        );
                } else {

                }
            });

    }

    public onNumberChange(value){
        if(value != null && value != 'undefined' && value.toString().length > 0) {
            if(value.toString()[value.toString().length - 1] != '.' || value.toString().split('.').length > 2){
                value = value.toString().replace(/,/g , '');
                value = parseFloat(value).toLocaleString('en', {maximumFractionDigits: 2});
                return "$"+value;
            }
        }
    }
}