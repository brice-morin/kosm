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
    fun execute(e : Event)
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

    override fun execute(e : Event) {}

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

    override fun execute(e : Event) {
        //println("Action took: " + measureTimeNano { action.execute() } + " ns")
    }

}

//Mock-ups
class DefaultStateAction() : DebugStateAction() {}

class DefaultHandlerAction() : DebugHandlerAction() {}



//Things to generate from ThingML
object HelloTransitionAction : HandlerAction {

    override var state : State? = null
    override var component : Component? = null

    override fun check(e: Event): Boolean {
        println("DEBUG")
        println(e is HelloEvent && e.port == component!!.ports.get("hello") )
        return e is HelloEvent && e.port == component!!.ports.get("hello") //&& <custom guard>
    }

    override fun execute(e : Event) {
        val e = e as HelloEvent
        println("Hello " + e.who)
    }

}

object S1Action : StateAction {

    override var state: State? = null
    override var component: Component? = null

    override fun onEntry() {
        println("s1.onEntry")
    }

    override fun onExit() {
        println("s1.onExit")
    }

}

object S2Action : StateAction {

    override var state: State? = null
    override var component: Component? = null

    override fun onEntry() {
        println("s2.onEntry")
    }

    override fun onExit() {
        println("s2.onExit")
    }

}