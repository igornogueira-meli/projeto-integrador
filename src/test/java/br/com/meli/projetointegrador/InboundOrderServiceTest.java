package br.com.meli.projetointegrador;

import br.com.meli.projetointegrador.exception.*;
import br.com.meli.projetointegrador.model.*;
import br.com.meli.projetointegrador.repository.*;
import br.com.meli.projetointegrador.security.services.UserDetailsImpl;
import br.com.meli.projetointegrador.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContext;
import org.springframework.security.test.context.support.WithUserDetails;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;


public class InboundOrderServiceTest {

    private InboundOrderServiceImpl inboundOrderService;

    @Mock
    private InboundOrderRepository inboundOrderRepository;
    @Mock
    private BatchService batchService;
    @Mock
    private SectionService sectionService;
    @Mock
    private StockManagerService stockManagerService;
    @Mock
    private WarehouseService warehouseService;

    @BeforeEach
    private void initializeInboundOrderService(){
       MockitoAnnotations.openMocks(this);
       this.inboundOrderService = new InboundOrderServiceImpl(inboundOrderRepository, batchService, sectionService, stockManagerService, warehouseService);
   }

   private void initializeAuthentication(){
       Authentication authentication = Mockito.mock(Authentication.class);
       SecurityContext securityContext = Mockito.mock(SecurityContext.class);

       Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
       Mockito.when((UserDetailsImpl) authentication.getPrincipal()).thenReturn(UserDetailsImpl.build(new User(1L, "Igor", "123.456.789-10","igor@gmail.com", "Rua 1",  "igor_sn", "abcd1234", Set.of(new Role(1, ERole.ROLE_STOCK_MANAGER)))));
       SecurityContextHolder.setContext(securityContext);
   }

    private InboundOrder generateInboundOrder(){
        StockManager stockManagerIgor = new StockManager(1L, new User(1L, "Igor", "123.456.789-10", "igor@gmail.com", "Rua 1", "igor_sn", "abcd1234", Set.of(new Role(1, ERole.ROLE_STOCK_MANAGER))), new Warehouse());
        StockManager stockManagerJederson = new StockManager(2L, new User(2L, "Jederson", "103.476.729-30", "jederson@gmail.com", "Rua 1", "jed", "abcd1234", Set.of(new Role(1, ERole.ROLE_STOCK_MANAGER))), new Warehouse());

        Warehouse warehouse = new Warehouse(1L,"Warehouse 1", Arrays.asList(stockManagerIgor, stockManagerJederson), Collections.singletonList(new Section()));

        stockManagerIgor.setWarehouse(warehouse);
        stockManagerJederson.setWarehouse(warehouse);

        Section section1 = new Section(1L, "Section 1", Category.FRESH, 6, 6, warehouse, Collections.singletonList(new Batch()));
        Section section2 = new Section(2L, "Section 2", Category.FROZEN, 6, 6, warehouse, Collections.singletonList(new Batch()));

        warehouse.setSectionList(Arrays.asList(section1, section2));

        Product productTomato= new Product(1L, "Tomato", 2.00, 5.0, 4.0, Collections.singletonList(new Batch()));
        Product productMeat= new Product(2L, "Meat", 40.00, 10.0, 10.0, Collections.singletonList(new Batch()));

        List<Batch> batchList = Arrays.asList(
                new Batch(1L, 15.0, 12.0, 200, 200,
                        LocalDate.of(2022, 1, 1), LocalDateTime.of(2022, 1, 1, 0, 0),
                        LocalDate.of(2022, 2, 2), productTomato, new InboundOrder(), section1),
                new Batch(2L, 0.0, -15.0, 50, 50,
                    LocalDate.of(2022, 3, 3), LocalDateTime.of(2022, 1, 3, 0, 0),
                    LocalDate.of(2022, 4, 4), productMeat, new InboundOrder(), section2));

        productTomato.setBatchList(Collections.singletonList(batchList.get(0)));
        productMeat.setBatchList(Collections.singletonList(batchList.get(1)));

        section1.setBatchList(Collections.singletonList(batchList.get(0)));
        section2.setBatchList(Collections.singletonList(batchList.get(1)));

        return new InboundOrder(1L, 1, LocalDate.now(), section1, Collections.singletonList(batchList.get(0)), stockManagerIgor);
    }

