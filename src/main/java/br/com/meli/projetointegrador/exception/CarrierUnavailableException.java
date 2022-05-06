package br.com.meli.projetointegrador.exception;

public class CarrierUnavailableException extends RuntimeException{
    public CarrierUnavailableException(String message) {
        super(message);
    }
}
