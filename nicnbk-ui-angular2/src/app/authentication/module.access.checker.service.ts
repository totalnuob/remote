import { Injectable } from '@angular/core';
import {CommonService} from "../common/common.service";
import {ROLE_ADMIN} from "./roles.constants";
import {ROLE_NEWS_EDIT} from "./roles.constants";
import {ROLE_RE_EDIT} from "./roles.constants";
import {ROLE_HF_EDIT} from "./roles.constants";
import {ROLE_PE_EDIT} from "./roles.constants";
import {ROLE_MM_EDIT} from "./roles.constants";
import {ROLE_REPORTING_EDIT} from "./roles.constants";
import {ROLE_CORPMEETINGS_EDIT} from "./roles.constants";
import {ROLE_STRATEGY_RISKS_EDIT} from "./roles.constants";
import {ROLE_IC_MEMBER} from "./roles.constants";
import {ROLE_IC_ADMIN} from "./roles.constants";
import {ROLE_USER_PROFILE_EDIT} from "./roles.constants";
import {ROLE_MONITORING_EDIT} from "./roles.constants";
import {ROLE_LOOKUPS_EDIT} from "./roles.constants";
import {ROLE_M2S2_EDIT} from "./roles.constants";
import {ROLE_HR_DOCS_EDIT} from "./roles.constants";
import {ROLE_HR_EDIT} from "./roles.constants";
import {ROLE_LEGAL_EDIT} from "./roles.constants";
import {ROLE_STRATEGY_EDIT} from "./roles.constants";
import {ROLE_RISKS_EDIT} from "./roles.constants";

import {ROLE_IC_EDIT} from "./roles.constants";
import {ROLE_IC_VIEW} from "./roles.constants";
import {ROLE_IC_TOPIC_EDIT} from "./roles.constants";
import {ROLE_IC_TOPIC_VIEW} from "./roles.constants";
import {ROLE_IC_TOPIC_VIEW_ALL} from "./roles.constants";
import {ROLE_IC_TOPIC_RESTR} from "./roles.constants";



@Injectable()
export class ModuleAccessCheckerService extends CommonService{

    roles;

    constructor(){
        super();
        var rolesText = localStorage.getItem("authenticatedUserRoles");
        this.roles = JSON.parse(rolesText);
    }

    public checkAccessInvest(){
        return this.checkAccess("ROLE_INVEST");
    }

    public checkAccessHREditor(){
        return this.checkAccess(ROLE_HR_EDIT);
    }

    public checkAccessLegalEditor(){
        return this.checkAccess(ROLE_LEGAL_EDIT);
    }

    public checkAccessEmployeeProfileEditor(){
        return this.checkAccess(ROLE_USER_PROFILE_EDIT);
    }

    public checkAccessMemoRestricted(){
        return this.checkAccess("ROLE_MEMO_RESTRICTED");
    }

    public checkAccessPrivateEquity(){
        return this.checkAccess("ROLE_PRIVATE_EQUITY");
    }

    public checkAccessM2S2(){
        return this.checkAccess("ROLE_M2S2");
    }

    public checkAccessM2S2Editor(){
        return this.checkAccess(ROLE_M2S2_EDIT);
    }

    public checkAccessPrivateEquityEditor(){
        return this.checkAccess(ROLE_PE_EDIT);
    }

    public checkAccessHedgeFunds(){
        return this.checkAccess("ROLE_HEDGE_FUND");
    }

    public checkAccessHedgeFundsEditor(){
        return this.checkAccess(ROLE_HF_EDIT);
    }

    public checkAccessRealEstate(){
        return this.checkAccess("ROLE_REAL_ESTATE");
    }

    public checkAccessRealEstateEditor(){
        return this.checkAccess(ROLE_RE_EDIT);
    }

    public checkAccessInfrastructureEditor(){
            return this.checkAccess(ROLE_RE_EDIT);
        }

    public checkAccessNews(){
        return this.checkAccess("ROLE_NEWS");
    }

    public checkAccessNewsEdit(){
        return this.checkAccess(ROLE_NEWS_EDIT);
    }

    public checkAccessReporting(){
        return this.checkAccess("ROLE_REPORTING");
    }

    public checkAccessReportingEditor(){
        return this.checkAccess(ROLE_REPORTING_EDIT);
    }

    public checkAccessMonitoringEditor(){
        return this.checkAccess(ROLE_MONITORING_EDIT);
    }

