package kz.nicnbk.repo.model.bloomberg;

import java.util.ArrayList;
import java.util.List;

public class SecurityData {

    private String security;
    private String sequenceNumber;
    private String[] fieldData;
    private List<FieldData> fieldDataList;
    private List<FieldExceptions> fieldExceptions;
    private List<SecurityError> securityErrors;

}
