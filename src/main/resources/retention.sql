SELECT
  req_stat.ins_date AS `ins_date`,
  req_date,
  (requests / ins_count * 100) as `percentage`
FROM (
       SELECT
         ins_date,
         req_date,
         count(req_date) AS `requests`
       FROM (SELECT DISTINCT
               user_id,
               date(install_date) AS `ins_date`
             FROM user
             WHERE date(install_date) BETWEEN '2017/06/28' AND '2017/07/01') `installed`
         JOIN (SELECT DISTINCT
                      user_id,
                      date(request_date) AS `req_date`
                    FROM request
                    WHERE date(
                        request_date) BETWEEN '2017/06/28' AND '2017/07/01') `requested`
           ON (installed.user_id = requested.user_id AND installed.ins_date <= requested.req_date)
       WHERE req_date IS NOT NULL
       GROUP BY ins_date, req_date) `req_stat`
  JOIN (SELECT DISTINCT
          date(install_date) AS `ins_date`,
          count(*)           AS `ins_count`
        FROM user
        WHERE date(install_date) BETWEEN '2017/06/28' AND '2017/07/01'
        GROUP BY date(install_date)) ins_stat
    ON req_stat.ins_date = ins_stat.ins_date
ORDER BY ins_date, req_date;



