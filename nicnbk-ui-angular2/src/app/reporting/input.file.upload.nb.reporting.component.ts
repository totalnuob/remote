import {Component} from "@angular/core";
import {CommonFormViewComponent} from "../common/common.component";
import {Router, ActivatedRoute} from '@angular/router';
import {InputFilesNBReport} from "./model/input.files.nb.report";
import {Subscription} from "rxjs/Subscription";
import {PeriodicReportService} from "./periodic.report.service";
import {ErrorResponse} from "../common/error-response";
import {DATA_APP_URL} from "../common/common.service.constants";
import {FileUploadResult} from "./model/file.uopload.result";
import {InputFilesInfoLookupNBReport} from "./model/input.files.info.lookup.nb.report";
import {PeriodicReport} from "./model/periodic.report";
import {ReportingFundRenameInfo} from "./model/reporting.fund.rename.info";
import {FundNameHolder} from "./model/fund.name.holder";

var fileSaver = require("file-saver");
import {Observable} from 'rxjs/Observable';
import 'rxjs/add/observable/forkJoin';
import {ReportingFundRenamePair} from "./model/reporting.fund.rename.pair";
import {ModuleAccessCheckerService} from "../authentication/module.access.checker.service";

@Component({
    selector: 'input-file-upload-nb-reporting',
    templateUrl: 'view/input.file.upload.nb.reporting.component.html',
    styleUrls: [],
    providers: [],
})
export class InputFileUploadNBReportingComponent extends CommonFormViewComponent {

    successMessageFundRename: string;
    errorMessageFundRename: string;

    private sub: any;
    private reportId;

    busy: Subscription;
    busyFundRenameDialog: Subscription;

    private selectedInfoHeader;
    private selectedInfoContent;

    private report: InputFilesNBReport;
    private periodicReport: PeriodicReport;

    private fundRenameInfo: ReportingFundRenameInfo;

    private fundRenamesPE: ReportingFundRenamePair[];
    private fundRenamesHF: ReportingFundRenamePair[];
    private fundRenamesRE: ReportingFundRenamePair[];

    private moduleAccessChecker: ModuleAccessCheckerService;

    //private fileTarragonScheduleInvestment;
    private fileTarragonSOIReport;
    private fileTarragonStatementAssets;
    private fileTarragonStatementCashflows;
    private fileTarragonStatementChanges;
    private fileSingularityBSTrancheA;
    private fileSingularityIMDRTrancheA;
    private fileSingularityPARTrancheA;
    private fileSingularityISTrancheA;
    private fileSingularityBSTrancheB;
    private fileSingularityIMDRTrancheB;
    private fileSingularityPARTrancheB;
    private fileSingularityISTrancheB;
    private fileSingularityGeneralLedger;
    private fileSingularityNOALTrancheA;
    private fileSingularityNOALTrancheB;
    private fileSingularityITD;
    private fileTerraCombined;
    private fileTerraGeneralLedger;


    private fundNameListHolder: FundNameHolder;

    constructor(
        private router: Router,
        private route: ActivatedRoute,
        private periodicReportService: PeriodicReportService
    ){
        super(router);

        this.moduleAccessChecker = new ModuleAccessCheckerService;

        this.periodicReport = new PeriodicReport();

        this.sub = this.route
            .params
            .subscribe(params => {
                this.reportId = +params['id'];

                if(this.reportId > 0){
                    // load report data

                    this.busy = this.periodicReportService.get(this.reportId)
                        .subscribe(
                            response  => {
                                this.periodicReport = response;
                                this.report = new InputFilesNBReport();
                                this.report.reportId = this.periodicReport.id;
                                if(this.periodicReport == null || this.periodicReport.id == null){
                                    this.postAction(null, "Report not loaded for id " + this.reportId);
                                    return;
                                }

                                // load existing report files info
                                this.busy = this.periodicReportService.loadInputFilesList(this.reportId)
                                    .subscribe(
                                        response  => {
                                            console.log(response);
                                            this.convertToViewModel(response.files);
                                            this.periodicReport = response.report;
                                            if(response.report == null){
                                                this.periodicReport = new PeriodicReport();
                                                this.periodicReport.id = this.reportId;
                                            }
                                        },
                                        (error: ErrorResponse) => {
                                            this.successMessage = null;
                                            this.errorMessage = "Error loading report files";
                                            this.report = null;
                                            if(error && !error.isEmpty()){
                                                this.processErrorMessage(error);
                                            }
                                            this.postAction(null,  this.errorMessage);
                                        }
                                    )
                            },
                            (error: ErrorResponse) => {
                                this.successMessage = null;
                                this.errorMessage = "Error loading report: id " + this.reportId;
                                this.report = null;
                                if(error && !error.isEmpty()){
                                    this.processErrorMessage(error);
                                }
                                this.postAction(null, this.errorMessage);
                            }
                        );

                }else{
                    this.report = new InputFilesNBReport();
                }
            });
    }

