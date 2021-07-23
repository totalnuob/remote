package kz.nicnbk.service.dto.reporting.realestate;

import kz.nicnbk.service.dto.reporting.GeneratedGeneralLedgerFormDto;

/**
 * Created by magzumov on 15.06.2018.
 */
public class TerraGeneratedGeneralLedgerFormDto extends GeneratedGeneralLedgerFormDto {

    private Boolean excludeFromTerraCalculation;
    private String type;
    private Double glAccountBalanceEdited;

    public TerraGeneratedGeneralLedgerFormDto(){}

    public TerraGeneratedGeneralLedgerFormDto(TerraGeneralLedgerBalanceRecordDto recordDto){
        setAcronym(recordDto.getAcronym());
        setBalanceDate(recordDto.getBalanceDate());
        setFinancialStatementCategory(recordDto.getFinancialStatementCategory());
        setChartAccountsLongDescription(recordDto.getGlSubclass());
        setShortName(recordDto.getPortfolioFund());
        setGLAccountBalance(recordDto.getAccountBalanceNICKMF());
        setGlAccountBalanceEdited(recordDto.getGlAccountBalanceEdited());
    }



    public Boolean getExcludeFromTerraCalculation() {
        return excludeFromTerraCalculation;
    }

    public void setExcludeFromTerraCalculation(Boolean excludeFromTerraCalculation) {
        this.excludeFromTerraCalculation = excludeFromTerraCalculation;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getGlAccountBalanceEdited() {
        return glAccountBalanceEdited;
    }

    public void setGlAccountBalanceEdited(Double glAccountBalanceEdited) {
        this.glAccountBalanceEdited = glAccountBalanceEdited;
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
                if(this.getShortName() != null && ((GeneratedGeneralLedgerFormDto) o).getShortName() != null) {
                    return this.getShortName().compareTo(((GeneratedGeneralLedgerFormDto) o).getShortName());
                }
            }
            return this.getNbAccountNumber().compareTo(((GeneratedGeneralLedgerFormDto) o).getNbAccountNumber());
        }
    }

    private int getSortingScore(GeneratedGeneralLedgerFormDto dto){
        int result = 10; //dto.getAcronym() != null && dto.getAcronym().endsWith(" B") ? 1000 : 10;
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
