import { Http, Response, Headers, RequestOptions } from '@angular/http';
import {Observable} from "rxjs/Observable";
import {ErrorResponse} from "./error-response";

export class CommonService{

    public extractDataList(res: Response) {
        // TODO: check res is empty
        let body = res.json();
        return body || [];
    }

    public extractData(res: Response) {
        let body = res.json();
        return body || {};
    }

    public handleErrorResponse (error: any) {
        var errorResponse = new ErrorResponse;
        if(error.message){
            //console.log(error);
            errorResponse.message = error.message;
        }
        if(error.status){
            errorResponse.status = error.status;
            errorResponse.statusText = error.statusText;
        }
        return Observable.throw(errorResponse);

    }

    public handleError(error: any){
        let errMsg = (error.message) ? error.message :
            error.status ? `${error.status} - ${error.statusText}` : 'Server error';
        return Observable.throw(errMsg);
    }

    public getOptionsWithCredentials(){
        let headers = new Headers({ 'Content-Type': 'application/json' });
        let options = new RequestOptions({headers: headers, withCredentials: true });
        return options;
    }
}