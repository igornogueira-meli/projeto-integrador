package br.com.meli.projetointegrador.validator;

import br.com.meli.projetointegrador.exception.CarrierAlreadyExists;
import br.com.meli.projetointegrador.repository.CarrierRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CarrierExists implements Validator {
    private String plate;
    private CarrierRepository carrierRepository;

    @Override
    public void validate() {
        if(carrierRepository.findByLicensePlate(plate).isPresent()) throw new CarrierAlreadyExists("Carrier with license plate " + plate + "already exists!");
    }
}
