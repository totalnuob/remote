package kz.nicnbk.service.impl.monitoring;

import kz.nicnbk.repo.api.monitoring.NicPortfolioRepository;
import kz.nicnbk.repo.model.monitoring.NicPortfolio;
import kz.nicnbk.service.api.monitoring.NicPortfolioService;
import kz.nicnbk.service.converter.monitoring.NicPortfolioEntityConverter;
import kz.nicnbk.service.dto.monitoring.NicPortfolioDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Pak on 13.06.2019.
 */

@Service
public class NicPortfolioServiceImpl implements NicPortfolioService {

    private static final Logger logger = LoggerFactory.getLogger(NicPortfolioServiceImpl.class);

    @Autowired
    private NicPortfolioRepository repository;

    @Autowired
    private NicPortfolioEntityConverter converter;

    @Override
    public List<NicPortfolioDto> get() {
        try {
            List<NicPortfolioDto> nicPortfolioDtoList = new ArrayList<>();
            for (NicPortfolio entity : this.repository.findAll()) {
                nicPortfolioDtoList.add(this.converter.disassemble(entity));
            }
            return nicPortfolioDtoList;
        } catch (Exception ex) {
            logger.error("Error loading NIC Portfolio", ex);
            return null;
        }
    }
}
