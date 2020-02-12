import { Component, AfterViewInit  } from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {HedgeFundService} from "./hf.fund.service";
import {CommonFormViewComponent} from "../common/common.component";
import {Subscription} from 'rxjs';
import {ModuleAccessCheckerService} from "../authentication/module.access.checker.service";
import {ErrorResponse} from "../common/error-response";
import {HedgeFundScreeningSearchParams} from "./model/hf.screening-search-params";
import {HedgeFundScreeningSearchResults} from "./model/hf-screening-search-results";
import {HedgeFundScreening} from "./model/hf.screening";
import {HedgeFundScreeningService} from "./hf.fund.screening.service";
import {HedgeFundScreeningFilteredResult} from "./model/hf.screening.filtered.result";
import {HedgeFundScreeningFilteredResultStatistics} from "./model/hf.screening.filtered.result.statistics";
import {HedgeFundScreeningFilteredResultFund} from "./model/hf.screening.filtered.result.fund";
import {GoogleChartComponent} from "../google-chart/google-chart.component";

import {DATA_APP_URL} from "../common/common.service.constants";

declare var google:any;
declare var $:any

@Component({
    selector: 'hf-fund-search',
    templateUrl: 'view/hf.screening.filters.edit.component.html',
    styleUrls: [
        //'../../../public/css/...',
        '../../../node_modules/angular2-busy/build/style/busy.css'
    ],
    providers: []
})
export class HFScreeningFilteredResultsEditComponent extends GoogleChartComponent implements AfterViewInit{

    screeningId;
    id;
    filteredResult = new HedgeFundScreeningFilteredResult;
    filteredFundList;
    isUnqualifiedFundList = false;
    isQualifiedFundList = false;
    fundListLookbackAUM;
    fundListLookbackReturn;
    fundListType;

    modalSuccessMessage;
    modalErrorMessage;

    selectedFund: HedgeFundScreeningFilteredResultFund;
    selectedFundReturns;

    selectedFundErrorMessage: string;
    selectedFundSuccessMessage: string;

    private moduleAccessChecker: ModuleAccessCheckerService;

    public sub: any;
    busyGet : Subscription;
    busyStats : Subscription;
    busyModal: Subscription;
    busyFundEdit: Subscription;

    needUpdate = false;
    excludeSource;

    uploadedReturns;
    returnUploadErrorMessage;
    returnUploadSuccessMessage;

    excludeFundModalErrorMessage;
    excludeFundModalSuccessMessage;

    showFinalQualifiedFundList = true;
    showFinalUnqualifiedFundList = false;
    showFinalUndecidedFundList = false;

    constructor(
        private screeningService: HedgeFundScreeningService,
        private router: Router,
        private route: ActivatedRoute
    ){
        super();

        this.moduleAccessChecker = new ModuleAccessCheckerService;

        if(!this.moduleAccessChecker.checkAccessHedgeFunds()){
            this.router.navigate(['accessDenied']);
        }

        this.sub = this.route
            .params
            .subscribe(params => {
                this.screeningId = +params['screeningId'];
                this.id = +params['id'];
                if(this.screeningId > 0) {
                    if (this.id > 0) {
                        this.loadFilteredResult();
                    } else {
                        this.filteredResult = new HedgeFundScreeningFilteredResult();
                        this.filteredResult.screeningId = this.screeningId;
                        this.filteredResult.editable = true;
                    }
                }
            });
    }


    ngOnInit():any {
        $('#startDateTPickeer').datetimepicker({
            //defaultDate: new Date(),
            format: 'MM.YYYY'
        });

        $('#fundListModal').on('hidden.bs.modal', function () {
            $('#closeFundListModalButton').click();
        });


        $('#fundEditModal').on('hidden.bs.modal', function () {
            $('#modalMessagesDiv').css("background-color", "white");
            $('#closeFundEditModalBtn').click();
            $('#closeReturnsModalBtn').click();
        });

//        $('#returnInputModal').on('hidden.bs.modal', function () {
//            $('#closeReturnsModalBtn').click();
//        });

        $('#excludeFundModal').on('hidden.bs.modal', function () {
            $('#modalMessagesDiv').css("background-color", "white");
            $('#closeFundExcludeModalBtn').click();
        });
    }

    drawGraph(){
        if(this.filteredResult != null && this.filteredResult.filteredResultStatistics != null && this.filteredResult.filteredResultStatistics.finalResults != null){
            this.drawFinalScreeningResultsPieChart();
            this.drawFinalUnqualifiedFundsReasonBarChart();

            this.drawFinalQualifiedTop50StrategiesPieChart();
            this.drawFinalTop5QualifiedBarChart();
        }
    }

    drawFinalScreeningResultsPieChart(){
        if(typeof google.visualization === 'undefined'){
            console.log("undefined");
            return;
        }
        var qualified = this.filteredResult.filteredResultStatistics.finalResults.qualifiedFunds != null ? this.filteredResult.filteredResultStatistics.finalResults.qualifiedFunds.length : 0;
        var unqualified = this.filteredResult.filteredResultStatistics.finalResults.unqualifiedFunds != null ? this.filteredResult.filteredResultStatistics.finalResults.unqualifiedFunds.length : 0;
        var undecided = this.filteredResult.filteredResultStatistics.finalResults.undecidedFunds != null ? this.filteredResult.filteredResultStatistics.finalResults.undecidedFunds.length : 0;
        var total = qualified + unqualified + undecided;

        var dataList = [['Type', 'Number']];
        if(qualified > 0){
            dataList.push(["Qualified funds", qualified]);
        }
        if(unqualified > 0){
            dataList.push(["Unqualified funds", unqualified]);
        }
        if(undecided > 0){
            dataList.push(["Undecided funds", undecided]);
        }

        var data = google.visualization.arrayToDataTable(dataList);

        var options = {
            title: 'Screening results',
            legend: {position: 'labeled'},
            chartArea: {
                height: '80%',
                top: '10%'
            },
            pieSliceText: 'value',
            colors: ['#428F4A', '#E86753', '#CFD0D0']
            sliceVisibilityThreshold: 0
        };

        var chart = new google.visualization.PieChart(document.getElementById('finalScreeningResultsPieChart'));
        chart.draw(data, options);
    }

