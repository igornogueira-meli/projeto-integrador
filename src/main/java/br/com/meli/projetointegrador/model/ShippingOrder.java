package br.com.meli.projetointegrador.model;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "shipping_order")
public class ShippingOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ShippingOrderStatusCode shippingOrderStatusCode;

    private String shippingAddress;

    private String receiverName;

    @OneToOne
    private Cart cart;

    @ManyToMany
    private List<Batch>  originBatches;

    @ManyToMany(cascade = { CascadeType.ALL })
    @JoinTable(
            name = "shipping_order_carrier",
            joinColumns = { @JoinColumn(name = "shipping_order_id") },
            inverseJoinColumns = { @JoinColumn(name = "carrier_id") }
    )
    List<Carrier> carrierList;

}
