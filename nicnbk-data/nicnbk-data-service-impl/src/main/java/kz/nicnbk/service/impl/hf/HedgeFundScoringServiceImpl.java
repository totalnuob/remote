package kz.nicnbk.service.impl.hf;

import kz.nicnbk.common.service.util.DateUtils;
import kz.nicnbk.common.service.util.MathUtils;
import kz.nicnbk.common.service.util.PaginationUtils;
import kz.nicnbk.repo.api.hf.HedgeFundScoringRepository;
import kz.nicnbk.repo.api.hf.HedgeFundScoringResultRepository;
import kz.nicnbk.repo.model.hf.HedgeFundScoring;
import kz.nicnbk.repo.model.hf.HedgeFundScoringResultFund;
import kz.nicnbk.repo.model.hf.HedgeFundScreeningParsedData;
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
import java.util.*;

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

//            // T-bills
//            List<BenchmarkValueDto> tbills = this.benchmarkService.getBenchmarkValuesForDatesAndType(dateFrom, dateTo, BenchmarkLookup.T_BILLS.getCode());
//            if(tbills.size() != filteredResultDto.getTrackRecord().intValue()){
//                String errorMessage = "HF Scoring calculation failed: missing T-bills return values for selected track record period (found " + tbills.size() + " values)";
//                logger.error(errorMessage);
//                responseDto.setErrorMessageEn(errorMessage);
//                responseDto.setRecords(screeningList);
//                return responseDto;
//                //throw new IllegalStateException(errorMessage);
//            }
//            double[] tbillsReturns = new double[tbills.size()];
//            for(int i = 0; i < tbillsReturns.length; i++){
//                if(tbills.get(i).getCalculatedMonthReturn() == null){
//                    String errorMessage = "HF Scoring calculation failed: missing T-bills return values for date " + DateUtils.getDateFormatted(tbills.get(i).getDate());
//                    logger.error(errorMessage);
//                    responseDto.setErrorMessageEn(errorMessage);
//                    responseDto.setRecords(screeningList);
//                    return responseDto;
//                    //throw new IllegalStateException(errorMessage);
//                }
//                tbillsReturns[i] = tbills.get(i).getCalculatedMonthReturn();
//            }
//
//            //S & P
//            List<BenchmarkValueDto> snp = this.benchmarkService.getBenchmarkValuesForDatesAndType(dateFrom, dateTo, BenchmarkLookup.SNP_500_SPTR.getCode());
//            if(snp.size() != filteredResultDto.getTrackRecord().intValue()){
//                String errorMessage = "HF Scoring calculation failed: missing S&P return values for selected track record period (found " + snp.size() + " values)";
//                logger.error(errorMessage);
//                responseDto.setErrorMessageEn(errorMessage);
//                responseDto.setRecords(screeningList);
//                return responseDto;
//                //throw new IllegalStateException(errorMessage);
//            }
//            double[] snpReturns = new double[snp.size()];
//            for(int i = 0; i < snpReturns.length; i++){
//                if(snp.get(i).getCalculatedMonthReturn() == null){
//                    String errorMessage = "HF Scoring calculation failed: missing S & P reeturn values for date " + DateUtils.getDateFormatted(snp.get(i).getDate());
//                    logger.error(errorMessage);
//                    responseDto.setErrorMessageEn(errorMessage);
//                    responseDto.setRecords(screeningList);
//                    return responseDto;
//                    //throw new IllegalStateException(errorMessage);
//                }
//                snpReturns[i] = snp.get(i).getCalculatedMonthReturn();
//            }

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

            HedgeFundScreeningDto screeningDto = this.screeningService.getScreening(scoringParams.getScreeningId());
            List<HedgeFundScreeningFundParamDataDto> fundParams = screeningDto.getParsedFundParamData();
            Map<String, HedgeFundScreeningFundParamDataDto> fundParamsMap = new HashMap<>();
            if(fundParams != null && !fundParams.isEmpty()){
                for(HedgeFundScreeningFundParamDataDto dataDto: fundParams){
                    fundParamsMap.put(dataDto.getFundName(), dataDto);
                }
            }
            List<HedgeFundScreeningFundParamDataDto> fundParamsList = new ArrayList<>(fundParams);

            Map<String, double[]> fundParamsMappedValues = getValuesArray(fundParamsList);
            Map<Integer, Double> annualizedReturnPercentileCoeffMap = getPercentileCoeff2(fundParamsMappedValues.get("AnnRoR"));
            Map<Integer, Double> sortinoPercentileCoeffMap = getPercentileCoeff2(fundParamsMappedValues.get("Sortino"));
            Map<Integer, Double> betaPercentileCoeffMap = getPercentileCoeff2(fundParamsMappedValues.get("Beta"));
            Map<Integer, Double> alphaPercentileCoeffMap = getPercentileCoeff2(fundParamsMappedValues.get("Alpha"));
            Map<Integer, Double> omegaPercentileCoeffMap = getPercentileCoeff2(fundParamsMappedValues.get("Omega"));
            Map<Integer, Double> cfvarPercentileCoeffMap = getPercentileCoeff2(fundParamsMappedValues.get("Cfvar"));

            int annualizedReturnUploadDataCount = 0;
            int alphaUploadDataCount = 0;
            int betaUploadDataCount = 0;
            int omegaUploadDataCount = 0;
            int sortinoUploadDataCount = 0;
            int cfVarUploadDataCount = 0;
            int annualizedReturnCalcCount = 0;
            int alphaCalcCount = 0;
            int betaCalcCount = 0;
            int omegaCalcCount = 0;
            int sortinoCalcCount = 0;
            int cfVarCalcCount = 0;
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
                    if(fundParamsMap.get(fund.getFundName()) != null && fundParamsMap.get(fund.getFundName()).getAnnualizedReturn() != null){
                        Double annRoR = fundParamsMap.get(fund.getFundName()).getAnnualizedReturn();
                        for(Map.Entry<Integer, Double> entry : annualizedReturnPercentileCoeffMap.entrySet()) {
                            if (annRoR <= entry.getValue()) {
                                annRoR = entry.getKey().doubleValue();
                                fund.setAnnualizedReturn(annRoR);
                                break;
                            } else {
                                fund.setAnnualizedReturn(10.0);
                            }
                        }
                        annualizedReturnUploadDataCount++;
                    }else {
                        fund.setAnnualizedReturn(MathUtils.getAnnualizedReturn(returns, scale));
                        annualizedReturnCalcCount++;
                    }

                    // Sortino
