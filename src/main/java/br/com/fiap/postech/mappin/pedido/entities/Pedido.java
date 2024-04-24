package br.com.fiap.postech.mappin.pedido.entities;

import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "tb_pedido")
public class Pedido {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;
    @Column(name = "id_usuario", nullable = false)
    private UUID idUsuario;
    @Column(name = "valor_total", nullable = false)
    private Double valorTotal;
    @Column(name = "status", nullable = false)
    private String status;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_pedido", nullable = false)
    private List<Item> itens;

    public Pedido() {
        super();
    }

    public Pedido(UUID idUsuario, Double valorTotal, String status, List<Item> itens) {
        this();
        setIdUsuario(idUsuario);
        setValorTotal(valorTotal);
        setStatus(status);
        setItens(itens);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pedido pedido)) return false;
        return Objects.equals(id, pedido.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(UUID idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(Double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Item> getItens() {
        return itens;
    }

    public void setItens(List<Item> itens) {
        this.itens = itens;
    }
}
