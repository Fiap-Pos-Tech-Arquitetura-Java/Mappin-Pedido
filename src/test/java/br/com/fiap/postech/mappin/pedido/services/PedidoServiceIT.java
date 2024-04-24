package br.com.fiap.postech.mappin.pedido.services;

import br.com.fiap.postech.mappin.pedido.entities.Pedido;
import br.com.fiap.postech.mappin.pedido.helper.PedidoHelper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@Transactional
public class PedidoServiceIT {
    @Autowired
    private PedidoService pedidoService;

    @Nested
    class CadastrarPedido {
        @Test
        void devePermitirCadastrarPedido() {
            // Arrange
            var pedido = PedidoHelper.getPedido(false);
            // Act
            var pedidoSalvo = pedidoService.save(pedido);
            // Assert
            assertThat(pedidoSalvo)
                    .isInstanceOf(Pedido.class)
                    .isNotNull();
            assertThat(pedidoSalvo.getIdUsuario()).isEqualTo(pedido.getIdUsuario());
            assertThat(pedidoSalvo.getValorTotal()).isEqualTo(pedido.getValorTotal());
            assertThat(pedidoSalvo.getStatus()).isEqualTo(pedido.getStatus());
            assertThat(pedidoSalvo.getId()).isNotNull();
        }

        @Test
        void deveGerarExcecao_QuandoCadastrarPedido_semInformarItens() {
            // Arrange
            var pedido = PedidoHelper.getPedido(false);
            pedido.setItens(null);
            // Act &&  Assert
            assertThatThrownBy(() -> pedidoService.save(pedido))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Não é possível criar um pedido sem pelo menos um item.");
        }
    }

    @Nested
    class BuscarPedido {
        @Test
        void devePermitirBuscarPedidoPorId() {
            // Arrange
            var id = UUID.fromString("a8c301d4-347f-4d1b-9171-c191b8aa0d88");
            var idUsuario = UUID.fromString("567c44cd-9441-4d88-9f43-54cc3655aaaf");
            // Act
            var pedidoObtido = pedidoService.findById(id);
            // Assert
            assertThat(pedidoObtido).isNotNull().isInstanceOf(Pedido.class);
            assertThat(pedidoObtido.getIdUsuario()).isEqualTo(idUsuario);
            assertThat(pedidoObtido.getId()).isNotNull();
            assertThat(pedidoObtido.getId()).isEqualTo(id);
        }

        @Test
        void deveGerarExcecao_QuandoBuscarPedidoPorId_idNaoExiste() {
            // Arrange
            var pedido = PedidoHelper.getPedido(true);
            UUID uuid = pedido.getId();
            // Act &&  Assert
            assertThatThrownBy(() -> pedidoService.findById(uuid))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Pedido não encontrado com o ID: " + pedido.getId());
        }

        @Test
        void devePermitirBuscarTodosPedido() {
            // Arrange
            Pedido criteriosDeBusca = new Pedido(null,null,null, null);
            criteriosDeBusca.setId(null);
            // Act
            var listaPedidosObtidos = pedidoService.findAll(Pageable.unpaged(), criteriosDeBusca);
            // Assert
            assertThat(listaPedidosObtidos).isNotNull().isInstanceOf(Page.class);
            assertThat(listaPedidosObtidos.getContent()).asList().hasSize(3);
            assertThat(listaPedidosObtidos.getContent()).asList().allSatisfy(
                    pedidoObtido -> assertThat(pedidoObtido).isNotNull()
            );
        }
    }

    @Nested
    class AlterarPedido {

        @Test
        void devePermitirAlterarPedido() {
            // Arrange
            var id = UUID.fromString("d5b351c5-bc58-4c5c-8549-5113e7fea1ac");
            var status = "FINALIZADO";

            var pedido = new Pedido(null, null, status, null);
            pedido.setId(null);
            // Act
            var pedidoAtualizada = pedidoService.update(id, pedido);
            // Assert
            assertThat(pedidoAtualizada).isNotNull().isInstanceOf(Pedido.class);
            assertThat(pedidoAtualizada.getId()).isNotNull();
            assertThat(pedidoAtualizada.getStatus()).isEqualTo(status);
        }

        @Test
        void deveGerarExcecao_QuandoAlterarPedidoPorId_idNaoExiste() {
            // Arrange
            var pedido = PedidoHelper.getPedido(true);
            var uuid = pedido.getId();
            // Act &&  Assert
            assertThatThrownBy(() -> pedidoService.update(uuid, pedido))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Pedido não encontrado com o ID: " + pedido.getId());
        }
    }

    @Nested
    class RemoverPedido {
        @Test
        void devePermitirRemoverPedido() {
            // Arrange
            var id = UUID.fromString("90b91424-d7f4-4695-b5a8-e489232007b9");
            // Act
            pedidoService.delete(id);
            // Assert
            assertThatThrownBy(() -> pedidoService.findById(id))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Pedido não encontrado com o ID: " + id);
        }

        @Test
        void deveGerarExcecao_QuandoRemoverPedidoPorId_idNaoExiste() {
            // Arrange
            var pedido = PedidoHelper.getPedido(true);
            var uuid = pedido.getId();
            // Act &&  Assert
            assertThatThrownBy(() -> pedidoService.delete(uuid))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Pedido não encontrado com o ID: " + pedido.getId());
        }
    }
}
