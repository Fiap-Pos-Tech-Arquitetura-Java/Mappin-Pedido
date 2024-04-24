insert into tb_pedido
(id, id_usuario, valor_total, status)
values
    ('a8c301d4-347f-4d1b-9171-c191b8aa0d88', '567c44cd-9441-4d88-9f43-54cc3655aaaf', 1999.99, 'PENDENTE'),
    ('d5b351c5-bc58-4c5c-8549-5113e7fea1ac', 'c0444933-ae73-43d9-b8a9-36eff4a79009', 250.83, 'PENDENTE'),
    ('90b91424-d7f4-4695-b5a8-e489232007b9', 'b74cda62-cae6-4d3c-9cb3-c5ae7d4b40b4', 489.12, 'PENDENTE');

insert into tb_item
(id, id_pedido, id_produto, quantidade)
values
    ('0e88cc85-d05d-4f7c-a1bf-4c5a40d05669', 'a8c301d4-347f-4d1b-9171-c191b8aa0d88', 'cbc55920-97a1-4fdf-a77e-b66bb824d074', 2),
    ('6d1dbafb-5335-4841-b48f-5236ecdba424', 'a8c301d4-347f-4d1b-9171-c191b8aa0d88', '39854e07-3c75-4523-be4f-42da0244edc9', 2),
    ('0f4b8290-678c-49ca-8ca7-3eddf2625cb6', 'd5b351c5-bc58-4c5c-8549-5113e7fea1ac', 'cbc55920-97a1-4fdf-a77e-b66bb824d074', 2),
    ('9bd2f440-be3e-4683-a06c-8ae9c21c40af', 'd5b351c5-bc58-4c5c-8549-5113e7fea1ac', '39854e07-3c75-4523-be4f-42da0244edc9', 2),
    ('3bf62f7f-2c85-4b56-ac76-7036f8f779ec', '90b91424-d7f4-4695-b5a8-e489232007b9', 'cbc55920-97a1-4fdf-a77e-b66bb824d074', 2),
    ('5115d690-3a07-43a8-93e6-00a18470a00f', '90b91424-d7f4-4695-b5a8-e489232007b9', '39854e07-3c75-4523-be4f-42da0244edc9', 2);