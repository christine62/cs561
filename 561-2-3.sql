CREATE VIEW q31 AS
select cust, prod, sum(quant) as SUM
from sales
GROUP BY cust,prod;

create view q32 AS
select cust, prod, month, sum(quant) as msum
from sales
group by cust, prod, month;

CREATE VIEW q33 AS
select a.cust, a.prod, a.month, sum(b.msum) as sum_total
from q32 a, q32 b
where a.cust=b.cust and a.prod = b.prod and b.month<=a.month
group by a.cust, a.prod, a.month;

SELECT q33.cust, q33.prod, min(q33.month) as HALF_PURCHASED_BY_MONTH
from q31, q33
where q33.sum_total >= (0.5)*q31.SUM and q31.cust=q33.cust and q31.prod=q33.prod
group by q33.cust,q33.prod;