    drawFinalQualifiedTop50StrategiesPieChart(){
        console.log("drawFinalQualifiedTop50StrategiesPieChart");
            if(typeof google.visualization === 'undefined'){
                console.log("undefined");
                return;
            }
            let strategyMap = new Map();
            for(var i = 0; i < this.filteredResult.filteredResultStatistics.finalResults.top50qualifiedFunds.length; i++){
                var strategy = this.filteredResult.filteredResultStatistics.finalResults.top50qualifiedFunds[i].mainStrategy;
                if(strategyMap.get(strategy) != null){
                    var count = strategyMap.get(strategy);
                    strategyMap.set(strategy, (count + 1));
                }else if(strategy != null){
                    strategyMap.set(strategy, 1);
                }
            }
            console.log(strategyMap);

            var dataList = [['Type', 'Number']];
            strategyMap.forEach(function(value, key) {
                console.log([key, value]);
                dataList.push([key, value]);
            })
            console.log(dataList);

            var data = google.visualization.arrayToDataTable(dataList);
            var options = {
                title: 'Top 50 by Strategy',
                //legend: {position: 'labeled'},
                chartArea: {
                    height: '80%',
                    top: '10%'
                },
                pieSliceText: 'value',
                //colors: ['#428F4A', '#E86753', '#CFD0D0']
                sliceVisibilityThreshold: 0
            };

            var chart = new google.visualization.PieChart(document.getElementById('finalQualifiedTop50StrategiesPieChart'));
            chart.draw(data, options);
        }

    getUnqualifiedFundsStatistics(){
        if(this.filteredResult.filteredResultStatistics.finalResults != null && this.filteredResult.filteredResultStatistics.finalResults.unqualifiedFunds != null){
            var fundAUMCount = 0;
            var managerAUMCount = 0;
            var trackRecordCount = 0;
            var excludedCount = 0;
            console.log(this.filteredResult.filteredResultStatistics.finalResults.unqualifiedFunds);
            for(var i = 0; i < this.filteredResult.filteredResultStatistics.finalResults.unqualifiedFunds.length; i++){
                var fund = this.filteredResult.filteredResultStatistics.finalResults.unqualifiedFunds[i];
                if(!fund.strategyAUMCheck){
                    fundAUMCount++;
                }
                if(!fund.managerAUMCheck){
                    managerAUMCount++;
                }
                if(!fund.trackRecordCheck){
                    trackRecordCount++;
                }
                if(fund.excluded){
                    excludedCount++;
                }
            }
            return [fundAUMCount, managerAUMCount, trackRecordCount, excludedCount];
        }
    }

    drawFinalUnqualifiedFundsReasonBarChart(){
        var unqualifiedFundsStatistics = this.getUnqualifiedFundsStatistics();
        console.log(unqualifiedFundsStatistics);
        if(unqualifiedFundsStatistics == null || unqualifiedFundsStatistics.length != 4){
            return;
        }

        var dataArray = [['', '']];
        dataArray.push(['Strategy AUM', unqualifiedFundsStatistics[0]]);
        dataArray.push(['Manager AUM', unqualifiedFundsStatistics[1]]);
        dataArray.push(['Track Record', unqualifiedFundsStatistics[2]]);
        dataArray.push(['Excluded fund', unqualifiedFundsStatistics[3]]);

        //console.log(dataArray);
        if(google && google.visualization) {
            var data = google.visualization.arrayToDataTable(dataArray);

            var options = {
                title: "Unqualified Funds Analysis",
                titleTextStyle: {
                    color: 'black',    // any HTML string color ('red', '#cc00cc')
                    //fontName: <string>, // i.e. 'Times New Roman'
                    fontSize: 12, // 12, 18 whatever you want (don't specify px)
                    bold: true,    // true or false
                    italic: false   // true of false
                },
                //width: 600,
                //height: 400,
                animation: {
                    duration: 500,
                    easing: 'out',
                    startup: true,
                },
                hAxis: {
                    format: '#.##',
                },
                chartArea: {left: '30%'},

                //bar: {groupWidth: "80%"},
                colors: ['#307240', '#79b588', '#b6dbbf', '#a7aba8'],
                legend: {position: "none"},
            };

            var chart = new google.visualization.BarChart(document.getElementById("finalUnqualifiedFundsReasonBarChart"));
            chart.draw(data, options);
        }
    }

    drawFinalTop5QualifiedBarChart(){
        if(google && google.visualization) {
            var count1 = 0;
            var count2 = 0;
            var count3 = 0;
            var count4 = 0;
            var count5 = 0;
            var count6 = 0;
            var count7 = 0;
            var count8 = 0;
            var count9 = 0;
            var count10 = 0;
            var minScore = Math.round(this.filteredResult.filteredResultStatistics.finalResults.top50qualifiedFunds[this.filteredResult.filteredResultStatistics.finalResults.top50qualifiedFunds.length - 1].totalScore);

            for(var i = 0; i < this.filteredResult.filteredResultStatistics.finalResults.top50qualifiedFunds.length; i++){
                var totalScore = Number(this.filteredResult.filteredResultStatistics.finalResults.top50qualifiedFunds[i].totalScore);
                if(totalScore <= 1){
                    count1++;
                }else if(totalScore > 1 && totalScore <= 2){
                    count2++;
                }else if(totalScore > 2 && totalScore <= 3){
                    count3++;
                }else if(totalScore > 3 && totalScore <= 4){
                    count4++;
                }else if(totalScore > 4 && totalScore <= 5){
                    count5++;
                }else if(totalScore > 5 && totalScore <= 6){
                    count6++;
                }else if(totalScore > 6 && totalScore <= 7){
                    count7++;
                }else if(totalScore > 7 && totalScore <= 8){
                    count8++;
                }else if(totalScore > 8 && totalScore <= 9){
                    count9++;
                }else if(totalScore > 9){
                    count10++;
                }
            }

            var dataArray = [['', '']];
            if(count1 > 0){
                dataArray.push(['<= 1', count1]);
            }
            if(count2 > 0){
                dataArray.push(['1 - 2', count2]);
            }
            if(count3 > 0){
                dataArray.push(['2 - 3', count3]);
            }
            if(count4 > 0){
                dataArray.push(['3 - 4', count4]);
            }
            if(count5 > 0){
                dataArray.push(['4 - 5', count5]);
            }
            if(count6 > 0){
                dataArray.push(['5 - 6', count6]);
            }
            if(count7 > 0){
                dataArray.push(['6 - 7', count7]);
            }
            if(count8 > 0){
                dataArray.push(['7 - 8', count8]);
            }
            if(count9 > 0){
                dataArray.push(['8 - 9', count9]);
            }
            if(count10 > 0){
                dataArray.push(['> 9', count10]);
            }
            //console.log(dataArray);
            var data = google.visualization.arrayToDataTable(dataArray);

            var options = {
                title: "Top 50 Total Score Distribution",
                titleTextStyle: {
                    color: 'black',    // any HTML string color ('red', '#cc00cc')
                    //fontName: <string>, // i.e. 'Times New Roman'
                    fontSize: 12, // 12, 18 whatever you want (don't specify px)
                    bold: true,    // true or false
                    italic: false   // true of false
                },
                //width: 600,
                //height: 400,
                animation: {
                    duration: 500,
                    easing: 'out',
                    startup: true,
                },
                hAxis: {
                    format: '#.##',
                },
                chartArea: {left: '30%'},

                //bar: {groupWidth: "80%"},
                colors: ['#307240', '#79b588', '#b6dbbf', '#a7aba8'],
                legend: {position: "none"},
            };

            var chart = new google.visualization.ColumnChart(document.getElementById("finalTop50QualifiedBarChart"));
            chart.draw(data, options);
        }
    }

