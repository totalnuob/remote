package kz.nicnbk.service.dto.hf;

import kz.nicnbk.common.service.model.BaseDto;
import kz.nicnbk.common.service.util.DateUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by timur on 27.10.2016.
 */
public class ReturnDto implements BaseDto, Comparable{

    private int year;
    private Double january;
    private Double february;
    private Double march;
    private Double april;
    private Double may;
    private Double june;
    private Double july;
    private Double august;
    private Double september;
    private Double october;
    private Double november;
    private Double december;
    private HedgeFundDto fund;


    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Double getJanuary() {
        return january;
    }

    public void setJanuary(Double january) {
        this.january = january;
    }

    public Double getFebruary() {
        return february;
    }

    public void setFebruary(Double february) {
        this.february = february;
    }

    public Double getMarch() {
        return march;
    }

    public void setMarch(Double march) {
        this.march = march;
    }

    public Double getApril() {
        return april;
    }

    public void setApril(Double april) {
        this.april = april;
    }

    public Double getMay() {
        return may;
    }

    public void setMay(Double may) {
        this.may = may;
    }

    public Double getJune() {
        return june;
    }

    public void setJune(Double june) {
        this.june = june;
    }

    public Double getJuly() {
        return july;
    }

    public void setJuly(Double july) {
        this.july = july;
    }

    public Double getAugust() {
        return august;
    }

    public void setAugust(Double august) {
        this.august = august;
    }

    public Double getSeptember() {
        return september;
    }

    public void setSeptember(Double september) {
        this.september = september;
    }

    public Double getOctober() {
        return october;
    }

    public void setOctober(Double october) {
        this.october = october;
    }

    public Double getNovember() {
        return november;
    }

    public void setNovember(Double november) {
        this.november = november;
    }

    public Double getDecember() {
        return december;
    }

    public void setDecember(Double december) {
        this.december = december;
    }

    public HedgeFundDto getFund() {
        return fund;
    }

    public void setFund(HedgeFundDto fund) {
        this.fund = fund;
    }

    public void setFundId(Long id){
        this.fund = new HedgeFundDto();
        this.fund.setId(id);
    }

    public int getNumberOfPositiveMonths(){
        int count = 0;
        count += this.january != null && this.january > 0 ? 1 : 0;
        count += this.february != null && this.february > 0 ? 1 : 0;
        count += this.march != null && this.march > 0 ? 1 : 0;
        count += this.april != null && this.april > 0 ? 1 : 0;
        count += this.may != null && this.may > 0 ? 1 : 0;
        count += this.june != null && this.june > 0 ? 1 : 0;
        count += this.july != null && this.july > 0 ? 1 : 0;
        count += this.august != null && this.august > 0 ? 1 : 0;
        count += this.september != null && this.september > 0 ? 1 : 0;
        count += this.october != null && this.october > 0 ? 1 : 0;
        count += this.november != null && this.november > 0 ? 1 : 0;
        count += this.december != null && this.december > 0 ? 1 : 0;
        return count;
    }

    public int getNumberOfNegativeMonths(){
        int count = 0;
        count += this.january != null && this.january < 0 ? 1 : 0;
        count += this.february != null && this.february < 0 ? 1 : 0;
        count += this.march != null && this.march < 0 ? 1 : 0;
        count += this.april != null && this.april < 0 ? 1 : 0;
        count += this.may != null && this.may < 0 ? 1 : 0;
        count += this.june != null && this.june < 0 ? 1 : 0;
        count += this.july != null && this.july < 0 ? 1 : 0;
        count += this.august != null && this.august < 0 ? 1 : 0;
        count += this.september != null && this.september < 0 ? 1 : 0;
        count += this.october != null && this.october < 0 ? 1 : 0;
        count += this.november != null && this.november < 0 ? 1 : 0;
        count += this.december != null && this.december < 0 ? 1 : 0;
        return count;
    }

    public int getNumberOfZeroReturnMonths(){
        int count = 0;
        count += this.january != null && this.january == 0 ? 1 : 0;
        count += this.february != null && this.february == 0 ? 1 : 0;
        count += this.march != null && this.march == 0 ? 1 : 0;
        count += this.april != null && this.april == 0 ? 1 : 0;
        count += this.may != null && this.may == 0 ? 1 : 0;
        count += this.june != null && this.june == 0 ? 1 : 0;
        count += this.july != null && this.july == 0 ? 1 : 0;
        count += this.august != null && this.august == 0 ? 1 : 0;
        count += this.september != null && this.september == 0 ? 1 : 0;
        count += this.october != null && this.october == 0 ? 1 : 0;
        count += this.november != null && this.november == 0 ? 1 : 0;
        count += this.december != null && this.december == 0 ? 1 : 0;
        return count;
    }
    