    uploadFile(fileType){
        console.log(fileType);
        if(fileType === 'tarragon_schedule_investment'){
            this.busy = this.periodicReportService.postFiles(this.report.reportId, this.fileTarragonScheduleInvestment, 'NB_REP_T1').subscribe(
                res => {
                    console.log(this.periodicReport);
                    // clear upload file on view
                    this.fileTarragonScheduleInvestment = null;
                    // set file id
                    this.report.tarragonScheduleInvestmentFileId = res.fileId;
                    this.report.tarragonScheduleInvestmentFileName = res.fileName;
                    this.postAction(res != null && res.message != null && res.message.nameEn != null ? res.message.nameEn : "Successfully uploaded file - Schedule of Investments", null);
                },
                error => {
                    var result = JSON.parse(error);
                    var message = result != null && result.message != null && result.message.nameEn != null ? result.message.nameEn : null;
                    this.fileTarragonScheduleInvestment = null;
                    this.postAction(null, message != null && message != null ? message : "Error uploading file");
                });
        } else if(fileType === 'tarragon_soi_report'){
            console.log("tarragon_soi_report");
            console.log(this.fileTarragonSOIReport);
           this.busy = this.periodicReportService.postFiles(this.report.reportId, this.fileTarragonSOIReport, 'NB_REP_T5').subscribe(
               res => {
                   console.log(this.periodicReport);
                   // clear upload file on view
                   this.fileTarragonSOIReport = null;
                   // set file id
                   this.report.tarragonSOIReportFileId = res.fileId;
                   this.report.tarragonSOIReportFileName = res.fileName;
                   this.postAction(res != null && res.message != null && res.message.nameEn != null ? res.message.nameEn : "Successfully uploaded file - SOI Report", null);
               },
               error => {
                   var result = JSON.parse(error);
                   var message = result != null && result.message != null && result.message.nameEn != null ? result.message.nameEn : null;
                   this.fileTarragonSOIReport = null;
                   this.postAction(null, message != null && message != null ? message : "Error uploading file");
               });
       }else if(fileType === 'tarragon_statement_assets'){
            console.log("tarragon_statement_assets");
            this.busy = this.periodicReportService.postFiles(this.report.reportId, this.fileTarragonStatementAssets, 'NB_REP_T2').subscribe(
                res => {
                    // clear upload file on view
                    this.fileTarragonStatementAssets = null;
                    // set file id
                    this.report.tarragonStatementAssetsFileId = res.fileId;
                    this.report.tarragonStatementAssetsFileName = res.fileName;
                    this.postAction(res != null && res.message != null && res.message.nameEn != null ? res.message.nameEn : "Successfully uploaded file - Statement of Assets, Liabilities and Partners Capital", null);
                },
                (error) => {
                    var result = JSON.parse(error);
                    var message = result != null && result.message != null && result.message.nameEn != null ? result.message.nameEn : null;
                    this.fileTarragonStatementAssets = null;
                    this.postAction(null, message != null && message != null ? message : "Error uploading file");
                });
        } else if(fileType === 'tarragon_statement_cashflows'){
            this.busy = this.periodicReportService.postFiles(this.report.reportId, this.fileTarragonStatementCashflows, 'NB_REP_T3').subscribe(
                res => {
                    // clear upload file on view
                    this.fileTarragonStatementCashflows = null;
                    // set file id
                    this.report.tarragonStatementCashflowsFileId = res.fileId;
                    this.report.tarragonStatementCashflowsFileName = res.fileName;
                    this.postAction(res != null && res.message != null && res.message.nameEn != null ? res.message.nameEn : "Successfully uploaded file - Statment of Cashflows", null);
                },
                error => {
                    var result = JSON.parse(error);
                    var message = result != null && result.message != null && result.message.nameEn != null ? result.message.nameEn : null;
                    this.fileTarragonStatementCashflows = null;
                    this.postAction(null, message != null && message != null ? message : "Error uploading file");
                });
        } else if(fileType === 'tarragon_statement_changes'){
            this.busy = this.periodicReportService.postFiles(this.report.reportId, this.fileTarragonStatementChanges, 'NB_REP_T4').subscribe(
                res => {
                    // clear upload file on view
                    this.fileTarragonStatementChanges = null;
                    // set file id
                    this.report.tarragonStatementChangesFileId = res.fileId;
                    this.report.tarragonStatementChangesFileName = res.fileName;
                    this.postAction(res != null && res.message != null && res.message.nameEn != null ? res.message.nameEn : "Successfully uploaded file - Statement of Changes", null);
                },
                error => {
                    var result = JSON.parse(error);
                    var message = result != null && result.message != null && result.message.nameEn != null ? result.message.nameEn : null;
                    this.fileTarragonStatementChanges = null;
                    this.postAction(null, message != null && message != null ? message : "Error uploading file");
                });
        } else if(fileType === 'singularity_bs_a'){
            this.busy = this.periodicReportService.postFiles(this.report.reportId, this.fileSingularityBSTrancheA, 'NB_REP_S1A').subscribe(
                res => {
                    // clear upload file on view
                    this.fileSingularityBSTrancheA = null;
                    // set file id
                    this.report.singularityBSTrancheAFileId = res.fileId;
                    this.report.singularityBSTrancheAFileName = res.fileName;
                    this.postAction(res != null && res.message != null && res.message.nameEn != null ? res.message.nameEn : "Successfully uploaded file - BS Singular Tranche A", null);
                },
                error => {
                    var result = JSON.parse(error);
                    var message = result != null && result.message != null && result.message.nameEn != null ? result.message.nameEn : null;
                    this.fileSingularityBSTrancheA = null;
                    this.postAction(null, message != null && message != null ? message : "Error uploading file");
                });
        } else if(fileType === 'singularity_imdr_a'){
            this.busy = this.periodicReportService.postFiles(this.report.reportId, this.fileSingularityIMDRTrancheA, 'NB_REP_S2A').subscribe(
                res => {
                    // clear upload file on view
                    this.fileSingularityIMDRTrancheA = null;
                    // set file id
                    this.report.singularityIMDRTrancheAFileId = res.fileId;
                    this.report.singularityIMDRTrancheAFileName = res.fileName;
                    this.postAction(res != null && res.message != null && res.message.nameEn != null ? res.message.nameEn : "Successfully uploaded file - IMDR Singular Tranche A", null);
                },
                error => {
                    var result = JSON.parse(error);
                    var message = result != null && result.message != null && result.message.nameEn != null ? result.message.nameEn : null;
                    this.fileSingularityIMDRTrancheA = null;
                    this.postAction(null, message != null && message != null ? message : "Error uploading file");
                });
        } else if(fileType === 'singularity_par_a'){
            this.busy = this.periodicReportService.postFiles(this.report.reportId, this.fileSingularityPARTrancheA, 'NB_REP_S3A').subscribe(
                res => {
                    // clear upload file on view
                    this.fileSingularityPARTrancheA = null;
                    // set file id
                    this.report.singularityPARTrancheAFileId = res.fileId;
                    this.report.singularityPARTrancheAFileName = res.fileName;
                    this.postAction(res != null && res.message != null && res.message.nameEn != null ? res.message.nameEn : "Successfully uploaded file - PAR Singular Tranche A", null);
                },
                error => {
                    var result = JSON.parse(error);
                    var message = result != null && result.message != null && result.message.nameEn != null ? result.message.nameEn : null;
                    this.fileSingularityPARTrancheA = null;
                    this.postAction(null, message != null && message != null ? message : "Error uploading file");
                });
        } else if(fileType === 'singularity_is_a'){
            this.busy = this.periodicReportService.postFiles(this.report.reportId, this.fileSingularityISTrancheA, 'NB_REP_S4A').subscribe(
                res => {
                    // clear upload file on view
                    this.fileSingularityISTrancheA = null;
                    // set file id
                    this.report.singularityISTrancheAFileId = res.fileId;
                    this.report.singularityISTrancheAFileName = res.fileName;
                    this.postAction(res != null && res.message != null && res.message.nameEn != null ? res.message.nameEn : "Successfully uploaded file - IS Singular Tranche A", null);
                },
                error => {
                    var result = JSON.parse(error);
                    var message = result != null && result.message != null && result.message.nameEn != null ? result.message.nameEn : null;
                    this.fileSingularityISTrancheA = null;
                    this.postAction(null, message != null && message != null ? message : "Error uploading file");
                });
        } else if(fileType === 'singularity_bs_b'){
            this.busy = this.periodicReportService.postFiles(this.report.reportId, this.fileSingularityBSTrancheB, 'NB_REP_S1B').subscribe(
                res => {
                    // clear upload file on view
                    this.fileSingularityBSTrancheB = null;
                    // set file id
                    this.report.singularityBSTrancheBFileId = res.fileId;
                    this.report.singularityBSTrancheBFileName = res.fileName;
                    this.postAction(res != null && res.message != null && res.message.nameEn != null ? res.message.nameEn : "Successfully uploaded file - BS Singular Tranche B", null);
                },
                error => {
                    var result = JSON.parse(error);
                    var message = result != null && result.message != null && result.message.nameEn != null ? result.message.nameEn : null;
                    this.fileSingularityBSTrancheB = null;
                    this.postAction(null, message != null && message != null ? message : "Error uploading file");
                });
        } else if(fileType === 'singularity_imdr_b'){
            this.busy = this.periodicReportService.postFiles(this.report.reportId, this.fileSingularityIMDRTrancheB, 'NB_REP_S2B').subscribe(
                res => {
                    // clear upload file on view
                    this.fileSingularityIMDRTrancheB = null;
                    // set file id
                    this.report.singularityIMDRTrancheBFileId = res.fileId;
                    this.report.singularityIMDRTrancheBFileName = res.fileName;
                    this.postAction(res != null && res.message != null && res.message.nameEn != null ? res.message.nameEn : "Successfully uploaded file - IMDR Singular Tranche B", null);
                },
                error => {
                    var result = JSON.parse(error);
                    var message = result != null && result.message != null && result.message.nameEn != null ? result.message.nameEn : null;
                    this.fileSingularityIMDRTrancheB = null;
                    this.postAction(null, message != null && message != null ? message : "Error uploading file");
                });
        } else if(fileType === 'singularity_par_b'){
            this.busy = this.periodicReportService.postFiles(this.report.reportId, this.fileSingularityPARTrancheB, 'NB_REP_S3B').subscribe(
                res => {
                    // clear upload file on view
                    this.fileSingularityPARTrancheB = null;
                    // set file id
                    this.report.singularityPARTrancheBFileId = res.fileId;
                    this.report.singularityPARTrancheBFileName = res.fileName;
                    this.postAction(res != null && res.message != null && res.message.nameEn != null ? res.message.nameEn : "Successfully uploaded file - PAR Singular Tranche B", null);
                },
                error => {
                    var result = JSON.parse(error);
                    var message = result != null && result.message != null && result.message.nameEn != null ? result.message.nameEn : null;
                    this.fileSingularityPARTrancheB = null;
                    this.postAction(null, message != null && message != null ? message : "Error uploading file");
                });
        } else if(fileType === 'singularity_is_b'){
            this.busy = this.periodicReportService.postFiles(this.report.reportId, this.fileSingularityISTrancheB, 'NB_REP_S4B').subscribe(
                res => {
                    // clear upload file on view
                    this.fileSingularityISTrancheB = null;
                    // set file id
                    this.report.singularityISTrancheBFileId = res.fileId;
                    this.report.singularityISTrancheBFileName = res.fileName;
                    this.postAction(res != null && res.message != null && res.message.nameEn != null ? res.message.nameEn : "Successfully uploaded file - IS Singular Tranche B", null);
                },
                error => {
                    var result = JSON.parse(error);
                    var message = result != null && result.message != null && result.message.nameEn != null ? result.message.nameEn : null;
                    this.fileSingularityISTrancheB = null;
                    this.postAction(null, message != null && message != null ? message : "Error uploading file");
                });
        }else if(fileType === 'singularity_general_ledger'){
            this.busy = this.periodicReportService.postFiles(this.report.reportId, this.fileSingularityGeneralLedger, 'NB_REP_SGL').subscribe(
                res => {
                    // clear upload file on view
                    this.fileSingularityGeneralLedger = null;
                    // set file id
                    this.report.singularityGeneralLedgerFileId = res.fileId;
                    this.report.singularityGeneralLedgerFileName = res.fileName;
                    this.postAction(res != null && res.message != null && res.message.nameEn != null ? res.message.nameEn : "Successfully uploaded file - Singularity General Ledger", null);
                },
                error => {
                    var result = JSON.parse(error);
                    var message = result != null && result.message != null && result.message.nameEn != null ? result.message.nameEn : null;
                    this.fileSingularityGeneralLedger = null;
                    this.postAction(null, message != null && message != null ? message : "Error uploading file");
                });
        }else if(fileType === 'singularity_noal_a'){
            this.busy = this.periodicReportService.postFiles(this.report.reportId, this.fileSingularityNOALTrancheA, 'NB_REP_SNA').subscribe(
                res => {
                    // clear upload file on view
                    this.fileSingularityNOALTrancheA = null;
                    // set file id
                    this.report.singularityNOALTrancheAFileId = res.fileId;
                    this.report.singularityNOALTrancheAFileName = res.fileName;
                    this.postAction(res != null && res.message != null && res.message.nameEn != null ? res.message.nameEn : "Successfully uploaded file - Singularity NOAL Tranche A", null);
                },
                error => {
                    var result = JSON.parse(error);
                    var message = result != null && result.message != null && result.message.nameEn != null ? result.message.nameEn : null;
                    this.fileSingularityNOALTrancheA = null;
                    this.postAction(null, message != null && message != null ? message : "Error uploading file");
                });
        }else if(fileType === 'singularity_noal_b'){
            this.busy = this.periodicReportService.postFiles(this.report.reportId, this.fileSingularityNOALTrancheB, 'NB_REP_SNB').subscribe(
                res => {
                    // clear upload file on view
                    this.fileSingularityNOALTrancheB = null;
                    // set file id
                    this.report.singularityNOALTrancheBFileId = res.fileId;
                    this.report.singularityNOALTrancheBFileName = res.fileName;
                    this.postAction(res != null && res.message != null && res.message.nameEn != null ? res.message.nameEn : "Successfully uploaded file - Singularity NOAL Tranche B", null);
                },
                error => {
                    var result = JSON.parse(error);
                    var message = result != null && result.message != null && result.message.nameEn != null ? result.message.nameEn : null;
                    this.fileSingularityNOALTrancheB = null;
                    this.postAction(null, message != null && message != null ? message : "Error uploading file");
                });
        }else if(fileType === 'singularity_itd'){
             this.busy = this.periodicReportService.postFiles(this.report.reportId, this.fileSingularityITD, 'NB_REP_ITD').subscribe(
                 res => {
                     // clear upload file on view
                     this.fileSingularityITD = null;
                     // set file id
                     this.report.singularityITDFileId = res.fileId;
                     this.report.singularityITDFileName = res.fileName;
                     this.postAction(res != null && res.message != null && res.message.nameEn != null ? res.message.nameEn : "Successfully uploaded file - Singularity ITD", null);
                 },
                 error => {
                     var result = JSON.parse(error);
                     var message = result != null && result.message != null && result.message.nameEn != null ? result.message.nameEn : null;
                     this.fileSingularityITD = null;
                     this.postAction(null, message != null && message != null ? message : "Error uploading file");
                 });
         }else if(fileType === 'terra_combined'){
            this.busy = this.periodicReportService.postFiles(this.report.reportId, this.fileTerraCombined, 'NB_REP_CMB').subscribe(
                res => {
                    // clear upload file on view
                    this.fileTerraCombined = null;
                    // set file id
                    this.report.terraCombinedFileId= res.fileId;
                    this.report.terraCombinedFileName = res.fileName;
                    this.postAction(res != null && res.message != null && res.message.nameEn != null ? res.message.nameEn : "Successfully uploaded file - Terra Combined", null);
                },
                error => {
                    var result = JSON.parse(error);
                    var message = result != null && result.message != null && result.message.nameEn != null ? result.message.nameEn : null;
                    this.fileTerraCombined = null;
                    this.postAction(null, message != null && message != null ? message : "Error uploading file");
                });
        }
        else if(fileType === 'terra_general_ledger'){
                this.busy = this.periodicReportService.postFiles(this.report.reportId, this.fileTerraGeneralLedger, 'NB_REP_TGL').subscribe(
                        res => {
                            // clear upload file on view
                            this.fileTerraGeneralLedger = null;
                            // set file id
                            this.report.terraGeneralLedgerFileId= res.fileId;
                            this.report.terraGeneralLedgerFileName = res.fileName;
                            this.postAction(res != null && res.message != null && res.message.nameEn != null ? res.message.nameEn : "Successfully uploaded file - Terra General Ledger", null);
                        },
                        error => {
                            var result = JSON.parse(error);
                            var message = result != null && result.message != null && result.message.nameEn != null ? result.message.nameEn : null;
                            this.fileTerraGeneralLedger = null;
                            this.postAction(null, message != null && message != null ? message : "Error uploading file");
                        });
                }
    }

