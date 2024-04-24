package br.com.fiap.postech.mappin.pedido.services;

import br.com.fiap.postech.mappin.pedido.entities.Pedido;
import br.com.fiap.postech.mappin.pedido.repository.PedidoRepository;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.UUID;

@Service
public class PedidoServiceImpl implements PedidoService {
    
    private final PedidoRepository
            pedidoRepository;

    @Autowired
    public PedidoServiceImpl(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    @Override
    public Pedido save(Pedido pedido) {
        if (pedido.getItens() == null || pedido.getItens().isEmpty()) {
            throw new IllegalArgumentException("Não é possível criar um pedido sem pelo menos um item.");
        }
        pedido.setId(UUID.randomUUID());
        pedido.getItens().forEach(item -> { item.setId(UUID.randomUUID()); });
        return pedidoRepository.save(pedido);
    }

    @Override
    public Page<Pedido> findAll(Pageable pageable, Pedido pedido) {
        Example<Pedido> pedidoExample = Example.of(pedido);
        return pedidoRepository.findAll(pedidoExample, pageable);
    }

    @Override
    public Pedido findById(UUID id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado com o ID: " + id));
    }

    @Override
    public Pedido update(UUID id, Pedido pedidoParam) {
        Pedido pedido = findById(id);
        if (pedidoParam.getId() != null && !pedido.getId().equals(pedidoParam.getId())) {
            throw new IllegalArgumentException("Não é possível alterar o id de um pedido.");
        }
        if (pedidoParam.getIdUsuario() != null && !pedido.getIdUsuario().equals(pedidoParam.getIdUsuario())) {
            throw new IllegalArgumentException("Não é possível alterar o usuário de um pedido.");
        }
        if (pedidoParam.getValorTotal() != null && !pedido.getValorTotal().equals(pedidoParam.getValorTotal())) {
            throw new IllegalArgumentException("Não é possível alterar o valor total de um pedido.");
        }
        if (pedidoParam.getItens() != null && !new HashSet<>(pedidoParam.getItens()).containsAll(pedido.getItens())) {
            throw new IllegalArgumentException("Não é possível alterar os itens um pedido.");
        }
        if (StringUtils.isNotEmpty(pedidoParam.getStatus())) {
            pedido.setStatus(pedidoParam.getStatus());
        }
        pedido = pedidoRepository.save(pedido);
        return pedido;
    }

    @Override
    public void delete(UUID id) {
        findById(id);
        pedidoRepository.deleteById(id);
    }
}
