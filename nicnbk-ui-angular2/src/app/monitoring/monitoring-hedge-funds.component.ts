import {Component, AfterViewInit, ViewChild} from "@angular/core";
import {ActivatedRoute} from "@angular/router";
import {CommonComponent} from "../common/common.component";
import {CommonTableau} from "./common-tableau.component";

@Component({
    selector: 'monitoring-hedge-funds',
    templateUrl: 'view/monitoring-hedge-funds.component.html',
    styleUrls: [],
    providers: [],
})
export class MonitoringHedgeFundsComponent extends CommonTableau implements AfterViewInit {
    ngAfterViewInit():void {
        this.tableau_func()
    }
}