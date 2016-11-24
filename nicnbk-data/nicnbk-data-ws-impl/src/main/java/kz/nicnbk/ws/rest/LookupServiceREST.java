package kz.nicnbk.ws.rest;

import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.service.datamanager.LookupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by magzumov on 04.08.2016.
 */

@RestController
@RequestMapping("/lookup")
public class LookupServiceREST {

    @Autowired
    private LookupService lookupService;

    @RequestMapping(value = "/PEStrategy", method = RequestMethod.GET)
    public List<BaseDictionaryDto> getPEStrategies(){
        List<BaseDictionaryDto> lookups = this.lookupService.getPrivateEquityStrategies();
        return lookups;
    }

    @RequestMapping(value = "/HFStrategy", method = RequestMethod.GET)
    public List<BaseDictionaryDto> getHFStrategies(){
        List<BaseDictionaryDto> lookups = this.lookupService.getHedgeFundsStrategy();
        return lookups;
    }

    @RequestMapping(value = "/HFSubStrategy/{strategy}", method = RequestMethod.GET)
    public List<BaseDictionaryDto> getHFSubStrategies(@PathVariable String strategy){
        List<BaseDictionaryDto> lookups = this.lookupService.getHedgeFundsSubStrategy(strategy);
        return lookups;
    }

    @RequestMapping(value = "/REStrategy", method = RequestMethod.GET)
    public List<BaseDictionaryDto> getREStrategies(){
        List<BaseDictionaryDto> lookups = this.lookupService.getRealEstateStrategies();
        return lookups;
    }

    @RequestMapping(value = "/Geography", method = RequestMethod.GET)
    public List<BaseDictionaryDto> getGeographies(){
        List<BaseDictionaryDto> lookups = this.lookupService.getGeographies();
        return lookups;
    }

    @RequestMapping(value = "/Currency", method = RequestMethod.GET)
    public List<BaseDictionaryDto> getCurrencies(){
        List<BaseDictionaryDto> lookups = this.lookupService.getCurrencies();
        return lookups;
    }
}