import { Component, OnInit  } from '@angular/core';
import {LookupService} from "../common/lookup.service";
import {PEMemo} from "./model/pe-memo";

import {Calendar} from 'primeng/primeng';

declare var $:any

@Component({
    selector: 'pe-memo-edit',
    templateUrl: `app/m2s2/view/pe-memo-edit.component.html`,
    styleUrls: [],
    directives: [],
    providers: [],
})
export class PrivateEquityMemoEditComponent implements OnInit{
    ngOnInit():any {

        // TODO: exclude jQuery
        // datetimepicker
        $('#meetingDate').datetimepicker({
            defaultDate: new Date(),
            format: 'DD-MM-YYYY'
        });
    }
    memo = new PEMemo;

    changeArrangedBy(){
        if(this.memo.arrangedBy != 'OTHER'){
            this.memo.arrangedByDescription = '';
        }
    }
}