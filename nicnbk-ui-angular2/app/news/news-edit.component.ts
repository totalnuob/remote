import {Component, OnInit} from '@angular/core';
import {LookupService} from "../common/lookup.service";
import {News} from "./model/news";
import {NewsService} from "./news.service";

declare var $: any

@Component({
    selector: 'news-create',
    templateUrl: `app/news/view/news-edit.component.html`
})
export class NewsEditComponent implements OnInit{
    newsTypes = [];

    newsItem = new News;

    successMessage: string;
    errorMessage: string;

    constructor(
        private lookupService: LookupService,
        private newsService: NewsService
    ){}

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