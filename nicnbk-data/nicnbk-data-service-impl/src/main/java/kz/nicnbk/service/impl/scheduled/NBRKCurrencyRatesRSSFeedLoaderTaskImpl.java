package kz.nicnbk.service.impl.scheduled;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.repo.model.lookup.CurrencyLookup;
import kz.nicnbk.service.api.common.CurrencyRatesService;
import kz.nicnbk.service.dto.common.EntitySaveResponseDto;
import kz.nicnbk.service.dto.common.ResponseStatusType;
import kz.nicnbk.service.dto.lookup.CurrencyRatesDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Date;

/**
 * Created by magzumov on 04.09.2018.
 */

@Component
public class NBRKCurrencyRatesRSSFeedLoaderTaskImpl {

    private static final Logger logger = LoggerFactory.getLogger(NBRKCurrencyRatesRSSFeedLoaderTaskImpl.class);
    public static final String JOBNAME = "SYSTEM_USER";

    @Autowired
    private CurrencyRatesService currencyRatesService;

    @Scheduled(cron="0 0 7 * * *") // Every day at 7 am
    public void loadUSDCurrencyRates() {
        try {
            URL feedUrl = new URL("http://www.nationalbank.kz/rss/rates_all.xml");

            System.setProperty("http.proxyHost", "10.10.163.253");
            System.setProperty("http.proxyPort", "8080");

            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(feedUrl));

            for(SyndEntry entry: feed.getEntries()){
                if(entry.getTitle() != null && entry.getTitle().equalsIgnoreCase(CurrencyLookup.USD.getCode()) && entry.getDescription() != null &&
                        entry.getDescription().getValue() != null) {
                    CurrencyRatesDto currencyRatesDto = new CurrencyRatesDto();
                    // TODO: change implementation, to get <pubDate></pubDate>
                    currencyRatesDto.setDate(new Date());
                    try{
                        Double value = Double.parseDouble(entry.getDescription().getValue());
                        currencyRatesDto.setValue(value);
                        currencyRatesDto.setCurrency(new BaseDictionaryDto(CurrencyLookup.USD.getCode(), null, null, null));

                        EntitySaveResponseDto saveResponseDto = this.currencyRatesService.save(currencyRatesDto, JOBNAME);
                        if(saveResponseDto.getStatus().getCode().equalsIgnoreCase(ResponseStatusType.SUCCESS.getCode())){
                            logger.info("Successfully saved NBRK Currency rate USD (from RSS Feed): " + currencyRatesDto.getValue());
                        }else{
                            logger.error("Error saving parsed NBRK Currency rate USD (from RSS Feed)");
                        }
                    }catch (NumberFormatException ex){
                        logger.error("Error parsing NBRK Currency rate RSS Feed double value: " + entry.getDescription().getValue());
                    }

                }
            }

            System.setProperty("http.proxyHost", "");
            System.setProperty("http.proxyPort", "");

        }
        catch (Exception ex) {
            logger.error("Error parsing NBRK Currency rate RSS Feed (with exception)", ex);
        }
    }
}
