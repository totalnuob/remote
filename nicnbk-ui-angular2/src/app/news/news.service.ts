import { Injectable } from '@angular/core';
import { Http, Response } from '@angular/http';

import { News } from './model/news';
import { NEWS } from './model/mock-news';
import {NEWS_MORE} from "./model/mock-news";
import {NEWS_VIEW} from "./model/mock-news";
import {DATA_APP_URL} from "../common/common.service.constants";

import { Observable }     from 'rxjs/Observable';
import {NEWS_PAGE_SIZE} from "./news.constants";
import {CommonService} from "../common/common.service";

@Injectable()
export class NewsService extends CommonService{

    constructor (private http: Http) {
        super();
    }

    private NEWS_BASE_URL = DATA_APP_URL + "news/";
    private NEWS_LIST_URL  = this.NEWS_BASE_URL + "load/";
    private NEWS_GET_BASE_URL = this.NEWS_BASE_URL + "get/";
    private NEWS_LOAD_MORE_BASE_URL = this.NEWS_BASE_URL + "load/" + NEWS_PAGE_SIZE + "/";
    private NEWS_SAVE_URL = this.NEWS_BASE_URL + "save/";

    loadNews(): Observable<News[]> {
        //return Promise.resolve(NEWS);

        return this.http.get(this.NEWS_LIST_URL, this.getOptionsWithCredentials())
            .map(this.extractDataList)
            .catch(this.handleError);
    }

    loadMore(category, page){
        //return Promise.resolve(NEWS_MORE);
        //return NEWS_MORE;
        //alert(page);
        return this.http.get(this.NEWS_LOAD_MORE_BASE_URL + category + "/" + page, this.getOptionsWithCredentials())
            .map(this.extractDataList)
            .catch(this.handleError);
    }

    getNewsById(id): Observable<News> {
        //alert(id);
        //return Promise.resolve(NEWS_VIEW);

        return this.http.get(this.NEWS_GET_BASE_URL + id, this.getOptionsWithCredentials())
            .map(this.extractDataList)
            .catch(this.handleError);
    }

    save(entity){
        let body = JSON.stringify(entity);

        //console.log(body);
        return this.http.post(this.NEWS_SAVE_URL, body, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleError);
    }

}
