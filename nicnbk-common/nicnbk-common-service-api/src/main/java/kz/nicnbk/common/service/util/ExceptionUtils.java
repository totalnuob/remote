package kz.nicnbk.common.service.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by magzumov on 18.07.2016.
 */
public class ExceptionUtils {

    public static String getStackTrace(Exception ex){
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        ex.printStackTrace(printWriter);
        return stringWriter.toString();
    }
}
