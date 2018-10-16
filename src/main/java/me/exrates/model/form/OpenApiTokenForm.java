package me.exrates.model.form;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.main.OpenApiToken;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@ToString
public class OpenApiTokenForm {

    private Long id;
    @NotNull
    @Pattern(regexp = "^[a-zA-Z\\d]{4,15}$]")
    private String alias;
    @NotNull
    private Boolean allowTrade;
    @NotNull
    private Boolean allowWithdraw;

    public OpenApiTokenForm() {
    }

    public OpenApiTokenForm(OpenApiToken token) {
        this.id = token.getId();
        this.alias = token.getAlias();
        this.allowTrade = token.getAllowTrade();
        this.allowWithdraw = token.getAllowWithdraw();
    }
}
