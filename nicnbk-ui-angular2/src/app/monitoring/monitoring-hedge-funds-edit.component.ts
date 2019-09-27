import {Component, OnInit, AfterViewChecked} from "@angular/core";
import {ActivatedRoute, Router} from "@angular/router";
import {CommonFormViewComponent} from "../common/common.component";
import {GoogleChartComponent} from "../google-chart/google-chart.component";
import {MonitoringHFData} from "./model/monitoring-hf-data";
import {Subscription} from 'rxjs';
import {MonitoringHedgeFundService} from "./monitoring-hf.service";
import {MonitoringHFDataHolder} from "./model/monitoring-hf-data-holder";
import {ErrorResponse} from "../common/error-response";
import {MonitoringHFDataOverall} from "./model/monitoring-hf-data-overall";
import {MonitoringHFDataClassA} from "./model/monitoring-hf-data-classA";
import {MonitoringHFDataClassB} from "./model/monitoring-hf-data-classB";
import {MonitoringHFDataApprovedFundInfo} from "./model/monitoring-hf-data-approved-fund-info";
import {MonitoringHFDataNameTextValue} from "./model/monitoring-hf-data-name-text-value";
import {MonitoringHFDataTopFundInfo} from "./model/monitoring-hf-data-top-fund-info";
import {MonitoringHFDataNameNumericValue} from "./model/monitoring-hf-data-name-numeric-value";

declare var google:any;
declare var $: any;

@Component({
    selector: 'monitoring-hedge-funds-edit',
    templateUrl: 'view/monitoring-hedge-funds-edit.component.html',
    styleUrls: [],
    providers: [],
})
export class MonitoringHedgeFundsEditComponent extends CommonFormViewComponent implements OnInit, AfterViewChecked{
    private sub: any;
    monitoringDate;
    monitoringId;
    busy: Subscription;

    activeTab = "OVERALL";

    selectedData = new MonitoringHFDataHolder;

    constructor(
        private route: ActivatedRoute,
        private router: Router,
        private monitoringHFService: MonitoringHedgeFundService
    ) {
        super(router);
        this.sub = this.route
            .params
            .subscribe(params => {
                if(params['params'] != null) {
                    this.monitoringDate = JSON.parse(params['params']).monitoringDate;
                    this.monitoringId = JSON.parse(params['params']).monitoringId;
                }
                this.selectedData.date = this.monitoringDate;
                //console.log(this.monitoringId);
                if(this.monitoringId) {
                    this.busy = this.monitoringHFService.get(JSON.parse(params['params']))
                        .subscribe(
                            monitoringData => {
                                console.log(monitoringData);
                                this.selectedData = monitoringData;
                                if(this.selectedData.monitoringData.approvedFunds != null && this.selectedData.monitoringData.approvedFunds.length > 0){
                                    for(var i = 0; i < this.selectedData.monitoringData.approvedFunds.length; i++) {
                                        $('#approveDateDTPicker' + i).datetimepicker({
                                            //defaultDate: new Date(),
                                            format: 'DD-MM-YYYY'
                                        });
                                    }
                                }
                            },
                            (error: ErrorResponse) => {
                                this.errorMessage = "Error loading monitoring date";
                                if(error && !error.isEmpty()){
                                    this.processErrorMessage(error);
                                }
                                this.postAction(null, null);
                            }
                        );
                }else{
                    this.selectedData = new MonitoringHFDataHolder;
                    this.setDefaultValues();
                }
            });
    }

    ngOnInit():any {
        // TODO: exclude jQuery
        // datetimepicker
        $('#monitoringDateDTPickeer').datetimepicker({
            //defaultDate: new Date(),
            format: 'DD-MM-YYYY'
        });
    }

    ngAfterViewChecked(){
        if(this.selectedData != null && this.selectedData.monitoringData != null && this.selectedData.monitoringData.approvedFunds != null){
            for(var i = 0; i < this.selectedData.monitoringData.approvedFunds.length; i++) {
                $('#approveDateDTPicker' + i).datetimepicker({
                    //defaultDate: new Date(),
                    format: 'DD-MM-YYYY'
                });
            }
        }
    }

