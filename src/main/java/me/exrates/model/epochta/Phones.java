package me.exrates.model.epochta;

import lombok.Data;

/**
 * Created by Maks on 17.10.2017.
 */
@Data
public class Phones {

    private String idMessage;
    private String varaibles;
    private String phone;

    public Phones(String idMessage,String variables,String phone){
        this.phone=phone;
        this.varaibles=variables;
        this.idMessage=idMessage;
    }

    public Phones(String idMessage, String phone) {
        this.idMessage = idMessage;
        this.phone = phone;
    }
}
