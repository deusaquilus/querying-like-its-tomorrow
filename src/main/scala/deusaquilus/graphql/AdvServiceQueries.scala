package deusaquilus.graphql

import deusaquilus.QuillContext._
import deusaquilus.{ Human, SuperHuman, Houses, Customer, PricingYears, Record }

import io.getquill._

object AdvServiceQueries:

  enum HumanType:
    case Regular(seg: String, year: Int)
    case Super(side: String, year: Int)

  inline def humanCustomer(inline tpe: HumanType) =
    inline tpe match
      case HumanType.Regular(seg, year) =>
        query[Human]
          .filter(h => h.segment == seg && h.age > 2022 - year)
          .map(h => Customer(h.id, h.firstName + " " + h.lastName, h.age, h.membership))
      case HumanType.Super(side, year) =>
        query[SuperHuman]
          .filter(h => h.side == side && h.age > 2022 - year)
          .map(h => Customer(h.id, h.heroName, h.age, "PLAT"))

  inline def customerMembership(
    inline customers: Query[Customer],
    inline houses: Query[Houses]
  )(
    inline housesFilter: Houses => Boolean,
    inline membershipFunction: (Customer, PricingYears) => String
  ) =
    for {
      c <- customers
      h <- houses.leftJoin(h => h.owner == c.id && housesFilter(h))
    } yield Record(c.name, c.age, c.membership, c.id, h.map(_.id).getOrElse(0))

  inline def customers =
    customerMembership(
      humanCustomer(HumanType.Regular("h", 1982)), query[Houses]
    )(_ => true, (c, p) => if p.pricing == "sane" then c.membership else p.insaneMembership)

  inline def superCustomers =
    customerMembership(
      humanCustomer(HumanType.Super("g", 1856)), querySchema[Houses]("SuperHouses")
    )(_ => true, (c, p) => if p.pricing == "sane" then c.membership else p.insaneMembership)

  inline def customersWithFiltersAndColumns(inline params: Map[String, String], inline columns: List[String]) =
    customers.filterByKeys(params).filterColumns(columns)
  inline def customersPlan(inline records: Query[Record]) =
    quote { infix"EXPLAIN VERBOSE ${records}".pure.as[Query[String]] }

  inline def superCustomersWithFiltersAndColumns(inline params: Map[String, String], inline columns: List[String]) =
    superCustomers.filterByKeys(params).filterColumns(columns)
  inline def superCustomersPlan(inline records: Query[Record]) =
    quote { infix"EXPLAIN VERBOSE ${records}".pure.as[Query[String]] }

end AdvServiceQueries
