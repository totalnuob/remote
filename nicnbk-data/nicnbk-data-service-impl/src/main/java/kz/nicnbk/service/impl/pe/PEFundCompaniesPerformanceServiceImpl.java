package kz.nicnbk.service.impl.pe;

import kz.nicnbk.repo.api.pe.PEFundCompaniesPerformanceRepository;
import kz.nicnbk.repo.model.pe.PEFundCompaniesPerformance;
import kz.nicnbk.service.api.pe.PEFundCompaniesPerformanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Pak on 10.10.2017.
 */
@Service
public class PEFundCompaniesPerformanceServiceImpl implements PEFundCompaniesPerformanceService {

    @Autowired
    PEFundCompaniesPerformanceRepository repository;

    @Override
    public Long save(PEFundCompaniesPerformance performance) {
        return repository.save(performance).getId();
    }

    @Override
    public boolean deleteByFundId(Long fundId) {
        this.repository.deleteByFundId(fundId);
        return true;
    }

    @Override
    public List<PEFundCompaniesPerformance> getEntitiesByFundId(Long id) {
        return this.repository.getEntitiesByFundId(id);
    }
}
