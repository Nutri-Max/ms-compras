package com.nutrimax.mscompras.repository;

import com.nutrimax.mscompras.model.EstadoPedido;
import com.nutrimax.mscompras.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    // RF-CO-01: buscar el carrito activo de un usuario
    Optional<Pedido> findByUsuarioIdAndEstado(Long usuarioId, EstadoPedido estado);

    // RF-CO-03: historial de pedidos de un usuario (sin incluir el carrito activo)
    List<Pedido> findByUsuarioIdAndEstadoNot(Long usuarioId, EstadoPedido estado);
}