    public BigDecimal getSum(){

        BigDecimal sum = new BigDecimal("0.0");
        sum = sum.add(getJanuary() != null ? new BigDecimal(getJanuary()) : new BigDecimal("0.0"));
        sum = sum.add(getFebruary() != null ? new BigDecimal(getFebruary()) : new BigDecimal("0.0"));
        sum = sum.add(getMarch() != null ? new BigDecimal(getMarch()) : new BigDecimal("0.0"));
        sum = sum.add(getApril() != null ? new BigDecimal(getApril()) : new BigDecimal("0.0"));
        sum = sum.add(getMay() != null ? new BigDecimal(getMay()) : new BigDecimal("0.0"));
        sum = sum.add(getJune() != null ? new BigDecimal(getJune()) : new BigDecimal("0.0"));
        sum = sum.add(getJuly() != null ? new BigDecimal(getJuly()) : new BigDecimal("0.0"));
        sum = sum.add(getAugust() != null ? new BigDecimal(getAugust()) : new BigDecimal("0.0"));
        sum = sum.add(getSeptember() != null ? new BigDecimal(getSeptember()) : new BigDecimal("0.0"));
        sum = sum.add(getOctober() != null ? new BigDecimal(getOctober()) : new BigDecimal("0.0"));
        sum = sum.add(getNovember() != null ? new BigDecimal(getNovember()) : new BigDecimal("0.0"));
        sum = sum.add(getDecember() != null ? new BigDecimal(getDecember()) : new BigDecimal("0.0"));
//        Double sum = 0.0;
//        sum += getJanuary() != null ? getJanuary() : 0.0;
//        sum += getFebruary() != null ? getFebruary() : 0.0;
//        sum += getMarch() != null ? getMarch() : 0.0;
//        sum += getApril() != null ? getApril() : 0.0;
//        sum += getMay() != null ? getMay() : 0.0;
//        sum += getJune() != null ? getJune() : 0.0;
//        sum += getJuly() != null ? getJuly() : 0.0;
//        sum += getAugust() != null ? getAugust() : 0.0;
//        sum += getSeptember() != null ? getSeptember() : 0.0;
//        sum += getOctober() != null ? getOctober() : 0.0;
//        sum += getNovember() != null ? getNovember() : 0.0;
//        sum += getDecember() != null ? getDecember() : 0.0;
        return sum;
    }

    public Double getReturnByMonth(int month){
        switch (month){
            case 1:
                return this.january;
            case 2:
                return this.february;
            case 3:
                return this.march;
            case 4:
                return this.april;
            case 5:
                return this.may;
            case 6:
                return this.june;
            case 7:
                return this.july;
            case 8:
                return this.august;
            case 9:
                return this.september;
            case 10:
                return this.october;
            case 11:
                return this.november;
            case 12:
                return this.december;
        }
        return null;
    }

    public void setReturnByMonth(int month, Double value){
        switch (month){
            case 1:
                this.january = value;
                break;
            case 2:
                this.february = value;
                break;
            case 3:
                this.march = value;
                break;
            case 4:
                this.april = value;
                break;
            case 5:
                this.may = value;
                break;
            case 6:
                this.june = value;
                break;
            case 7:
                this.july = value;
                break;
            case 8:
                this.august = value;
                break;
            case 9:
                this.september = value;
                break;
            case 10:
                this.october = value;
                break;
            case 11:
                this.november = value;
                break;
            case 12:
                this.december = value;
                break;
        }
    }