    clearFile(fileType){
        if(fileType === 'tarragon_schedule_investment'){
            this.fileTarragonScheduleInvestment = null;
        } else if(fileType === 'tarragon_soi_report'){
           this.fileTarragonSOIReport = null;
       }else if(fileType === 'tarragon_statement_assets'){
            this.fileTarragonStatementAssets = null;
        } else if(fileType === 'tarragon_statement_cashflows'){
            this.fileTarragonStatementCashflows = null;
        } else if(fileType === 'tarragon_statement_changes'){
            this.fileTarragonStatementChanges = null;
        } else if(fileType === 'singularity_bs_a'){
            this.fileSingularityBSTrancheA = null;
        } else if(fileType === 'singularity_imdr_a'){
            this.fileSingularityIMDRTrancheA = null;
        } else if(fileType === 'singularity_par_a'){
            this.fileSingularityPARTrancheA = null;
        } else if(fileType === 'singularity_is_a'){
            this.fileSingularityISTrancheA = null;
        } else if(fileType === 'singularity_bs_b'){
            this.fileSingularityBSTrancheB = null;
        } else if(fileType === 'singularity_imdr_b'){
            this.fileSingularityIMDRTrancheB = null;
        } else if(fileType === 'singularity_par_b'){
            this.fileSingularityPARTrancheB = null;
        } else if(fileType === 'singularity_is_b'){
            this.fileSingularityISTrancheB = null;
        } else if(fileType === 'singularity_general_ledger'){
            this.fileSingularityGeneralLedger = null;
        } else if(fileType === 'singularity_noal_a'){
            this.fileSingularityNOALTrancheA = null;
        } else if(fileType === 'singularity_noal_b'){
            this.fileSingularityNOALTrancheB = null;
        } else if(fileType === 'singularity_itd'){
            this.fileSingularityITD = null;
        } else if(fileType === 'terra_combined'){
            this.fileTerraCombined = null;
        }else if(fileType === 'terra_general_ledger'){
            this.fileTerraGeneralLedger = null;
        }
    }

