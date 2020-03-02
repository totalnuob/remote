import { Component, OnInit, ViewChild, ChangeDetectionStrategy } from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Location} from '@angular/common';
import {News} from "./model/news";
import {NEWS} from "./model/mock-news";
import {NewsService} from "./news.service";
import {NEWS_PAGE_SIZE} from "./news.constants";
import {AuthenticationService} from "../authentication/authentication.service";

import {Subscription} from 'rxjs';
import {ModuleAccessCheckerService} from "../authentication/module.access.checker.service";
import {ErrorResponse} from "../common/error-response";
import {CommonFormViewComponent} from "../common/common.component";


declare var $: any

@Component({
    selector: 'news-list',
    templateUrl: 'view/news-list.component.html',
    styleUrls: [
    ],
    providers: [AuthenticationService],
    changeDetection: ChangeDetectionStrategy.Default // TODO: change to OnPush ??
})
export class NewsListComponent extends CommonFormViewComponent implements OnInit{
    activeTab = "NEWS_GENERAL";

    busy: Subscription;
    newsList:  News[];
    selectedNews = new News;
    pageMap = {};

    errorMessage: string;

    showMoreButtonMap = {};

    alternativeInvestmentTypes = ['PE', 'HF', 'RE', 'SWF', 'RM'];

    id: number;

    private moduleAccessChecker: ModuleAccessCheckerService;
    private sub: any;

    constructor(
      private newsService: NewsService,
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

        this.sub = this.route
            .params
            .subscribe( params => {
                    if(params['params'] != null) {
                        try {
                            this.id = JSON.parse(params['params']);
                            this.getNewsById(this.id);
                            $('#newsModal').modal('show');
                        } catch(error) {
                            return;
                        }
                    }
                }
            )
    }

    getPage(category){
        if(category in this.pageMap){
            var  page = this.pageMap[category];
            this.pageMap[category] = this.pageMap[category] + 1;
            return page;
        }else{
            this.pageMap[category] = 2;
            return 1;
        }
    }

    loadMore(category){
        //alert(category);

        // TODO: loading icon
        var categoryWithNews;
        if(category === 'ALTERNATIVE_INVESTMENTS'){
            for(var i = 0; i < this.alternativeInvestmentTypes.length; i++){
                var type = this.alternativeInvestmentTypes[i];
                if(type in this.showMoreButtonMap){
                    if(this.showMoreButtonMap[type]){
                        categoryWithNews = type;
                        break;
                    }
                }else{
                    this.showMoreButtonMap[type] = true;
                    categoryWithNews = type;
                    break;
                }
            }
        }else{
            categoryWithNews = category;
        }
        console.log(categoryWithNews);
        this.busy = this.newsService.loadMore(categoryWithNews, this.getPage(categoryWithNews))
            .subscribe(
                loadedNews => {
                    Array.prototype.push.apply(this.newsList,loadedNews);
                    if(loadedNews.length < NEWS_PAGE_SIZE){
                        this.showMoreButtonMap[category] = false;
                    }
                },
                error => this.errorMessage = <any>error
            );
        //let loadedNews = this.newsService.loadMore(category, this.getPage(category));
        //for(let i = 0; i < loadedNews.length; i++){
        //    this.newsList.push(loadedNews[i]);
        //}
    }

    isShowMoreButton(category){
        if(category === 'ALTERNATIVE_INVESTMENTS'){
            for(var i = 0; i < this.alternativeInvestmentTypes.length; i++){
                var type = this.alternativeInvestmentTypes[i];
                if(type in this.showMoreButtonMap){
                    if(this.showMoreButtonMap[category]){
                        return true;
                    }
                }else{
                    this.showMoreButtonMap[category] = true;
                    return true;
                }
            }
            return false;
        }else if(category in this.showMoreButtonMap){
            return this.showMoreButtonMap[category];
        }else{
            this.showMoreButtonMap[category] = true;
            return true;
        }
    }

    loadNews(){

        this.busy = this.newsService.loadNews()
            .subscribe(
                newsList => {
                    this.newsList = newsList;
                },
                (error: ErrorResponse) => {
                    this.successMessage = null;
                    this.errorMessage = "Error loading news";
                    if(error && !error.isEmpty()){
                        this.processErrorMessage(error);
                    }
                    this.postAction(null, null);
                }
            );
    }

    getNewsById(id){

        this.selectedNews = null;
        this.location.go('/news;params=' + id);

        this.newsService.getNewsById(id)
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

    closeNewsModal(){
        this.selectedNews = null;
    }

    public canEdit(){
        return this.moduleAccessChecker.checkAccessNewsEdit();
    }

    isAlternativeInvestments(type){
        return type === 'PE' || type === 'HF' || type === 'RE' || type === 'SWF' || type === 'RM';
    }
}