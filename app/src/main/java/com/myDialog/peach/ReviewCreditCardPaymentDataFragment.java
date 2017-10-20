package com.myDialog.peach;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.common.powertech.R;

@SuppressLint("ValidFragment")
public class ReviewCreditCardPaymentDataFragment extends Fragment implements OnClickListener {

    private String _providedCCName;
    private String _providedCCNumberStripped;
    private String _providedCCExpiry;
    private int _providedCCCVVLength;

    private TextView _inputName, _inputCCNumber, _inputCCExpiry, _inputCCCVV;
    private Button _confirmButton;
    private TextView _paymentIcon;

    private CheckBox _storePaymentData;
    private boolean _showStorePaymentData;

    public ReviewCreditCardPaymentDataFragment(String name, String ccNumberStripped, String expiryMonth,
                                               String expiryYear, String ccv, boolean _showStorePaymentData) {
//		_workflowCallback = callback;
//		_selectedPaymenMethod = paymentMethod;

        _providedCCName = name;
        _providedCCNumberStripped = ccNumberStripped;
        _providedCCExpiry = expiryMonth + "/" + expiryYear;
        _providedCCCVVLength = ccv.length();
        this._showStorePaymentData = _showStorePaymentData;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.connect_checkout_review_cc_payment_data, container, false);
        _inputName = (TextView) view.findViewById(R.id.connect_checkout_review_cc_payment_data_name);
        _inputName.setText(_providedCCName);
        _inputCCNumber = (TextView) view.findViewById(R.id.connect_checkout_review_cc_payment_data_ccnumber);
        _inputCCNumber.setText("*-" + _providedCCNumberStripped);
        _inputCCExpiry = (TextView) view.findViewById(R.id.connect_checkout_review_cc_payment_data_expiry);
        _inputCCExpiry.setText(_providedCCExpiry);
        _confirmButton = (Button) view.findViewById(R.id.connect_checkout_review_cc_payment_data_button);
        _confirmButton.setOnClickListener(this);
        _paymentIcon = (TextView) view.findViewById(R.id.connect_checkout_review_cc_payment_data_payment_method_icon);
        _paymentIcon.setText(((PeachPayActivity) getParentFragment()).getPay_brand());
        _inputCCCVV = (TextView) view.findViewById(R.id.connect_checkout_review_cc_payment_data_cvv);
        _storePaymentData = (CheckBox) view.findViewById(R.id.connect_checkout_review_cc_payment_data_store_account_data);

        if (_showStorePaymentData == true) {
            _storePaymentData.setVisibility(View.VISIBLE);
        } else {
            _storePaymentData.setVisibility(View.INVISIBLE);
        }

        String xxxEd = "";
        for (int i = 0; i < _providedCCCVVLength; i++) xxxEd += "x";
        _inputCCCVV.setText(xxxEd);
        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.connect_checkout_review_cc_payment_data_button) {
//			getActivity().paymentDataAccepted(_storePaymentData.isChecked());
            ((PeachPayActivity) getParentFragment()).setRequestPeachPay(_storePaymentData.isChecked());
        }
    }

}
