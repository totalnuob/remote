import {CommonService} from "../common/common.service";
import {Injectable} from "@angular/core";
import {DATA_APP_URL} from "../common/common.service.constants";
import { Observable }     from 'rxjs/Observable';
import {Http} from "@angular/http";

@Injectable()
export class LegalService extends CommonService {
    private LEGAL_UPDATE_BASE_URL = DATA_APP_URL + "legal/updates/";
    private LEGAL_UPDATES_LIST_URL  = this.LEGAL_UPDATE_BASE_URL + "load/";
    private LEGAL_UPDATES_LOAD_MORE_URL  = this.LEGAL_UPDATE_BASE_URL + "load/";
    private LEGAL_UPDATES_GET_URL = this.LEGAL_UPDATE_BASE_URL + "get/";
    private LEGAL_UPDATES_SAVE_URL = this.LEGAL_UPDATE_BASE_URL + "save/";

    constructor (
        private http: Http)
    {
        super();
    }

    loadUpdates(): Observable<any[]> {
        return this.http.get(this.LEGAL_UPDATES_LIST_URL, this.getOptionsWithCredentials())
            .map(this.extractDataList)
            .catch(this.handleErrorResponse);
    }

    loadMoreLegalUpdates(pageSize, page){
        return this.http.get(this.LEGAL_UPDATES_LOAD_MORE_URL + pageSize + "/" + page, this.getOptionsWithCredentials())
            .map(this.extractDataList)
            .catch(this.handleError);
    }

    getLegalUpdateById(id): Observable<any> {
        return this.http.get(this.LEGAL_UPDATES_GET_URL + id, this.getOptionsWithCredentials())
            .map(this.extractDataList)
            .catch(this.handleErrorResponse);
    }

    save(entity){
        let body = JSON.stringify(entity);

        //console.log(body);
        return this.http.post(this.LEGAL_UPDATES_SAVE_URL, body, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

}