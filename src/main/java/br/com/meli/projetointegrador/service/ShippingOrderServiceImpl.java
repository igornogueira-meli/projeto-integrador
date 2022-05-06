package br.com.meli.projetointegrador.service;

import br.com.meli.projetointegrador.exception.InexistentShippingOrderException;
import br.com.meli.projetointegrador.model.Carrier;
import br.com.meli.projetointegrador.model.ShippingOrder;
import br.com.meli.projetointegrador.model.ShippingOrderStatusCode;
import br.com.meli.projetointegrador.model.Warehouse;
import br.com.meli.projetointegrador.repository.ShippingOrderRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ShippingOrderServiceImpl implements ShippingOrderService{

    ShippingOrderRepository shippingOrderRepository;
    CarrierService carrierService;

    @Override
    public ShippingOrder findById(Long id) {
        return shippingOrderRepository.findById(id).orElseThrow(() ->new InexistentShippingOrderException("Shipping Order " + id +" does not exists!"));
    }

    @Override
    public ShippingOrder findByPurchaseId(Long id) {
        return shippingOrderRepository.findByCartId(id).orElseThrow(() -> new InexistentShippingOrderException("Shipping Order with cart id " + id + " does not exists!"));
    }

    @Override
    public void presave(ShippingOrder shippingOrder){
        shippingOrder.setShippingOrderStatusCode(ShippingOrderStatusCode.WAITING);
        shippingOrderRepository.save(shippingOrder);
    }

    @Override
    public ShippingOrder saveByPurchaseId(Long purchaseId){
        ShippingOrder shippingOrder = findByPurchaseId(purchaseId);

        List<Warehouse> warehouseList = shippingOrder.getOriginBatches().stream().map(batch -> batch.getSection().getWarehouse()).collect(Collectors.toList());

        List<Carrier> carrierList = carrierService.checkAvailableCarries(warehouseList);

        shippingOrder.setShippingAddress(shippingOrder.getCart().getCustomer().getUser().getAddress());
        shippingOrder.setCarrierList(carrierList);
        shippingOrder.setShippingOrderStatusCode(ShippingOrderStatusCode.CREATED);

        return shippingOrderRepository.save(shippingOrder);
    }

    @Override
    public List<ShippingOrder> findAll() {
        return shippingOrderRepository.findAll();
    }


}