    convertToViewModel(filesList){
        if(filesList){
            for(var i = 0; i < filesList.length; i++){
                if(filesList[i].type == 'NB_REP_T1'){
                    this.report.tarragonScheduleInvestmentFileId = filesList[i].id;
                    this.report.tarragonScheduleInvestmentFileName = filesList[i].fileName;
                } else if(filesList[i].type == 'NB_REP_T5'){
                    this.report.tarragonSOIReportFileId = filesList[i].id;
                    this.report.tarragonSOIReportFileName = filesList[i].fileName;
                }else if(filesList[i].type == 'NB_REP_T2'){
                    this.report.tarragonStatementAssetsFileId = filesList[i].id;
                    this.report.tarragonStatementAssetsFileName = filesList[i].fileName;
                } else if(filesList[i].type == 'NB_REP_T3'){
                    this.report.tarragonStatementCashflowsFileId = filesList[i].id;
                    this.report.tarragonStatementCashflowsFileName = filesList[i].fileName;
                } else if(filesList[i].type == 'NB_REP_T4'){
                    this.report.tarragonStatementChangesFileId = filesList[i].id;
                    this.report.tarragonStatementChangesFileName = filesList[i].fileName;
                } else if(filesList[i].type == 'NB_REP_S1A'){
                    this.report.singularityBSTrancheAFileId = filesList[i].id;
                    this.report.singularityBSTrancheAFileName = filesList[i].fileName;
                } else if(filesList[i].type == 'NB_REP_S2A'){
                    this.report.singularityIMDRTrancheAFileId = filesList[i].id;
                    this.report.singularityIMDRTrancheAFileName = filesList[i].fileName;
                } else if(filesList[i].type == 'NB_REP_S3A'){
                    this.report.singularityPARTrancheAFileId = filesList[i].id;
                    this.report.singularityPARTrancheAFileName = filesList[i].fileName;
                } else if(filesList[i].type == 'NB_REP_S4A'){
                    this.report.singularityISTrancheAFileId = filesList[i].id;
                    this.report.singularityISTrancheAFileName = filesList[i].fileName;
                } else if(filesList[i].type == 'NB_REP_S1B'){
                    this.report.singularityBSTrancheBFileId = filesList[i].id;
                    this.report.singularityBSTrancheBFileName = filesList[i].fileName;
                } else if(filesList[i].type == 'NB_REP_S2B'){
                    this.report.singularityIMDRTrancheBFileId = filesList[i].id;
                    this.report.singularityIMDRTrancheBFileName = filesList[i].fileName;
                } else if(filesList[i].type == 'NB_REP_S3B'){
                    this.report.singularityPARTrancheBFileId = filesList[i].id;
                    this.report.singularityPARTrancheBFileName = filesList[i].fileName;
                } else if(filesList[i].type == 'NB_REP_S4B'){
                    this.report.singularityISTrancheBFileId = filesList[i].id;
                    this.report.singularityISTrancheBFileName = filesList[i].fileName;
                } else if(filesList[i].type == 'NB_REP_SGL'){
                    this.report.singularityGeneralLedgerFileId = filesList[i].id;
                    this.report.singularityGeneralLedgerFileName = filesList[i].fileName;
                } else if(filesList[i].type == 'NB_REP_SNA'){
                    this.report.singularityNOALTrancheAFileId = filesList[i].id;
                    this.report.singularityNOALTrancheAFileName = filesList[i].fileName;
                } else if(filesList[i].type == 'NB_REP_SNB'){
                    this.report.singularityNOALTrancheBFileId = filesList[i].id;
                    this.report.singularityNOALTrancheBFileName = filesList[i].fileName;
                } else if(filesList[i].type == 'NB_REP_ITD'){
                    this.report.singularityITDFileId = filesList[i].id;
                    this.report.singularityITDFileName = filesList[i].fileName;
                } else if(filesList[i].type == 'NB_REP_CMB'){
                    this.report.terraCombinedFileId = filesList[i].id;
                    this.report.terraCombinedFileName = filesList[i].fileName;
                }else if(filesList[i].type == 'NB_REP_TGL'){
                    this.report.terraGeneralLedgerFileId = filesList[i].id;
                    this.report.terraGeneralLedgerFileName = filesList[i].fileName;
                }
            }
        }
    }

    getReportDateShortFormatted(reportDate){
        if(reportDate){
            var monthNum = reportDate.split("-")[1];
            var yearNum = reportDate.split("-")[2];
            if(monthNum === '01'){
                return 'JAN ' + yearNum;
            }else if(monthNum === '02'){
                return 'FEB ' + yearNum;
            }else if(monthNum === '03'){
                return 'MAR ' + yearNum;
            }else if(monthNum === '04'){
                return 'APR ' + yearNum;
            }else if(monthNum === '05'){
                return 'MAY ' + yearNum;
            }else if(monthNum === '06'){
                return 'JUN ' + yearNum;
            }else if(monthNum === '07'){
                return 'JUL ' + yearNum;
            }else if(monthNum === '08'){
                return 'AUG ' + yearNum;
            }else if(monthNum === '09'){
                return 'SEP ' + yearNum;
            }else if(monthNum === '10'){
                return 'OCT ' + yearNum;
            }else if(monthNum === '11'){
                return 'NOV ' + yearNum;
            }else if(monthNum === '12'){
                return 'DEC ' + yearNum;
            }
        }
        return "";
    }