    loadFilteredResult(){
        this.busyGet = this.screeningService.getFilteredResult(this.id)
            .subscribe(
                result => {
                    this.filteredResult = result;
                    //console.log(this.filteredResult);
                    this.onNumberChangeFundAUM();
                    this.onNumberChangeManagerAUM();
                    if(this.filteredResult.filteredResultStatistics.finalResults != null){
                        this.drawGraph();
                    }
                    this.successMessage = null;
                    this.errorMessage = null;
                    this.modalSuccessMessage = null;
                    this.modalErrorMessage = nulls;
                    this.selectedFundErrorMessage = null;
                    this.selectedFundSuccessMessage = null;
                },
                error => {
                    //console.log(error);
                    this.postAction(null, "Failed to load screening filtered results");
                }
            );
    }

    saveFilters() {
        this.filteredResult.startDateMonth= $('#startDate').val();
        this.filteredResult.fundAUM = Number(this.filteredResult.fundAUM.toString().replace(/,/g, ''));
        this.filteredResult.managerAUM = Number(this.filteredResult.managerAUM.toString().replace(/,/g, ''));
        if(this.filteredResult.trackRecord != null && this.filteredResult.trackRecord == 0){
            this.postAction(null, "Track record must be greater than 0");
        }

        if(this.filteredResult.startDateMonth == null){
            this.postAction(null, "Date required");
            return;
        }

        //console.log(this.filteredResult);
        this.busyGet = this.screeningService.saveFilteredResult(this.filteredResult)
            .subscribe(
                response => {
                    this.filteredResult.id = response.entityId;
                    this.id = this.filteredResult.id;

                    this.onNumberChangeFundAUM();
                    this.onNumberChangeManagerAUM();
                    this.postAction("Successfully saved filters. Need to reapply filters to see the changes", null);
                },
                (error: ErrorResponse) => {
                    this.errorMessage = "Error saving filters";
                    if(error && !error.isEmpty()){
                        this.processErrorMessage(error);
                    }
                    this.postAction(null, this.errorMessage);
                }
            );
    }

    applyFilters(){

        // TODO: check params

        this.filteredResult.startDateMonth= $('#startDate').val();
        this.filteredResult.fundAUM = Number(this.filteredResult.fundAUM.toString().replace(/,/g, ''));
        this.filteredResult.managerAUM = Number(this.filteredResult.managerAUM.toString().replace(/,/g, ''));

        // TODO: check required parameters
        if(this.filteredResult.startDateMonth == null){
            this.postAction(null, "Date required");
            return;
        }

        this.loadFilteredResult();

/*
        this.busyStats = this.screeningService.getFilteredResultStatistics(this.filteredResult)
            .subscribe(
                result => {
                    this.filteredResult.filteredResultStatistics = result;

                    this.onNumberChangeFundAUM();
                    this.onNumberChangeManagerAUM();
                },
                error => {
                    this.postAction(null, "Failed to load screening filtered results statistics");
                }
            );
*/
    }

    checkManagerAUM(fund){
        if(fund.managerAUM != null) {
            //fund.managerAUM = Number(fund.managerAUM.toString().replace(/,/g, ''));
            var fundAUM = fund.fundAUM != null ? Number(fund.fundAUM.toString().replace(/,/g, '')) : 0;
            return Number(fund.managerAUM.toString().replace(/,/g, '')) >= Number(this.filteredResult.managerAUM.toString().replace(/,/g, '')) &&
                Number(fund.managerAUM.toString().replace(/,/g, '')) >= fundAUM;
        }else if(fund.strategyAUM != null){
            return Number(fund.strategyAUM.toString().replace(/,/g, '')) >= Number(this.filteredResult.managerAUM.toString().replace(/,/g, ''))
        }else if(fund.fundAUM != null){
            return Number(fund.fundAUM.toString().replace(/,/g, '')) >= Number(this.filteredResult.managerAUM.toString().replace(/,/g, ''))
        }else{
            // manager, strategy, fund AUM - NULL
            return false;
        }
    }

    checkFundAUM(fundAUM) {
        if(fundAUM!= null) {
            var fundAUMValue = Number(fundAUM.toString().replace(/,/g, ''));
            return fundAUMValue >= Number(this.filteredResult.fundAUM.toString().replace(/,/g, ''))
        }else{
            // manager, strategy, fund AUM - NULL
            return false;
        }
    }

    checkStrategyAUM(strategyAUM){
        return this.checkFundAUM(strategyAUM);
    }


    exportFundList() {
        //console.log(this.fundListLookbackAUM, this.fundListLookbackReturn, this.fundListType);
        console.log(this.fundListType);
        this.busyModal = this.screeningService.makeFileRequest(DATA_APP_URL + `hf/screening/scoring/export/${this.fundListType}/${this.filteredResult.id}/${this.fundListLookbackAUM}/${this.fundListLookbackReturn}`)
            .subscribe(
                response  => {
                    //console.log("ok");
                },
                error => {
                    //console.log("fails")
                    this.modalErrorMessage = "Error exporting data";
                    this.modalSuccessMessage = null;
                }
            );
    }

