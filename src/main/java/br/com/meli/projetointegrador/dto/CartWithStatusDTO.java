package br.com.meli.projetointegrador.dto;

import br.com.meli.projetointegrador.model.Cart;


import br.com.meli.projetointegrador.model.CartStatusCode;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

/**
 * Classe DTO para retornar status de uma Cart após atualização.
 * @author Igor de Souza Nogueira
 * @author Luis Felipe Floriano Olimpio
 */
@AllArgsConstructor
@Getter
public class CartWithStatusDTO {

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate orderDate;
    private CartStatusCode cartStatusCode;
    private List<CartProductDTO> items;

    public static CartWithStatusDTO map(Cart cart){
        return new CartWithStatusDTO(cart.getOrderDate(), cart.getOrderStatus().getCartStatusCode(), CartProductDTO.map(cart.getItems()));
    }

}
