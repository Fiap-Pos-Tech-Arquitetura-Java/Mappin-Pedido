package br.com.fiap.postech.mappin.pedido.helper;

import br.com.fiap.postech.mappin.pedido.entities.Item;
import br.com.fiap.postech.mappin.pedido.entities.Pedido;

import java.util.List;
import java.util.UUID;

public class PedidoHelper {

    public static Pedido getPedido(boolean geraId) {
        var pedido = new Pedido(
                UUID.randomUUID(),
                Math.random() * 1000,
                "PENDENTE",
                List.of(
                        new Item(UUID.randomUUID(),3),
                        new Item(UUID.randomUUID(),5)
                )
        );
        pedido.getItens().get(0).setId(UUID.randomUUID());
        pedido.getItens().get(1).setId(UUID.randomUUID());
        if (geraId) {
            pedido.setId(UUID.randomUUID());
        }
        return pedido;
    }
}
