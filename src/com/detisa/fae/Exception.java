package com.detisa.fae;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import lombok.Data;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Exception", propOrder = {
    "message"
})
@Data
public class Exception {

    protected String message;
}
