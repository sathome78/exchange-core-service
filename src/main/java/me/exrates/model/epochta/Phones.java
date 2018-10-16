package me.exrates.model.epochta;

import lombok.Data;

@Data
public class Phones {

    private String idMessage;
    private String varaibles;
    private String phone;

    public Phones(String idMessage, String variables, String phone) {
        this.phone = phone;
        this.varaibles = variables;
        this.idMessage = idMessage;
    }

    public Phones(String idMessage, String phone) {
        this.idMessage = idMessage;
        this.phone = phone;
    }
}
