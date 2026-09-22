package com.nutrimax.mscompras.service;

import com.nutrimax.mscompras.config.RabbitMQConfig;
import com.nutrimax.mscompras.dto.OrderConfirmedEvent;
import com.nutrimax.mscompras.model.EstadoPedido;
import com.nutrimax.mscompras.model.ItemPedido;
import com.nutrimax.mscompras.model.Pedido;
import com.nutrimax.mscompras.repository.PedidoRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    // RF-CO-01: obtener o crear el carrito activo del usuario
    public Pedido obtenerOCrearCarrito(Long usuarioId) {
        return pedidoRepository.findByUsuarioIdAndEstado(usuarioId, EstadoPedido.CARRITO)
                .orElseGet(() -> {
                    Pedido nuevoCarrito = new Pedido();
                    nuevoCarrito.setUsuarioId(usuarioId);
                    nuevoCarrito.setEstado(EstadoPedido.CARRITO);
                    return pedidoRepository.save(nuevoCarrito);
                });
    }

    // RF-CO-01: agregar producto al carrito
    public Pedido agregarItem(Long usuarioId, Long productoId, String nombreProducto,
            Integer cantidad, BigDecimal precioUnitario) {
        Pedido carrito = obtenerOCrearCarrito(usuarioId);

        ItemPedido item = new ItemPedido();
        item.setPedido(carrito);
        item.setProductoId(productoId);
        item.setNombreProducto(nombreProducto);
        item.setCantidad(cantidad);
        item.setPrecioUnitario(precioUnitario);

        carrito.getItems().add(item);
        recalcularTotal(carrito);

        return pedidoRepository.save(carrito);
    }

    // RF-CO-01: quitar producto del carrito
    public Pedido quitarItem(Long usuarioId, Long itemId) {
        Pedido carrito = pedidoRepository.findByUsuarioIdAndEstado(usuarioId, EstadoPedido.CARRITO)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));

        carrito.getItems().removeIf(item -> item.getId().equals(itemId));
        recalcularTotal(carrito);

        return pedidoRepository.save(carrito);
    }

    private void recalcularTotal(Pedido pedido) {
        BigDecimal total = pedido.getItems().stream()
                .map(ItemPedido::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        pedido.setTotal(total);
    }

    // RF-CO-02: confirmar el pedido (simula el pago) y publicar el evento
    // OrderConfirmed
    public Pedido confirmarPedido(Long usuarioId) {
        Pedido carrito = pedidoRepository.findByUsuarioIdAndEstado(usuarioId, EstadoPedido.CARRITO)
                .orElseThrow(() -> new RuntimeException("No hay carrito activo"));

        if (carrito.getItems().isEmpty()) {
            throw new RuntimeException("El carrito esta vacio");
        }

        carrito.setEstado(EstadoPedido.PAGADO);
        carrito.setFechaPago(LocalDateTime.now());
        Pedido pedidoConfirmado = pedidoRepository.save(carrito);

        publicarEventoOrderConfirmed(pedidoConfirmado);

        return pedidoConfirmado;
    }

    private void publicarEventoOrderConfirmed(Pedido pedido) {
        List<OrderConfirmedEvent.ItemEvento> itemsEvento = pedido.getItems().stream()
                .map(item -> new OrderConfirmedEvent.ItemEvento(item.getProductoId(), item.getCantidad()))
                .collect(Collectors.toList());

        OrderConfirmedEvent evento = new OrderConfirmedEvent(
                pedido.getId(), pedido.getUsuarioId(), pedido.getTotal(), itemsEvento);

        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY, evento);
    }

    // RF-CO-03: historial de pedidos
    public List<Pedido> obtenerHistorial(Long usuarioId) {
        return pedidoRepository.findByUsuarioIdAndEstadoNot(usuarioId, EstadoPedido.CARRITO);
    }

    // RF-CO-03: consultar un pedido especifico
    public Pedido obtenerPedido(Long pedidoId) {
        return pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
    }
}