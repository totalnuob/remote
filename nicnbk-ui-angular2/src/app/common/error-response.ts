export class ErrorResponse{

    status: number;
    statusText: string;
    message: string;

    messageKz: string;
    messageRu: string;

    public isEmpty(){
        if(this.message || (this.statusText && this.status)){
            return false;
        }else {
            return true;
        }
    }
}
