package br.com.fiap.postech.mappin.pedido.repository;

import br.com.fiap.postech.mappin.pedido.entities.Pedido;
import br.com.fiap.postech.mappin.pedido.enumerations.Status;
import br.com.fiap.postech.mappin.pedido.helper.PedidoHelper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@Transactional
public class PedidoRepositoryIT {
    @Autowired
    private PedidoRepository pedidoRepository;

    @Test
    void devePermitirCriarEstrutura() {
        var totalRegistros = pedidoRepository.count();
        assertThat(totalRegistros).isEqualTo(3);
    }

    @Test
    void devePermitirCadastrarPedido() {
        // Arrange
        var pedido = PedidoHelper.getPedido(true);
        pedido.setStatus(Status.AGUARDANDO_PAGAMENTO.name());
        pedido.setValorTotal(1d);
        // Act
        var pedidoCadastrado = pedidoRepository.save(pedido);
        // Assert
        assertThat(pedidoCadastrado).isInstanceOf(Pedido.class).isNotNull();
        assertThat(pedidoCadastrado.getId()).isEqualTo(pedido.getId());
        assertThat(pedidoCadastrado.getIdUsuario()).isEqualTo(pedido.getIdUsuario());
        assertThat(pedidoCadastrado.getStatus()).isEqualTo(pedido.getStatus());
        assertThat(pedidoCadastrado.getItens()).isNotNull();
        assertThat(pedidoCadastrado.getItens().get(0)).isEqualTo(pedido.getItens().get(0));
        assertThat(pedidoCadastrado.getItens().get(0).getId()).isEqualTo(pedido.getItens().get(0).getId());
        assertThat(pedidoCadastrado.getItens().get(0).getIdProduto()).isEqualTo(pedido.getItens().get(0).getIdProduto());
        assertThat(pedidoCadastrado.getItens().get(0).getQuantidade()).isEqualTo(pedido.getItens().get(0).getQuantidade());
        assertThat(pedidoCadastrado.getItens().get(1)).isEqualTo(pedido.getItens().get(1));
        assertThat(pedidoCadastrado.getItens().get(1).getId()).isEqualTo(pedido.getItens().get(1).getId());
        assertThat(pedidoCadastrado.getItens().get(1).getIdProduto()).isEqualTo(pedido.getItens().get(1).getIdProduto());
        assertThat(pedidoCadastrado.getItens().get(1).getQuantidade()).isEqualTo(pedido.getItens().get(1).getQuantidade());
    }
    @Test
    void devePermitirBuscarPedido() {
        // Arrange
        var id = UUID.fromString("a8c301d4-347f-4d1b-9171-c191b8aa0d88");
        var status = "PENDENTE";
        // Act
        var pedidoOpcional = pedidoRepository.findById(id);
        // Assert
        assertThat(pedidoOpcional).isPresent();
        pedidoOpcional.ifPresent(
                pedidoRecebido -> {
                    assertThat(pedidoRecebido).isInstanceOf(Pedido.class).isNotNull();
                    assertThat(pedidoRecebido.getId()).isEqualTo(id);
                    assertThat(pedidoRecebido.getStatus()).isEqualTo(status);
                }
        );
    }
    @Test
    void devePermitirRemoverPedido() {
        // Arrange
        var id = UUID.fromString("8855e7b2-77b6-448b-97f8-8a0b529f3976");
        // Act
        pedidoRepository.deleteById(id);
        // Assert
        var pedidoOpcional = pedidoRepository.findById(id);
        assertThat(pedidoOpcional).isEmpty();
    }
    @Test
    void devePermitirListarPedidos() {
        // Arrange
        // Act
        var pedidosListados = pedidoRepository.findAll();
        // Assert
        assertThat(pedidosListados).hasSize(3);
    }
}
