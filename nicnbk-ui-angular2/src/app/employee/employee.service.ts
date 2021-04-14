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
    private EMPLOYEE_ACTIVE_FIND_ALL_URL = this.EMPLOYEE_BASE_URL + "findActiveAll/";

    private EMPLOYEE_FIND_IC_MEMBERS_URL = this.EMPLOYEE_BASE_URL + "findICMembers/";
    private EMPLOYEE_FIND_USERS_BY_ROLE_URL = this.EMPLOYEE_BASE_URL + "findUsersWithRole/";
    private EMPLOYEE_FIND_USERS_BY_DEPARTMENT_URL = this.EMPLOYEE_BASE_URL + "findByDepartmentAndActive/";
    private EMPLOYEE_FIND_EXECUTIVES_URL = this.EMPLOYEE_BASE_URL + "findExecutivesAndActive/";
    private EMPLOYEE_FIND_USERS_BY_DEPARTMENT_ACTIVE_WITH_EXECUTIVES_URL = this.EMPLOYEE_BASE_URL + "findByDepartmentAndActiveWithExecutives/";
    private EMPLOYEE_FIND_USERS_BY_DEPARTMENT_WITH_EXECUTIVES_URL = this.EMPLOYEE_BASE_URL + "findByDepartmentWithExecutives/";

    private EMPLOYEE_SEARCH_URL = this.EMPLOYEE_BASE_URL + "search/";
    private EMPLOYEE_GET_URL = this.EMPLOYEE_BASE_URL + "get/";
    private EMPLOYEE_SAVE_URL = this.EMPLOYEE_BASE_URL + "save/";
    private EMPLOYEE_SAVE_ADMIN_URL = this.EMPLOYEE_BASE_URL + "saveAdmin/";
    private EMPLOYEE_SAVE_AND_CHANGE_PASSWORD_URL = this.EMPLOYEE_BASE_URL + "saveAndChangePassword/";
    private EMPLOYEE_GET_BY_USERNAME_URL = this.EMPLOYEE_BASE_URL + "getByUsername/";
    private EMPLOYEE_GET_FULL_BY_USERNAME_URL = this.EMPLOYEE_BASE_URL + "getFullByUsername/";
    private EMPLOYEE_CHANGE_PASSWORD_URL = this.EMPLOYEE_BASE_URL + "changeSelfPassword/";
    private EMPLOYEE_POSITIONS_ALL_URL = this.EMPLOYEE_BASE_URL + "getAllPositions/";
    private EMPLOYEE_ROLES_ALL_URL = this.EMPLOYEE_BASE_URL + "getAllRoles/";
    private EMPLOYEE_DEPARTMENTS_ALL_URL = this.EMPLOYEE_BASE_URL + "getAllDepartments/";
    private EMPLOYEE_REGISTER_MFA_URL = this.EMPLOYEE_BASE_URL + "registerMfa/";


    findAll(): Observable<any[]> {
        return this.http.get(this.EMPLOYEE_FINDALL_URL, this.getOptionsWithCredentials())
            .map(this.extractDataList)
            .catch(this.handleErrorResponse);
    }

    findActiveAll(): Observable<any[]> {
        return this.http.get(this.EMPLOYEE_ACTIVE_FIND_ALL_URL, this.getOptionsWithCredentials())
            .map(this.extractDataList)
            .catch(this.handleErrorResponse);
    }


    findICMembers(): Observable<any[]> {
        return this.http.get(this.EMPLOYEE_FIND_IC_MEMBERS_URL, this.getOptionsWithCredentials())
            .map(this.extractDataList)
            .catch(this.handleErrorResponse);
    }

    findUsersWithRole(role): Observable<any[]> {
        return this.http.get(this.EMPLOYEE_FIND_USERS_BY_ROLE_URL + role, this.getOptionsWithCredentials())
            .map(this.extractDataList)
            .catch(this.handleErrorResponse);
    }

    findByDepartmentAndActive(departmentId): Observable<any[]> {
        return this.http.get(this.EMPLOYEE_FIND_USERS_BY_DEPARTMENT_URL + departmentId, this.getOptionsWithCredentials())
            .map(this.extractDataList)
            .catch(this.handleErrorResponse);
    }

    findExecutivesAndActive(): Observable<any[]> {
        return this.http.get(this.EMPLOYEE_FIND_EXECUTIVES_URL, this.getOptionsWithCredentials())
            .map(this.extractDataList)
            .catch(this.handleErrorResponse);
    }

    findByDepartmentAndActiveWithExecutives(departmentId): Observable<any[]> {
        return this.http.get(this.EMPLOYEE_FIND_USERS_BY_DEPARTMENT_ACTIVE_WITH_EXECUTIVES_URL + departmentId, this.getOptionsWithCredentials())
            .map(this.extractDataList)
            .catch(this.handleErrorResponse);
    }

    findByDepartmentWithExecutives(departmentId): Observable<any[]> {
        return this.http.get(this.EMPLOYEE_FIND_USERS_BY_DEPARTMENT_WITH_EXECUTIVES_URL + departmentId, this.getOptionsWithCredentials())
            .map(this.extractDataList)
            .catch(this.handleErrorResponse);
    }

    search(searchParam){
        let body = JSON.stringify(searchParam);

        //console.log(body);
        return this.http.post(this.EMPLOYEE_SEARCH_URL, body, this.getOptionsWithCredentials())
            .map(this.extractDataList)
            .catch(this.handleErrorResponse);
    }

    getEmployeeById(employeeId){
        //console.log(body);
        return this.http.get(this.EMPLOYEE_GET_URL + employeeId, this.getOptionsWithCredentials())
            .map(this.extractDataList)
            .catch(this.handleErrorResponse);
    }

    getEmployeeByUsername(username){
        //console.log(body);
        return this.http.get(this.EMPLOYEE_GET_BY_USERNAME_URL + username, this.getOptionsWithCredentials())
            .map(this.extractDataList)
            .catch(this.handleErrorResponse);
    }

    getFullEmployeeByUsername(username){
        return this.http.get(this.EMPLOYEE_GET_FULL_BY_USERNAME_URL + username, this.getOptionsWithCredentials())
            .map(this.extractDataList)
            .catch(this.handleErrorResponse);
    }

    getAllPositions(): Observable<any[]> {
        return this.http.get(this.EMPLOYEE_POSITIONS_ALL_URL, this.getOptionsWithCredentials())
            .map(this.extractDataList)
            .catch(this.handleErrorResponse);
    }

    getAllRoles(): Observable<any[]> {
        return this.http.get(this.EMPLOYEE_ROLES_ALL_URL, this.getOptionsWithCredentials())
            .map(this.extractDataList)
            .catch(this.handleErrorResponse);
    }

    getAllDepartments(): Observable<any[]> {
        return this.http.get(this.EMPLOYEE_DEPARTMENTS_ALL_URL, this.getOptionsWithCredentials())
            .map(this.extractDataList)
            .catch(this.handleErrorResponse);
    }

    changeSelfPassword(credentials): Observable<any> {
        var body = JSON.stringify(credentials);
        return this.http.post(this.EMPLOYEE_CHANGE_PASSWORD_URL, body, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleError);
    }

    save(profile): Observable<any> {
        var body = JSON.stringify(profile);
        return this.http.post(this.EMPLOYEE_SAVE_URL, body, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    saveAdmin(profile): Observable<any> {
        var body = JSON.stringify(profile);
        return this.http.post(this.EMPLOYEE_SAVE_ADMIN_URL, body, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    saveAndChangePassword(profile, password, checkbox): Observable<any> {
        var body = JSON.stringify({"employeeFullDto": profile, "password": password, "emailCheckbox": checkbox});
        // console.log(body);
        return this.http.post(this.EMPLOYEE_SAVE_AND_CHANGE_PASSWORD_URL, body, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    registerMfa(secret, otp): Observable<any> {
        var body = JSON.stringify({secret: secret, otp: otp});
        return this.http.post(this.EMPLOYEE_REGISTER_MFA_URL, body, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    //getAll() {
    //    var list = [];
    //    list.push({id: 1, lastName: "Nazym A."});
    //    list.push({id: 2, lastName: "Galym B."});
    //    list.push({id: 3, lastName: "Aliya K."});
    //    list.push({id: 4, lastName: "Tolkyn T."});
    //    list.push({id: 5, lastName: "Nadya T."});
    //    list.push({id: 6, lastName: "Timur T."});
    //    list.push({id: 7, lastName: "Aman K."});
    //    list.push({id: 8, lastName: "Dana P."});
    //    list.push({id: 9, lastName: "Nazira M."});
    //    list.push({id: 10, lastName: "Assel O."});
    //    return Promise.resolve(list);
    //}
}