    onFileChange(event, fileType) {
        var target = event.target || event.srcElement;
        var files = target.files;
        if(fileType === 'tarragon_schedule_investment'){
            this.fileTarragonScheduleInvestment = files;
        } else if(fileType === 'tarragon_soi_report'){
            this.fileTarragonSOIReport = files;
        } else if(fileType === 'tarragon_statement_assets'){
            this.fileTarragonStatementAssets = files;
        } else if(fileType === 'tarragon_statement_cashflows'){
            this.fileTarragonStatementCashflows = files;
        } else if(fileType === 'tarragon_statement_changes'){
            this.fileTarragonStatementChanges = files;
        } else if(fileType === 'singularity_bs_a'){
            this.fileSingularityBSTrancheA = files;
        } else if(fileType === 'singularity_imdr_a'){
            this.fileSingularityIMDRTrancheA = files;
        } else if(fileType === 'singularity_par_a'){
            this.fileSingularityPARTrancheA = files;
        } else if(fileType === 'singularity_is_a'){
            this.fileSingularityISTrancheA = files;
        } else if(fileType === 'singularity_bs_b'){
            this.fileSingularityBSTrancheB = files;
        } else if(fileType === 'singularity_imdr_b'){
            this.fileSingularityIMDRTrancheB = files;
        } else if(fileType === 'singularity_par_b'){
            this.fileSingularityPARTrancheB = files;
        } else if(fileType === 'singularity_is_b'){
            this.fileSingularityISTrancheB = files;
        } else if(fileType === 'singularity_general_ledger'){
            this.fileSingularityGeneralLedger = files;
        } else if(fileType === 'singularity_noal_a'){
            this.fileSingularityNOALTrancheA = files;
        } else if(fileType === 'singularity_noal_b'){
            this.fileSingularityNOALTrancheB = files;
        }  else if(fileType === 'singularity_itd'){
            this.fileSingularityITD = files;
        }else if(fileType === 'terra_combined'){
            this.fileTerraCombined = files;
        }else if(fileType === 'terra_general_ledger'){
            this.fileTerraGeneralLedger = files;
        }


    }

    showUploadButton(fileType){
        if(fileType === 'tarragon_schedule_investment' && !this.fileTarragonScheduleInvestment && this.report != null && !this.report.tarragonScheduleInvestmentFileId){
            return true;
        }else if(fileType === 'tarragon_soi_report' && !this.fileTarragonSOIReport && this.report != null && !this.report.tarragonSOIReportFileId){
            return true;
        } else if(fileType === 'tarragon_statement_assets' && !this.fileTarragonStatementAssets && this.report != null && !this.report.tarragonStatementAssetsFileId){
            return true;
        } else if(fileType === 'tarragon_statement_cashflows' && !this.fileTarragonStatementCashflows && this.report != null && !this.report.tarragonStatementCashflowsFileId){
            return true;
        } else if(fileType === 'tarragon_statement_changes' && !this.fileTarragonStatementChanges && this.report != null && !this.report.tarragonStatementChangesFileId){
            return true;
        } else if(fileType === 'singularity_bs_a' && !this.fileSingularityBSTrancheA && this.report != null && !this.report.singularityBSTrancheAFileId){
            return true;
        } else if(fileType === 'singularity_imdr_a' && !this.fileSingularityIMDRTrancheA && this.report != null && !this.report.singularityIMDRTrancheAFileId){
            return true;
        } else if(fileType === 'singularity_par_a' && !this.fileSingularityPARTrancheA && this.report != null && !this.report.singularityPARTrancheAFileId){
            return true;
        } else if(fileType === 'singularity_is_a' && !this.fileSingularityISTrancheA && this.report != null && !this.report.singularityISTrancheAFileId){
            return true;
        } else if(fileType === 'singularity_bs_b' && !this.fileSingularityBSTrancheB && this.report != null && !this.report.singularityBSTrancheBFileId){
            return true;
        } else if(fileType === 'singularity_imdr_b' && !this.fileSingularityIMDRTrancheB && this.report != null && !this.report.singularityIMDRTrancheBFileId){
            return true;
        } else if(fileType === 'singularity_par_b' && !this.fileSingularityPARTrancheB && this.report != null && !this.report.singularityPARTrancheBFileId){
            return true;
        } else if(fileType === 'singularity_is_b' && !this.fileSingularityISTrancheB && this.report != null && !this.report.singularityISTrancheBFileId){
            return true;
        } else if(fileType === 'singularity_general_ledger' && !this.fileSingularityGeneralLedger && this.report != null && !this.report.singularityGeneralLedgerFileId){
            return true;
        } else if(fileType === 'singularity_noal_a' && !this.fileSingularityNOALTrancheA && this.report != null && !this.report.singularityNOALTrancheAFileId){
            return true;
        } else if(fileType === 'singularity_noal_b' && !this.fileSingularityNOALTrancheB && this.report != null && !this.report.singularityNOALTrancheBFileId){
            return true;
        } else if(fileType === 'singularity_itd' && !this.fileSingularityITD && this.report != null && !this.report.singularityITDFileId){
            return true;
        } else if(fileType === 'terra_combined' && !this.fileTerraCombined && this.report != null && !this.report.terraCombinedFileId){
            return true;
        }else if(fileType === 'terra_general_ledger' && !this.fileTerraGeneralLedger && this.report != null && !this.report.terraGeneralLedgerFileId){
            return true;
        }

        return false;
    }

    showDownloadButton(fileType){
        if(fileType === 'tarragon_schedule_investment' && this.report != null && this.report.tarragonScheduleInvestmentFileId > 0){
            return true;
        }else if(fileType === 'tarragon_soi_report' && this.report != null && this.report.tarragonSOIReportFileId > 0){
            return true;
        } else if(fileType === 'tarragon_statement_assets' && this.report != null && this.report.tarragonStatementAssetsFileId > 0){
            return true;
        } else if(fileType === 'tarragon_statement_cashflows' && this.report != null && this.report.tarragonStatementCashflowsFileId > 0){
            return true;
        } else if(fileType === 'tarragon_statement_changes' && this.report != null && this.report.tarragonStatementChangesFileId > 0){
            return true;
        } else if(fileType === 'singularity_bs_a' && this.report != null && this.report.singularityBSTrancheAFileId > 0){
            return true;
        } else if(fileType === 'singularity_imdr_a' && this.report != null && this.report.singularityIMDRTrancheAFileId > 0){
            return true;
        } else if(fileType === 'singularity_par_a' && this.report != null && this.report.singularityPARTrancheAFileId > 0){
            return true;
        } else if(fileType === 'singularity_is_a' && this.report != null && this.report.singularityISTrancheAFileId > 0){
            return true;
        } else if(fileType === 'singularity_bs_b' && this.report != null && this.report.singularityBSTrancheBFileId > 0){
            return true;
        } else if(fileType === 'singularity_imdr_b' && this.report != null && this.report.singularityIMDRTrancheBFileId > 0){
            return true;
        } else if(fileType === 'singularity_par_b' && this.report != null && this.report.singularityPARTrancheBFileId > 0){
            return true;
        } else if(fileType === 'singularity_is_b' && this.report != null && this.report.singularityISTrancheBFileId > 0){
            return true;
        } else if(fileType === 'singularity_general_ledger' && this.report != null && this.report.singularityGeneralLedgerFileId > 0){
            return true;
        } else if(fileType === 'singularity_noal_a' && this.report != null && this.report.singularityNOALTrancheAFileId > 0){
            return true;
        } else if(fileType === 'singularity_noal_b' && this.report != null && this.report.singularityNOALTrancheBFileId > 0){
            return true;
        } else if(fileType === 'singularity_itd' && this.report != null && this.report.singularityITDFileId > 0){
            return true;
        } else if(fileType === 'terra_combined' && this.report != null && this.report.terraCombinedFileId > 0){
            return true;
        }else if(fileType === 'terra_general_ledger' && this.report != null && this.report.terraGeneralLedgerFileId > 0){
            return true;
        }

        return false;
    }

