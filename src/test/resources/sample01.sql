-- 
-- Commment
-- 
/*
sss
*/
SELECT
    A.ID           AS ID    -- ID
  , A.NAME         AS NAME  -- 名前
  , NVL(A.AGE, 0)  AS 年齢
  , 'STRING'       AS 文字列
  , 'TEST'         AS "あ""ああ" -- 文字列
FROM
    TM_PROFILE A  -- プロフィールテーブル
WHERE
    A.ID  IN(0,1,'2',3) /* ここは条件 */
AND A.NAME IS NOT NULL
AND A.AGE >= 10
AND A.AGE <= 99
/* test1
   test2
*/ AND 'AAAA' = '--''--'
