package kz.nicnbk.service.impl.hf;

import kz.nicnbk.common.service.util.DateUtils;
import kz.nicnbk.common.service.util.MathUtils;
import kz.nicnbk.common.service.util.PaginationUtils;
import kz.nicnbk.repo.api.hf.HedgeFundScoringRepository;
import kz.nicnbk.repo.api.hf.HedgeFundScoringResultRepository;
import kz.nicnbk.repo.model.hf.HedgeFundScoring;
import kz.nicnbk.repo.model.hf.HedgeFundScoringResultFund;
import kz.nicnbk.repo.model.lookup.BenchmarkLookup;
import kz.nicnbk.service.api.benchmark.BenchmarkService;
import kz.nicnbk.service.api.hf.HedgeFundScoringService;
import kz.nicnbk.service.api.hf.HedgeFundScreeningService;
import kz.nicnbk.service.converter.hf.HedgeFundScoringConverter;
import kz.nicnbk.service.converter.hf.HedgeFundScoringResultFundConverter;
import kz.nicnbk.service.dto.benchmark.BenchmarkValueDto;
import kz.nicnbk.service.dto.common.ListResponseDto;
import kz.nicnbk.service.dto.common.ResponseStatusType;
import kz.nicnbk.service.dto.hf.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by magzumov on 27.05.2019.
 */

@Service
public class HedgeFundScoringServiceImpl implements HedgeFundScoringService {

    private static final Logger logger = LoggerFactory.getLogger(HedgeFundScoringServiceImpl.class);

    @Autowired
    private HedgeFundScreeningService screeningService;

    @Autowired
    private HedgeFundScoringRepository scoringRepository;

    @Autowired
    private HedgeFundScoringResultRepository scoringResultRepository;

    @Autowired
    private HedgeFundScoringConverter scoringConverter;

    @Autowired
    private HedgeFundScoringResultFundConverter scoringResultFundConverter;

    @Autowired
    private BenchmarkService benchmarkService;



    @Override
    public HedgeFundScoringDto getScoring(Long id){
        HedgeFundScoring entity =  this.scoringRepository.findOne(id);
        if(entity != null){

            HedgeFundScoringDto scoringDto = this.scoringConverter.disassemble(entity);
            // funds
            List<HedgeFundScoringResultFund> resultFunds = this.scoringResultRepository.findByScoringId(id);
            if(resultFunds != null && !resultFunds.isEmpty()){
                List<HedgeFundScoringResultFundDto> funds = this.scoringResultFundConverter.disassembleList(resultFunds);
                scoringDto.setFunds(funds);
            }
            return scoringDto;
        }
        return null;
    }

    @Override
    public HedgeFundScoringPagedSearchResult searchScoring(HedgeFundScoringSearchParams searchParams){
        try {
            Page<HedgeFundScoring> entitiesPage = null;
            int page = searchParams.getPage() > 0 ? searchParams.getPage() - 1 : 0;

            entitiesPage = scoringRepository.search(searchParams.getDateFromNonEmpty(), searchParams.getDateToNonEmpty(), searchParams.getSearchTextLowerCase(),
                    new PageRequest(page, searchParams.getPageSize(), new Sort(Sort.Direction.DESC, "date", "name")));


            HedgeFundScoringPagedSearchResult result = new HedgeFundScoringPagedSearchResult();
            if (entitiesPage != null) {
                result.setTotalElements(entitiesPage.getTotalElements());
                if (entitiesPage.getTotalElements() > 0) {
                    result.setShowPageFrom(PaginationUtils.getShowPageFrom(DEFAULT_PAGES_PER_VIEW, page));
                    result.setShowPageTo(PaginationUtils.getShowPageTo(DEFAULT_PAGES_PER_VIEW,
                            page, result.getShowPageFrom(), entitiesPage.getTotalPages()));
                }
                result.setTotalPages(entitiesPage.getTotalPages());
                result.setCurrentPage(page + 1);
                if (searchParams != null) {
                    result.setSearchParams(searchParams.getSearchParamsAsString());
                }
                //result.setScorings(this.scoringConverter.disassembleList(entitiesPage.getContent()));

                //Collections.sort(result.getScreenings());
            }
            return result;
        }catch(Exception ex){
            logger.error("Error searching HF Scorings", ex);
        }
        return null;
    }

    @Override
    public Long save(HedgeFundScoringDto scoringDto, String username) {
        try {
            if(scoringDto.getId() == null){
                scoringDto.setCreator(username);
            }else{
                scoringDto.setUpdater(username);
                scoringDto.setCreator(null);
            }
            HedgeFundScoring entity = this.scoringConverter.assemble(scoringDto);

            if (entity != null) {
                Long id = this.scoringRepository.save(entity).getId();
                logger.info("Successfully saved HF Scoring: id=" + id + " [user]=" + username);
                return id;
            }
        }catch (Exception ex){
            logger.error("Error saving HF Scoring (with exception) [user]=" + username, ex);
        }
        return null;
    }

