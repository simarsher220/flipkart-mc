package org.codejudge.sb.model;

import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class Cart {

    private User user;
    private Map<Item, Integer> itemToQuantityMap;

    public Cart(User user) {
        this.user = user;
        itemToQuantityMap = Maps.newHashMap();
    }
}
