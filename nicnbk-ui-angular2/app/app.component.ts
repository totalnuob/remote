import { Component } from '@angular/core';
import { ROUTER_DIRECTIVES } from '@angular/router';

import './rxjs-operators';

import {NewsService} from "./news/news.service";
import {LookupService} from "./common/lookup.service";
import {EmployeeService} from "./employee/employee.service";
import {NewsListComponent} from "./news/news-list.component";
import {NewsEditComponent} from "./news/news-edit.component";
import {NewsViewComponent} from "./news/news-view.component";
import {MemoListComponent} from "./m2s2/memo-list.component";
import {GeneralMemoEditComponent} from "./m2s2/general-memo-edit.component";
import {PrivateEquityMemoEditComponent} from "./m2s2/pe-memo-edit.component";
import {HedgeFundsMemoEditComponent} from "./m2s2/hf-memo-edit.component";
import {RealEstateMemoEditComponent} from "./m2s2/re-memo-edit.component";
import {MemoService} from "./m2s2/memo.service";
@Component({
    selector: 'app-main',
    templateUrl: `app/static/templates/fragments/layout.html`,
    styleUrls: [
        'app/static/css/header.css', 'app/static/css/footer.css'
    ],
    directives: [ROUTER_DIRECTIVES],
    providers: [
        MemoService,
        NewsService,
        LookupService,
        EmployeeService
    ],
    precompile: [
        NewsListComponent, NewsEditComponent, NewsViewComponent,
        MemoListComponent,
        GeneralMemoEditComponent, PrivateEquityMemoEditComponent, HedgeFundsMemoEditComponent, RealEstateMemoEditComponent
    ]
})
export class AppComponent {
}

