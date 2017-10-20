package com.common.powertech.bussiness;

/**
 * Created by yeqw on 2015/10/15.
 * 冲正查询返回的数据结构类
 */
public class BillRecovery_Class {

    private String PRDORDNO, ORDERTIME, ORDAMT, BIZ_TYPE, USER_NO, ELEN_ID, R_STATUS;

    public void Set_PRDORDNO(String str){
        PRDORDNO = str;
    }

    public void Set_ORDERTIME(String str){
        ORDERTIME = str;
    }

    public void Set_ORDAMT(String str){
        ORDAMT = str;
    }

    public void Set_BIZ_TYPE(String str){
        BIZ_TYPE = str;
    }

    public void Set_USER_NO(String str){
        USER_NO = str;
    }

    public void Set_ELEN_ID(String str){
        ELEN_ID = str;
    }

    public void Set_R_STATUS(String str){
        R_STATUS = str;
    }


    public String Get_PRDORDNO(){
       return PRDORDNO;
    }

    public String Get_ORDERTIME(){
        return ORDERTIME;
    }

    public String Get_ORDAMT(){
        return ORDAMT;
    }

    public String Get_BIZ_TYPE(){
        return BIZ_TYPE;
    }

    public String Get_USER_NO(){
        return USER_NO;
    }

    public String Get_ELEN_ID(){
        return ELEN_ID;
    }

    public String Get_R_STATUS(){
        return R_STATUS;
    }

}
