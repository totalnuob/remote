import {Lookup} from "../common/lookup";

export class CommonComponent {
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
}