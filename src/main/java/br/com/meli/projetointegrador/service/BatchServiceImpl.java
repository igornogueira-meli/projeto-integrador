package br.com.meli.projetointegrador.service;

import br.com.meli.projetointegrador.exception.InexistentBatchException;
import br.com.meli.projetointegrador.exception.NotFoundProductException;
import br.com.meli.projetointegrador.model.Batch;
import br.com.meli.projetointegrador.model.Item;
import br.com.meli.projetointegrador.repository.BatchRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * Classe de serviço responsável por processar  os lotes.
 * @author Igor de Souza Nogueira
 * @author Luis Felipe Floriano Olimpio
 */
@Service
@AllArgsConstructor
public class BatchServiceImpl implements BatchService {

    private BatchRepository batchRepository;
    private SectionService sectionService;

    @Override
    public List<Batch> save(List<Batch> batches) {
        return batchRepository.saveAll(batches);
    }

    @Override
    public Batch findById(Long id) {
        return batchRepository.findById(id).orElseThrow(() -> new InexistentBatchException("Batch " + id + " does not exists!"));
    }

    @Override
    public List<Object[]> getBatchStockByWarehouse(Long productId) {
        List<Object[]> result = batchRepository.groupAllByWarehouseId(productId);

        if (result.size() == 0) {
            throw new NotFoundProductException("The Product ID: " + productId + " does not exists!");
        }
        return result;
    }

    public List<Batch> getBatchesWithExpirationDateGreaterThan3Weeks(Long productId) {
        List<Batch> batches = findAllBatchesByProduct(productId);

        return batches.stream().filter(batch -> ChronoUnit.DAYS.between(LocalDate.now(), batch.getExpirationDate()) > 21).collect(Collectors.toList());
    }

    @Override
    public List<Batch> findAllBatchesByProduct(Long productId) {
        return batchRepository.findAllByProductId(productId);
    }

    @Override
    public List<Batch> decreaseBatch(List<Batch> batches, Integer remainingQuantity) {

        List<Batch> movedBatches = new ArrayList<>();

        for (Batch batch:batches){

            if (remainingQuantity == 0) break;

            else if(batch.getCurrentQuantity().equals(remainingQuantity)){
                remainingQuantity = 0;
                batch.setCurrentQuantity(0);
                sectionService.updateCurrentSize(1, batch.getSection().getId(), true);
                movedBatches.add(batch);
            }
            else if(batch.getCurrentQuantity() > remainingQuantity){
                batch.setCurrentQuantity(batch.getCurrentQuantity() - remainingQuantity);
                remainingQuantity = 0;
                movedBatches.add(batch);
            }
            else{
                remainingQuantity -= batch.getCurrentQuantity();
                batch.setCurrentQuantity(0);
                sectionService.updateCurrentSize(1, batch.getSection().getId(), true);
                movedBatches.add(batch);
            }
            batchRepository.save(batch);

        }
        return movedBatches;
    }

    @Override
    public List<Batch> takeOutProducts(List<Item> items) {
        List<Batch> movedBatches = new ArrayList<>();

        items.forEach(item -> {
            List<Batch> batches = getBatchesWithExpirationDateGreaterThan3Weeks(item.getAdvertisement().getId());
            batches.sort(Comparator.comparing(Batch::getCurrentQuantity));
            movedBatches.addAll(decreaseBatch(batches, item.getQuantity()));
        });

        return  movedBatches;
    }

}
