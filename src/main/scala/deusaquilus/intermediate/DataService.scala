package deusaquilus.intermediate

import zio.IO
import java.sql.SQLException
import javax.sql.DataSource
import zio.Console.printLine
import io.getquill._

import deusaquilus.QuillContext._
import io.getquill.context.ZioJdbc.DataSourceLayer
import zio._
import deusaquilus.Record
import deusaquilus.ServiceQueries._

trait DataService:
  def getCustomers(params: Map[String, String], columns: List[String]): IO[SQLException, List[Record]]

object DataService:
  val live = (DataServiceLive.apply _).toLayer[DataService]

final case class DataServiceLive(ds: DataSource) extends DataService:
  def getCustomers(params: Map[String, String], columns: List[String]): IO[SQLException, List[Record]] =
    run(
      customerMembership {
        humanCustomer(HumanType.Regular("h", 1982))
        ++
        humanCustomer(HumanType.Super("g", 1856))
      }(_ => true, (c, p) => if p.pricing == "sane" then c.membership else p.insaneMembership)
        .filterByKeys(params)
        .filterColumns(columns)
    ).provideService(ds)
