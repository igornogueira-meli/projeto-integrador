package br.com.meli.projetointegrador.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PurchaseOrderDTO {

    @NotNull
    private Long purchaseId;

}
