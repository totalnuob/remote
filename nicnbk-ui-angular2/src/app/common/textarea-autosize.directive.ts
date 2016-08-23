import { ElementRef, HostListener, Directive} from '@angular/core';

@Directive({
    selector: 'textarea[autosize]'
})

export class TextareaAutosize {
    @HostListener('input',['$event.target'])
    onInput(textArea: HTMLTextAreaElement): void {
        this.adjust();
    }

    constructor(public element: ElementRef){
    }

    ngOnInit(): void{
        //this.adjust();
    }

    // TODO: called to often?
    ngAfterContentChecked() {
        //onsole.log("ngAfterContentChecked");
        this.adjust();

    }

    adjust(): void{
        this.element.nativeElement.style.overflow = 'hidden';
        this.element.nativeElement.style.height = 'auto';
        this.element.nativeElement.style.height = this.element.nativeElement.scrollHeight + "px";
    }
}