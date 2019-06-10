import { Injectable } from '@angular/core';
import { Http, Response, Headers, RequestOptions } from '@angular/http';
import { Observable }     from 'rxjs/Observable';

import {DATA_APP_URL} from "../common/common.service.constants";
import {CommonService} from "../common/common.service";
import {ModuleAccessCheckerService} from "../authentication/module.access.checker.service";
import {HedgeFundScoring} from "./model/hf.scoring";
import {HedgeFundScoringFund} from "./model/hf.scoring.fund";

@Injectable()
export class HedgeFundScoringService extends CommonService{

    private HF_BASE_URL = DATA_APP_URL + "hf/scoring/";

    private HF_SCORING_SAVE_URL = this.HF_BASE_URL + "save/";
    private HF_SCORING_GET_URL = this.HF_BASE_URL + "get/";
    private HF_SCORING_SEARCH_URL = this.HF_BASE_URL + "search/";

    private HF_SCORING_CALCULATE_URL = this.HF_BASE_URL + "calculateScoring/";


    constructor( private http: Http,
                 private moduleAccessChecker: ModuleAccessCheckerService) {
        super();
    }

    save(entity){

        let body = JSON.stringify(entity);

        return this.http.post(this.HF_SCORING_SAVE_URL, body, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    get(id): Observable<HedgeFundScoring> {
        // TODO: check id
        return this.http.get(this.HF_SCORING_GET_URL + id, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    search(searchParams){
        let body = JSON.stringify(searchParams);

        //console.log(body);
        return this.http.post(this.HF_SCORING_SEARCH_URL, body, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    getCalculatedScoring(params): Observable<HedgeFundScoringFund[]> {

        let body = JSON.stringify(params);

        return this.http.post(this.HF_SCORING_CALCULATE_URL, body, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

}