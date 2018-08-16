package kz.nicnbk.service.dto.reporting;

import kz.nicnbk.common.service.model.BaseDto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by magzumov on 25.10.2017.
 */
public class ReportingFundNameListHolderDto implements BaseDto {

    private String[] currentPEFundNameList;
    private String[] currentHFFundNameList;
    private String[] currentREFundNameList;

    private String[] previousPEFundNameList;
    private String[] previousHFFundNameList;
    private String[] previousREFundNameList;

    public String[] getCurrentPEFundNameList() {
        return currentPEFundNameList;
    }

    public void setCurrentPEFundNameList(String[] currentPEFundNameList) {
        this.currentPEFundNameList = currentPEFundNameList;
    }

    public String[] getCurrentHFFundNameList() {
        return currentHFFundNameList;
    }

    public void setCurrentHFFundNameList(String[] currentHFFundNameList) {
        this.currentHFFundNameList = currentHFFundNameList;
    }

    public String[] getCurrentREFundNameList() {
        return currentREFundNameList;
    }

    public void setCurrentREFundNameList(String[] currentREFundNameList) {
        this.currentREFundNameList = currentREFundNameList;
    }

    public String[] getPreviousPEFundNameList() {
        return previousPEFundNameList;
    }

    public void setPreviousPEFundNameList(String[] previousPEFundNameList) {
        this.previousPEFundNameList = previousPEFundNameList;
    }

    public String[] getPreviousHFFundNameList() {
        return previousHFFundNameList;
    }

    public void setPreviousHFFundNameList(String[] previousHFFundNameList) {
        this.previousHFFundNameList = previousHFFundNameList;
    }

    public String[] getPreviousREFundNameList() {
        return previousREFundNameList;
    }

    public void setPreviousREFundNameList(String[] previousREFundNameList) {
        this.previousREFundNameList = previousREFundNameList;
    }
}
