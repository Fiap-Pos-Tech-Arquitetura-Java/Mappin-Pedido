package br.com.fiap.postech.mappin.pedido.integration;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(value = "estoque", url = "http://localhost:8081/mappin")
public interface ProdutoProducer {
    @GetMapping(value = "/produto/{idProduto}")
    ProdutoResponse consultarValor(@RequestParam UUID idProduto);
    @PostMapping(value = "/consumer-remover-do-estoque")
    void removerDoEstoque(ProdutoRequest produtoRequest);
}
