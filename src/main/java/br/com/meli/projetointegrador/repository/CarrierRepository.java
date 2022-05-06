package br.com.meli.projetointegrador.repository;

import br.com.meli.projetointegrador.model.Carrier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarrierRepository extends JpaRepository<Carrier, Long> {
    Optional<Carrier> findByLicensePlate(String plate);
    List<Carrier> findAllByWarehouseId(Long warehouseId);
}
