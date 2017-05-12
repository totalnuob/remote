import { Injectable } from '@angular/core';
import {CommonService} from "../common/common.service";
import {ROLE_ADMIN} from "./roles.constants";
import {ROLE_NEWS_EDIT} from "./roles.constants";
import {ROLE_RE_EDIT} from "./roles.constants";
import {ROLE_HF_EDIT} from "./roles.constants";
import {ROLE_PE_EDIT} from "./roles.constants";
import {ROLE_MM_EDIT} from "./roles.constants";

export class ModuleAccessCheckerService extends CommonService{

    roles;

    constructor(){
        super();

        // TODO: refactor string
        var rolesText = localStorage.getItem("authenticatedUserRoles");
        this.roles = JSON.parse(rolesText);
    }

    public checkAccessPrivateEquity(){
        return this.checkAccess("ROLE_PRIVATE_EQUITY");
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

    public checkAccessNewsEdit(){
        return this.checkAccess(ROLE_NEWS_EDIT);
    }

    public checkAccessAdmin(){
        return this.checkAccess(ROLE_ADMIN);
    }

    public checkAccessMacroMonitor(){
        return this.checkAccess(ROLE_MM_EDIT);
    }


    /**
     * Checks if current user roles (from localStorage) contain specified role or admin role.
     */
    private checkAccess(role){
        //
        //TODO: this func gets called multiple times !!! try console.log("checkAccessPrivateEquity");
        //console.log(this.roles);
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