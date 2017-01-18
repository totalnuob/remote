package kz.nicnbk.service.impl.benchmark;

import kz.nicnbk.repo.api.benchmark.BenchmarkValueRepository;
import kz.nicnbk.repo.model.benchmark.BenchmarkValue;
import kz.nicnbk.service.api.benchmark.BenchmarkService;
import kz.nicnbk.service.converter.benchmark.BenchmarkValueEntityConverter;
import kz.nicnbk.service.dto.benchmark.BenchmarkValueDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by magzumov on 05.01.2017.
 */

@Service
public class BenchmarkServiceImpl implements BenchmarkService {

    @Autowired
    private BenchmarkValueRepository benchmarkValueRepository;

    @Autowired
    private BenchmarkValueEntityConverter benchmarkValueEntityConverter;

    @Override
    public List<BenchmarkValueDto> getValuesFromDateAsList(Date fromDate, String benchmarkCode) {

        // TODO: pagination
        int pageNumber = 0;
        int pageSize = 10000;
        Page<BenchmarkValue> entites = this.benchmarkValueRepository.getValuesFromDate(fromDate, benchmarkCode,
                new PageRequest(pageNumber, pageSize, new Sort(Sort.Direction.ASC, "date", "id")));

        List<BenchmarkValueDto> dtoList = this.benchmarkValueEntityConverter.disassembleList(entites.getContent());
        return dtoList;
    }

    @Override
    public double[] getReturnValuesFromDateAsArray(Date fromDate, String benchmarkCode) {
        // TODO: pagination
        int pageNumber = 0;
        int pageSize = 10000;
        Page<BenchmarkValue> entities = this.benchmarkValueRepository.getValuesFromDate(fromDate, benchmarkCode,
                new PageRequest(pageNumber, pageSize, new Sort(Sort.Direction.ASC, "date", "id")));

        double[] values = new double[(int) entities.getTotalElements()];
        for(int i = 0; i < entities.getContent().size(); i++){
            values[i] = entities.getContent().get(i).getReturnValue();
        }
        return values;
    }

    @Override
    public double[] getReturnValuesBetweenDatesAsArray(Date dateFrom, Date dateTo, String benchmarkCode) {
        // TODO: pagination
        int pageNumber = 0;
        int pageSize = 10000;
        Page<BenchmarkValue> entities = this.benchmarkValueRepository.getValuesBetweenDates(dateFrom, dateTo, benchmarkCode,
                new PageRequest(pageNumber, pageSize, new Sort(Sort.Direction.ASC, "date", "id")));

        double[] values = new double[(int) entities.getTotalElements()];
        for(int i = 0; i < entities.getContent().size(); i++){
            values[i] = entities.getContent().get(i).getReturnValue();
        }
        return values;
    }
}
