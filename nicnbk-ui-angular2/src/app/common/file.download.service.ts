import { Injectable } from '@angular/core';
import {CommonService} from "./common.service";
import {Observable} from "rxjs/Observable";

var fileSaver = require("file-saver");

@Injectable()
export class FileDownloadService extends CommonService {

    public makeFileRequest(url, fileName): Observable<Response> {
        return Observable.fromPromise(new Promise((resolve, reject) => {
            let xhr = new XMLHttpRequest();
            xhr.withCredentials = true; // send auth token with the request
            // TODO: url const
            //let url =  DATA_APP_URL + `periodicReport/export/${this.reportId}/${'KZT_FORM_1'}`;
            xhr.open('GET', url, true);
            xhr.responseType = 'blob';

            // Xhr callback when we get a result back
            // We are not using arrow function because we need the 'this' context
            xhr.onreadystatechange = function () {
                // We use setTimeout to trigger change detection in Zones
                setTimeout(() => {
                    //self.pending = false;
                }, 0);

                // If we get an HTTP status OK (200), save the file using fileSaver
                if (xhr.readyState === 4) {
                    if (xhr.status === 200) {
                        console.log("OK " + xhr.status);
                        resolve(JSON.parse("{\"message\" : \"OK\"}"));
                        var blob = new Blob([this.response], {type: this.response.type});
                        fileSaver.saveAs(blob, fileName);
                    }else {
                        console.log("Error - " + xhr.status);
                        console.log(xhr);
                        reject(xhr.response);
                    }
                }
            };
            // Start the Ajax request
            //xhr.open("GET", url);
            xhr.send();
        }));
        //.map(this.extractData);
        //.catch(this.handleErrorResponse);
    }
}