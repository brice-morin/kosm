package org.sintef.kosm

import jet.List
import java.util.HashMap
import java.util.ArrayList


trait State {
    val name : String
    val action : StateAction
    fun setComponent(component : Component) {action.component = component}

    fun onEntry() {action.onEntry()}
    fun onExit() {action.onExit()}
}

class AtomicState(override val name : String, override val action : StateAction) : State {
    {
        println(action)
        println(this)
        action.state = this
    }
}

open class Region(val states : List<State>, val initial : State, internals : List<InternalTransition>, transitions : List<Transition>, val keepHistory : Boolean = false) {

    var current : State = initial
    val map : MutableMap<State, Map<EventType, Handler>> = HashMap();

    {
        for(s : State in states) {
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

    open fun setComponent(component : Component) {
        for(s : State in states) {
            s.setComponent(component)
        }

        for(m : Map<EventType, Handler> in map.values()) {
            for(h : Handler in m.values()) {
                h.action.component = component
            }
        }
    }

    open fun dispatch(event : Event) : Boolean {
        val handler : Handler? = map.get(current)!!.get(event.eType)//TODO: we should check guard (and improve transitions with guards, first)

        var handled : Boolean

        when(current) {
            is CompositeState -> handled = current as CompositeState dispatch(event)
            else -> handled = false
        }

        if (!handled) {
            when(handler) {
                is InternalTransition -> {
                    if (handler.check(event)/* && handler.port == event.port*/) {
                        handler.execute(event)
                        dispatch(NullEvent)//it might be an auto-transition (with no event) after this one. Not recommended for internal transition, as it is likely to be an infinite loop... but why not?
                        handled = true
                    } else {handled = false}
                }
                is Transition -> {//note: this could also be an AutoTransition, still, it is the same behavior
                    if (handler.check(event)/* && handler.port == event.port*/) {
                    current.action.onExit()
                    handler.execute(event)
                    current = handler.target
                    current.action.onEntry()
                    dispatch(NullEvent)//it might be an auto-transition (with no event) after this one
                    handled = true
                    } else {handled = false}
                }
                else -> {
                    handled = false
                }
            }
        }
        return handled
    }

}

open class CompositeState(override val action : StateAction = NullStateAction, override val name : String, val regions : List<Region> = ArrayList(), states : List<State>, initial : State, internals : List<InternalTransition>, transitions : List<Transition>, keepHistory : Boolean = false) : State, Region(states, initial, internals, transitions, keepHistory) {

    override fun setComponent(component : Component) {
        super<State>.setComponent(component)
        super<Region>.setComponent(component)
        for (r : Region in regions) {
            r.setComponent(component)
        }
    }

    override fun onEntry() {
        super<State>.onEntry()
        if (!keepHistory)
            current = initial
        current.onEntry()
        dispatch(NullEvent)
    }

    override fun onExit() {
        current.onExit()
        super<State>.onExit()
    }

    override fun dispatch(event : Event) : Boolean {
        for(r : Region in regions) {
            r.dispatch(event)
        }
        return super<Region>.dispatch(event)
    }
}

class StateMachine(action : StateAction = NullStateAction, name : String, regions : List<Region> = ArrayList(), states : List<State>, initial : State, internals : List<InternalTransition>, transitions : List<Transition>, keepHistory : Boolean = false) : CompositeState(action, name, regions, states, initial, internals, transitions, keepHistory) {

}

/*fun main(args : Array<String>) {
    println("Test")

    val s1 : State = AtomicState(action = DefaultStateAction(), name = "s1")
    val s2 : State = AtomicState(action = DefaultStateAction(), name = "s2")
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

    val inEvents : MutableList<EventType> = ArrayList()
    inEvents.add(et1)
    inEvents.add(et2)
    inEvents.add(et3)
    val outEvents : MutableList<EventType> = ArrayList()
    val p : Port = Port("p", PortType.PROVIDED, inEvents, outEvents)
    val ports : MutableMap<String, Port> = HashMap()
    ports.put(p.name, p)
    val c : Component = Component("component", ports, sm)
    c.start() //this should trigger t1
    p.receive(e1) //this should trigger it1
    p.receive(e1) //this should trigger it1
    p.receive(e2) //this should trigger nothing
    p.receive(e1) //this should trigger it1
    p.receive(e3) //this should trigger t2, and then t1 (auto-transition)

}   */