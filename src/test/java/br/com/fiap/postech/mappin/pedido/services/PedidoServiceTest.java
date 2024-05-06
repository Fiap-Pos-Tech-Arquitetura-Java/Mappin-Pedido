package br.com.fiap.postech.mappin.pedido.services;

import br.com.fiap.postech.mappin.pedido.entities.Pedido;
import br.com.fiap.postech.mappin.pedido.enumerations.Status;
import br.com.fiap.postech.mappin.pedido.helper.PedidoHelper;
import br.com.fiap.postech.mappin.pedido.integration.ProdutoProducer;
import br.com.fiap.postech.mappin.pedido.integration.ProdutoRequest;
import br.com.fiap.postech.mappin.pedido.integration.ProdutoResponse;
import br.com.fiap.postech.mappin.pedido.integration.ClienteProducer;
import br.com.fiap.postech.mappin.pedido.repository.PedidoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PedidoServiceTest {
    private PedidoService pedidoService;

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ProdutoProducer produtoProducer;

    @Mock
    private ClienteProducer clienteProducer;

    private AutoCloseable mock;

    @BeforeEach
    void setUp() {
        mock = MockitoAnnotations.openMocks(this);
        pedidoService = new PedidoServiceImpl(pedidoRepository, produtoProducer, clienteProducer);
    }

    @AfterEach
    void tearDown() throws Exception {
        mock.close();
    }

    @Nested
    class CadastrarPedido {
        @Test
        void devePermitirCadastrarPedido() {
            // Arrange
            var pedido = PedidoHelper.getPedido(false);
            when(pedidoRepository.save(any(Pedido.class))).thenAnswer(r -> r.getArgument(0));
            when(produtoProducer.consultarValor(any(UUID.class))).thenReturn(new ProdutoResponse(Math.random() * 100));
            // Act
            var pedidoSalvo = pedidoService.save(pedido);
            // Assert
            assertThat(pedidoSalvo)
                    .isInstanceOf(Pedido.class)
                    .isNotNull();
            assertThat(pedidoSalvo.getIdCliente()).isEqualTo(pedido.getIdCliente());
            assertThat(pedidoSalvo.getValorTotal()).isEqualTo(pedido.getValorTotal());
            assertThat(pedidoSalvo.getStatus()).isEqualTo(pedido.getStatus());
            assertThat(pedidoSalvo.getId()).isNotNull();
            verify(pedidoRepository, times(1)).save(any(Pedido.class));
            verify(produtoProducer, times(2)).consultarValor(any(UUID.class));
        }

        @Test
        void deveGerarExcecao_QuandoCadastrarPedido_semInformarItens() {
            // Arrange
            var pedido = PedidoHelper.getPedido(true);
            pedido.setItens(null);
            when(pedidoRepository.findById(pedido.getId())).thenReturn(Optional.of(pedido));
            // Act && Assert
            assertThatThrownBy(() -> pedidoService.save(pedido))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Não é possível criar um pedido sem pelo menos um item.");
            verify(pedidoRepository, never()).save(any(Pedido.class));
        }

        @Test
        void deveGerarExcecao_QuandoCadastrarPedido_informandoListaItensVazia() {
            // Arrange
            var pedido = PedidoHelper.getPedido(true);
            pedido.setItens(List.of());
            when(pedidoRepository.findById(pedido.getId())).thenReturn(Optional.of(pedido));
            // Act && Assert
            assertThatThrownBy(() -> pedidoService.save(pedido))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Não é possível criar um pedido sem pelo menos um item.");
            verify(pedidoRepository, never()).save(any(Pedido.class));
        }

        @Test
        void deveGerarExcecao_QuandoCadastrarPedido_informandoValorTotal() {
            // Arrange
            var pedido = PedidoHelper.getPedido(true);
            pedido.setValorTotal(Math.random() * 100);
            when(pedidoRepository.findById(pedido.getId())).thenReturn(Optional.of(pedido));
            // Act && Assert
            assertThatThrownBy(() -> pedidoService.save(pedido))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("O valor total de um pedido é calculado.");
            verify(pedidoRepository, never()).save(any(Pedido.class));
        }

        @Test
        void deveGerarExcecao_QuandoCadastrarPedido_informandoStatus() {
            // Arrange
            var pedido = PedidoHelper.getPedido(true);
            pedido.setStatus(Status.FINALIZADO.name());
            when(pedidoRepository.findById(pedido.getId())).thenReturn(Optional.of(pedido));
            // Act && Assert
            assertThatThrownBy(() -> pedidoService.save(pedido))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("O status inicial de um pedido é sempre AGUARDANDO_PAGAMENTO.");
            verify(pedidoRepository, never()).save(any(Pedido.class));
        }
    }

    @Nested
    class BuscarPedido {
        @Test
        void devePermitirBuscarPedidoPorId() {
            // Arrange
            var pedido = PedidoHelper.getPedido(true);
            when(pedidoRepository.findById(pedido.getId())).thenReturn(Optional.of(pedido));
            // Act
            var pedidoObtido = pedidoService.findById(pedido.getId());
            // Assert
            assertThat(pedidoObtido).isEqualTo(pedido);
            verify(pedidoRepository, times(1)).findById(any(UUID.class));
        }

        @Test
        void deveGerarExcecao_QuandoBuscarPedidoPorId_idNaoExiste() {
            // Arrange
            var pedido = PedidoHelper.getPedido(true);
            when(pedidoRepository.findById(pedido.getId())).thenReturn(Optional.empty());
            UUID uuid = pedido.getId();
            // Act
            assertThatThrownBy(() -> pedidoService.findById(uuid))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Pedido não encontrado com o ID: " + pedido.getId());
            // Assert
            verify(pedidoRepository, times(1)).findById(any(UUID.class));
        }

        @Test
        void devePermitirBuscarTodosPedido() {
            // Arrange
            Pedido criteriosDeBusca = PedidoHelper.getPedido(false);
            Page<Pedido> pedidos = new PageImpl<>(Arrays.asList(
                    PedidoHelper.getPedido(true),
                    PedidoHelper.getPedido(true),
                    PedidoHelper.getPedido(true)
            ));
            when(pedidoRepository.findAll(any(Example.class), any(Pageable.class))).thenReturn(pedidos);
            // Act
            var pedidosObtidos = pedidoService.findAll(Pageable.unpaged(), criteriosDeBusca);
            // Assert
            assertThat(pedidosObtidos).hasSize(3);
            assertThat(pedidosObtidos.getContent()).asList().allSatisfy(
                    pedido -> assertThat(pedido)
                            .isNotNull()
                            .isInstanceOf(Pedido.class)
            );
            verify(pedidoRepository, times(1)).findAll(any(Example.class), any(Pageable.class));
        }
    }

    @Nested
    class AlterarPedido {
        @Test
        void devePermitirAlterarPedido() {
            // Arrange
            var pedido = PedidoHelper.getPedido(true);
            var pedidoReferencia = new Pedido(pedido.getIdCliente(), pedido.getValorTotal(), pedido.getStatus(), pedido.getItens());
            var novoPedido = new Pedido(
                    pedido.getIdCliente(),
                    pedido.getValorTotal(),
                    "AGUARDANDO ENTREGA",
                    pedido.getItens()
            );
            novoPedido.setId(pedido.getId());
            when(pedidoRepository.findById(pedido.getId())).thenReturn(Optional.of(pedido));
            when(pedidoRepository.save(any(Pedido.class))).thenAnswer(r -> r.getArgument(0));
            // Act
            var pedidoSalvo = pedidoService.update(pedido.getId(), novoPedido);
            // Assert
            assertThat(pedidoSalvo)
                    .isInstanceOf(Pedido.class)
                    .isNotNull();

            assertThat(pedidoSalvo.getStatus()).isEqualTo(novoPedido.getStatus());
            assertThat(pedidoSalvo.getStatus()).isNotEqualTo(pedidoReferencia.getStatus());

            verify(pedidoRepository, times(1)).findById(any(UUID.class));
            verify(pedidoRepository, times(1)).save(any(Pedido.class));
        }

        @Test
        void devePermitirAlterarProduto_semBody() {
            // Arrange
            var pedido = PedidoHelper.getPedido(true);
            var novoPedido = new Pedido(null, null, null, null);
            when(pedidoRepository.findById(pedido.getId())).thenReturn(Optional.of(pedido));
            when(pedidoRepository.save(any(Pedido.class))).thenAnswer(r -> r.getArgument(0));
            // Act
            var pedidoSalvo = pedidoService.update(pedido.getId(), novoPedido);
            // Assert
            assertThat(pedidoSalvo)
                    .isInstanceOf(Pedido.class)
                    .isNotNull();

            assertThat(pedidoSalvo.getStatus()).isEqualTo(pedido.getStatus());

            verify(pedidoRepository, times(1)).findById(any(UUID.class));
            verify(pedidoRepository, times(1)).save(any(Pedido.class));
        }

        @Test
        void devePermitirAlterarPedido_statusPagagmentoRealizado() {
            // Arrange
            var pedido = PedidoHelper.getPedido(true);
            var pedidoReferencia = new Pedido(pedido.getIdCliente(), pedido.getValorTotal(), pedido.getStatus(), pedido.getItens());
            var novoPedido = new Pedido(
                    pedido.getIdCliente(),
                    pedido.getValorTotal(),
                    "PAGAMENTO_REALIZADO",
                    pedido.getItens()
            );
            novoPedido.setId(pedido.getId());
            when(pedidoRepository.findById(pedido.getId())).thenReturn(Optional.of(pedido));
            when(pedidoRepository.save(any(Pedido.class))).thenAnswer(r -> r.getArgument(0));
            // Act
            var pedidoSalvo = pedidoService.update(pedido.getId(), novoPedido);
            // Assert
            assertThat(pedidoSalvo)
                    .isInstanceOf(Pedido.class)
                    .isNotNull();

            assertThat(pedidoSalvo.getStatus()).isEqualTo(novoPedido.getStatus());
            assertThat(pedidoSalvo.getStatus()).isNotEqualTo(pedidoReferencia.getStatus());

            verify(pedidoRepository, times(1)).findById(any(UUID.class));
            verify(pedidoRepository, times(1)).save(any(Pedido.class));
            verify(produtoProducer, times(2)).removerDoEstoque(any(ProdutoRequest.class));
        }

        @Test
        void deveGerarExcecao_QuandoAlterarPedidoPorId_alterandoId() {
            // Arrange
            var pedido = PedidoHelper.getPedido(true);
            var novoPedido = new Pedido(
                    pedido.getIdCliente(),
                    pedido.getValorTotal(),
                    "AGUARDANDO ENTREGA",
                    pedido.getItens()
            );
            novoPedido.setId(UUID.randomUUID());
            when(pedidoRepository.findById(pedido.getId())).thenReturn(Optional.of(pedido));
            // Act && Assert
            assertThatThrownBy(() -> pedidoService.update(pedido.getId(), novoPedido))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Não é possível alterar o id de um pedido.");
            verify(pedidoRepository, times(1)).findById(any(UUID.class));
            verify(pedidoRepository, never()).save(any(Pedido.class));
        }

        @Test
        void deveGerarExcecao_QuandoAlterarPedidoPorId_alterandoCliente() {
            // Arrange
            var pedido = PedidoHelper.getPedido(true);
            var novoPedido = new Pedido(
                    UUID.randomUUID(),
                    pedido.getValorTotal(),
                    "AGUARDANDO ENTREGA",
                    pedido.getItens()
            );
            novoPedido.setId(pedido.getId());
            when(pedidoRepository.findById(pedido.getId())).thenReturn(Optional.of(pedido));
            // Act && Assert
            assertThatThrownBy(() -> pedidoService.update(pedido.getId(), novoPedido))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Não é possível alterar o usuário de um pedido.");
            verify(pedidoRepository, times(1)).findById(any(UUID.class));
            verify(pedidoRepository, never()).save(any(Pedido.class));
        }

        @Test
        void deveGerarExcecao_QuandoAlterarPedidoPorId_alterandoValorTotal() {
            // Arrange
            var pedido = PedidoHelper.getPedido(true);
            pedido.setValorTotal(Math.random() * 100);
            var novoPedido = new Pedido(
                    pedido.getIdCliente(),
                    pedido.getValorTotal() + 1,
                    "AGUARDANDO ENTREGA",
                    pedido.getItens()
            );
            novoPedido.setId(pedido.getId());
            when(pedidoRepository.findById(pedido.getId())).thenReturn(Optional.of(pedido));
            // Act && Assert
            assertThatThrownBy(() -> pedidoService.update(pedido.getId(), novoPedido))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Não é possível alterar o valor total de um pedido.");
            verify(pedidoRepository, times(1)).findById(any(UUID.class));
            verify(pedidoRepository, never()).save(any(Pedido.class));
        }

        @Test
        void deveGerarExcecao_QuandoAlterarPedidoPorId_alterandoItens() {
            // Arrange
            var pedido = PedidoHelper.getPedido(true);
            var pedidoItens = PedidoHelper.getPedido(true);
            var novoPedido = new Pedido(
                    pedido.getIdCliente(),
                    pedido.getValorTotal(),
                    "AGUARDANDO ENTREGA",
                    pedidoItens.getItens()
            );
            novoPedido.setId(pedido.getId());
            when(pedidoRepository.findById(pedido.getId())).thenReturn(Optional.of(pedido));
            // Act && Assert
            assertThatThrownBy(() -> pedidoService.update(pedido.getId(), novoPedido))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Não é possível alterar os itens um pedido.");
            verify(pedidoRepository, times(1)).findById(any(UUID.class));
            verify(pedidoRepository, never()).save(any(Pedido.class));
        }

        @Test
        void deveGerarExcecao_QuandoAlterarPedidoPorId_idNaoExiste() {
            // Arrange
            var pedido = PedidoHelper.getPedido(true);
            when(pedidoRepository.findById(pedido.getId())).thenReturn(Optional.empty());
            UUID uuid = pedido.getId();
            // Act && Assert
            assertThatThrownBy(() -> pedidoService.update(uuid, pedido))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Pedido não encontrado com o ID: " + pedido.getId());
            verify(pedidoRepository, times(1)).findById(any(UUID.class));
            verify(pedidoRepository, never()).save(any(Pedido.class));
        }
    }

    @Nested
    class RemoverPedido {
        @Test
        void devePermitirRemoverPedido() {
            // Arrange
            var pedido = PedidoHelper.getPedido(true);
            when(pedidoRepository.findById(pedido.getId())).thenReturn(Optional.of(pedido));
            doNothing().when(pedidoRepository).deleteById(pedido.getId());
            // Act
            pedidoService.delete(pedido.getId());
            // Assert
            verify(pedidoRepository, times(1)).findById(any(UUID.class));
            verify(pedidoRepository, times(1)).deleteById(any(UUID.class));
        }

        @Test
        void deveGerarExcecao_QuandoRemoverPedidoPorId_idNaoExiste() {
            // Arrange
            var pedido = PedidoHelper.getPedido(true);
            doNothing().when(pedidoRepository).deleteById(pedido.getId());
            UUID uuid = pedido.getId();
            // Act && Assert
            assertThatThrownBy(() -> pedidoService.delete(uuid))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Pedido não encontrado com o ID: " + pedido.getId());
            verify(pedidoRepository, times(1)).findById(any(UUID.class));
            verify(pedidoRepository, never()).deleteById(any(UUID.class));
        }
    }
}