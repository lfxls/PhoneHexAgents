package com.common.powertech.bussiness;

/**
 * Created by yeqw on 2015/10/15.
 * 日结查询返回结构类
 */
public class BillDaily_Class {

    private String TOF_DATE, TOF_NO, TOF_OPR, TOF_AMT, FTA_STATUS, FTA_DEAL_OPR, FTA_DEAL_DATE, JSON;


    public void Set_TOF_DATE(String str){
        TOF_DATE = str;
    }
    public void Set_TOF_NO(String str){
        TOF_NO = str;
    }
    public void Set_TOF_OPR(String str){
        TOF_OPR = str;
    }
    public void Set_TOF_AMT(String str){
        TOF_AMT = str;
    }
    public void Set_FTA_STATUS(String str){
        FTA_STATUS = str;
    }
    public void Set_FTA_DEAL_OPR(String str){
        FTA_DEAL_OPR = str;
    }
    public void Set_FTA_DEAL_DATE(String str){
        FTA_DEAL_DATE = str;
    }
    public void Set_JSON(String str){
        JSON = str;
    }


    public String Get_TOF_DATE(){
       return TOF_DATE;
    }
    public String Get_TOF_NO(){
        return TOF_NO;
    }
    public String Get_TOF_OPR(){
        return TOF_OPR;
    }
    public String Get_TOF_AMT(){
        return TOF_AMT;
    }
    public String Get_FTA_STATUS(){
        return FTA_STATUS;
    }
    public String Get_FTA_DEAL_OPR(){
        return FTA_DEAL_OPR;
    }
    public String Get_FTA_DEAL_DATE(){
        return FTA_DEAL_DATE;
    }
    public String Get_JSON(){
        return JSON;
    }


}
