import { Injectable } from '@angular/core';

import { Http, Response, Headers, RequestOptions } from '@angular/http';
import {DATA_APP_URL} from "../common/common.service.constants";
import {Observable} from "rxjs/Observable";
import {CommonService} from "../common/common.service";

@Injectable()
export class EmployeeService extends CommonService{
    constructor (private http: Http) {
        super();
    }

    private EMPLOYEE_BASE_URL = DATA_APP_URL + "employee/";
    private EMPLOYEE_FINDALL_URL = this.EMPLOYEE_BASE_URL + "findAll/";

    findAll(): Observable<any[]> {
        //return Promise.resolve(NEWS);

        return this.http.get(this.EMPLOYEE_FINDALL_URL)
            .map(this.extractDataList)
            .catch(this.handleError);
    }

    getAll() {
        var list = [];
        list.push({id: 1, lastName: "Nazym A."});
        list.push({id: 2, lastName: "Galym B."});
        list.push({id: 3, lastName: "Aliya K."});
        list.push({id: 4, lastName: "Tolkyn T."});
        list.push({id: 5, lastName: "Nadya T."});
        list.push({id: 6, lastName: "Timur T."});
        list.push({id: 7, lastName: "Aman K."});
        list.push({id: 8, lastName: "Dana P."});
        list.push({id: 9, lastName: "Nazira M."});
        list.push({id: 10, lastName: "Assel O."});
        return Promise.resolve(list);
    }
}
