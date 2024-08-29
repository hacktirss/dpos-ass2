/*
 * QRFactory
 * ASS2PuntoVenta®
 * © 2023, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since feb 2023
 */
package com.ass2.volumetrico.puntoventa.utils.qr;

public class QRFactory {
    
    private QRFactory() {}

    public static QRGenerator getFactory(String classname) throws ReflectiveOperationException {
        Class clazz = Class.forName(classname);
        if (!QRGenerator.class.isAssignableFrom(clazz)) {
            throw new IllegalAccessException("La clase " + classname + " no implementa QRGenerator");
        }
        return (QRGenerator) clazz.getDeclaredConstructor().newInstance();
    }
}
