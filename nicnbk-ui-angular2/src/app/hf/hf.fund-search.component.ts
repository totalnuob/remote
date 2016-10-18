import { Component } from '@angular/core';

@Component({
    selector: 'hf-fund-search',
    templateUrl: 'view/hf.fund-search.component.html',
    styleUrls: [
        //'../../../public/css/...',
    ],
    providers: []
})
export class HFFundSearchComponent {



    search(){
        alert("search fund");
    }
}