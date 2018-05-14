create view v1 as
select cust, prod, state, avg(quant) as AVG
from sales
group by cust, prod, state;

create view v2 as
select v1.cust, v1.prod, v1.state, avg(s.quant) as OTHER_STATE_AVG
from sales s, v1
where s.cust=v1.cust and s.prod=v1.prod and s.state!=v1.state
group by v1.cust, v1.prod, v1.state;

create view v3 as
select v2.cust, v2.prod, v2.state, avg(s.quant) as OTHER_PROD_AVG
from sales s, v2
where s.cust=v2.cust and s.prod!=v2.prod and s.state=v2.state
group by v2.cust, v2.prod, v2.state;

select *
from v1 natural full outer join v2 natural full outer join v3;


