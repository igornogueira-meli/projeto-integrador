package br.com.meli.projetointegrador.service;

import br.com.meli.projetointegrador.exception.CarrierAlreadyExists;
import br.com.meli.projetointegrador.exception.CarrierUnavailableException;
import br.com.meli.projetointegrador.exception.EmptyCarrierListException;
import br.com.meli.projetointegrador.exception.InexistentCarrierException;
import br.com.meli.projetointegrador.model.Carrier;
import br.com.meli.projetointegrador.model.Warehouse;
import br.com.meli.projetointegrador.repository.CarrierRepository;
import br.com.meli.projetointegrador.validator.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@AllArgsConstructor
@Service
public class CarrierServiceImpl implements CarrierService{

    CarrierRepository carrierRepository;

    @Override
    public Carrier save(Carrier carrier){

        List<Validator> validators = Collections.singletonList(
                new CarrierExists(carrier.getLicensePlate(), carrierRepository));

        validators.forEach(Validator::validate);

        return carrierRepository.save(carrier);
    }

    @Override
    public Carrier findByPlate(String plate) {
        return carrierRepository.findByLicensePlate(plate).orElseThrow(() -> new InexistentCarrierException ("Carrier with plate " + plate + " does not exists!"));
    }

    @Override
    public Carrier findById(Long id) {
        return carrierRepository.findById(id).orElseThrow(() -> new InexistentCarrierException ("Carrier " + id + " does not exists!"));
    }

    @Override
    public List<Carrier> findAllByWarehouseId(Optional<Long> warehouseId) {
        if (warehouseId.isEmpty()) return findAll();

        if(carrierRepository.findAllByWarehouseId(warehouseId.get()).size() == 0) throw new EmptyCarrierListException("No products were found for this search.");
        return carrierRepository.findAllByWarehouseId(warehouseId.get());
    }

    @Override
    public List<Carrier> checkAvailableCarries(List<Warehouse> warehouseList) {
        List<Carrier> carrierList = new ArrayList<>();

        warehouseList.stream().distinct().forEach(warehouse -> {
            carrierList.add((carrierRepository.findAllByWarehouseId(warehouse.getId()))
                    .stream().min(Comparator.comparing(carrier -> carrier.getShippingOrderList().size()))
                    .orElseThrow(() -> new CarrierUnavailableException("No carrier available!")));
        });

        return carrierList;
    }

    @Override
    public List<Carrier> findAll() {
        if(carrierRepository.findAll().size() == 0) throw new EmptyCarrierListException("No products were found for this search.");

        return carrierRepository.findAll();
    }
}
