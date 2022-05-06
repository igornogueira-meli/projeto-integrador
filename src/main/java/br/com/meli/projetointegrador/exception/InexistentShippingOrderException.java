package br.com.meli.projetointegrador.exception;

public class InexistentShippingOrderException extends RuntimeException {
    public InexistentShippingOrderException(String message) {
            super(message);
        }
}
