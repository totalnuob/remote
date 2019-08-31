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
import {ROLE_USER_PROFILE_EDIT} from "./roles.constants";
import {ROLE_MONITORING_EDIT} from "./roles.constants";
import {ROLE_LOOKUPS_EDIT} from "./roles.constants";
import {ROLE_M2S2_EDIT} from "./roles.constants";


@Injectable()
export class ModuleAccessCheckerService extends CommonService{

    roles;

    constructor(){
        super();

        // TODO: refactor string
        var rolesText = localStorage.getItem("authenticatedUserRoles");
        this.roles = JSON.parse(rolesText);
    }

    public checkAccessInvest(){
        return this.checkAccess("ROLE_INVEST");
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
        return this.checkAccess("ROLE_MM");
    }

    //public checkAccessCorpMeetingsEditor(){
    //    return this.checkAccess(ROLE_CORPMEETINGS_EDIT);
    //}


    public checkAccessCorpMeetingsView(){
        return this.checkAccessICMember() || this.checkAccess(ROLE_CORPMEETINGS_EDIT);
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


    /**
     * Checks if current user roles (from localStorage) contain specified role or admin role.
     */
    private checkAccess(role){
        //
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