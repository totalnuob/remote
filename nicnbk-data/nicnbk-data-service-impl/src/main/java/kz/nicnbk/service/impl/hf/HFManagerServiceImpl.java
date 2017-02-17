package kz.nicnbk.service.impl.hf;

import kz.nicnbk.common.service.util.PaginationUtils;
import kz.nicnbk.repo.api.hf.HFManagerRepository;
import kz.nicnbk.repo.model.hf.HFManager;
import kz.nicnbk.service.api.hf.HFManagerService;
import kz.nicnbk.service.api.hf.HedgeFundService;
import kz.nicnbk.service.converter.hf.HFManagerEntityConverter;
import kz.nicnbk.service.dto.hf.HFManagerDto;
import kz.nicnbk.service.dto.hf.HedgeFundManagerPagedSearchResult;
import kz.nicnbk.service.dto.hf.HedgeFundSearchParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by timur on 19.10.2016.
 */
@Service
public class HFManagerServiceImpl implements HFManagerService {

    @Autowired
    private HFManagerRepository repository;

    @Autowired
    private HFManagerEntityConverter converter;

    @Autowired
    private HedgeFundService fundService;

    @Override
    public Long save(HFManagerDto firmDto) {
        HFManager entity = converter.assemble(firmDto);
        Long id = repository.save(entity).getId();
        return id;
    }

    @Override
    public HFManagerDto get(Long id) {
        HFManager entity = this.repository.findOne(id);
        HFManagerDto firmDto = this.converter.disassemble(entity);

        // load funds
        firmDto.setFunds(this.fundService.loadManagerFunds(id));

        return firmDto;
    }

    @Override
    public List<HFManagerDto> findAll(){
        return converter.disassembleList(repository.findAllByOrderByNameDesc());
    }

    @Override
    public HedgeFundManagerPagedSearchResult findByName(HedgeFundSearchParams searchParams) {
        int page = searchParams != null && searchParams.getPage() > 0 ? searchParams.getPage() - 1 : 0;
        int pageSize = searchParams != null && searchParams.getPageSize() > 0 ? searchParams.getPageSize() : DEFAULT_PAGE_SIZE;
        Page<HFManager> entityPage = this.repository.findByName(searchParams.getName(),
                //new PageRequest(searchParams.getPage(), searchParams.getPageSize(), new Sort(Sort.Direction.DESC, "id")));
                new PageRequest(page, pageSize, new Sort(Sort.Direction.DESC, "id")));

        HedgeFundManagerPagedSearchResult result = new HedgeFundManagerPagedSearchResult();
        if(entityPage != null) {
            result.setTotalElements(entityPage.getTotalElements());
            if(entityPage.getTotalElements() > 0) {
                result.setShowPageFrom(PaginationUtils.getShowPageFrom(DEFAULT_PAGES_PER_VIEW, page + 1));
                result.setShowPageTo(PaginationUtils.getShowPageTo(DEFAULT_PAGES_PER_VIEW,
                        page + 1, result.getShowPageFrom(), entityPage.getTotalPages()));
            }
            result.setTotalPages(entityPage.getTotalPages());
            result.setCurrentPage(page + 1);
            if(searchParams != null) {
                result.setSearchParams(searchParams.getSearchParamsAsString());
            }
            result.setManagers(this.converter.disassembleList(entityPage.getContent()));
        }
        return result;
    }
}
