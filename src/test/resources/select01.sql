/*
 * Name: Select 01
 * File: select01.sql
 */
SELECT
    distinct
    A.ID      AS ID     -- ID
  , A.NAME    AS NAME   -- 名前
  , CASE A.SEX
        WHEN '1' THEN '男'
        WHEN '2' THEN '女'
        ELSE          'その他'
    END       AS SEX    -- 性別
  , B.LABEL   AS LABEL  -- ラベル
  , 1 + 100.0 AS NUMBER -- 数値
FROM
    TM_PROFILE A
        LEFT OUTER JOIN TD_PARAMETER B ON A.CD = B.CD
  , TM_ACCOUNT C
WHERE
    A.ID          = B.ID
AND A.DELETE_FLAG = FALSE
AND B.DELETE_FLAG = FALSE
ORDER BY
    A.ID     ASC
  , B.LABEL  DESC