    public ListResponseDto getCalculatedScoring(List<HedgeFundScreeningParsedDataDto> screeningList, HedgeFundScoringFundParamsDto scoringParams){
        ListResponseDto responseDto = new ListResponseDto();
        if(screeningList != null){
            HedgeFundScreeningFilteredResultDto filteredResultDto = this.screeningService.getFilteredResultWithoutFundsInfo(scoringParams.getFilteredResultId());

            Date dateFrom =  DateUtils.getFirstDayOfCurrentMonth(DateUtils.moveDateByMonths(filteredResultDto.getStartDate(),
                    0 - (Math.max(0, filteredResultDto.getTrackRecord().intValue() + scoringParams.getLookbackReturn().intValue()- 1))));
            Date dateTo = DateUtils.getLastDayOfCurrentMonth(DateUtils.moveDateByMonths(filteredResultDto.getStartDate(), 0 - scoringParams.getLookbackReturn().intValue()));

            // T-bills
            List<BenchmarkValueDto> tbills = this.benchmarkService.getBenchmarkValuesForDatesAndType(dateFrom, dateTo, BenchmarkLookup.T_BILLS.getCode());
            if(tbills.size() != filteredResultDto.getTrackRecord().intValue()){
                String errorMessage = "HF Scoring calculation failed: missing T-bills return values for selected track record period (found " + tbills.size() + " values)";
                logger.error(errorMessage);
                responseDto.setErrorMessageEn(errorMessage);
                responseDto.setRecords(screeningList);
                return responseDto;
                //throw new IllegalStateException(errorMessage);
            }
            double[] tbillsReturns = new double[tbills.size()];
            for(int i = 0; i < tbillsReturns.length; i++){
                if(tbills.get(i).getCalculatedMonthReturn() == null){
                    String errorMessage = "HF Scoring calculation failed: missing T-bills return values for date " + DateUtils.getDateFormatted(tbills.get(i).getDate());
                    logger.error(errorMessage);
                    responseDto.setErrorMessageEn(errorMessage);
                    responseDto.setRecords(screeningList);
                    return responseDto;
                    //throw new IllegalStateException(errorMessage);
                }
                tbillsReturns[i] = tbills.get(i).getCalculatedMonthReturn();
            }

            //S & P
            List<BenchmarkValueDto> snp = this.benchmarkService.getBenchmarkValuesForDatesAndType(dateFrom, dateTo, BenchmarkLookup.SNP_500_SPTR.getCode());
            if(snp.size() != filteredResultDto.getTrackRecord().intValue()){
                String errorMessage = "HF Scoring calculation failed: missing S&P return values for selected track record period (found " + snp.size() + " values)";
                logger.error(errorMessage);
                responseDto.setErrorMessageEn(errorMessage);
                responseDto.setRecords(screeningList);
                return responseDto;
                //throw new IllegalStateException(errorMessage);
            }
            double[] snpReturns = new double[snp.size()];
            for(int i = 0; i < snpReturns.length; i++){
                if(snp.get(i).getCalculatedMonthReturn() == null){
                    String errorMessage = "HF Scoring calculation failed: missing S & P reeturn values for date " + DateUtils.getDateFormatted(snp.get(i).getDate());
                    logger.error(errorMessage);
                    responseDto.setErrorMessageEn(errorMessage);
                    responseDto.setRecords(screeningList);
                    return responseDto;
                    //throw new IllegalStateException(errorMessage);
                }
                snpReturns[i] = snp.get(i).getCalculatedMonthReturn();
            }

            Collections.sort(screeningList);

//            for(HedgeFundScreeningParsedDataDto fund: screeningList){
//                double[] returns = null;
//                if(fund.isAdded()){
//                    returns = this.screeningService.getAddedFundReturns(fund.getFilteredResultId(), fund.getFundName(), filteredResultDto.getTrackRecord(), dateFrom, dateTo);
//                }else{
//                    try {
//                        returns = this.screeningService.getParsedFundReturns(filteredResultDto.getScreeningId(), fund.getFundId(), filteredResultDto.getTrackRecord().intValue(), dateFrom, dateTo);
//                    }catch (Exception ex){
//                        responseDto.setErrorMessageEn("Scoring failed. " + ex.getMessage());
//                        responseDto.setRecords(screeningList);
//                        return responseDto;
//                    }
//                }
//            }

            for(HedgeFundScreeningParsedDataDto fund: screeningList){
                // Fund returns
                double[] returns = null;
                if(fund.isAdded()){
                    returns = this.screeningService.getAddedFundReturns(fund.getFilteredResultId(), fund.getFundName(), filteredResultDto.getTrackRecord(), dateFrom, dateTo);
                }else{
                    try {
                        returns = this.screeningService.getParsedFundReturns(filteredResultDto.getScreeningId(), fund.getFundId(), filteredResultDto.getTrackRecord().intValue(), dateFrom, dateTo);
                    }catch (Exception ex){
                        responseDto.setErrorMessageEn("Scoring failed. " + ex.getMessage());
                        responseDto.setRecords(screeningList);
                        return responseDto;
                    }
                }
                if(returns == null || returns.length != filteredResultDto.getTrackRecord().intValue()){
                    String errorMessage = "HF Scoring Calculation failed: missing fund returns (found " + (returns != null ? returns.length : 0) + " from " + filteredResultDto.getTrackRecord().intValue() + ")";
                    logger.error(errorMessage);
                    responseDto.setErrorMessageEn(errorMessage);
                    responseDto.setRecords(screeningList);
                    return responseDto;
                    //throw new IllegalStateException(errorMessage);
                }

                int scale = 16;
                if(returns != null && returns.length == filteredResultDto.getTrackRecord().intValue()) {
                    // Annualized return
                    fund.setAnnualizedReturn(MathUtils.getAnnualizedReturn(returns, scale));

                    // Sortino
                    Double annualizedTbills = MathUtils.getAnnualizedReturn(tbillsReturns, scale);
                    if(fund.getAnnualizedReturn() != null && annualizedTbills != null) {
                        fund.setSortino(MathUtils.getSortinoRatio(fund.getAnnualizedReturn(), annualizedTbills, returns, scale));
                    }

                    // Beta
                    Double beta = MathUtils.getBeta(returns, snpReturns, scale);
                    if(beta != null){
                        double newValue = (new BigDecimal(beta).setScale(1, RoundingMode.HALF_UP)).doubleValue();
                        if(newValue == 0.0){
                            newValue = beta > 0 ? 0.1 : -0.1;
                        }
                        fund.setBeta(newValue);
                    }

                    // Alpha
                    if(fund.getBeta() != null) {
                        fund.setAlpha(MathUtils.getAlpha(scale, returns, tbillsReturns, snpReturns, beta));
                    }

                    // Omega
                    fund.setOmega(MathUtils.getOmega(scale, returns));

                    // CFVar
                    fund.setCfVar(MathUtils.getCFVar(scale, returns, MathUtils.Z_SCORE_99_PERCENT));

                }else{
                }
            }

            calculateTotalScore(screeningList);
//            Collections.sort(screeningList);
//            for(HedgeFundScreeningParsedDataDto fund: screeningList){
//                System.out.print(fund.getFundName() + "\t");
//                System.out.print(fund.getAnnualizedReturn() + "\t");
//                System.out.print(fund.getSortino() + "\t");
//                System.out.print(fund.getBeta() + "\t");
//                System.out.print(fund.getCfVar() + "\t");
//                System.out.print(fund.getAlpha() + "\t");
//                System.out.print(fund.getOmega() + "\t");
//                System.out.println(fund.getTotalScore());
//            }
        }
        responseDto.setStatus(ResponseStatusType.SUCCESS);
        responseDto.setRecords(screeningList);
        return responseDto;
    }

