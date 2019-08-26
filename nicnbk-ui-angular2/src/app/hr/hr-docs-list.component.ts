import { Component, OnInit, ViewChild, ChangeDetectionStrategy } from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Location} from '@angular/common';
import {AuthenticationService} from "../authentication/authentication.service";

//import '../../../public/js/star-rating/star-rating.min.js';
import {Subscription} from 'rxjs';
import {ModuleAccessCheckerService} from "../authentication/module.access.checker.service";
import {ErrorResponse} from "../common/error-response";
import {CommonFormViewComponent} from "../common/common.component";
import {HRNewsService} from "./hr-news.service";


declare var $: any

@Component({
    selector: 'hr-docs-list',
    templateUrl: 'view/hr-docs-list.component.html',
    styleUrls: [
        //'../../../public/css/star-rating/star-rating.min.css',
        //'../../../public/css/star-rating/theme-krajee-fa.min.css',
        //'../../../public/css/star-rating/theme-krajee-svg.min.css',
        //'../../../public/css/star-rating/theme-krajee-uni.min.css',
    ],
    providers: [AuthenticationService],
    changeDetection: ChangeDetectionStrategy.Default // TODO: change to OnPush ??
})
export class HRDocsListComponent extends CommonFormViewComponent implements OnInit{
    busy: Subscription;
    newsList:  any[];
    selectedNews = {};
    pageMap = {};

    errorMessage: string;

    showMoreButtonMap = {};

    alternativeInvestmentTypes = ['PE', 'HF', 'RE', 'SWF', 'RM'];

    id: number;

    private moduleAccessChecker: ModuleAccessCheckerService;
    private sub: any;

    constructor(
        private hrNewsService: HRNewsService,
        private route: ActivatedRoute,
        private router: Router,
        private location: Location
    ){
        super(router);
        this.moduleAccessChecker = new ModuleAccessCheckerService;

    }

    ngOnInit(){

        // load news
        this.loadNews();
    }


    canEdit(){
        return false;
    }
    loadNews(){

        //this.busy = this.hrNewsService.loadNews()
        //    .subscribe(
        //        newsList => {
        //            this.newsList = newsList;
        //        },
        //        (error: ErrorResponse) => {
        //            this.successMessage = null;
        //            this.errorMessage = "Error loading news";
        //            if(error && !error.isEmpty()){
        //                this.processErrorMessage(error);
        //            }
        //            this.postAction(null, null);
        //        }
        //    );
    }

}