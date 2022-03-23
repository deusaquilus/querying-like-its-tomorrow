package deusaquilus

import io.getquill._
import io.getquill.context.ZioJdbc.DataSourceLayer
import zio._
import javax.sql.DataSource

object QuillContext extends PostgresZioJdbcContext(Literal):
  val dataSourceLayer: ULayer[DataSource] =
    DataSourceLayer.fromPrefix("database").orDie
