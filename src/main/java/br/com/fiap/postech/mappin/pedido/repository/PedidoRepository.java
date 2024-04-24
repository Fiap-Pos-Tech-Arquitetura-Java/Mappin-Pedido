package br.com.fiap.postech.mappin.pedido.repository;

import br.com.fiap.postech.mappin.pedido.entities.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PedidoRepository extends JpaRepository<Pedido, UUID> {

}