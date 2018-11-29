import {Lookup} from "../common/lookup";
import {Component} from '@angular/core';
import {Router} from '@angular/router';
import {ErrorResponse} from "./error-response";

declare var $:any;

@Component({
    selector: 'common-form',
    providers: []
})
export class CommonFormViewComponent {

    successMessage: string;
    errorMessage: string;

    constructor(
        private _router?: Router
    ){}

    convertToServiceModel(list){
        var dtoList = [];
        for(var i = 0; list != null && i < list.length; i++){
            var lookup = new Lookup;
            lookup.code = list[i].id != null ? list[i].id : list[i].code;
            dtoList.push(lookup);
        }
        return dtoList;
    }

    range(min, max){
        let array = [];
        for(var i = min; i <= max; i++ ){
            array.push(i);
        }
        return array;
    }

    waitSleep(milliseconds) {
        var start = new Date().getTime();
        for (var i = 0; i < 1e7; i++) {
            if ((new Date().getTime() - start) > milliseconds) {
                break;
            }
        }
    }

    postAction(successMessage, errorMessage){
        //if(successMessage && successMessage != null) {
            this.successMessage = successMessage;
        //}
        //if(errorMessage && errorMessage != null) {
            this.errorMessage = errorMessage;
        //}

        // TODO: non jQuery
        $('html, body').animate({ scrollTop: 0 }, 'fast');
    }

    processErrorResponse(errorResponse: ErrorResponse, errorMessage?: string){
        this.errorMessage = errorMessage != null ? errorMessage : "Error occurred processing your request";

        if(errorResponse != null && (errorResponse.message != null || errorResponse.messageRu != null || errorResponse.messageKz != null)){
            this.errorMessage = errorResponse.message;
        }

        if(errorResponse.status){
            if(errorResponse.status == 401){
                this.errorMessage = "Access Denied";

                alert("Access Denied. Please re-login");
                localStorage.removeItem("authenticatedUser");
                localStorage.removeItem("authenticatedUserRoles");
                //location.reload();
                this._router.navigate(['login']);
                return;
            }
        }
        this.postAction(null, this.errorMessage);
    }

    processErrorMessage(errorResponse){
        if(errorResponse.message){
            this.errorMessage = errorResponse.message;
        }else if(errorResponse.status){
            if(errorResponse.status == 401){
                this.errorMessage = "Access Denied";

                alert("Access Denied. Please re-login");
                localStorage.removeItem("authenticatedUser");
                localStorage.removeItem("authenticatedUserRoles");
                //location.reload();
                this._router.navigate(['login']);
            }
        }else {
            //this.errorMessage = "Error occurred when processing request";
        }
    }

    public getReportDateShortFormatted(reportDate){
        if(reportDate){
            var monthNum = reportDate.split("-")[1];
            var yearNum = reportDate.split("-")[2];
            if(monthNum === '01'){
                return 'JAN ' + yearNum;
            }else if(monthNum === '02'){
                return 'FEB ' + yearNum;
            }else if(monthNum === '03'){
                return 'MAR ' + yearNum;
            }else if(monthNum === '04'){
                return 'APR ' + yearNum;
            }else if(monthNum === '05'){
                return 'MAY ' + yearNum;
            }else if(monthNum === '06'){
                return 'JUN ' + yearNum;
            }else if(monthNum === '07'){
                return 'JUL ' + yearNum;
            }else if(monthNum === '08'){
                return 'AUG ' + yearNum;
            }else if(monthNum === '09'){
                return 'SEP ' + yearNum;
            }else if(monthNum === '10'){
                return 'OCT ' + yearNum;
            }else if(monthNum === '11'){
                return 'NOV ' + yearNum;
            }else if(monthNum === '12'){
                return 'DEC ' + yearNum;
            }
        }
        return "";
    }

    getAmountShort(value){
        if(value) {
            value = Number(value.toString().replace(/,/g, '')).toFixed(0);
            //value = Number(value).toFixed(0);
            if(("" + value).length > 6 && ("" + value).length <= 9 ){
                var shortValue = parseFloat(value / Math.pow(1000, 2)).toFixed(1);
                return ((shortValue + "")[(shortValue + "").length-1] === '0' ? (shortValue + "").substring(0, (shortValue + "").length-2) : shortValue) + " M";
            }else if(("" + value).length > 9 && ("" + value).length <= 12 ){
                var shortValue = parseFloat(value / Math.pow(1000, 3)).toFixed(1);
                return ((shortValue + "")[(shortValue + "").length-1] === '0' ? (shortValue + "").substring(0, (shortValue + "").length-2) : shortValue) + " B";
            }else if(("" + value).length > 12){
                var shortValue = parseFloat(value / Math.pow(1000, 4)).toFixed(1);
                return ((shortValue + "")[(shortValue + "").length-1] === '0' ? (shortValue + "").substring(0, (shortValue + "").length-2) : shortValue) + " T";
            }else{
                return value;
            }
        }else{
            return "";
        }
    }
}