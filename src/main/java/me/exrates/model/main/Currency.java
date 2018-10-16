package me.exrates.model.main;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class Currency {
  private int id;
  private String name;
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String description;

  public Currency(int id) {
    this.id = id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Currency currency = (Currency) o;

    if (id != currency.id) return false;
    if (name != null ? !name.equals(currency.name) : currency.name != null) return false;
    return description != null ? description.equals(currency.description) : currency.description == null;
  }

  @Override
  public int hashCode() {
    int result = id;
    result = 31 * result + (name != null ? name.hashCode() : 0);
    result = 31 * result + (description != null ? description.hashCode() : 0);
    return result;
  }

}
