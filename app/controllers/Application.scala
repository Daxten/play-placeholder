package controllers

import java.awt.{Graphics2D, Font, Color}
import java.awt.image.BufferedImage
import play.api.Play.current
import play.api.{Play, Logger}
import play.api.mvc._
import scala.reflect.io.File

object Application extends Controller {
  val backgroundColor = new Color(204, 204, 204)
  val markerColor = new Color(224, 224, 224)
  val textColor = new Color(0, 0, 0)

  def getPlaceholder(size: String) = Action {
    val split = size.split('x')

    if (split.length != 2) BadRequest("Format: <width>x<height>, example: 150x120")
    else {
      val x = Math.min(1920, toInt(split(0)).getOrElse(0))
      val y = Math.min(1080, toInt(split(1)).getOrElse(0))

      val target = File(s"public/placeholder/$size.png")
      if (!target.exists || Play.isDev) createPlaceholder(size, x, y)
      Ok.sendFile(target.jfile, inline = true)
    }
  }

  private def createPlaceholder(size: String, width: Int, height: Int) = {
    val target = File(s"public/placeholder/$size.png")
    target.parent.createDirectory()
    target.createFile()

    val canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
    val g = canvas.createGraphics()

    // clear background
    g.setColor(backgroundColor)
    g.fillRect(0, 0, width, height)

    // enable anti-aliased rendering (prettier lines and circles)
    g.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
      java.awt.RenderingHints.VALUE_ANTIALIAS_ON)

    drawMarkers(g, width, height)

    // draw text describing placeholder
    val string = width + " x " + height
    g.setColor(textColor)
    g.setFont(new Font("Batang", Font.PLAIN, height / 4))
    val stringLen = g.getFontMetrics.getStringBounds(string, g).getWidth.toInt
    val stringHeight = g.getFontMetrics.getStringBounds(string, g).getHeight.toInt
    val startX = width/2 - stringLen/2
    val startY = height/2 + stringHeight/4
    g.drawString(string, startX, startY)

    // done with drawing
    g.dispose()
    javax.imageio.ImageIO.write(canvas, "png", target.jfile)

    target
  }

  private def drawMarkers(g: Graphics2D, width: Int, height: Int) = {
    val offset = Seq(12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1).find(e => width % e == 0 && height % e == 0).head

    val lineLength = Math.min(height / offset, width / offset)
    g.setColor(markerColor)

    for (dx <- offset to (width - offset, offset)) {
      g.drawLine(dx, 0, dx, lineLength)
      g.drawLine(dx, height, dx, height - lineLength)
    }

    for (dy <- offset to (height - offset, offset)) {
      g.drawLine(0, dy, lineLength, dy)
      g.drawLine(width, dy, width - lineLength, dy)
    }
  }

  private def toInt(s: String):Option[Int] = {
    try {
      Some(s.toInt)
    } catch {
      case e:Exception => None
    }
  }
}