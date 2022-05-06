package br.com.meli.projetointegrador.dto;

import br.com.meli.projetointegrador.model.ShippingOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public class ShippingOrderCarrierDTO {

    private Long id;

    private String status;

    private String shippingAddress;

    private List<CartProductDTO> items;

    public static ShippingOrderCarrierDTO map(ShippingOrder shippingOrder){
        return new ShippingOrderCarrierDTO(shippingOrder.getId(),
                shippingOrder.getShippingOrderStatusCode().name(),
                shippingOrder.getShippingAddress(),
                CartProductDTO.map(shippingOrder.getCart().getItems()));
    }
}
