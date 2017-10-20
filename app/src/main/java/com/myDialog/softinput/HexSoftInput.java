package com.myDialog.softinput;

import android.content.Context;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;

import com.common.powertech.R;
import com.common.powertech.util.StringUtil;

/**
 * Created by HEX144 on 2017/10/19.
 */

public class HexSoftInput {
    Context context;
    View view;

    private AutoCompleteTextView inputCond; //操作的输入框
    private Button button1, button2, button3, button4, button5, button6,
            button7, button8, button9, button0, switchtoABC;
    private Button buttonq ,buttonw ,buttone ,buttonr ,buttont ,buttony ,buttonu ,buttoni ,buttono ,buttonp ,
            buttona ,buttons ,buttond ,buttonf ,buttong ,buttonh ,buttonj ,buttonk ,buttonl ,buttonz ,buttonx ,
            buttonc ,buttonv ,buttonb ,buttonn ,buttonm ,switchto123  ;
    private TableLayout input_model123,input_modelABC;
    private LinearLayout buttonBack123,buttonBackABC;

   public HexSoftInput(Context context,View view){
        this.context = context;
        this.view = view;
       initNumberButton();
    }

    public AutoCompleteTextView getInputCond() {
        return inputCond;
    }

    public void setInputCond(AutoCompleteTextView inputCond) {
        this.inputCond = inputCond;
    }
    private void initNumberButton() {

        button1 = (Button) view.findViewById(R.id.button1);
        button2 = (Button) view.findViewById(R.id.button2);
        button3 = (Button) view.findViewById(R.id.button3);
        button4 = (Button) view.findViewById(R.id.button4);
        button5 = (Button) view.findViewById(R.id.button5);
        button6 = (Button) view.findViewById(R.id.button6);
        button7 = (Button) view.findViewById(R.id.button7);
        button8 = (Button) view.findViewById(R.id.button8);
        button9 = (Button) view.findViewById(R.id.button9);
        button0 = (Button) view.findViewById(R.id.button0);
        switchtoABC = (Button) view.findViewById(R.id.switchtoABC);
        buttonBack123 = (LinearLayout) view.findViewById(R.id.buttonBack123);
        button1.setOnClickListener(numberOnclickListener);
        button2.setOnClickListener(numberOnclickListener);
        button3.setOnClickListener(numberOnclickListener);
        button4.setOnClickListener(numberOnclickListener);
        button5.setOnClickListener(numberOnclickListener);
        button6.setOnClickListener(numberOnclickListener);
        button7.setOnClickListener(numberOnclickListener);
        button8.setOnClickListener(numberOnclickListener);
        button9.setOnClickListener(numberOnclickListener);
        button0.setOnClickListener(numberOnclickListener);
        switchtoABC.setOnClickListener(numberOnclickListener);
        buttonBack123.setOnClickListener(numberOnclickListener);

        input_model123 = (TableLayout) view.findViewById(R.id.input_model123);
        input_modelABC = (TableLayout) view.findViewById(R.id.input_modelABC);

        buttonq = (Button) view.findViewById(R.id.buttonq );
        buttonw = (Button) view.findViewById(R.id.buttonw );
        buttone = (Button) view.findViewById(R.id.buttone );
        buttonr = (Button) view.findViewById(R.id.buttonr );
        buttont = (Button) view.findViewById(R.id.buttont );
        buttony = (Button) view.findViewById(R.id.buttony );
        buttonu = (Button) view.findViewById(R.id.buttonu );
        buttoni = (Button) view.findViewById(R.id.buttoni );
        buttono = (Button) view.findViewById(R.id.buttono );
        buttonp = (Button) view.findViewById(R.id.buttonp );
        buttona = (Button) view.findViewById(R.id.buttona );
        buttons = (Button) view.findViewById(R.id.buttons );
        buttond = (Button) view.findViewById(R.id.buttond );
        buttonf = (Button) view.findViewById(R.id.buttonf );
        buttong = (Button) view.findViewById(R.id.buttong );
        buttonh = (Button) view.findViewById(R.id.buttonh );
        buttonj = (Button) view.findViewById(R.id.buttonj );
        buttonk = (Button) view.findViewById(R.id.buttonk );
        buttonl = (Button) view.findViewById(R.id.buttonl );
        buttonz = (Button) view.findViewById(R.id.buttonz );
        buttonx = (Button) view.findViewById(R.id.buttonx );
        buttonc = (Button) view.findViewById(R.id.buttonc );
        buttonv = (Button) view.findViewById(R.id.buttonv );
        buttonb = (Button) view.findViewById(R.id.buttonb );
        buttonn = (Button) view.findViewById(R.id.buttonn );
        buttonm = (Button) view.findViewById(R.id.buttonm );
        switchto123 = (Button) view.findViewById(R.id.switchto123 );
        buttonBackABC = (LinearLayout)view.findViewById(R.id.buttonBackABC );

        buttonq.setOnClickListener(letterOnclickListener);
        buttonw.setOnClickListener(letterOnclickListener);
        buttone.setOnClickListener(letterOnclickListener);
        buttonr.setOnClickListener(letterOnclickListener);
        buttont.setOnClickListener(letterOnclickListener);
        buttony.setOnClickListener(letterOnclickListener);
        buttonu.setOnClickListener(letterOnclickListener);
        buttoni.setOnClickListener(letterOnclickListener);
        buttono.setOnClickListener(letterOnclickListener);
        buttonp.setOnClickListener(letterOnclickListener);
        buttona.setOnClickListener(letterOnclickListener);
        buttons.setOnClickListener(letterOnclickListener);
        buttond.setOnClickListener(letterOnclickListener);
        buttonf.setOnClickListener(letterOnclickListener);
        buttong.setOnClickListener(letterOnclickListener);
        buttonh.setOnClickListener(letterOnclickListener);
        buttonj.setOnClickListener(letterOnclickListener);
        buttonk.setOnClickListener(letterOnclickListener);
        buttonl.setOnClickListener(letterOnclickListener);
        buttonz.setOnClickListener(letterOnclickListener);
        buttonx.setOnClickListener(letterOnclickListener);
        buttonc.setOnClickListener(letterOnclickListener);
        buttonv.setOnClickListener(letterOnclickListener);
        buttonb.setOnClickListener(letterOnclickListener);
        buttonn.setOnClickListener(letterOnclickListener);
        buttonm.setOnClickListener(letterOnclickListener);
        switchto123.setOnClickListener(letterOnclickListener);
        buttonBackABC.setOnClickListener(letterOnclickListener);

    }

