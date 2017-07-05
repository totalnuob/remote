package kz.nicnbk.service.dto.reporting;

import java.util.List;

/**
 * Created by magzumov on 05.05.2017.
 */
@Deprecated
public class ScheduleInvestmentsDto extends InputFileReportDataDto{

    private List<FundInvestmentDto> fundInvestments;
    private List<CoInvestmentDto> coInvestments;

    private CommonInvestmentDto total;


    public List<FundInvestmentDto> getFundInvestments() {
        return fundInvestments;
    }

    public void setFundInvestments(List<FundInvestmentDto> fundInvestments) {
        this.fundInvestments = fundInvestments;
    }

    public List<CoInvestmentDto> getCoInvestments() {
        return coInvestments;
    }

    public void setCoInvestments(List<CoInvestmentDto> coInvestments) {
        this.coInvestments = coInvestments;
    }

    public CommonInvestmentDto getTotal() {
        return total;
    }

    public void setTotal(CommonInvestmentDto total) {
        this.total = total;
    }

        public void print(){
            System.out.println("Fund Investments: ");
            if(this.fundInvestments != null){
                for(FundInvestmentDto dto: this.fundInvestments){
                    System.out.println(dto.getInvestmentName() + " | " + dto.getCapitalCommitments() + " | " +
                    dto.getNetCost() + " | " + dto.getFairValue() + " | " + dto.getCurrency() + " | " + dto.getStrategy());
                }
            }

            System.out.println("\nCo-Investments: ");
            if(this.coInvestments != null){
                for(CoInvestmentDto dto: this.coInvestments){
                    System.out.println(dto.getInvestmentName() + " | " + dto.getCapitalCommitments() + " | " +
                            dto.getNetCost() + " | " + dto.getFairValue() + " | " + dto.getCurrency() + " | " + dto.getDescription());
                }
            }

            if(this.total != null) {
                System.out.println("\nTotal Private Equity Partnerships and Co-Investments | " + this.total.getCapitalCommitments()
                        + " | " + this.total.getNetCost() + " | " + this.total.getFairValue() + " | " + this.total.getCurrency());
            }
            System.out.println("**************************************************");
    }
}