    @Test
    public void saveInboundOrderTest(){
        InboundOrder inboundOrder = generateInboundOrder();

        initializeAuthentication();

        Mockito.when(inboundOrderRepository.save(Mockito.any())).thenReturn(inboundOrder);
        Mockito.when(batchService.save(Mockito.any())).thenReturn(inboundOrder.getBatchList());
        Mockito.when(sectionService.findById(Mockito.any())).thenReturn(inboundOrder.getSection());
        Mockito.when(warehouseService.findById(Mockito.any())).thenReturn(inboundOrder.getSection().getWarehouse());
        Mockito.when(stockManagerService.findByUserUsername(Mockito.any())).thenReturn(inboundOrder.getStockManager());
        Mockito.when(stockManagerService.findById(Mockito.any())).thenReturn(inboundOrder.getStockManager());

        List<Batch> response = inboundOrderService.save(inboundOrder);

        assertEquals(generateInboundOrder().getBatchList().get(0).getId(), response.get(0).getId());
    }

    @Test
    public void updateInboundOrderTest(){
        InboundOrder inboundOrder = generateInboundOrder();

        initializeAuthentication();

        Mockito.when(inboundOrderRepository.save(Mockito.any())).thenReturn(inboundOrder);
        Mockito.when(batchService.save(Mockito.any())).thenReturn(inboundOrder.getBatchList());
        Mockito.when(sectionService.findById(Mockito.any())).thenReturn(inboundOrder.getSection());
        Mockito.when(warehouseService.findById(Mockito.any())).thenReturn(inboundOrder.getSection().getWarehouse());
        Mockito.when(stockManagerService.findByUserUsername(Mockito.any())).thenReturn(inboundOrder.getStockManager());
        Mockito.when(stockManagerService.findById(Mockito.any())).thenReturn(inboundOrder.getStockManager());

        inboundOrder.getBatchList().get(0).setExpirationDate(LocalDate.of(2022, 6, 9));

        List<Batch> response = inboundOrderService.update(inboundOrder);

        assertEquals(LocalDate.of(2022, 6, 9), response.get(0).getExpirationDate());
    }

    @Test
    public void InboudOrderFindByIdTest(){
        InboundOrder inboundOrder = generateInboundOrder();

        Mockito.when(inboundOrderRepository.findById(Mockito.any())).thenReturn(Optional.of(inboundOrder));

        InboundOrder response = inboundOrderService.findById(generateInboundOrder().getId());

        assertEquals(inboundOrder.getOrderNumber(), response.getOrderNumber());

    }

    @Test
    public void SectionAvailableSpaceValidatorTest(){
        InboundOrder inboundOrder = generateInboundOrder();

        initializeAuthentication();

        Mockito.when(inboundOrderRepository.save(Mockito.any())).thenReturn(inboundOrder);
        Mockito.when(batchService.save(Mockito.any())).thenReturn(inboundOrder.getBatchList());
        Mockito.when(sectionService.findById(Mockito.any())).thenReturn(inboundOrder.getSection());
        Mockito.when(warehouseService.findById(Mockito.any())).thenReturn(inboundOrder.getSection().getWarehouse());
        Mockito.when(stockManagerService.findByUserUsername(Mockito.any())).thenReturn(inboundOrder.getStockManager());
        Mockito.when(stockManagerService.findById(Mockito.any())).thenReturn(inboundOrder.getStockManager());

        inboundOrder.getSection().setCurrentSize(0);

        assertThrows(SectionUnavailableSpaceException.class, () -> inboundOrderService.save(inboundOrder));
    }