    private setDefaultValues(){
        this.selectedData.monitoringData = new MonitoringHFData();

        // OVERALL
        this.selectedData.monitoringData.overall = new MonitoringHFDataOverall();
        ///////////General Information
        this.selectedData.monitoringData.overall.generalInformation = [];
        var info1 = new MonitoringHFDataNameTextValue();
        info1.name = "AUM of the program –total";
        //info1.value = "600 million USD";

        var info2 = new MonitoringHFDataNameTextValue();
        info2.name = "AUM - invested";

        var info3 = new MonitoringHFDataNameTextValue();
        info3.name = "NAV";

        var info4 = new MonitoringHFDataNameTextValue();
        info4.name = "AUM – uninvested";

        var info5 = new MonitoringHFDataNameTextValue();
        info5.name = "Number of funds";

        var info6 = new MonitoringHFDataNameTextValue();
        info6.name = "Mandate";

        this.selectedData.monitoringData.overall.generalInformation.push(info1);
        this.selectedData.monitoringData.overall.generalInformation.push(info2);
        this.selectedData.monitoringData.overall.generalInformation.push(info3);
        this.selectedData.monitoringData.overall.generalInformation.push(info4);
        this.selectedData.monitoringData.overall.generalInformation.push(info5);
        this.selectedData.monitoringData.overall.generalInformation.push(info6);

        // CLASS A
        this.selectedData.monitoringData.classA = new MonitoringHFDataClassA();
        ///////////// GENERAL INFORMATION
        this.selectedData.monitoringData.classA.generalInformation = [];

        var infoA1 = new MonitoringHFDataNameTextValue();
        infoA1.name = "AUM - invested";

        var infoA2 = new MonitoringHFDataNameTextValue();
        infoA2.name = "NAV";

        var infoA3 = new MonitoringHFDataNameTextValue();
        infoA3.name = "Inception";

        var infoA4 = new MonitoringHFDataNameTextValue();
        infoA4.name = "Number of funds";

        var infoA5 = new MonitoringHFDataNameTextValue();
        infoA5.name = "Strategy";

        this.selectedData.monitoringData.classA.generalInformation.push(infoA1);
        this.selectedData.monitoringData.classA.generalInformation.push(infoA2);
        this.selectedData.monitoringData.classA.generalInformation.push(infoA3);
        this.selectedData.monitoringData.classA.generalInformation.push(infoA4);
        this.selectedData.monitoringData.classA.generalInformation.push(infoA5);

        ///////////// TOP 5 POSITIVE CONTRIBUTORS
        this.selectedData.monitoringData.classA.positiveContributors = [];
        this.selectedData.monitoringData.classA.positiveContributors.push(new MonitoringHFDataTopFundInfo());
        this.selectedData.monitoringData.classA.positiveContributors.push(new MonitoringHFDataTopFundInfo());
        this.selectedData.monitoringData.classA.positiveContributors.push(new MonitoringHFDataTopFundInfo());
        this.selectedData.monitoringData.classA.positiveContributors.push(new MonitoringHFDataTopFundInfo());
        this.selectedData.monitoringData.classA.positiveContributors.push(new MonitoringHFDataTopFundInfo());

        ///////////// TOP 5 NEGATIVE CONTRIBUTORS
        this.selectedData.monitoringData.classA.negativeContributors = [];
        this.selectedData.monitoringData.classA.negativeContributors.push(new MonitoringHFDataTopFundInfo());
        this.selectedData.monitoringData.classA.negativeContributors.push(new MonitoringHFDataTopFundInfo());
        this.selectedData.monitoringData.classA.negativeContributors.push(new MonitoringHFDataTopFundInfo());
        this.selectedData.monitoringData.classA.negativeContributors.push(new MonitoringHFDataTopFundInfo());
        this.selectedData.monitoringData.classA.negativeContributors.push(new MonitoringHFDataTopFundInfo());

        ///////////// TOP 10 ALLOCATIONS
        this.selectedData.monitoringData.classA.fundAllocations = [];
        this.selectedData.monitoringData.classA.fundAllocations.push(new MonitoringHFDataTopFundInfo());
        this.selectedData.monitoringData.classA.fundAllocations.push(new MonitoringHFDataTopFundInfo());
        this.selectedData.monitoringData.classA.fundAllocations.push(new MonitoringHFDataTopFundInfo());
        this.selectedData.monitoringData.classA.fundAllocations.push(new MonitoringHFDataTopFundInfo());
        this.selectedData.monitoringData.classA.fundAllocations.push(new MonitoringHFDataTopFundInfo());
        this.selectedData.monitoringData.classA.fundAllocations.push(new MonitoringHFDataTopFundInfo());
        this.selectedData.monitoringData.classA.fundAllocations.push(new MonitoringHFDataTopFundInfo());
        this.selectedData.monitoringData.classA.fundAllocations.push(new MonitoringHFDataTopFundInfo());
        this.selectedData.monitoringData.classA.fundAllocations.push(new MonitoringHFDataTopFundInfo());
        this.selectedData.monitoringData.classA.fundAllocations.push(new MonitoringHFDataTopFundInfo());



        // CLASS B
        this.selectedData.monitoringData.classB = new MonitoringHFDataClassB();
        ///////////// GENERAL INFORMATION
        this.selectedData.monitoringData.classB.generalInformation = [];

        var infoB1 = new MonitoringHFDataNameTextValue();
        infoB1.name = "AUM - invested";

        var infoB2 = new MonitoringHFDataNameTextValue();
        infoB2.name = "NAV";

        var infoB3 = new MonitoringHFDataNameTextValue();
        infoB3.name = "Inception";

        var infoB4 = new MonitoringHFDataNameTextValue();
        infoB4.name = "Number of funds";

        var infoB5 = new MonitoringHFDataNameTextValue();
        infoB5.name = "Strategy";

        this.selectedData.monitoringData.classB.generalInformation.push(infoB1);
        this.selectedData.monitoringData.classB.generalInformation.push(infoB2);
        this.selectedData.monitoringData.classB.generalInformation.push(infoB3);
        this.selectedData.monitoringData.classB.generalInformation.push(infoB4);
        this.selectedData.monitoringData.classB.generalInformation.push(infoB5);

        ///////////// TOP 10 ALLOCATIONS
        this.selectedData.monitoringData.classB.fundAllocations = [];
        this.selectedData.monitoringData.classB.fundAllocations.push(new MonitoringHFDataTopFundInfo());
        //this.selectedData.monitoringData.classB.fundAllocations.push(new MonitoringHFDataTopFundInfo());
        //this.selectedData.monitoringData.classB.fundAllocations.push(new MonitoringHFDataTopFundInfo());
        //this.selectedData.monitoringData.classB.fundAllocations.push(new MonitoringHFDataTopFundInfo());
        //this.selectedData.monitoringData.classB.fundAllocations.push(new MonitoringHFDataTopFundInfo());

        // APPROVED FUNDS
        this.selectedData.monitoringData.approvedFunds = [];

    }

