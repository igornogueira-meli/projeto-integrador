package br.com.meli.projetointegrador;

import br.com.meli.projetointegrador.exception.CarrierAlreadyExists;
import br.com.meli.projetointegrador.exception.EmptyCarrierListException;
import br.com.meli.projetointegrador.exception.InexistentCarrierException;
import br.com.meli.projetointegrador.model.Carrier;
import br.com.meli.projetointegrador.model.Section;
import br.com.meli.projetointegrador.model.Warehouse;
import br.com.meli.projetointegrador.repository.CarrierRepository;
import br.com.meli.projetointegrador.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class CarrierServiceTest {

    private CarrierService carrierService;

    @Mock
    private CarrierRepository carrierRepository;


    @BeforeEach
    private void initializeInboundOrderService(){
        MockitoAnnotations.openMocks(this);
        this.carrierService = new CarrierServiceImpl(carrierRepository);
    }

    private Carrier generateCarrier(){
        return new Carrier(1L, "XXX-XXX", "Fiat", "Fiorino", new Warehouse(2L,"Warehouse 2", Collections.emptyList(), Collections.singletonList(new Section())), Collections.emptyList());
    }

    @Test
    public void saveValidCarrierTest(){

        Carrier carrier = generateCarrier();

        Mockito.when(carrierRepository.findByLicensePlate(Mockito.any())).thenReturn(Optional.empty());
        Mockito.when(carrierRepository.save(Mockito.any())).thenReturn(carrier);

        assertEquals(carrier.getId(), carrierService.save(carrier).getId());

    }

    @Test
    public void saveInvalidCarrierTest(){
        Carrier carrier = generateCarrier();

        Mockito.when(carrierRepository.findByLicensePlate(Mockito.any())).thenReturn(Optional.of(carrier));

        assertThrows(CarrierAlreadyExists.class, () -> carrierService.save(carrier));
    }

    @Test
    public void findByValidPlateTest(){
        Carrier carrier = generateCarrier();

        Mockito.when(carrierRepository.findByLicensePlate(Mockito.any())).thenReturn(Optional.of(carrier));

        assertEquals(carrier.getId(), carrierService.findByPlate(carrier.getLicensePlate()).getId());
    }

    @Test
    public void findByInvalidPlateTest(){
        Carrier carrier = generateCarrier();

        Mockito.when(carrierRepository.findByLicensePlate(Mockito.any())).thenReturn(Optional.empty());

        assertThrows(InexistentCarrierException.class, () -> carrierService.findByPlate(carrier.getLicensePlate()));
    }

    @Test
    public void findByValidIdTest(){
        Carrier carrier = generateCarrier();

        Mockito.when(carrierRepository.findById(Mockito.any())).thenReturn(Optional.of(carrier));

        assertEquals(carrier.getId(), carrierService.findById(carrier.getId()).getId());
    }

    @Test
    public void findByInvalidIdTest(){
        Carrier carrier = generateCarrier();

        Mockito.when(carrierRepository.findById(Mockito.any())).thenReturn(Optional.empty());

        assertThrows(InexistentCarrierException.class, () -> carrierService.findById(carrier.getId()));
    }

    @Test
    public void findAllInvalidTest(){


        Mockito.when(carrierRepository.findAll()).thenReturn(Collections.emptyList());

        assertThrows(EmptyCarrierListException.class, () -> carrierService.findAll());

    }

    @Test
    public void findAllTest(){
        List<Carrier> carriers = Collections.singletonList(generateCarrier());

        Mockito.when(carrierRepository.findAll()).thenReturn(carriers);

        assertEquals(carriers.get(0).getId(), carrierService.findAll().get(0).getId());

    }

    @Test
    public void findAllByInvalidWarehouseId(){

        Mockito.when(carrierRepository.findAll()).thenReturn(Collections.emptyList());

        assertThrows(EmptyCarrierListException.class, () -> carrierService.findAllByWarehouseId(Optional.of(1L)));
    }

    @Test
    public void findAllByValidWarehouseId(){
        List<Carrier> carriers = Collections.singletonList(generateCarrier());

        Mockito.when(carrierRepository.findAllByWarehouseId(Mockito.any())).thenReturn(carriers);

        assertEquals(carriers.get(0).getId(), carrierService.findAllByWarehouseId(Optional.of(1L)).get(0).getId());
    }

    @Test
    public void checkAvailableCarriesTest(){
        Carrier carrier = generateCarrier();
        Carrier carrier1 = generateCarrier();
        Carrier carrier2 = generateCarrier();

        carrier1.setId(2L);
        carrier2.setId(3L);
        carrier2.setWarehouse(new Warehouse(2L,"Warehouse 2", Collections.emptyList(), Collections.singletonList(new Section())));

        List<Carrier> carriers = Arrays.asList(carrier, carrier1, carrier2);
        List<Warehouse> warehouses = Arrays.asList(carrier.getWarehouse(), carrier2.getWarehouse());

        Mockito.when(carrierRepository.findAllByWarehouseId(Mockito.any())).thenReturn(carriers);

        assertEquals(2, carrierService.checkAvailableCarries(warehouses).size());
    }


}