    showProcessButton(fileType){
        if(fileType === 'tarragon_schedule_investment' && this.fileTarragonScheduleInvestment && this.report != null && !this.report.tarragonScheduleInvestmentFileId){
            return true;
        }else if(fileType === 'tarragon_soi_report' && this.fileTarragonSOIReport && this.report != null && !this.report.tarragonSOIReportFileId){
            return true;
        } else if(fileType === 'tarragon_statement_assets' && this.fileTarragonStatementAssets && this.report != null && !this.report.tarragonStatementAssetsFileId){
            return true;
        } else if(fileType === 'tarragon_statement_cashflows' && this.fileTarragonStatementCashflows && this.report != null && !this.report.tarragonStatementCashflowsFileId){
            return true;
        } else if(fileType === 'tarragon_statement_changes' && this.fileTarragonStatementChanges && this.report != null && !this.report.tarragonStatementChangesFileId){
            return true;
        } else if(fileType === 'singularity_bs_a' && this.fileSingularityBSTrancheA && this.report != null && !this.report.singularityBSTrancheAFileId){
            return true;
        } else if(fileType === 'singularity_imdr_a' && this.fileSingularityIMDRTrancheA && this.report != null && !this.report.singularityIMDRTrancheAFileId){
            return true;
        } else if(fileType === 'singularity_par_a' && this.fileSingularityPARTrancheA && this.report != null && !this.report.singularityPARTrancheAFileId){
            return true;
        } else if(fileType === 'singularity_is_a' && this.fileSingularityISTrancheA && this.report != null && !this.report.singularityISTrancheAFileId){
            return true;
        } else if(fileType === 'singularity_bs_b' && this.fileSingularityBSTrancheB && this.report != null && !this.report.singularityBSTrancheBFileId){
            return true;
        } else if(fileType === 'singularity_imdr_b' && this.fileSingularityIMDRTrancheB && this.report != null && !this.report.singularityIMDRTrancheBFileId){
            return true;
        } else if(fileType === 'singularity_par_b' && this.fileSingularityPARTrancheB && this.report != null && !this.report.singularityPARTrancheBFileId){
            return true;
        } else if(fileType === 'singularity_is_b' && this.fileSingularityISTrancheB && this.report != null && !this.report.singularityISTrancheBFileId){
            return true;
        } else if(fileType === 'singularity_general_ledger' && this.fileSingularityGeneralLedger && this.report != null && !this.report.singularityGeneralLedgerFileId){
            return true;
        } else if(fileType === 'singularity_noal_a' && this.fileSingularityNOALTrancheA && this.report != null && !this.report.singularityNOALTrancheAFileId){
            return true;
        } else if(fileType === 'singularity_noal_b' && this.fileSingularityNOALTrancheB && this.report != null && !this.report.singularityNOALTrancheBFileId){
            return true;
        } else if(fileType === 'singularity_itd' && this.fileSingularityITD && this.report != null && !this.report.singularityITDFileId){
            return true;
        }  else if(fileType === 'terra_combined' && this.fileTerraCombined && this.report != null && !this.report.terraCombinedFileId){
            return true;
        }else if(fileType === 'terra_general_ledger' && this.fileTerraGeneralLedger && this.report != null && !this.report.terraGeneralLedgerFileId){
            return true;
        }

        return false;
    }

    showViewButton(fileType){
        return this.showDownloadButton(fileType);
    }

    showDeleteButton(fileType){
        return this.periodicReport != null && this.periodicReport.status != 'SUBMITTED' && this.showViewButton(fileType) &&
            this.moduleAccessChecker.checkAccessReportingEditor();

        // TODO: check report status
    }

    deleteFile(fileType){
        if(!this.moduleAccessChecker.checkAccessReportingEditor()){
            return;
        }
        if(confirm("Are you sure want to delete this file?")) {
            if (fileType === 'tarragon_schedule_investment' && this.report != null && this.report.tarragonScheduleInvestmentFileId > 0) {
                this.periodicReportService.deleteFile(this.report.tarragonScheduleInvestmentFileId)
                    .subscribe(
                        response => {
                            this.report.tarragonScheduleInvestmentFileId = null;
                            this.report.tarragonScheduleInvestmentFileName = null;
                            this.postAction("File deleted.", null);
                        },
                        (error: ErrorResponse) => {
                            this.errorMessage = "Error deleting file";
                            if(error && !error.isEmpty()){
                                this.processErrorMessage(error);
                            }
                            this.postAction(null, this.errorMessage);
                        }
                    );
            } else if (fileType === 'tarragon_soi_report' && this.report != null && this.report.tarragonSOIReportFileId > 0) {
                this.periodicReportService.deleteFile(this.report.tarragonSOIReportFileId)
                    .subscribe(
                        response => {
                            this.report.tarragonSOIReportFileId = null;
                            this.report.tarragonSOIReportFileName = null;
                            this.postAction("File deleted.", null);
                        },
                        (error: ErrorResponse) => {
                            this.errorMessage = "Error deleting file";
                            if(error && !error.isEmpty()){
                                this.processErrorMessage(error);
                            }
                            this.postAction(null, this.errorMessage);
                        }
                    );
            } else if (fileType === 'tarragon_statement_assets' && this.report != null && this.report.tarragonStatementAssetsFileId > 0) {
                this.periodicReportService.deleteFile(this.report.tarragonStatementAssetsFileId)
                    .subscribe(
                        response => {
                            this.report.tarragonStatementAssetsFileId = null;
                            this.report.tarragonStatementAssetsFileName = null;
                            this.postAction("File deleted.", null);
                        },
                        (error: ErrorResponse) => {
                            this.errorMessage = "Error deleting file";
                            if(error && !error.isEmpty()){
                                this.processErrorMessage(error);
                            }
                            this.postAction(null, this.errorMessage);
                        }
                    );
            } else if (fileType === 'tarragon_statement_cashflows' && this.report != null && this.report.tarragonStatementCashflowsFileId > 0) {
                this.periodicReportService.deleteFile(this.report.tarragonStatementCashflowsFileId)
                    .subscribe(
                        response => {
                            this.report.tarragonStatementCashflowsFileId = null;
                            this.report.tarragonStatementCashflowsFileName = null;
                            this.postAction("File deleted.", null);
                        },
                        (error: ErrorResponse) => {
                            this.errorMessage = "Error deleting file";
                            if(error && !error.isEmpty()){
                                this.processErrorMessage(error);
                            }
                            this.postAction(null, this.errorMessage);
                        }
                    );
            } else if (fileType === 'tarragon_statement_changes' && this.report != null && this.report.tarragonStatementChangesFileId > 0) {
                this.periodicReportService.deleteFile(this.report.tarragonStatementChangesFileId)
                    .subscribe(
                        response => {
                            this.report.tarragonStatementChangesFileId = null;
                            this.report.tarragonStatementChangesFileName = null;
                            this.postAction("File deleted.", null);
                        },
                        (error: ErrorResponse) => {
                            this.errorMessage = "Error deleting file";
                            if(error && !error.isEmpty()){
                                this.processErrorMessage(error);
                            }
                            this.postAction(null, this.errorMessage);
                        }
                    );
            } else if (fileType === 'singularity_general_ledger' && this.report != null && this.report.singularityGeneralLedgerFileId > 0) {
                this.periodicReportService.deleteFile(this.report.singularityGeneralLedgerFileId)
                    .subscribe(
                        response => {
                            this.report.singularityGeneralLedgerFileId = null;
                            this.report.singularityGeneralLedgerFileName = null;
                            this.postAction("File deleted.", null);
                        },
                        (error: ErrorResponse) => {
                            this.errorMessage = "Error deleting file";
                            if(error && !error.isEmpty()){
                                this.processErrorMessage(error);
                            }
                            this.postAction(null, this.errorMessage);
                        }
                    );
            } else if (fileType === 'singularity_noal_a' && this.report != null && this.report.singularityNOALTrancheAFileId > 0) {
                this.periodicReportService.deleteFile(this.report.singularityNOALTrancheAFileId)
                    .subscribe(
                        response => {
                            this.report.singularityNOALTrancheAFileId = null;
                            this.report.singularityNOALTrancheAFileName = null;
                            this.postAction("File deleted.", null);
                        },
                        (error: ErrorResponse) => {
                            this.errorMessage = "Error deleting file";
                            if(error && !error.isEmpty()){
                                this.processErrorMessage(error);
                            }
                            this.postAction(null, this.errorMessage);
                        }
                    );
            } else if (fileType === 'singularity_noal_b' && this.report != null && this.report.singularityNOALTrancheBFileId > 0) {
                this.periodicReportService.deleteFile(this.report.singularityNOALTrancheBFileId)
                    .subscribe(
                        response => {
                            this.report.singularityNOALTrancheBFileId = null;
                            this.report.singularityNOALTrancheBFileName = null;
                            this.postAction("File deleted.", null);
                        },
                        (error: ErrorResponse) => {
                            this.errorMessage = "Error deleting file";
                            if(error && !error.isEmpty()){
                                this.processErrorMessage(error);
                            }
                            this.postAction(null, this.errorMessage);
                        }
                    );
            }else if (fileType === 'singularity_itd' && this.report != null && this.report.singularityITDFileId > 0) {
                this.periodicReportService.deleteFile(this.report.singularityITDFileId)
                    .subscribe(
                        response => {
                            this.report.singularityITDFileId = null;
                            this.report.singularityITDFileName = null;
                            this.postAction("File deleted.", null);
                        },
                        (error: ErrorResponse) => {
                            this.errorMessage = "Error deleting file";
                            if(error && !error.isEmpty()){
                                this.processErrorMessage(error);
                            }
                            this.postAction(null, this.errorMessage);
                        }
                    );
            } else if (fileType === 'terra_combined' && this.report != null && this.report.terraCombinedFileId > 0) {
                this.periodicReportService.deleteFile(this.report.terraCombinedFileId)
                    .subscribe(
                        response => {
                            this.report.terraCombinedFileId = null;
                            this.report.terraCombinedFileName = null;
                            this.postAction("File deleted.", null);
                        },
                        (error: ErrorResponse) => {
                            this.errorMessage = "Error deleting file";
                            if(error && !error.isEmpty()){
                                this.processErrorMessage(error);
                            }
                            this.postAction(null, this.errorMessage);
                        }
                    );
            }else if (fileType === 'terra_general_ledger' && this.report != null && this.report.terraGeneralLedgerFileId > 0) {
                 this.periodicReportService.deleteFile(this.report.terraGeneralLedgerFileId)
                     .subscribe(
                         response => {
                             this.report.terraGeneralLedgerFileId = null;
                             this.report.terraGeneralLedgerFileName = null;
                             this.postAction("File deleted.", null);
                         },
                         (error: ErrorResponse) => {
                             this.errorMessage = "Error deleting file";
                             if(error && !error.isEmpty()){
                                 this.processErrorMessage(error);
                             }
                             this.postAction(null, this.errorMessage);
                         }
                     );
             }
        }
    }

