with customer as
(select cust, min(quant)as min_q,max(quant)as max_q,avg(quant) as avg_q from sales group by cust),
minq as
(select allt.cust as customer, allt.quant as min_q, allt.prod as min_prod, allt.month, allt.day, allt.year, state
from customer
left join
(select * from sales) as allt
on allt.cust=customer.cust and allt.quant = customer.min_q),
maxq as
(select  allt.cust as customer,allt.quant as max_q, allt.prod as max_prod, allt.month, allt.day, allt.year, state
from customer
left join
(select * from sales) as allt
on allt.cust=customer.cust and allt.quant = customer.max_q)
select customer.cust as customer, minq.min_q,minq.min_prod,minq.month,minq.day,minq.year,minq.state as ST,maxq.max_q,maxq.max_prod,maxq.month,maxq.day,maxq.year,maxq.state as ST,customer.avg_q
from maxq join minq on maxq.customer=minq.customer join customer on maxq.customer=customer.cust