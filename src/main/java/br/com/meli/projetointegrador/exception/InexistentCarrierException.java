package br.com.meli.projetointegrador.exception;

public class InexistentCarrierException extends RuntimeException{
    public InexistentCarrierException(String message) {
        super(message);
    }
}
