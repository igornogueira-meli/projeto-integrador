package br.com.meli.projetointegrador.controller;

import br.com.meli.projetointegrador.dto.*;
import br.com.meli.projetointegrador.service.CarrierService;
import br.com.meli.projetointegrador.service.ShippingOrderService;
import br.com.meli.projetointegrador.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/fresh-products/carriers")
public class CarrierController {

    @Autowired
    CarrierService carrierService;

    @Autowired
    WarehouseService warehouseService;

    @Autowired
    ShippingOrderService shippingOrderService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_STOCK_MANAGER')")
    public ResponseEntity<CarrierResponseDTO> postPurchaseOrder(@Valid @RequestBody CarrierDTO carrierDTO) {
        return new ResponseEntity<>(CarrierResponseDTO.map(carrierService.save(CarrierDTO.map(carrierDTO, warehouseService))), HttpStatus.CREATED);
    }

    @GetMapping("/{plate}")
    @PreAuthorize("hasRole('ROLE_STOCK_MANAGER')")
    public ResponseEntity<CarrierDTO> getCarrierByPlate(@PathVariable String plate) {
        return new ResponseEntity<>(CarrierDTO.map(carrierService.findByPlate(plate)), HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_STOCK_MANAGER')")
    public ResponseEntity<List<CarrierDTO>> getAllCarrier(@RequestParam(name = "warehouseId") Optional<Long> warehouseId) {
        return new ResponseEntity<>(carrierService.findAllByWarehouseId(warehouseId).stream().map(CarrierDTO::map).collect(Collectors.toList()), HttpStatus.OK);
    }


}