    showFunds(lookbackReturn, lookbackAUM, type, value: number){

        this.fundListLookbackAUM = lookbackAUM;
        this.fundListLookbackReturn = lookbackReturn;
        this.fundListType = type;

        this.modalErrorMessage = null;
        this.modalSuccessMessage = null;
        //this.showManagerAUMInput = false;
        this.isUnqualifiedFundList = false;
        this.isQualifiedFundList = false;

        var params = new HedgeFundScreeningFilteredResult();
        params.screeningId = this.filteredResult.screeningId;
        params.id = this.filteredResult.id;

        params.fundAUM = Number(this.filteredResult.fundAUM.toString().replace(/,/g, ''));
        params.managerAUM = Number(this.filteredResult.managerAUM.toString().replace(/,/g, ''));
        params.startDateMonth = $('#startDate').val();
        params.trackRecord = this.filteredResult.trackRecord;
        params.lookbackReturns = lookbackReturn;
        params.lookbackAUM = lookbackAUM;

        this.filteredFundList = [];

        if(params.startDateMonth == null){
            this.postAction(null, "Date required");
            return;
        }

        if(type == 1) {
            this.busyModal = this.screeningService.getFilteredResultQualifiedFundList(params)
                .subscribe(
                    response  => {
                        if (response) {
                            if (response.status === 'FAIL') {
                                if(response.message != null){
                                    this.modalErrorMessage = response.message.nameEn ? response.message.nameEn :
                                        response.message.nameKz ? response.message.nameKz : response.message.nameRu ? response.message.nameRu : null;
                                }
                                if(this.modalErrorMessage == null){
                                    this.modalErrorMessage = "Error loading KZT Form 1";
                                }
                                this.isQualifiedFundList = true;

                                this.filteredFundList = response.records;
                                if(value != null && value != this.filteredFundList.length){
                                    alert("Expected " + value + ", received " + this.filteredFundList.length);
                                }

                                this.modalPostAction(null, this.modalErrorMessage);
                            }else {
                                this.filteredFundList = response.records;
                            }
                        }
                    },
                    error => {
                        this.modalPostAction(null,  "Failed to load fund list");
                    }
                );
        }else if(type == 2) {
            this.isUnqualifiedFundList = true;
            this.busyModal = this.screeningService.getFilteredResultUnqualifiedFundList(params)
                .subscribe(
                    result => {
                        this.filteredFundList = result;
                        //console.log(result);
                        if(value != null && value != this.filteredFundList.length){
                            alert("Expected " + value + ", received " + this.filteredFundList.length);
                        }
                    },
                    error => {
                        this.modalPostAction(null,  "Failed to load fund list");
                    }
                );
        }else if(type == 3) {
            //this.showManagerAUMInput = true;
            this.busyModal = this.screeningService.getFilteredResultUndecidedFundList(params)
                .subscribe(
                    result => {
                        this.filteredFundList = result;
                        if(value != null && value != this.filteredFundList.length){
                            alert("Expected " + value + ", received " + this.filteredFundList.length);
                        }
                    },
                    error => {
                        this.modalPostAction(null,  "Failed to load fund list");
                    }
                );
        }
    }

    getCellColor(i,j){
        var value = this.filteredResult.filteredResultStatistics.qualified[i][j];
        if(j==0 || i==0){
            return "white";
        }
        var min = null;
        var max = null;
        for(var i = 1; i < this.filteredResult.filteredResultStatistics.qualified.length; i++){
            for(var j = 1; j < this.filteredResult.filteredResultStatistics.qualified[i].length; j++){
                var temp = this.filteredResult.filteredResultStatistics.qualified[i][j];
                if(min == null || temp < min){
                    min = temp;
                }
                if(max == null || temp > max){
                    max = temp;
                }
            }
        }
        var step = (max - min) / 5.0;
        //console.log("value=" + value + ", min="  +min + ", max=" + max + ", step=" + step)

        if(value >= min && value <= min + 1*step){
            return '#E86753'; //'#f7dea5';
        }else if(value > min + 1*step && value <= min + 2*step){
            return '#FCB4A5'; //'#f7dea5';
        }else if(value > min + 2*step && value <= min + 3*step){
            return '#B9D7B7';
        }else if(value > min + 3*step && value <= min + 4*step){
            return '#74AF72';
        }else if(value > min + 4*step ){
            return '#428F4A';
        }

        //if(value >= min && value <= min + 1*step){
        //    return '#f4dec0';
        //}else if(value > min + 1*step && value <= min + 2*step){
        //    return '#b1ceaf';
        //}else if(value > min + 2*step && value <= min + 3*step){
        //    return '#86af83';
        //}else if(value > min + 3*step && value <= min + 4*step){
        //    return '#62a060';
        //}else if(value > min + 4*step ){
        //    return '#428F4A';
        //}
    }

    //editFundManagerAUM(fund){
    //    //this.showManagerAUMInput = !this.showManagerAUMInput;
    //    fund.editing = !fund.editing;
    //    this.onNumberFund(fund);
    //    this.managerAUMbeforeEdit = fund.managerAUM;
    //}

    //saveUndecidedList(){
    //
    //    for(var i = 0; i < this.filteredFundList.length; i++) {
    //        if(this.filteredFundList[i].managerAUM != null) {
    //            if(this.filteredFundList[i].managerAUM != null && this.filteredFundList[i].managerAUM.trim() != '') {
    //                var managerAUM = Number(this.filteredFundList[i].managerAUM.toString().replace(/,/g, ''));
    //                if (this.filteredFundList[i].fundAUM != null) {
    //                    var fundAUM = Number(this.filteredFundList[i].fundAUM.toString().replace(/,/g, ''));
    //                    if (fundAUM > managerAUM) {
    //                        this.modalErrorMessage = "Manager AUM cannot be less than Fund AUM: " + this.filteredFundList[i].fundName;
    //                        this.modalSuccessMessage = null;
    //                        $('#modalMessagesDiv')[0].scrollIntoView({
    //                            block: "start",
    //                            behavior: "smooth"
    //                        });
    //
    //                        $('#' + this.filteredFundList[i].fundId).css({'background-color': '#dcdcdc'});
    //                        return;
    //                    } else {
    //                        $('#' + this.filteredFundList[i].fundId).css({'background-color': 'white'});
    //                    }
    //                }
    //            }
    //        }
    //    }
    //
    //    for(var i = 0; i < this.filteredFundList.length; i++) {
    //        if (this.filteredFundList[i].managerAUM != null) {
    //            this.filteredFundList[i].managerAUM = Number(this.filteredFundList[i].managerAUM.toString().replace(/,/g, ''));
    //            if(this.filteredFundList[i].managerAUM == 0){
    //                this.filteredFundList[i].managerAUM = null;
    //            }
    //        }
    //    }
    //    this.busyStats = this.screeningService.updateManagerAUM(this.filteredFundList)
    //        .subscribe(
    //            result => {
    //                //console.log(result);
    //
    //                for(var i = 0; i < this.filteredFundList.length; i++) {
    //                    if(this.filteredFundList[i].managerAUM != null) {
    //                        this.onNumberFund(this.filteredFundList[i]);
    //                    }
    //                }
    //                //this.showManagerAUMInput = false;
    //
    //                this.modalPostAction("Successfully updated Manager AUM. Need to reapply filters to see the changes.", null);
    //            },
    //            error => {
    //                this.modalPostAction(null,  "Failed to update Manager AUM");
    //            }
    //        );
    //}

