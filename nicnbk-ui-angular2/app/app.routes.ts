import { provideRouter, RouterConfig }  from '@angular/router';

import {NewsListComponent} from './news/news-list.component';
import {NewsEditComponent} from "./news/news-edit.component";
import {NewsViewComponent} from "./news/news-view.component";

import {M2S2ListComponent} from "./m2s2/m2s2-list.component";
import {PrivateEquityMemoEditComponent} from "./m2s2/pe-memo-edit.component";
import {HedgeFundsMemoEditComponent} from "./m2s2/hf-memo-edit.component";
import {RealEstateMemoEditComponent} from "./m2s2/re-memo-edit.component";
import {GeneralMemoEditComponent} from "./m2s2/general-memo-edit.component";

const routes: RouterConfig = [
    {
        path: '',
        redirectTo: '/news/list',
        pathMatch: 'full'
    },
    {
        path: 'news/list',
        component: NewsListComponent
    },
    {
        path: 'news/edit',
        component: NewsEditComponent
    },
    {
        path: 'news/view/:id',
        component: NewsViewComponent
    },
    {
        path: 'm2s2/list',
        component: M2S2ListComponent
    },
    {
        path: 'm2s2/pe/edit/:id',
        component: PrivateEquityMemoEditComponent
    },
    {
        path: 'm2s2/hf/edit/:id',
        component: HedgeFundsMemoEditComponent
    },
    {
        path: 'm2s2/re/edit/:id',
        component: RealEstateMemoEditComponent
    },
    {
        path: 'm2s2/general/edit/:id',
        component: GeneralMemoEditComponent
    }

];

export const appRouterProviders = [
    provideRouter(routes)
];
