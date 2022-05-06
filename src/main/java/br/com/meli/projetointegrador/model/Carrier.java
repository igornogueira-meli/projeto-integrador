package br.com.meli.projetointegrador.model;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "carrier")
@Entity
public class Carrier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String licensePlate;

    private String brand;

    private String model;

    @ManyToOne
    private Warehouse warehouse;

    @ManyToMany(mappedBy = "carrierList")
    private List<ShippingOrder> shippingOrderList;


}
