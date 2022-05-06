package br.com.meli.projetointegrador.dto;

import br.com.meli.projetointegrador.model.Carrier;
import br.com.meli.projetointegrador.model.ShippingOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ShippingOrderResponseDTO {

    private String message;

    private Long id;

    private String status;

    private String shippingAddress;

    private List<CartProductDTO> items;

    private List<CarrierResponseDTO> carriers;

    public static ShippingOrderResponseDTO map(ShippingOrder shippingOrder){
        return new ShippingOrderResponseDTO("Shipping order generated successfully!",
                shippingOrder.getId(),
                shippingOrder.getShippingOrderStatusCode().name(),
                shippingOrder.getShippingAddress(),
                CartProductDTO.map(shippingOrder.getCart().getItems()),
                shippingOrder.getCarrierList().stream().map(CarrierResponseDTO::map).collect(Collectors.toList()));
    }
}
