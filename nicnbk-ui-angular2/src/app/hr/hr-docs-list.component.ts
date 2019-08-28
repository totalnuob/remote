import {Component, OnInit} from '@angular/core';
import {CommonFormViewComponent} from "../common/common.component";
import {ModuleAccessCheckerService} from "../authentication/module.access.checker.service";
import {Subscription} from "../../../node_modules/rxjs";
import {HrDocsService} from "./hr-docs.service";
import {Router} from "@angular/router";

declare var $: any;

@Component({
    selector: 'hr-docs-list',
    templateUrl: 'view/hr-docs-list.component.html',
    styleUrls: [],
    providers: [HrDocsService],
})
export class HRDocsListComponent extends CommonFormViewComponent implements OnInit {

    private moduleAccessChecker = new ModuleAccessCheckerService;

    private docsList = [];

    private myFiles: File[];

    busy: Subscription;

    constructor(
        private hrDocsService: HrDocsService,
        private router: Router,
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
                    this.docsList = response.filesDtoList;
                    console.log(this.docsList);

                    console.log(response.message.nameEn);
                    this.postAction(response.message.nameEn, null);
                },
                error => {
                    this.docsList = [];
                    console.log(error.message);
                    this.postAction(null, error.message);
                }
            )
    }

    private onSubmitHRDocument() {
        this.busy = this.hrDocsService.postFiles(this.myFiles)
            .subscribe(
                (response) => {
                    // console.log(response);
                    this.docsList = response.filesDtoList;
                    console.log(this.docsList);

                    console.log(response.message.nameEn);
                    this.postAction(response.message.nameEn, null);

                    this.myFiles = [];
                    $("#fileupload").val(null);
                },
                error => {
                    console.log(this.docsList);
                    console.log(JSON.parse(error).message.nameEn);
                    this.postAction(null, JSON.parse(error).message.nameEn);
                }
            )
    }

    postAction(successMessage, errorMessage) {
        this.successMessage = successMessage;
        this.errorMessage = errorMessage;

        // TODO: non jQuery
        $('html, body').animate({scrollTop: 0}, 'fast');
    }
}