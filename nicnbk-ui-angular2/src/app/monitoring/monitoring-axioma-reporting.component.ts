import {Component, AfterViewInit, ViewChild} from "@angular/core";
import {ActivatedRoute} from "@angular/router";
import {CommonFormViewComponent} from "../common/common.component";
import {CommonTableau} from "./common-tableau.component";

@Component({
    selector: 'monitoring-axioma-reporting',
    templateUrl: 'view/monitoring-axioma-reporting.component.html',
    styleUrls: [],
    providers: [],
})
export class MonitoringAxiomaReportingComponent extends CommonTableau implements AfterViewInit {
    ngAfterViewInit():void {
        this.tableau_func()
    }
}