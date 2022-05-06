package br.com.meli.projetointegrador.exception;

public class CarrierAlreadyExists extends RuntimeException{
    public CarrierAlreadyExists(String message){
        super(message);
    }
}
