package com.krihl4n

import java.beans.Expression
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket

//fun main() {
//
//    val server = ServerSocket(9999)
//    val socket = server.accept()
//
//    val game = GameCoordinator()
//    val playerOne = InMemoryPlayer()
//    val playerTwo = InMemoryPlayer()
//
//    game.registerPlayer(playerOne)
//    game.registerPlayer(playerTwo)
//
//    game.startGame()
//
//    game.move(playerOne.id!!, "a2","a3")
//}

class InMemoryPlayer : Player {

    var id: String? = null

    override fun idAssigned(id: String) {
        this.id = id
    }

    override fun notifyPlayersTurn() {
        TODO("Not yet implemented")
    }
}

var game: GameCoordinator? = null
var playerOne: InMemoryPlayer? = null

fun main() {
    Thread { server() }.start()

    game = GameCoordinator()
    playerOne = InMemoryPlayer()
    val playerTwo = InMemoryPlayer()

    game!!.registerPlayer(playerOne!!)
    game!!.registerPlayer(playerTwo)

    game!!.startGame()

   // game!!.move(playerOne.id!!, "a2","a3")
}

private fun move(expression: String) {
    val fields = expression.split(" ")
    game!!.move(playerOne?.id!!, fields[0], fields[1])
}

//https://www.baeldung.com/websockets-spring

fun server() {
    val server = ServerSocket(9998)
    val client = server.accept()
    val output = PrintWriter(client.getOutputStream(), true)
    val input = BufferedReader(InputStreamReader(client.getInputStream()))

    var inputLine = input.readLine()
    while(inputLine != null) {
        //println("[received]: $inputLine")
        output.println("echo $inputLine")
        move(inputLine)
        inputLine = input.readLine()
    }
}
