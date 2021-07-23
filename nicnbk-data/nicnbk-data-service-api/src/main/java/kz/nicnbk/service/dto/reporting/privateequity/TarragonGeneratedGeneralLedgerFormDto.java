package kz.nicnbk.service.dto.reporting.privateequity;

import kz.nicnbk.service.dto.reporting.GeneratedGeneralLedgerFormDto;

/**
 * Created by magzumov on 15.06.2018.
 */
public class TarragonGeneratedGeneralLedgerFormDto extends GeneratedGeneralLedgerFormDto {

    private Boolean excludeFromTarragonCalculation;
    private String type;


    public TarragonGeneratedGeneralLedgerFormDto(){}

    public TarragonGeneratedGeneralLedgerFormDto(TarragonGeneratedGeneralLedgerFormDto other){
        super(other);

        this.excludeFromTarragonCalculation = other.getExcludeFromTarragonCalculation();
        this.type = other.getType();
    }

    public Boolean getExcludeFromTarragonCalculation() {
        return excludeFromTarragonCalculation;
    }

    public void setExcludeFromTarragonCalculation(Boolean excludeFromTarragonCalculation) {
        this.excludeFromTarragonCalculation = excludeFromTarragonCalculation;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int compareTo(Object o) {
        if(getSortingScore(this) < getSortingScore((GeneratedGeneralLedgerFormDto)o)){
            return -1;
        }else if(getSortingScore(this) > getSortingScore((GeneratedGeneralLedgerFormDto)o)){
            return 1;
        }else {
            if(this.getNbAccountNumber() == null){
                if(((GeneratedGeneralLedgerFormDto) o).getNbAccountNumber() == null){
                    return 0;
                }
                return 1;
            }
            if(((GeneratedGeneralLedgerFormDto) o).getNbAccountNumber() == null){
                return -1;
            }
            if(this.getNbAccountNumber().equalsIgnoreCase(((GeneratedGeneralLedgerFormDto) o).getNbAccountNumber())){
                // compare fund name
                if(this.getChartAccountsLongDescription() != null && ((GeneratedGeneralLedgerFormDto) o).getChartAccountsLongDescription() != null) {
                    return this.getChartAccountsLongDescription().compareTo(((GeneratedGeneralLedgerFormDto) o).getChartAccountsLongDescription());
                }
            }
            return this.getNbAccountNumber().compareTo(((GeneratedGeneralLedgerFormDto) o).getNbAccountNumber());
        }
    }

    private int getSortingScore(GeneratedGeneralLedgerFormDto dto){
        int result = 10;//dto.getAcronym() != null && dto.getAcronym().endsWith(" B") ? 1000 : 10;
        if(dto.getFinancialStatementCategory() == null){
            return result;
        }else if(dto.getFinancialStatementCategory().equalsIgnoreCase("A")){
            result += 1;
        }else if(dto.getFinancialStatementCategory().equalsIgnoreCase("L")){
            result += 2;
        }else if(dto.getFinancialStatementCategory().equalsIgnoreCase("E")){
            result += 3;
        }if(dto.getFinancialStatementCategory().equalsIgnoreCase("I")){
            result += 4;
        }if(dto.getFinancialStatementCategory().equalsIgnoreCase("X")){
            result += 5;
        }

        return result;
    }
}
