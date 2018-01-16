package kz.nicnbk.ws.model;

import java.io.Serializable;

/**
 * Created by magzumov on 09.08.2016.
 */
public class Response implements Serializable {
    private Boolean success;
    private ResponseMessage message;

    public Response(){}

    public Response(Boolean success, ResponseMessage message){
        this.success = success;
        this.message = message;;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public ResponseMessage getMessage() {
        return message;
    }

    public void setMessage(ResponseMessage message) {
        this.message = message;
    }
}
