package br.com.meli.projetointegrador;


import br.com.meli.projetointegrador.exception.InexistentBatchException;
import br.com.meli.projetointegrador.exception.NotFoundProductException;
import br.com.meli.projetointegrador.model.*;
import br.com.meli.projetointegrador.repository.BatchRepository;
import br.com.meli.projetointegrador.service.BatchServiceImpl;
import br.com.meli.projetointegrador.service.SectionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class BatchServiceTest {
    private BatchServiceImpl batchService;

    @Mock
    private BatchRepository batchRepository;
    @Mock
    private SectionService sectionService;

    @BeforeEach
    private void initializeBatchService() {
        MockitoAnnotations.openMocks(this);
        this.batchService = new BatchServiceImpl(batchRepository, sectionService);
    }

    private List<Batch> generateBatches(){
        return Arrays.asList(
                new Batch(1L, 15.0, 12.0, 200, 200,
                        LocalDate.of(2022, 1, 1), LocalDateTime.of(2022, 1, 1, 0, 0),
                        LocalDate.of(2022, 7, 9), new Product(), new InboundOrder(), new Section()),
                new Batch(2L, 0.0, -15.0, 50, 50,
                        LocalDate.of(2022, 3, 3), LocalDateTime.of(2022, 1, 3, 0, 0),
                        LocalDate.of(2022, 7, 10), new Product(), new InboundOrder(), new Section()));
    }

    @Test
    public void saveTest() {
        List<Batch> batches = generateBatches();

        Mockito.when(batchRepository.saveAll(Mockito.any())).thenReturn(batches);

        assertEquals(batches.get(0), batchService.save(batches).get(0));
    }

    @Test
    public void findByIdTest() {
        List<Batch> batches = generateBatches();

        Mockito.when(batchRepository.findById(Mockito.any())).thenReturn(java.util.Optional.of(batches.get(0)));

        assertEquals(batches.get(0), batchService.findById(1L));
    }

    @Test
    public void inexistentBatchExceptionTest(){
        Mockito.when(batchRepository.findById(Mockito.any())).thenReturn(Optional.empty());

        assertThrows(InexistentBatchException.class, () -> batchService.findById(1L));
    }

    @Test
    void EmptyBatchStockByWarehouse() {

        List<Object[]> result = new ArrayList<>();
        Mockito.when(batchRepository.groupAllByWarehouseId(Mockito.anyLong())).thenReturn(result);

        assertThrows(NotFoundProductException.class, () -> batchService.getBatchStockByWarehouse(2L));

    }

    @Test
    void NonEmptyBatchStockByWarehouse() {
        Object[] objects = new Object[0];

        List<Object[]> result = Arrays.asList(objects, objects);
        Mockito.when(batchRepository.groupAllByWarehouseId(Mockito.anyLong())).thenReturn(result);

        assertDoesNotThrow(() -> batchService.getBatchStockByWarehouse(2L));
    }

    @Test
    public void getBatchesWithExpirationDateGreaterThan3Weeks(){
        List<Batch> batches = generateBatches();

        Mockito.when(batchRepository.findAllByProductId(Mockito.any())).thenReturn(batches);

        assertEquals(batches.size(), batchService.getBatchesWithExpirationDateGreaterThan3Weeks(1L).size());

    }

    @Test
    public void findAllBatchesByProductTest(){

        List<Batch> batches = generateBatches();

        Mockito.when(batchRepository.findAllByProductId(Mockito.any())).thenReturn(batches);

        assertEquals(batches.size(), batchService.findAllBatchesByProduct(1L).size());
    }





}
