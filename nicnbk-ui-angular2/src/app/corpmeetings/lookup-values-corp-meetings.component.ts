import {Component, OnInit} from "@angular/core";
import {CommonFormViewComponent} from "../common/common.component";
import { Http, Response } from '@angular/http';

import {Router, ActivatedRoute} from '@angular/router';
import {PeriodicReport} from "./model/periodic.report";
import {ErrorResponse} from "../common/error-response";
import {Subscription} from "rxjs/Subscription";
import {OKResponse} from "../common/ok-response";
import {SaveResponse} from "../common/save-response";

import {CorpMeetingService} from "./corp-meetings.service";
import {ModuleAccessCheckerService} from "../authentication/module.access.checker.service";
import {LookupService} from "../common/lookup.service";
import {BaseDictionary} from "../common/model/base-dictionary";

declare var $:any

@Component({
    selector: 'lookup-values-corp-meetings',
    templateUrl: 'view/lookup-values-corp-meetings.component.html',
    styleUrls: [],
    providers: [],
})
export class LookupValuesCorpMeetingsComponent extends CommonFormViewComponent implements OnInit{

    tagTypes = [{'code': 'IC', 'nameEn': 'IC Meetings Tag'}];

    selectedLookupName;
    selectedLookupValues;
    selectedEditLookup;

    constructor(
        private router: Router,
        private route: ActivatedRoute,
        private lookupService: LookupService,
        private corpMeetingService: CorpMeetingService,
        private moduleAccessChecker: ModuleAccessCheckerService
    ){
        super(router);
    }
    busy: Subscription;
    ngOnInit():void {
    }

    selectLookup(value){
        this.errorMessage = null;
        this.successMessage = null;
        if(value){
            this.selectedLookupName = value;
        }
        if(this.selectedLookupName){
            if(this.selectedLookupName === 'TAGS'){
                this.busy = this.lookupService.getAvailableTagsByType('IC')
                    .subscribe(
                        result  => {
                            this.selectedLookupValues = result;
                        },
                        (error: ErrorResponse) => {
                            this.errorMessage = "Error loading lookups";
                            if(error && !error.isEmpty()){
                                this.processErrorMessage(error);
                            }
                            this.postAction(null, null);
                        }
                    );

            }
        }
    }

    edit(item){
        this.errorMessageSaveLookup = null;
        this.successMessageSaveLookup = null;
        if(item){
            this.selectedEditLookup = item;
        }else {
            this.selectedEditLookup = new BaseDictionary();
        }
        if(this.selectedEditLookup.type == null){
            this.selectedEditLookup.type = new BaseDictionary();
        }
        this.selectedEditLookup.type.code = "IC";

    }

    save(){
        if(this.selectedEditLookup == null){
            this.errorMessageSaveLookup = "Error saving lookup";
            return;
        }
        if (this.selectedEditLookup.name == null || this.selectedEditLookup.name.trim() === '') {
            this.errorMessageSaveLookup = "Name required.";
            return;
        }

        // Check duplicate name
        if(this.selectedLookupValues && this.selectedLookupValues.length > 0) {
            for (var i = 0; i < this.selectedLookupValues.length; i++) {
                if(this.selectedLookupValues[i].name === this.selectedEditLookup.name){
                    this.errorMessageSaveLookup = "Duplicate name.";
                    this.successMessageSaveLookup = null;
                    return;
                }
            }
        }

        this.busy = this.lookupService.saveTag(this.selectedEditLookup)
            .subscribe(
                (saveResponse: SaveResponse) => {
                    if(saveResponse.status === 'SUCCESS' ){
                        this.errorMessageSaveLookup = null;
                        this.successMessageSaveLookup = saveResponse.message.nameEn;
                        this.selectedEditLookup.id = saveResponse.entityId;

                        this.selectLookup(this.selectedLookupName);

                    }else{
                        if(saveResponse.message != null){
                            var message = saveResponse.message.nameEn != null ? saveResponse.message.nameEn :
                                saveResponse.message.nameRu != null ? saveResponse.message.nameRu : saveResponse.message.nameKz;
                            if(message != null && message != ''){
                                this.postAction(null, message);
                            }else{
                                this.postAction(null, "Error saving lookup value");
                            }
                            this.successMessageSaveLookup = null;
                        }
                    }
                },
                (error: ErrorResponse) => {
                    this.errorMessageSaveLookup = error && error.message ? error.message : "Error saving lookup value.";
                    this.successMessageSaveLookup = null;
                }
            );
    }

    canEdit(){
        return this.moduleAccessChecker.checkAccessICMeetingAdmin();
    }

}