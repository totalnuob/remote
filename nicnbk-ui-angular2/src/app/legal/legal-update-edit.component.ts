import {Component, OnInit} from '@angular/core';
import {Router, ActivatedRoute} from '@angular/router';
import {LookupService} from "../common/lookup.service";
import {LegalService} from "../legal/legal.service";
import '../../../public/js/jquery.hotkeys.js';
import '../../../public/js/bootstrap/bootstrap-wysiwyg.js';
import {Subscription} from 'rxjs';
import {ModuleAccessCheckerService} from "../authentication/module.access.checker.service";
import {CommonFormViewComponent} from "../common/common.component";
import {ErrorResponse} from "../common/error-response";

declare var $:any

@Component({
    selector: 'legal-update-edit',
    templateUrl: './view/legal-update-edit.component.html'
})
export class LegalUpdateEditComponent extends CommonFormViewComponent implements OnInit{

    newsItem = {};
    successMessage: string;
    errorMessage: string;

    private sub: any;
    private newsIdParam: number;
    busy: Subscription;

    private moduleAccessChecker: ModuleAccessCheckerService;
    private breadcrumbParams: string;

    constructor(
        private lookupService: LookupService,
        private legalService: LegalService,
        private router: Router,
        private route: ActivatedRoute
    ){
        super(router);
        this.moduleAccessChecker = new ModuleAccessCheckerService;

        if(!this.moduleAccessChecker.checkAccessLegalEditor()) {
            this.router.navigate(['accessDenied']);
        }

        this.sub = this.route
            .params
            .subscribe(params => {
                this.newsIdParam = +params['id'];
                this.breadcrumbParams = JSON.stringify({"activeTab": "LEGAL_UPDATES"});
                if(this.newsIdParam > 0) {
                     this.busy = this.legalService.getLegalUpdateById(this.newsIdParam)
                        .subscribe(
                            newsItem => {
                                this.newsItem = newsItem;
                                $('#editor').html(newsItem.content);
                            },
                            (error:ErrorResponse) => {
                                this.errorMessage = "Error loading legal update";
                                if (error && !error.isEmpty()) {
                                    this.processErrorMessage(error);
                                }
                                this.postAction(null, null);
                            }
                        );
                } else {
                }
            });
    }

    ngOnInit(){
        // TODO: remove jQuery
        // setup editor
        $('#editor').wysiwyg();
        $('#editor').cleanHtml();
        var itemId = $('#newsItemId').val();
        if(itemId === ''){
            $('#editor').html('Enter body..');
        }else{
            $('#editor').html($('#newsContent').val());
        }
    }

    save(){
        // TODO: remove jQuery
        this.newsItem.content = $("#editor").html().replace(/<script[^>]*>/gi, "&lt;script&rt;").replace(/<\/script[^>]*>/gi, "&lt;/script&rt;");
        this.busy = this.legalService.save(this.newsItem)
            .subscribe(
                response  => {
                    //console.log(response);
                    this.successMessage = "Successfully saved.";
                    this.errorMessage = null;

                    this.newsItem.id = response.entityId;

                    // TODO: rafactor?s
                    $('html, body').animate({ scrollTop: 0 }, 'fast');
                },
                (error: ErrorResponse) => {
                    this.successMessage = null;
                    this.errorMessage = "Error saving legal update";
                    if(error && !error.isEmpty()){
                        this.processErrorMessage(error);
                    }
                    this.postAction(null, null);
                }
            );
    }


}