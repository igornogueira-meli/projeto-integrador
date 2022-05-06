package br.com.meli.projetointegrador.validator;

import br.com.meli.projetointegrador.exception.StatusCodeIncorrectException;
import br.com.meli.projetointegrador.model.CartStatusCode;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class OrderStatusCorrect implements Validator {

    private CartStatusCode cartStatusCode;

    @Override
    public void validate() {
        if (!cartStatusCode.equals(CartStatusCode.CART)) throw new StatusCodeIncorrectException("Status code " + cartStatusCode + " not correct, expected " + CartStatusCode.CART.name());
    }
}
