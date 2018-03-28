import {Component, OnInit} from '@angular/core';
import {Router, ActivatedRoute} from '@angular/router';

import {LookupService} from "../common/lookup.service";
import {CommonFormViewComponent} from "../common/common.component";
import {Subscription} from 'rxjs';
import {ErrorResponse} from "../common/error-response";

declare var $:any

@Component({
    selector: 'corp-meetings-list',
    templateUrl: 'view/corp-meetings-list.component.html',
    styleUrls: [],
    providers: [],
})
export class CorpMeetingsListComponent extends CommonFormViewComponent implements OnInit {
    busy: Subscription;
    public sub: any;

    constructor(
        private lookupService: LookupService,
        private router: Router,
        private route: ActivatedRoute
    ){
        super(router);

        //this.sub = this.route
        //    .params
        //    .subscribe(params => {
        //        if(params['params'] != null){
        //            this.searchParams = JSON.parse(params['params']);
        //            this.busy = this.tripMemoService.search(this.searchParams)
        //                .subscribe(
        //                    searchResult  => {
        //                        this.tripMemoList = searchResult.tripMemos;
        //                        this.tripMemoSearchResult = searchResult;
        //                    },
        //                    error =>  this.errorMessage = "Failed to search trip memos."
        //                );
        //        } else {
        //            this.search(0);
        //        }
        //    });

    }

    ngOnInit():any {
        // TODO: exclude jQuery
        // datetimepicker
        $('#fromDate').datetimepicker({
            //defaultDate: new Date(),
            format: 'DD-MM-YYYY'
        });
        $('#toDate').datetimepicker({
            //defaultDate: new Date(),
            format: 'DD-MM-YYYY'
        });

        // load lookups
        //this.loadLookups();

        // find all
        //fthis.search(0);
    }

    public canEdit(){
        return true;
    }

    search(page){

        //this.busy = this.tripMemoService.search(this.searchParams)
        //    .subscribe(
        //        searchResult  => {
        //            this.tripMemoList = searchResult.tripMemos;
        //            this.tripMemoSearchResult = searchResult;
        //        },
        //        (error: ErrorResponse) => {
        //            this.errorMessage = "Error searching memos";
        //            console.log("Error searching memos");
        //            if(error && !error.isEmpty()){
        //                this.processErrorMessage(error);
        //            }
        //            this.postAction(null, null);
        //        }
        //    );
    }
}