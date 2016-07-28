package kz.nicnbk.service.impl.m2s2;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import kz.nicnbk.repo.api.m2s2.MeetingMemoRepository;
import kz.nicnbk.repo.model.lookup.MeetingTypeLookup;
import kz.nicnbk.repo.model.m2s2.PrivateEquityMeetingMemo;
import kz.nicnbk.service.CommonTest;
import kz.nicnbk.service.api.m2s2.MeetingMemoService;
import kz.nicnbk.service.dto.m2s2.MemoPagedSearchResult;
import kz.nicnbk.service.dto.m2s2.MemoSearchParams;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

/**
 * Created by magzumov on 19.07.2016.
 */
public class MeetingMemoSearchTest extends CommonTest{

    @Autowired
    private MeetingMemoService service;

    @Autowired
    private MeetingMemoRepository repository;

    @Test
    public void testContext(){
        assert (service != null);
    }

    @Test
    @DatabaseSetup({"classpath:datasets/m2s2/meeting_memo_search.xml"})
    public void testSearchEmpty(){
        System.out.println(repository.findAll(new PageRequest(0, 20)).getTotalElements());
        MemoPagedSearchResult result = service.search(null);
        assert (result.getMemos().size() == 5);
    }

    @Test
    @DatabaseSetup("classpath:datasets/m2s2/meeting_memo_search.xml")
    public void testSearchByType(){
        MemoSearchParams searchParams = new MemoSearchParams();
        //searchParams.setMemoType(MemoTypeLookup.PRIVATE_EQUITY.getCode());
        searchParams.setMemoType(PrivateEquityMeetingMemo.PE_DISCRIMINATOR);

        searchParams.setMeetingType(MeetingTypeLookup.MEETING.getCode());
        MemoPagedSearchResult result = service.search(searchParams);
        assert (result.getMemos().size() == 2);
    }
}
