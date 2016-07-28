import { Component } from '@angular/core';
import { ROUTER_DIRECTIVES } from '@angular/router';

import './rxjs-operators';

import {NewsService} from "./news/news.service";
import {LookupService} from "./common/lookup.service";
import {NewsListComponent} from "./news/news-list.component";
import {NewsEditComponent} from "./news/news-edit.component";
import {NewsViewComponent} from "./news/news-view.component";
import {M2S2ListComponent} from "./m2s2/m2s2-list.component";
import {GeneralMemoEditComponent} from "./m2s2/general-memo-edit.component";
import {PrivateEquityMemoEditComponent} from "./m2s2/pe-memo-edit.component";
import {HedgeFundsMemoEditComponent} from "./m2s2/hf-memo-edit.component";
import {RealEstateMemoEditComponent} from "./m2s2/re-memo-edit.component";
@Component({
    selector: 'app-main',
    templateUrl: `app/static/templates/fragments/layout.html`,
    styleUrls: [
        'app/static/css/header.css', 'app/static/css/footer.css'
    ],
    directives: [ROUTER_DIRECTIVES],
    providers: [
        NewsService,
        LookupService
    ],
    precompile: [
        NewsListComponent, NewsEditComponent, NewsViewComponent,
        M2S2ListComponent,
        GeneralMemoEditComponent, PrivateEquityMemoEditComponent, HedgeFundsMemoEditComponent, RealEstateMemoEditComponent
    ]
})
export class AppComponent {
}

