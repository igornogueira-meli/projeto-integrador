package br.com.meli.projetointegrador.dto;

import br.com.meli.projetointegrador.model.Carrier;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CarrierResponseDTO {
    private Long id;
    private String licensePlate;
    private Long warehouseId;

    public static CarrierResponseDTO map(Carrier carrier){
        return new CarrierResponseDTO(carrier.getId(), carrier.getLicensePlate(), carrier.getWarehouse().getId());
    }

}
