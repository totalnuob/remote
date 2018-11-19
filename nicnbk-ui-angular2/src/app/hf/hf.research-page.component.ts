import {CommonFormViewComponent} from "../common/common.component";
import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {ErrorResponse} from "../common/error-response";
import {CommonFormViewComponent} from "../common/common.component";
import {ModuleAccessCheckerService} from "../authentication/module.access.checker.service";
import {Subscription} from 'rxjs';
import {HFManagerService} from "./hf.manager.service";
import {HFResearchPage} from "./model/hf.research-page";
import {HFManager} from "./model/hf.manager";

declare var $:any;

@Component({
    selector: 'hf-research-page',
    templateUrl: 'view/hf.research-page.component.html',
    styleUrls: [
        //'../../../public/css/...',
        '../../../node_modules/angular2-busy/build/style/busy.css'
    ],
    providers: []
})

export class HFResearchPageComponent extends CommonFormViewComponent implements OnInit{

    private moduleAccessChecker: ModuleAccessCheckerService;
    successMessage: string;
    errorMessage: string;
    public sub: any;
    busy: Subscription;

    public managerIdParam: number;
    public researchPageIdParam: number;

    researchPage = new HFResearchPage;

    public uploadFiles: Array<any> = [];

    constructor(
        private service: HFManagerService,
        private route: ActivatedRoute,
        private router: Router
    ){
        super(router);

        this.moduleAccessChecker = new ModuleAccessCheckerService;

        if(!this.moduleAccessChecker.checkAccessHedgeFunds()){
            this.router.navigate(['accessDenied']);
        }

        this.sub = this.route
            .params
            .subscribe(params => {
                this.managerIdParam = +params['managerId'];
                this.researchPageIdParam = params['pageId'];

                if(this.researchPageIdParam > 0) {
                    this.busy = this.service.getResearchPage(this.researchPageIdParam)
                        .subscribe(
                            data => {
                                this.researchPage = data;
                                this.researchPage.manager = new HFManager();
                                this.researchPage.manager.id = this.managerIdParam;
                                console.log(this.researchPage);
                            },
                            (error: ErrorResponse) => {
                                this.errorMessage = "Error loading HF Research page";
                                if(error && !error.isEmpty()){
                                    this.processErrorMessage(error);
                                }
                                this.postAction(null, null);
                                console.log("Error loading HF Research page");
                            }
                        );
                } else {
                    // new Research page
                }
            });
    }

    saveResearchPage(){
        this.researchPage.manager = new HFManager();
        this.researchPage.manager.id = this.managerIdParam;
        this.researchPage.date = $('#researchDate').val();

        this.service.saveResearchPage(this.researchPage)
            .subscribe(
                response => {
                    this.researchPage.id = response.entityId;
                    this.researchPage.creationDate = response.creationDate;

                    if(this.uploadFiles.length > 0) {
                        this.service.postFiles(this.researchPage.id, [], this.uploadFiles)
                            .subscribe(
                                res => {
                                    // clear upload files list on view
                                    this.uploadFiles.length = 0;

                                    // update files list with new files
                                    if (!this.researchPage.files) { // no files existed
                                        this.researchPage.files = [];
                                    }

                                    for (var i = 0; i < res.length; i++) {
                                        this.researchPage.files.push(res[i]);
                                    }
                                    this.postAction("Successfully saved.", null);
                                },
                                error => {
                                    // TODO: don't save research page?

                                    this.postAction(null, "Error uploading attachments.");
                            });
                    } else {
                        this.postAction("Successfully saved.", null);
                    }
                },
                (error: ErrorResponse) => {
                    this.errorMessage = "Error saving HF Research page";
                    if(error && !error.isEmpty()){
                        this.processErrorMessage(error);
                    }
                    this.postAction(null, null);
                }
            );
    }

    deleteAttachment(fileId){
        var confirmed = window.confirm("Are you sure want to delete");
        if(confirmed) {
            this.service.deleteAttachment(this.researchPage.id, fileId)
                .subscribe(
                    response => {
                        for(var i = this.researchPage.files.length - 1; i >= 0; i--) {
                            if(this.researchPage.files[i].id === fileId) {
                                this.researchPage.files.splice(i, 1);
                            }
                        }

                        this.postAction("Attachment deleted.", null);
                    },
                    (error: ErrorResponse) => {
                        this.errorMessage = "Error deleting attachment";
                        if(error && !error.isEmpty()){
                            this.processErrorMessage(error);
                        }
                        this.postAction(null, null);
                    }
                );
        }
    }

    onFileChange(event) {
        var target = event.target || event.srcElement;
        var files = target.files;
        this.uploadFiles.length = 0;
        for (let i = 0; i < files.length; i++) {
            this.uploadFiles.push(files[i]);
        }
    }

    public canEdit(){
        return this.moduleAccessChecker.checkAccessHedgeFundsEditor();
    }

    remove(page){
        if(confirm('Are yu sure?')){
            this.busy = this.service.deleteResearchPage(page.id)
                .subscribe(
                    (response) => {
                        console.log("Deleted");
                    },
                    (error: ErrorResponse) => {
                        this.processErrorMessage(error);
                        this.postAction(null, error.message);
                        console.log(error);
                    }
                );
        } else {
            this.postAction(null, null);
            console.log('Not deleted');
        }
    }

    ngOnInit():any {

        this.postAction(null, null);

        // TODO: exclude jQuery
        // datetimepicker
        $('#researchDate').datetimepicker({
            //defaultDate: new Date(),
            format: 'DD-MM-YYYY'
        });


    }
}