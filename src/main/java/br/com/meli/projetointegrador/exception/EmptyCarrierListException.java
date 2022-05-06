package br.com.meli.projetointegrador.exception;

public class EmptyCarrierListException extends RuntimeException{
    public EmptyCarrierListException(String message) {
        super(message);
    }
}
