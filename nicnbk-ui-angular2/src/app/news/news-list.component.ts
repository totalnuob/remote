import { Component, OnInit, ViewChild, ChangeDetectionStrategy } from '@angular/core';
import { } from '@angular/router';
import {News} from "./model/news";
import {NEWS} from "./model/mock-news";
import {NewsService} from "./news.service";
import {NEWS_PAGE_SIZE} from "./news.constants";
import {AuthenticationService} from "../authentication/authentication.service";

//import '../../../public/js/star-rating/star-rating.min.js';

declare var $: any

@Component({
    selector: 'news-list',
    templateUrl: 'view/news-list.component.html',
    styleUrls: [
        //'../../../public/css/star-rating/star-rating.min.css',
        //'../../../public/css/star-rating/theme-krajee-fa.min.css',
        //'../../../public/css/star-rating/theme-krajee-svg.min.css',
        //'../../../public/css/star-rating/theme-krajee-uni.min.css',
    ],
    providers: [AuthenticationService],
    changeDetection: ChangeDetectionStrategy.Default // TODO: change to OnPush ??
})
export class NewsListComponent implements OnInit{
    newsList:  News[];
    selectedNews = new News;
    pageMap = {};

    errorMessage: string;

    showMoreButtonMap = {};


    constructor(
      private newsService: NewsService){
    }

    ngOnInit(){
        //TODO: exclude jQuery
        //$('.rating').rating({min:1, max:10, step:1, size:'xs', showClear: false, showCaption: false}); }, 3000);

        // load news
        this.loadNews();
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
        this.newsService.loadMore(category, this.getPage(category))
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
        if(category in this.showMoreButtonMap){
            return this.showMoreButtonMap[category];
        }else{
            this.showMoreButtonMap[category] = true;
            return true;
        }
    }

    loadNews(){

        this.newsService.loadNews()
            .subscribe(
                newsList => {
                    this.newsList = newsList;
                },
                error =>  this.errorMessage = <any>error);

    }

    getNewsById(id){
        //alert(id);
        this.newsService.getNewsById(id)
            .subscribe(
                newsItem => this.selectedNews = newsItem,
                error => this.errorMessage = <any>error
            );
    }
}