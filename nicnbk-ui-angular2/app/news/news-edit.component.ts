import {Component, OnInit} from '@angular/core';
import {LookupService} from "../common/lookup.service";
import {News} from "./news";

declare var $: any

@Component({
    selector: 'news-create',
    templateUrl: `app/news/news-edit.component.html`
})
export class NewsEditComponent implements OnInit{
    newsTypes = [];

    newsItem = new News;

    successMessage: string;
    errorMessage: string;

    constructor(
        private lookupService: LookupService
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

        alert(this.newsItem.id);
        alert(this.newsItem.type);
        alert(this.newsItem.header);
        alert(this.newsItem.source);
        alert(this.newsItem.content);

        this.successMessage = "Successfully saved.";
    }

    loadLookups(){
        // news types
        this.lookupService.getNewsTypes().then(newsTypes => this.newsTypes = newsTypes);
    }

}