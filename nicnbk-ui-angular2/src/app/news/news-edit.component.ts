import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {LookupService} from "../common/lookup.service";
import {News} from "./model/news";
import {NewsService} from "./news.service";

import '../../../public/js/jquery.hotkeys.js';
import '../../../public/js/bootstrap/bootstrap-wysiwyg.js';
import {ModuleAccessCheckerService} from "../authentication/module.access.checker.service";

declare var $:any

@Component({
    selector: 'news-create',
    templateUrl: './view/news-edit.component.html'
})
export class NewsEditComponent implements OnInit{
    newsTypes = [];

    newsItem = new News;

    successMessage: string;
    errorMessage: string;

    private moduleAccessChecker: ModuleAccessCheckerService;

    constructor(
        private lookupService: LookupService,
        private newsService: NewsService,
        private router: Router
    ){
        this.moduleAccessChecker = new ModuleAccessCheckerService;

        if(!this.moduleAccessChecker.checkAccessNewsEdit()) {
            this.router.navigate(['accessDenied']);
        }
    }

    ngOnInit(){
        // load news types
        this.loadLookups();


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
        this.newsItem.content = $("#editor").html();

        this.newsService.save(this.newsItem)
            .subscribe(
                response  => {
                    //console.log(response);
                    this.successMessage = "Successfully saved.";
                    this.errorMessage = null;

                    // TODO: rafactor?
                    $('html, body').animate({ scrollTop: 0 }, 'fast');
                },
                error =>  {
                    this.successMessage = null;
                    this.errorMessage = <any>error;

                    // TODO: rafactor?
                    $('html, body').animate({ scrollTop: 0 }, 'fast');
                }
            );
    }

    loadLookups(){
        // news types
        this.lookupService.getNewsTypes().then(newsTypes => this.newsTypes = newsTypes);
    }

}