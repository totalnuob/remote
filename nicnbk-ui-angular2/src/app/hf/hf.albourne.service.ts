import { Injectable } from '@angular/core';
import { Http, Response, Headers, RequestOptions } from '@angular/http';
import { Observable }     from 'rxjs/Observable';

import {CommonService} from "../common/common.service";

@Injectable()
export class AlbourneService extends CommonService{

    constructor ()
    {
        super();
    }


    getIDDAnalysisAssessmentLookup(){
        return {
            'IDD_AA': [
                {value: 'A', color: '#13c813', text: 'Amongst best in strategy'},
                {value: 'B', color: '#94fc94', text: 'Above average'},
                {value: 'C', color: 'yellow', text: 'Average'},
                {value: 'D', color: 'orange', text: 'Below average'},
                {value: 'E', color: 'red', text: 'Serious Investment Risk or Fund in liquidation'},
                {value: 'IA', color: 'grey', text: 'Insufficient Access'},
                {value: 'NR', color: 'grey', text: 'Not Rated currently'}
            ],
            'CONVICTION': [
                {value: '0', color: 'white', text: 'Decayed'},
                {value: '1', color: 'white', text: 'Minimal'},
                {value: '2', color: 'white', text: 'Limited'},
                {value: '3', color: 'white', text: 'Moderate'},
                {value: '4', color: 'white', text: 'High'},
                {value: '5', color: 'white', text: 'Highest'}
            ],
            'EXPECTED_ALPHA': [
                {value: 'H', color: '#13c813', text: 'Higher than average versus strategy peers'},
                {value: 'M', color: 'yellow', text: 'In line with strategy peers'},
                {value: 'L', color: 'red', text: 'Lower than average versus strategy peers'}

            ],
            'EXPECTED_BETA': [
                {value: 'H',color: 'red', text: 'High beta to style versus strategy peers'},
                {value: 'M',color: 'orange', text: 'Average to style versus strategy peers'},
                {value: 'L',color: 'yellow', text: 'Low beta to style versus strategy peers'},
                {value: 'N',color: '#13c813', text: 'Consistently negative beta to style versus strategy peers'},
                {value: 'V',color: '#94fc94', text: 'Occasional positive and negative beta to style versus strategy peers'}
            ],
            'EXPECTED_RISK': [
                {value: 'H',color: 'red', text: 'Higher than average versus strategy peers'},
                {value: 'M',color: 'yellow', text: 'In line with strategy peers'},
                {value: 'L',color: '#13c813', text: 'Lower than average versus strategy peers'}
            ],
            'STRATEGY_INVESTMENT': [
                {value: 'A',color: '#13c813', text: 'Amongst best in strategy'},
                {value: 'B',color: '#94fc94', text: 'Above average versus strategy peers'},
                {value: 'C',color: 'yellow', text: 'Average for the strategy'},
                {value: 'D',color: 'orange', text: 'Below average versus strategy peers'},
                {value: 'E',color: 'red', text: 'Well below average versus strategy peers'}
            ],
            'MNG_TEAM': [
                {value: 'A', color: '#13c813', text: 'Amongst best in strategy'},
                {value: 'B', color: '#94fc94', text: 'Above average'},
                {value: 'C', color: 'yellow', text: 'Average'},
                {value: 'D', color: 'orange', text: 'Below average'},
                {value: 'E', color: 'red', text: 'Serious Investment Risk or Fund in liquidation'},
                {value: 'IA', color: 'grey', text: 'Insufficient Access'},
                {value: 'NR', color: 'grey', text: 'Not Rated currently'}
            ],
            'RISK_PROCESS': [
                {value: 'A', color: '#13c813', text: 'Amongst best in strategy'},
                {value: 'B', color: '#94fc94', text: 'Above average'},
                {value: 'C', color: 'yellow', text: 'Average'},
                {value: 'D', color: 'orange', text: 'Below average'},
                {value: 'E', color: 'red', text: 'Serious Investment Risk or Fund in liquidation'},
                {value: 'IA', color: 'grey', text: 'Insufficient Access'},
                {value: 'NR', color: 'grey', text: 'Not Rated currently'}
            ],
        };
    }



