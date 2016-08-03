import {Lookup} from "../common/lookup";

export class MemoComponent {
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
}