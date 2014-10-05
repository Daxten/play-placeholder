package controllers

import java.awt.image.BufferedImage
import java.awt.{Color, Font, Graphics2D}

import play.api.Play
import play.api.Play.current
import play.api.mvc._

import scala.reflect.io.File

object Application extends Controller {
  val backgroundColor = new Color(204, 204, 204)
  val markerColor = new Color(224, 224, 224)
  val textColor = new Color(0, 0, 0)

  def index = Action { Ok(views.html.index())}

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

    val offsetX = (12 until width / 4 + 1).find(e => width % e == 0).headOption
    val offsetY = (12 until height / 4 + 1).find(e => height % e == 0).headOption
    if (offsetX.isDefined && offsetY.isDefined)
      drawMarkers(g, offsetX.get, offsetY.get, width, height, markerColor)

    if (width >= 3 && height >= 3) {
      g.drawRect(0, 0, width - 1, height - 1)
      g.drawRect(1, 1, width - 3, height - 3)
    }

    writeText(g, width, height)

    // done with drawing
    g.dispose()
    javax.imageio.ImageIO.write(canvas, "png", target.jfile)

    target
  }

  private def writeText(g: Graphics2D, width: Int, height: Int) = {
    // draw text describing placeholder
    val string = width + " x " + height
    g.setColor(textColor)
    var size = height / 3
    var stringLen = 0
    do {
      g.setFont(new Font("Batang", Font.PLAIN, size))
      stringLen = g.getFontMetrics.getStringBounds(string, g).getWidth.toInt
      size-=1
    } while(stringLen >= width)
    val stringHeight = g.getFontMetrics.getStringBounds(string, g).getHeight.toInt
    val startX = width / 2 - stringLen / 2
    val startY = height / 2 + stringHeight / 4
    g.drawString(string, startX, startY)
  }

  private def drawMarkers(g: Graphics2D, offsetX: Int, offsetY: Int, width: Int, height: Int, color: Color): Unit = {
    g.setColor(color)

    for (dx <- 0 to(width, offsetX);
         dy <- 0 to(height, offsetY))
      g.drawRect(dx, dy, offsetX, offsetY)

    g.setColor(color.darker)
  }

  private def toInt(s: String): Option[Int] = {
    try {
      Some(s.toInt)
    } catch {
      case e: Exception => None
    }
  }
}