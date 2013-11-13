package org.sintef.kosm

import jet.List
import java.util.HashMap
import java.util.ArrayList


trait StateT : StateAction {
    val action : StateAction?

    override fun onEntry() { action?.onEntry() }
    override fun onExit() { action?.onExit() }
}

class State(action : StateAction = NullStateAction) : StateT {
    override val action : StateAction = action
}

open class Region(val states : List<State>, val initial : State, val internals : List<InternalTransition>, val transitions : List<Transition>, val keepHistory : Boolean = false) {

    var current : State = initial
    val map : MutableMap<State, Map<String?, Handler>> = HashMap();

    {
        for(s : State in states) {
            val tmap : MutableMap<String?, Handler> = HashMap()
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

    open fun dispatch(event : String?) {
        val handler : Handler? = map.get(current)!!.get(event)//TODO: we should check guard (and improve transitions with guards, first)
        when(handler) {
            is InternalTransition -> {
                handler.execute()
                dispatch(null)//it might be an auto-transition (with no event) after this one. Not recommended for internal transition, as it is likely to be an infinite loop... but why not?
            }
            is Transition -> {
                current.onExit()
                handler.execute()
                current = handler.target
                current.onEntry()
                dispatch(null)//it might be an auto-transition (with no event) after this one
            }
            else -> {
                //nothing to do
            }
        }
    }

}

open class CompositeState(action : StateAction = NullStateAction, val regions : List<Region> = ArrayList(), states : List<State>, initial : State, internals : List<InternalTransition>, transitions : List<Transition>, keepHistory : Boolean = false) : StateT, Region(states, initial, internals, transitions, keepHistory) {


    override val action : StateAction = action

    override fun onEntry() {
        super<StateT>.onEntry()
        if (!keepHistory)
            current = initial
        current.onEntry()
    }

    override fun onExit() {
        current.onExit()
        super<StateT>.onExit()
    }

    override fun dispatch(event : String?) {
        super<Region>.dispatch(event)
        for(r : Region in regions) {
          r.dispatch(event)
        }
    }
}

class StateMachine(action : StateAction = NullStateAction, regions : List<Region> = ArrayList(), states : List<State>, initial : State, internals : List<InternalTransition>, transitions : List<Transition>, keepHistory : Boolean = false) : CompositeState(action, regions, states, initial, internals, transitions, keepHistory) {

    override val action : StateAction = action

}

fun main(args : Array<String>) {
    println("Test")

    val s1 : State = State(action = DefaultStateAction)
    val s2 : State = State(action = DefaultStateAction)
    val states : MutableList<State> = ArrayList()
    states.add(s1)
    states.add(s2)

    val transitions : MutableList<Transition> = ArrayList()
    val t1 : Transition = Transition(source = s1, target = s2, action = DefaultHandlerAction)
    transitions.add(t1)
    val t2 : Transition = Transition(source = s2, target = s1, action = DefaultHandlerAction, event = "c")
    transitions.add(t2)

    val internals : MutableList<InternalTransition> = ArrayList()
    val it1 : InternalTransition = InternalTransition(state = s2, event = "a", action = DefaultHandlerAction)
    internals.add(it1)

    val sm : StateMachine = StateMachine(action = DefaultStateAction, states = states, initial = s1, internals = internals, transitions = transitions)
    sm.onEntry()
    sm.dispatch(null)//this should trigger t1
    sm.dispatch("a")//this should trigger it1
    sm.dispatch("a")//this should trigger it1
    sm.dispatch("b")//this should trigger nothing
    sm.dispatch("a")//this should trigger it1
    sm.dispatch("c")////this should trigger t2, and then t1 (auto-transition)
}