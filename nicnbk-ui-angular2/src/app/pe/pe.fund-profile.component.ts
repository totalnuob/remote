import {Component, ViewChild, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {CommonFormViewComponent} from "../common/common.component";
import {PEFund} from "./model/pe.fund";
import {PEFundDataForOnePager} from "./model/pe.fund.data.for.one.pager";
import {PEFirmService} from "./pe.firm.service";
import {PEFirm} from "./model/pe.firm";
import {PEFundService} from "./pe.fund.service";
import {SaveResponse} from "../common/save-response";
import {LookupService} from "../common/lookup.service";

import {Subscription} from 'rxjs';
import {ModuleAccessCheckerService} from "../authentication/module.access.checker.service";
import {ErrorResponse} from "../common/error-response";
import {PEIrrParam} from "./model/pe.irrParam";
import {DATA_APP_URL} from "../common/common.service.constants";
import {FileDownloadService} from "../common/file.download.service";

declare var $:any

@Component({
    selector: 'pe-fund-profile',
    templateUrl: 'view/pe.fund-profile.component.html',
    styleUrls: ['../../../public/css/pe/pe.fund-profile.component.css'],
    providers: [PEFirmService, PEFundService]
})
export class PEFundProfileComponent extends CommonFormViewComponent implements OnInit{
    private PE_FUND_CREATE_ONE_PAGER_URL = DATA_APP_URL + "pe/fund/" + "createOnePager/";

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

    public rounding = 1000000;

    public performanceIddTotal = [];

    public dynamicIRR: number;
    public irrParam = new PEIrrParam();
    public companyDescriptionIRRList = [];
    public industryIRRList = [];
    public countryIRRList = [];
    public typeOfInvestmentIRRList = [];
    public controlIRRList = [];
    public dealSourceIRRList = [];
    public currencyIRRList = [];

    private visible = false;
    private openingSoon = false;

    busy: Subscription;

    uploadedGrossCf;
    uploadedNetCf;

    myFiles: File[];
    //fileGPlogo: File[];
    //fileNIClogo: File[];

    url_GP: any;
    url_NIC: any;

    performanceSaveTypeMessage: string;
    grossCashFlowSaveTypeMessage: string;

    industryAsString: string;
    strategyAsString: string;
    geographyAsString: string;

    public firmFunds: Array<any> = [];

    public totalNumberOfInvestments: number;
    public totalInvested: number;
    public totalRealized: number;
    public totalUnrealized: number;
    public totalGrossMOIC: number;
    public totalGrossIrr: number;

    public dataForOnePager = new PEFundDataForOnePager();

    private moduleAccessChecker: ModuleAccessCheckerService;


    constructor(
        private lookupService: LookupService,
        private firmService: PEFirmService,
        private fundService: PEFundService,
        private route: ActivatedRoute,
        private downloadService: FileDownloadService,
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
        //this.fileGPlogo = [];
        //this.fileNIClogo = [];

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

                                    console.log(this.fund);

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

                                    if(this.fund.onePagerDescriptions == null){
                                        this.fund.onePagerDescriptions = [];
                                    }

                                    if(this.fund.managementTeam == null){
                                        this.fund.managementTeam = [];
                                    }

                                    //this.addOnePagerAsOfDate();
                                    //this.addOnePagerBenchmarkName();

                                    this.updateIRRParamList();

                                    this.updatePerformanceIddTotal();

                                    if(this.fund.grossCashflow == null){
                                        this.fund.grossCashflow = [];
                                    }

                                    if(this.fund.netCashflow == null){
                                        this.fund.netCashflow = [];
                                    }

                                    this.updateIndustryStrategyGeographyAsStrings();

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
                }else{
                    this.fund.calculationType = 0;
                }
                if(this.firmIdParam > 0){
                    this.firmService.get(this.firmIdParam)
                        .subscribe(
                            (data: PEFirm) => {
                                if(data && data.id > 0) {
                                    this.fund.firm = data;
                                    this.preselectFirmStrategyGeographyIndustry();

                                    if (this.fund.firm.logo != null) {
                                        this.url_GP = "data:" + this.fund.firm.logo.mimeType + ";base64," + this.fund.firm.logo.bytes;
                                    }

                                    if (this.fund.firm.logoNIC != null) {
                                        this.url_NIC = "data:" + this.fund.firm.logoNIC.mimeType + ";base64," + this.fund.firm.logoNIC.bytes;
                                    }
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
                    this.getFunds(this.firmIdParam);
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

        //$('#asOfDateOnePager').datetimepicker({
        //    //defaultDate: new Date(),
        //    format: 'DD-MM-YYYY'
        //});
    }

    save(){
        console.log(this.fund);

        this.fund.firstClose = $('#firstClose').val();
        this.fund.finalClose = $('#finalClose').val();
        this.fund.asOfDateOnePager = $('#asOfDateOpenFund').val();

        //if(this.fund.status != 'Closed') {
        //    this.fund.asOfDate = $('#asOfDateOpenFund').val();
        //} else {
        //    this.fund.asOfDate = $('#asOfDate').val();
        //}

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

                    this.updateIndustryStrategyGeographyAsStrings();

                    this.getFunds(this.fund.firm.id);
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

    getFunds(id) {
        this.firmService.loadFirmFunds(id)
            .subscribe(
                (response) => {
                    this.firmFunds = response;

                    this.totalNumberOfInvestments = 0;
                    this.totalInvested = 0.0;
                    this.totalRealized = 0.0;
                    this.totalUnrealized = 0.0;
                    this.totalGrossMOIC = null;
                    this.totalGrossIrr = null;

                    this.firmFunds.forEach(element => {
                        this.totalNumberOfInvestments += (element.numberOfInvestments != null) ? element.numberOfInvestments : 0;
                        this.totalInvested += (element.investedAmount != null) ? element.investedAmount : 0;
                        this.totalRealized += (element.realized != null) ? element.realized : 0;
                        this.totalUnrealized += (element.unrealized != null) ? element.unrealized : 0;
                    })

                    if (this.totalInvested != 0.0) {
                        this.totalGrossMOIC = (this.totalRealized + this.totalUnrealized) / this.totalInvested;
                    }
                },
                (error: ErrorResponse) => {
                    this.errorMessage = "Error loading firm funds";
                    if(error && !error.isEmpty()){
                        this.processErrorMessage(error);
                        console.log(error);
                    }
                    this.postAction(null, null);
                }
            )
    }

    updateIndustryStrategyGeographyAsStrings() {
        this.industryAsString = '';
        for (var i = 0; i < this.fund.industry.length; i++) {
            this.industryList.forEach(element => {
                if (element.id === this.fund.industry[i].code) {
                    if (this.industryAsString === '') {
                        this.industryAsString = element.text;
                    } else {
                        this.industryAsString += ', ' + element.text;
                    }
                }
            })
        }

        this.strategyAsString = '';
        for (var i = 0; i < this.fund.strategy.length; i++) {
            this.strategyList.forEach(element => {
                if (element.id === this.fund.strategy[i].code) {
                    if (this.strategyAsString === '') {
                        this.strategyAsString = element.text;
                    } else {
                        this.strategyAsString += ', ' + element.text;
                    }
                }
            })
        }

        this.geographyAsString = '';
        for (var i = 0; i < this.fund.geography.length; i++) {
            this.geographyList.forEach(element => {
                if (element.id === this.fund.geography[i].code) {
                    if (this.geographyAsString === '') {
                        this.geographyAsString = element.text;
                    } else {
                        this.geographyAsString += ', ' + element.text;
                    }
                }
            })
        }
    }

    updateSaveTypeMessage() {
        if (this.fund.calculationType == 0) {
            this.performanceSaveTypeMessage = "By pressing SAVE, the Performance will be SAVED and the Key fund statistics will be RESTORED to its original";
            this.grossCashFlowSaveTypeMessage = "By pressing SAVE, the Gross cash flow will be SAVED, the Performance will be UPDATED and the Key fund statistics will be RESTORED to its original";
        } else if (this.fund.calculationType == 1) {
            this.performanceSaveTypeMessage = "By pressing SAVE, the Performance will be SAVED and the Key fund statistics will be partially RESTORED/UPDATED";
            this.grossCashFlowSaveTypeMessage = "By pressing SAVE, the Gross cash flow will be SAVED, the Performance will be UPDATED and the Key fund statistics will be RESTORED to its original";
        } else if (this.fund.calculationType == 2) {
            this.performanceSaveTypeMessage = "By pressing SAVE, the Performance will be SAVED and the Key fund statistics will be RESTORED to its original";
            this.grossCashFlowSaveTypeMessage = "By pressing SAVE, the Gross cash flow will be SAVED, the Performance will be UPDATED and the Key fund statistics will be partially RESTORED/UPDATED";
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

    savePortfolioInfo() {
        this.busy = this.fundService.savePortfolioInfo(this.fund.companyPerformanceIdd, this.fund.id)
            .subscribe(
                (response) => {
                    this.postAction(response.messageEn, null);

                    this.fund.companyPerformanceIdd = response.performanceIddDtoList;

                    this.updateIRRParamList();
                },
                (error: ErrorResponse) => {
                    this.processErrorMessage(error);
                    this.postAction(null, error.message);
                    console.log(error);
                }
            )
    }

    updateIRRParamList() {
        this.companyDescriptionIRRList = [];
        this.industryIRRList = [];
        this.countryIRRList = [];
        this.typeOfInvestmentIRRList = [];
        this.controlIRRList = [];
        this.dealSourceIRRList = [];
        this.currencyIRRList = [];

        for (var i = 0; i < this.fund.companyPerformanceIdd.length; i++) {
            var companyDescription = this.fund.companyPerformanceIdd[i].companyDescription;
            var industry = this.fund.companyPerformanceIdd[i].industry;
            var country = this.fund.companyPerformanceIdd[i].country;
            var typeOfInvestment = this.fund.companyPerformanceIdd[i].typeOfInvestment;
            var control = this.fund.companyPerformanceIdd[i].control;
            var dealSource = this.fund.companyPerformanceIdd[i].dealSource;
            var currency = this.fund.companyPerformanceIdd[i].currency;

            if (companyDescription != null && companyDescription != '' && this.companyDescriptionIRRList.includes(companyDescription) == false) {
                this.companyDescriptionIRRList.push(companyDescription);
            }
            if (industry != null && industry != '' && this.industryIRRList.includes(industry) == false) {
                this.industryIRRList.push(industry);
            }
            if (country != null && country != '' && this.countryIRRList.includes(country) == false) {
                this.countryIRRList.push(country);
            }
            if (typeOfInvestment != null && typeOfInvestment != '' && this.typeOfInvestmentIRRList.includes(typeOfInvestment) == false) {
                this.typeOfInvestmentIRRList.push(typeOfInvestment);
            }
            if (control != null && control != '' && this.controlIRRList.includes(control) == false) {
                this.controlIRRList.push(control);
            }
            if (dealSource != null && dealSource != '' && this.dealSourceIRRList.includes(dealSource) == false) {
                this.dealSourceIRRList.push(dealSource);
            }
            if (currency != null && currency != '' && this.currencyIRRList.includes(currency) == false) {
                this.currencyIRRList.push(currency);
            }
        }
    }

    calculateIRR() {
            this.busy = this.fundService.calculateIRR(this.irrParam, this.fund.id)
            .subscribe(
                (response) => {
                    this.postAction(response.messageEn, null);

                    this.dynamicIRR = response.irr;
                },
                (error: ErrorResponse) => {
                    this.dynamicIRR = null;
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

                    if (calculationType == 2) {
                        this.fund.grossIrr = response.trackRecordDTO.grossIrr;
                    }

                    //this.fund.autoCalculation = true;
                },
                (error: ErrorResponse) => {
                    this.processErrorMessage(error);
                    this.postAction(null, error.message);
                    console.log(error);
                }
            )
    }

    updatePerformanceIddTotal() {
        this.busy = this.fundService.calculateTrackRecord(this.fund.id, 2)
            .subscribe(
                (response) => {
                    this.performanceIddTotal = [];

                    this.performanceIddTotal.invested = response.trackRecordDTO.investedAmount;
                    this.performanceIddTotal.realized = response.trackRecordDTO.realized;
                    this.performanceIddTotal.unrealized = response.trackRecordDTO.unrealized;

                    this.performanceIddTotal.totalValue = Number(this.performanceIddTotal.realized) + Number(this.performanceIddTotal.unrealized);
                    if (Number(this.performanceIddTotal.invested) === 0) {
                        this.performanceIddTotal.multiple = null;
                    } else {
                        this.performanceIddTotal.multiple = Number(this.performanceIddTotal.totalValue) / Number(this.performanceIddTotal.invested);
                    }

                    this.performanceIddTotal.grossIrr = response.trackRecordDTO.grossIrr;
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

    saveGrossCFAndRecalculatePerformanceIddAndUpdateStatistics() {
        this.busy = this.fundService.saveGrossCFAndRecalculatePerformanceIddAndUpdateStatistics(this.fund.grossCashflow, this.fund.id)
            .subscribe(
                (response) => {
                    this.postAction(response.messageEn, null);

                    this.fund.grossCashflow = response.cashflowDtoList;

                    this.fund.companyPerformanceIdd = response.performanceIddDtoList;

                    this.updateIRRParamList();

                    this.updatePerformanceIddTotal();

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

    sortPerformanceIdd(sortType) {
        if (sortType === 11 || sortType === 13) {
            this.fund.companyPerformanceIdd.sort( function(name1, name2) {
                if ( name1.companyName < name2.companyName ){
                    return sortType - 12;
                }else if( name1.companyName > name2.companyName ){
                    return 12 - sortType;
                }else{
                    return 0;
                }
            });
        }
        if (sortType === 21 || sortType === 23) {
            this.fund.companyPerformanceIdd.sort( function(name1, name2) {
                if ( name1.invested < name2.invested ){
                    return sortType - 22;
                }else if( name1.invested > name2.invested ){
                    return 22 - sortType;
                }else{
                    return 0;
                }
            });
        }
        if (sortType === 31 || sortType === 33) {
            this.fund.companyPerformanceIdd.sort( function(name1, name2) {
                if ( name1.realized < name2.realized ){
                    return sortType - 32;
                }else if( name1.realized > name2.realized ){
                    return 32 - sortType;
                }else{
                    return 0;
                }
            });
        }
        if (sortType === 41 || sortType === 43) {
            this.fund.companyPerformanceIdd.sort( function(name1, name2) {
                if ( name1.unrealized < name2.unrealized ){
                    return sortType - 42;
                }else if( name1.unrealized > name2.unrealized ){
                    return 42 - sortType;
                }else{
                    return 0;
                }
            });
        }
        if (sortType === 51 || sortType === 53) {
            this.fund.companyPerformanceIdd.sort( function(name1, name2) {
                if ( name1.totalValue < name2.totalValue ){
                    return sortType - 52;
                }else if( name1.totalValue > name2.totalValue ){
                    return 52 - sortType;
                }else{
                    return 0;
                }
            });
        }
        if (sortType === 61 || sortType === 63) {
            this.fund.companyPerformanceIdd.sort( function(name1, name2) {
                if ( name1.multiple < name2.multiple ){
                    return sortType - 62;
                }else if( name1.multiple > name2.multiple ){
                    return 62 - sortType;
                }else{
                    return 0;
                }
            });
        }
        if (sortType === 71 || sortType === 73) {
            this.fund.companyPerformanceIdd.sort( function(name1, name2) {
                if ( name1.grossIrr < name2.grossIrr ){
                    return sortType - 72;
                }else if( name1.grossIrr > name2.grossIrr ){
                    return 72 - sortType;
                }else{
                    return 0;
                }
            });
        }
        if (sortType === 81 || sortType === 83) {
            this.fund.companyPerformanceIdd.sort( function(name1, name2) {
                if ( name1.netIrr < name2.netIrr ){
                    return sortType - 82;
                }else if( name1.netIrr > name2.netIrr ){
                    return 82 - sortType;
                }else{
                    return 0;
                }
            });
        }
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
        this.fund.companyPerformance.push({id:"", companyName:"", invested:"", realized:"", unrealized:"", totalValue:"", multiple:null, autoCalculation:true, grossIrr:"", netIrr:""});
    }

    removeRowPerformance(item){
        for(var i = this.fund.companyPerformance.length; i--;) {
            if(this.fund.companyPerformance[i] === item) {
                this.fund.companyPerformance.splice(i, 1);
            }
        }
    }

    addRowDescription(typeOfDescription){
        this.fund.onePagerDescriptions.push({id:"", descriptionBold:"", description:"", type: typeOfDescription});
    }

    removeRowDescription(item){
        for(var i = this.fund.onePagerDescriptions.length; i--;) {
            if(this.fund.onePagerDescriptions[i] === item) {
                this.fund.onePagerDescriptions.splice(i, 1);
            }
        }
    }

    addRowManagementTeam(){
        this.fund.managementTeam.push({id:"", name:"", position:"", age:"", experience:"", education:""});
    }

    removeRowManagementTeam(item){
        for(var i = this.fund.managementTeam.length; i--;) {
            if(this.fund.managementTeam[i] === item) {
                this.fund.managementTeam.splice(i, 1);
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

    //fileLogoChange(files: any, partner: string){
    //    if(partner === 'GP') {
    //        this.fileGPlogo = files;
    //        console.log(this.fileGPlogo);
    //    }
    //    if(partner === 'NIC') {
    //        this.fileNIClogo = files;
    //        console.log(this.fileNIClogo);
    //    }
    //}

    onSubmitGrossCF() {
        this.busy = this.fundService.postFiles(this.myFiles)
            .subscribe(
                (response) => {
                    for (var i = 0; i < response.cashflowDtoList.length; i++) {
                        this.fund.grossCashflow.push({
                            id:"",
                            companyName:response.cashflowDtoList[i].companyName,
                            date:response.cashflowDtoList[i].date,
                            invested:response.cashflowDtoList[i].invested,
                            realized:response.cashflowDtoList[i].realized,
                            unrealized:response.cashflowDtoList[i].unrealized,
                            grossCF:response.cashflowDtoList[i].grossCF,
                            autoCalculation:response.cashflowDtoList[i].autoCalculation});
                    }
                    this.postAction(response.messageEn + ' (a total of ' + response.cashflowDtoList.length + ' new transactions)', null);
                },
                (error) => {
                    this.processErrorMessage(error);
                    this.postAction(null, JSON.parse(error).messageEn);
                    console.log(error);
                }
            )
    }

    //addOnePagerAsOfDate() {
    //    var found = false;
    //
    //    for (var i = 0; i < this.fund.onePagerDescriptions.length; i++) {
    //        if (this.fund.onePagerDescriptions[i].type === 0) {
    //            found = true;
    //        }
    //    }
    //
    //    if (!found) {
    //        this.addRowDescription(0);
    //    }
    //}

    //addOnePagerBenchmarkName() {
    //    var found = false;
    //
    //    for (var i = 0; i < this.fund.onePagerDescriptions.length; i++) {
    //        if (this.fund.onePagerDescriptions[i].type === -1) {
    //            found = true;
    //        }
    //    }
    //
    //    if (!found) {
    //        this.addRowDescription(-1);
    //    }
    //}

    saveDataAndCreateOnePager() {
        this.dataForOnePager.onePagerDescriptions = this.fund.onePagerDescriptions;
        this.dataForOnePager.managementTeam = this.fund.managementTeam;
        //this.dataForOnePager.asOfDateOnePager = $('#asOfDateOnePager').val();

        //console.log(this.fund.asOfDateOnePager);

        this.busy = this.fundService.saveDataForOnePager(this.dataForOnePager, this.fund.id)
            .subscribe(
                (response) => {
                    this.postAction(response.messageEn, null);

                    this.fund.onePagerDescriptions = response.onePagerDescriptions;
                    this.fund.managementTeam = response.managementTeam;
                    //this.fund.asOfDateOnePager = response.asOfDateOnePager;

                    this.createAndDownloadOnePager();
                },
                (error: ErrorResponse) => {
                    this.processErrorMessage(error);
                    this.postAction(null, error.message);
                    console.log(error);
                }
            )
    }

    createAndDownloadOnePager() {
        //this.busy = this.fundService.createAndDownloadOnePager(this.fund.id, 'OnePager')
        this.downloadService.makeFileRequest(this.PE_FUND_CREATE_ONE_PAGER_URL + this.fund.id, 'OnePager')
            .subscribe(
                response  => {
                    //console.log("ok");
                },
                error => {
                    //console.log("fails")
                    this.postAction(null, "Error exporting data");
                }
            );
    }
}