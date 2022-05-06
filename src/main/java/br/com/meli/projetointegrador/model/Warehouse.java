package br.com.meli.projetointegrador.model;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="warehouse")
public class Warehouse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @OneToMany(mappedBy = "warehouse", cascade = CascadeType.ALL)
    private List<StockManager> stockManagerList = new ArrayList<StockManager>();

    @OneToMany(mappedBy = "warehouse", cascade = CascadeType.ALL)
    private List<Section> sectionList = new ArrayList<Section>();

    @OneToMany(mappedBy = "warehouse", cascade = CascadeType.ALL)
    private List<Carrier> carrierList;

    public Warehouse(Long id, String name, List<StockManager> stockManagerList, List<Section> sectionList) {
        this.id = id;
        this.name = name;
        this.stockManagerList = stockManagerList;
        this.sectionList = sectionList;
    }
}
