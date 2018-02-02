package kz.nicnbk.service.dto.reporting.exception;

/**
 * Created by magzumov on 16.05.2017.
 */
public class ExcelFileParseException extends IllegalArgumentException {

    public ExcelFileParseException(String message){
        super(message);
    }
}
