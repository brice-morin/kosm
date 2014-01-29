package org.sintef.kosm

//import kotlin.util.measureTimeNano

trait Action{
    var state : State?
    var component : Component?
}

trait StateAction : Action {
    fun onEntry()
    fun onExit()
}

trait HandlerAction : Action {
    fun check(e : Event) = true
    fun execute()
}

//Null Actions
object NullStateAction : StateAction {

    override var state : State? = null
    override var component : Component? = null

    override fun onEntry() {}

    override fun onExit() {}

}

object NullHandlerAction : HandlerAction {

    override var state : State? = null
    override var component : Component? = null

    override fun execute() {}

}

//Debug
open class DebugStateAction() : StateAction {

    override var state : State? = null
    override var component : Component? = null

    override fun onEntry() {
        //println(state?.name + ".onEntry took: " + measureTimeNano { action.onEntry() } + " ns")
    }

    override fun onExit() {
        //println(state?.name + ".onExit took: " + measureTimeNano { action.onExit() } + " ns")
    }

}

open class DebugHandlerAction() : HandlerAction {

    override var state : State? = null
    override var component : Component? = null

    override fun execute() {
        //println("Action took: " + measureTimeNano { action.execute() } + " ns")
    }

}

//Mock-ups
class DefaultStateAction() : DebugStateAction() {}

class DefaultHandlerAction() : DebugHandlerAction() {}