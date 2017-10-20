package com.myDialog.peach;

import java.util.Calendar;

import com.common.powertech.R;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PayInfFragment1 extends Fragment implements TextWatcher {

    private EditText _inputName, _inputCCNumber, _inputCCExpiryMonth, _inputCCExpiryYear, _inputCCCVV;

    private String name;
    private String ccNumber;
    private String expiryMonth;
    private String expiryYear;
    private String cvv;

    private Button mBtn;
    private View _container;
    private TextView imbrand;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        _container = inflater.inflate(R.layout.connect_checkout_insert_cc_payment_data, container, false);
        _inputName = (EditText) _container.findViewById(R.id.connect_checkout_insert_cc_payment_data_name);
        _inputCCNumber = (EditText) _container.findViewById(R.id.connect_checkout_insert_cc_payment_data_ccnumber);
        _inputCCExpiryMonth = (EditText) _container.findViewById(R.id.connect_checkout_insert_cc_payment_data_expiry_month);
        _inputCCExpiryMonth.addTextChangedListener(this);

        _inputCCExpiryYear = (EditText) _container.findViewById(R.id.connect_checkout_insert_cc_payment_data_expiry_year);
        _inputCCExpiryYear.addTextChangedListener(this);

        _inputCCCVV = (EditText) _container.findViewById(R.id.connect_checkout_insert_cc_payment_data_cvv);

        mBtn = (Button) _container.findViewById(R.id.connect_checkout_insert_cc_payment_data_button);
        mBtn.setOnClickListener(onclick);
        imbrand = (TextView) _container.findViewById(R.id.connect_checkout_insert_cc_payment_data_payment_method_icon);
        imbrand.setText(((PeachPayActivity) getParentFragment()).getPay_brand());
        return _container;
    }

    private OnClickListener onclick = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            //获取信息
//			connect_checkout_insert_cc_payment_data_ccnumber
            name = _inputName.getText().toString().trim();
//			connect_checkout_insert_cc_payment_data_expiry
            ccNumber = _inputCCNumber.getText().toString().trim();
//			connect_checkout_insert_cc_payment_data_expiry_month
            expiryMonth = _inputCCExpiryMonth.getText().toString().trim();
//			connect_checkout_insert_cc_payment_data_expiry_year
            expiryYear = _inputCCExpiryYear.getText().toString().trim();
//			connect_checkout_insert_cc_payment_data_cvv
            cvv = _inputCCCVV.getText().toString().trim();
            //检查信息格式
            if (name.length() < 1) {
                Toast.makeText(getActivity(), getString(R.string.connect_checkout_noname),
                        Toast.LENGTH_LONG).show();
                return;
            }
            if (ccNumber.length() < 1) {
                Toast.makeText(getActivity(), getString(R.string.connect_checkout_nocard),
                        Toast.LENGTH_LONG).show();
                return;
            }
            if (expiryMonth.length() < 1) {
                Toast.makeText(getActivity(), getString(R.string.connect_checkout_wormonth),
                        Toast.LENGTH_LONG).show();
                return;
            }
            if (expiryYear.length() < 4) {
                Toast.makeText(getActivity(), getString(R.string.connect_checkout_woryear),
                        Toast.LENGTH_LONG).show();
                return;
            }
            if (cvv.length() < 3) {
                Toast.makeText(getActivity(), getString(R.string.connect_checkout_nocvv),
                        Toast.LENGTH_LONG).show();
                return;
            }
            //提交跳转
            Calendar cal = Calendar.getInstance();
            if (new Integer(expiryYear) < cal.get(Calendar.YEAR) && new Integer(expiryMonth) < cal.get(Calendar.MONTH)) {
                Toast.makeText(getActivity(), getString(R.string.connect_checkout_payment_datecheck),
                        Toast.LENGTH_LONG).show();
                return;
            }
            if (getParentFragment() instanceof PeachPayActivity) {
                ((PeachPayActivity) getParentFragment()).paymentCreditCardDataProvided(name, ccNumber, expiryMonth, expiryYear, cvv, true);
            }
//
        }

    };


    @Override
    public void afterTextChanged(Editable editable) {
        View currentFocus = _container.findFocus();
        if (currentFocus == _inputCCExpiryMonth) {
            if (editable.length() == 2) {
                _inputCCExpiryYear.requestFocus();
            }
        } else if (currentFocus == _inputCCExpiryYear) {
            if (editable.length() == 4) {
                _inputCCCVV.requestFocus();
            }
        }
    }

    @Override
    public void onPause() {
        _inputName.setText("");
        _inputCCNumber.setText("");
        _inputCCCVV.setText("");
        _inputCCExpiryMonth.setText("");
        _inputCCExpiryYear.setText("");
        super.onPause();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // TODO Auto-generated method stub
    }
}
