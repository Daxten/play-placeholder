import play.Logger
import play.api.{Application, GlobalSettings}

import scala.reflect.io.{Directory, File}

/**
 * Created by Alexej on 05.10.2014.
 */
object Global extends GlobalSettings{
  override def onStart(app: Application) {
    Logger.info("placeholder-play starting")
    Directory("public/placeholder").jfile.listFiles.map(_.delete)
  }
}
