import {Component, AfterViewInit, ViewChild} from "@angular/core";
import {ActivatedRoute} from "@angular/router";
import {CommonComponent} from "../common/common.component";
import {CommonTableau} from "./common-tableau.component";

import '../../../public/js/viz_v1.js';

@Component({
    selector: 'monitoring-macro-monitor',
    templateUrl: 'view/monitoring-macro-monitor.component.html',
    styleUrls: [],
    providers: [],
})
export class MonitoringMacroMonitorComponent extends CommonTableau implements AfterViewInit {
    ngAfterViewInit():void {
        this.tableau_func()
    }
}