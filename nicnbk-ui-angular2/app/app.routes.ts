import { provideRouter, RouterConfig }  from '@angular/router';

import {NewsListComponent} from './news/news-list.component';
import {NewsEditComponent} from "./news/news-edit.component";
import {NewsViewComponent} from "./news/news-view.component";

import {MemoListComponent} from "./m2s2/memo-list.component";
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
    /* NEWS ***************************************/
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

    /* M2S2 ****************************************/
    {
        path: 'm2s2/list',
        component: MemoListComponent
    },
    {
        path: 'm2s2/edit/2/:id',
        component: PrivateEquityMemoEditComponent
    },
    {
        path: 'm2s2/edit/3/:id',
        component: HedgeFundsMemoEditComponent
    },
    {
        path: 'm2s2/edit/4/:id',
        component: RealEstateMemoEditComponent
    },
    {
        path: 'm2s2/edit/1/:id',
        component: GeneralMemoEditComponent
    }

];

export const appRouterProviders = [
    provideRouter(routes)
];
