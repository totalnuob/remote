import { Component, OnInit, ViewChild, ChangeDetectionStrategy } from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Location} from '@angular/common';
import {AuthenticationService} from "../authentication/authentication.service";

//import '../../../public/js/star-rating/star-rating.min.js';
import {Subscription} from 'rxjs';
import {ModuleAccessCheckerService} from "../authentication/module.access.checker.service";
import {ErrorResponse} from "../common/error-response";
import {CommonFormViewComponent} from "../common/common.component";
import {HRService} from "./hr.service";
import {NEWS_PAGE_SIZE} from "./hr-news.constants";


declare var $: any

@Component({
    selector: 'hr-news-list',
    templateUrl: 'view/hr-news-list.component.html',
    styleUrls: [
        //'../../../public/css/star-rating/star-rating.min.css',
        //'../../../public/css/star-rating/theme-krajee-fa.min.css',
        //'../../../public/css/star-rating/theme-krajee-svg.min.css',
        //'../../../public/css/star-rating/theme-krajee-uni.min.css',
    ],
    providers: [AuthenticationService],
    changeDetection: ChangeDetectionStrategy.Default // TODO: change to OnPush ??
})
export class HRNewsListComponent extends CommonFormViewComponent implements OnInit{
    busy: Subscription;
    newsList:  any[];
    selectedNews = {};
    pageMap = {};

    currentPage = 0;
    moreNewsExist = true;

    id: number;

    private moduleAccessChecker: ModuleAccessCheckerService;
    private sub: any;

    constructor(
        private hrService: HRService,
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
        return this.moduleAccessChecker.checkAccessHREditor();
    }
    loadNews(){

        this.busy = this.hrService.loadNews()
            .subscribe(
                newsList => {
                    this.newsList = newsList;
                },
                (error: ErrorResponse) => {
                    this.successMessage = null;
                    this.errorMessage = "Error loading hr news";
                    if(error && !error.isEmpty()){
                        this.processErrorMessage(error);
                    }
                    this.postAction(null, null);
                }
            );
    }


    isShowMoreButton(){
        return this.moreNewsExist;
    }

    getNewsById(id){

        this.selectedNews = null;
        //this.location.go('/news;params=' + id);

        this.hrService.getNewsById(id)
            .subscribe(
                newsItem => this.selectedNews = newsItem,
                (error: ErrorResponse) => {
                    //this.successMessage = null;
                    //this.errorMessage = "Error loading news details";
                    this.selectedNews = null;
                    if(error && !error.isEmpty()){
                        this.processErrorMessage(error);
                    }
                    this.postAction(null, null);
                }
            );
    }

    loadMore(){
        var nextPage = this.currentPage + 1;
        var pageSize = 5;
        this.busy = this.hrService.loadMoreNews(pageSize, nextPage)
            .subscribe(
                loadedNews => {
                    this.currentPage = nextPage;
                    Array.prototype.push.apply(this.newsList,loadedNews);
                    console.log(loadedNews.length);
                    if(loadedNews.length < NEWS_PAGE_SIZE){
                        this.moreNewsExist = false;
                    }
                },
                error => this.errorMessage = <any>error
            );
    }

    closeNewsModal(){
        this.selectedNews = null;
    }

}