    showNextButton() {
/*
        if (this.report != null && !this.report.tarragonSOIReportFileId) {
            return false;
        } else if (this.report != null && !this.report.tarragonStatementAssetsFileId) {
            return false;
        //} else if (this.report != null && !this.report.tarragonStatementCashflowsFileId) {
        //    return false;
        } else if (this.report != null && !this.report.tarragonStatementChangesFileId) {
            return false;
        } else if(this.report != null && !this.report.singularityGeneralLedgerFileId){
            return false
        } else if(this.report != null && !this.report.singularityNOALTrancheAFileId){
            return false
        //} else if(this.report != null && !this.report.terraCombinedFileId){
        //    return false
        }else if(this.report != null && !this.report.terraGeneralLedgerFileId){
            return false
        }
*/


        //else if (this.report != null && !this.report.tarragonStatementChangesFileId) {
        //    return false;
        //} else if (this.report != null && !this.report.singularityBSTrancheAFileId) {
        //    return false;
        //} else if (this.report != null && !this.report.singularityIMDRTrancheAFileId) {
        //    return false;
        //} else if (this.report != null && !this.report.singularityPARTrancheAFileId) {
        //    return false;
        //} else if (this.report != null && !this.report.singularityISTrancheAFileId) {
        //    return false;
        //} else if (this.report != null && !this.report.singularityBSTrancheBFileId) {
        //    return false;
        //} else if (this.report != null && !this.report.singularityIMDRTrancheBFileId) {
        //    return false;
        //} else if (this.report != null && !this.report.singularityPARTrancheBFileId) {
        //    return false;
        //} else if (this.report != null && !this.report.singularityISTrancheBFileId) {
        //    return false;
        //} else if (this.report != null && !this.report.singularityGeneralLedgerFileId) {
        //    return false;
        //} else if (this.report != null && !this.report.singularityNOALTrancheAFileId) {
        //    return false;
        //} else if (this.report != null && !this.report.singularityNOALTrancheBFileId) {
        //    return false;
        //}
        return true;
    }

    public getFundRenameModal(){
        //alert("getFundRenameModal");


        // TODO: 1. Fork join - load fund names (PE, HF, RE)

        this.busyFundRenameDialog = Observable.forkJoin(
            // Load lookups
            this.periodicReportService.getFundNameList(this.reportId)
            )
            .subscribe(
                ([data1]) => {
                    this.fundNameListHolder = data1;
                    console.log(data1);

                    this.busyFundRenameDialog = this.periodicReportService.getFundRenameInfo(this.reportId)
                        .subscribe(
                            response  => {
                                this.fundRenameInfo = response;
                                if(this.fundRenameInfo && this.fundRenameInfo.fundRenames){
                                    this.fundRenamesPE = [];
                                    this.fundRenamesHF = [];
                                    this.fundRenamesRE = [];
                                    for(var i = 0; i < this.fundRenameInfo.fundRenames.length; i++){
                                        if(this.fundRenameInfo.fundRenames[i].type === "PE"){
                                            this.fundRenamesPE.push(this.fundRenameInfo.fundRenames[i]);
                                        }else if(this.fundRenameInfo.fundRenames[i].type === "HF"){
                                            this.fundRenamesHF.push(this.fundRenameInfo.fundRenames[i]);
                                        }else if(this.fundRenameInfo.fundRenames[i].type === "RE"){
                                            this.fundRenamesRE.push(this.fundRenameInfo.fundRenames[i]);
                                        }
                                    }
                                }else{
                                    this.fundRenameInfo = new ReportingFundRenameInfo();
                                    this.fundRenameInfo.fundRenames = [];
                                }
                            },
                            (error: ErrorResponse) => {
                                this.successMessageFundRename = null;
                                this.errorMessageFundRename = "Error loading fund rename info: id " + this.reportId;
                            }
                        );
                }
            );
    }