    /**
     * 按键监听器
     */
    View.OnClickListener numberOnclickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String temp = StringUtil.convertStringNull(inputCond.getText()
                    .toString());
            String tempPre = temp.substring(0, inputCond.getSelectionEnd());
            String tempLes = temp.substring(inputCond.getSelectionEnd(),
                    temp.length());
            switch (v.getId()) {
                case R.id.button1:
                    if (tempPre.length() < temp.length()) {
                        tempPre += "1";
                        temp = tempPre + tempLes;
                        inputCond.setText(temp);
                        inputCond.setSelection(tempPre.length());
                        return;
                    } else {
                        temp += "1";
                        inputCond.setText(temp);
                        break;
                    }
                case R.id.button2:
                    if (tempPre.length() < temp.length()) {
                        tempPre += "2";
                        temp = tempPre + tempLes;
                        inputCond.setText(temp);
                        inputCond.setSelection(tempPre.length());
                        return;
                    } else {
                        temp += "2";
                        inputCond.setText(temp);
                        break;
                    }
                case R.id.button3:
                    if (tempPre.length() < temp.length()) {
                        tempPre += "3";
                        temp = tempPre + tempLes;
                        inputCond.setText(temp);
                        inputCond.setSelection(tempPre.length());
                        return;
                    } else {
                        temp += "3";
                        inputCond.setText(temp);
                        break;
                    }
                case R.id.button4:
                    if (tempPre.length() < temp.length()) {
                        tempPre += "4";
                        temp = tempPre + tempLes;
                        inputCond.setText(temp);
                        inputCond.setSelection(tempPre.length());
                        return;
                    } else {
                        temp += "4";
                        inputCond.setText(temp);
                        break;
                    }
                case R.id.button5:
                    if (tempPre.length() < temp.length()) {
                        tempPre += "5";
                        temp = tempPre + tempLes;
                        inputCond.setText(temp);
                        inputCond.setSelection(tempPre.length());
                        return;
                    } else {
                        temp += "5";
                        inputCond.setText(temp);
                        break;
                    }
                case R.id.button6:
                    if (tempPre.length() < temp.length()) {
                        tempPre += "6";
                        temp = tempPre + tempLes;
                        inputCond.setText(temp);
                        inputCond.setSelection(tempPre.length());
                        return;
                    } else {
                        temp += "6";
                        inputCond.setText(temp);
                        break;
                    }
                case R.id.button7:
                    if (tempPre.length() < temp.length()) {
                        tempPre += "7";
                        temp = tempPre + tempLes;
                        inputCond.setText(temp);
                        inputCond.setSelection(tempPre.length());
                        return;
                    } else {
                        temp += "7";
                        inputCond.setText(temp);
                        break;
                    }
                case R.id.button8:
                    if (tempPre.length() < temp.length()) {
                        tempPre += "8";
                        temp = tempPre + tempLes;
                        inputCond.setText(temp);
                        inputCond.setSelection(tempPre.length());
                        return;
                    } else {
                        temp += "8";
                        inputCond.setText(temp);
                        break;
                    }
                case R.id.button9:
                    if (tempPre.length() < temp.length()) {
                        tempPre += "9";
                        temp = tempPre + tempLes;
                        inputCond.setText(temp);
                        inputCond.setSelection(tempPre.length());
                        return;
                    } else {
                        temp += "9";
                        inputCond.setText(temp);
                        break;
                    }
                case R.id.button0:
                    if (tempPre.length() < temp.length()) {
                        tempPre += "0";
                        temp = tempPre + tempLes;
                        inputCond.setText(temp);
                        inputCond.setSelection(tempPre.length());
                        return;
                    } else {
                        temp += "0";
                        inputCond.setText(temp);
                        break;
                    }
                case R.id.buttonBack123:
                    if (tempPre.length() > 1) {
                        tempPre = tempPre.substring(0, tempPre.length() - 1);
                    } else {
                        tempPre = "";
                    }
                    inputCond.setText(tempPre + tempLes);
                    if (tempPre.length() <= inputCond.getText().length()) {
                        inputCond.setSelection(tempPre.length());
                    } else {
                        inputCond.setSelection(inputCond.length()); // 设置光标在最后
                    }
                    return;
                // break;
                case R.id.switchtoABC:
                    input_model123.setVisibility(View.GONE);
                    input_modelABC.setVisibility(View.VISIBLE);
                    break;
            }
            inputCond.setSelection(inputCond.length()); // 设置光标在最后
        }
    };


    View.OnClickListener letterOnclickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String temp = StringUtil.convertStringNull(inputCond.getText()
                    .toString());
            String tempPre = temp.substring(0, inputCond.getSelectionEnd());
            String tempLes = temp.substring(inputCond.getSelectionEnd(),
                    temp.length());
            switch (v.getId()) {
                case R.id.buttona:
                    if (tempPre.length() < temp.length()) {
                        tempPre += "A";
                        temp = tempPre + tempLes;
                        inputCond.setText(temp);
                        inputCond.setSelection(tempPre.length());
                        return;
                    } else {
                        temp += "A";
                        inputCond.setText(temp);
                        break;
                    }
                case R.id.buttonb:
                    if (tempPre.length() < temp.length()) {
                        tempPre += "B";
                        temp = tempPre + tempLes;
                        inputCond.setText(temp);
                        inputCond.setSelection(tempPre.length());
                        return;
                    } else {
                        temp += "B";
                        inputCond.setText(temp);
                        break;
                    }
                case R.id.buttonc:
                    if (tempPre.length() < temp.length()) {
                        tempPre += "C";
                        temp = tempPre + tempLes;
                        inputCond.setText(temp);
                        inputCond.setSelection(tempPre.length());
                        return;
                    } else {
                        temp += "C";
                        inputCond.setText(temp);
                        break;
                    }
                case R.id.buttond:
                    if (tempPre.length() < temp.length()) {
                        tempPre += "D";
                        temp = tempPre + tempLes;
                        inputCond.setText(temp);
                        inputCond.setSelection(tempPre.length());
                        return;
                    } else {
                        temp += "D";
                        inputCond.setText(temp);
                        break;
                    }
                case R.id.buttone:
                    if (tempPre.length() < temp.length()) {
                        tempPre += "E";
                        temp = tempPre + tempLes;
                        inputCond.setText(temp);
                        inputCond.setSelection(tempPre.length());
                        return;
                    } else {
                        temp += "E";
                        inputCond.setText(temp);
                        break;
                    }
                case R.id.buttonf:
                    if (tempPre.length() < temp.length()) {
                        tempPre += "F";
                        temp = tempPre + tempLes;
                        inputCond.setText(temp);
                        inputCond.setSelection(tempPre.length());
                        return;
                    } else {
                        temp += "F";
                        inputCond.setText(temp);
                        break;
                    }
                case R.id.buttong:
                    if (tempPre.length() < temp.length()) {
                        tempPre += "G";
                        temp = tempPre + tempLes;
                        inputCond.setText(temp);
                        inputCond.setSelection(tempPre.length());
                        return;
                    } else {
                        temp += "G";
                        inputCond.setText(temp);
                        break;
                    }
                case R.id.buttonh:
                    if (tempPre.length() < temp.length()) {
                        tempPre += "H";
                        temp = tempPre + tempLes;
                        inputCond.setText(temp);
                        inputCond.setSelection(tempPre.length());
                        return;
                    } else {
                        temp += "H";
                        inputCond.setText(temp);
                        break;
                    }
                case R.id.buttoni:
                    if (tempPre.length() < temp.length()) {
                        tempPre += "I";
                        temp = tempPre + tempLes;
                        inputCond.setText(temp);
                        inputCond.setSelection(tempPre.length());
                        return;
                    } else {
                        temp += "I";
                        inputCond.setText(temp);
                        break;
                    }
                case R.id.buttonj:
                    if (tempPre.length() < temp.length()) {
                        tempPre += "J";
                        temp = tempPre + tempLes;
                        inputCond.setText(temp);
                        inputCond.setSelection(tempPre.length());
                        return;
                    } else {
                        temp += "J";
                        inputCond.setText(temp);
                        break;
                    }
                case R.id.buttonk:
                    if (tempPre.length() < temp.length()) {
                        tempPre += "K";
                        temp = tempPre + tempLes;
                        inputCond.setText(temp);
                        inputCond.setSelection(tempPre.length());
                        return;
                    } else {
                        temp += "K";
                        inputCond.setText(temp);
                        break;
                    }
                case R.id.buttonl:
                    if (tempPre.length() < temp.length()) {
                        tempPre += "L";
                        temp = tempPre + tempLes;
                        inputCond.setText(temp);
                        inputCond.setSelection(tempPre.length());
                        return;
                    } else {
                        temp += "L";
                        inputCond.setText(temp);
                        break;
                    }
                case R.id.buttonm:
                    if (tempPre.length() < temp.length()) {
                        tempPre += "M";
                        temp = tempPre + tempLes;
                        inputCond.setText(temp);
                        inputCond.setSelection(tempPre.length());
                        return;
                    } else {
                        temp += "M";
                        inputCond.setText(temp);
                        break;
                    }
                case R.id.buttonn:
                    if (tempPre.length() < temp.length()) {
                        tempPre += "N";
                        temp = tempPre + tempLes;
                        inputCond.setText(temp);
                        inputCond.setSelection(tempPre.length());
                        return;
                    } else {
                        temp += "N";
                        inputCond.setText(temp);
                        break;
                    }
                case R.id.buttono:
                    if (tempPre.length() < temp.length()) {
                        tempPre += "O";
                        temp = tempPre + tempLes;
                        inputCond.setText(temp);
                        inputCond.setSelection(tempPre.length());
                        return;
                    } else {
                        temp += "O";
                        inputCond.setText(temp);
                        break;
                    }
                case R.id.buttonp:
                    if (tempPre.length() < temp.length()) {
                        tempPre += "P";
                        temp = tempPre + tempLes;
                        inputCond.setText(temp);
                        inputCond.setSelection(tempPre.length());
                        return;
                    } else {
                        temp += "P";
                        inputCond.setText(temp);
                        break;
                    }
                case R.id.buttonq:
                    if (tempPre.length() < temp.length()) {
                        tempPre += "Q";
                        temp = tempPre + tempLes;
                        inputCond.setText(temp);
                        inputCond.setSelection(tempPre.length());
                        return;
                    } else {
                        temp += "Q";
                        inputCond.setText(temp);
                        break;
                    }
                case R.id.buttonr:
                    if (tempPre.length() < temp.length()) {
                        tempPre += "R";
                        temp = tempPre + tempLes;
                        inputCond.setText(temp);
                        inputCond.setSelection(tempPre.length());
                        return;
                    } else {
                        temp += "R";
                        inputCond.setText(temp);
                        break;
                    }
                case R.id.buttons:
                    if (tempPre.length() < temp.length()) {
                        tempPre += "S";
                        temp = tempPre + tempLes;
                        inputCond.setText(temp);
                        inputCond.setSelection(tempPre.length());
                        return;
                    } else {
                        temp += "S";
                        inputCond.setText(temp);
                        break;
                    }
                case R.id.buttont:
                    if (tempPre.length() < temp.length()) {
                        tempPre += "T";
                        temp = tempPre + tempLes;
                        inputCond.setText(temp);
                        inputCond.setSelection(tempPre.length());
                        return;
                    } else {
                        temp += "T";
                        inputCond.setText(temp);
                        break;
                    }
                case R.id.buttonu:
                    if (tempPre.length() < temp.length()) {
                        tempPre += "U";
                        temp = tempPre + tempLes;
                        inputCond.setText(temp);
                        inputCond.setSelection(tempPre.length());
                        return;
                    } else {
                        temp += "U";
                        inputCond.setText(temp);
                        break;
                    }
                case R.id.buttonv:
                    if (tempPre.length() < temp.length()) {
                        tempPre += "V";
                        temp = tempPre + tempLes;
                        inputCond.setText(temp);
                        inputCond.setSelection(tempPre.length());
                        return;
                    } else {
                        temp += "V";
                        inputCond.setText(temp);
                        break;
                    }
                case R.id.buttonw:
                    if (tempPre.length() < temp.length()) {
                        tempPre += "W";
                        temp = tempPre + tempLes;
                        inputCond.setText(temp);
                        inputCond.setSelection(tempPre.length());
                        return;
                    } else {
                        temp += "W";
                        inputCond.setText(temp);
                        break;
                    }
                case R.id.buttonx:
                    if (tempPre.length() < temp.length()) {
                        tempPre += "X";
                        temp = tempPre + tempLes;
                        inputCond.setText(temp);
                        inputCond.setSelection(tempPre.length());
                        return;
                    } else {
                        temp += "X";
                        inputCond.setText(temp);
                        break;
                    }
                case R.id.buttony:
                    if (tempPre.length() < temp.length()) {
                        tempPre += "Y";
                        temp = tempPre + tempLes;
                        inputCond.setText(temp);
                        inputCond.setSelection(tempPre.length());
                        return;
                    } else {
                        temp += "Y";
                        inputCond.setText(temp);
                        break;
                    }
                case R.id.buttonz:
                    if (tempPre.length() < temp.length()) {
                        tempPre += "Z";
                        temp = tempPre + tempLes;
                        inputCond.setText(temp);
                        inputCond.setSelection(tempPre.length());
                        return;
                    } else {
                        temp += "Z";
                        inputCond.setText(temp);
                        break;
                    }
                case R.id.buttonBackABC:
                    if (tempPre.length() > 1) {
                        tempPre = tempPre.substring(0, tempPre.length() - 1);
                    } else {
                        tempPre = "";
                    }
                    inputCond.setText(tempPre + tempLes);
                    if (tempPre.length() <= inputCond.getText().length()) {
                        inputCond.setSelection(tempPre.length());
                    } else {
                        inputCond.setSelection(inputCond.length()); // 设置光标在最后
                    }
                    return;
                case R.id.switchto123:
                    input_model123.setVisibility(View.VISIBLE);
                    input_modelABC.setVisibility(View.GONE);
                    break;
            }
            inputCond.setSelection(inputCond.length()); // 设置光标在最后

        }
    };


}