    private void calculateTotalScore(List<HedgeFundScreeningParsedDataDto> funds){
        if(funds != null){
            double[] annualizedReturnsList = new double[funds.size()];
            double[] sortinoList = new double[funds.size()];
            //double[] betaList = new double[funds.size()];
            double[] alphaList = new double[funds.size()];
            double[] omegaList = new double[funds.size()];
            double[] cfVarList = new double[funds.size()];
            for(int i = 0; i < funds.size(); i++){
                annualizedReturnsList[i] = funds.get(i).getAnnualizedReturn().doubleValue();
                sortinoList[i] = funds.get(i).getSortino().doubleValue();
                //betaList[i] = funds.get(i).getBeta().doubleValue();
                alphaList[i] = funds.get(i).getAlpha().doubleValue();
                omegaList[i] = funds.get(i).getOmega().doubleValue();
                cfVarList[i] = funds.get(i).getCfVar().doubleValue();
            }

//            System.out.println("ANN ROR Percentile");
//            System.out.println(MathUtils.getPercentile(annualizedReturnsList, 10));
//            System.out.println(MathUtils.getPercentile(annualizedReturnsList, 20));
//            System.out.println(MathUtils.getPercentile(annualizedReturnsList, 30));
//            System.out.println(MathUtils.getPercentile(annualizedReturnsList, 40));
//            System.out.println(MathUtils.getPercentile(annualizedReturnsList, 50));
//            System.out.println(MathUtils.getPercentile(annualizedReturnsList, 60));
//            System.out.println(MathUtils.getPercentile(annualizedReturnsList, 70));
//            System.out.println(MathUtils.getPercentile(annualizedReturnsList, 80));
//            System.out.println(MathUtils.getPercentile(annualizedReturnsList, 90));

            for(HedgeFundScreeningParsedDataDto fund: funds){
                int totalScore = 0;
                totalScore += getPercentileCoeff(annualizedReturnsList, fund.getAnnualizedReturn());
                totalScore += getPercentileCoeff(sortinoList, fund.getSortino());
                totalScore += getBetaPercentileCoeff(fund.getBeta());
                totalScore += getPercentileCoeff(alphaList, fund.getAlpha());
                totalScore += getPercentileCoeff(omegaList, fund.getOmega());
                totalScore += getPercentileCoeff(cfVarList, fund.getCfVar());

                // TODO: adjustable scale?
                int scale = 18;
                fund.setTotalScore(MathUtils.divide(scale, totalScore + 0.0, 6.0));
            }

//            for(int p = 10; p <= 90; p = p + 10){
//                System.out.print(p);
//                System.out.print("\t" + MathUtils.getPercentile(annualizedReturnsList, p));
//                System.out.print("\t" + MathUtils.getPercentile(sortinoList, p));
//                System.out.print("\t" + p/100.0);
//                System.out.print("\t" +  MathUtils.getPercentile(cfVarList, p));
//                System.out.print("\t" +  MathUtils.getPercentile(alphaList, p));
//                System.out.println("\t" + MathUtils.getPercentile(omegaList, p));
//            }
//
//            for(int p = 10; p <= 90; p = p + 10){
//                System.out.print(p);
//                System.out.print("\t" + MathUtils.getPercentileExcel(annualizedReturnsList, p));
//                System.out.print("\t" + MathUtils.getPercentileExcel(sortinoList, p));
//                System.out.print("\t" + p/100.0);
//                System.out.print("\t" +  MathUtils.getPercentileExcel(cfVarList, p));
//                System.out.print("\t" +  MathUtils.getPercentileExcel(alphaList, p));
//                System.out.println("\t" + MathUtils.getPercentileExcel(omegaList, p));
//            }
        }
    }

