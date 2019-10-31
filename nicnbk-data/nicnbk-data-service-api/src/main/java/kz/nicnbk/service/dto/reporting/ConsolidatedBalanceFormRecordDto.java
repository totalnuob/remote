package kz.nicnbk.service.dto.reporting;

import kz.nicnbk.common.service.model.BaseDto;
import kz.nicnbk.common.service.util.StringUtils;

/**
 * Created by magzumov on 25.10.2017.
 */
public class ConsolidatedBalanceFormRecordDto implements BaseDto, Comparable {
    private String accountNumber;
    private String name;
    private Integer lineNumber;
    private Integer subLineNumber;
    private String otherEntityName;
    private Double currentAccountBalance;
    private Double previousAccountBalance;

    public ConsolidatedBalanceFormRecordDto(){}

    public ConsolidatedBalanceFormRecordDto(String name, Integer lineNumber){
        this.name = name;
        this.lineNumber = lineNumber;
    }

    public ConsolidatedBalanceFormRecordDto(String name, Integer lineNumber, Integer subLineNumber){
        this.name = name;
        this.lineNumber = lineNumber;
        this.subLineNumber = subLineNumber;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(Integer lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getOtherEntityName() {
        return otherEntityName;
    }

    public void setOtherEntityName(String otherEntityName) {
        this.otherEntityName = otherEntityName;
    }

    public Double getCurrentAccountBalance() {
        return currentAccountBalance;
    }

    public void setCurrentAccountBalance(Double currentAccountBalance) {
        this.currentAccountBalance = currentAccountBalance;
    }

    public Double getPreviousAccountBalance() {
        return previousAccountBalance;
    }

    public void setPreviousAccountBalance(Double previousAccountBalance) {
        this.previousAccountBalance = previousAccountBalance;
    }

    @Override
    public int compareTo(Object o) {
        ConsolidatedBalanceFormRecordDto other = (ConsolidatedBalanceFormRecordDto) o;
        if(this.lineNumber != null && other.getLineNumber() != null){
            if(lineNumber.intValue() == other.getLineNumber().intValue()){
                if(StringUtils.isEmpty(accountNumber) && StringUtils.isNotEmpty(other.accountNumber)){
                    return -1;
                }else if(StringUtils.isNotEmpty(accountNumber) && StringUtils.isEmpty(other.accountNumber)){
                    return 1;
                }else if(StringUtils.isNotEmpty(accountNumber) && StringUtils.isNotEmpty(other.accountNumber)){
                    //return this.accountNumber.compareTo(other.accountNumber);
                    if(this.accountNumber.equalsIgnoreCase(other.accountNumber)){
                        if(this.otherEntityName != null){
                            if(StringUtils.isEmpty(otherEntityName) && StringUtils.isNotEmpty(other.otherEntityName)){
                                return -1;
                            }else if(StringUtils.isNotEmpty(otherEntityName) && StringUtils.isEmpty(other.otherEntityName)){
                                return 1;
                            }else if(StringUtils.isNotEmpty(otherEntityName) && StringUtils.isNotEmpty(other.otherEntityName)){
                                if(this.otherEntityName.equalsIgnoreCase(other.otherEntityName)){
                                    return this.name.compareTo(other.name);
                                }else{
                                    return this.otherEntityName.compareTo(other.otherEntityName);
                                }
                            }else{
                                return 0;
                            }
                        }else {
                            return this.name.compareTo(other.name);
                        }
                    }else {
                        return this.accountNumber.compareTo(other.accountNumber);
                    }
                }else{
                    return 0;
                }
            }else{
                return lineNumber.compareTo(other.getLineNumber());
            }
        }else if(this.lineNumber != null && other.getLineNumber() == null){
            return -1;
        }else if(this.lineNumber == null && other.getLineNumber() != null){
            return 1;
        }else{
            return 0;
        }
    }

    public Integer getSubLineNumber() {
        return subLineNumber;
    }

    public void setSubLineNumber(Integer subLineNumber) {
        this.subLineNumber = subLineNumber;
    }
}
