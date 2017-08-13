SELECT req_date as `date`, count(*) AS `dau`
FROM (SELECT DISTINCT
        user_id,
        DATE(request_date) AS 'req_date'
      FROM request
      WHERE DATE(request_date) IN ('2017/06/07', '2017/06/08', '2017/06/09', '2017/06/10', '2017/06/15')) AS uniq_req
GROUP BY uniq_req.req_date;