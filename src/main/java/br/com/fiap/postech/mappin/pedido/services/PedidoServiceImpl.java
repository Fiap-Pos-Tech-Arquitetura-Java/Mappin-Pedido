package br.com.fiap.postech.mappin.pedido.services;

import br.com.fiap.postech.mappin.pedido.entities.Item;
import br.com.fiap.postech.mappin.pedido.entities.Pedido;
import br.com.fiap.postech.mappin.pedido.enumerations.Status;
import br.com.fiap.postech.mappin.pedido.integration.ProdutoProducer;
import br.com.fiap.postech.mappin.pedido.integration.ProdutoRequest;
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
    
    private final PedidoRepository pedidoRepository;
    private final ProdutoProducer produtoProducer;

    @Autowired
    public PedidoServiceImpl(PedidoRepository pedidoRepository, ProdutoProducer produtoProducer) {
        this.pedidoRepository = pedidoRepository;
        this.produtoProducer = produtoProducer;
    }

    @Override
    public Pedido save(Pedido pedido) {
        if (pedido.getItens() == null || pedido.getItens().isEmpty()) {
            throw new IllegalArgumentException("Não é possível criar um pedido sem pelo menos um item.");
        }
        if (pedido.getValorTotal() != null) {
            throw new IllegalArgumentException("O valor total de um pedido é calculado.");
        }
        if (pedido.getStatus() != null) {
            throw new IllegalArgumentException("O status inicial de um pedido é sempre AGUARDANDO_PAGAMENTO.");
        }
        pedido.setId(UUID.randomUUID());
        pedido.setStatus(Status.AGUARDANDO_PAGAMENTO.name());
        pedido.getItens().forEach(item -> {
            item.setId(UUID.randomUUID());
            var produtoResponse = produtoProducer.consultarValor(item.getIdProduto());
            item.setValorProduto(item.getQuantidade() * produtoResponse.getPreco());
        });
        pedido.setValorTotal(
            pedido.getItens().stream().mapToDouble(Item::getValorProduto).sum()
        );
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
        if (Status.PAGAMENTO_REALIZADO.name().equals(pedido.getStatus())) {
            pedido.getItens().forEach(item -> {
                var produtoRequest = new ProdutoRequest(item.getIdProduto(), item.getQuantidade());
                produtoProducer.removerDoEstoque(produtoRequest);
            });
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