    //saveManagerAUM(fund){
    //
    //    var fundAUM = fund.fundAUM != null ? Number(fund.fundAUM.toString().replace(/,/g, '')) : 0;
    //    var managerAUM = fund.managerAUM != null ? Number(fund.managerAUM.toString().replace(/,/g, '')) : 0;
    //    if(fund.managerAUM != null && fund.managerAUM != '') {
    //        if (fundAUM > managerAUM) {
    //            this.modalErrorMessage = "Manager AUM cannot be less than Fund AUM: " + fund.fundName;
    //            this.modalSuccessMessage = null;
    //            $('#modalMessagesDiv')[0].scrollIntoView({
    //                block: "start",
    //                behavior: "smooth"
    //            });
    //
    //            $('#' + fund.fundId).css({'background-color': '#dcdcdc'});
    //            return;
    //        } else {
    //            $('#' + fund.fundId).css({'background-color': 'white'});
    //        }
    //    }
    //
    //
    //    if(managerAUM == 0){
    //        fund.managerAUM = null;
    //    }
    //
    //    var updates = [fund];
    //    this.busyStats = this.screeningService.updateManagerAUM(updates)
    //        .subscribe(
    //            result => {
    //                //console.log(result);
    //
    //                for(var i = 0; i < this.filteredFundList.length; i++) {
    //                    if(this.filteredFundList[i].managerAUM != null) {
    //                        this.onNumberFund(this.filteredFundList[i]);
    //                    }
    //                }
    //                //this.showManagerAUMInput = false;
    //                fund.editing = false;
    //                this.managerAUMbeforeEdit = null;
    //
    //                this.modalPostAction("Successfully updated Manager AUM. Need to reapply filters to see the changes.", null);
    //            },
    //            error => {
    //                this.modalPostAction(null,  "Failed to update Manager AUM");
    //            }
    //        );
    //}

    //cancelManagerAUMUpdate(fund){
    //    fund.editing = false;
    //    fund.managerAUM = this.managerAUMbeforeEdit;
    //}




    closeFundListModal(){
        this.filteredFundList = null;

        if(this.needUpdate) {
            this.loadFilteredResult();

            this.needUpdate = false;
        }
    }

    closeFundEditModal(){

        if(this.selectedFund.added){
            if(this.needUpdate) {
                this.loadFilteredResult();
            }
            this.needUpdate = false;
        }else{
            if(this.needUpdate) {
                this.showFunds(this.fundListLookbackReturn, this.fundListLookbackAUM, this.fundListType, null);
                //this.needUpdate = false;
            }
        }
        this.selectedFund = null;

    }

    closeFundExcludeModal(){
        if(this.needUpdate) {
            if(this.excludeSource != null && this.excludeSource === 'FUNDLIST'){
                this.showFunds(this.fundListLookbackReturn, this.fundListLookbackAUM, this.fundListType, null);
                //this.needUpdate = false;
            }else if(this.excludeSource != null && this.excludeSource === 'CHECK'){
                this.loadFilteredResult();
            }
        }
        this.excludeFundModalSuccessMessage = null;
        this.excludeFundModalErrorMessage = null;
        this.excludeSource = null;
        this.selectedFund = null;
    }

    public onNumberChangeFundAUM(){
        if(this.filteredResult.fundAUM != null && this.filteredResult.fundAUM != 'undefined' && this.filteredResult.fundAUM.toString().length > 0) {
            if(this.filteredResult.fundAUM.toString()[this.filteredResult.fundAUM.toString().length - 1] != '.' || this.filteredResult.fundAUM.toString().split('.').length > 2){
                this.filteredResult.fundAUM = this.filteredResult.fundAUM.toString().replace(/,/g , '');
                if(this.filteredResult.fundAUM != '-'){
                    this.filteredResult.fundAUM = parseFloat(this.filteredResult.fundAUM).toLocaleString('en', {maximumFractionDigits: 2});
                }
            }
        }
    }
    public onNumberChangeManagerAUM(){
        if(this.filteredResult.managerAUM != null && this.filteredResult.managerAUM != 'undefined' && this.filteredResult.managerAUM.toString().length > 0) {
            if(this.filteredResult.managerAUM.toString()[this.filteredResult.managerAUM.toString().length - 1] != '.' || this.filteredResult.managerAUM.toString().split('.').length > 2){
                this.filteredResult.managerAUM = this.filteredResult.managerAUM.toString().replace(/,/g , '');
                if(this.filteredResult.managerAUM != '-'){
                    this.filteredResult.managerAUM = parseFloat(this.filteredResult.managerAUM).toLocaleString('en', {maximumFractionDigits: 2});
                }
            }
        }
    }

    public onNumberChangeSelectedFundAUM(){
        if(this.selectedFund.fundAUM != null && this.selectedFund.fundAUM != 'undefined' && this.selectedFund.fundAUM.toString().length > 0) {
            if(this.selectedFund.fundAUM.toString()[this.selectedFund.fundAUM.toString().length - 1] != '.' || this.selectedFund.fundAUM.toString().split('.').length > 2){
                this.selectedFund.fundAUM = this.selectedFund.fundAUM.toString().replace(/,/g , '');
                if(this.selectedFund.fundAUM != '-'){
                    this.selectedFund.fundAUM = parseFloat(this.selectedFund.fundAUM).toLocaleString('en', {maximumFractionDigits: 2});
                    if(this.selectedFund.fundAUM == 'NaN'){
                        this.selectedFund.fundAUM = null;
                    }
                }
            }
        }
    }
    public onNumberChangeSelectedEditedFundAUM(){
        if(this.selectedFund.editedFundAUM != null && this.selectedFund.editedFundAUM != 'undefined' && this.selectedFund.editedFundAUM.toString().length > 0) {
            if(this.selectedFund.editedFundAUM.toString()[this.selectedFund.editedFundAUM.toString().length - 1] != '.' || this.selectedFund.editedFundAUM.toString().split('.').length > 2){
                this.selectedFund.editedFundAUM = this.selectedFund.editedFundAUM.toString().replace(/,/g , '');
                if(this.selectedFund.editedFundAUM != '-'){
                    this.selectedFund.editedFundAUM = parseFloat(this.selectedFund.editedFundAUM).toLocaleString('en', {maximumFractionDigits: 2});
                    if(this.selectedFund.editedFundAUM == 'NaN'){
                        this.selectedFund.editedFundAUM = null;
                    }
                }
            }
        }
    }
    public onNumberChangeSelectedFundManagerAUM(){
        if(this.selectedFund.managerAUM != null && this.selectedFund.managerAUM != 'undefined' && this.selectedFund.managerAUM.toString().length > 0) {
            if(this.selectedFund.managerAUM.toString()[this.selectedFund.managerAUM.toString().length - 1] != '.' || this.selectedFund.managerAUM.toString().split('.').length > 2){
                this.selectedFund.managerAUM = this.selectedFund.managerAUM.toString().replace(/,/g , '');
                if(this.selectedFund.managerAUM != '-'){
                    this.selectedFund.managerAUM = parseFloat(this.selectedFund.managerAUM).toLocaleString('en', {maximumFractionDigits: 2});
                    if(this.selectedFund.managerAUM == 'NaN'){
                        this.selectedFund.managerAUM = null;
                    }
                }
            }
        }
    }

