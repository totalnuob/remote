import {Component,Input } from '@angular/core';
import {BrowserXhr} from '@angular/http';
import {CommonService} from "../common/common.service";

declare var saveAs: any; // file-saver

@Component({
    selector: 'memo-attachment-downloader',
    template: `
        <a (click)="download()">{{fileName}}
            <!--<i class="glyphicon glyphicon-download-alt" *ngIf="!pending"></i>-->
            <i class="glyphicon glyphicon-refresh" *ngIf="pending"></i>
        </a>
        `
})

export class MemoAttachmentDownloaderComponent  extends CommonService{

    @Input()
    private fileId: number;
    @Input()
    private fileName: string;

    public pending:boolean = false;

    constructor() {
        super();
    }

    public download() {

        // Xhr creates new context so we need to create reference to this
        let self = this;

        // Status flag used in the template.
        this.pending = true;

        // Create the Xhr request object
        let xhr = new XMLHttpRequest();
        let url =  `http://localhost:8089/m2s2/attachment/${this.fileId}`;
        xhr.open('GET', url, true);
        xhr.responseType = 'blob';

        // Xhr callback when we get a result back
        // We are not using arrow function because we need the 'this' context
        xhr.onreadystatechange = function() {

            // We use setTimeout to trigger change detection in Zones
            setTimeout( () => { self.pending = false; }, 0);

            // If we get an HTTP status OK (200), save the file using fileSaver
            if(xhr.readyState === 4 && xhr.status === 200) {
                var blob = new Blob([this.response], {type: this.response.type});
                saveAs(blob, self.fileName);
            }
        };

        // Start the Ajax request
        xhr.send();
    }
}