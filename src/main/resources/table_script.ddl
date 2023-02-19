create table customer (
	id bigint generated by default as identity,
	name varchar(255),
	tier integer,
	primary key (id)
)

create table order_product_relationship (
	order_id bigint not null,
	product_id bigint not null,
	primary key (order_id, product_id)
)

create table product (
	id bigint generated by default as identity,
	category varchar(255),
	name varchar(255),
	price double,
	primary key (id)
)

create table product_order (
	id bigint generated by default as identity,
	order_date date,
	customer_id bigint,
	primary key (id)
)

alter table order_product_relationship add constraint FKn8aeo7cic1d0ejbbxu3vxlb4c
foreign key (product_id) references product

alter table order_product_relationship add constraint FK722amt5gugjshh8fhjt6i66i3
foreign key (order_id) references product_order

alter table product_order add constraint FKa90wgrcf86ft7kh3pjivc5c5e
foreign key (customer_id) references customer