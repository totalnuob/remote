import {Component} from "@angular/core";
import {CommonFormViewComponent} from "../common/common.component";
import {Router, ActivatedRoute} from '@angular/router';
import {InputFilesNBReport} from "./model/input.files.nb.report";
import {Subscription} from "rxjs/Subscription";
import {PeriodicReportService} from "./periodic.report.service";
import {ErrorResponse} from "../common/error-response";
import {DATA_APP_URL} from "../common/common.service.constants";
import {FileUploadResult} from "./model/file.uopload.result";

var fileSaver = require("file-saver");

@Component({
    selector: 'input-file-upload-nb-reporting',
    templateUrl: 'view/input.file.upload.nb.reporting.component.html',
    styleUrls: [],
    providers: [],
})
export class InputFileUploadNBReportingComponent extends CommonFormViewComponent {

    private sub: any;
    private reportId;

    busy: Subscription;

    private report: InputFilesNBReport;

    private fileTarragonScheduleInvestment;
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

    constructor(
        private router: Router,
        private route: ActivatedRoute,
        private periodicReportService: PeriodicReportService
    ){
        super(router);

        this.sub = this.route
            .params
            .subscribe(params => {
                this.reportId = +params['id'];

                if(this.reportId > 0){
                    // load report data

                    this.busy = this.periodicReportService.get(this.reportId)
                        .subscribe(
                            response  => {
                                this.report = response;
                                this.report.reportId = this.reportId;

                                // load existing report files info
                                this.busy = this.periodicReportService.loadInputFilesList(this.reportId)
                                    .subscribe(
                                        response  => {
                                            this.convertToViewModel(response);
                                        },
                                        (error: ErrorResponse) => {
                                            this.successMessage = null;
                                            this.errorMessage = "Error loading report files";
                                            this.report = null;
                                            if(error && !error.isEmpty()){
                                                this.processErrorMessage(error);
                                            }
                                            this.postAction(null, null);
                                        }
                                    )
                            },
                            (error: ErrorResponse) => {
                                this.successMessage = null;
                                this.errorMessage = "Error loading report";
                                this.report = null;
                                if(error && !error.isEmpty()){
                                    this.processErrorMessage(error);
                                }
                                this.postAction(null, null);
                            }
                        );

                }else{
                    this.report = new InputFilesNBReport();
                }
            });
    }