    saveFundRenames(){
        this.fundRenameInfo.fundRenames = [];
        // TODO: check for duplicate values        this.fundRenameInfo.fundRenames = [];
        if(this.fundRenamesPE != null && this.fundRenamesPE.length > 0){
            for(var i = 0; i < this.fundRenamesPE.length; i++){
                if($('#checkbox_pe_' + i).prop("checked")){
                   this.fundRenamesPE[i].usePreviousFundName = true;
                }else{
                    this.fundRenamesPE[i].usePreviousFundName = false;
                }
                this.fundRenameInfo.fundRenames.push(this.fundRenamesPE[i]);
            }
        }
        if(this.fundRenamesHF != null && this.fundRenamesHF.length > 0){
            for(var i = 0; i < this.fundRenamesHF.length; i++){
                if($('#checkbox_hf_' + i).prop("checked")){
                   this.fundRenamesHF[i].usePreviousFundName = true;
                }else{
                    this.fundRenamesHF[i].usePreviousFundName = false;
                }
                this.fundRenameInfo.fundRenames.push(this.fundRenamesHF[i]);
            }
        }
        if(this.fundRenamesRE != null && this.fundRenamesRE.length > 0){
            for(var i = 0; i < this.fundRenamesRE.length; i++){
                if($('#checkbox_re_' + i).prop("checked")){
                   this.fundRenamesRE[i].usePreviousFundName = true;
                }else{
                    this.fundRenamesRE[i].usePreviousFundName = false;
                }
                this.fundRenameInfo.fundRenames.push(this.fundRenamesRE[i]);
            }
        }

        this.fundRenameInfo.report = new PeriodicReport(this.reportId);
        this.busyFundRenameDialog = this.periodicReportService.saveFundRenameInfo(this.fundRenameInfo)
            .subscribe(
                response  => {
                    this.successMessageFundRename = "Successfully saved Fund Rename Info";
                    this.errorMessageFundRename = null;
                },
                (error: ErrorResponse) => {
                    this.successMessageFundRename = null;
                    this.errorMessageFundRename = "Error saving fund rename info: id " + this.reportId;
                }
            );
    }

    fundNameSelected(list, fundName){
    }

    public addPEFundRename(){
        if(this.fundRenamesPE == null){
            this.fundRenamesPE = [];
        }
        var element = new ReportingFundRenamePair();
        element.type = "PE";
        this.fundRenamesPE.push(element);
    }

    public addHFFundRename(){
        if(this.fundRenamesHF == null){
            this.fundRenamesHF = [];
        }
        var element = new ReportingFundRenamePair();
        element.type = "HF";
        this.fundRenamesHF.push(element);
    }


    public addREFundRename(){
        if(this.fundRenamesRE == null){
            this.fundRenamesRE = [];
        }
        var element = new ReportingFundRenamePair();
        element.type = "RE";
        this.fundRenamesRE.push(element);
    }

    deleteFundRename(list, fundName){
        //alert("deleteFundRename");
        if(confirm("Are you sure want to delete this record?")) {
            for (var i = 0; i < list.length; i++) {
                if (list[i] === fundName) {
                    list.splice(i, 1);
                }
            }
        }
    }

    public getInfo(fileType){
        if(fileType === 'tarragon_schedule_investment'){
            this.selectedInfoHeader = "Schedule of Investments";
            this.selectedInfoContent = InputFilesInfoLookupNBReport.SCHEDULE_INVESTMENTS_DESCRIPTION;
        }else if(fileType === 'tarragon_soi_report'){
            this.selectedInfoHeader = "SOI Report";
            this.selectedInfoContent = InputFilesInfoLookupNBReport.SCHEDULE_INVESTMENTS_DESCRIPTION;
        } else if(fileType === 'tarragon_statement_assets'){
            this.selectedInfoHeader = "Statement of Asssets, Liabilities, and Partners' Capital";
            this.selectedInfoContent = InputFilesInfoLookupNBReport.STATEMENT_ASSETS_LIABILITIES_PX_DESCRIPTION;
        } else if(fileType === 'tarragon_statement_cashflows'){
            this.selectedInfoHeader = "Statement of Cash flows";
            this.selectedInfoContent = InputFilesInfoLookupNBReport.STATEMENT_CASH_FLOWS_DESCRIPTION;
        } else if(fileType === 'tarragon_statement_changes'){
            this.selectedInfoHeader = "Statement of Changes";
            this.selectedInfoContent = InputFilesInfoLookupNBReport.STATEMENT_CHANGES_DESCRIPTION;
        }
        //else if(fileType === 'singularity_bs_a' && this.fileSingularityBSTrancheA && this.report != null && !this.report.singularityBSTrancheAFileId){
        //} else if(fileType === 'singularity_imdr_a' && this.fileSingularityIMDRTrancheA && this.report != null && !this.report.singularityIMDRTrancheAFileId){
        //} else if(fileType === 'singularity_par_a' && this.fileSingularityPARTrancheA && this.report != null && !this.report.singularityPARTrancheAFileId){
        //} else if(fileType === 'singularity_is_a' && this.fileSingularityISTrancheA && this.report != null && !this.report.singularityISTrancheAFileId){
        //} else if(fileType === 'singularity_bs_b' && this.fileSingularityBSTrancheB && this.report != null && !this.report.singularityBSTrancheBFileId){
        //} else if(fileType === 'singularity_imdr_b' && this.fileSingularityIMDRTrancheB && this.report != null && !this.report.singularityIMDRTrancheBFileId){
        //} else if(fileType === 'singularity_par_b' && this.fileSingularityPARTrancheB && this.report != null && !this.report.singularityPARTrancheBFileId){
        //} else if(fileType === 'singularity_is_b' && this.fileSingularityISTrancheB && this.report != null && !this.report.singularityISTrancheBFileId){
        else if(fileType === 'singularity_general_ledger'){
            this.selectedInfoHeader = "Singularity - General Ledger Balance";
            this.selectedInfoContent = InputFilesInfoLookupNBReport.GENERAL_LEDGER_BALANCE_DESCRIPTION;
        } else if(fileType === 'singularity_noal_a'){
            this.selectedInfoHeader = "Singularity - NOAL";
            this.selectedInfoContent = InputFilesInfoLookupNBReport.NOAL_DESCRIPTION;
        } else if(fileType === 'singularity_noal_b'){
            this.selectedInfoHeader = "Singularity - NOAL";
            this.selectedInfoContent = InputFilesInfoLookupNBReport.NOAL_DESCRIPTION;
        }
    }

    public closeInfoModal(){
        this.successMessageFundRename = null;
        this.errorMessageFundRename = null;

        this.fundRenamesPE = [];
        this.fundRenamesHF = [];
        this.fundRenamesRE = [];
    }


    // TODO: separate REST Service for file downloads etc and separate service
    download(fileType, fileId, fileName) {

        // Xhr creates new context so we need to create reference to this
        let self = this;

        // Status flag used in the template.
        //this.pending = true;

        // Create the Xhr request object
        let xhr = new XMLHttpRequest();
        xhr.withCredentials = true; // send auth token with the request
        // TODO: url const
        let url =  DATA_APP_URL + `files/download/${fileType}/${fileId}`;
        xhr.open('GET', url, true);
        xhr.responseType = 'blob';

        // Xhr callback when we get a result back
        // We are not using arrow function because we need the 'this' context
        xhr.onreadystatechange = function() {

            // We use setTimeout to trigger change detection in Zones
            setTimeout( () => {
                //self.pending = false;
            }, 0);

            // If we get an HTTP status OK (200), save the file using fileSaver
            if(xhr.readyState === 4 && xhr.status === 200) {
                var blob = new Blob([this.response], {type: this.response.type});
                fileSaver.saveAs(blob, fileName);

            }
        };

        // Start the Ajax request
        xhr.send();
    }

}