    public Date getFirstNotNullMonth(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        try {
            if(this.january != null) {
                return simpleDateFormat.parse("31-01-" + this.year);
            }
            if(this.february != null) {
                int day = DateUtils.isLeapYear(this.year) ? 29 : 28;
                return simpleDateFormat.parse(day + "-02-" + this.year);
            }
            if(this.march != null) {
                return simpleDateFormat.parse("31-03-" + this.year);
            }
            if(this.april != null) {
                return simpleDateFormat.parse("30-04-" + this.year);
            }
            if(this.may != null) {
                return simpleDateFormat.parse("31-05-" + this.year);
            }
            if(this.june != null) {
                return simpleDateFormat.parse("30-06-" + this.year);
            }
            if(this.july != null) {
                return simpleDateFormat.parse("31-07-" + this.year);
            }
            if(this.august != null) {
                return simpleDateFormat.parse("31-08-" + this.year);
            }
            if(this.september != null) {
                return simpleDateFormat.parse("30-09-" + this.year);
            }
            if(this.october != null) {
                return simpleDateFormat.parse("31-10-" + this.year);
            }
            if(this.november != null) {
                return simpleDateFormat.parse("30-11-" + this.year);
            }
            if(this.december != null) {
                return simpleDateFormat.parse("31-12-" + this.year);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Date getLastNotNullMonth(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        try {
            if(this.december != null) {
                return simpleDateFormat.parse("31-12-" + this.year);
            }
            if(this.november != null) {
                return simpleDateFormat.parse("30-11-" + this.year);
            }
            if(this.october != null) {
                return simpleDateFormat.parse("31-10-" + this.year);
            }
            if(this.september != null) {
                return simpleDateFormat.parse("30-09-" + this.year);
            }
            if(this.august != null) {
                return simpleDateFormat.parse("31-08-" + this.year);
            }
            if(this.july != null) {
                return simpleDateFormat.parse("31-07-" + this.year);
            }
            if(this.june != null) {
                return simpleDateFormat.parse("30-06-" + this.year);
            }
            if(this.may != null) {
                return simpleDateFormat.parse("31-05-" + this.year);
            }
            if(this.april != null) {
                return simpleDateFormat.parse("30-04-" + this.year);
            }
            if(this.march != null) {
                return simpleDateFormat.parse("31-03-" + this.year);
            }
            if(this.february != null) {
                int day = DateUtils.isLeapYear(this.year) ? 29 : 28;
                return simpleDateFormat.parse(day + "-02-" + this.year);
            }
            if(this.january != null) {
                return simpleDateFormat.parse("31-01-" + this.year);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void scaleReturnsToPercent(){
        if(this.january != null){
            this.january = BigDecimal.valueOf(this.january).multiply(new BigDecimal("100.0")).doubleValue();
        }
        if(this.february != null){
            this.february = BigDecimal.valueOf(this.february).multiply(new BigDecimal("100.0")).doubleValue();
        }
        if(this.march != null){
            this.march = BigDecimal.valueOf(this.march).multiply(new BigDecimal("100.0")).doubleValue();
        }
        if(this.april != null){
            this.april = BigDecimal.valueOf(this.april).multiply(new BigDecimal("100.0")).doubleValue();
        }
        if(this.may != null){
            this.may = BigDecimal.valueOf(this.may).multiply(new BigDecimal("100.0")).doubleValue();
        }
        if(this.june != null){
            this.june = BigDecimal.valueOf(this.june).multiply(new BigDecimal("100.0")).doubleValue();
        }
        if(this.july != null){
            this.july = BigDecimal.valueOf(this.july).multiply(new BigDecimal("100.0")).doubleValue();
        }
        if(this.august != null){
            this.august = BigDecimal.valueOf(this.august).multiply(new BigDecimal("100.0")).doubleValue();
        }
        if(this.september != null){
            this.september = BigDecimal.valueOf(this.september).multiply(new BigDecimal("100.0")).doubleValue();
        }
        if(this.october != null){
            this.october = BigDecimal.valueOf(this.october).multiply(new BigDecimal("100.0")).doubleValue();
        }
        if(this.november != null){
            this.november = BigDecimal.valueOf(this.november).multiply(new BigDecimal("100.0")).doubleValue();
        }
        if(this.december != null){
            this.december = BigDecimal.valueOf(this.december).multiply(new BigDecimal("100.0")).doubleValue();
        }
    }

    public void scaleReturnsToNumber(){
        if(this.january != null){
            this.january = BigDecimal.valueOf(this.january).divide(new BigDecimal("100.0")).doubleValue();
        }
        if(this.february != null){
            this.february = BigDecimal.valueOf(this.february).divide(new BigDecimal("100.0")).doubleValue();
        }
        if(this.march != null){
            this.march = BigDecimal.valueOf(this.march).divide(new BigDecimal("100.0")).doubleValue();
        }
        if(this.april != null){
            this.april = BigDecimal.valueOf(this.april).divide(new BigDecimal("100.0")).doubleValue();
        }
        if(this.may != null){
            this.may = BigDecimal.valueOf(this.may).divide(new BigDecimal("100.0")).doubleValue();
        }
        if(this.june != null){
            this.june = BigDecimal.valueOf(this.june).divide(new BigDecimal("100.0")).doubleValue();
        }
        if(this.july != null){
            this.july = BigDecimal.valueOf(this.july).divide(new BigDecimal("100.0")).doubleValue();
        }
        if(this.august != null){
            this.august = BigDecimal.valueOf(this.august).divide(new BigDecimal("100.0")).doubleValue();
        }
        if(this.september != null){
            this.september = BigDecimal.valueOf(this.september).divide(new BigDecimal("100.0")).doubleValue();
        }
        if(this.october != null){
            this.october = BigDecimal.valueOf(this.october).divide(new BigDecimal("100.0")).doubleValue();
        }
        if(this.november != null){
            this.november = BigDecimal.valueOf(this.november).divide(new BigDecimal("100.0")).doubleValue();
        }
        if(this.december != null){
            this.december = BigDecimal.valueOf(this.december).divide(new BigDecimal("100.0")).doubleValue();
        }
    }

    @Override
    public int compareTo(Object o) {
        if(this.year < ((ReturnDto) o).year){
            return -1;
        }else if(this.year > ((ReturnDto) o).year){
            return 1;
        }
        return 0;
    }
}
