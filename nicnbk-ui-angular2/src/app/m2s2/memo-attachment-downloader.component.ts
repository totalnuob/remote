import {Component,Input } from '@angular/core';
import {BrowserXhr} from '@angular/http';
import {CommonService} from "../common/common.service";
import {DATA_APP_URL} from "../common/common.service.constants";

var fileSaver = require("file-saver");

@Component({
    selector: 'memo-attachment-downloader',
    templateUrl: './view/memo-attachment-downloader.component.html',
})

export class MemoAttachmentDownloaderComponent  extends CommonService{

    @Input()
    fileId: number;
    @Input()
    fileName: string;

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
        // TODO: url const
        let url =  DATA_APP_URL + `m2s2/attachment/${this.fileId}`;
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
                fileSaver.saveAs(blob, self.fileName);
            }
        };

        // Start the Ajax request
        xhr.send();
    }
}