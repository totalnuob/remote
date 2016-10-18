import {Component, AfterViewInit, ViewChild} from "@angular/core";
import {ActivatedRoute} from "@angular/router";
import {CommonComponent} from "../common/common.component";
import {CommonTableau} from "./common-tableau.component";


@Component({
    selector: 'monitoring-private-equity',
    templateUrl: 'view/monitoring-private-equity.component.html',
    styleUrls: [],
    providers: [],
})
export class MonitoringPrivateEquityComponent extends CommonTableau implements AfterViewInit {
    ngAfterViewInit():void {
        this.tableau_func()
    }
}