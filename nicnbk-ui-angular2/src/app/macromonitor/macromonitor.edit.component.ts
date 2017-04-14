import { Component, OnInit, AfterViewInit } from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {MacroMonitorService} from "./macromonitor.service";
import {Subscription} from 'rxjs';
import {MacroMonitorScore} from "./model/macromonitor.score";
import {CommonFormViewComponent} from "../common/common.component";
import {SaveResponse} from "../common/save-response";
import {ErrorResponse} from "../common/error-response";
import {LookupService} from "../common/lookup.service";
import {error} from "util";


declare var $:any

@Component({
    selector: 'mm-edit',
    templateUrl: 'view/macromonitor.edit.component.html',
    styleUrls: [],
    providers: [MacroMonitorService]
})
export class MMEditComponent extends CommonFormViewComponent implements OnInit, AfterViewInit{

    private typeStr: string;

    scoreListForModel = [];
    fieldsValueList = [];

    scoreList = [];
    dateList = [];

    scoreListInput = [];
    dateListInput = [];


    // <parsing from modal>
    uploadedMacroMonitorScores;

    uploadedDates;

    scoreUploadErrorMessage;
    scoreUploadSuccessMessage;
    fromDate;
    toDate;
    // </>

    public sub: any;
    public typeId: number;

    busy: Subscription;

    fieldsLookup = [];

    constructor (
        private route: ActivatedRoute,
        private router: Router,
        private lookupService: LookupService,
        private mmService: MacroMonitorService
    ) {
        super();

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
                            //this.scoreList.push(data);
                            //console.log(data);
                            this.parseDataForModel(data);
                        } else {
                            this.errorMessage = "Error loading macromonitor data";
                        }
                    });
            });
    }

    ngOnInit():any {

        $('#fromDate').datetimepicker({
            //defaultDate: new Date(),
            format: 'MM-YYYY'
        });

        $('#toDate').datetimepicker({
            //defaultDate: new Date(),
            format: 'MM-YYYY'
        });

    }

    ngAfterViewInit(){
        //$('#scoresDate').datetimepicker({
        //    //defaultDate: new Date(),
        //    format: 'DD-MM-YYYY'
        //});
    }

    setDateTimePicker() {
        console.log("Hey1");
        $('#scoresDateValue').datetimepicker({
            format: 'MM-YYYY'
        });
        //$("[name = 'scoresDate']").each(
        //    console.log("Hey")
            //$('#scoresDate').datetimepicker({
            ////defaultDate: new Date(),
            //format: 'DD-MM-YYYY'
            //})
        //);
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

        $('#fromDate').val(this.dateList[this.dateList.length - 1]);
        $('#toDate').val(this.dateList[0]);



        console.log("DateList");
        console.log(this.dateList);
        console.log("ScoreListForModel");
        console.log(this.scoreListForModel);

    }

    //parseData(dataList) {
    //    let valueList = [];
    //
    //
    //    for(var i = 0; i < this.fieldsLookup.length; i ++) {
    //        valueList.push([this.fieldsLookup[i].code]);
    //    }
    //
    //
    //
    //    console.log(valueList);
    //
    //    for(var i = 0; i < dataList[0].length; i++) {
    //        if(this.dateList.indexOf(dataList[0][i].date) == -1) {
    //            this.dateList.push(dataList[0][i].date);
    //        }
    //        for(var j = 0; j < valueList.length; j++) {
    //            if(dataList[0][i].field == valueList[j][0]) {
    //                valueList[j].push(dataList[0][i].score);
    //            }
    //        }
    //    }
    //
    //    console.log(this.dateList);
    //    console.log(valueList);
    //}

    save(){

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

        this.mmService.save(data)
            .subscribe(
                (response: SaveResponse) => {
                    //this.mmScore.id = response.entityId;
                    //this.mmScore.creationDate = response.creationDate;

                    this.postAction("Successfully saved", null);
                },
                (error: ErrorResponse) => {
                    this.errorMessage = "Error saving macromonitor data";
                    if(error && !error.isEmpty()){
                        this.processErrorMessage(error);
                    }
                    this.postAction(null, null);
                }
            )
    }

    loadLookups(){
        this.lookupService.getMacroMonitorFields()
            .subscribe(
                data => {
                    this.fieldsLookup = data;
                    //console.log(this.fieldsLookup);
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

        this.dateList.splice(0,0, new String);

        for(var i = 0; i < this.scoreListForModel.length; i++) {
            let temp = new MacroMonitorScore();
            temp.field = this.fieldsValueList[i];
            temp.type = this.typeStr;
            this.scoreListForModel[i].splice(0,0, temp);
        }

        //let list = [];
        //for(var i = 0; i < this.fieldsLookup.length; i++) {
        //    list.push(new MacroMonitorScore());
        //    list[i].field = this.fieldsLookup[i].code;
        //    list[i].type = "GLOBAL";
        //}
        //
        //this.scoreListInput.push(list);
        //this.dateListInput.push(new Date);

        console.log(this.dateList);
        console.log(this.scoreListForModel);
    }

    removeColumn(dateIndex){

        this.dateList.splice(dateIndex, 1);

        for(var i = 0; i < this.scoreListForModel.length; i++) {
            this.scoreListForModel[i].splice(dateIndex,1);
        }

        ////console.log(item);
        //for(var i = this.scoreListInput.length; i--;) {
        //    if(this.scoreListInput[i] === item) {
        //        this.scoreListInput.splice(i, 1);
        //    }
        //}
        //
        //this.dateListInput.splice(dateIndex,1);
    }

    closeMacroMonitorScoresModal() {
        this.uploadedMacroMonitorScores = "";
        this.scoreUploadErrorMessage = null;
        this.scoreUploadSuccessMessage = null;
    }

    parseMacroMonitorScoresModal() {
        console.log(this.uploadedMacroMonitorScores);

        var rows = this.uploadedMacroMonitorScores.split('\n');

        for(var i = 0; i < rows.length; i++) {
            console.log(rows[i]);
            console.log(rows[i].length);
        }
    }

    createNewDateList() {
        //this.toDate = $('#toDate').val();
        //this.fromDate = $('#fromDate').val();

        this.dateList = [];

        var rows = this.uploadedDates.split("\t");
        console.log(rows);
        for(var i = 0; i < rows.length; i++) {
            this.dateList.push(rows[i]);
        }

    }
}