    //public onNumberFund(fund){
    //    //console.log(fund);
    //    if(fund.managerAUM != null && fund.managerAUM != 'undefined' && fund.managerAUM.toString().length > 0) {
    //        if(fund.managerAUM.toString()[fund.managerAUM.toString().length - 1] != '.' || fund.managerAUM.toString().split('.').length > 2){
    //            fund.managerAUM = fund.managerAUM.toString().replace(/,/g , '');
    //            if(fund.managerAUM != '-'){
    //                fund.managerAUM = parseFloat(fund.managerAUM).toLocaleString('en', {maximumFractionDigits: 2});
    //            }
    //        }
    //    }
    //}

    modalPostAction(successMessage, errorMessage){
        this.modalSuccessMessage = successMessage;
        this.modalErrorMessage = errorMessage;

        $('#modalMessagesDiv')[0].scrollIntoView({
            block: "start",
            behavior: "smooth"
        });
    }

    getNewCurrencyRateParams(fund){
        let params = {'date': fund.fundAUMDate, 'currency': fund.currency};
        return JSON.stringify(params);
    }

    generateTrackRecordMonths(){
        var dates = [];
        var currentDate = this.filteredResult.startDateMonth;
        dates.push(currentDate);

        while(dates.length != (this.filteredResult.trackRecord + this.filteredResult.lookbackReturns)){
            var month = currentDate.split(".")[0];
            var year = currentDate.split(".")[1];
            if(Number(month) == 1){
                var date = "12." + (year - 1);
                dates.push(date);
                currentDate = date;
            }else{
                var date = (month - 1 < 10 ? "0" + (month - 1) : (month - 1)) + "." + year;
                dates.push(date);
                currentDate = date;
            }
        }
        //console.log(dates);
        return dates;
    }

    generateTrackRecordMonthsYearList(){
        var dates = this.generateTrackRecordMonths();
        //console.log(dates);

        var datesByYears = [];
        var currentYear = dates[0].split(".")[1];
        var currentYearDates = [];
        for(var i = 0; i < dates.length; i++){
            if(currentYear != dates[i].split(".")[1]){
                while(currentYearDates.length < 12){
                    //currentYearDates.push(null);
                    currentYearDates.splice(0, 0, null);
                }
                datesByYears.push(currentYearDates);

                currentYear = dates[i].split(".")[1];
                currentYearDates = [];
            }
            currentYearDates.push(dates[i]);
        }
        if(currentYearDates.length > 0){
            while(currentYearDates.length < 12){
                currentYearDates.push(null);
            }
            datesByYears.push(currentYearDates);
        }
        //console.log(datesByYears);
        return datesByYears;
    }

    editFund(fund: HedgeFundScreeningFilteredResultFund){
        $('#modalMessagesDiv').css("background-color", "grey");

        this.selectedFund = new HedgeFundScreeningFilteredResultFund();
        if(fund != null) {
            this.selectedFund.clone(fund);
        }else{
            this.selectedFund.added = true;
        }

        //console.log(this.selectedFund);

        this.selectedFundErrorMessage = null;
        this.selectedFundSuccessMessage = null;

        this.onNumberChangeSelectedFundAUM();
        this.onNumberChangeSelectedEditedFundAUM();
        this.onNumberChangeSelectedFundManagerAUM();

        var returns = [];
        var datesByYear = this.generateTrackRecordMonthsYearList();
        for(var i = 0; i < datesByYear.length; i++){
            var yearReturns = [];
            for(var j = 0; j < datesByYear[i].length; j++){
                var value = null;
                if(this.selectedFund.returns != null && this.selectedFund.returns.length > 0) {
                    for (var k = 0; k < this.selectedFund.returns.length; k++) {
                        if(this.selectedFund.returns[k].date === datesByYear[i][j]){
                            value = this.selectedFund.returns[k].value;
                            break;
                        }
                    }
                }
                yearReturns.push({"date": datesByYear[i][j], "value": value});
            }
            returns.push(yearReturns);
        }
        this.selectedFundReturns = returns;
        //console.log(this.selectedFund.returns);

        // FIX, since rendering takes time, no element for datetimepicker() function call
        setTimeout(function(){
            $('#editedFundAUMDateTimePicker').datetimepicker({
                //defaultDate: new Date(),
                format: 'MM.YYYY'
            });
        }, 500);
    }

