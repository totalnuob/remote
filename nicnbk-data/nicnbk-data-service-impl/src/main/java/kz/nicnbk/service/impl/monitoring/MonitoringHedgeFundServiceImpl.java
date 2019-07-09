package kz.nicnbk.service.impl.monitoring;

import kz.nicnbk.common.service.util.DateUtils;
import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.repo.api.monitoring.*;
import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.repo.model.lookup.BenchmarkLookup;
import kz.nicnbk.repo.model.lookup.monitoring.MonitoringHedgeFundClassTypeLookup;
import kz.nicnbk.repo.model.lookup.monitoring.MonitoringHedgeFundFundInfoTypeLookup;
import kz.nicnbk.repo.model.monitoring.*;
import kz.nicnbk.service.api.benchmark.BenchmarkService;
import kz.nicnbk.service.api.employee.EmployeeService;
import kz.nicnbk.service.api.monitoring.MonitoringHedgeFundService;
import kz.nicnbk.service.api.monitoring.NicPortfolioService;
import kz.nicnbk.service.dto.benchmark.BenchmarkValueDto;
import kz.nicnbk.service.dto.common.EntitySaveResponseDto;
import kz.nicnbk.service.dto.common.ResponseStatusType;
import kz.nicnbk.service.dto.employee.EmployeeDto;
import kz.nicnbk.service.dto.monitoring.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Service
public class MonitoringHedgeFundServiceImpl implements MonitoringHedgeFundService {

    private static final Logger logger = LoggerFactory.getLogger(MonitoringHedgeFundServiceImpl.class);

    @Autowired
    private MonitoringHedgeFundGeneralInformationRepository generalInformationRepository;

    @Autowired
    private MonitoringHedgeFundContributionToReturnRepository contributionToReturnRepository;

    @Autowired
    private MonitoringHedgeFundFundAllocationByStrategyRepository allocationByStrategyRepository;

    @Autowired
    private MonitoringHedgeFundFundInformationRepository fundFundInformationRepository;

    @Autowired
    private MonitoringHedgeFundApprovedFundRepository approvedFundRepository;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private BenchmarkService benchmarkService;

    @Autowired
    private NicPortfolioService nicPortfolioService;

    @Override
    public MonitoringHedgeFundListDataHolderDto getAllData() {
        //return getDummyData();
        MonitoringHedgeFundListDataHolderDto dateHolder = new MonitoringHedgeFundListDataHolderDto();

        Map<Date, MonitoringHedgeFundDataHolderDto> dataMap = new HashMap<>();
        // General information
        setGeneralInformation(dataMap);

        // Allocation by strategy
        setAllocationByStrategy(dataMap);

        // Fund info
        setFundInformation(dataMap);

        // Contribution to return
        setContributionToReturn(dataMap);

        // Approved funds
        setApprovedFunds(dataMap);

        // Check dates
        //Date minDate = null;
        Date maxDate = null;
        for(Date date: dataMap.keySet()){
//            if(minDate == null || minDate.after(date)){
//                minDate = date;
//            }
            if(maxDate == null || maxDate.before(date)){
                maxDate = date;
            }
        }

        // Set returns
        NicPortfolioResultDto nicPortfolioResultDto = this.nicPortfolioService.get();
        if(nicPortfolioResultDto != null && nicPortfolioResultDto.getStatus() != null &&
                nicPortfolioResultDto.getStatus().getCode().equalsIgnoreCase(ResponseStatusType.SUCCESS.getCode()) && nicPortfolioResultDto.getNicPortfolioDtoList() != null){
            for(NicPortfolioDto portfolioDto: nicPortfolioResultDto.getNicPortfolioDtoList()){
//                if(minDate != null && portfolioDto.getDate() != null && minDate.after(portfolioDto.getDate())){
//                    continue;
//                }
                if(maxDate != null && portfolioDto.getDate() != null && maxDate.before(portfolioDto.getDate())){
                    continue;
                }
                if(portfolioDto.getHedgeFundsMtd() != null){
                    dateHolder.getReturnsConsolidated().add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getLastDayOfCurrentMonth(portfolioDto.getDate()), portfolioDto.getHedgeFundsMtd()));
                }
                if(portfolioDto.getHedgeFundsClassAMtd() != null) {
                    dateHolder.getReturnsClassA().add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getLastDayOfCurrentMonth(portfolioDto.getDate()), portfolioDto.getHedgeFundsClassAMtd()));
                }
                if(portfolioDto.getHedgeFundsClassBMtd() != null) {
                    dateHolder.getReturnsClassB().add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getLastDayOfCurrentMonth(portfolioDto.getDate()), portfolioDto.getHedgeFundsClassBMtd()));
                }
            }

            Collections.sort(dateHolder.getReturnsConsolidated());
            Collections.sort(dateHolder.getReturnsClassA());
            Collections.sort(dateHolder.getReturnsClassB());
        }


        // Set HFRI
        Date dateFrom = DateUtils.getDate("01.08.2015"); // TODO: min date for benchmarks
        Date dateTo = maxDate;

