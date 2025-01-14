CREATE TABLE t_orders
(
    id          bigserial PRIMARY KEY,
    order_number varchar(255) DEFAULT NULL,
    sku_code     varchar(255),
    price        decimal(19, 2),
    quantity     integer
);