//                    Double annualizedTbills = MathUtils.getAnnualizedReturn(tbillsReturns, scale);
                    if(fundParamsMap.get(fund.getFundName()) != null && fundParamsMap.get(fund.getFundName()).getSortino() != null){
                        Double sortino = fundParamsMap.get(fund.getFundName()).getSortino();
                        for(Map.Entry<Integer, Double> entry : sortinoPercentileCoeffMap.entrySet()) {
                            if (sortino <= entry.getValue()) {
                                sortino = entry.getKey().doubleValue();
                                fund.setSortino(sortino);
                                break;
                            } else {
                                fund.setSortino(10.0);
                            }
                        }
                        sortinoUploadDataCount++;
                    }else {
                        //fund.setSortino(MathUtils.getSortinoRatio(fund.getAnnualizedReturn(), annualizedTbills, returns, scale));
//                            fund.setSortino(MathUtils.getSortinoRatio2(returns, tbillsReturns, scale));
                        sortinoCalcCount++;
                    }


                    // Beta
                    if(fundParamsMap.get(fund.getFundName()) != null && fundParamsMap.get(fund.getFundName()).getBeta() != null){
                        Double beta = fundParamsMap.get(fund.getFundName()).getBeta();
                        if (beta <= 0) {
                            beta = 0.0;
                        } else if (beta > 0 && beta <= 1) {
                            beta = -beta;
                        } else if (beta > 1) {
                            beta = -1.0;
                        }
                        for(Map.Entry<Integer, Double> entry : betaPercentileCoeffMap.entrySet()) {
                            if (beta <= entry.getValue()) {
                                beta = entry.getKey().doubleValue();
                                fund.setBeta(beta);
                                break;
                            } else {
                                fund.setBeta(10.0);
                            }
                        }
                        betaUploadDataCount++;
                    }else {
                        //Double beta = MathUtils.getBeta(returns, snpReturns, scale);
//                        fund.setBeta(MathUtils.getBeta2(returns, snpReturns, tbillsReturns, scale));
                        betaCalcCount++;
                    }

//                    double betaValue = fund.getBeta() != null ? fund.getBeta().doubleValue() : 0;
//                    if(fund.getBeta() != null){
//                        double newValue = (new BigDecimal(fund.getBeta() ).setScale(1, RoundingMode.HALF_UP)).doubleValue();
//                        if(newValue == 0.0){
//                            newValue = fund.getBeta() > 0 ? 0.1 : -0.1;
//                        }
//                        fund.setBeta(newValue);
//                    }

