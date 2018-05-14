create view vv1 as
  (select * from
  (select distinct cust, prod from sales group by cust, prod) a,
  (select distinct month from sales) b);

create view vv2 as
select cust, prod, month, avg(quant) as avg
from sales
group by cust, prod, month;

create view vv3 as
select *
from vv1 natural left join vv2;

create view vv4 AS
select c.cust,c.prod,c.month, d.avg as after
from vv3 c, vv3 d
where d.cust=c.cust and d.prod=c.prod and d.month=c.month+1;

create view vv5 AS
select c.cust,c.prod,c.month, d.avg as before
from vv3 c, vv3 d
where d.cust=c.cust and d.prod=c.prod and d.month=c.month-1;

SELECT *
FROM vv4 NATURAL FULL OUTER JOIN vv5;

