package br.com.meli.projetointegrador.controller;

import br.com.meli.projetointegrador.dto.CarrierDTO;
import br.com.meli.projetointegrador.dto.CarrierResponseDTO;
import br.com.meli.projetointegrador.dto.PurchaseOrderDTO;
import br.com.meli.projetointegrador.dto.ShippingOrderResponseDTO;
import br.com.meli.projetointegrador.service.ShippingOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/fresh-products/shipping_orders")
public class ShippingOrderController {

    @Autowired
    ShippingOrderService shippingOrderService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_STOCK_MANAGER')")
    public ResponseEntity<ShippingOrderResponseDTO> postShippingOrder(@Valid @RequestBody PurchaseOrderDTO purchaseOrderDTO) {
        return new ResponseEntity<>(ShippingOrderResponseDTO.map(shippingOrderService.saveByPurchaseId(purchaseOrderDTO.getPurchaseId())), HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_STOCK_MANAGER')")
    public ResponseEntity<List<ShippingOrderResponseDTO>> getAllShippingOrders() {
        return new ResponseEntity<>(shippingOrderService.findAll().stream().map(ShippingOrderResponseDTO::map).collect(Collectors.toList()), HttpStatus.OK);
    }

    @GetMapping("/{shippingOrderId}")
    @PreAuthorize("hasRole('ROLE_STOCK_MANAGER')")
    public ResponseEntity<ShippingOrderResponseDTO> getShippingOrderById(@PathVariable Long shippingOrderId) {
        return new ResponseEntity<>(ShippingOrderResponseDTO.map(shippingOrderService.findById(shippingOrderId)), HttpStatus.OK);
    }


}