/*        for(MonitoringHedgeFundDataHolderDto dataValue: dataMap.values()){
            dateTo = dateTo == null ? dataValue.getDate() : (dateTo.before(dataValue.getDate()) ? dataValue.getDate() : dateTo);
        }*/
        List<BenchmarkValueDto> benchmarks = benchmarkService.getBenchmarkValuesForDatesAndType(dateFrom, dateTo, BenchmarkLookup.HFRI.getCode());
        if(benchmarks != null){
            List<MonitoringHedgeFundDateDoubleValueDto> returnsHFRI = new ArrayList<>();
            for(BenchmarkValueDto dto: benchmarks){
                MonitoringHedgeFundDateDoubleValueDto valueHFRI = new MonitoringHedgeFundDateDoubleValueDto();
                valueHFRI.setDate(dto.getDate());
                valueHFRI.setValue(dto.getReturnValue());
                returnsHFRI.add(valueHFRI);
            }

            dateHolder.setReturnsHFRI(returnsHFRI);
        }

        List<MonitoringHedgeFundDataHolderDto> valueList = new ArrayList<MonitoringHedgeFundDataHolderDto>(dataMap.values());
        Collections.sort(valueList);

        dateHolder.setData(valueList);

        return dateHolder;
    }

    private void setGeneralInformation(Map<Date, MonitoringHedgeFundDataHolderDto> dataMap){
        List<MonitoringHedgeFundGeneralInformation> allGeneralInfo = this.generalInformationRepository.findAll();
        if(allGeneralInfo != null){
            for(MonitoringHedgeFundGeneralInformation generalInfo: allGeneralInfo){
                if(dataMap.get(generalInfo.getMonitoringDate()) == null){
                    dataMap.put(generalInfo.getMonitoringDate(), new MonitoringHedgeFundDataHolderDto(DateUtils.getDateOnly(generalInfo.getMonitoringDate())));
                }

                if(generalInfo.getType().getCode().equalsIgnoreCase(MonitoringHedgeFundClassTypeLookup.OVERALL.getCode())){
                    if(dataMap.get(generalInfo.getMonitoringDate()).getMonitoringData().getOverall().getGeneralInformation() == null){
                        dataMap.get(generalInfo.getMonitoringDate()).getMonitoringData().getOverall().setGeneralInformation(new ArrayList<>());
                    }
                    MonitoringHedgeFundNameTextValueDto dto = new MonitoringHedgeFundNameTextValueDto();
                    dto.setName(generalInfo.getName());
                    dto.setValue(generalInfo.getValue());
                    dataMap.get(generalInfo.getMonitoringDate()).getMonitoringData().getOverall().getGeneralInformation().add(dto);

                }else if(generalInfo.getType().getCode().equalsIgnoreCase(MonitoringHedgeFundClassTypeLookup.CLASS_A.getCode())){
                    if(dataMap.get(generalInfo.getMonitoringDate()).getMonitoringData().getClassA().getGeneralInformation() == null){
                        dataMap.get(generalInfo.getMonitoringDate()).getMonitoringData().getClassA().setGeneralInformation(new ArrayList<>());
                    }
                    MonitoringHedgeFundNameTextValueDto dto = new MonitoringHedgeFundNameTextValueDto();
                    dto.setName(generalInfo.getName());
                    dto.setValue(generalInfo.getValue());
                    dataMap.get(generalInfo.getMonitoringDate()).getMonitoringData().getClassA().getGeneralInformation().add(dto);
                }else if(generalInfo.getType().getCode().equalsIgnoreCase(MonitoringHedgeFundClassTypeLookup.CLASS_B.getCode())){
                    if(dataMap.get(generalInfo.getMonitoringDate()).getMonitoringData().getClassB().getGeneralInformation() == null){
                        dataMap.get(generalInfo.getMonitoringDate()).getMonitoringData().getClassB().setGeneralInformation(new ArrayList<>());
                    }
                    MonitoringHedgeFundNameTextValueDto dto = new MonitoringHedgeFundNameTextValueDto();
                    dto.setName(generalInfo.getName());
                    dto.setValue(generalInfo.getValue());
                    dataMap.get(generalInfo.getMonitoringDate()).getMonitoringData().getClassB().getGeneralInformation().add(dto);
                }
            }
        }
    }

    private void setAllocationByStrategy(Map<Date, MonitoringHedgeFundDataHolderDto> dataMap){
        List<MonitoringHedgeFundAllocationByStrategy> allAllocationByStrategy = this.allocationByStrategyRepository.findAll();
        if(allAllocationByStrategy != null){
            for(MonitoringHedgeFundAllocationByStrategy allocationByStrategy: allAllocationByStrategy){
                if(dataMap.get(allocationByStrategy.getMonitoringDate()) == null){
                    dataMap.put(allocationByStrategy.getMonitoringDate(), new MonitoringHedgeFundDataHolderDto(DateUtils.getDateOnly(allocationByStrategy.getMonitoringDate())));
                }

                if(allocationByStrategy.getType().getCode().equalsIgnoreCase(MonitoringHedgeFundClassTypeLookup.OVERALL.getCode())){
                    if(dataMap.get(allocationByStrategy.getMonitoringDate()).getMonitoringData().getOverall().getAllocationByStrategy() == null){
                        dataMap.get(allocationByStrategy.getMonitoringDate()).getMonitoringData().getOverall().setAllocationByStrategy(new ArrayList<>());
                    }
                    MonitoringHedgeFundNameDoubleValueDto dto = new MonitoringHedgeFundNameDoubleValueDto();
                    dto.setName(allocationByStrategy.getName());
                    dto.setValue(allocationByStrategy.getValue());
                    dataMap.get(allocationByStrategy.getMonitoringDate()).getMonitoringData().getOverall().getAllocationByStrategy().add(dto);

                }else if(allocationByStrategy.getType().getCode().equalsIgnoreCase(MonitoringHedgeFundClassTypeLookup.CLASS_A.getCode())){
                    if(dataMap.get(allocationByStrategy.getMonitoringDate()).getMonitoringData().getClassA().getAllocationByStrategy() == null){
                        dataMap.get(allocationByStrategy.getMonitoringDate()).getMonitoringData().getClassA().setAllocationByStrategy(new ArrayList<>());
                    }
                    MonitoringHedgeFundNameDoubleValueDto dto = new MonitoringHedgeFundNameDoubleValueDto();
                    dto.setName(allocationByStrategy.getName());
                    dto.setValue(allocationByStrategy.getValue());
                    dataMap.get(allocationByStrategy.getMonitoringDate()).getMonitoringData().getClassA().getAllocationByStrategy().add(dto);
                }else if(allocationByStrategy.getType().getCode().equalsIgnoreCase(MonitoringHedgeFundClassTypeLookup.CLASS_B.getCode())){
                    if(dataMap.get(allocationByStrategy.getMonitoringDate()).getMonitoringData().getClassB().getAllocationByStrategy() == null){
                        dataMap.get(allocationByStrategy.getMonitoringDate()).getMonitoringData().getClassB().setAllocationByStrategy(new ArrayList<>());
                    }
                    MonitoringHedgeFundNameDoubleValueDto dto = new MonitoringHedgeFundNameDoubleValueDto();
                    dto.setName(allocationByStrategy.getName());
                    dto.setValue(allocationByStrategy.getValue());
                    dataMap.get(allocationByStrategy.getMonitoringDate()).getMonitoringData().getClassB().getAllocationByStrategy().add(dto);
                }
            }
        }
    }

    private void setFundInformation(Map<Date, MonitoringHedgeFundDataHolderDto> dataMap){
        List<MonitoringHedgeFundFundInformation> allFundInfo = this.fundFundInformationRepository.findAll();
        if(allFundInfo != null){
            for(MonitoringHedgeFundFundInformation fundInfo: allFundInfo){
                if(dataMap.get(fundInfo.getMonitoringDate()) == null){
                    dataMap.put(fundInfo.getMonitoringDate(), new MonitoringHedgeFundDataHolderDto(DateUtils.getDateOnly(fundInfo.getMonitoringDate())));
                }

                if(fundInfo.getType().getCode().equalsIgnoreCase(MonitoringHedgeFundClassTypeLookup.CLASS_A.getCode())){
                    if(fundInfo.getFundInfoType().getCode().equalsIgnoreCase(MonitoringHedgeFundFundInfoTypeLookup.POSITIVE_CONTRIBUTORS.getCode())){
                        if(dataMap.get(fundInfo.getMonitoringDate()).getMonitoringData().getClassA().getPositiveContributors() == null){
                            dataMap.get(fundInfo.getMonitoringDate()).getMonitoringData().getClassA().setPositiveContributors(new ArrayList<>());
                        }
                        MonitoringHedgeFundTopFundInfoDto dto = new MonitoringHedgeFundTopFundInfoDto();
                        dto.setFundName(fundInfo.getFundName());
                        dto.setStrategy(fundInfo.getStrategy());
                        dto.setYtd(fundInfo.getYtd());
                        dataMap.get(fundInfo.getMonitoringDate()).getMonitoringData().getClassA().getPositiveContributors().add(dto);

                    }else if(fundInfo.getFundInfoType().getCode().equalsIgnoreCase(MonitoringHedgeFundFundInfoTypeLookup.NEGATIVE_CONTRIBUTORS.getCode())){
                        if(dataMap.get(fundInfo.getMonitoringDate()).getMonitoringData().getClassA().getNegativeContributors() == null){
                            dataMap.get(fundInfo.getMonitoringDate()).getMonitoringData().getClassA().setNegativeContributors(new ArrayList<>());
                        }
                        MonitoringHedgeFundTopFundInfoDto dto = new MonitoringHedgeFundTopFundInfoDto();
                        dto.setFundName(fundInfo.getFundName());
                        dto.setStrategy(fundInfo.getStrategy());
                        dto.setYtd(fundInfo.getYtd());
                        dataMap.get(fundInfo.getMonitoringDate()).getMonitoringData().getClassA().getNegativeContributors().add(dto);

                    }else if(fundInfo.getFundInfoType().getCode().equalsIgnoreCase(MonitoringHedgeFundFundInfoTypeLookup.FUND_ALLOCATIONS.getCode())){
                        if(dataMap.get(fundInfo.getMonitoringDate()).getMonitoringData().getClassA().getFundAllocations() == null){
                            dataMap.get(fundInfo.getMonitoringDate()).getMonitoringData().getClassA().setFundAllocations(new ArrayList<>());
                        }
                        MonitoringHedgeFundTopFundInfoDto dto = new MonitoringHedgeFundTopFundInfoDto();
                        dto.setFundName(fundInfo.getFundName());
                        dto.setStrategy(fundInfo.getStrategy());
                        dto.setAllocation(fundInfo.getAllocation());
                        dataMap.get(fundInfo.getMonitoringDate()).getMonitoringData().getClassA().getFundAllocations().add(dto);

                    }

                }else if(fundInfo.getType().getCode().equalsIgnoreCase(MonitoringHedgeFundClassTypeLookup.CLASS_B.getCode())){
                    if(fundInfo.getFundInfoType().getCode().equalsIgnoreCase(MonitoringHedgeFundFundInfoTypeLookup.FUND_ALLOCATIONS.getCode())){
                        if(dataMap.get(fundInfo.getMonitoringDate()).getMonitoringData().getClassB().getFundAllocations() == null){
                            dataMap.get(fundInfo.getMonitoringDate()).getMonitoringData().getClassB().setFundAllocations(new ArrayList<>());
                        }
                        MonitoringHedgeFundTopFundInfoDto dto = new MonitoringHedgeFundTopFundInfoDto();
                        dto.setFundName(fundInfo.getFundName());
                        dto.setStrategy(fundInfo.getStrategy());
                        dto.setAllocation(fundInfo.getAllocation());
                        dataMap.get(fundInfo.getMonitoringDate()).getMonitoringData().getClassB().getFundAllocations().add(dto);

                    }
                }
            }
        }
    }

    private void setContributionToReturn(Map<Date, MonitoringHedgeFundDataHolderDto> dataMap){
        List<MonitoringHedgeFundContributionToReturn> allContributionToReturns = this.contributionToReturnRepository.findAll();
        if(allContributionToReturns != null){
            for(MonitoringHedgeFundContributionToReturn contributionToReturn: allContributionToReturns){
                if(dataMap.get(contributionToReturn.getMonitoringDate()) == null){
                    dataMap.put(contributionToReturn.getMonitoringDate(), new MonitoringHedgeFundDataHolderDto(DateUtils.getDateOnly(contributionToReturn.getMonitoringDate())));
                }

                if(contributionToReturn.getType().getCode().equalsIgnoreCase(MonitoringHedgeFundClassTypeLookup.OVERALL.getCode())){
                    if(dataMap.get(contributionToReturn.getMonitoringDate()).getMonitoringData().getOverall().getContributionToReturn() == null){
                        dataMap.get(contributionToReturn.getMonitoringDate()).getMonitoringData().getOverall().setContributionToReturn(new ArrayList<>());
                    }
                    MonitoringHedgeFundNameDoubleValueDto dto = new MonitoringHedgeFundNameDoubleValueDto();
                    dto.setName(contributionToReturn.getName());
                    dto.setValue(contributionToReturn.getValue());
                    dataMap.get(contributionToReturn.getMonitoringDate()).getMonitoringData().getOverall().getContributionToReturn().add(dto);
                }
            }
        }
    }

    private void setApprovedFunds(Map<Date, MonitoringHedgeFundDataHolderDto> dataMap){
        List<MonitoringHedgeFundApprovedFund> allApprovedFunds = this.approvedFundRepository.findAll();
        if(allApprovedFunds != null){
            for(MonitoringHedgeFundApprovedFund approvedFund: allApprovedFunds){
                if(dataMap.get(approvedFund.getMonitoringDate()) == null){
                    dataMap.put(approvedFund.getMonitoringDate(), new MonitoringHedgeFundDataHolderDto(DateUtils.getDateOnly(approvedFund.getMonitoringDate())));
                }
                if(dataMap.get(approvedFund.getMonitoringDate()).getMonitoringData().getApprovedFunds() == null){
                    dataMap.get(approvedFund.getMonitoringDate()).getMonitoringData().setApprovedFunds(new ArrayList<>());
                }
                MonitoringHedgeFundApprovedFundInfoDto dto = new MonitoringHedgeFundApprovedFundInfoDto();
                dto.setFundName(approvedFund.getFundName());
                dto.setManagerName(approvedFund.getManagerName());
                dto.setStrategy(approvedFund.getStrategy());
                dto.setProtocol(approvedFund.getProtocol());
                dto.setApproveDate(DateUtils.getDateOnly(approvedFund.getApproveDate()));
                dto.setLimits(approvedFund.getLimits());
                dataMap.get(approvedFund.getMonitoringDate()).getMonitoringData().getApprovedFunds().add(dto);
            }
        }
    }

    @Override
    public MonitoringHedgeFundDataHolderDto getMonitoringDataByDate(MonitoringHedgeFundSearchParamsDto searchParams){
        MonitoringHedgeFundDataHolderDto dataHolderDto = new MonitoringHedgeFundDataHolderDto();
        dataHolderDto.setDate(searchParams.getMonitoringDate());

        // OVERALL
        // General information
        dataHolderDto.getMonitoringData().getOverall().setGeneralInformation(getGeneralInformation(MonitoringHedgeFundClassTypeLookup.OVERALL.getId(), searchParams.getMonitoringDate()));
        // Contribution to return
        dataHolderDto.getMonitoringData().getOverall().setContributionToReturn(getContributionToReturn(MonitoringHedgeFundClassTypeLookup.OVERALL.getId(), searchParams.getMonitoringDate()));
        // Allocation by strategy
        dataHolderDto.getMonitoringData().getOverall().setAllocationByStrategy(getAllocationByStrategy(
                MonitoringHedgeFundClassTypeLookup.OVERALL.getId(), searchParams.getMonitoringDate()));

        // CLASS A
        // General information
        dataHolderDto.getMonitoringData().getClassA().setGeneralInformation(getGeneralInformation(
                MonitoringHedgeFundClassTypeLookup.CLASS_A.getId(), searchParams.getMonitoringDate()));
        //Positive Contributors
        dataHolderDto.getMonitoringData().getClassA().setPositiveContributors(getFundInformation(
                MonitoringHedgeFundClassTypeLookup.CLASS_A.getId(),
                MonitoringHedgeFundFundInfoTypeLookup.POSITIVE_CONTRIBUTORS.getId(), searchParams.getMonitoringDate()));
        // Negative Contributors
        dataHolderDto.getMonitoringData().getClassA().setNegativeContributors(getFundInformation(
                MonitoringHedgeFundClassTypeLookup.CLASS_A.getId(),
                MonitoringHedgeFundFundInfoTypeLookup.NEGATIVE_CONTRIBUTORS.getId(), searchParams.getMonitoringDate()));
        // Fund Allocations
        dataHolderDto.getMonitoringData().getClassA().setFundAllocations(getFundInformation(
                MonitoringHedgeFundClassTypeLookup.CLASS_A.getId(),
                MonitoringHedgeFundFundInfoTypeLookup.FUND_ALLOCATIONS.getId(), searchParams.getMonitoringDate()));
        // Allocation by strategy
        dataHolderDto.getMonitoringData().getClassA().setAllocationByStrategy(getAllocationByStrategy(MonitoringHedgeFundClassTypeLookup.CLASS_A.getId(), searchParams.getMonitoringDate()));

        // CLASS B
        // General information
        dataHolderDto.getMonitoringData().getClassB().setGeneralInformation(getGeneralInformation(MonitoringHedgeFundClassTypeLookup.CLASS_B.getId(), searchParams.getMonitoringDate()));
        // Fund allocations
        dataHolderDto.getMonitoringData().getClassB().setFundAllocations(getFundInformation(
                MonitoringHedgeFundClassTypeLookup.CLASS_B.getId(),
                MonitoringHedgeFundFundInfoTypeLookup.FUND_ALLOCATIONS.getId(), searchParams.getMonitoringDate()));
        // Allocation by strategy
        dataHolderDto.getMonitoringData().getClassB().setAllocationByStrategy(getAllocationByStrategy(
                MonitoringHedgeFundClassTypeLookup.CLASS_B.getId(), searchParams.getMonitoringDate()));

        // APPROVED FUNDS
        dataHolderDto.getMonitoringData().setApprovedFunds(getApprovedFunds(searchParams.getMonitoringDate()));

        return dataHolderDto;
    }

    @Override
    public MonitoringHedgeFundDataDto getMonitoringDataForPreviousDate(MonitoringHedgeFundTypedSearchParamsDto searchParams){
        MonitoringHedgeFundDataDto dataDto = new MonitoringHedgeFundDataDto();

        if(searchParams.getType().equalsIgnoreCase("OVERALL")){
            MonitoringHedgeFundDataOverallDto overallDto = new MonitoringHedgeFundDataOverallDto();
            // General information
            overallDto.setGeneralInformation(getPreviousGeneralInformation(MonitoringHedgeFundClassTypeLookup.OVERALL.getId()));
            // Contribution to return
            overallDto.setContributionToReturn(getPreviousContributionToReturn(MonitoringHedgeFundClassTypeLookup.OVERALL.getId()));

            // Allocation by strategy
            overallDto.setAllocationByStrategy(getPreviousAllocationByStrategy(MonitoringHedgeFundClassTypeLookup.OVERALL.getId()));

            dataDto.setOverall(overallDto);
        }else if(searchParams.getType().equalsIgnoreCase("CLASS_A")){
            MonitoringHedgeFundDataClassADto classADto = new MonitoringHedgeFundDataClassADto();
            // General information
            classADto.setGeneralInformation(getPreviousGeneralInformation(MonitoringHedgeFundClassTypeLookup.CLASS_A.getId()));

            // Allocation by strategy
            classADto.setAllocationByStrategy(getPreviousAllocationByStrategy(MonitoringHedgeFundClassTypeLookup.CLASS_A.getId()));

            // Positive contributors
            classADto.setPositiveContributors(getPreviousFundInformation(MonitoringHedgeFundClassTypeLookup.CLASS_A.getId(),
                    MonitoringHedgeFundFundInfoTypeLookup.POSITIVE_CONTRIBUTORS.getId()));

            // Negative contributors
            classADto.setNegativeContributors(getPreviousFundInformation(MonitoringHedgeFundClassTypeLookup.CLASS_A.getId(),
                    MonitoringHedgeFundFundInfoTypeLookup.NEGATIVE_CONTRIBUTORS.getId()));

            // Fund allocations
            classADto.setFundAllocations(getPreviousFundInformation(MonitoringHedgeFundClassTypeLookup.CLASS_A.getId(),
                    MonitoringHedgeFundFundInfoTypeLookup.FUND_ALLOCATIONS.getId()));

            dataDto.setClassA(classADto);

        }else if(searchParams.getType().equalsIgnoreCase("CLASS_B")){
            MonitoringHedgeFundDataClassBDto classBDto = new MonitoringHedgeFundDataClassBDto();
            // General information
            classBDto.setGeneralInformation(getPreviousGeneralInformation(MonitoringHedgeFundClassTypeLookup.CLASS_B.getId()));

            // Allocation by strategy
            classBDto.setAllocationByStrategy(getPreviousAllocationByStrategy(MonitoringHedgeFundClassTypeLookup.CLASS_B.getId()));

            // Fund allocations
            classBDto.setFundAllocations(getPreviousFundInformation(MonitoringHedgeFundClassTypeLookup.CLASS_B.getId(),
                    MonitoringHedgeFundFundInfoTypeLookup.FUND_ALLOCATIONS.getId()));

            dataDto.setClassB(classBDto);

        }else if(searchParams.getType().equalsIgnoreCase("APPROVED_FUNDS")){
            dataDto.setApprovedFunds(getPreviousApprovedFunds());
        }

        return dataDto;
    }

    private List<MonitoringHedgeFundNameTextValueDto> getPreviousGeneralInformation(Integer typeId){
        List<MonitoringHedgeFundNameTextValueDto> records = new ArrayList<>();
        List<MonitoringHedgeFundGeneralInformation> recentGenealInfoRecords =
                this.generalInformationRepository.getMostRecentRecordsBeforeDate(typeId);
        if(recentGenealInfoRecords != null && !recentGenealInfoRecords.isEmpty()){
            for(MonitoringHedgeFundGeneralInformation record: recentGenealInfoRecords){
                MonitoringHedgeFundNameTextValueDto dto = new MonitoringHedgeFundNameTextValueDto();
                dto.setName(record.getName());
                dto.setValue(record.getValue());
                records.add(dto);
            }
        }
        return records;
    }

    private List<MonitoringHedgeFundNameDoubleValueDto> getPreviousAllocationByStrategy(Integer typeId){
        List<MonitoringHedgeFundNameDoubleValueDto> records = new ArrayList<>();
        List<MonitoringHedgeFundAllocationByStrategy> recentAllocationByStrategyRecords =
                this.allocationByStrategyRepository.getMostRecentRecordsBeforeDate(typeId);
        if(recentAllocationByStrategyRecords != null && !recentAllocationByStrategyRecords.isEmpty()){
            for(MonitoringHedgeFundAllocationByStrategy record: recentAllocationByStrategyRecords){
                MonitoringHedgeFundNameDoubleValueDto dto = new MonitoringHedgeFundNameDoubleValueDto();
                dto.setName(record.getName());
                dto.setValue(record.getValue());
                records.add(dto);
            }
        }
        return records;
    }

    private List<MonitoringHedgeFundNameDoubleValueDto> getPreviousContributionToReturn(Integer typeId){
        List<MonitoringHedgeFundNameDoubleValueDto> records = new ArrayList<>();
        List<MonitoringHedgeFundContributionToReturn> recentContributionRecords =
                this.contributionToReturnRepository.getMostRecentRecordsBeforeDate(typeId);
        if(recentContributionRecords != null && !recentContributionRecords.isEmpty()){
            for(MonitoringHedgeFundContributionToReturn record: recentContributionRecords){
                MonitoringHedgeFundNameDoubleValueDto dto = new MonitoringHedgeFundNameDoubleValueDto();
                dto.setName(record.getName());
                dto.setValue(record.getValue());
                records.add(dto);
            }
        }
        return records;
    }

    private List<MonitoringHedgeFundTopFundInfoDto> getPreviousFundInformation(Integer typeId, Integer fundInfoTypeId){
        List<MonitoringHedgeFundTopFundInfoDto> records = new ArrayList<>();
        List<MonitoringHedgeFundFundInformation> recentFundAllocationsContributors =
                this.fundFundInformationRepository.getMostRecentRecordsBeforeDate(typeId, fundInfoTypeId);
        if(recentFundAllocationsContributors != null && !recentFundAllocationsContributors.isEmpty()){
            for(MonitoringHedgeFundFundInformation record: recentFundAllocationsContributors){
                MonitoringHedgeFundTopFundInfoDto dto = new MonitoringHedgeFundTopFundInfoDto();
                dto.setFundName(record.getFundName());
                dto.setStrategy(record.getStrategy());
                dto.setYtd(record.getYtd());
                dto.setAllocation(record.getAllocation());
                records.add(dto);
            }
        }
        return records;
    }

    private List<MonitoringHedgeFundApprovedFundInfoDto> getPreviousApprovedFunds(){
        List<MonitoringHedgeFundApprovedFundInfoDto> records = new ArrayList<>();
        List<MonitoringHedgeFundApprovedFund> recentApprovedFunds =
                this.approvedFundRepository.getMostRecentRecordsBeforeDate();
        if(recentApprovedFunds != null && !recentApprovedFunds.isEmpty()){
            for(MonitoringHedgeFundApprovedFund record: recentApprovedFunds){
                MonitoringHedgeFundApprovedFundInfoDto dto = new MonitoringHedgeFundApprovedFundInfoDto();
                dto.setFundName(record.getFundName());
                dto.setManagerName(record.getManagerName());
                dto.setStrategy(record.getStrategy());
                dto.setProtocol(record.getProtocol());
                dto.setApproveDate(DateUtils.getDateOnly(record.getApproveDate()));
                dto.setLimits(record.getLimits());
                records.add(dto);
            }
        }
        return records;
    }



    private List<MonitoringHedgeFundNameTextValueDto> getGeneralInformation(Integer typeId, Date monitoringDate){
        List<MonitoringHedgeFundNameTextValueDto> dtoList = new ArrayList<>();
        List<MonitoringHedgeFundGeneralInformation> entities =
                this.generalInformationRepository.findByTypeIdAndMonitoringDate(typeId, monitoringDate);
        if(entities != null && !entities.isEmpty()){
            for(MonitoringHedgeFundGeneralInformation entity: entities){
                MonitoringHedgeFundNameTextValueDto dto = new MonitoringHedgeFundNameTextValueDto();
                dto.setName(entity.getName());
                dto.setValue(entity.getValue());
                dtoList.add(dto);
            }
        }

        return dtoList;
    }

    private List<MonitoringHedgeFundNameDoubleValueDto> getContributionToReturn(Integer typeId, Date monitoringDate){
        List<MonitoringHedgeFundNameDoubleValueDto> dtoList = new ArrayList<>();
        List<MonitoringHedgeFundContributionToReturn> entities =
                this.contributionToReturnRepository.findByTypeIdAndMonitoringDate(typeId, monitoringDate);
        if(entities != null && !entities.isEmpty()){
            for(MonitoringHedgeFundContributionToReturn entity: entities){
                MonitoringHedgeFundNameDoubleValueDto dto = new MonitoringHedgeFundNameDoubleValueDto();
                dto.setName(entity.getName());
                dto.setValue(entity.getValue());
                dtoList.add(dto);
            }
        }

        return dtoList;
    }

    private List<MonitoringHedgeFundNameDoubleValueDto> getAllocationByStrategy(Integer typeId, Date monitoringDate){
        List<MonitoringHedgeFundNameDoubleValueDto> dtoList = new ArrayList<>();
        List<MonitoringHedgeFundAllocationByStrategy> entities =
                this.allocationByStrategyRepository.findByTypeIdAndMonitoringDate(typeId, monitoringDate);
        if(entities != null && !entities.isEmpty()){
            for(MonitoringHedgeFundAllocationByStrategy entity: entities){
                MonitoringHedgeFundNameDoubleValueDto dto = new MonitoringHedgeFundNameDoubleValueDto();
                dto.setName(entity.getName());
                dto.setValue(entity.getValue());
                dtoList.add(dto);
            }
        }
        return dtoList;
    }

    private List<MonitoringHedgeFundTopFundInfoDto> getFundInformation(Integer typeId, Integer fundInfoTypeId, Date monitoringDate){
        List<MonitoringHedgeFundTopFundInfoDto> dtoList = new ArrayList<>();
        List<MonitoringHedgeFundFundInformation> entities =
                this.fundFundInformationRepository.findByTypeIdAndFundInfoTypeIdAndMonitoringDate(typeId, fundInfoTypeId, monitoringDate);
        if(entities != null && !entities.isEmpty()){
            for(MonitoringHedgeFundFundInformation entity: entities){
                MonitoringHedgeFundTopFundInfoDto dto = new MonitoringHedgeFundTopFundInfoDto();
                dto.setFundName(entity.getFundName());
                dto.setStrategy(entity.getStrategy());
                dto.setYtd(entity.getYtd());
                dto.setAllocation(entity.getAllocation());

                dtoList.add(dto);
            }
        }
        return dtoList;
    }

    private List<MonitoringHedgeFundApprovedFundInfoDto> getApprovedFunds(Date monitoringDate){
        List<MonitoringHedgeFundApprovedFundInfoDto> dtoList = new ArrayList<>();
        List<MonitoringHedgeFundApprovedFund> entities =
                this.approvedFundRepository.findByMonitoringDate(monitoringDate);
        if(entities != null && !entities.isEmpty()){
            for(MonitoringHedgeFundApprovedFund entity: entities){
                MonitoringHedgeFundApprovedFundInfoDto dto = new MonitoringHedgeFundApprovedFundInfoDto();
                dto.setFundName(entity.getFundName());
                dto.setManagerName(entity.getManagerName());
                dto.setStrategy(entity.getStrategy());
                dto.setProtocol(entity.getProtocol());
                dto.setApproveDate(DateUtils.getDateOnly(entity.getApproveDate()));
                dto.setLimits(entity.getLimits());

                dtoList.add(dto);
            }
        }
        return dtoList;
    }

    @Override
    @Transactional
    public EntitySaveResponseDto save(MonitoringHedgeFundDataHolderDto dataDto, String username) {

        // TODO: check transactional - one repository save fails

        EntitySaveResponseDto saveResponse = checkMonitoringData(dataDto, username);

        // Check fields
        if(saveResponse.isError()){
            return saveResponse;
        }

        // SAVE
        try {
            EmployeeDto employeeDto = this.employeeService.findByUsername(username);

            saveMonitoringDataOverall(dataDto, employeeDto);
            saveMonitoringDataClassA(dataDto, employeeDto);
            saveMonitoringDataClassB(dataDto, employeeDto);
            saveMonitoringDataApprovedFunds(dataDto, employeeDto);

        }catch (Exception ex){
            // For the purpose of logging
            logger.error("Error when saving monitoring data (Hedge Funds) with exception.", ex);
            throw ex;
        }
        saveResponse.setStatus(ResponseStatusType.SUCCESS);
        return saveResponse;
    }

    private void saveMonitoringDataOverall(MonitoringHedgeFundDataHolderDto dataDto,  EmployeeDto employeeDto){
        if(dataDto == null || dataDto.getMonitoringData() == null || dataDto.getMonitoringData().getOverall() == null) {
            return;
        }
        // OVERALL - general info
        saveMonitoringDataGeneralInformation(dataDto.getMonitoringData().getOverall().getGeneralInformation(),
                employeeDto, dataDto.getDate(), MonitoringHedgeFundClassTypeLookup.OVERALL.getId());


        // OVERALL - contribution to return
        if (dataDto.getMonitoringData().getOverall() != null && dataDto.getMonitoringData().getOverall().getContributionToReturn() != null) {
            List<MonitoringHedgeFundContributionToReturn> entities = new ArrayList<>();
            for (MonitoringHedgeFundNameDoubleValueDto infoDto : dataDto.getMonitoringData().getOverall().getContributionToReturn()) {
                MonitoringHedgeFundContributionToReturn entity = new MonitoringHedgeFundContributionToReturn();
                entity.setCreator(new Employee(employeeDto.getId()));
                entity.setCreationDate(new Date());
                entity.setType(new MonitoringHedgeFundClassType(MonitoringHedgeFundClassTypeLookup.OVERALL.getId()));
                entity.setMonitoringDate(dataDto.getDate());
                entity.setName(infoDto.getName());
                entity.setValue(infoDto.getValue());
                entities.add(entity);
            }
            contributionToReturnRepository.deleteByTypeIdAndDate(MonitoringHedgeFundClassTypeLookup.OVERALL.getId(), dataDto.getDate());
            contributionToReturnRepository.save(entities);
        }

        // OVERALL - allocation by strategy
        saveMonitoringDataAllocationByStrategy(dataDto.getMonitoringData().getOverall().getAllocationByStrategy(), employeeDto,
                dataDto.getDate(), MonitoringHedgeFundClassTypeLookup.OVERALL.getId());
    }

    private void saveMonitoringDataClassA(MonitoringHedgeFundDataHolderDto dataDto,  EmployeeDto employeeDto){
        if(dataDto == null || dataDto.getMonitoringData() == null || dataDto.getMonitoringData().getClassA() == null) {
            return;
        }
        // CLASS A - general info
        saveMonitoringDataGeneralInformation(dataDto.getMonitoringData().getClassA().getGeneralInformation(),
                employeeDto, dataDto.getDate(), MonitoringHedgeFundClassTypeLookup.CLASS_A.getId());

        // CLASS A - allocation by strategy
        saveMonitoringDataAllocationByStrategy(dataDto.getMonitoringData().getClassA().getAllocationByStrategy(), employeeDto,
                dataDto.getDate(), MonitoringHedgeFundClassTypeLookup.CLASS_A.getId());

        // CLASS A - positive contributions
        saveMonitoringDataFundInformation(dataDto.getMonitoringData().getClassA().getPositiveContributors(), employeeDto,
                dataDto.getDate(), MonitoringHedgeFundClassTypeLookup.CLASS_A.getId(), MonitoringHedgeFundFundInfoTypeLookup.POSITIVE_CONTRIBUTORS.getId());

        // CLASS A - negative contributions
        saveMonitoringDataFundInformation(dataDto.getMonitoringData().getClassA().getNegativeContributors(), employeeDto,
                dataDto.getDate(), MonitoringHedgeFundClassTypeLookup.CLASS_A.getId(), MonitoringHedgeFundFundInfoTypeLookup.NEGATIVE_CONTRIBUTORS.getId());

        // CLASS A - fund allocations
        saveMonitoringDataFundInformation(dataDto.getMonitoringData().getClassA().getFundAllocations(), employeeDto,
                dataDto.getDate(), MonitoringHedgeFundClassTypeLookup.CLASS_A.getId(), MonitoringHedgeFundFundInfoTypeLookup.FUND_ALLOCATIONS.getId());
    }

    private void saveMonitoringDataClassB(MonitoringHedgeFundDataHolderDto dataDto,  EmployeeDto employeeDto){
        if(dataDto == null || dataDto.getMonitoringData() == null || dataDto.getMonitoringData().getClassB() == null) {
            return;
        }
        // CLASS B - general info
        saveMonitoringDataGeneralInformation(dataDto.getMonitoringData().getClassB().getGeneralInformation(),
                employeeDto, dataDto.getDate(), MonitoringHedgeFundClassTypeLookup.CLASS_B.getId());

        // CLASS B - allocation by strategy
        saveMonitoringDataAllocationByStrategy(dataDto.getMonitoringData().getClassB().getAllocationByStrategy(), employeeDto,
                dataDto.getDate(), MonitoringHedgeFundClassTypeLookup.CLASS_B.getId());

        // CLASS B - fund allocations
        saveMonitoringDataFundInformation(dataDto.getMonitoringData().getClassB().getFundAllocations(), employeeDto,
                dataDto.getDate(), MonitoringHedgeFundClassTypeLookup.CLASS_B.getId(), MonitoringHedgeFundFundInfoTypeLookup.FUND_ALLOCATIONS.getId());

    }

    private void saveMonitoringDataApprovedFunds(MonitoringHedgeFundDataHolderDto dataDto, EmployeeDto employeeDto){
        if (dataDto != null && dataDto.getMonitoringData() != null && dataDto.getMonitoringData().getApprovedFunds() != null) {
            List<MonitoringHedgeFundApprovedFund> entities = new ArrayList<>();
            for (MonitoringHedgeFundApprovedFundInfoDto infoDto : dataDto.getMonitoringData().getApprovedFunds()) {
                MonitoringHedgeFundApprovedFund entity = new MonitoringHedgeFundApprovedFund();
                entity.setCreator(new Employee(employeeDto.getId()));
                entity.setCreationDate(new Date());
                entity.setMonitoringDate(dataDto.getDate());

                entity.setFundName(infoDto.getFundName());
                entity.setManagerName(infoDto.getManagerName());
                entity.setStrategy(infoDto.getStrategy());
                entity.setProtocol(infoDto.getProtocol());
                entity.setApproveDate(infoDto.getApproveDate());
                entity.setLimits(infoDto.getLimits());

                entities.add(entity);
            }
            approvedFundRepository.deleteByDate(dataDto.getDate());
            approvedFundRepository.save(entities);
        }
    }

    private void saveMonitoringDataGeneralInformation(List<MonitoringHedgeFundNameTextValueDto> dtoList, EmployeeDto employeeDto,
                                                      Date monitoringDate, Integer typeId){
        if (dtoList != null) {
            List<MonitoringHedgeFundGeneralInformation> entities = new ArrayList<>();
            for (MonitoringHedgeFundNameTextValueDto generalInfoDto : dtoList) {
                MonitoringHedgeFundGeneralInformation entity = new MonitoringHedgeFundGeneralInformation();
                entity.setCreator(new Employee(employeeDto.getId()));
                entity.setCreationDate(new Date());
                entity.setType(new MonitoringHedgeFundClassType(typeId));
                entity.setMonitoringDate(monitoringDate);
                entity.setName(generalInfoDto.getName());
                entity.setValue(generalInfoDto.getValue());
                entities.add(entity);
            }
            generalInformationRepository.deleteByTypeIdAndDate(typeId, monitoringDate);
            generalInformationRepository.save(entities);
        }
    }

    private void saveMonitoringDataAllocationByStrategy(List<MonitoringHedgeFundNameDoubleValueDto> dtoList, EmployeeDto employeeDto,
                                                        Date monitoringDate, Integer typeId){
        if (dtoList != null) {
            List<MonitoringHedgeFundAllocationByStrategy> entities = new ArrayList<>();
            for (MonitoringHedgeFundNameDoubleValueDto infoDto : dtoList) {
                MonitoringHedgeFundAllocationByStrategy entity = new MonitoringHedgeFundAllocationByStrategy();
                entity.setCreator(new Employee(employeeDto.getId()));
                entity.setCreationDate(new Date());
                entity.setType(new MonitoringHedgeFundClassType(typeId));
                entity.setMonitoringDate(monitoringDate);
                entity.setName(infoDto.getName());
                entity.setValue(infoDto.getValue());
                entities.add(entity);
            }
            allocationByStrategyRepository.deleteByTypeIdAndDate(typeId, monitoringDate);
            allocationByStrategyRepository.save(entities);
        }
    }

    private void saveMonitoringDataFundInformation(List<MonitoringHedgeFundTopFundInfoDto> dtoList, EmployeeDto employeeDto,
                                                   Date monitoringDate, Integer typeId, Integer fundInfoTypeId){
        if (dtoList != null) {
            List<MonitoringHedgeFundFundInformation> entities = new ArrayList<>();
            for (MonitoringHedgeFundTopFundInfoDto infoDto : dtoList) {
                MonitoringHedgeFundFundInformation entity = new MonitoringHedgeFundFundInformation();
                entity.setCreator(new Employee(employeeDto.getId()));
                entity.setCreationDate(new Date());
                entity.setType(new MonitoringHedgeFundClassType(typeId));
                entity.setMonitoringDate(monitoringDate);
                entity.setFundInfoType(new MonitoringHedgeFundInfoType(fundInfoTypeId));

                entity.setFundName(infoDto.getFundName());
                entity.setStrategy(infoDto.getStrategy());
                entity.setAllocation(infoDto.getAllocation());
                entity.setYtd(infoDto.getYtd());
                entities.add(entity);
            }
            fundFundInformationRepository.deleteByTypeIdAndFundInfoTypeIdAndDate(typeId, fundInfoTypeId, monitoringDate);
            fundFundInformationRepository.save(entities);
        }
    }


    private EntitySaveResponseDto checkMonitoringData(MonitoringHedgeFundDataHolderDto data, String username){
        EntitySaveResponseDto saveResponse = new EntitySaveResponseDto();
        // 1. Check null
        if(data == null || data.getMonitoringData() == null){
            String errorMessage = "Failed to save Hedge Fund monitoring data: data object is null";
            logger.error(errorMessage + " [username=" + username + "]");
            saveResponse.setErrorMessageEn(errorMessage);
            return saveResponse;
        }
        // 2. Check date
        if(data.getDate() == null){
            String errorMessage = "Failed to save Hedge Fund monitoring data: date missing";
            logger.error(errorMessage + " [username=" + username + "]");
            saveResponse.setErrorMessageEn(errorMessage);
            return saveResponse;
        }else{
            // TODO: check existing date





        }
        // 3. Check data content
        saveResponse = checkMonitoringDataOverall(data, username);
        if(saveResponse.isError()){
            return saveResponse;
        }
        saveResponse = checkMonitoringDataClassA(data, username);
        if(saveResponse.isError()){
            return saveResponse;
        }
        saveResponse = checkMonitoringDataClassB(data, username);
        if(saveResponse.isError()){
            return saveResponse;
        }
        saveResponse = checkMonitoringDataApprovedFunds(data, username);
        if(saveResponse.isError()){
            return saveResponse;
        }

        saveResponse.setStatus(ResponseStatusType.SUCCESS);
        return saveResponse;
    }

    private EntitySaveResponseDto checkMonitoringDataOverall(MonitoringHedgeFundDataHolderDto data, String username){
        EntitySaveResponseDto saveResponse = new EntitySaveResponseDto();
        // Check OVERALL
        if(data.getMonitoringData().getOverall() == null){
            String errorMessage = "Failed to save Hedge Fund monitoring data: OVERALL data missing";
            logger.error(errorMessage + " [username=" + username + "]");
            saveResponse.setErrorMessageEn(errorMessage);
            return saveResponse;
        }
        // Overall - General information
        if(data.getMonitoringData().getOverall().getGeneralInformation() == null || data.getMonitoringData().getOverall().getGeneralInformation().isEmpty()){
            String errorMessage = "Failed to save Hedge Fund monitoring data: OVERALL - GENERAL INFORMATION data missing";
            logger.error(errorMessage + " [username=" + username + "]");
            saveResponse.setErrorMessageEn(errorMessage);
            return saveResponse;
        }else{
            for(MonitoringHedgeFundNameTextValueDto nameValueDataDto: data.getMonitoringData().getOverall().getGeneralInformation()){
                if(StringUtils.isEmpty(nameValueDataDto.getName()) || StringUtils.isEmpty(nameValueDataDto.getValue())){
                    String errorMessage = "Failed to save Hedge Fund monitoring data: OVERALL - GENERAL INFORMATION record has empty fields";
                    logger.error(errorMessage + " [username=" + username + "]");
                    saveResponse.setErrorMessageEn(errorMessage);
                    return saveResponse;
                }
            }
        }
        // Overall - Contribution to return
        if(data.getMonitoringData().getOverall().getContributionToReturn() == null || data.getMonitoringData().getOverall().getContributionToReturn().isEmpty()){
            String errorMessage = "Failed to save Hedge Fund monitoring data: OVERALL - CONTRIBUTION TO RETURN data missing";
            logger.error(errorMessage + " [username=" + username + "]");
            saveResponse.setErrorMessageEn(errorMessage);
            return saveResponse;
        }else{
            for(MonitoringHedgeFundNameDoubleValueDto nameValueDataDto: data.getMonitoringData().getOverall().getContributionToReturn()){
                if(StringUtils.isEmpty(nameValueDataDto.getName()) || nameValueDataDto.getValue() == null){
                    String errorMessage = "Failed to save Hedge Fund monitoring data: OVERALL - CONTRIBUTION TO RETURN record has empty fields";
                    logger.error(errorMessage + " [username=" + username + "]");
                    saveResponse.setErrorMessageEn(errorMessage);
                    return saveResponse;
                }
            }
        }
        // Overall - Allocation by strategy
        if(data.getMonitoringData().getOverall().getAllocationByStrategy() == null || data.getMonitoringData().getOverall().getAllocationByStrategy().isEmpty()){
            String errorMessage = "Failed to save Hedge Fund monitoring data: OVERALL - ALLOCATION BY STRATEGY data missing";
            logger.error(errorMessage + " [username=" + username + "]");
            saveResponse.setErrorMessageEn(errorMessage);
            return saveResponse;
        }else{
            for(MonitoringHedgeFundNameDoubleValueDto nameValueDataDto: data.getMonitoringData().getOverall().getAllocationByStrategy()){
                if(StringUtils.isEmpty(nameValueDataDto.getName()) || nameValueDataDto.getValue() == null){
                    String errorMessage = "Failed to save Hedge Fund monitoring data: OVERALL - ALLOCATION BY STRATEGY record has empty fields";
                    logger.error(errorMessage + " [username=" + username + "]");
                    saveResponse.setErrorMessageEn(errorMessage);
                    return saveResponse;
                }
            }
        }

        saveResponse.setStatus(ResponseStatusType.SUCCESS);
        return saveResponse;
    }

    private EntitySaveResponseDto checkMonitoringDataClassA(MonitoringHedgeFundDataHolderDto data, String username){
        EntitySaveResponseDto saveResponse = new EntitySaveResponseDto();

        // Check CLASS A
        if(data.getMonitoringData().getClassA() == null){
            String errorMessage = "Failed to save Hedge Fund monitoring data: CLASS A data missing";
            logger.error(errorMessage + " [username=" + username + "]");
            saveResponse.setErrorMessageEn(errorMessage);
            return saveResponse;
        }
        // Class A - General information
        if(data.getMonitoringData().getClassA().getGeneralInformation() == null || data.getMonitoringData().getClassA().getGeneralInformation().isEmpty()){
            String errorMessage = "Failed to save Hedge Fund monitoring data: CLASS A - GENERAL INFORMATION data missing";
            logger.error(errorMessage + " [username=" + username + "]");
            saveResponse.setErrorMessageEn(errorMessage);
            return saveResponse;
        }else{
            for(MonitoringHedgeFundNameTextValueDto nameValueDataDto: data.getMonitoringData().getClassA().getGeneralInformation()){
                if(StringUtils.isEmpty(nameValueDataDto.getName()) || StringUtils.isEmpty(nameValueDataDto.getValue())){
                    String errorMessage = "Failed to save Hedge Fund monitoring data: CLASS A - GENERAL INFORMATION record has empty fields";
                    logger.error(errorMessage + " [username=" + username + "]");
                    saveResponse.setErrorMessageEn(errorMessage);
                    return saveResponse;
                }
            }
        }

        // Class A - top positive contributors
        if(data.getMonitoringData().getClassA().getPositiveContributors() == null || data.getMonitoringData().getClassA().getPositiveContributors().isEmpty()){
            String errorMessage = "Failed to save Hedge Fund monitoring data: CLASS A - POSITIVE CONTRIBUTORS data missing";
            logger.error(errorMessage + " [username=" + username + "]");
            saveResponse.setErrorMessageEn(errorMessage);
            return saveResponse;
        }else{
            for(MonitoringHedgeFundTopFundInfoDto fundInfo: data.getMonitoringData().getClassA().getPositiveContributors()){
                if(StringUtils.isEmpty(fundInfo.getFundName()) || StringUtils.isEmpty(fundInfo.getStrategy()) || fundInfo.getYtd() == null){
                    String errorMessage = "Failed to save Hedge Fund monitoring data: CLASS A - POSITIVE CONTRIBUTORS record has empty fields";
                    logger.error(errorMessage + " [username=" + username + "]");
                    saveResponse.setErrorMessageEn(errorMessage);
                    return saveResponse;
                }
            }
        }
        // Class A - top negative contributors
        if(data.getMonitoringData().getClassA().getNegativeContributors() == null || data.getMonitoringData().getClassA().getNegativeContributors().isEmpty()){
            String errorMessage = "Failed to save Hedge Fund monitoring data: CLASS A - NEGATIVE CONTRIBUTORS data missing";
            logger.error(errorMessage + " [username=" + username + "]");
            saveResponse.setErrorMessageEn(errorMessage);
            return saveResponse;
        }else{
            for(MonitoringHedgeFundTopFundInfoDto fundInfo: data.getMonitoringData().getClassA().getNegativeContributors()){
                if(StringUtils.isEmpty(fundInfo.getFundName()) || StringUtils.isEmpty(fundInfo.getStrategy()) || fundInfo.getYtd() == null){
                    String errorMessage = "Failed to save Hedge Fund monitoring data: CLASS A - NEGATIVE CONTRIBUTORS record has empty fields";
                    logger.error(errorMessage + " [username=" + username + "]");
                    saveResponse.setErrorMessageEn(errorMessage);
                    return saveResponse;
                }
            }
        }

        // Class A - top fund allocations
        if(data.getMonitoringData().getClassA().getFundAllocations() == null || data.getMonitoringData().getClassA().getFundAllocations().isEmpty()){
            String errorMessage = "Failed to save Hedge Fund monitoring data: CLASS A - FUND ALLOCATIONS data missing";
            logger.error(errorMessage + " [username=" + username + "]");
            saveResponse.setErrorMessageEn(errorMessage);
            return saveResponse;
        }else{
            for(MonitoringHedgeFundTopFundInfoDto fundInfo: data.getMonitoringData().getClassA().getFundAllocations()){
                if(StringUtils.isEmpty(fundInfo.getFundName()) || StringUtils.isEmpty(fundInfo.getStrategy()) || fundInfo.getAllocation() == null){
                    String errorMessage = "Failed to save Hedge Fund monitoring data: CLASS A -  FUND ALLOCATIONS record has empty fields";
                    logger.error(errorMessage + " [username=" + username + "]");
                    saveResponse.setErrorMessageEn(errorMessage);
                    return saveResponse;
                }
            }
        }

        // Class A - allocation by strategy
        if(data.getMonitoringData().getClassA().getAllocationByStrategy() == null || data.getMonitoringData().getClassA().getAllocationByStrategy().isEmpty()){
            String errorMessage = "Failed to save Hedge Fund monitoring data: CLASS A - ALLOCATION BY STRATEGY data missing";
            logger.error(errorMessage + " [username=" + username + "]");
            saveResponse.setErrorMessageEn(errorMessage);
            return saveResponse;
        }else{
            for(MonitoringHedgeFundNameDoubleValueDto nameValueDataDto: data.getMonitoringData().getClassA().getAllocationByStrategy()){
                if(StringUtils.isEmpty(nameValueDataDto.getName()) || nameValueDataDto.getValue() == null){
                    String errorMessage = "Failed to save Hedge Fund monitoring data: CLASS A - ALLOCATION BY STRATEGY record has empty fields";
                    logger.error(errorMessage + " [username=" + username + "]");
                    saveResponse.setErrorMessageEn(errorMessage);
                    return saveResponse;
                }
            }
        }

        saveResponse.setStatus(ResponseStatusType.SUCCESS);
        return saveResponse;
    }

    private EntitySaveResponseDto checkMonitoringDataClassB(MonitoringHedgeFundDataHolderDto data, String username){
        EntitySaveResponseDto saveResponse = new EntitySaveResponseDto();

        if(data.getMonitoringData().getClassB() == null){
            String errorMessage = "Failed to save Hedge Fund monitoring data: CLASS B data missing";
            logger.error(errorMessage + " [username=" + username + "]");
            saveResponse.setErrorMessageEn(errorMessage);
            return saveResponse;
        }
        // Class B - General information
        if(data.getMonitoringData().getClassB().getGeneralInformation() == null || data.getMonitoringData().getClassB().getGeneralInformation().isEmpty()){
            String errorMessage = "Failed to save Hedge Fund monitoring data: CLASS B - GENERAL INFORMATION data missing";
            logger.error(errorMessage + " [username=" + username + "]");
            saveResponse.setErrorMessageEn(errorMessage);
            return saveResponse;
        }else{
            for(MonitoringHedgeFundNameTextValueDto nameValueDataDto: data.getMonitoringData().getClassB().getGeneralInformation()){
                if(StringUtils.isEmpty(nameValueDataDto.getName()) || StringUtils.isEmpty(nameValueDataDto.getValue())){
                    String errorMessage = "Failed to save Hedge Fund monitoring data: CLASS B - GENERAL INFORMATION record has empty fields";
                    logger.error(errorMessage + " [username=" + username + "]");
                    saveResponse.setErrorMessageEn(errorMessage);
                    return saveResponse;
                }
            }
        }
        // Class B - fund allocations
        if(data.getMonitoringData().getClassB().getFundAllocations() == null || data.getMonitoringData().getClassB().getFundAllocations().isEmpty()){
            String errorMessage = "Failed to save Hedge Fund monitoring data: CLASS B - FUND ALLOCATIONS data missing";
            logger.error(errorMessage + " [username=" + username + "]");
            saveResponse.setErrorMessageEn(errorMessage);
            return saveResponse;
        }else{
            for(MonitoringHedgeFundTopFundInfoDto fundInfo: data.getMonitoringData().getClassB().getFundAllocations()){
                if(StringUtils.isEmpty(fundInfo.getFundName()) || StringUtils.isEmpty(fundInfo.getStrategy()) || fundInfo.getAllocation() == null){
                    String errorMessage = "Failed to save Hedge Fund monitoring data: CLASS B -  FUND ALLOCATIONS record has empty fields";
                    logger.error(errorMessage + " [username=" + username + "]");
                    saveResponse.setErrorMessageEn(errorMessage);
                    return saveResponse;
                }
            }
        }
        // Class B - allocation by strategy
        if(data.getMonitoringData().getClassB().getAllocationByStrategy() == null || data.getMonitoringData().getClassB().getAllocationByStrategy().isEmpty()){
            String errorMessage = "Failed to save Hedge Fund monitoring data: CLASS B - ALLOCATION BY STRATEGY data missing";
            logger.error(errorMessage + " [username=" + username + "]");
            saveResponse.setErrorMessageEn(errorMessage);
            return saveResponse;
        }else{
            for(MonitoringHedgeFundNameDoubleValueDto nameValueDataDto: data.getMonitoringData().getClassB().getAllocationByStrategy()){
                if(StringUtils.isEmpty(nameValueDataDto.getName()) || nameValueDataDto.getValue() == null){
                    String errorMessage = "Failed to save Hedge Fund monitoring data: CLASS B - ALLOCATION BY STRATEGY record has empty fields";
                    logger.error(errorMessage + " [username=" + username + "]");
                    saveResponse.setErrorMessageEn(errorMessage);
                    return saveResponse;
                }
            }
        }
        saveResponse.setStatus(ResponseStatusType.SUCCESS);
        return saveResponse;
    }

    private EntitySaveResponseDto checkMonitoringDataApprovedFunds(MonitoringHedgeFundDataHolderDto data, String username){
        EntitySaveResponseDto saveResponse = new EntitySaveResponseDto();

        if(data.getMonitoringData().getApprovedFunds() == null || data.getMonitoringData().getApprovedFunds().isEmpty()){
            String errorMessage = "Failed to save Hedge Fund monitoring data: APPROVED FUNDS data missing";
            logger.error(errorMessage + " [username=" + username + "]");
            saveResponse.setErrorMessageEn(errorMessage);
            return saveResponse;
        }else{
            for(MonitoringHedgeFundApprovedFundInfoDto approvedFund:  data.getMonitoringData().getApprovedFunds()){
                if(StringUtils.isEmpty(approvedFund.getFundName()) || StringUtils.isEmpty(approvedFund.getManagerName())
                        || StringUtils.isEmpty(approvedFund.getStrategy()) || StringUtils.isEmpty(approvedFund.getProtocol()) ||
                        approvedFund.getApproveDate() == null || approvedFund.getLimits() == null){
                    String errorMessage = "Failed to save Hedge Fund monitoring data: APPROVED FUNDS record has empty fields";
                    logger.error(errorMessage + " [username=" + username + "]");
                    saveResponse.setErrorMessageEn(errorMessage);
                    return saveResponse;
                }
            }
        }
        saveResponse.setStatus(ResponseStatusType.SUCCESS);
        return saveResponse;
    }

    private List<MonitoringHedgeFundDataHolderDto> getDummyData(){
        List<MonitoringHedgeFundDataHolderDto> dataList = new ArrayList<>();

        MonitoringHedgeFundDataDto data = new MonitoringHedgeFundDataDto();

        // HFRI
        List<MonitoringHedgeFundDateDoubleValueDto> returnsHFRI = new ArrayList<>();

        returnsHFRI.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("31.08.2015"), 0.09));
        returnsHFRI.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("30.09.2015"), 0.09));
        returnsHFRI.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("31.10.2015"), 0.09));
        returnsHFRI.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("30.11.2015"), 0.09));
        returnsHFRI.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("31.12.2015"), 0.09));
        returnsHFRI.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("31.01.2016"), 0.09));
        returnsHFRI.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("28.02.2016"), 0.09));
        returnsHFRI.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("31.03.2016"), 0.09));
        returnsHFRI.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("30.04.2016"), 0.09));
        returnsHFRI.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("31.05.2016"), 0.09));
        returnsHFRI.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("30.06.2016"), 0.09));
        returnsHFRI.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("31.07.2016"), 0.09));
        returnsHFRI.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("31.08.2016"), 0.09));
        returnsHFRI.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("30.09.2016"), 0.09));
        returnsHFRI.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("31.10.2016"), 0.09));
        returnsHFRI.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("30.11.2016"), 0.09));
        returnsHFRI.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("31.12.2016"), 0.09));
        returnsHFRI.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("31.01.2017"), 0.09));
        returnsHFRI.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("28.02.2017"), 0.09));
        returnsHFRI.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("31.03.2017"), 0.09));
        returnsHFRI.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("30.04.2017"), 0.09));
        returnsHFRI.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("31.05.2017"), 0.09));
        returnsHFRI.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("30.06.2017"), 0.09));
        returnsHFRI.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("31.07.2017"), 0.09));
        returnsHFRI.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("31.08.2017"), 0.09));
        returnsHFRI.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("30.09.2017"), 0.09));
        returnsHFRI.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("31.10.2017"), 0.09));
        returnsHFRI.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("30.11.2017"), 0.09));
        returnsHFRI.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("31.12.2017"), 0.09));
        returnsHFRI.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("31.01.2018"), 0.09));
        returnsHFRI.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("28.02.2018"), 0.09));
        returnsHFRI.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("31.03.2018"), 0.09));
        returnsHFRI.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("30.04.2018"), 0.09));
        returnsHFRI.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("31.05.2018"), 0.09));
        returnsHFRI.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("30.06.2018"), 0.09));
        returnsHFRI.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("31.07.2018"), 0.09));
        returnsHFRI.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("31.08.2018"), 0.09));
        returnsHFRI.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("30.09.2018"), 0.09));
        returnsHFRI.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("31.10.2018"), 0.09));
        returnsHFRI.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("30.11.2018"), 0.09));
        returnsHFRI.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("31.12.2018"), 0.09));
        returnsHFRI.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("31.01.2019"), 0.09));
        returnsHFRI.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("28.02.2019"), 0.09));
        returnsHFRI.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("31.03.2019"), 0.09));
        returnsHFRI.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("30.04.2019"), 0.09));

        //data.setReturnsHFRI(returnsHFRI);


        // OVERALL
        MonitoringHedgeFundDataOverallDto overall = new MonitoringHedgeFundDataOverallDto();
        // general info
        List<MonitoringHedgeFundNameTextValueDto> generalInformation = new ArrayList<>();
        MonitoringHedgeFundNameTextValueDto info1 = new MonitoringHedgeFundNameTextValueDto();
        info1.setName("NAME 1");
        info1.setValue("600 million USD");
        MonitoringHedgeFundNameTextValueDto info2 = new MonitoringHedgeFundNameTextValueDto();
        info2.setName("NAME 2");
        info2.setValue("324 500 000 USD");
        generalInformation.add(info1);
        generalInformation.add(info2);
        overall.setGeneralInformation(generalInformation);

        // contribution
        List<MonitoringHedgeFundNameDoubleValueDto> contributions = new ArrayList<>();
        MonitoringHedgeFundNameDoubleValueDto value1 = new MonitoringHedgeFundNameDoubleValueDto();
        value1.setName("_Quantitative");
        value1.setValue(0.0);

        MonitoringHedgeFundNameDoubleValueDto value3 = new MonitoringHedgeFundNameDoubleValueDto();
        value3.setName("_Equities");
        value3.setValue(2.86);

        MonitoringHedgeFundNameDoubleValueDto value2 = new MonitoringHedgeFundNameDoubleValueDto();
        value2.setName("_Credit");
        value2.setValue(0.62);

        MonitoringHedgeFundNameDoubleValueDto value4 = new MonitoringHedgeFundNameDoubleValueDto();
        value4.setName("_Multi-Strategy");
        value4.setValue(0.10);

        MonitoringHedgeFundNameDoubleValueDto value5 = new MonitoringHedgeFundNameDoubleValueDto();
        value5.setName("_Macro");
        value5.setValue(0.55);

        MonitoringHedgeFundNameDoubleValueDto value6 = new MonitoringHedgeFundNameDoubleValueDto();
        value6.setName("_Relative Value");
        value6.setValue(0.48);

        contributions.add(value1);
        contributions.add(value2);
        contributions.add(value3);
        contributions.add(value4);
        contributions.add(value5);
        contributions.add(value6);
        overall.setContributionToReturn(contributions);

        // allocation by strategy
        List<MonitoringHedgeFundNameDoubleValueDto> allocations = new ArrayList<>();
        MonitoringHedgeFundNameDoubleValueDto v1 = new MonitoringHedgeFundNameDoubleValueDto();
        v1.setName("_Credit");
        v1.setValue(25.23);

        MonitoringHedgeFundNameDoubleValueDto v2 = new MonitoringHedgeFundNameDoubleValueDto();
        v2.setName("_Equities");
        v2.setValue(24.67);

        MonitoringHedgeFundNameDoubleValueDto v3 = new MonitoringHedgeFundNameDoubleValueDto();
        v3.setName("_Macro");
        v3.setValue(8.35);

        MonitoringHedgeFundNameDoubleValueDto v4 = new MonitoringHedgeFundNameDoubleValueDto();
        v4.setName("_Relative Value");
        v4.setValue(13.43);

        MonitoringHedgeFundNameDoubleValueDto v5 = new MonitoringHedgeFundNameDoubleValueDto();
        v5.setName("_Multi-Strategy");
        v5.setValue(13.75);

        MonitoringHedgeFundNameDoubleValueDto v6 = new MonitoringHedgeFundNameDoubleValueDto();
        v6.setName("_Quantitative");
        v6.setValue(10.47);

        MonitoringHedgeFundNameDoubleValueDto v7 = new MonitoringHedgeFundNameDoubleValueDto();
        v7.setName("_Cash");
        v7.setValue(3.76);

        allocations.add(v1);
        allocations.add(v2);
        allocations.add(v3);
        allocations.add(v4);
        allocations.add(v5);
        allocations.add(v6);
        allocations.add(v7);
        overall.setAllocationByStrategy(allocations);

        // Returns
        List<MonitoringHedgeFundDateDoubleValueDto> returns = new ArrayList<>();

        returns.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("31.08.2015"), 0.01));
        returns.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("30.09.2015"), 0.01));
        returns.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("31.10.2015"), 0.01));
        returns.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("30.11.2015"), 0.01));
        returns.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("31.12.2015"), 0.01));
        returns.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("31.01.2016"), 0.01));
        returns.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("28.02.2016"), 0.01));
        returns.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("31.03.2016"), 0.01));
        returns.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("30.04.2016"), 0.01));
        returns.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("31.05.2016"), 0.01));
        returns.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("30.06.2016"), 0.01));
        returns.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("31.07.2016"), 0.01));
        returns.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("31.08.2016"), 0.01));
        returns.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("30.09.2016"), 0.01));
        returns.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("31.10.2016"), 0.01));
        returns.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("30.11.2016"), 0.01));
        returns.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("31.12.2016"), 0.01));
        returns.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("31.01.2017"), 0.01));
        returns.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("28.02.2017"), 0.01));
        returns.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("31.03.2017"), 0.01));
        returns.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("30.04.2017"), 0.01));
        returns.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("31.05.2017"), 0.01));
        returns.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("30.06.2017"), 0.01));
        returns.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("31.07.2017"), 0.01));
        returns.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("31.08.2017"), 0.01));
        returns.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("30.09.2017"), 0.01));
        returns.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("31.10.2017"), 0.01));
        returns.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("30.11.2017"), 0.01));
        returns.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("31.12.2017"), 0.01));
        returns.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("31.01.2018"), 0.01));
        returns.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("28.02.2018"), 0.01));
        returns.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("31.03.2018"), 0.01));
        returns.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("30.04.2018"), 0.01));
        returns.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("31.05.2018"), 0.01));
        returns.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("30.06.2018"), 0.01));
        returns.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("31.07.2018"), 0.01));
        returns.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("31.08.2018"), 0.01));
        returns.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("30.09.2018"), 0.01));
        returns.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("31.10.2018"), 0.01));
        returns.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("30.11.2018"), 0.01));
        returns.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("31.12.2018"), 0.01));
        returns.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("31.01.2019"), 0.01));
        returns.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("28.02.2019"), 0.01));
        returns.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("31.03.2019"), 0.01));
        returns.add(new MonitoringHedgeFundDateDoubleValueDto(DateUtils.getDate("30.04.2019"), 0.01));

        //overall.setReturns(returns);

        data.setOverall(overall);



        // CLASS A
        MonitoringHedgeFundDataClassADto classA = new MonitoringHedgeFundDataClassADto();

        // general info
        List<MonitoringHedgeFundNameTextValueDto> generalInfoA = new ArrayList<>();
        MonitoringHedgeFundNameTextValueDto infoA1 = new MonitoringHedgeFundNameTextValueDto("NAME CLASS A 1", "123");
        MonitoringHedgeFundNameTextValueDto infoA2 = new MonitoringHedgeFundNameTextValueDto("NAME CLASS A 2", "456");
        generalInfoA.add(infoA1);
        generalInfoA.add(infoA2);
        classA.setGeneralInformation(generalInfoA);


        // top 5 positive
        List<MonitoringHedgeFundTopFundInfoDto> positiveContributors = new ArrayList<>();
        MonitoringHedgeFundTopFundInfoDto posA1 = new MonitoringHedgeFundTopFundInfoDto("FUND 1","Macro", 1.0, null);
        MonitoringHedgeFundTopFundInfoDto posA2 = new MonitoringHedgeFundTopFundInfoDto("FUND 2","Macro", 1.0, null);
        MonitoringHedgeFundTopFundInfoDto posA3 = new MonitoringHedgeFundTopFundInfoDto("FUND 3","Macro", 1.0, null);
        MonitoringHedgeFundTopFundInfoDto posA4 = new MonitoringHedgeFundTopFundInfoDto("FUND 4","Macro", 1.0, null);
        MonitoringHedgeFundTopFundInfoDto posA5 = new MonitoringHedgeFundTopFundInfoDto("FUND 5","Macro", 1.0, null);

        positiveContributors.add(posA1);
        positiveContributors.add(posA2);
        positiveContributors.add(posA3);
        positiveContributors.add(posA4);
        positiveContributors.add(posA5);

        classA.setPositiveContributors(positiveContributors);


        // top 5 negative
        List<MonitoringHedgeFundTopFundInfoDto> negativeContributors = new ArrayList<>();
        MonitoringHedgeFundTopFundInfoDto negA1 = new MonitoringHedgeFundTopFundInfoDto("FUND 1","Micro", -1.0, null);
        MonitoringHedgeFundTopFundInfoDto negA2 = new MonitoringHedgeFundTopFundInfoDto("FUND 2","Micro", -1.0, null);
        MonitoringHedgeFundTopFundInfoDto negA3 = new MonitoringHedgeFundTopFundInfoDto("FUND 3","Micro", -1.0, null);
        MonitoringHedgeFundTopFundInfoDto negA4 = new MonitoringHedgeFundTopFundInfoDto("FUND 4","Micro", -1.0, null);
        MonitoringHedgeFundTopFundInfoDto negA5 = new MonitoringHedgeFundTopFundInfoDto("FUND 5","Micro", -1.0, null);

        negativeContributors.add(negA1);
        negativeContributors.add(negA2);
        negativeContributors.add(negA3);
        negativeContributors.add(negA4);
        negativeContributors.add(negA5);

        classA.setNegativeContributors(negativeContributors);


        // top allocations
        List<MonitoringHedgeFundTopFundInfoDto> fundAllocations = new ArrayList<>();
        MonitoringHedgeFundTopFundInfoDto allocA1 = new MonitoringHedgeFundTopFundInfoDto("FUND 1","Micro", null, 10.);
        MonitoringHedgeFundTopFundInfoDto allocA2 = new MonitoringHedgeFundTopFundInfoDto("FUND 2","Micro", null, 10.);
        MonitoringHedgeFundTopFundInfoDto allocA3 = new MonitoringHedgeFundTopFundInfoDto("FUND 3","Micro", null, 10.);
        MonitoringHedgeFundTopFundInfoDto allocA4 = new MonitoringHedgeFundTopFundInfoDto("FUND 4","Micro", null, 10.);
        MonitoringHedgeFundTopFundInfoDto allocA5 = new MonitoringHedgeFundTopFundInfoDto("FUND 5","Micro", null, 10.);
        MonitoringHedgeFundTopFundInfoDto allocA6 = new MonitoringHedgeFundTopFundInfoDto("FUND 6","Micro", null, 10.);
        MonitoringHedgeFundTopFundInfoDto allocA7 = new MonitoringHedgeFundTopFundInfoDto("FUND 7","Micro", null, 10.);
        MonitoringHedgeFundTopFundInfoDto allocA8 = new MonitoringHedgeFundTopFundInfoDto("FUND 8","Micro", null, 10.);
        MonitoringHedgeFundTopFundInfoDto allocA9 = new MonitoringHedgeFundTopFundInfoDto("FUND 9","Micro", null, 10.);
        MonitoringHedgeFundTopFundInfoDto allocA10 = new MonitoringHedgeFundTopFundInfoDto("FUND 10","Micro", null, 10.);

        fundAllocations.add(allocA1);
        fundAllocations.add(allocA2);
        fundAllocations.add(allocA3);
        fundAllocations.add(allocA4);
        fundAllocations.add(allocA5);
        fundAllocations.add(allocA6);
        fundAllocations.add(allocA7);
        fundAllocations.add(allocA8);
        fundAllocations.add(allocA9);
        fundAllocations.add(allocA10);

        classA.setFundAllocations(fundAllocations);

        // allocations by strategy
        List<MonitoringHedgeFundNameDoubleValueDto> allocationsA = new ArrayList<>();
        MonitoringHedgeFundNameDoubleValueDto a1 = new MonitoringHedgeFundNameDoubleValueDto();
        a1.setName("#_Credit");
        a1.setValue(25.23);

        MonitoringHedgeFundNameDoubleValueDto a2 = new MonitoringHedgeFundNameDoubleValueDto();
        a2.setName("#_Equities");
        a2.setValue(24.67);

        MonitoringHedgeFundNameDoubleValueDto a3 = new MonitoringHedgeFundNameDoubleValueDto();
        a3.setName("#_Macro");
        a3.setValue(8.35);

        MonitoringHedgeFundNameDoubleValueDto a4 = new MonitoringHedgeFundNameDoubleValueDto();
        a4.setName("#_Relative Value");
        a4.setValue(13.43);

        allocationsA.add(a1);
        allocationsA.add(a2);
        allocationsA.add(a3);
        allocationsA.add(a4);
        classA.setAllocationByStrategy(allocationsA);

        // RETURNS
        //classA.setReturns(returns);

        data.setClassA(classA);


        // CLASS B
        MonitoringHedgeFundDataClassBDto classB = new MonitoringHedgeFundDataClassBDto();
        //data.setClassB(classB);

        // general info
        List<MonitoringHedgeFundNameTextValueDto> generalInformationB = new ArrayList<>();
        MonitoringHedgeFundNameTextValueDto infob1 = new MonitoringHedgeFundNameTextValueDto();
        infob1.setName("NAME 1 (B)");
        infob1.setValue("600 million USD");
        MonitoringHedgeFundNameTextValueDto infob2 = new MonitoringHedgeFundNameTextValueDto();
        infob2.setName("NAME 2  (B)");
        infob2.setValue("324 500 000 USD");
        generalInformationB.add(infob1);
        generalInformationB.add(infob2);
        classB.setGeneralInformation(generalInformationB);

        // fund allocations
        List<MonitoringHedgeFundTopFundInfoDto> fundAllocationsByStrategyB = new ArrayList<>();
        MonitoringHedgeFundTopFundInfoDto fundB1 = new MonitoringHedgeFundTopFundInfoDto("FUND 1 (B)","Macro", null, 5.0);
        MonitoringHedgeFundTopFundInfoDto fundB2 = new MonitoringHedgeFundTopFundInfoDto("FUND 2 (B)","Macro", null, 5.0);
        MonitoringHedgeFundTopFundInfoDto fundB3 = new MonitoringHedgeFundTopFundInfoDto("FUND 3 (B)","Macro", null, 5.0);
        MonitoringHedgeFundTopFundInfoDto fundB4 = new MonitoringHedgeFundTopFundInfoDto("FUND 4 (B)","Macro", null, 5.0);

        fundAllocationsByStrategyB.add(fundB1);
        fundAllocationsByStrategyB.add(fundB2);
        fundAllocationsByStrategyB.add(fundB3);
        fundAllocationsByStrategyB.add(fundB4);

        classB.setFundAllocations(fundAllocationsByStrategyB);

        // allocations by strategy
        List<MonitoringHedgeFundNameDoubleValueDto> allocationsB = new ArrayList<>();
        MonitoringHedgeFundNameDoubleValueDto b1 = new MonitoringHedgeFundNameDoubleValueDto();
        b1.setName("*_Credit");
        b1.setValue(25.23);

        MonitoringHedgeFundNameDoubleValueDto b2 = new MonitoringHedgeFundNameDoubleValueDto();
        b2.setName("*_Equities");
        b2.setValue(24.67);

        MonitoringHedgeFundNameDoubleValueDto b3 = new MonitoringHedgeFundNameDoubleValueDto();
        b3.setName("*_Macro");
        b3.setValue(8.35);

        MonitoringHedgeFundNameDoubleValueDto b4 = new MonitoringHedgeFundNameDoubleValueDto();
        b4.setName("*_Relative Value");
        b4.setValue(13.43);

        allocationsB.add(b1);
        allocationsB.add(b2);
        allocationsB.add(b3);
        allocationsB.add(b4);
        classB.setAllocationByStrategy(allocationsB);

        // returns
        //classB.setReturns(returns);

        data.setClassB(classB);

        // approved funds
        List<MonitoringHedgeFundApprovedFundInfoDto> approvedFunds = new ArrayList<>();
        MonitoringHedgeFundApprovedFundInfoDto f1 = new MonitoringHedgeFundApprovedFundInfoDto();
        f1.setFundName("FUND 1");
        f1.setManagerName("MANAGER");
        f1.setStrategy("MACRO");
        f1.setProtocol("PROTOCOL");
        f1.setApproveDate(new Date());
        f1.setLimits("LIMITS");
        approvedFunds.add(f1);

        MonitoringHedgeFundApprovedFundInfoDto f2 = new MonitoringHedgeFundApprovedFundInfoDto();
        f2.setFundName("FUND 2");
        f2.setManagerName("MANAGER");
        f2.setStrategy("MACRO");
        f2.setProtocol("PROTOCOL");
        f2.setApproveDate(new Date());
        f2.setLimits("LIMITS");
        approvedFunds.add(f2);

        MonitoringHedgeFundApprovedFundInfoDto f3 = new MonitoringHedgeFundApprovedFundInfoDto();
        f3.setFundName("FUND 3");
        f3.setManagerName("MANAGER");
        f3.setStrategy("MACRO");
        f3.setProtocol("PROTOCOL");
        f3.setApproveDate(new Date());
        f3.setLimits("LIMITS");
        approvedFunds.add(f3);

        data.setApprovedFunds(approvedFunds);


        MonitoringHedgeFundDataHolderDto dataHolder1 = new MonitoringHedgeFundDataHolderDto();
        dataHolder1.setDate(DateUtils.getDate("31.05.2019"));
        dataHolder1.setMonitoringData(data);
        dataList.add(dataHolder1);

        MonitoringHedgeFundDataHolderDto dataHolder2 = new MonitoringHedgeFundDataHolderDto();
        dataHolder2.setDate(DateUtils.getDate("30.04.2019"));
        dataHolder2.setMonitoringData(data);
        dataList.add(dataHolder2);

        return dataList;
    }
}