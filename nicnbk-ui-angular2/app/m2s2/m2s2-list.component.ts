import { Component, OnInit  } from '@angular/core';
import { ROUTER_DIRECTIVES } from '@angular/router';
import {LookupService} from "../common/lookup.service";

@Component({
    selector: 'm2s2-list',
    templateUrl: `app/m2s2/view/m2s2-list.component.html`,
    styleUrls: [],
    directives: [ROUTER_DIRECTIVES],
    providers: [],
})
export class M2S2ListComponent implements OnInit{

    memoTypes = [];
    meetingTypes = [];

    constructor(
        private lookupService: LookupService
    ){}

    ngOnInit():any {
        // load lookups
        this.loadLookups();
    }

    loadLookups(){
        // memo types
        this.lookupService.getMemoTypes().then(memoTypes => this.memoTypes = memoTypes);

        //meeting types
        this.lookupService.getMeetingTypes().then(meetingTypes => this.meetingTypes = meetingTypes);
    }

}