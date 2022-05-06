package br.com.meli.projetointegrador.repository;

import br.com.meli.projetointegrador.model.Carrier;
import br.com.meli.projetointegrador.model.ShippingOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShippingOrderRepository extends JpaRepository<ShippingOrder, Long> {
    List<ShippingOrder> findAll();
    Optional<ShippingOrder> findByCartId(Long Id);

}
