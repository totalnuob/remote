import {Component, OnInit} from '@angular/core';
import {Router, ActivatedRoute} from '@angular/router';
import {LookupService} from "../common/lookup.service";
import {News} from "./model/news";
import {NewsService} from "./news.service";

import '../../../public/js/jquery.hotkeys.js';
import '../../../public/js/bootstrap/bootstrap-wysiwyg.js';
import {ModuleAccessCheckerService} from "../authentication/module.access.checker.service";
import {CommonFormViewComponent} from "../common/common.component";
import {ErrorResponse} from "../common/error-response";

declare var $:any

@Component({
    selector: 'news-create',
    templateUrl: './view/news-edit.component.html'
})
export class NewsEditComponent extends CommonFormViewComponent implements OnInit{
    newsTypes = [];

    newsItem = new News;

    successMessage: string;
    errorMessage: string;

    private sub: any;
    private newsIdParam: number;
    submitted = false;

    private moduleAccessChecker: ModuleAccessCheckerService;
    private breadcrumbParams: string;

    constructor(
        private lookupService: LookupService,
        private newsService: NewsService,
        private router: Router,
        private route: ActivatedRoute
    ){
        super();
        this.moduleAccessChecker = new ModuleAccessCheckerService;

        if(!this.moduleAccessChecker.checkAccessNewsEdit()) {
            this.router.navigate(['accessDenied']);
        }

        this.sub = this.route
            .params
            .subscribe(params => {
                this.newsIdParam = +params['id'];
                this.breadcrumbParams = params['params'];
                if(this.newsIdParam > 0) {
                    this.newsService.getNewsById(this.newsIdParam)
                        .subscribe(
                            newsItem => {
                                this.newsItem = newsItem;
                                $('#editor').html(newsItem.content);
                            },
                            (error:ErrorResponse) => {
                                this.errorMessage = "Error loading news item";
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
        // load news types
        this.loadLookups();


        // TODO: remove jQuery
        // setup editor
        $('#editor').wysiwyg();
        $('#editor').cleanHtml();
        var itemId = $('#newsItemId').val();
        if(itemId === ''){
            $('#editor').html('Enter body..');
        }else{
            $('#editor').html($('#newsContent').val());
        }
    }

    save(){
        // TODO: remove jQuery
        this.newsItem.content = $("#editor").html().replace(/<script[^>]*>/gi, "&lt;script&rt;").replace(/<\/script[^>]*>/gi, "&lt;/script&rt;");

        //console.log(this.newsItem.content);

        this.newsService.save(this.newsItem)
            .subscribe(
                response  => {
                    //console.log(response);
                    this.successMessage = "Successfully saved.";
                    this.errorMessage = null;
                    this.submitted = true;

                    // TODO: rafactor?
                    $('html, body').animate({ scrollTop: 0 }, 'fast');
                },
                error =>  {
                    this.successMessage = null;
                    this.errorMessage = <any>error;
                    this.submitted = false;

                    // TODO: rafactor?
                    $('html, body').animate({ scrollTop: 0 }, 'fast');
                }
            );
    }

    loadLookups(){
        // news types
        this.lookupService.getNewsTypes().then(newsTypes => this.newsTypes = newsTypes);
    }

}