    public checkAccessMonitoring(){
        return this.checkAccess("ROLE_MONITORING");
    }

    public checkAccessLookupsEditor(){
        return this.checkAccess(ROLE_LOOKUPS_EDIT);
    }

    public checkAccessLookups(){
        return this.checkAccess("ROLE_LOOKUPS");
    }

    public checkAccessAdmin(){
        return this.checkAccess(ROLE_ADMIN);
    }

    public checkAccessMacroMonitorEditor(){
        return this.checkAccess(ROLE_MM_EDIT);
    }

    public checkAccessMacroMonitor(){
        return this.checkAccess("ROLE_MACROMONITOR");
    }

    //public checkAccessCorpMeetingsEditor(){
    //    return this.checkAccess(ROLE_CORPMEETINGS_EDIT);
    //}


    public checkAccessCorpMeetingsView(){
        return this.checkAccess(ROLE_IC_EDIT) || this.checkAccess(ROLE_IC_VIEW) || this.checkAccess(ROLE_IC_TOPIC_EDIT)
        || this.checkAccess(ROLE_IC_TOPIC_VIEW) || this.checkAccess(ROLE_IC_TOPIC_RESTR) || this.checkAccess(ROLE_IC_MEMBER)
         || this.checkAccess(ROLE_IC_ADMIN);
    }

    public checkAccessICMeetingsView(){
        return this.checkAccess(ROLE_IC_EDIT) || this.checkAccess(ROLE_IC_VIEW) || this.checkAccess(ROLE_IC_MEMBER)
            || this.checkAccess(ROLE_IC_ADMIN);
    }

    public checkAccessICMeetingsEdit(){
        return this.checkAccess(ROLE_IC_EDIT) || this.checkAccess(ROLE_IC_ADMIN);
    }

    public checkAccessICMeetingTopicsView(){
        return this.checkAccess(ROLE_IC_TOPIC_EDIT) || this.checkAccess(ROLE_IC_TOPIC_VIEW_ALL)
            || this.checkAccess(ROLE_IC_TOPIC_VIEW) || this.checkAccess(ROLE_IC_TOPIC_RESTR)
            || this.checkAccess(ROLE_IC_MEMBER) || this.checkAccess(ROLE_IC_ADMIN);
    }

    public checkAccessICMeetingTopicsViewFull(){
        return this.checkAccess(ROLE_IC_TOPIC_EDIT) || this.checkAccess(ROLE_IC_TOPIC_VIEW_ALL)
            || this.checkAccess(ROLE_IC_MEMBER) || this.checkAccess(ROLE_IC_ADMIN);
    }

    public checkAccessICMeetingAdmin(){
        return this.checkAccess(ROLE_IC_ADMIN);
    }

    public checkAccessICMeetingTopicsEdit(departmentId){
        if(this.checkAccessAdmin() || this.checkAccess(ROLE_IC_ADMIN)){
            return true;
        }
        var access = this.checkAccess(ROLE_IC_TOPIC_EDIT);
        var userPosition = JSON.parse(localStorage.getItem("authenticatedUserPosition"));
        var departmentOk = userPosition != null && userPosition.department != null && departmentId != null && userPosition.department.id == departmentId;
        return access && departmentOk;
    }

    public checkAccessCorpMeetingsEdit(){
        return this.checkAccess(ROLE_CORPMEETINGS_EDIT);
    }

    public checkAccessICMember(){
        return this.checkAccess(ROLE_IC_MEMBER);
    }

    public checkAccessStrategyRisksEditor(){
        return this.checkAccess(ROLE_STRATEGY_RISKS_EDIT);
    }

    public checkAccessStrategyEditor(){
        return this.checkAccess(ROLE_STRATEGY_EDIT);
    }
    public checkAccessRisksEditor(){
        return this.checkAccess(ROLE_RISKS_EDIT);
    }




    /**
     * Checks if current user roles (from localStorage) contain specified role or admin role.
     */
    private checkAccess(role){
        //TODO: this func gets called multiple times !!! try console.log("checkAccessPrivateEquity");
        if(this.roles != null && this.roles.length > 0){
            for(var i = 0; i < this.roles.length; i++){
                if((this.roles[i].indexOf(role) != -1) || this.roles[i] === ROLE_ADMIN){
                    return true;
                }
            }
        }
        return false;
    }
}