    saveFund(){
        if(this.selectedFund){
            if(this.selectedFund.fundName == null || this.selectedFund.fundName.trim() === ''){
                this.selectedFundErrorMessage = "Fund Name required";
                this.selectedFundSuccessMessage = null;
                return;
            }
            if(this.selectedFund.investmentManager == null || this.selectedFund.investmentManager.trim() === ''){
                            this.selectedFundErrorMessage = "Investment manager required";
                            this.selectedFundSuccessMessage = null;
                            return;
                        }
            if((this.selectedFund.fundAUM == null || this.selectedFund.fundAUM.trim() === '') &&
                (this.selectedFund.editedFundAUM == null || this.selectedFund.editedFundAUM.trim() === '')){
                this.selectedFundErrorMessage = "Fund AUM required";
                this.selectedFundSuccessMessage = null;
                return;
            }
            this.selectedFund.editedFundAUMDateMonthYear = $('#editedFundAUMDate').val();

            // RETURNS
            var flatReturns = this.selectedFundReturns.flat();
            var nonNullReturns = [];
            for(var i = 0; i < flatReturns.length; i++){
                if(flatReturns[i].date != null && flatReturns[i].value != null){
                    nonNullReturns.push(flatReturns[i]);
                }
            }
            this.selectedFund.returns = nonNullReturns;
/*
            if(this.selectedFund.added && this.selectedFund.returns.length < this.filteredResult.trackRecord){
                this.selectedFundErrorMessage = "Min required track record size is " + this.filteredResult.trackRecord +
                    " (" + this.selectedFund.returns.length + ")";
                this.selectedFundSuccessMessage = null;
                return;
            }
*/
            this.selectedFund.fundAUM = this.selectedFund.fundAUM != null ? Number(this.selectedFund.fundAUM.toString().replace(/,/g, '')) : 0;

            // Added fund
            if(this.selectedFund.added) {
                this.selectedFund.screening = {"id": this.filteredResult.screeningId};
                // Check AUM
                var minFundAUM = this.filteredResult.fundAUM != null ? Number(this.filteredResult.fundAUM.toString().replace(/,/g, '')) : 0;

/*                if (this.selectedFund.fundAUM < minFundAUM) {
                    this.selectedFundErrorMessage = "Qualified Fund AUM must be greater than min AUM parameter: " + super.getAmountShort(minFundAUM);
                    this.selectedFundSuccessMessage = null;
                    return;
                }
*/

                // TODO: Check track record
            }
            this.selectedFund.filteredResultId = this.filteredResult.id;

            // Edited Fund AUM
            if(this.selectedFund.editedFundAUM != null && this.selectedFund.editedFundAUM != ''){
                var editedFundAUM = this.selectedFund.editedFundAUM != null ? Number(this.selectedFund.editedFundAUM.toString().replace(/,/g, '')) : 0;
                if(editedFundAUM < 0){
                    this.selectedFundErrorMessage = "AUM cannot be negative";
                    this.selectedFundSuccessMessage = null;
                    return;
                }else if(editedFundAUM > 0 && (this.selectedFund.editedFundAUMComment == null || this.selectedFund.editedFundAUMComment == '')){
                    this.selectedFundErrorMessage = "Comment required when updating fund AUM";
                    this.selectedFundSuccessMessage = null;
                    return;
                }else if(editedFundAUM > 0 && (this.selectedFund.editedFundAUMDateMonthYear == null || this.selectedFund.editedFundAUMDateMonthYear == '')){
                    this.selectedFundErrorMessage = "Date required when updating fund AUM";
                    this.selectedFundSuccessMessage = null;
                    return;
                }
                if(editedFundAUM == 0){
                    this.selectedFund.editedFundAUM= null;
                }else{
                    this.selectedFund.editedFundAUM = editedFundAUM;
                }
            }

            // Manager AUM
            if(this.selectedFund.managerAUM != null && this.selectedFund.managerAUM != '') {
                var managerAUM = this.selectedFund.managerAUM != null ? Number(this.selectedFund.managerAUM.toString().replace(/,/g, '')) : 0;
                if (managerAUM < 0) {
                    this.selectedFundErrorMessage = "AUM cannot be negative";
                    return;
                }
                if(managerAUM == 0){
                    this.selectedFund.managerAUM= null;
                }else{
                    this.selectedFund.managerAUM = managerAUM;
                }
                if(this.selectedFund.fundAUM != null){
                    var fundAUM = this.selectedFund.fundAUM != null ? Number(this.selectedFund.fundAUM.toString().replace(/,/g, '')) : 0;
                    if(managerAUM < fundAUM){
                        this.selectedFundErrorMessage = "Manager AUM cannot be less than fund AUM";
                        this.selectedFundSuccessMessage = null;
                        return;
                    }
                }
                if(this.selectedFund.editedFundAUM != null){
                    var editedFundAUM = this.selectedFund.editedFundAUM != null ? Number(this.selectedFund.editedFundAUM.toString().replace(/,/g, '')) : 0;
                    if(managerAUM < editedFundAUM){
                        this.selectedFundErrorMessage = "Manager AUM cannot be less than fund AUM";
                        this.selectedFundSuccessMessage = null;
                        return;
                    }
                }
            }
            //console.log(this.selectedFund);
            // Save fund info
            this.busyFundEdit = this.screeningService.updateFund(this.selectedFund)
                .subscribe(
                    result => {
                        //console.log(result);
                        //this.showFunds(this.fundListLookbackReturn, this.fundListLookbackAUM, this.fundListType, null);

                        this.selectedFundSuccessMessage = "Successfully updated fund info."
                        this.selectedFundErrorMessage = null;

                        this.needUpdate = true;
                    },
                    error => {
                        this.selectedFundErrorMessage = "Failed to update fund info";
                        this.selectedFundSuccessMessage = null;
                    }
                );
        }
    }

    removeFund(fund){
        if(fund.added){
            if(confirm("Are you sure want to delete fund?")){
                var fundShort = new HedgeFundScreeningFilteredResultFund();
                fundShort.fundName = fund.fundName;
                fundShort.filteredResultId = this.filteredResult.id;
                this.busyFundEdit = this.screeningService.deleteFund(fundShort)
                    .subscribe(
                        result => {
                            this.successMessage = "Successfully deleted fund."
                            this.errorMessage = null;

                            this.loadFilteredResult();

                            this.postAction(this.successMessage, this.errorMessage);
                        },
                        error => {
                            this.successMessage = "Failed to delete fund info";
                            this.errorMessage = null;

                            this.postAction(this.successMessage, this.errorMessage);
                        }
                    );
            }
        }
    }

    openExcludeFund(fund, source){
        $('#modalMessagesDiv').css("background-color", "grey");
        this.selectedFund = fund;
        this.excludeSource = source;
        //console.log(this.selectedFund);
    }

