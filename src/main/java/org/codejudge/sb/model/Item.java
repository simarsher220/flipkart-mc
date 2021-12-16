package org.codejudge.sb.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class Item {

    private String brand;
    private String category;
    private Integer quantity;
    private Double price;

    public Item(String brand, String category, Double price) {
        this.brand = brand;
        this.category = category;
        this.price = price;
    }

    public Item(String brand, String category, Integer quantity) {
        this.brand = brand;
        this.category = category;
        this.quantity = quantity;
    }

    public Item(String brand, String category) {
        this.brand = brand;
        this.category = category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return brand.equals(item.brand) && category.equals(item.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(brand, category);
    }
}
