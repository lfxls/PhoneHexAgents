package com.common.powertech.bussiness;

/**
 * Created by yeqw on 2015/10/15.
 */
public class BillIncome_Class {

    private String DATETIME,PRDORDNO, TXNTYP, OUT, IN, BANLANCE;

    public void Set_DATETIME(String str){
        DATETIME = str;
    }

    public void Set_PRDORDNO(String str){
        PRDORDNO = str;
    }

    public void Set_TXNTYP(String str){
        TXNTYP = str;
    }

    public void Set_OUT(String str){
        OUT = str;
    }

    public void Set_IN(String str){
        IN = str;
    }

    public void Set_BANLANCE(String str){
        BANLANCE = str;
    }


    public String Get_DATETIME(){
       return DATETIME;
    }

    public String Get_PRDORDNO(){
        return PRDORDNO;
    }

    public String Get_TXNTYP(){
        return TXNTYP;
    }

    public String Get_OUT(){
        return OUT;
    }

    public String Get_IN(){
        return IN;
    }

    public String Get_BANLANCE(){
        return BANLANCE;
    }
    
}
