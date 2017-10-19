import {Component, ViewChild, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {CommonFormViewComponent} from "../common/common.component";
import {PEFund} from "./model/pe.fund";
import {PEFirmService} from "./pe.firm.service";
import {PEFirm} from "./model/pe.firm";
import {PEFundService} from "./pe.fund.service";
import {SaveResponse} from "../common/save-response";
import {LookupService} from "../common/lookup.service";

import {Subscription} from 'rxjs';
import {ModuleAccessCheckerService} from "../authentication/module.access.checker.service";
import {ErrorResponse} from "../common/error-response";

declare var $:any

@Component({
    selector: 'pe-fund-profile',
    templateUrl: 'view/pe.fund-profile.component.html',
    styleUrls: ['../../../public/css/pe/pe.fund-profile.component.css'],
    providers: [PEFirmService, PEFundService]
})
export class PEFundProfileComponent extends CommonFormViewComponent implements OnInit{
    private fund = new PEFund();

    @ViewChild('strategySelect')
    private strategySelect;

    @ViewChild('industrySelect')
    private industrySelect;

    @ViewChild('geographySelect')
    private geographySelect;

    public strategyList: Array<any> = [];
    public industryList: Array<any> = [];
    public geographyList: Array<any> = [];

    public firmStrategyList: Array<any> = [];
    public firmIndustryList: Array<any> = [];
    public firmGeographyList: Array<any> = [];

    public currencyList = [];
    openingScheduleList = [];

    public sub:any;
    public fundIdParam: number;
    public firmIdParam: number;

    private visible = false;
    private openingSoon = false;

    busy: Subscription;

    uploadedGrossCf;
    uploadedNetCf;

    myFiles: File[];

    performanceSaveTypeMessage: string;
    grossCashFlowSaveTypeMessage: string;

    private moduleAccessChecker: ModuleAccessCheckerService;


    constructor(
        private lookupService: LookupService,
        private firmService: PEFirmService,
        private fundService: PEFundService,
        private route: ActivatedRoute,
        private router: Router
    ){
        super(router);

        this.moduleAccessChecker = new ModuleAccessCheckerService;

        if(!this.moduleAccessChecker.checkAccessPrivateEquity()){
            this.router.navigate(['accessDenied']);
        }

        //loadLookups
        this.loadLookups();

        this.myFiles = [];

        // TODO: wait/sync on lookup loading
        // TODO: sync on subscribe results

        //console.log(this.currencyList);
        //this.waitSleep(700);

        //parse params and load data
        this.sub = this.route
            .params
            .subscribe(params => {
                this.firmIdParam = +params['firmId'];
                this.fundIdParam = +params['id'];
                this.fund.firm = new PEFirm();

                if(this.fundIdParam > 0){
                    this.busy = this.fundService.get(this.fundIdParam)
                        .subscribe(
                            (data: PEFund) => {
                                if(data && data.id > 0) {
                                    this.fund = data;

                                    //console.log(this.fund);

                                    // preselect firm strategies
                                    this.preselectStrategy();

                                    // preselect industry focus
                                    this.preselectIndustry();

                                    // preselect geography focus
                                    this.preselectGeographies();

                                    //untoggle funds details if status is open
                                    if(this.fund.status == 'Open'){
                                        this.visible = true;
                                    }
                                    if(this.fund.status == 'Opening soon'){
                                        this.visible = true;
                                        this.openingSoon = true;
                                    }

                                    if(this.fund.calculationType == null){
                                        this.fund.calculationType = 0;
                                    }

                                    this.updateSaveTypeMessage();

                                    if(this.fund.companyPerformance == null){
                                        this.fund.companyPerformance = [];
                                    }

                                    if(this.fund.companyPerformanceIdd == null){
                                        this.fund.companyPerformanceIdd = [];
                                    }

                                    if(this.fund.grossCashflow == null){
                                        this.fund.grossCashflow = [];
                                    }

                                    if(this.fund.netCashflow == null){
                                        this.fund.netCashflow = [];
                                    }

                                }else{
                                    // TODO: handle error
                                    this.errorMessage = "Error loading fund profile.";
                                }
                            },
                            (error: ErrorResponse) => {
                                this.errorMessage = "Error loading fund profile";
                                if(error && !error.isEmpty()){
                                    this.processErrorMessage(error);
                                    console.log(error);
                                }
                                this.postAction(null, null);
                            }
                        );
                }
                if(this.firmIdParam > 0){
                    this.firmService.get(this.firmIdParam)
                        .subscribe(
                            (data: PEFirm) => {
                                if(data && data.id > 0) {
                                    this.fund.firm = data;
                                    this.preselectFirmStrategyGeographyIndustry();
                                }else{
                                    // TODO: handle error
                                    this.errorMessage = "Error loading fund manager info.";
                                }
                            },
                            (error: ErrorResponse) => {
                                this.errorMessage = "Error loading manager profile";
                                if(error && !error.isEmpty()){
                                    this.processErrorMessage(error);
                                    console.log(error);
                                }
                                this.postAction(null, null);
                            }
                        );
                }else{
                    // TODO: handle error
                    error => this.errorMessage = "Invalid parameter values";
                    return;
                }
            });
    }

    ngOnInit(): any {

        // TODO: exclude jQuery
        // datetimepicker
        $('#firstClose').datetimepicker({
            //defaultDate: new Date(),
            format: 'DD-MM-YYYY'
        });

        $('#finalClose').datetimepicker({
            //defaultDate: new Date(),
            format: 'DD-MM-YYYY'
        });

        $('#asOfDate').datetimepicker({
            //defaultDate: new Date(),
            format: 'DD-MM-YYYY'
        });

        $('#asOfDateOpenFund').datetimepicker({
            //defaultDate: new Date(),
            format: 'DD-MM-YYYY'
        });

        $('#dateGrossCF').datetimepicker({
            //defaultDate: new Date(),
            format: 'DD-MM-YYYY'
        });
    }

    save(){
        console.log(this.fund);

        this.fund.firstClose = $('#firstClose').val();
        this.fund.finalClose = $('#finalClose').val();

        if(this.fund.status != 'Closed') {
            this.fund.asOfDate = $('#asOfDateOpenFund').val();
        } else {
            this.fund.asOfDate = $('#asOfDate').val();
        }

        this.fund.strategy = this.convertToServiceModel(this.fund.strategy);
        this.fund.industry = this.convertToServiceModel(this.fund.industry);
        this.fund.geography = this.convertToServiceModel(this.fund.geography);

        this.busy = this.fundService.save(this.fund)
            .subscribe(
                (response: SaveResponse) => {
                    this.fund.id = response.entityId;
                    this.fund.creationDate = response.creationDate;

                    this.updateSaveTypeMessage();

                    this.postAction("Successfully saved.", null);
                },
                (error: ErrorResponse) => {
                    this.errorMessage = "Error saving fund profile";
                    if(error && !error.isEmpty()){
                        this.processErrorMessage(error);
                        console.log(error);
                    }
                    this.postAction(null, null);
                }
            )
    }

    updateSaveTypeMessage() {
        if (this.fund.calculationType == 0) {
            this.performanceSaveTypeMessage = "By pressing SAVE, the Performance will be SAVED and the Key fund statistics will be RESTORED to its original";
            this.grossCashFlowSaveTypeMessage = "By pressing SAVE, the Gross cash flow will be SAVED, the Performance will be UPDATED and the Key fund statistics will be RESTORED to its original";
        } else if (this.fund.calculationType == 1) {
            this.performanceSaveTypeMessage = "By pressing SAVE, the Performance will be SAVED and the Key fund statistics will be UPDATED";
            this.grossCashFlowSaveTypeMessage = "By pressing SAVE, the Gross cash flow will be SAVED, the Performance will be UPDATED and the Key fund statistics will be RESTORED to its original";
        } else if (this.fund.calculationType == 2) {
            this.performanceSaveTypeMessage = "By pressing SAVE, the Performance will be SAVED and the Key fund statistics will be RESTORED to its original";
            this.grossCashFlowSaveTypeMessage = "By pressing SAVE, the Gross cash flow will be SAVED, the Performance will be UPDATED and the Key fund statistics will be UPDATED";
        }
    }

    savePerformance() {
        this.busy = this.fundService.savePerformanceAndUpdateStatistics(this.fund.companyPerformance, this.fund.id)
            .subscribe(
                (response) => {
                    this.postAction(response.messageEn, null);

                    this.fund.companyPerformance = response.performanceDtoList;

                    this.fund.calculationType = response.trackRecordDTO.calculationType;

                    this.fund.numberOfInvestments = response.trackRecordDTO.numberOfInvestments;
                    this.fund.investedAmount = response.trackRecordDTO.investedAmount;
                    this.fund.realized = response.trackRecordDTO.realized;
                    this.fund.unrealized = response.trackRecordDTO.unrealized;
                    this.fund.dpi = response.trackRecordDTO.dpi;
                    this.fund.netIrr = response.trackRecordDTO.netIrr;
                    this.fund.netTvpi = response.trackRecordDTO.netTvpi;
                    this.fund.grossIrr = response.trackRecordDTO.grossIrr;
                    this.fund.grossTvpi = response.trackRecordDTO.grossTvpi;
                    this.fund.asOfDate = response.trackRecordDTO.asOfDate;
                    this.fund.benchmarkNetIrr = response.trackRecordDTO.benchmarkNetIrr;
                    this.fund.benchmarkNetTvpi = response.trackRecordDTO.benchmarkNetTvpi;
                    this.fund.benchmarkName = response.trackRecordDTO.benchmarkName;

                    //this.fund.autoCalculation = true;
                },
                (error: ErrorResponse) => {
                    this.processErrorMessage(error);
                    this.postAction(null, error.message);
                    console.log(error);
                }
            )
    }

    calculateTrackRecord(calculationType) {
        this.busy = this.fundService.calculateTrackRecord(this.fund.id, calculationType)
            .subscribe(
                (response) => {
                    this.fund.numberOfInvestments = response.trackRecordDTO.numberOfInvestments;
                    this.fund.investedAmount = response.trackRecordDTO.investedAmount;
                    this.fund.realized = response.trackRecordDTO.realized;
                    this.fund.unrealized = response.trackRecordDTO.unrealized;
                    this.fund.dpi = response.trackRecordDTO.dpi;
                    this.fund.grossTvpi = response.trackRecordDTO.grossTvpi;

                    this.fund.grossIrr = response.trackRecordDTO.grossIrr;

                    //this.fund.autoCalculation = true;
                },
                (error: ErrorResponse) => {
                    this.processErrorMessage(error);
                    this.postAction(null, error.message);
                    console.log(error);
                }
            )
    }

    //savePerformanceAndRecalculateStatistics() {
    //    this.busy = this.fundService.savePerformanceAndRecalculateStatistics(this.fund.companyPerformance, this.fund.id)
    //        .subscribe(
    //            (response) => {
    //                this.postAction(response.messageEn, null);
    //
    //                this.fund.companyPerformance = response.performanceDtoList;
    //
    //                this.fund.numberOfInvestments = response.trackRecordDTO.numberOfInvestments;
    //                this.fund.investedAmount = response.trackRecordDTO.investedAmount;
    //                this.fund.realized = response.trackRecordDTO.realized;
    //                this.fund.unrealized = response.trackRecordDTO.unrealized;
    //                this.fund.dpi = response.trackRecordDTO.dpi;
    //                this.fund.grossTvpi = response.trackRecordDTO.grossTvpi;
    //
    //                //this.fund.autoCalculation = true;
    //            },
    //            (error: ErrorResponse) => {
    //                this.processErrorMessage(error);
    //                this.postAction(null, error.message);
    //                console.log(error);
    //            }
    //        )
    //}

    //saveGrossCF() {
    //    this.busy = this.fundService.saveGrossCF(this.fund.grossCashflow, this.fund.id)
    //        .subscribe(
    //            (response) => {
    //                this.postAction(response.messageEn, null);
    //
    //                this.fund.grossCashflow = response.cashflowDtoList;
    //            },
    //            (error: ErrorResponse) => {
    //                this.processErrorMessage(error);
    //                this.postAction(null, error.message);
    //                console.log(error);
    //            }
    //        )
    //}

    saveGrossCFAndRecalculatePerformanceAndUpdateStatistics() {
        this.busy = this.fundService.saveGrossCFAndRecalculatePerformanceAndUpdateStatistics(this.fund.grossCashflow, this.fund.id)
            .subscribe(
                (response) => {
                    this.postAction(response.messageEn, null);

                    this.fund.grossCashflow = response.cashflowDtoList;

                    this.fund.companyPerformanceIdd = response.performanceIddDtoList;

                    this.fund.calculationType = response.trackRecordDTO.calculationType;

                    this.fund.numberOfInvestments = response.trackRecordDTO.numberOfInvestments;
                    this.fund.investedAmount = response.trackRecordDTO.investedAmount;
                    this.fund.realized = response.trackRecordDTO.realized;
                    this.fund.unrealized = response.trackRecordDTO.unrealized;
                    this.fund.dpi = response.trackRecordDTO.dpi;
                    this.fund.netIrr = response.trackRecordDTO.netIrr;
                    this.fund.netTvpi = response.trackRecordDTO.netTvpi;
                    this.fund.grossIrr = response.trackRecordDTO.grossIrr;
                    this.fund.grossTvpi = response.trackRecordDTO.grossTvpi;
                    this.fund.asOfDate = response.trackRecordDTO.asOfDate;
                    this.fund.benchmarkNetIrr = response.trackRecordDTO.benchmarkNetIrr;
                    this.fund.benchmarkNetTvpi = response.trackRecordDTO.benchmarkNetTvpi;
                    this.fund.benchmarkName = response.trackRecordDTO.benchmarkName;
                },
                (error: ErrorResponse) => {
                    this.processErrorMessage(error);
                    this.postAction(null, error.message);
                    console.log(error);
                }
            )
    }

    postAction(successMessage, errorMessage) {
        this.successMessage = successMessage;
        this.errorMessage = errorMessage;

        // TODO: non jQuery
        $('html, body').animate({scrollTop: 0}, 'fast');
    }

    // TODO: Move to a common component
    loadLookups(){

        this.lookupService.getOpeningScheduleList().then(data => this.openingScheduleList = data);

        //load strategies
        this.lookupService.getPEStrategies()
            .subscribe(
                data => {
                    data.forEach(element => {
                        this.strategyList.push({id: element.code, text: element.nameEn});
                    });
                },
                (error: ErrorResponse) => {
                    this.errorMessage = "Error loading lookups";
                    if(error && !error.isEmpty()){
                        this.processErrorMessage(error);
                        console.log(error);
                    }
                    this.postAction(null, null);
                }
            );

        //load PE_Industry_Focus
        this.lookupService.getPEIndustryFocus()
            .subscribe(
                data => {
                    data.forEach(element => {
                        this.industryList.push({id: element.code, text: element.nameEn});
                    });
                },
                (error: ErrorResponse) => {
                    this.errorMessage = "Error loading lookups";
                    if(error && !error.isEmpty()){
                        this.processErrorMessage(error);
                        console.log(error);
                    }
                    this.postAction(null, null);
                }
            );
        // load geographies
        this.lookupService.getGeographies()
            .subscribe(
                data => {
                    data.forEach(element => {
                        this.geographyList.push({ id: element.code, text: element.nameEn});
                    });
                },
                (error: ErrorResponse) => {
                    this.errorMessage = "Error loading lookups";
                    if(error && !error.isEmpty()){
                        this.processErrorMessage(error);
                        console.log(error);
                    }
                    this.postAction(null, null);
                }
            );

        this.lookupService.getCurrencyList()
            .subscribe(
                data => {
                    data.forEach(element => {
                        this.currencyList.push(element);
                    });
                },
                error =>  this.errorMessage = <any>error
            );
    }

    // TODO: Move to a common component
    preselectIndustry(){
        if(this.fund.industry) {
            this.fund.industry.forEach(element => {
                for (var i = 0; i < this.industryList.length; i++) {
                    var option = this.industryList[i];
                    if (element.code === option.id) {
                        this.industrySelect.active.push(option);
                    }
                }
            });
        }
    }

    // TODO: Move to a common component
    preselectStrategy(){
        if(this.fund.strategy) {
            this.fund.strategy.forEach(element => {
                for (var i = 0; i < this.strategyList.length; i++) {
                    var option = this.strategyList[i];
                    if (element.code === option.id) {
                        this.strategySelect.active.push(option);
                    }
                }
            });
        }
    }
    preselectFirmStrategyGeographyIndustry(){
        if(this.fund.firm.strategy) {
            this.fund.firm.strategy.forEach(element => {
                this.firmStrategyList.push(element.nameEn.toString());
            });
        }
        if(this.fund.firm.industryFocus) {
            this.fund.firm.industryFocus.forEach(element => {
                this.firmIndustryList.push(element.nameEn.toString());
            });
        }
        if(this.fund.firm.geographyFocus) {
            this.fund.firm.geographyFocus.forEach(element => {
                this.firmGeographyList.push(element.nameEn.toString());
            });
        }
    }

    // TODO: Move to a common component
    preselectGeographies(){
        if(this.fund.geography) {
            this.fund.geography.forEach(element => {
                for (var i = 0; i < this.geographyList.length; i++) {
                    var option = this.geographyList[i];
                    if (element.code === option.id) {
                        this.geographySelect.active.push(option);
                    }
                }
            });
        }
    }

    public selected(value:any):void {
        //console.log('Selected value is: ', value);
    }
    public removed(value:any):void {
        //console.log('Removed value is: ', value);
    }

    // TODO: Move to a common component
    public refreshStrategy(value:any):void {
        this.fund.strategy = value;
    }
    // TODO: Move to a common component
    public refreshIndustry(value:any):void {
        this.fund.industry = value;
    }
    // TODO: Move to a common component
    public refreshGeography(value:any):void {
        this.fund.geography = value;
    }

    toggle() {
        if(this.fund.status != "Closed"){
            this.visible = true;
        } else {
            this.visible = false;
        }
        if(this.fund.status == "Opening soon"){
            this.openingSoon = true;
        } else {
            this.openingSoon = false;
        }
    }

    addRowGrossCF(){
        //console.log(this.fund.grossCashflow);
        this.fund.grossCashflow.push({id:"", companyName:"", date:"", invested:"", realized:"", unrealized:"", grossCF:"", autoCalculation:true});
    }

    removeRowGrossCF(item){
        for(var i = this.fund.grossCashflow.length; i--;) {
            if(this.fund.grossCashflow[i] === item) {
                this.fund.grossCashflow.splice(i, 1);
            }
        }
    }

    addRowNetCf(){
        //console.log(this.fund.netCashflow);
        this.fund.netCashflow.push({fundName:"", currency:"", transactionDate:"", drawn:"", distributed:"", nav:"", netCF:"", typeOfFundTransaction:""})
    }

    addRowPerformance(){
        //console.log(this.fund.companyPerformance);
        this.fund.companyPerformance.push({id:"", companyName:"", invested:"", realized:"", unrealized:"", totalValue:"", multiple:"", autoCalculation:true, grossIrr:"", netIrr:""});
    }

    removeRowPerformance(item){
        for(var i = this.fund.companyPerformance.length; i--;) {
            if(this.fund.companyPerformance[i] === item) {
                this.fund.companyPerformance.splice(i, 1);
            }
        }
    }

    grossCfParse(){
        var cf = [];
        var rows = this.uploadedGrossCf.split("\n");

        for(var i = 0; i < rows.length; i++){
            var row = rows[i].split("\t");
            if(row[0] != "") {
                this.fund.grossCashflow.push({companyName: row[0], date: row[1], invested: row[2], realized: row[3], unrealized: row[4], grossCF: row[5]});
            }
        }
        $('#tabs li:eq(2) a').tab('show');
    }

    netCfParse(){
        var cf = [];
        var rows = this.uploadedNetCf.split("\n");

        for(var i = 0; i < rows.length; i++){
            var row = rows[i].split("\t");
            if(row[0] != "") {
                this.fund.netCashflow.push({fundName: row[0], currency: row[1], transactionDate: row[2], drawn: row[3], distributed: row[4], nav: row[5], netCF: row[6], typeOfFundTransaction: row[7]});
            }
        }
        $('#tabs a:last').tab('show');
    }

    canEdit(){
        return this.moduleAccessChecker.checkAccessPrivateEquityEditor();
    }

    statisticsAutoCalculation() {
        if (this.fund.calculationType == 1) {
            this.calculateTrackRecord(1);
        } else if (this.fund.calculationType == 2) {
            this.calculateTrackRecord(2);
        }
    }

    autoCalculationTotalAndMultiple(item) {
        if (item.autoCalculation) {
            item.totalValue = Number(item.realized) + Number(item.unrealized);
            if (Number(item.invested) === 0) {
                item.multiple = null;
            } else {
                item.multiple = Number(item.totalValue) / Number(item.invested);
            }
        }
    }

    autoCalculationGrossCF(item) {
        if (item.autoCalculation) {
            item.grossCF = Number(item.invested) + Number(item.realized) + Number(item.unrealized);
        }
    }

    performancesComparison(item) {
        for (let performance of this.fund.companyPerformanceIdd) {
            if (item.companyName == performance.companyName &&
                Math.abs(item.invested - performance.invested) <= 0.001 &&
                Math.abs(item.realized - performance.realized) <= 0.001 &&
                Math.abs(item.unrealized - performance.unrealized) <= 0.001 &&
                Math.abs(item.totalValue - performance.totalValue) <= 0.001 &&
                Math.abs(item.multiple - performance.multiple) <= 0.00001 &&
                Math.abs(item.grossIrr - performance.grossIrr) <= 0.001 &&
                Math.abs(item.netIrr - performance.netIrr) <= 0.001 ) {
                return true;
            }
        }
        return false;
    }

    fileChange(files: any){
        this.myFiles = files;
        console.log(this.myFiles);
    }

    onSubmitGrossCF() {
        this.busy = this.fundService.postFiles(this.myFiles, this.fund.id)
            .subscribe(
                (response) => {
                    this.postAction(response.messageEn, null);
                },
                (error: ErrorResponse) => {
                    this.processErrorMessage(error);
                    this.postAction(null, error.message);
                    console.log(error);
                }
            )
    }
}