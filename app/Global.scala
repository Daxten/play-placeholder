import java.io.File

import play.Logger
import play.api.{Application, GlobalSettings}


/**
 * Created by Alexej on 05.10.2014.
 */
object Global extends GlobalSettings {
  override def onStart(app: Application) {
    Logger.info("placeholder-play starting")
    val dir = new File("public/placeholder")
    dir.mkdirs
    dir.listFiles.map(_.delete)
  }
}
