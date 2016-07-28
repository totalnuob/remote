import {Component} from '@angular/core';
import {ActivatedRoute, ROUTER_DIRECTIVES} from '@angular/router';

import {News} from "./news";
import {NewsService} from "./news.service";


@Component({
    selector: 'news-view',
    templateUrl: `app/news/news-view.component.html`,
    directives: [ROUTER_DIRECTIVES],
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