    uploadFile(fileType){
        if(fileType === 'tarragon_schedule_investment'){
            this.busy = this.periodicReportService.postFiles(this.report.reportId, this.fileTarragonScheduleInvestment, 'NB_REP_T1').subscribe(
                res => {
                    // clear upload file on view
                    this.fileTarragonScheduleInvestment = null;
                    // set file id
                    this.report.tarragonScheduleInvestmentFileId = res.fileId;
                    this.postAction(res != null && res.messageEn != null ? res.messageEn : "Successfully uploaded file", null);
                },
                error => {
                    var result = JSON.parse(error);
                    var message = result != null && result.messageEn != null ? result.messageEn : null;
                    this.postAction(null, message != null && message != null ? message : "Error uploading file");
                });
        } else if(fileType === 'tarragon_statement_assets'){
            console.log("tarragon_statement_assets");
            this.busy = this.periodicReportService.postFiles(this.report.reportId, this.fileTarragonStatementAssets, 'NB_REP_T2').subscribe(
                res => {
                    // clear upload file on view
                    this.fileTarragonStatementAssets = null;
                    // set file id
                    this.report.tarragonStatementAssetsFileId = res.fileId;
                    this.postAction(res != null && res.messageEn != null ? res.messageEn : "Successfully uploaded file", null);
                },
                (error) => {
                    var result = JSON.parse(error);
                    var message = result != null && result.messageEn != null ? result.messageEn : null;
                    this.postAction(null, message != null && message != null ? message : "Error uploading file");
                });
        } else if(fileType === 'tarragon_statement_cashflows'){
            this.busy = this.periodicReportService.postFiles(this.report.reportId, this.fileTarragonStatementCashflows, 'NB_REP_T3').subscribe(
                res => {
                    // clear upload file on view
                    this.fileTarragonStatementCashflows = null;
                    // set file id
                    this.report.tarragonStatementCashflowsFileId = res.fileId;
                    this.postAction(res != null && res.messageEn != null ? res.messageEn : "Successfully uploaded file", null);
                },
                error => {
                    var result = JSON.parse(error);
                    var message = result != null && result.messageEn != null ? result.messageEn : null;
                    this.postAction(null, message != null && message != null ? message : "Error uploading file");
                });
        } else if(fileType === 'tarragon_statement_changes'){
            this.busy = this.periodicReportService.postFiles(this.report.reportId, this.fileTarragonStatementChanges, 'NB_REP_T4').subscribe(
                res => {
                    // clear upload file on view
                    this.fileTarragonStatementChanges = null;
                    // set file id
                    this.report.tarragonStatementChangesFileId = res.fileId;
                    this.postAction(res != null && res.messageEn != null ? res.messageEn : "Successfully uploaded file", null);
                },
                error => {
                    var result = JSON.parse(error);
                    var message = result != null && result.messageEn != null ? result.messageEn : null;
                    this.postAction(null, message != null && message != null ? message : "Error uploading file");
                });
        } else if(fileType === 'singularity_bs_a'){
            this.busy = this.periodicReportService.postFiles(this.report.reportId, this.fileSingularityBSTrancheA, 'NB_REP_S1A').subscribe(
                res => {
                    // clear upload file on view
                    this.fileSingularityBSTrancheA = null;
                    // set file id
                    this.report.singularityBSTrancheAFileId = res.fileId;
                    this.postAction(res != null && res.messageEn != null ? res.messageEn : "Successfully uploaded file", null);
                },
                error => {
                    var result = JSON.parse(error);
                    var message = result != null && result.messageEn != null ? result.messageEn : null;
                    this.postAction(null, message != null && message != null ? message : "Error uploading file");
                });
        } else if(fileType === 'singularity_imdr_a'){
            this.busy = this.periodicReportService.postFiles(this.report.reportId, this.fileSingularityIMDRTrancheA, 'NB_REP_S2A').subscribe(
                res => {
                    // clear upload file on view
                    this.fileSingularityIMDRTrancheA = null;
                    // set file id
                    this.report.singularityIMDRTrancheAFileId = res.fileId;
                    this.postAction(res != null && res.messageEn != null ? res.messageEn : "Successfully uploaded file", null);
                },
                error => {
                    var result = JSON.parse(error);
                    var message = result != null && result.messageEn != null ? result.messageEn : null;
                    this.postAction(null, message != null && message != null ? message : "Error uploading file");
                });
        } else if(fileType === 'singularity_par_a'){
            this.busy = this.periodicReportService.postFiles(this.report.reportId, this.fileSingularityPARTrancheA, 'NB_REP_S3A').subscribe(
                res => {
                    // clear upload file on view
                    this.fileSingularityPARTrancheA = null;
                    // set file id
                    this.report.singularityPARTrancheAFileId = res.fileId;
                    this.postAction(res != null && res.messageEn != null ? res.messageEn : "Successfully uploaded file", null);
                },
                error => {
                    var result = JSON.parse(error);
                    var message = result != null && result.messageEn != null ? result.messageEn : null;
                    this.postAction(null, message != null && message != null ? message : "Error uploading file");
                });
        } else if(fileType === 'singularity_is_a'){
            this.busy = this.periodicReportService.postFiles(this.report.reportId, this.fileSingularityISTrancheA, 'NB_REP_S4A').subscribe(
                res => {
                    // clear upload file on view
                    this.fileSingularityISTrancheA = null;
                    // set file id
                    this.report.singularityISTrancheAFileId = res.fileId;
                    this.postAction(res != null && res.messageEn != null ? res.messageEn : "Successfully uploaded file", null);
                },
                error => {
                    var result = JSON.parse(error);
                    var message = result != null && result.messageEn != null ? result.messageEn : null;
                    this.postAction(null, message != null && message != null ? message : "Error uploading file");
                });
        } else if(fileType === 'singularity_bs_b'){
            this.busy = this.periodicReportService.postFiles(this.report.reportId, this.fileSingularityBSTrancheB, 'NB_REP_S1B').subscribe(
                res => {
                    // clear upload file on view
                    this.fileSingularityBSTrancheB = null;
                    // set file id
                    this.report.singularityBSTrancheBFileId = res.fileId;
                    this.postAction(res != null && res.messageEn != null ? res.messageEn : "Successfully uploaded file", null);
                },
                error => {
                    var result = JSON.parse(error);
                    var message = result != null && result.messageEn != null ? result.messageEn : null;
                    this.postAction(null, message != null && message != null ? message : "Error uploading file");
                });
        } else if(fileType === 'singularity_imdr_b'){
            this.busy = this.periodicReportService.postFiles(this.report.reportId, this.fileSingularityIMDRTrancheB, 'NB_REP_S2B').subscribe(
                res => {
                    // clear upload file on view
                    this.fileSingularityIMDRTrancheB = null;
                    // set file id
                    this.report.singularityIMDRTrancheBFileId = res.fileId;
                    this.postAction(res != null && res.messageEn != null ? res.messageEn : "Successfully uploaded file", null);
                },
                error => {
                    var result = JSON.parse(error);
                    var message = result != null && result.messageEn != null ? result.messageEn : null;
                    this.postAction(null, message != null && message != null ? message : "Error uploading file");
                });
        } else if(fileType === 'singularity_par_b'){
            this.busy = this.periodicReportService.postFiles(this.report.reportId, this.fileSingularityPARTrancheB, 'NB_REP_S3B').subscribe(
                res => {
                    // clear upload file on view
                    this.fileSingularityPARTrancheB = null;
                    // set file id
                    this.report.singularityPARTrancheBFileId = res.fileId;
                    this.postAction(res != null && res.messageEn != null ? res.messageEn : "Successfully uploaded file", null);
                },
                error => {
                    var result = JSON.parse(error);
                    var message = result != null && result.messageEn != null ? result.messageEn : null;
                    this.postAction(null, message != null && message != null ? message : "Error uploading file");
                });
        } else if(fileType === 'singularity_is_b'){
            this.busy = this.periodicReportService.postFiles(this.report.reportId, this.fileSingularityISTrancheB, 'NB_REP_S4B').subscribe(
                res => {
                    // clear upload file on view
                    this.fileSingularityISTrancheB = null;
                    // set file id
                    this.report.singularityISTrancheBFileId = res.fileId;
                    this.postAction(res != null && res.messageEn != null ? res.messageEn : "Successfully uploaded file", null);
                },
                error => {
                    var result = JSON.parse(error);
                    var message = result != null && result.messageEn != null ? result.messageEn : null;
                    this.postAction(null, message != null && message != null ? message : "Error uploading file");
                });
        }
    }