    removeOverallGeneralInfo(element){
        for(var i = 0; i < this.selectedData.monitoringData.overall.generalInformation.length; i++){
            if(this.selectedData.monitoringData.overall.generalInformation[i] === element) {
                this.selectedData.monitoringData.overall.generalInformation.splice(i, 1);
            }
        }
    }

    addOverallGeneralInfo(){
        if(this.selectedData.monitoringData.overall.generalInformation == null){
            this.selectedData.monitoringData.overall.generalInformation = [];
        }
        this.selectedData.monitoringData.overall.generalInformation.push(new MonitoringHFDataNameTextValue());
    }

    removeOverallContributionToReturn(element){
        for(var i = 0; i < this.selectedData.monitoringData.overall.contributionToReturn.length; i++){
            if(this.selectedData.monitoringData.overall.contributionToReturn[i] === element) {
                this.selectedData.monitoringData.overall.contributionToReturn.splice(i, 1);
            }
        }
    }

    addOverallContributionToReturn(){
        if(this.selectedData.monitoringData.overall.contributionToReturn == null){
            this.selectedData.monitoringData.overall.contributionToReturn = [];
        }
        this.selectedData.monitoringData.overall.contributionToReturn.push(new MonitoringHFDataNameNumericValue());
    }

    removeOverallAllocationByStrategy(element){
        for(var i = 0; i < this.selectedData.monitoringData.overall.allocationByStrategy.length; i++){
            if(this.selectedData.monitoringData.overall.allocationByStrategy[i] === element) {
                this.selectedData.monitoringData.overall.allocationByStrategy.splice(i, 1);
            }
        }
    }