    @Test
    public void WarehouseExistsValidatorTest(){
        InboundOrder inboundOrder = generateInboundOrder();

        initializeAuthentication();

        Mockito.when(inboundOrderRepository.save(Mockito.any())).thenReturn(inboundOrder);
        Mockito.when(batchService.save(Mockito.any())).thenReturn(inboundOrder.getBatchList());
        Mockito.when(sectionService.findById(Mockito.any())).thenReturn(inboundOrder.getSection());
        Mockito.when(warehouseService.findById(Mockito.any())).thenReturn(inboundOrder.getSection().getWarehouse());
        Mockito.when(stockManagerService.findByUserUsername(Mockito.any())).thenReturn(inboundOrder.getStockManager());
        Mockito.when(stockManagerService.findById(Mockito.any())).thenReturn(inboundOrder.getStockManager());
        Mockito.when(warehouseService.findById(Mockito.any())).thenThrow(InexistentWarehouseException.class);

        assertThrows(InexistentWarehouseException.class, () -> inboundOrderService.save(inboundOrder));

    }

    @Test
    public void SectionExistsValidatorTest(){
        InboundOrder inboundOrder = generateInboundOrder();

        initializeAuthentication();

        Mockito.when(inboundOrderRepository.save(Mockito.any())).thenReturn(inboundOrder);
        Mockito.when(batchService.save(Mockito.any())).thenReturn(inboundOrder.getBatchList());
        Mockito.when(sectionService.findById(Mockito.any())).thenReturn(inboundOrder.getSection());
        Mockito.when(warehouseService.findById(Mockito.any())).thenReturn(inboundOrder.getSection().getWarehouse());
        Mockito.when(stockManagerService.findByUserUsername(Mockito.any())).thenReturn(inboundOrder.getStockManager());
        Mockito.when(stockManagerService.findById(Mockito.any())).thenReturn(inboundOrder.getStockManager());
        Mockito.when(sectionService.findById(Mockito.any())).thenThrow(InexistentSectionException.class);

        assertThrows(InexistentSectionException.class, () -> inboundOrderService.save(inboundOrder));
    }

    @Test
    public void SectionMatchWithWarehouseValidatorTest(){
        InboundOrder inboundOrder = generateInboundOrder();

        initializeAuthentication();

        Section section3 = new Section(3L, "Section 3", Category.FRESH, 6, 6, new Warehouse(), Collections.singletonList(new Batch()));


        Mockito.when(batchService.save(Mockito.any())).thenReturn(inboundOrder.getBatchList());
        Mockito.when(warehouseService.findById(Mockito.any())).thenReturn(inboundOrder.getSection().getWarehouse());
        Mockito.when(stockManagerService.findByUserUsername(Mockito.any())).thenReturn(inboundOrder.getStockManager());
        Mockito.when(stockManagerService.findById(Mockito.any())).thenReturn(inboundOrder.getStockManager());
        Mockito.when(inboundOrderRepository.save(Mockito.any())).thenReturn(inboundOrder);
        Mockito.when(sectionService.findById(Mockito.any())).thenReturn(section3);

        assertThrows(SectionNotMatchWithWarehouseException.class, () -> inboundOrderService.save(inboundOrder));
    }

    @Test
    public void SectionMatchWithBatchCategoryValidatorTest(){
        InboundOrder inboundOrder = generateInboundOrder();

        initializeAuthentication();

        inboundOrder.getSection().setCategory(Category.REFRIGERATED);

        Mockito.when(inboundOrderRepository.save(Mockito.any())).thenReturn(inboundOrder);
        Mockito.when(batchService.save(Mockito.any())).thenReturn(inboundOrder.getBatchList());
        Mockito.when(sectionService.findById(Mockito.any())).thenReturn(inboundOrder.getSection());
        Mockito.when(warehouseService.findById(Mockito.any())).thenReturn(inboundOrder.getSection().getWarehouse());
        Mockito.when(stockManagerService.findByUserUsername(Mockito.any())).thenReturn(inboundOrder.getStockManager());
        Mockito.when(stockManagerService.findById(Mockito.any())).thenReturn(inboundOrder.getStockManager());

        assertThrows(SectionNotMatchWithBatchCategoryException.class, () -> inboundOrderService.save(inboundOrder));
    }

    @Test
    public void inexistentInboundOrderException(){
        Mockito.when(inboundOrderRepository.findById(Mockito.any())).thenReturn(Optional.empty());

        assertThrows(InexistentInboundOrderException.class, () -> inboundOrderService.findById(1L));
    }
}
