import {Component, OnInit, AfterViewInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {MacroMonitorService} from "./macromonitor.service";
import {Subscription} from 'rxjs';
import {MacroMonitorScore} from "./model/macromonitor.score";
import {CommonFormViewComponent} from "../common/common.component";
import {SaveResponse} from "../common/save-response";
import {ErrorResponse} from "../common/error-response";
import {LookupService} from "../common/lookup.service";
import {error} from "util";
import {ModuleAccessCheckerService} from "../authentication/module.access.checker.service";

declare var $:any;

@Component({
    selector: 'mm-edit',
    templateUrl: 'view/macromonitor.edit.component.html',
    styleUrls: [],
    providers: [MacroMonitorService]
})
export class MMEditComponent extends CommonFormViewComponent{

    private typeStr: string;

    scoreListForModel = [];
    fieldsValueList = [];

    dateList = [];

    // <parsing from modal>
    uploadedMacroMonitorScores;
    modalFieldTypeIndex;
    uploadedDates;
    scoreUploadErrorMessage;
    scoreUploadSuccessMessage;
    // </parsing from modal>

    public sub: any;
    public typeId: number;

    busy: Subscription;

    fieldsLookup = [];

    private moduleAccessChecker: ModuleAccessCheckerService;


    constructor (
        private route: ActivatedRoute,
        private router: Router,
        private lookupService: LookupService,
        private mmService: MacroMonitorService
    ) {
        super(router);

        this.moduleAccessChecker = new ModuleAccessCheckerService;

        if(!this.moduleAccessChecker.checkAccessMacroMonitorEditor()){
            this.router.navigate(['accessDenied']);
        }

        //load lookups
        this.sub = this.loadLookups();

        this.sub = this.route
            .params
            .subscribe(params => {
                this.typeId = +params['id'];


                // <TODO>: move to lookups
                if(this.typeId == 1) {
                    this.typeStr = 'GLOBAL';
                } else if(this.typeId == 2) {
                    this.typeStr = 'US';
                } else if(this.typeId == 3) {
                    this.typeStr = 'CHINA';
                } else {
                    this.typeStr = 'EU';
                }
                // </TODO>


                this.busy = this.mmService.get(this.typeId)
                    .subscribe(
                    (data: [MacroMonitorScore]) => {
                        if(data) {
                            this.parseDataForModel(data);
                        } else {
                            this.errorMessage = "Error loading macromonitor data";
                        }
                    });
            });
    }

    parseDataForModel(dataList) {

        for(var i = 0; i < this.fieldsLookup.length; i ++) {
            this.fieldsValueList.push(this.fieldsLookup[i].code);
            this.scoreListForModel.push([]);
        }

        for(var i = 0; i < dataList.length; i++) {
            //create list of unique dates from data and store in dateList
            if(this.dateList.indexOf(dataList[i].date) == -1) {
                this.dateList.push(dataList[i].date);
            }

            for(var j = 0; j < this.fieldsValueList.length; j++) {
                if(dataList[i].field == this.fieldsValueList[j]) {
                    this.scoreListForModel[j].push(dataList[i]);
                }
            }
        }
    }

    save(){
        this.errorMessage = null;
        this.successMessage = null;

        for(var i = 0; i < this.dateList.length - 1; i++) {
            if(this.dateList.includes(this.dateList[i], i + 1)) {
                this.postAction(null, "Duplicate date is not allowed. Please check this date: " + this.dateList[i] + ". At column: " + (i+1));
                return;
            }
        }

        let data = [];

        for(var i = 0; i < this.dateList.length; i++) {
            for(var j = 0; j < this.scoreListForModel.length; j++) {
                this.scoreListForModel[j][i].date = this.dateList[i];
            }
        }

        for(var i = 0; i < this.scoreListForModel.length; i++) {
            for(var j = 0; j < this.scoreListForModel[i].length; j++) {
                data.push(this.scoreListForModel[i][j]);
            }
        }

        this.busy = this.mmService.save(data)
            .subscribe(
                (response: SaveResponse) => {
                    this.postAction("Successfully saved", null);
                    this.errorMessage = null;
                },
                (error: ErrorResponse) => {
                    this.successMessage = null;
                    if(error && !error.isEmpty()){
                        this.processErrorMessage(error);
                    }
                    this.postAction(null, "Error saving macromonitor data");
                }
            )
    }

    loadLookups(){
        this.busy = this.lookupService.getMacroMonitorFields()
            .subscribe(
                data => {
                    this.fieldsLookup = data;
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

    addRow() {
        this.errorMessage = null;
        this.successMessage = null;

        this.dateList.splice(0,0, new String);

        for(var i = 0; i < this.scoreListForModel.length; i++) {
            let temp = new MacroMonitorScore();
            temp.field = this.fieldsValueList[i];
            temp.type = this.typeStr;
            this.scoreListForModel[i].splice(0,0, temp);
        }
    }

    removeColumn(dateIndex){
        this.errorMessage = null;
        this.successMessage = null;

        this.dateList.splice(dateIndex, 1);

        for(var i = 0; i < this.scoreListForModel.length; i++) {
            this.scoreListForModel[i].splice(dateIndex,1);
        }

    }

    closeMacroMonitorScoresModal() {
        this.errorMessage = null;
        this.successMessage = null;
        this.uploadedMacroMonitorScores = "";
        this.scoreUploadErrorMessage = null;
        this.scoreUploadSuccessMessage = null;
    }

    // Function for parsing list of scores entered from modal
    parseMacroMonitorScoresModal() {
        this.errorMessage = null;
        this.successMessage = null;

        var rows = this.uploadedMacroMonitorScores.split('\t');
        //this.scoreListForModel[this.modalFieldTypeIndex] = [];

        if( this.dateList.length != rows.length) {
            this.scoreUploadErrorMessage = "Error! The length of list of SCORES entered doesn't match dates list length!";
            this.scoreUploadSuccessMessage = null;
            return;
        }

        for(var i = 0; i < rows.length; i++) {
            let temp = new MacroMonitorScore();
            temp.field = this.fieldsValueList[this.modalFieldTypeIndex];
            temp.type = this.typeStr;
            temp.score = rows[i];
            this.scoreListForModel[this.modalFieldTypeIndex][i].score = rows[i];
        }

        this.scoreUploadSuccessMessage = "Scores added";
        this.scoreUploadErrorMessage = null;
    }


    // Function for parsing list of dates entered from modal
    createNewDateList() {
        this.errorMessage = null;
        this.successMessage = null;

        this.dateList = [];
        for(var i = 0; i < this.scoreListForModel.length; i++) {
            this.scoreListForModel[i] = [];
        }

        var rows = this.uploadedDates.split("\t");

        for(var i = 0; i < rows.length; i++) {
            this.dateList.push(rows[i]);
            //this.dateList.splice(0,0,rows[i]);

            for(var j = 0; j < this.scoreListForModel.length; j++) {
                let temp = new MacroMonitorScore();
                temp.field = this.fieldsValueList[j];
                temp.type = this.typeStr;
                this.scoreListForModel[j].splice(0,0, temp);
            }
        }

        this.scoreUploadSuccessMessage = "Dates added";
        this.scoreUploadErrorMessage = null;
    }

    // Function for getting type of the field for the list of scores from modal input
    macroScoreInputModalHelper(index) {
        this.modalFieldTypeIndex = index;
    }


    // Function for correct loop index in dynamic <input> elements
    trackByIndex(index: number, obj: any): any {
        return index;
    }

    deleteAll() {
        this.busy = this.mmService.delete(this.typeId)
            .subscribe(
                (response: SaveResponse) => {
                    this.postAction("Successfully deleted", null);
                    this.errorMessage = null;
                    this.dateList = [];
                    for(var i = 0; i < this.scoreListForModel.length; i++) {
                        this.scoreListForModel[i] = [];
                    }
                },
                (error: ErrorResponse) => {
                    this.successMessage = null;
                    if(error && !error.isEmpty()){
                        this.processErrorMessage(error);
                    }
                    this.postAction(null, "Error deleting macromonitor data");
                }
            )
    }

}