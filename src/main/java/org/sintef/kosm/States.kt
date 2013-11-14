package org.sintef.kosm

import jet.List
import java.util.HashMap
import java.util.ArrayList


trait StateT : StateAction {
    val action : StateAction
    val name : String

    override fun onEntry() { action.onEntry() }
    override fun onExit() { action.onExit() }
    override fun setContext(state : StateT) { action.setContext(this) }
}

class State(name : String, action : StateAction = NullStateAction) : StateT {
    override val action : StateAction = action
    override val name : String = name
    {setContext(this)}
}

open class Region(val states : List<StateT>, val initial : StateT, val internals : List<InternalTransition>, val transitions : List<Transition>, val keepHistory : Boolean = false) {

    var current : StateT = initial
    val map : MutableMap<StateT, Map<EventType, Handler>> = HashMap();

    {
        for(s : StateT in states) {
            val tmap : MutableMap<EventType, Handler> = HashMap()
            map.put(s, tmap)

            //we first check if an event can trigger a transition for the current state.
            @tloop for(t : Transition in transitions) {
                if (t.source == s) {
                    tmap.put(t.event, t)
                    break@tloop
                }
            }

            //we then check if an event can trigger an internal transition, thus possibly overriding a normal transition
            @iloop for(it : InternalTransition in internals) {
                if (it.state == s) {
                    tmap.put(it.event, it)
                    break@iloop
                }
            }
        }
    }

    open fun dispatch(event : Event) {
        val handler : Handler? = map.get(current)!!.get(event.eType)//TODO: we should check guard (and improve transitions with guards, first)
        when(handler) {
            is InternalTransition -> {
                handler.execute()
                dispatch(NullEvent)//it might be an auto-transition (with no event) after this one. Not recommended for internal transition, as it is likely to be an infinite loop... but why not?
            }
            is Transition -> {
                current.onExit()
                handler.execute()
                current = handler.target
                current.onEntry()
                dispatch(NullEvent)//it might be an auto-transition (with no event) after this one
            }
            else -> {
                //nothing to do
            }
        }
    }

}

open class CompositeState(action : StateAction = NullStateAction, name : String, val regions : List<Region> = ArrayList(), states : List<StateT>, initial : State, internals : List<InternalTransition>, transitions : List<Transition>, keepHistory : Boolean = false) : StateT, Region(states, initial, internals, transitions, keepHistory) {

    override val action : StateAction = action
    override val name : String = name
    {setContext(this)}

    override fun onEntry() {
        super<StateT>.onEntry()
        if (!keepHistory)
            current = initial
        println("current.name = " + current.name)
        current.onEntry()
    }

    override fun onExit() {
        current.onExit()
        super<StateT>.onExit()
    }

    override fun dispatch(event : Event) {
        super<Region>.dispatch(event)
        for(r : Region in regions) {
          r.dispatch(event)
        }
    }
}

class StateMachine(action : StateAction = NullStateAction, name : String, regions : List<Region> = ArrayList(), states : List<StateT>, initial : State, internals : List<InternalTransition>, transitions : List<Transition>, keepHistory : Boolean = false) : CompositeState(action, name, regions, states, initial, internals, transitions, keepHistory) {

   override fun onEntry() {
        super<CompositeState>.onEntry()
        dispatch(NullEvent)
    }

}

fun main(args : Array<String>) {
    println("Test")

    val s1 : State = State(action = DefaultStateAction(), name = "s1")
    val s2 : State = State(action = DefaultStateAction(), name = "s2")
    val states : MutableList<State> = ArrayList()
    states.add(s1)
    states.add(s2)

    val et1 : EventType = EventType("a")
    val et2 : EventType = EventType("b")
    val et3 : EventType = EventType("c")

    val e1 : Event = Event(et1)
    val e2 : Event = Event(et2)
    val e3 : Event = Event(et3)

    val transitions : MutableList<Transition> = ArrayList()
    val t1 : Transition = AutoTransition(source = s1, target = s2, action = DefaultHandlerAction())
    transitions.add(t1)
    val t2 : Transition = Transition(source = s2, target = s1, action = DefaultHandlerAction(), event = et3)
    transitions.add(t2)

    val internals : MutableList<InternalTransition> = ArrayList()
    val it1 : InternalTransition = InternalTransition(state = s2, event = et2, action = DefaultHandlerAction())
    internals.add(it1)

    val sm : StateMachine = StateMachine(action = DefaultStateAction(), states = states, initial = s1, internals = internals, transitions = transitions, name = "State Machine")
    sm.onEntry()//this should trigger t1
    sm.dispatch(e1)//this should trigger it1
    sm.dispatch(e1)//this should trigger it1
    sm.dispatch(e2)//this should trigger nothing
    sm.dispatch(e1)//this should trigger it1
    sm.dispatch(e3)//this should trigger t2, and then t1 (auto-transition)


}