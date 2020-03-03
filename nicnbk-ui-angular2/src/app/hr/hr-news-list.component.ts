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
import {LegalService} from "../legal/legal.service";
import {NEWS_PAGE_SIZE} from "./hr-news.constants";


declare var $: any

@Component({
    selector: 'hr-news-list',
    templateUrl: 'view/hr-news-list.component.html',
    styleUrls: [
    ],
    providers: [AuthenticationService],
    changeDetection: ChangeDetectionStrategy.Default // TODO: change to OnPush ??
})
export class HRNewsListComponent extends CommonFormViewComponent implements OnInit{
    activeTab = "NEWS_HR";

    busy: Subscription;
    busyLegalUpdateModal: Subscription;

    newsList:  any[];
    selectedNews = {};
    currentPage = 0;
    moreNewsExist = true;

    legalUpdatesList: any[];
    //selectedLegalUpdates = {};
    currentPageLegalUpdates = 0;
    moreLegalUpdatesExist = true;

    id: number;
    private moduleAccessChecker: ModuleAccessCheckerService;
    private sub: any;

    constructor(
        private hrService: HRService,
        private legalService: LegalService,
        private route: ActivatedRoute,
        private router: Router,
        private location: Location
    ){
        super(router);
        this.moduleAccessChecker = new ModuleAccessCheckerService;
    }

    ngOnInit(){
        // load news
        this.loadHRNews();
        this.loadLegalUpdates();

        this.sub = this.route
            .params
            .subscribe( params => {
                    if(params['hrNews'] != null) {
                        try {
                            this.activeTab = "NEWS_HR";
                            this.id = JSON.parse(params['hrNews']);
                            this.getNewsById(this.id);
                            $('#newsModal').modal('show');
                        } catch(error) {
                            return;
                        }
                    }else if(params['legalUpdate'] != null) {
                        try {
                            this.activeTab = "LEGAL_UPDATES";
                            this.id = JSON.parse(params['legalUpdate']);
                            this.getLegalUpdateById(this.id);
                            //$('#legalUpdatesModal').modal('show');
                            $('#newsModal').modal('show');
                        } catch(error) {
                            return;
                        }
                    }
                    if (params['params'] != null) {
                        var parsedParams = JSON.parse(params['params']);
                        this.activeTab = parsedParams['activeTab'];
                    }
                }
            )
    }

    canEdit(){
        return this.moduleAccessChecker.checkAccessHREditor();
    }

    canEditLegalUpdates(){
       return this.moduleAccessChecker.checkAccessLegalEditor();
    }

    loadHRNews(){
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

    loadLegalUpdates(){
        this.busy = this.legalService.loadUpdates()
            .subscribe(
                legalUpdatesList => {
                    this.legalUpdatesList = legalUpdatesList;
                },
                (error: ErrorResponse) => {
                    this.successMessage = null;
                    this.errorMessage = "Error loading legal updates";
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

    isShowMoreButtonLegalUpdates(){
        return this.moreLegalUpdatesExist;
    }

    getNewsById(id){

        this.selectedNews = null;
        this.location.go('hr/news;hrNews=' + id);

        this.hrService.getNewsById(id)
            .subscribe(
                newsItem => {
                    this.selectedNews = newsItem;
                }
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

    getLegalUpdateById(id){

        this.selectedNews = null;
        this.location.go('hr/news;legalUpdate=' + id);

        this.busyLegalUpdateModal = this.legalService.getLegalUpdateById(id)
            .subscribe(
                newsItem => {
                    this.selectedNews = newsItem;
                    if(this.selectedNews.content != null){
                        this.selectedNews.content = this.selectedNews.content.split("\<a href").join("\<a target=\"_blank\" href");
                    }
                }
                (error: ErrorResponse) => {
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

    loadMoreLegalUpdates(){
        var nextPage = this.currentPageLegalUpdates + 1;
        var pageSize = 5;
        this.busy = this.legalService.loadMoreLegalUpdates(pageSize, nextPage)
            .subscribe(
                loadedNews => {
                    this.currentPageLegalUpdates = nextPage;
                    Array.prototype.push.apply(this.legalUpdatesList, loadedNews);
                    if(loadedNews.length < NEWS_PAGE_SIZE){
                        this.moreLegalUpdatesExist = false;
                    }
                },
                error => this.errorMessage = <any>error
            );
    }

    closeNewsModal(){
        this.selectedNews = null;
    }

    closeLegalUpdatesModal(){
        this.selectedNews = null;
    }

}