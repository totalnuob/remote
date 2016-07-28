import { Injectable } from '@angular/core';
import { Http, Response } from '@angular/http';

import { News } from './news';
import { NEWS } from './mock-news';
import {NEWS_MORE} from "./mock-news";
import {NEWS_VIEW} from "./mock-news";

import { Observable }     from 'rxjs/Observable';
import {NEWS_PAGE_SIZE} from "./news.constants";

@Injectable()
export class NewsService {

    constructor (private http: Http) {}

    private newsListURL  = 'http://localhost:8089/news/load';
    private newsGetBaseURL = "http://localhost:8089/news/get/";

    private newsLoadMoreBaseURL = "http://localhost:8089/news/load/" + NEWS_PAGE_SIZE + "/";

    loadNews(): Observable<News[]> {
        //return Promise.resolve(NEWS);

        return this.http.get(this.newsListURL)
            .map(this.extractData)
            .catch(this.handleError);
    }

    loadMore(category, page){
        //return Promise.resolve(NEWS_MORE);
        //return NEWS_MORE;
        //alert(page);
        return this.http.get(this.newsLoadMoreBaseURL + category + "/" + page)
            .map(this.extractData)
            .catch(this.handleError);
    }

    getNewsById(id): Observable<News> {
        //alert(id);
        //return Promise.resolve(NEWS_VIEW);

        return this.http.get(this.newsGetBaseURL + id)
            .map(this.extractData)
            .catch(this.handleError);
    }

    private extractData(res: Response) {
        let body = res.json();
        return body || [];
    }

    private handleError (error: any) {
        // In a real world app, we might use a remote logging infrastructure
        // We'd also dig deeper into the error to get a better message
        let errMsg = (error.message) ? error.message :
            error.status ? `${error.status} - ${error.statusText}` : 'Server error';
        //console.error(errMsg); // log to console instead
        return Observable.throw(errMsg);
    }
}
