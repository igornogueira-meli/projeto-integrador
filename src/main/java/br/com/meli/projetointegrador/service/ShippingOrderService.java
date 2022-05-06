package br.com.meli.projetointegrador.service;

import br.com.meli.projetointegrador.model.ShippingOrder;

import java.util.Collection;
import java.util.List;

public interface ShippingOrderService {

    ShippingOrder findById(Long id);

    ShippingOrder findByPurchaseId(Long id);

    void presave(ShippingOrder shippingOrder);

    ShippingOrder saveByPurchaseId(Long purchaseId);

    List<ShippingOrder> findAll();
}
