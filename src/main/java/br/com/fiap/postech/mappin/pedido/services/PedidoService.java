package br.com.fiap.postech.mappin.pedido.services;

import br.com.fiap.postech.mappin.pedido.entities.Pedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface PedidoService {
    Pedido save(Pedido pedido);

    Page<Pedido> findAll(Pageable pageable, Pedido pedido);

    Pedido findById(UUID id);

    Pedido update(UUID id, Pedido pedido);

    void delete(UUID id);
}
