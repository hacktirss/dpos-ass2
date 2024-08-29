/*
 * FactoryBuilder
 * ASS2PuntoVenta®
 * © 2019, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since mar 2019
 */
package com.detisa.omicrom.integraciones;

import com.ass2.volumetrico.puntoventa.services.actions.ActionFactory;

public class FactoryBuilder {
    
    private FactoryBuilder() {}

    public static ActionFactory getFactory(String classname) throws ReflectiveOperationException {
        Class clazz = Class.forName(classname);
        if (!ActionFactory.class.isAssignableFrom(clazz)) {
            throw new IllegalAccessException("La clase " + classname + " no extiende un ActionFactory");
        }
        return (ActionFactory) clazz.getDeclaredConstructor().newInstance();
    }
}