    private int getPercentileCoeff(double[] values, Double checkValue){
        for(int p = 10; p <= 90; p = p + 10){
            Double percentileValue = MathUtils.getPercentileExcel(values, p);
            if(checkValue < percentileValue.doubleValue()){
                int coeff = p/10;
                return coeff;
            }
        }
        return 10;
    }

    private int getBetaPercentileCoeff(Double checkValue){
        Double newCheckValue = new BigDecimal(checkValue).setScale(1, RoundingMode.HALF_UP).doubleValue();
//        if(newCheckValue.doubleValue() == 0.0){
//            newCheckValue = checkValue > 0 ? 0.1 : -0.1;
//        }
        checkValue = newCheckValue;
        for(int p = 1; p <= 9; p = p + 1){
            if(Math.abs(checkValue.doubleValue()) < p/10.0){
                int coeff = 10 - p + 1;
                return coeff;
                //return (coeff == 10 ? 9 : coeff);
            }
        }
        return 1;
    }

//    @Override
//    public List<HedgeFundScreeningParsedDataDto> getCalculatedScoring(HedgeFundScoringFundParamsDto scoringParams){
//
//        HedgeFundScreeningFilteredResultDto filteredResultDto = this.screeningService.getFilteredResultWithFundsInfo(scoringParams.getFilteredResultId());
//        if(filteredResultDto == null){
//            logger.error("Failed to load qualified fund list for scoring: no filtered result found with id " + scoringParams.getFilteredResultId());
//            return null;
//        }
//        HedgeFundScreeningFilteredResultDto screeningParams = new HedgeFundScreeningFilteredResultDto();
//        screeningParams.setScreeningId(filteredResultDto.getScreeningId());
//        screeningParams.setId(filteredResultDto.getId());
//        screeningParams.setFundAUM(filteredResultDto.getFundAUM());
//        screeningParams.setManagerAUM(filteredResultDto.getManagerAUM());
//        screeningParams.setStartDate(filteredResultDto.getStartDate());
//        screeningParams.setStartDateMonth(DateUtils.getMM_YYYYYFormatDate(filteredResultDto.getStartDate()));
//        screeningParams.setTrackRecord(filteredResultDto.getTrackRecord());
//
//        screeningParams.setLookbackAUM(scoringParams.getLookbackAUM());
//        screeningParams.setLookbackReturns(scoringParams.getLookbackReturn());
//
//        List<HedgeFundScreeningParsedDataDto> screeningList = this.screeningService.getFilteredResultQualifiedFundList(screeningParams);
//
//        List<HedgeFundScreeningParsedDataDto> fundList = getCalculatedScoring(screeningList, scoringParams);
//        return fundList;
//    }
}
