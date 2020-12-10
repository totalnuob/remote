import { Injectable } from '@angular/core';
import { Http, Response, Headers, RequestOptions } from '@angular/http';
import {Observable} from "rxjs/Observable";

import {DATA_APP_URL} from "../common/common.service.constants";
import {CommonService} from "../common/common.service";

@Injectable()
export class NotificationService extends CommonService{

private NOTIFICATION_SERVICE_BASE_URL = DATA_APP_URL + "notification/";
private USER_NOTIFICATIONS_URL = this.NOTIFICATION_SERVICE_BASE_URL + "getUserNotifications/";
private CLOSE_USER_NOTIFICATIONS_URL = this.NOTIFICATION_SERVICE_BASE_URL + "closeUserNotification/";
private CLOSE_USER_NOTIFICATIONS_ALL_URL = this.NOTIFICATION_SERVICE_BASE_URL + "closeUserNotificationsAll/";


constructor(private http: Http){
        super();
    }

    getUserNotifications(): Observable<any[]>{
        return this.http.get(this.USER_NOTIFICATIONS_URL, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }


    closeUserNotification(id): Observable<any>{
        return this.http.post(this.CLOSE_USER_NOTIFICATIONS_URL + id, null, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    closeUserNotificationsAll(): Observable<any>{
        return this.http.post(this.CLOSE_USER_NOTIFICATIONS_ALL_URL, null, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }
}