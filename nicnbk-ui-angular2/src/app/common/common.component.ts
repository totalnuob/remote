import {Lookup} from "../common/lookup";
import {Component} from '@angular/core';
import {Router} from '@angular/router';

declare var $:any;

@Component({
    selector: 'common-form',
    providers: []
})
export class CommonFormViewComponent {

    successMessage: string;
    errorMessage: string;

    constructor(
        private _router: Router
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
        //console.log(errorResponse);
    }
}