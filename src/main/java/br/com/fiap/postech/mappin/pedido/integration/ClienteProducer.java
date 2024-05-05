package br.com.fiap.postech.mappin.pedido.integration;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(value = "cliente", url = "${url.cliente}")
public interface ClienteProducer {
    @GetMapping(value = "/cliente/{idCliente}")
    void clienteExiste(@PathVariable UUID idCliente);
}