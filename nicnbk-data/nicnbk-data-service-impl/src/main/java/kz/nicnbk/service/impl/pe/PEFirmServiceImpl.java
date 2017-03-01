package kz.nicnbk.service.impl.pe;

import kz.nicnbk.common.service.util.PaginationUtils;
import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.repo.api.pe.PEFirmRepository;
import kz.nicnbk.repo.model.pe.PEFirm;
import kz.nicnbk.service.api.pe.PEFirmService;
import kz.nicnbk.service.converter.pe.PEFirmEntityConverter;
import kz.nicnbk.service.dto.pe.PEFirmDto;
import kz.nicnbk.service.dto.pe.PEPagedSearchResult;
import kz.nicnbk.service.dto.pe.PESearchParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import java.util.Date;

/**
 * Created by zhambyl on 16-Nov-16.
 */
@Service
public class PEFirmServiceImpl implements PEFirmService {

    @Autowired
    private PEFirmRepository repository;

    @Autowired
    private PEFirmEntityConverter converter;

    @Override
    public Long save(PEFirmDto firmDto) {
        PEFirm entity = converter.assemble(firmDto);
        entity.setUpdateDate(new Date());

        Long id = repository.save(entity).getId();
        return id;
    }

    @Override
    public PEFirmDto get(Long id) {
        PEFirm entity = this.repository.findOne(id);
        PEFirmDto firmDto = this.converter.disassemble(entity);

        return firmDto;
    }

    @Override
    public List<PEFirmDto> findAll() {
        return converter.disassembleList(repository.findAllByOrderByFirmNameDesc());
    }

    @Override
    public PEPagedSearchResult findByName(PESearchParams searchParams) {

        int page = searchParams != null && searchParams.getPage() > 0 ? searchParams.getPage() - 1 : 0;
        int pageSize = searchParams != null && searchParams.getPageSize() > 0 ? searchParams.getPageSize() : DEFAULT_PAGE_SIZE;
        Page<PEFirm> entityPage = this.repository.findByName(searchParams.getName(),
                //new PageRequest(searchParams.getPage(), searchParams.getPageSize(), new Sort(Sort.Direction.DESC, "id")));
                new PageRequest(page, pageSize, new Sort(Sort.Direction.DESC, "id")));

        PEPagedSearchResult result = new PEPagedSearchResult();

        if (entityPage != null) {
            result.setTotalElements(entityPage.getTotalElements());
            if (entityPage.getTotalElements() > 0) {
                result.setShowPageFrom(PaginationUtils.getShowPageFrom(DEFAULT_PAGES_PER_VIEW, page + 1));
                result.setShowPageTo(PaginationUtils.getShowPageTo(DEFAULT_PAGES_PER_VIEW,
                        page + 1, result.getShowPageFrom(), entityPage.getTotalPages()));
            }
            result.setTotalPages(entityPage.getTotalPages());
            result.setCurrentPage(page + 1);
            if (searchParams != null) {
                result.setSearchParams(searchParams.getSearchParamsAsString());
            }
            result.setFirms(this.converter.disassembleList(entityPage.getContent()));
        }
        return result;
    }
}