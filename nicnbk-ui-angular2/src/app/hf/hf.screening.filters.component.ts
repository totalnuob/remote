import { Component } from '@angular/core';
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

declare var $:any

@Component({
    selector: 'hf-fund-search',
    templateUrl: 'view/hf.screening.filters.component.html',
    styleUrls: [
        //'../../../public/css/...',
        '../../../node_modules/angular2-busy/build/style/busy.css'
    ],
    providers: []
})
export class HFScreeningFilteredResultsComponent extends CommonFormViewComponent{

    screeningId;
    records: HedgeFundScreeningFilteredResult[];

    private moduleAccessChecker: ModuleAccessCheckerService;

    public sub: any;
    busy: Subscription;

    constructor(
        private screeningService: HedgeFundScreeningService,
        private router: Router,
        private route: ActivatedRoute
    ){
        super(router);

        this.moduleAccessChecker = new ModuleAccessCheckerService;

        if(!this.moduleAccessChecker.checkAccessHedgeFunds()){
            this.router.navigate(['accessDenied']);
        }

        this.sub = this.route
            .params
            .subscribe(params => {
                this.screeningId = +params['id'];
                if(this.screeningId > 0) {
                    this.busy = this.screeningService.findAllFilteredResults(this.screeningId)
                        .subscribe(
                            result  => {
                                //console.log(result);
                                this.records = result;
                            },
                            error =>  {
                                this.postAction(null, "Failed to load screening filtered results");
                            }
                        );
                }else{
                }
            });

        //this.records = [];
        //var e = new HedgeFundScreeningFilteredResult();
        //e.fundAUM = 100;
        //e.managerAUM = 200;
        //e.trackRecord = 12;
        //e.lookbackReturns = 6;
        //e.lookbackReturns = 24;
        //e.startDate = "01-01-2018";
        //this.records.push(e);
        //
        //var b = new HedgeFundScreeningFilteredResult();
        //b.fundAUM = 1000;
        //b.managerAUM = 2000;
        //b.trackRecord = 120;
        //b.lookbackReturns = 60;
        //b.lookbackReturns = 240;
        //b.startDate = "01-01-2017";
        //this.records.push(b);
    }

    ngOnInit():any {
        $('#dateStartDTPickeer').datetimepicker({
            //defaultDate: new Date(),
            format: 'DD-MM-YYYY'
        });
    }


    navigate(id){
        this.router.navigate(['/hf/screening/filteredResults/edit/', this.screeningId, id]);
    }

    deleteFilteredResult(id){
        if(confirm("Are you sure want to delete filtered result?")){
            this.busy = this.screeningService.deleteFilteredResult(id)
                .subscribe(
                    result => {
                        console.log(result);
                        if(result.status != null && result.status === 'SUCCESS'){
                            this.successMessage = "Successfully deleted filtered result"
                            this.errorMessage = null;
                            this.busy = this.screeningService.findAllFilteredResults(this.screeningId)
                                .subscribe(
                                    result  => {
                                        //console.log(result);
                                        this.records = result;
                                    },
                                    error =>  {
                                        this.postAction(null, "Failed to load screening filtered results (after deletion)");
                                    }
                                );
                        }else{
                            this.successMessage = null;
                            this.errorMessage = "Failed to delete filtered result";
                            if(result.message != null && result.message.nameEn != null && result.message.nameEn.trim() != ''{
                                this.errorMessage = result.message.nameEn;
                            }
                        }
                    },
                    error => {
                        this.errorMessage = "Failed to delete filtered result";
                        this.successMessage = null;
                    }
                );
        }
    }

}