//                    if (fund.getBeta() != null) {
//                        double newValue = 0.0;
//                        if (fund.getBeta() <= 0) {
//                            newValue = 0.0;
//                        } else if (fund.getBeta() > 0 && fund.getBeta() <= 1) {
//                            newValue = -fund.getBeta();
//                        } else if (fund.getBeta() > 1) {
//                            newValue = -1;
//                        }
//                        fund.setBeta(newValue);
//                        betaValue = newValue;
//                    }

                    // Alpha
                    if(fundParamsMap.get(fund.getFundName()) != null && fundParamsMap.get(fund.getFundName()).getAlpha() != null){
                        Double alpha = fundParamsMap.get(fund.getFundName()).getAlpha();
                        for(Map.Entry<Integer, Double> entry : alphaPercentileCoeffMap.entrySet()) {
                            if (alpha <= entry.getValue()) {
                                alpha = entry.getKey().doubleValue();
                                fund.setAlpha(alpha);
                                break;
                            } else {
                                fund.setAlpha(10.0);
                            }
                        }
                        alphaUploadDataCount++;
                    }else {
                        //fund.setAlpha(MathUtils.getAlpha(scale, returns, tbillsReturns, snpReturns, beta));
//                            fund.setAlpha(MathUtils.getAlpha2(scale, returns, tbillsReturns, snpReturns, betaValue));
                        alphaCalcCount++;
                    }

                    // Omega
                    if(fundParamsMap.get(fund.getFundName()) != null && fundParamsMap.get(fund.getFundName()).getOmega() != null){
                        Double omega = fundParamsMap.get(fund.getFundName()).getOmega();
                        for(Map.Entry<Integer, Double> entry : omegaPercentileCoeffMap.entrySet()) {
                            if (omega <= entry.getValue()) {
                                omega = entry.getKey().doubleValue();
                                fund.setOmega(omega);
                                break;
                            } else {
                                fund.setOmega(10.0);
                            }
                        }
                        omegaUploadDataCount++;
                    }else {
                        fund.setOmega(MathUtils.getOmega(scale, returns));
                        omegaCalcCount++;
                    }


                    // CFVar
                    if(fundParamsMap.get(fund.getFundName()) != null && fundParamsMap.get(fund.getFundName()).getCfVar() != null){
                        Double cfvar = fundParamsMap.get(fund.getFundName()).getCfVar();
                        for(Map.Entry<Integer, Double> entry : cfvarPercentileCoeffMap.entrySet()) {
                            if (cfvar <= entry.getValue()) {
                                cfvar = entry.getKey().doubleValue();
                                fund.setCfVar(cfvar);
                                break;
                            } else {
                                fund.setCfVar(10.0);
                            }
                        }
                        cfVarUploadDataCount++;
                    }else {
                        //fund.setCfVar(MathUtils.getCFVar(scale, returns, MathUtils.Z_SCORE_99_PERCENT));
                        fund.setCfVar(MathUtils.getCFVar2(scale, returns, MathUtils.Z_SCORE_99_PERCENT));
                        cfVarCalcCount++;
                    }
                }else{
                }
            }

            try {
                calculateTotalScore(screeningList);
            } catch (NullPointerException ex) {
                responseDto.setErrorMessageEn("Scoring error. Fund missing scoring parameters. " + ex.getMessage());
                responseDto.setRecords(screeningList);
                return responseDto;
            }
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

            if(annualizedReturnUploadDataCount > 0 && annualizedReturnCalcCount > 0){
                responseDto.appendErrorMessageEn(" 'Ann Return' calculation used uploaded values.");
            }
            if(sortinoUploadDataCount > 0 && sortinoCalcCount > 0){
                responseDto.appendErrorMessageEn(" 'Sortino' calculation used uploaded values.");
            }
            if(betaUploadDataCount > 0  && betaCalcCount > 0){
                responseDto.appendErrorMessageEn(" 'Beta' calculation used uploaded values.");
            }
            if(alphaUploadDataCount > 0 && alphaCalcCount > 0){
                responseDto.appendErrorMessageEn(" 'Alpha' calculation used uploaded values.");
            }
            if(omegaUploadDataCount > 0 && omegaCalcCount > 0){
                responseDto.appendErrorMessageEn(" 'Omega' calculation used uploaded values.");
            }
            if(cfVarUploadDataCount > 0 && cfVarCalcCount > 0){
                responseDto.appendErrorMessageEn(" 'cfVar' calculation used uploaded values.");
            }
        }
        if(responseDto.getStatus() == null) {
            responseDto.setStatus(ResponseStatusType.SUCCESS);
        }
        responseDto.setRecords(screeningList);
        return responseDto;
    }

    private Map<String, double[]> getValuesArray(List<HedgeFundScreeningFundParamDataDto> funds) {
        if (funds != null) {
            Map<String, double[]> mappedValues = new HashMap<>();
            double[] annualizedReturnsList = new double[funds.size()];
            double[] sortinoList = new double[funds.size()];
            double[] betaList = new double[funds.size()];
            double[] alphaList = new double[funds.size()];
            double[] omegaList = new double[funds.size()];
            double[] cfVarList = new double[funds.size()];
            for(int i = 0; i < funds.size(); i++){
                annualizedReturnsList[i] = funds.get(i).getAnnualizedReturn().doubleValue();
                sortinoList[i] = funds.get(i).getSortino().doubleValue();
                betaList[i] = funds.get(i).getBeta().doubleValue();
                alphaList[i] = funds.get(i).getAlpha().doubleValue();
                omegaList[i] = funds.get(i).getOmega().doubleValue();
                cfVarList[i] = funds.get(i).getCfVar().doubleValue();
            }
            mappedValues.put("AnnRoR", annualizedReturnsList);
            mappedValues.put("Sortino", sortinoList);
            mappedValues.put("Beta", normalizeBetaValues(betaList));
            mappedValues.put("Alpha", alphaList);
            mappedValues.put("Omega", omegaList);
            mappedValues.put("Cfvar", cfVarList);

            return mappedValues;
        }
        return null;
    }

    private void calculateTotalScore(List<HedgeFundScreeningParsedDataDto> funds) throws NullPointerException{
        if(funds != null){
//            double[] annualizedReturnsList = new double[funds.size()];
//            double[] sortinoList = new double[funds.size()];
//            double[] betaList = new double[funds.size()];
//            double[] alphaList = new double[funds.size()];
//            double[] omegaList = new double[funds.size()];
//            double[] cfVarList = new double[funds.size()];
//            for(int i = 0; i < funds.size(); i++){
//                annualizedReturnsList[i] = funds.get(i).getAnnualizedReturn().doubleValue();
//                sortinoList[i] = funds.get(i).getSortino().doubleValue();
//                betaList[i] = funds.get(i).getBeta().doubleValue();
//                alphaList[i] = funds.get(i).getAlpha().doubleValue();
//                omegaList[i] = funds.get(i).getOmega().doubleValue();
//                cfVarList[i] = funds.get(i).getCfVar().doubleValue();
//            }

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
                totalScore += fund.getAnnualizedReturn();
                totalScore += fund.getSortino();

                totalScore += fund.getBeta();
                //totalScore += getBetaPercentileCoeff(fund.getBeta());

                totalScore += fund.getAlpha();
                totalScore += fund.getOmega();
                totalScore += fund.getCfVar();

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
            if(checkValue <= percentileValue.doubleValue()){
                int coeff = p/10;
                return coeff;
            }
        }
        return 10;
    }

//    private int calculatePercentileCoeff(double[] values, Double checkValue) {
//        for (int p = 10; p <= 100; p = p + 10) {
//            Double percentileValue = MathUtils.getPercentileExcel(values, p);
//        }
//    }

    private Map<Integer, Double> getPercentileCoeff2(double[] values) {
        Map<Integer, Double> percentileCoeff = new HashMap<>();
        for (int p = 10; p <= 100; p = p + 10) {
            Double percentileValue = MathUtils.getPercentileExcel(values, p);
            percentileCoeff.put(p/10, percentileValue);
        }
        return percentileCoeff;
    }

    private double[] normalizeBetaValues(double[] values) {
        double[] normalizedValues = new double[values.length];
        for (int i = 0; i < values.length; i++) {
            double newValue = 0.0;
            double betaValue = values[i];
            if (betaValue <= 0) {
                newValue = 0.0;
            } else if(betaValue > 0 && betaValue <= 1) {
                newValue = -betaValue;
            } else if (betaValue > 1) {
                newValue = -1;
            }
            normalizedValues[i] = newValue;
        }
        return normalizedValues;
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
