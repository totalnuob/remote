import { Component, OnInit } from '@angular/core';

declare var $:any

@Component({
    selector: 'hf-dashboard',
    templateUrl: 'view/hf.dashboard.component.html',
    styleUrls: [
        //'../../../public/css/...',
    ],
    providers: []
})
export class HFDashboardComponent implements OnInit{

    x1 = 800;
    y1 = 425;
    r1 = 90;

    x2 = 1015;
    y2 = 270;
    r2 = 80;

    ngOnInit():any {
        //console.log($(window).height());
        //console.log($(window).width());
        //$('body').height($(window).height());
        //$('body').width($(window).width());
        $('#planets').height($(window).height() - 20);
        $('#planets').width($(window).width() - 20);
    }
}