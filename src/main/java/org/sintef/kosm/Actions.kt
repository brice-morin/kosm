package org.sintef.kosm

import java.util.Date

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
        println("before " + state?.name + " onEntry [" + Date().getTime() + "]")
        action.onEntry()
        println("after " + state?.name + " onEntry [" + Date().getTime() + "]")
    }

    override fun onExit() {
        println("before " + state?.name + " onExit [" + Date().getTime() + "]")
        action.onExit()
        println("after " + state?.name + " onExit [" + Date().getTime() + "]")
    }

}

open class DebugHandlerAction(val action : HandlerAction) : HandlerAction by action {

    override fun execute() {
        println("before execute [" + Date().getTime() + "]")
        action.execute()
        println("after execute [" + Date().getTime() + "]")
    }

}

//Mock-ups
class DefaultStateAction : DebugStateAction(NullStateAction) {}

class DefaultHandlerAction : DebugHandlerAction(NullHandlerAction) {}