    excludeFund(){
        //console.log(this.selectedFund);
        if(this.selectedFund.excludeComment == null || this.selectedFund.excludeComment.trim() === ''){
            this.excludeFundModalErrorMessage = "Comment required";
            this.excludeFundModalSuccessMessage = null;
            return;
        }
        if(confirm("Are you sure want to exclude fund " + this.selectedFund.fundName + " ?")){
            var fundShort = new HedgeFundScreeningFilteredResultFund();
            fundShort.fundId= this.selectedFund.fundId;
            fundShort.excludeComment = this.selectedFund.excludeComment;
            fundShort.screening = new HedgeFundScreening();
            fundShort.screening.id = this.screeningId;
            fundShort.filteredResultId = this.filteredResult.id;
            fundShort.excludeFromStrategyAUM = this.selectedFund.excludeFromStrategyAUM;
            console.log(this.selectedFund);
            this.busyModal = this.screeningService.excludeFund(fundShort)
                .subscribe(
                    result => {
                        this.excludeFundModalSuccessMessage = "Successfully excluded fund."
                        this.excludeFundModalErrorMessage = null;

                        this.needUpdate = true;

                        this.postAction(this.excludeFundModalSuccessMessage, this.excludeFundModalErrorMessage);
                    },
                    error => {
                        this.excludeFundModalErrorMessage = "Failed to exclude fund info";
                        this.excludeFundModalSuccessMessage = null;

                        this.postAction(this.excludeFundModalSuccessMessage, this.excludeFundModalErrorMessage);
                    }
                );
            if(this.excludeSource == null || this.excludeSource === 'FUNDLIST'){
                this.showFunds(this.fundListLookbackReturn, this.fundListLookbackAUM, this.fundListType, null);
            }
        }else{

        }
    }

    includeFund(fund){
        if(confirm("Are you sure want to include fund " + fund.fundName + " ?")){

            var fundShort = new HedgeFundScreeningFilteredResultFund();
            fundShort.fundId= fund.fundId;
            fundShort.screening = new HedgeFundScreening();
            fundShort.screening.id = this.screeningId;
            fundShort.filteredResultId = this.filteredResult.id;
            this.busyModal = this.screeningService.includeFund(fundShort)
                .subscribe(
                    result => {
                        this.successMessage = "Successfully included fund."
                        this.errorMessage = null;

                        this.loadFilteredResult();

                        this.postAction(this.successMessage, this.errorMessage);
                    },
                    error => {
                        this.successMessage = "Failed to include fund info";
                        this.errorMessage = null;

                        this.postAction(this.successMessage, this.errorMessage);
                    }
                );
            this.showFunds(this.fundListLookbackReturn, this.fundListLookbackAUM, this.fundListType, null);
        }else{

        }
    }
    closeReturnsModal(){
        //console.log("closeReturnsModal");
        this.uploadedReturns = "";
        this.returnUploadSuccessMessage = null;
        this.returnUploadErrorMessage = null;
    }

    parseReturns(){
        var fundReturns = [];
        var rows = this.uploadedReturns.split("\n");

        for(var i = 0; i < rows.length; i++){
            if(rows[i].trim() === ""){
                continue;
            }
            var row = rows[i].split("\t");
            if(row.length != 2){
                this.returnUploadSuccessMessage = null;
                this.returnUploadErrorMessage = "Invalid returns format";
                return;
            }
            if(row[0] == null || row[0] === 'undefined' || row[0].split(".").length != 3){
                this.returnUploadSuccessMessage = null;
                this.returnUploadErrorMessage = "Invalid return format - date";
                return;
            }
            var day = row[0].split(".")[0];
            var month = row[0].split(".")[1];
            var year = row[0].split(".")[2];
            if(Number(month) < 1 || Number(month) > 12){
                this.returnUploadErrorMessage = "Invalid return date format: month must be 1-12";
                this.returnUploadSuccessMessage = null;
                return;
            }else if(Number(day) < 1 || Number(day) > 31){
                this.returnUploadErrorMessage = "Invalid return date format: day must be 1-31";
                this.returnUploadSuccessMessage = null;
                return;
            }
            var returnValue = row[1].replace(/,/g, '.');
            returnValue = returnValue.replace('%', '');
            fundReturns.push({"date": month + '.' + year, "value": parseFloat(Number(returnValue)/100).toFixed(12)});
        }
        //console.log(fundReturns);
        var returns = [];
        var datesByYear = this.generateTrackRecordMonthsYearList();
        for(var i = 0; i < datesByYear.length; i++){
            var yearReturns = [];
            for(var j = 0; j < datesByYear[i].length; j++){
                var value = null;
                if(fundReturns != null && fundReturns.length > 0) {
                    for (var k = 0; k < fundReturns.length; k++) {
                        if(fundReturns[k].date === datesByYear[i][j]){
                            value = fundReturns[k].value;
                            break;
                        }
                    }
                }
                yearReturns.push({"date": datesByYear[i][j], "value": value});
            }
            returns.push(yearReturns);
        }
        this.selectedFundReturns = returns;

        //console.log(returns);

        this.returnUploadErrorMessage = null;
        this.returnUploadSuccessMessage = "Returns added";

    }

    saveScoringResults(fundListLookbackAUM, fundListLookbackReturn){
        if(confirm("Are you sure want to save results?"){
            let saveParams = {"filteredResultId": this.filteredResult.id, "lookbackReturn": fundListLookbackReturn, "lookbackAUM": fundListLookbackAUM};
            this.busyModal = this.screeningService.saveResults(saveParams)
                .subscribe(
                    result => {
                        if(result.status != null && result.status === 'SUCCESS'){
                            this.modalSuccessMessage = "Successfully saved results"
                            this.modalErrorMessage = null;
                            //this.filteredResult.editable = false;
                            this.needUpdate = true;
                        }else{
                            console.log(result);
                            this.modalSuccessMessage = null;
                            this.modalErrorMessage = "Failed to save results";
                            if(result.message != null && result.message.nameEn != null && result.message.nameEn.trim() != ''{
                                this.modalErrorMessage = result.message.nameEn;
                            }
                        }
                    },
                    error => {
                        this.modalErrorMessage = "Failed to save results";
                        this.modalSuccessMessage = null;
                    }
                );
        }
    }


    deleteSavedResults(){
        if(confirm("Are you sure want to delete saved results?"){
            this.busyGet = this.screeningService.deleteSavedResults(this.filteredResult.filteredResultStatistics.finalResults.id)
                .subscribe(
                    result => {
                        console.log(result);
                        if(result.status != null && result.status === 'SUCCESS'){
                            this.successMessage = "Successfully deleted saved results"
                            this.errorMessage = null;
                            this.loadFilteredResult();
                        }else{
                            this.successMessage = null;
                            this.errorMessage = "Failed to delete saved results";
                            if(result.message != null && result.message.nameEn != null && result.message.nameEn.trim() != ''){
                                this.errorMessage = result.message.nameEn;
                            }
                        }
                    },
                    error => {
                        this.errorMessage = "Failed to delete saved results";
                        this.successMessage = null;
                    }
                );
        }
    }
}