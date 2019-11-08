import {Injectable} from "@angular/core";
import {CommonService} from "./common.service";
import {Http} from "@angular/http";
import {DATA_APP_URL} from "./common.service.constants";

@Injectable()
export class VersionService extends CommonService {

    private VERSION_BASE_URL = DATA_APP_URL + "version/";

    private VERSION_GET_LATEST_URL = this.VERSION_BASE_URL + "getLatest/";

    constructor(private http: Http){
        super();
    }

    getVersion() {
        return this.http.get(this.VERSION_GET_LATEST_URL, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }








}