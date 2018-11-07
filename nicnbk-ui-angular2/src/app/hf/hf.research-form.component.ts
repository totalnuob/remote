import {CommonFormViewComponent} from "../common/common.component";
import { Component } from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {ErrorResponse} from "../common/error-response";
import {CommonFormViewComponent} from "../common/common.component";
import {Subscription} from 'rxjs';
import {ModuleAccessCheckerService} from "../authentication/module.access.checker.service";
import {HFResearchService} from "./hf.research.service";
import {HFResearch} from "./model/hf.research-form"

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

    private moduleAccessChecker: ModuleAccessCheckerService;
    successMessage: string;
    errorMessage: string;
    public sub: any;
    busy: Subscription;

    researchForm = new HFResearch;

    constructor(
        private researchService: HFResearchService,
        private route: ActivatedRoute,
        private router: Router
    ){
        super(router);

        this.moduleAccessChecker = new ModuleAccessCheckerService;

        if(!this.moduleAccessChecker.checkAccessHedgeFunds()){
            this.router.navigate(['accessDenied']);
        }


    }
}