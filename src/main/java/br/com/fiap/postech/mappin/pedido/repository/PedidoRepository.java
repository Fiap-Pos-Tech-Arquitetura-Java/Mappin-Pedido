package br.com.fiap.postech.mappin.pedido.repository;

import br.com.fiap.postech.mappin.pedido.entities.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, UUID> {

    Optional<List<Pedido>> findByStatus(String status);
}