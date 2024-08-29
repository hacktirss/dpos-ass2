package com.ass2.volumetrico.puntoventa.common;

import com.softcoatl.utils.logging.LogManager;
import com.softcoatl.commons.Currency;
import com.softcoatl.commons.NumberToLetterConverter;
import com.softcoatl.commons.NumberToLetterConvertionException;
import com.softcoatl.commons.NumericalCurrencyConverter;
import com.softcoatl.commons.SpanishNumbers;
import java.math.BigDecimal;

/**
 *
 * @author ROLANDO
 */
public class FacturacionUtils {

    public static String importeLetra(String sImporte) {
        NumberToLetterConverter numericalCurrencyConverter = new NumericalCurrencyConverter(new SpanishNumbers(), new Currency("PESOS", "PESO"));
        try {
            return numericalCurrencyConverter.convert(new BigDecimal(sImporte))+ " M.N.";
        } catch (NumberToLetterConvertionException ex) {
            LogManager.error(ex);
        }
        return "";
    }//importeLetra
}
