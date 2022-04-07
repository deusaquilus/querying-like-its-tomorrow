package deusaquilus

import net.andreinc.mockneat.MockNeat
import net.andreinc.mockneat.types.enums.RandomType
import zio.Console.printLine

import io.getquill._
import io.getquill.context.ZioJdbc.DataSourceLayer

/** Generate an aribtrarily large amount of mock-data  for database querying to examine scaling. */
object RandomData:
  val hashBasis: Long = 99823981L
  val mock            = new MockNeat(RandomType.OLD, hashBasis)
  def mockPeople(number: Int): List[Human] =
    (1 to number).map(id =>
      Human(
        id,
        mock.names.first.get,
        mock.names.last.get,
        mock.ints.range(22, 88).get,
        mock.chars.from(Array('k', 'l', 'n')).get.toString,
        mock.chars.from(Array('h', 'i', 'j')).get.toString
      )
    ).toList

  def mockHouses(number: Int, numPeople: Int): List[Houses] =
    (1 to number).map(id =>
      Houses(
        id,
        mock.ints.range(1, numPeople).get,
        mock.countries.iso2.get,
        mock.bools.probability(0.33).get
      )
    ).toList

  def fillPeopleAndHouses(): Unit =
    import QuillContext._
    val people  = mockPeople(1000)
    val houses  = mockHouses(1000, 1000)
    val runtime = zio.Runtime.unsafeFromLayer(dataSourceLayer ++ zio.Console.live)
    runtime.unsafeRun(for {
      _ <- printLine("========== Clearing Table ==========")
      _ <- run(query[Human].delete)
      _ <- run(query[Houses].delete)
      _ <- printLine("========== Inserting Values ==========")
      _ <- run(liftQuery(people).foreach(p => query[Human].insertValue(p)))
      _ <- run(liftQuery(houses).foreach(p => query[Houses].insertValue(p)))
    } yield ())

  def mockSuperPeople(number: Int): List[SuperHuman] =
    (1 to number).map(id =>
      SuperHuman(
        id,
        mock.names.first.get,
        mock.ints.range(22, 300).get,
        mock.chars.from(Array('g', 'b')).get.toString
      )
    ).toList

  // House object for SuperHouses table have unique owner constraints so just use the id
  // for that field which will ensure every id-index is an owner
  def mockSuperHouses(number: Int, numPeople: Int): List[Houses] =
    (1 to number).map(id =>
      Houses(
        id,
        id,
        mock.countries.iso2.get,
        mock.bools.probability(0.33).get
      )
    ).toList

  def fillSuperPeopleAndHouses(): Unit =
    import QuillContext._
    val people  = mockSuperPeople(1000)
    val houses  = mockSuperHouses(1000, 1000)
    val runtime = zio.Runtime.unsafeFromLayer(dataSourceLayer ++ zio.Console.live)
    runtime.unsafeRun(for {
      _ <- printLine("========== Clearing Table ==========")
      _ <- run(query[SuperHuman].delete)
      _ <- run(querySchema[Houses]("SuperHouses").delete)
      _ <- printLine("========== Inserting Values ==========")
      _ <- run(liftQuery(people).foreach(p => query[SuperHuman].insertValue(p)))
      _ <- run(liftQuery(houses).foreach(p => querySchema[Houses]("SuperHouses").insertValue(p)))
    } yield ())

  def main(args: Array[String]): Unit =
    fillSuperPeopleAndHouses()