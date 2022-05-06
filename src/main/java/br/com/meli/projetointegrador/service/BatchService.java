package br.com.meli.projetointegrador.service;

import br.com.meli.projetointegrador.model.Batch;
import br.com.meli.projetointegrador.model.Item;

import java.util.List;

import java.util.List;
/**
 * Interface de serviço responsável por processar  os lotes.
 * @author Igor de Souza Nogueira
 * @author Luis Felipe Floriano Olimpio
 */
public interface BatchService {
    List<Batch> save(List<Batch> batches);
    Batch findById(Long id);
    List<Object[]> getBatchStockByWarehouse(Long id);
    List<Batch> getBatchesWithExpirationDateGreaterThan3Weeks(Long productId);
    List<Batch> findAllBatchesByProduct(Long productId);
    List<Batch> takeOutProducts(List<Item> items);
    List<Batch> decreaseBatch(List<Batch> batches, Integer remainingQuantity);
}
