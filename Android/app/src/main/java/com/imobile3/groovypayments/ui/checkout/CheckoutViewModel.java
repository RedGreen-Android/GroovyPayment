package com.imobile3.groovypayments.ui.checkout;

import android.content.Context;

import com.imobile3.groovypayments.concurrent.GroovyExecutors;
import com.imobile3.groovypayments.data.PaymentTypeRepository;
import com.imobile3.groovypayments.data.model.PaymentType;
import com.imobile3.groovypayments.rules.CurrencyRules;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * The ViewModel serves as an async bridge between the View (Activity, Fragment)
 * and our backing data repository (Database).
 */
public class CheckoutViewModel extends ViewModel {

    private PaymentTypeRepository mRepository;

    CheckoutViewModel(PaymentTypeRepository repository) {
        mRepository = repository;
    }

    public LiveData<List<PaymentType>> getPaymentTypes(@NonNull final Context context) {
        // Caller should observe this object for changes. When the data has finished
        // async loading, the observer can react accordingly.
        final MutableLiveData<List<PaymentType>> observable =
                new MutableLiveData<>(new ArrayList<>());

        GroovyExecutors.getInstance().getDiskIo().execute(() -> {
            List<PaymentType> resultSet = mRepository.getPaymentTypes(context);
            observable.postValue(resultSet);
        });

        return observable;
    }

    public String getPaymentTotalDifference(String paymentReceived, String totalAmountDue) {
        return (
                new CurrencyRules().getFormattedAmount(
                        Math.abs(
                                convertCurrencyToLong(paymentReceived)
                                        - convertCurrencyToLong(totalAmountDue)
                        ),
                        Locale.getDefault()
                )
        );
    }

    public boolean isPaymentTotalDifferenceChangeDue(
            String paymentReceived,
            String totalAmountDue) {
        return convertCurrencyToLong(paymentReceived) - convertCurrencyToLong(totalAmountDue) > 0;
    }

    private long convertCurrencyToLong(String targetValue) {
        return Math.round(Double.parseDouble(targetValue.substring(1)) * 100);
    }
}
