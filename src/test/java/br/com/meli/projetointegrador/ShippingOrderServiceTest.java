package br.com.meli.projetointegrador;

import br.com.meli.projetointegrador.model.*;
import br.com.meli.projetointegrador.repository.CarrierRepository;
import br.com.meli.projetointegrador.repository.ShippingOrderRepository;
import br.com.meli.projetointegrador.service.CarrierService;
import br.com.meli.projetointegrador.service.CarrierServiceImpl;
import br.com.meli.projetointegrador.service.ShippingOrderService;
import br.com.meli.projetointegrador.service.ShippingOrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ShippingOrderServiceTest {

    private ShippingOrderService shippingOrderService;

    @Mock
    private ShippingOrderRepository shippingOrderRepository;

    @Mock
    private CarrierService carrierService;

    @BeforeEach
    private void initializeInboundOrderService(){
        MockitoAnnotations.openMocks(this);
        this.shippingOrderService = new ShippingOrderServiceImpl(shippingOrderRepository, carrierService);
    }

    private ShippingOrder generateShippingOrder(){
        Cart cart = new Cart(1L, LocalDate.of(2022, 1, 1), new Customer(1L, new User(1L, "Igor", "475", " igor@hotmail.com", "Rua 1", "igor", "abcd234", Set.of(new Role(1, ERole.ROLE_CUSTOMER)))), BigDecimal.valueOf(0), new OrderStatus(1L, CartStatusCode.PURCHASE), Collections.emptyList());
        return new ShippingOrder(1L, ShippingOrderStatusCode.CREATED, "Rua 1", "Igor", cart, Collections.emptyList(), Collections.emptyList());

    }

    private Carrier generateCarrier(){
        return new Carrier(1L, "XXX-XXX", "Fiat", "Fiorino", new Warehouse(2L,"Warehouse 2", Collections.emptyList(), Collections.singletonList(new Section())), Collections.emptyList());
    }

    @Test
    public void findByIdTest(){
        ShippingOrder shippingOrder = generateShippingOrder();

        Mockito.when(shippingOrderRepository.findById(Mockito.any())).thenReturn(Optional.of(shippingOrder));

        assertEquals(shippingOrder.getId(), shippingOrderService.findById(shippingOrder.getId()).getId());

    }

    @Test
    public void findByPurchaseIdTest(){
        ShippingOrder shippingOrder = generateShippingOrder();

        Mockito.when(shippingOrderRepository.findByCartId(Mockito.any())).thenReturn(Optional.of(shippingOrder));

        assertEquals(shippingOrder.getId(), shippingOrderService.findByPurchaseId(shippingOrder.getId()).getId());

    }

    @Test
    public void findAllTest(){
        List<ShippingOrder> shippingOrders = Collections.singletonList(generateShippingOrder());

        Mockito.when(shippingOrderRepository.findAll()).thenReturn(shippingOrders);

        assertEquals(shippingOrders.size(), shippingOrderService.findAll().size());
    }

    @Test
    public void saveByPurchaseId(){
        ShippingOrder shippingOrder = generateShippingOrder();
        ShippingOrder shippingOrderToSave = generateShippingOrder();

        shippingOrderToSave.setShippingOrderStatusCode(ShippingOrderStatusCode.WAITING);

        Carrier carrier = generateCarrier();
        Carrier carrier2 = generateCarrier();

        carrier2.setId(3L);
        carrier2.setWarehouse(new Warehouse(2L,"Warehouse 2", Collections.emptyList(), Collections.singletonList(new Section())));

        List<Carrier> carriers = Arrays.asList(carrier, carrier2);


        Mockito.when(carrierService.findAllByWarehouseId(Mockito.any())).thenReturn(carriers);
        Mockito.when(shippingOrderRepository.findByCartId(Mockito.any())).thenReturn(Optional.of(shippingOrderToSave));
        Mockito.when(shippingOrderRepository.save(Mockito.any())).thenReturn(shippingOrderToSave);

        shippingOrder.setCarrierList(carriers);

        assertEquals(shippingOrder.getShippingOrderStatusCode().name(), shippingOrderService.saveByPurchaseId(1L).getShippingOrderStatusCode().name());

    }




}
