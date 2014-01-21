package org.sintef.kosm

import kotlin.util.measureTimeNano

trait Action {}

trait StateAction : Action {
    fun onEntry()
    fun onExit()
    fun setContext(state : StateT)
}

trait HandlerAction : Action {
    fun execute()
}

//Null Actions
object NullStateAction : StateAction {

    override fun onEntry() {}

    override fun onExit() {}

    override fun setContext(state : StateT) {}

}

object NullHandlerAction : HandlerAction {

    override fun execute() {}

}

//Debug
open class DebugStateAction(val action : StateAction) : StateAction by action {

    var state : StateT? = null

    override fun setContext(state : StateT) { this.state = state }

    override fun onEntry() {
        println(state?.name + ".onEntry took: " + measureTimeNano { action.onEntry() } + " ns")
    }

    override fun onExit() {
        println(state?.name + ".onExit took: " + measureTimeNano { action.onExit() } + " ns")
    }

}

open class DebugHandlerAction(val action : HandlerAction) : HandlerAction by action {

    override fun execute() {
        println("Action took: " + measureTimeNano { action.execute() } + " ns")
    }

}

//Mock-ups
class DefaultStateAction : DebugStateAction(NullStateAction) {}

class DefaultHandlerAction : DebugHandlerAction(NullHandlerAction) {}