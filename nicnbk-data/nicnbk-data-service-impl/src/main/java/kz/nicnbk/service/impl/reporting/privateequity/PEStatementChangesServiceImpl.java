package kz.nicnbk.service.impl.reporting.privateequity;

import kz.nicnbk.common.service.util.ArrayUtils;
import kz.nicnbk.repo.api.reporting.privateequity.ReportingPEStatementChangesRepository;
import kz.nicnbk.repo.model.reporting.PeriodicReport;
import kz.nicnbk.repo.model.reporting.privateequity.ReportingPEStatementChanges;
import kz.nicnbk.service.api.reporting.privateequity.PEStatementChangesService;
import kz.nicnbk.service.converter.reporting.PeriodReportConverter;
import kz.nicnbk.service.dto.reporting.ConsolidatedReportRecordDto;
import kz.nicnbk.service.dto.reporting.ConsolidatedReportRecordHolderDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by magzumov on 17.07.2017.
 */
@Service
public class PEStatementChangesServiceImpl implements PEStatementChangesService {

    private static final Logger logger = LoggerFactory.getLogger(PEStatementChangesServiceImpl.class);

    @Autowired
    private ReportingPEStatementChangesRepository peStatementChangesRepository;

    @Autowired
    private PeriodReportConverter periodReportConverter;

    @Override
    public ReportingPEStatementChanges assemble(ConsolidatedReportRecordDto dto, Long reportId) {
        ReportingPEStatementChanges entity = new ReportingPEStatementChanges();
        entity.setName(dto.getName());

        entity.setBeginningCapitalBalance(dto.getValues()[0]);
        entity.setCapitalContributions(dto.getValues()[1]);
        entity.setDistributions(dto.getValues()[2]);
        entity.setDividendAndInterestIncome(dto.getValues()[3]);
        entity.setOtherIncome(dto.getValues()[4]);
        entity.setManagementFee(dto.getValues()[5]);
        entity.setAdministrationFee(dto.getValues()[6]);
        entity.setAuditTaxFee(dto.getValues()[7]);
        entity.setOrganizationalCosts(dto.getValues()[8]);
        entity.setInterestExpense(dto.getValues()[9]);
        entity.setLicenseFilingFee(dto.getValues()[10]);
        entity.setOtherExpenses(dto.getValues()[11]);
        entity.setRealizedGainLoss(dto.getValues()[13]);
        entity.setUnrealizedGainLoss(dto.getValues()[14]);
        entity.setPriorYearPotentialInterest(dto.getValues()[16]);
        entity.setCurrentYearPotentialInterest(dto.getValues()[17]);

        // report
        entity.setReport(new PeriodicReport(reportId));

        return entity;
    }

    @Override
    public List<ReportingPEStatementChanges> assembleList(List<ConsolidatedReportRecordDto> dtoList, Long reportId) {
        List<ReportingPEStatementChanges> entities = new ArrayList<>();
        if(dtoList != null){
            for(ConsolidatedReportRecordDto dto: dtoList){
                ReportingPEStatementChanges entity = assemble(dto, reportId);
                entities.add(entity);
            }
        }
        return entities;
    }

    @Override
    public boolean save(List<ReportingPEStatementChanges> entities) {
        // TODO: boolean result, check for error?
        if(entities != null){
            this.peStatementChangesRepository.save(entities);
        }
        return true;
    }

    @Override
    public ConsolidatedReportRecordHolderDto get(Long reportId) {
        List<ReportingPEStatementChanges> entities = this.peStatementChangesRepository.getEntitiesByReportId(reportId,
                new PageRequest(0, 1000, new Sort(Sort.Direction.ASC, "id")));

        ConsolidatedReportRecordHolderDto result = new ConsolidatedReportRecordHolderDto();
        List<ConsolidatedReportRecordDto>  records = disassembleList(entities);

        result.setChanges(records);

        if(entities != null) {
            result.setReport(periodReportConverter.disassemble(entities.get(0).getReport()));
        }

        return result;
    }


    // TODO: refactor
    private List<ConsolidatedReportRecordDto> disassembleList(List<ReportingPEStatementChanges> entities){
        List<ConsolidatedReportRecordDto> records = new ArrayList<>();
        Double[] totalSum = new Double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
        if(entities != null && !entities.isEmpty()){
            for(ReportingPEStatementChanges entity: entities) {
                Double netInvestmentIncomeLoss = ArrayUtils.sumArray(new Double[]{entity.getDividendAndInterestIncome(),
                        entity.getOtherIncome(),entity.getManagementFee(), entity.getAdministrationFee(), entity.getAuditTaxFee(),
                        entity.getOrganizationalCosts(), entity.getInterestExpense(), entity.getLicenseFilingFee(), entity.getOtherExpenses()});
                Double balanceBefore = ArrayUtils.sumArray(new Double[]{entity.getBeginningCapitalBalance(), entity.getCapitalContributions(), entity.getDistributions()}) +
                        netInvestmentIncomeLoss + ArrayUtils.sumArray(new Double[]{ entity.getRealizedGainLoss(), entity.getUnrealizedGainLoss()});
                Double total = balanceBefore + ArrayUtils.sumArray(new Double[]{entity.getPriorYearPotentialInterest(), entity.getCurrentYearPotentialInterest()});

                Double[] values = {entity.getBeginningCapitalBalance(), entity.getCapitalContributions(), entity.getDistributions(),
                        entity.getDividendAndInterestIncome(), entity.getOtherIncome(), entity.getManagementFee(), entity.getAdministrationFee(),
                        entity.getAuditTaxFee(), entity.getOrganizationalCosts(), entity.getInterestExpense(), entity.getLicenseFilingFee(),
                        entity.getOtherExpenses(), netInvestmentIncomeLoss, entity.getRealizedGainLoss(), entity.getUnrealizedGainLoss(), balanceBefore,
                        entity.getPriorYearPotentialInterest(), entity.getCurrentYearPotentialInterest(), total};
                ConsolidatedReportRecordDto recordDto = new ConsolidatedReportRecordDto(entity.getName(), null, values, null, false, false);
                records.add(recordDto);

                ArrayUtils.addArrayValues(totalSum, values);
            }
            ConsolidatedReportRecordDto recordDto = new ConsolidatedReportRecordDto("Total", null, totalSum, null, false, false);
            records.add(recordDto);
        }

        return records;
    }

}
