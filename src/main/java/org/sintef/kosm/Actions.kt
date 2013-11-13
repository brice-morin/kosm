package org.sintef.kosm

import java.util.Date

trait Action {}

trait StateAction : Action {
    fun onEntry()
    fun onExit()
}

trait HandlerAction : Action {
    fun execute()
}

//Null Actions
object NullStateAction : StateAction {

    override fun onEntry() {}

    override fun onExit() {}

}

object NullHandlerAction : HandlerAction {

    override fun execute() {}

}

//Mock-ups
object DefaultStateAction : StateAction {

    override fun onEntry() { println("onEntry at " + Date().getTime()) }

    override fun onExit() { println("onExit at " + Date().getTime()) }
}

object DefaultHandlerAction : HandlerAction {

    override fun execute() { println("transition at " + Date().getTime()) }

}