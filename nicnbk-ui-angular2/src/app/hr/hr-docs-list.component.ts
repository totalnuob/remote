import {Component, OnInit} from '@angular/core';
import {CommonFormViewComponent} from "../common/common.component";
import {ModuleAccessCheckerService} from "../authentication/module.access.checker.service";
import {Subscription} from "../../../node_modules/rxjs";
import {HrDocsService} from "./hr-docs.service";
import {Router} from "@angular/router";
// import {FileDownloadService} from "../common/file.download.service";
// import {DATA_APP_URL} from "../common/common.service.constants";
import {ErrorResponse} from "../common/error-response";

declare var $: any;

@Component({
    selector: 'hr-docs-list',
    templateUrl: 'view/hr-docs-list.component.html',
    styleUrls: [],
    providers: [HrDocsService],
})
export class HRDocsListComponent extends CommonFormViewComponent implements OnInit {

    private moduleAccessChecker = new ModuleAccessCheckerService;

    // private FILE_DOWNLOAD_URL = DATA_APP_URL + "files/download/";

    private docsList = [];

    private myFiles: File[];

    busy: Subscription;

    constructor(
        private hrDocsService: HrDocsService,
        private router: Router,
        // private downloadService: FileDownloadService,
    ){
        super(router);

        this.myFiles = [];
    }

    ngOnInit(){
        this.getList();
    }

    canEdit(){
        return this.moduleAccessChecker.checkAccessHRDocsEditor();
    }

    fileChange(files: any){
        this.myFiles = files;
        console.log(this.myFiles);
    }

    private getList() {
        this.busy = this.hrDocsService.get()
            .subscribe(
                (response) => {
                    // console.log(response);
                    // console.log(this.docsList);
                    // console.log(response.message.nameEn);
                    this.docsList = response.filesDtoList;
                    // this.postAction(response.message.nameEn, null);
                },
                error => {
                    // console.log(error.message);
                    this.docsList = [];
                    this.postAction(null, error.message);
                }
            )
    }

    private onSubmitHRDocument() {
        this.busy = this.hrDocsService.postFiles(this.myFiles)
            .subscribe(
                (response) => {
                    // console.log(response);
                    // console.log(this.docsList);
                    // console.log(response.message.nameEn);
                    this.docsList = response.filesDtoList;
                    this.postAction(response.message.nameEn, null);
                    this.myFiles = [];
                    $("#fileupload").val(null);
                },
                error => {
                    // console.log(this.docsList);
                    // console.log(JSON.parse(error).message.nameEn);
                    this.postAction(null, JSON.parse(error).message.nameEn);
                }
            )
    }

    // private fileDownload(id) {
    //     this.busy = this.downloadService.makeFileRequest(this.FILE_DOWNLOAD_URL + "HR_DOCS/" + id, '')
    //         .subscribe(
    //             (response) => {
    //                 console.log("File downloaded!");
    //             },
    //             (error) => {
    //                 this.postAction(null, "Error loading file!");
    //                 console.log(error);
    //             }
    //         )
    // }

    deleteAttachment(fileId) {
        var confirmed = window.confirm("Are you sure you want to delete the document?");
        if(confirmed) {
            this.hrDocsService.deleteDocument(fileId)
                .subscribe(
                    (response) => {
                        this.docsList = response.filesDtoList;
                        this.postAction(response.message.nameEn, null);
                    },
                    (error: ErrorResponse) => {
                        this.postAction(null, error.message);
                    }
                );
        }
    }

    postAction(successMessage, errorMessage) {
        this.successMessage = successMessage;
        this.errorMessage = errorMessage;

        // TODO: non jQuery
        $('html, body').animate({scrollTop: 0}, 'fast');
    }
}