    addOverallAllocationByStrategy(){
        if(this.selectedData.monitoringData.overall.allocationByStrategy == null){
            this.selectedData.monitoringData.overall.allocationByStrategy = [];
        }
        this.selectedData.monitoringData.overall.allocationByStrategy.push(new MonitoringHFDataNameNumericValue());
    }

    removeClassAGeneralInfo(element){
        for(var i = 0; i < this.selectedData.monitoringData.classA.generalInformation.length; i++){
            if(this.selectedData.monitoringData.classA.generalInformation[i] === element) {
                this.selectedData.monitoringData.classA.generalInformation.splice(i, 1);
            }
        }
    }

    addClassAGeneralInfo(){
        if(this.selectedData.monitoringData.classA.generalInformation == null){
            this.selectedData.monitoringData.classA.generalInformation = [];
        }
        this.selectedData.monitoringData.classA.generalInformation.push(new MonitoringHFDataNameTextValue());
    }

    removeClassAPositiveContributor(element){
        for(var i = 0; i < this.selectedData.monitoringData.classA.positiveContributors.length; i++){
            if(this.selectedData.monitoringData.classA.positiveContributors[i] === element) {
                this.selectedData.monitoringData.classA.positiveContributors.splice(i, 1);
            }
        }
    }

    addClassAPositiveContributor(){
        if(this.selectedData.monitoringData.classA.positiveContributors == null){
            this.selectedData.monitoringData.classA.positiveContributors = [];
        }
        this.selectedData.monitoringData.classA.positiveContributors.push(new MonitoringHFDataTopFundInfo());
    }

    removeClassANegativeContributor(element){
        for(var i = 0; i < this.selectedData.monitoringData.classA.negativeContributors.length; i++){
            if(this.selectedData.monitoringData.classA.negativeContributors[i] === element) {
                this.selectedData.monitoringData.classA.negativeContributors.splice(i, 1);
            }
        }
    }

    addClassANegativeContributor(){
        if(this.selectedData.monitoringData.classA.negativeContributors == null){
            this.selectedData.monitoringData.classA.negativeContributors = [];
        }
        this.selectedData.monitoringData.classA.negativeContributors.push(new MonitoringHFDataTopFundInfo());
    }

    removeClassAFundAllocation(element){
        for(var i = 0; i < this.selectedData.monitoringData.classA.fundAllocations.length; i++){
            if(this.selectedData.monitoringData.classA.fundAllocations[i] === element) {
                this.selectedData.monitoringData.classA.fundAllocations.splice(i, 1);
            }
        }
    }

    addClassAFundAllocation(){
        if(this.selectedData.monitoringData.classA.fundAllocations == null){
            this.selectedData.monitoringData.classA.fundAllocations = [];
        }
        this.selectedData.monitoringData.classA.fundAllocations.push(new MonitoringHFDataTopFundInfo());
    }

    removeClassAAllocationByStrategy(element){
        for(var i = 0; i < this.selectedData.monitoringData.classA.allocationByStrategy.length; i++){
            if(this.selectedData.monitoringData.classA.allocationByStrategy[i] === element) {
                this.selectedData.monitoringData.classA.allocationByStrategy.splice(i, 1);
            }
        }
    }

    addClassAAllocationByStrategy(){
        if(this.selectedData.monitoringData.classA.allocationByStrategy == null){
            this.selectedData.monitoringData.classA.allocationByStrategy = [];
        }
        this.selectedData.monitoringData.classA.allocationByStrategy.push(new MonitoringHFDataNameNumericValue());
    }

