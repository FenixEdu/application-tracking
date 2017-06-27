package org.fenixedu.applicationtracking.util;

import javax.money.MonetaryAmount;
import javax.money.format.MonetaryFormats;
import java.util.Locale;

public class MoneyConverter {
    public static String format(MonetaryAmount amount){
        return MonetaryFormats.getAmountFormat(Locale.ENGLISH).format(amount);
    }

    public static MonetaryAmount parse(String amount) {
        return MonetaryFormats.getAmountFormat(Locale.ENGLISH).parse(amount);
    }
}
