package kz.nicnbk.service.impl.m2s2;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import kz.nicnbk.repo.api.m2s2.MeetingMemoRepository;
import kz.nicnbk.repo.model.lookup.MeetingTypeLookup;
import kz.nicnbk.repo.model.m2s2.PrivateEquityMeetingMemo;
import kz.nicnbk.service.CommonTest;
import kz.nicnbk.service.api.m2s2.MeetingMemoService;
import kz.nicnbk.service.dto.m2s2.MemoPagedSearchResult;
import kz.nicnbk.service.dto.m2s2.MemoSearchParamsExtended;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

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
        MemoPagedSearchResult result = service.search(null, null);
        assert (result.getMemos().size() == 5);
    }

    @Test
    @DatabaseSetup("classpath:datasets/m2s2/meeting_memo_search.xml")
    public void testSearchByType(){
        MemoSearchParamsExtended searchParams = new MemoSearchParamsExtended();
        //searchParams.setMemoType(MemoTypeLookup.PRIVATE_EQUITY.getCode());
        searchParams.setMemoType(PrivateEquityMeetingMemo.PE_DISCRIMINATOR);

        searchParams.setMeetingType(MeetingTypeLookup.MEETING.getCode());
        MemoPagedSearchResult result = service.search(searchParams, null);
        assert (result.getMemos().size() == 2);
    }
}
