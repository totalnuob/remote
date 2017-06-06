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
    styleUrls: [],
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


                                    console.log(this.fund.fundCompanyPerformance);
                                    if(this.fund.grossCashflow == null){
                                        this.fund.grossCashflow = [];
                                    }

                                    if(this.fund.netCashflow == null){
                                        this.fund.grossCashflow = [];
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

    }

    save(){
        console.log(this.fund);

        this.fund.firstClose = $('#firstClose').val();
        this.fund.finalClose = $('#finalClose').val();
        this.fund.strategy = this.convertToServiceModel(this.fund.strategy);
        this.fund.industry = this.convertToServiceModel(this.fund.industry);
        this.fund.geography = this.convertToServiceModel(this.fund.geography);

        this.busy = this.fundService.save(this.fund)
            .subscribe(
                (response: SaveResponse) => {
                    this.fund.id = response.entityId;
                    this.fund.creationDate = response.creationDate;

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

    addRow(){
        console.log(this.fund.grossCashflow);
        this.fund.grossCashflow.push({companyName:"", date:"",invested:"", realized:"", unrealized:"", grossCF:"", irr:""});
    }

    addRowNetCf(){
        console.log(this.fund.netCashflow);
        this.fund.netCashflow.push({fundName:"", currency:"", transactionDate:"", drawn:"", distributed:"", nav:"", netCF:"", typeOfFundTransaction:""})
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
}