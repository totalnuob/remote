import {Component, OnInit} from '@angular/core';
import {Router, ActivatedRoute} from '@angular/router';
import {LookupService} from "../common/lookup.service";

import '../../../public/js/jquery.hotkeys.js';
import '../../../public/js/bootstrap/bootstrap-wysiwyg.js';
import {ModuleAccessCheckerService} from "../authentication/module.access.checker.service";
import {CommonFormViewComponent} from "../common/common.component";
import {ErrorResponse} from "../common/error-response";
import {HRNews} from "./model/hr-news";
import {HRService} from "./hr.service";

declare var $:any

@Component({
    selector: 'news-create',
    templateUrl: './view/hr-news-edit.component.html'
})
export class HRNewsEditComponent extends CommonFormViewComponent implements OnInit{
    newsTypes = [];

    newsItem = new HRNews;

    successMessage: string;
    errorMessage: string;

    private sub: any;
    private newsIdParam: number;
    submitted = false;

    private moduleAccessChecker: ModuleAccessCheckerService;
    private breadcrumbParams: string;

    constructor(
        private lookupService: LookupService,
        private hrService: HRService,
        private router: Router,
        private route: ActivatedRoute
    ){
        super(router);
        this.moduleAccessChecker = new ModuleAccessCheckerService;

        if(!this.moduleAccessChecker.checkAccessHREditor()) {
            this.router.navigate(['accessDenied']);
        }

        this.sub = this.route
            .params
            .subscribe(params => {
                this.newsIdParam = +params['id'];
                this.breadcrumbParams = params['params'];
                if(this.newsIdParam > 0) {
                    this.hrService.getNewsById(this.newsIdParam)
                        .subscribe(
                            newsItem => {
                                this.newsItem = newsItem;
                                $('#editor').html(newsItem.content);
                            },
                            (error:ErrorResponse) => {
                                this.errorMessage = "Error loading news";
                                if (error && !error.isEmpty()) {
                                    this.processErrorMessage(error);
                                }
                                this.postAction(null, null);
                            }
                        );
                } else {

                }
            });
    }

    ngOnInit(){

    }

    save(){

        this.hrService.saveNews(this.newsItem)
            .subscribe(
                response  => {
                    this.newsItem.id = response.id;
                    this.postAction( "Successfully saved.", null)
                },

                (error: ErrorResponse) => {
                    this.successMessage = null;
                    this.errorMessage = "Error saving news";
                    if(error && !error.isEmpty()){
                        this.processErrorMessage(error);
                    }
                    this.postAction(null, null);
                }
            );
    }

}