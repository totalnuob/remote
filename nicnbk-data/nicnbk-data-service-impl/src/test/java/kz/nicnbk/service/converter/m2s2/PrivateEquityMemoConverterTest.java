package kz.nicnbk.service.converter.m2s2;

import kz.nicnbk.repo.model.common.Currency;
import kz.nicnbk.repo.model.m2s2.PrivateEquityMeetingMemo;
import kz.nicnbk.service.config.ServiceBeanConfiguration;
import kz.nicnbk.service.dto.m2s2.PrivateEquityMeetingMemoDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

/**
 * Created by magzumov on 11.07.2016.
 */

@SuppressWarnings("SpringJavaAutowiringInspection")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {ServiceBeanConfiguration.class})
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class})
public class PrivateEquityMemoConverterTest {

    @Autowired
    private PEMeetingMemoEntityConverter converter;

    @Test
    public void testPEMeetingMemoEntityAssemble(){
        PrivateEquityMeetingMemoDto dto = getTestDto();
        PrivateEquityMeetingMemo entity = converter.assemble(dto);
        assert (checkEquals(entity, dto));
    }


    @Test
    public void testPEMeetingMemoEntityDisassemble(){
        PrivateEquityMeetingMemo entity = getTestEntity();
        PrivateEquityMeetingMemoDto dto = converter.disassemble(entity);
        assert (checkEquals(entity, dto));
    }

    private PrivateEquityMeetingMemoDto getTestDto() {
        PrivateEquityMeetingMemoDto dto = new PrivateEquityMeetingMemoDto();
        dto.setId(1112L);
        dto.setCurrentlyFundRaising(true);
        dto.setFundName("Test fund name");
        dto.setFundSizeCurrency(Currency.USD);
        dto.setFundSize(100200d);
        dto.setConviction((short)3);
        return dto;
    }

    private PrivateEquityMeetingMemo getTestEntity() {
        PrivateEquityMeetingMemo entity = new PrivateEquityMeetingMemo();
        entity.setId(1113L);
        entity.setFundName("Test fund name");
        entity.setCurrentlyFundRaising(true);
        Currency currency = new Currency();
        currency.setCode(Currency.USD);
        currency.setId(1);
        entity.setFundSizeCurrency(currency);
        entity.setFundSize(100300d);
        entity.setConviction((short)4);
        return entity;
    }

    private boolean checkEquals(PrivateEquityMeetingMemo entity, PrivateEquityMeetingMemoDto dto) {
        return entity.getId() == dto.getId() &&
                entity.getFundName().equals(dto.getFundName()) &&
                entity.getCurrentlyFundRaising().booleanValue() == dto.getCurrentlyFundRaising().booleanValue() &&
                entity.getFundSizeCurrency().getCode().equals(dto.getFundSizeCurrency()) &&
                entity.getFundSize().doubleValue() == dto.getFundSize().doubleValue() &&
                entity.getConviction().shortValue() == dto.getConviction().shortValue();
    }

}