    getDescriptionByValue(type, value){
        if(type === 'IDD_AA' || type === 'MNG_TEAM' || type === 'RISK_PROCESS'){
            return this.getIDDAnalysisAssessment(value);
        }else if(type === 'CONVICTION'){
            return this.getConviction(value);
        }else if(type === 'EXPECTED_ALPHA'){
            return this.getExpectedAlpha(value);
        }else if(type === 'EXPECTED_BETA'){
            return this.getExpectedBeta(value);
        }else if(type === 'EXPECTED_RISK'){
            return this.getExpectedRisk(value);
        }else if(type === 'STRATEGY_INVESTMENT'){
            return this.getStrategyInvestment(value);
        }else {

        }
    }

    private getIDDAnalysisAssessment(value){
        if(value === 'A'){
            return {color: '#13c813', text: 'Amongst best in strategy'};
        }else if(value === 'B'){
            return {color: '#94fc94', text: 'Above average'};
        }else if(value === 'C'){
            return {color: 'yellow', text: 'Average'};
        }else if(value === 'D'){
            return {color: 'orange', text: 'Below average'};
        }else if(value === 'E'){
            return {color: 'red', text: 'Serious Investment Risk or Fund in liquidation'};
        }else if(value === 'IA' || value === 'NR'){
            return {color: 'grey', text: 'Insufficient Access'};
        }else if(value === 'NR'){
            return {color: 'grey', text: 'Not Rated currently'};
        }else{
            return {color: 'white', text: ''};
        }
    }

    private getConviction(value){
        if(value === '1'){
            return {color: 'white', text: 'Minimal'};
        }else if( value === '2'){
            return {color: 'white', text: 'Limited'};
        }else if( value === '3'){
            return {color: 'white', text: 'Moderate'};
        }else if( value === '4'){
            return {color: 'white', text: 'High'};
        }else if( value === '5'){
            return {color: 'white', text: 'Highest'};
        }else if( value === '0'){
            return {color: 'white', text: 'Decayed'};
        }
    }

    private getExpectedAlpha(value){
        if(value === 'H'){
            return {color: '#13c813', text: 'Higher than average versus strategy peers'};
        }else if(value === 'M'){
            return {color: 'yellow', text: 'In line with strategy peers'};
        }else if(value === 'L') {
            return {color: 'red', text: 'Lower than average versus strategy peers'};
        }else{
            return {color: 'white', text: ''};
        }
    }

    private getExpectedBeta(value){
        if(value === 'H'){
            return {color: 'red', text: 'High beta to style versus strategy peers'};
        }else if(value === 'M'){
            return {color: 'orange', text: 'Average to style versus strategy peers'};
        }else if(value === 'L') {
            return {color: 'yellow', text: 'Low beta to style versus strategy peers'};
        }else if(value === 'N'){
            return {color: '#13c813', text: 'Consistently negative beta to style versus strategy peers'};
        }else if(value === 'V') {
            return {color: '#94fc94', text: 'Occasional positive and negative beta to style versus strategy peers'};
        }else{
            return {color: 'white', text: ''};
        }
    }

    private getExpectedRisk(value){
        if(value === 'H'){
            return {color: 'red', text: 'Higher than average versus strategy peers'};
        }else if(value === 'M'){
            return {color: 'yellow', text: 'In line with strategy peers'};
        }else if(value === 'L') {
            return {color: '#13c813', text: 'Lower than average versus strategy peers'};
        }else{
            return {color: 'white', text: ''};
        }
    }

    private getStrategyInvestment(value){
        if(value === 'A'){
            return {color: '#13c813', text: 'Amongst best in strategy'};
        }else if(value === 'B'){
            return {color: '#94fc94', text: 'Above average versus strategy peers'};
        }else if(value === 'C'){
            return {color: 'yellow', text: 'Average for the strategy'};
        }else if(value === 'D'){
            return {color: 'orange', text: 'Below average versus strategy peers'};
        }else if(value === 'E'){
            return {color: 'red', text: 'Well below average versus strategy peers'};
        }else{
            return {color: 'white', text: ''};
        }
    }


}