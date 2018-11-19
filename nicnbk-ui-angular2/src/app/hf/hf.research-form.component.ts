import {CommonFormViewComponent} from "../common/common.component";
import { Component } from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {ErrorResponse} from "../common/error-response";
import {CommonFormViewComponent} from "../common/common.component";
import {ModuleAccessCheckerService} from "../authentication/module.access.checker.service";
import {Subscription} from 'rxjs';
//import {HFResearchService} from "./hf.research.service";
import {HFResearch} from "./model/hf.research-form"
import {HFManagerService} from "./hf.manager.service";
import {HFManager} from "./model/hf.manager";
import {HFResearchPage} from "./model/hf.research-page";

@Component({
    selector: 'hf-research-form',
    templateUrl: 'view/hf.research-form.component.html',
    styleUrls: [
        //'../../../public/css/...',
        '../../../node_modules/angular2-busy/build/style/busy.css'
    ],
    providers: []
})

export class HFResearchFormComponent extends CommonFormViewComponent {

    private breadcrumbParams: string;
    private moduleAccessChecker: ModuleAccessCheckerService;
    successMessage: string;
    errorMessage: string;
    public sub: any;
    busy: Subscription;
    public managerIdParam: number;

    researchForm = new HFResearch;

    constructor(
        private service: HFManagerService,
        private route: ActivatedRoute,
        private router: Router
    ){
        super(router);

        this.researchForm.manager = new HFManager();
        this.moduleAccessChecker = new ModuleAccessCheckerService;


        if(!this.moduleAccessChecker.checkAccessHedgeFunds()){
            this.router.navigate(['accessDenied']);
        }

        this.sub = this.route
            .params
            .subscribe(params => {
                    this.managerIdParam = +params['id'];
                    console.log(params);
                    if(this.managerIdParam > 0){
                        this.busy = this.service.getResearch(this.managerIdParam)
                            .subscribe(
                                data => {
                                    this.researchForm = data;
                                    this.onNumberChange(this.researchForm);
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

    save(){

        if(this.researchForm.allocationSize) {
            this.researchForm.allocationSize = Number(this.researchForm.allocationSize.toString().replace(/,/g, ''));
        }

        console.log(this.researchForm.allocationSize);

        this.service.researchSave(this.researchForm)
            .subscribe(
                response => {
                    this.researchForm.id = response.entityId;
                    this.researchForm.creationDate = response.creationDate;
                },
                (error: ErrorResponse) => {
                    this.errorMessage = "Error saving memo";
                    if(error && !error.isEmpty()){
                        this.processErrorMessage(error);
                    }
                    this.postAction(null, null);
                }
            );
    }

    navigate(managerId, researchId){
        //this.searchParams.path = '/test';
        //let params = JSON.stringify(this.searchParams);
        //console.log(this.searchParams);
        this.router.navigate(['/hf/research/edit/page/', managerId, researchId]);
    }


    public canEdit(){
        return this.moduleAccessChecker.checkAccessHedgeFundsEditor();
    }

    public onNumberChange(record){
        if(record.allocationSize != null && record.allocationSize != 'undefined' && record.allocationSize.toString().length > 0) {
            if(record.allocationSize.toString()[record.allocationSize.toString().length - 1] != '.' || record.allocationSize.toString().split('.').length > 2){
                record.allocationSize = record.allocationSize.toString().replace(/,/g , '');
                record.allocationSize = parseFloat(record.allocationSize).toLocaleString('en', {maximumFractionDigits: 2});
            }
        }
    }

}