    removeClassBGeneralInfo(element){
        for(var i = 0; i < this.selectedData.monitoringData.classB.generalInformation.length; i++){
            if(this.selectedData.monitoringData.classB.generalInformation[i] === element) {
                this.selectedData.monitoringData.classB.generalInformation.splice(i, 1);
            }
        }
    }

    addClassBGeneralInfo(){
        if(this.selectedData.monitoringData.classB.generalInformation == null){
            this.selectedData.monitoringData.classB.generalInformation = [];
        }
        this.selectedData.monitoringData.classB.generalInformation.push(new MonitoringHFDataNameTextValue());
    }

    removeClassBFundAllocationByStrategy(element){
        for(var i = 0; i < this.selectedData.monitoringData.classB.fundAllocations.length; i++){
            if(this.selectedData.monitoringData.classB.fundAllocations[i] === element) {
                this.selectedData.monitoringData.classB.fundAllocations.splice(i, 1);
            }
        }
    }

    addClassBFundAllocationByStrategy(){
        if(this.selectedData.monitoringData.classB.fundAllocations == null){
            this.selectedData.monitoringData.classB.fundAllocations = [];
        }
        this.selectedData.monitoringData.classB.fundAllocations.push(new MonitoringHFDataTopFundInfo());
    }


    removeClassBAllocationByStrategy(element){
        for(var i = 0; i < this.selectedData.monitoringData.classB.allocationByStrategy.length; i++){
            if(this.selectedData.monitoringData.classB.allocationByStrategy[i] === element) {
                this.selectedData.monitoringData.classB.allocationByStrategy.splice(i, 1);
            }
        }
    }

    addClassBAllocationByStrategy(){
        if(this.selectedData.monitoringData.classB.allocationByStrategy == null){
            this.selectedData.monitoringData.classB.allocationByStrategy = [];
        }
        this.selectedData.monitoringData.classB.allocationByStrategy.push(new MonitoringHFDataNameNumericValue());
    }

    removeApprovedFund(element){
        for(var i = 0; i < this.selectedData.monitoringData.approvedFunds.length; i++){
            if(this.selectedData.monitoringData.approvedFunds[i] === element) {
                this.selectedData.monitoringData.approvedFunds.splice(i, 1);
            }
        }
    }

    addApprovedFund(){
        if(this.selectedData.monitoringData.approvedFunds == null){
            this.selectedData.monitoringData.approvedFunds = [];
        }
        this.selectedData.monitoringData.approvedFunds.push(new MonitoringHFDataApprovedFundInfo());
    }


    save(){
        this.selectedData.date = $('#monitoringDate').val();
        if(this.selectedData.monitoringData.approvedFunds != null && this.selectedData.monitoringData.approvedFunds.length > 0){
            for(var i = 0; i < this.selectedData.monitoringData.approvedFunds.length; i++){
                this.selectedData.monitoringData.approvedFunds[i].approveDate = $('#approveDate' + i).val();
            }
        }
        console.log(this.selectedData);
        //if(!this.checkDataFields()){
        //    return;
        //}
        // save
        this.busy = this.monitoringHFService.save(this.selectedData)
            .subscribe(
                response  => {
                    this.postAction("Successfully saved monitoring data", null);
                },
                (error: ErrorResponse) => {
                    this.successMessage = null;
                    this.errorMessage = "Error saving monitoring data";
                    if(error && !error.isEmpty()){
                        this.processErrorMessage(error);
                    }else {
                        this.postAction(null, this.errorMessage);
                    }
                }
            );
    }

