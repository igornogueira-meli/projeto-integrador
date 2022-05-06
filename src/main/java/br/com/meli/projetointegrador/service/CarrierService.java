package br.com.meli.projetointegrador.service;

import br.com.meli.projetointegrador.model.Carrier;
import br.com.meli.projetointegrador.model.Warehouse;

import java.util.List;
import java.util.Optional;

public interface CarrierService {
    Carrier save(Carrier carrier);
    Carrier findByPlate(String plate);
    Carrier findById(Long id);
    List<Carrier> findAllByWarehouseId(Optional<Long> warehouseId);
    List<Carrier> checkAvailableCarries(List<Warehouse> warehouseList);
    List<Carrier> findAll();
}
