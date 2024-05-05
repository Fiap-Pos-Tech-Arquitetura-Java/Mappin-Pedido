package br.com.fiap.postech.mappin.pedido.integration;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.UUID;

@FeignClient(value = "estoque", url = "${url.produto}")
public interface ProdutoProducer {
    @GetMapping(value = "/produto/{idProduto}")
    ProdutoResponse consultarValor(@PathVariable UUID idProduto);
    @PostMapping(value = "/api/consumer-remover-do-estoque")
    void removerDoEstoque(ProdutoRequest produtoRequest);
}
