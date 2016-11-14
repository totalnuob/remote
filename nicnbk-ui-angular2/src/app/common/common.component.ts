import {Lookup} from "../common/lookup";

declare var $:any

export class CommonFormViewComponent {

    successMessage: string;
    errorMessage: string;

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
        this.successMessage = successMessage;
        this.errorMessage = errorMessage;

        // TODO: non jQuery
        $('html, body').animate({ scrollTop: 0 }, 'fast');
    }
}