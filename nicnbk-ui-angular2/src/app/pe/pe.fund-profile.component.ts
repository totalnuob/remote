import {Component, ViewChild, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {CommonComponent} from "../common/common.component";
import {PeFund} from "./model/pe.fund";
import {PeFirmService} from "./pe.firm.service";
import {PeFirm} from "./model/pe.firm";
import {PeFundService} from "./pe.fund.service";
import {SaveResponse} from "../common/save-response";
import {LookupService} from "../common/lookup.service";
import any = jasmine.any;

declare var $:any

@Component({
    selector: 'pe-fund-profile',
    templateUrl: 'view/pe.fund-profile.component.html',
    styleUrls: [],
    providers: [PeFirmService, PeFundService]
})
export class PeFundProfileComponent extends CommonComponent implements OnInit{
    private fund = new PeFund();

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


    public sub:any;
    public fundIdParam: number;
    public firmIdParam: number;

    private visible = false;

    uploadedCf;
    numOfRows;

    constructor(
        private lookupService: LookupService,
        private firmService: PeFirmService,
        private fundService: PeFundService,
        private route: ActivatedRoute
    ){
        super();

        //loadLookups
        this.loadLookups();

        // TODO: wait/sync on lookup loading
        // TODO: sync on subscribe results
        this.waitSleep(700);

        //parse params and load data
        this.sub = this.route
            .params
            .subscribe(params => {
                this.firmIdParam = +params['firmId'];
                this.fundIdParam = +params['id'];
                this.fund.firm = new PeFirm();

                if(this.fundIdParam > 0){
                    this.fundService.get(this.fundIdParam)
                        .subscribe(
                            (data: PeFund) => {
                                if(data && data.id > 0) {
                                    this.fund = data;

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
                                    console.log(this.fund.fundCompanyPerformance);
                                    if(this.fund.cashflow == null){
                                        this.fund.cashflow = [];
                                    }

                                }else{
                                    // TODO: handle error
                                    this.errorMessage = "Error loading fund profile.";
                                }
                            },
                            error => this.errorMessage = "Error loading manager profile"
                        );
                }
                if(this.firmIdParam > 0){
                    this.firmService.get(this.firmIdParam)
                        .subscribe(
                            (data: PeFirm) => {
                                if(data && data.id > 0) {
                                    this.fund.firm = data;
                                    this.preselectFirmStrategyGeographyIndustry();
                                }else{
                                    // TODO: handle error
                                    this.errorMessage = "Error loading fund manager info.";
                                }
                            },
                            error => this.errorMessage = "Error loading manager profile"
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

        this.fundService.save(this.fund)
            .subscribe(
                (response: SaveResponse) => {
                    this.fund.id = response.entityId;
                    this.fund.creationDate = response.creationDate;

                    this.postAction("Successfully saved.", null);
                },
                error => {
                    this.postAction(null, "Error saving fund profile.");
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
        //load strategies
        this.lookupService.getPEStrategies()
            .subscribe(
                data => {
                    data.forEach(element => {
                        this.strategyList.push({id: element.code, text: element.nameEn});
                    });
                },
                error => this.errorMessage = <any>error
            );

        //load PE_Industry_Focus
        this.lookupService.getPEIndustryFocus()
            .subscribe(
                data => {
                    data.forEach(element => {
                        this.industryList.push({id: element.code, text: element.nameEn});
                    });
                },
                error => this.errorMessage = <any>error
            );
        // load geographies
        this.lookupService.getGeographies()
            .subscribe(
                data => {
                    data.forEach(element => {
                        this.geographyList.push({ id: element.code, text: element.nameEn});
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
        if(this.fund.status == "Open"){
            this.visible = true;
        } else {
            this.visible = false;
        }
    }

    addRow(){
        console.log(this.fund.cashflow);
        this.fund.cashflow.push({companyName:"", date:"",invested:"", realized:"", unrealized:"", grossCF:"", irr:""});
    }

    parse(){
        var cf = [];
        var rows = this.uploadedCf.split("\n");

        for(var i = 0; i < rows.length; i++){
            var row = rows[i].split(",");
            if(row[0] != "") {
                this.fund.cashflow.push({companyName: row[0], date: row[1], invested: row[2], realized: row[3], unrealized: row[4], grossCF: row[5]});
            }
        }
        this.numOfRows = rows.length;
        $('#tabs a:last').tab('show');
    }
}