    convertToViewModel(filesList){
        if(filesList){
            for(var i = 0; i < filesList.length; i++){
                if(filesList[i].type == 'NB_REP_T1'){
                    this.report.tarragonScheduleInvestmentFileId = filesList[i].id;
                    this.report.tarragonScheduleInvestmentFileName = filesList[i].fileName;
                } else if(filesList[i].type == 'NB_REP_T2'){
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
        }


    }

    showUploadButton(fileType){
        if(fileType === 'tarragon_schedule_investment' && !this.fileTarragonScheduleInvestment && this.report != null && !this.report.tarragonScheduleInvestmentFileId){
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
        }

        return false;
    }

    showDownloadButton(fileType){
        if(fileType === 'tarragon_schedule_investment' && this.report != null && this.report.tarragonScheduleInvestmentFileId > 0){
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
        }
        return false;
    }

    showProcessButton(fileType){
        if(fileType === 'tarragon_schedule_investment' && this.fileTarragonScheduleInvestment && this.report != null && !this.report.tarragonScheduleInvestmentFileId){
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
        }

        return false;
    }

    showViewButton(fileType){
        return this.showDownloadButton(fileType);
    }

    showNextButton() {
        if (this.report != null && !this.report.tarragonScheduleInvestmentFileId) {
            return false;
        } else if (this.report != null && !this.report.tarragonStatementAssetsFileId) {
            return false;
        } else if (this.report != null && !this.report.tarragonStatementCashflowsFileId) {
            return false;
        } else if (this.report != null && !this.report.tarragonStatementChangesFileId) {
            return false;
        } else if (this.report != null && !this.report.singularityBSTrancheAFileId) {
            return false;
        } else if (this.report != null && !this.report.singularityIMDRTrancheAFileId) {
            return false;
        } else if (this.report != null && !this.report.singularityPARTrancheAFileId) {
            return false;
        } else if (this.report != null && !this.report.singularityISTrancheAFileId) {
            return false;
        } else if (this.report != null && !this.report.singularityBSTrancheBFileId) {
            return false;
        } else if (this.report != null && !this.report.singularityIMDRTrancheBFileId) {
            return false;
        } else if (this.report != null && !this.report.singularityPARTrancheBFileId) {
            return false;
        } else if (this.report != null && !this.report.singularityISTrancheBFileId) {
            return false;
        }
        return true;
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
        let url =  DATA_APP_URL + `periodicReport/inputFiles/download/${fileType}/${fileId}`;
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