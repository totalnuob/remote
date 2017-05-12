import { Injectable } from '@angular/core';
import {Observable} from 'rxjs/Rx';

@Injectable()
export class FileUploadService {
    constructor () {
    }

    public postFiles (url: string, params: string[], files: File[], type: string): Observable<any> {
        return Observable.create(observer => {
            let formData: FormData = new FormData(),
                xhr: XMLHttpRequest = new XMLHttpRequest();

            for (let i = 0; i < files.length; i++) {
                formData.append("file", files[i], files[i].name);
                if(type != null){
                    formData.append("fileType", type);
                }
            }

            xhr.withCredentials = true;
            xhr.onreadystatechange = () => {
                if (xhr.readyState === 4) {
                    if (xhr.status === 200) {
                        observer.next(JSON.parse(xhr.response));
                        observer.complete();
                    } else {
                        observer.error(xhr.response);
                    }
                }
            };

            xhr.upload.onprogress = (event) => {
                //this.progress = Math.round(event.loaded / event.total * 100);
                //this.progressObserver.next(this.progress);
            };
            xhr.open('POST', url, true);
            // add custom header for preflight CORS call
            xhr.setRequestHeader('X-PREFLIGHT', 'preflight');
            xhr.send(formData);
        });
    }
}