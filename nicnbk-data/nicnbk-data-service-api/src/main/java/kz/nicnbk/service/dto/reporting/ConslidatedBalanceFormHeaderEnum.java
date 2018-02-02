package kz.nicnbk.service.dto.reporting;

/**
 * Created by magzumov on 25.10.2017.
 */
public enum ConslidatedBalanceFormHeaderEnum {
    ASSETS_1("ASSETS_1", 1);

    ConslidatedBalanceFormHeaderEnum(String name, int number){
        this.name = name;
        this.number = number;
    }

    private String name;
    private int number;

}
