with NJ as
(select sales.cust, sales.prod, njmax.max_q as nj_max, sales.month as nj_month,sales.day as nj_day,sales.year as nj_year
from (select cust,prod,max(quant)as max_q
from sales
WHERE state='NJ' and year<2009
group by cust, prod) as njmax
left join sales
on sales.cust = njmax.cust and sales.prod =njmax.prod and sales.quant=njmax.max_q),

NY AS
(select sales.cust, sales.prod, nymin.min_q as ny_min, sales.month as ny_month,sales.day as ny_day,sales.year as ny_year
from (select cust,prod,min(quant)as min_q
from sales
WHERE state='NY' and year<2009
group by cust, prod) as nymin
left join sales
on sales.cust = nymin.cust and sales.prod =nymin.prod and sales.quant = nymin.min_q),

CT AS
(select sales.cust, sales.prod, ctmin.min_q as ct_min, sales.month as ct_month,sales.day as ct_day,sales.year as ct_year
from (select cust,prod,min(quant)as min_q
from sales
WHERE state='CT'
group by cust, prod) as ctmin
left join sales
on sales.cust = ctmin.cust and sales.prod =ctmin.prod and sales.quant = ctmin.min_q)

select * from ((NJ natural full outer join NY) natural full outer join CT) as result