    copyFromPreviousOverall(){
        if(confirm("Are you sure want to copy data from most recent date?")) {
            this.busy = this.monitoringHFService.getPreviousData({"type": "OVERALL"})
                .subscribe(
                    monitoringData => {
                        console.log(monitoringData);
                        this.selectedData.monitoringData.overall = monitoringData.overall;
                        this.successMessage = "Successfully loaded previous date OVERALL data.";
                    },
                    (error:ErrorResponse) => {
                        this.errorMessage = "Error loading monitoring data (previous)";
                        if (error && !error.isEmpty()) {
                            this.processErrorMessage(error);
                        }
                        this.postAction(null, null);
                    }
                );
        }
    }
    copyFromPreviousClassA(){
        if(confirm("Are you sure want to copy data from most recent date?")) {
            this.busy = this.monitoringHFService.getPreviousData({"type": "CLASS_A"})
                .subscribe(
                    monitoringData => {
                        console.log(monitoringData);
                        this.selectedData.monitoringData.classA = monitoringData.classA;
                        this.successMessage = "Successfully loaded previous date CLASS A data.";
                    },
                    (error:ErrorResponse) => {
                        this.errorMessage = "Error loading monitoring data (previous)";
                        if (error && !error.isEmpty()) {
                            this.processErrorMessage(error);
                        }
                        this.postAction(null, null);
                    }
                );
        }
    }
    copyFromPreviousClassB(){
        if(confirm("Are you sure want to copy data from most recent date?")) {
            this.busy = this.monitoringHFService.getPreviousData({"type": "CLASS_B"})
                .subscribe(
                    monitoringData => {
                        console.log(monitoringData);
                        this.selectedData.monitoringData.classB = monitoringData.classB;
                        this.successMessage = "Successfully loaded previous date CLASS B data.";
                    },
                    (error:ErrorResponse) => {
                        this.errorMessage = "Error loading monitoring data (previous)";
                        if (error && !error.isEmpty()) {
                            this.processErrorMessage(error);
                        }
                        this.postAction(null, null);
                    }
                );
        }
    }
    copyFromPreviousApprovedFunds(){
        if(confirm("Are you sure want to copy data from most recent date?")) {
            this.busy = this.monitoringHFService.getPreviousData({"type": "APPROVED_FUNDS"})
                .subscribe(
                    monitoringData => {
                        console.log(monitoringData);
                        this.selectedData.monitoringData.approvedFunds = monitoringData.approvedFunds;
                        this.successMessage = "Successfully loaded previous date APPROVED FUNDS data.";
                        if (this.selectedData.monitoringData.approvedFunds != null && this.selectedData.monitoringData.approvedFunds.length > 0) {
                            for (var i = 0; i < this.selectedData.monitoringData.approvedFunds.length; i++) {
                                $('#approveDateDTPicker' + i).datetimepicker({
                                    //defaultDate: new Date(),
                                    format: 'DD-MM-YYYY'
                                });
                            }
                        }
                    },
                    (error:ErrorResponse) => {
                        this.errorMessage = "Error loading monitoring data (previous)";
                        if (error && !error.isEmpty()) {
                            this.processErrorMessage(error);
                        }
                        this.postAction(null, null);
                    }
                );
        }
    }

    checkDataFields(){
        return true;
        if(this.selectedData == null || this.selectedData.monitoringData == null){
            this.postAction(null, "No data to save");
            return false;
        }
        if(this.selectedData.date == null || this.selectedData.date === ''){
            this.postAction(null, "Date required");
            return false;
        }

        // check overall
        if(this.selectedData.monitoringData.overall == null){
            this.postAction(null, "OVERALL tab data required")
            return false;
        }
        if(this.selectedData.monitoringData.overall.generalInformation == null || this.selectedData.monitoringData.overall.generalInformation.length == 0){
            this.postAction(null, "OVERALL - GENERAL INFORMATION data required")''
            return false;
        }else{
            // TODO: check general information data
        }

        if(this.selectedData.monitoringData.overall.contributionToReturn == null || this.selectedData.monitoringData.overall.contributionToReturn.length == 0){
            this.postAction(null, "OVERALL - CONTRIBUTION TO RETURN data required");
            return false;
        }else{
            // TODO: check contribution to return data

        }
        if(this.selectedData.monitoringData.overall.generalInformation == null || this.selectedData.monitoringData.overall.generalInformation.length == 0){
            this.postAction(null, "OVERALL - ALLOCATION BY STRATEGY data required");
            return false;
        }else{
            // TODO: check allocation by strategy data

        }



        // check class A

        // check class B

        // check approved funds

    }
}