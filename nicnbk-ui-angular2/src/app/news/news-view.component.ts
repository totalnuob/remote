import {Component} from '@angular/core';
import {ActivatedRoute} from '@angular/router';

import {News} from "./model/news";
import {NewsService} from "./news.service";


@Component({
    selector: 'news-view',
    templateUrl: './view/news-view.component.html',
})
export class NewsViewComponent{
    private selectedId: number;
    private newsItem = new News;

    errorMessage: string;

    private sub: any;

    constructor(
        private route: ActivatedRoute,
        private newsService: NewsService
    ){}

    ngOnInit() {
        this.sub = this.route
            .params
            .subscribe(params => {
                this.selectedId = +params['id'];
                this.newsService.getNewsById(this.selectedId)
                    .subscribe(
                        newsItem => this.newsItem = newsItem,
                        error => this.errorMessage = <any>error
                    );
            });
    }

    ngOnDestroy() {
        this.sub.unsubscribe();
    }
}