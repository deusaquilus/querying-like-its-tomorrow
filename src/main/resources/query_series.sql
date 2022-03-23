--Initial
SELECT h.firstName || ' ' || h.lastName AS name, h.age, h.membership FROM Humans h

--Bit more complex
SELECT h.firstName || ' ' || h.lastName AS name, h.age, h.membership FROM Humans h
WHERE h.memberOf = 'h'

--Bit more complex
SELECT h.firstName || ' ' || h.lastName AS name, h.age, h.membership FROM Humans h
WHERE h.memberOf = 'h'
UNION
SELECT h.heroName AS name, h.age, 'PLAT' FROM SuperHumans h
WHERE h.side = 'g'

--Bit more complex
SELECT h.firstName || ' ' || h.lastName AS name, h.age, h.membership FROM Humans h
WHERE h.segment = 'h' AND h.age > 44
UNION
SELECT h.heroName AS name, h.age, 'PLAT' FROM SuperHumans h
WHERE h.side = 'g' AND h.age > 123


-- New Biz Unit
SELECT r.model AS name, today() - r.assemblyYear, 'AUTO' FROM Robots r
WHERE r.assemblyYear > 1990
UNION
SELECT r.series || "-" || r.model AS name, today() - r.assemblyYear, 'AUTO' FROM KillerRobots r
WHERE r.series  = 'T' AND r.assemblyYear > 1992


--Bit more complex
SELECT customer.name, customer.age, customer.membership, customer.id, h.zip FROM (
  SELECT h.firstName || ' ' || h.lastName AS name, h.age, h.membership, h.id FROM Humans h
  WHERE h.segment = 'h' AND h.age > 1982
  UNION
  SELECT h.heroName AS name, h.age, 'PLAT', h.id FROM SuperHumans h
  WHERE h.side = 'g' AND h.age > 1856
) AS customer
JOIN Houses h ON h.OWNER = customer.ID

--Bit more complex (robot)
SELECT customer.name, customer.age, customer.membership, customer.id, h.zip FROM (
  SELECT r.model AS name, today() - r.assemblyYear, 'AUTO' FROM Robots r
  WHERE r.assemblyYear > 1990
  UNION
  SELECT r.series || "-" || r.model AS name, today() - r.assemblyYear, 'AUTO' FROM KillerRobots r
  WHERE r.series  = 'T' AND r.assemblyYear > 1992
) AS customer
JOIN Houses h ON h.OWNER = customer.ID AND h.hasChargingPort=true



--Bit more complex
SELECT customer.name, customer.age, customer.membership, customer.id, h.zip FROM (
  SELECT h.firstName || ' ' || h.lastName AS name, h.age, h.membership, h.id FROM Humans h
  WHERE h.segment = 'h' AND h.age > 1982
  UNION
  SELECT h.heroName AS name, h.age, 'PLAT', h.id FROM SuperHumans h
  WHERE h.side = 'g' AND h.age > 1856
) AS customer
JOIN Houses h ON h.OWNER = customer.ID AND h.hasChargingPort=true
JOIN PricingYears p ON customer.age > p.startYear && customer.age < p.endYear

--Bit more complex (robot)
SELECT customer.name, customer.age, customer.membership, customer.id, h.zip FROM (
  SELECT r.model AS name, today() - r.assemblyYear, 'AUTO' FROM Robots r
  WHERE r.assemblyYear > 1990
  UNION
  SELECT r.series || "-" || r.model AS name, today() - r.assemblyYear, 'AUTO' FROM KillerRobots r
  WHERE r.series  = 'T' AND r.assemblyYear > 1992
) AS customer
JOIN Pods h ON h.OWNER = customer.ID AND h.hasChargingPort=true
JOIN PodLifetimes l ON l.POD_FK = h.ID AND l.supportedModelsYear >= r.assemblyYear


--Bit more complex
SELECT customer.name, customer.age,
  CASE WHEN p.pricing == 'sane' THEN p. customer.membership ELSE p.insaneMembership END as membership,
  customer.id, h.zip FROM
(
  SELECT h.firstName || ' ' || h.lastName AS name, h.age, h.membership, h.id FROM Humans h
  WHERE h.segment = 'h' AND h.age > 1982
  UNION
  SELECT h.heroName AS name, h.age, 'PLAT', h.id FROM SuperHumans h
  WHERE h.side = 'g' AND h.age > 1856
) AS customer
JOIN Houses h ON h.OWNER = customer.ID AND h.hasChargingPort=true
JOIN PricingYears p ON customer.age > p.startYear && customer.age < p.endYear


--Bit more complex (robot)
SELECT customer.name, customer.age,
  CASE WHEN p.voltage = 120 THEN 'USA' ELSE 'EU' END as membership,
  customer.id, h.zip FROM
(
  SELECT r.model AS name, today() - r.assemblyYear, 'AUTO' FROM Robots r
  WHERE r.assemblyYear > 1990
  UNION
  SELECT r.series || "-" || r.model AS name, today() - r.assemblyYear, 'AUTO' FROM KillerRobots r
  WHERE r.series  = 'T' AND r.assemblyYear > 1992
) AS customer
JOIN Houses h ON h.OWNER = customer.ID AND h.hasChargingPort=true
JOIN PricingYears p ON customer.age > p.startYear && customer.age < p.endYear



SELECT customer.name, customer.age, customer.membership, customer.id, h.zip
FROM
(
  SELECT r.uniqueGruntingSound AS name, y.age, 'YETT'
  FROM Yetti y
) AS customer
JOIN Houses h ON h.OWNER = customer.ID AND (h.origin = 'Canada' OR h.origin = 'Russia')
JOIN PricingYears p ON customer.age > p.startYear && customer.age < p.endYear


/* Testing the Slick Query */
select x2.x3,
       x2.x4,
       (case when (x5."pricing" = 'sane') then x2.x6 else x5."insaneMembership" end),
       x2.x7,
       x8."zip"
from ((select "id" as x7, ("firstName" || ' ') || "lastName" as x3, "age" as x4, "membership" as x6
      from "Human" where ("segment" = 'h') and ("age" > ?))
      union all
      (select "id" as x7, "heroName" as x3, "age" as x4, ? as x6
      from "SuperHuman" where ("side" = 'g') and ("age" > ?))) x2,
     "Houses" x8, "PrincingYears" x5
where ((x8."owner" = x2.x7) and ?)
  and ((x2.x4 > x5."startYear") and (x2.x4 < x5."endYear"))

