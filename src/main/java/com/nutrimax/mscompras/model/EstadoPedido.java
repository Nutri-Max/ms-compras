package com.nutrimax.mscompras.model;

public enum EstadoPedido {
    CARRITO, // aun no confirmado, el cliente puede seguir editando
    PENDIENTE, // confirmado, esperando pago
    PAGADO, // pago confirmado
    ENVIADO,
    ENTREGADO,
    CANCELADO
}