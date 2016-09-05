import {Component, AfterViewInit, ViewChild} from "@angular/core";
import {ActivatedRoute, ROUTER_DIRECTIVES} from "@angular/router";
import {CommonComponent} from "../common/common.component";
import {CommonTableau} from "./common-tableau.component";

@Component({
    selector: 'monitoring-hedge-funds',
    templateUrl: 'view/monitoring-hedge-funds.component.html',
    styleUrls: [],
    directives: [ROUTER_DIRECTIVES],
    providers: [],
})
export class MonitoringHedgeFundsComponent extends CommonTableau implements AfterViewInit {
    ngAfterViewInit():void {
        this.tableau_func()
    }
}