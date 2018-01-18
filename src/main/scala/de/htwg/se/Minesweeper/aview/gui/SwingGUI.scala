package de.htwg.se.Minesweeper.aview.gui

import java.awt.BorderLayout

import scala.swing._
import scala.swing.event._
import de.htwg.se.Minesweeper.controller.controllerComponent.{CellChange, ControllerInterface}
class CellClicked(val row: Int, val column: Int) extends Event

class SwingGUI (controller: ControllerInterface) extends Frame {

  listenTo(controller)

  title = "Minesweeper"
  var cells = Array.ofDim[CellPanel](controller.fieldsizey, controller.fieldsizex)


  def gridPanel = new GridPanel(controller.blockSize, controller.blockSize) {
        background = java.awt.Color.BLACK
        preferredSize= new Dimension(510,510)
        for {
          innerRow <- 0 until controller.blockSize
          innerColumn <- 0 until controller.blockSize
        } {
          val x = innerRow
          val y = innerColumn
          val cellPanel = new CellPanel(x, y, controller)
          cells(x)(y) = cellPanel
          contents += cellPanel
          listenTo(cellPanel)
    }
  }
  val statusline = new TextField(controller.statusText, 20)
  reactions +={
    case cellevent:CellChange=>redraw
  }
  val mines = new Button("Restliche Minen: "+controller.getRest)
  contents = new BorderPanel {
    add(mines,BorderPanel.Position.North)
    add(gridPanel, BorderPanel.Position.Center)
    add(statusline, BorderPanel.Position.South)
  }

  menuBar = new MenuBar {
    contents += new Menu("File") {
      mnemonic = Key.F
      contents += new MenuItem(Action("New") { controller.createRandomField()
      redraw
      })
      contents += new MenuItem(Action("Quit") { System.exit(0) })
    }
    contents += new Menu("Edit") {
      mnemonic = Key.E
      contents += new MenuItem(Action("Undo") { controller.undo
        redraw})
      contents += new MenuItem(Action("Redo") { controller.redo
        redraw})
    }
    contents += new Menu("Solve") {
      mnemonic = Key.S
      contents += new MenuItem(Action("Solve") { controller.solve
      redraw})
    }



    contents += new Menu("Options") {
      mnemonic = Key.O
      contents += new MenuItem(Action("Easy") { controller.resize(10,15) })
      contents += new MenuItem(Action("Medium") { controller.resize(12,20) })
      contents += new MenuItem(Action("Heavy") { controller.resize(15,40) })

    }

    contents += mines
  }

  visible = true
  redraw

  def resize(gridSize: Int) = {
    cells = Array.ofDim[CellPanel](controller.fieldsizex, controller.fieldsizey)
    contents = new BorderPanel {
      add(gridPanel, BorderPanel.Position.Center)
      add(statusline, BorderPanel.Position.South)
    }
  }
  def redraw = {
    for {
      row <- 0 until controller.fieldsizex
      column <- 0 until controller.fieldsizey
    } cells(row)(column).redraw
    mines.text_=("Restliche Minen:"+controller.getRest)
    statusline.text = controller.statusText
    repaint
  }
}
