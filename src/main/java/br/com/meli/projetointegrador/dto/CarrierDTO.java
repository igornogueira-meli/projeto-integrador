package br.com.meli.projetointegrador.dto;

import br.com.meli.projetointegrador.model.Carrier;
import br.com.meli.projetointegrador.model.ShippingOrder;
import br.com.meli.projetointegrador.model.Warehouse;
import br.com.meli.projetointegrador.service.ShippingOrderService;
import br.com.meli.projetointegrador.service.WarehouseService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CarrierDTO {

    @NotNull(message = "LicensePlate missing.")
    private String licensePlate;

    @NotNull(message = "Brand missing.")
    private String brand;

    @NotNull(message = "Model missing.")
    private String model;

    @NotNull(message = "WarehouseId missing.")
    private Long warehouseId;

    private List<ShippingOrderCarrierDTO> shippingOrderList;

    public static Carrier map(CarrierDTO carrierDTO, WarehouseService warehouseService){
        Warehouse warehouse = warehouseService.findById(carrierDTO.getWarehouseId());

        return Carrier.builder()
                .licensePlate(carrierDTO.getLicensePlate())
                .brand(carrierDTO.getBrand())
                .model(carrierDTO.getModel())
                .warehouse(warehouse)
                .build();
    }

    public static CarrierDTO map(Carrier carrier){
        return new CarrierDTO(carrier.getLicensePlate(), carrier.getBrand(), carrier.getModel(), carrier.getWarehouse().getId(), carrier.getShippingOrderList().stream().map(ShippingOrderCarrierDTO::map).collect(Collectors.toList()));

    }

}
