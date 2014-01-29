package org.sintef.kosm

import org.junit.Test
import kotlin.test.*
import org.junit.After
import org.junit.Before
import java.util.ArrayList

public class CompositeTest {

    var c : Component? = null
    var p : Port? = null
    var comp : CompositeState? = null
    var s1 : State? = null
    var s2 : State? = null
    var s1b : State? = null
    var s2b : State? = null

    val et1 : EventType = EventType("a")
    val et2 : EventType = EventType("b")
    val et3 : EventType = EventType("c")

    val e1 : Event = Event(et1)
    val e2 : Event = Event(et2)
    val e3 : Event = Event(et3)

    Before fun setUp() {
        s1 = AtomicState(action = DefaultStateAction(), name = "s1")
        s2 = AtomicState(action = DefaultStateAction(), name = "s2")
        val states : MutableList<State> = ArrayList()
        states.add(s1!!)
        states.add(s2!!)

        s1b = AtomicState(action = DefaultStateAction(), name = "s1b")
        s2b = AtomicState(action = DefaultStateAction(), name = "s2b")
        val states2 : MutableList<State> = ArrayList()
        states2.add(s1b!!)
        states2.add(s2b!!)

        val internals2 : MutableList<InternalTransition> = ArrayList()
        val it1b : InternalTransition = InternalTransition(state = s2b!!, event = et1, action = DefaultHandlerAction())
        internals2.add(it1b)

        val transitions2 : MutableList<Transition> = ArrayList()
        val t1b : Transition = Transition(source = s1b!!, target = s2b!!, action = DefaultHandlerAction(), event = et1)
        transitions2.add(t1b)
        val t2b : Transition = Transition(source = s2b!!, target = s1b!!, action = DefaultHandlerAction(), event = et3)
        transitions2.add(t2b)

        comp = CompositeState(action = DefaultStateAction(), states = states2, initial = s1b!!, internals = internals2, transitions = transitions2, name = "Composite Test")
        states.add(comp!!)

        val transitions : MutableList<Transition> = ArrayList()
        val t1 : Transition = AutoTransition(source = s1!!, target = comp!!, action = DefaultHandlerAction())
        transitions.add(t1)
        val t2 : Transition = Transition(source = comp!!, target = s2!!, action = DefaultHandlerAction(), event = et3)
        transitions.add(t2)


        val internals : MutableList<InternalTransition> = ArrayList()
        val it1 : InternalTransition = InternalTransition(state = s2!!, event = et1, action = DefaultHandlerAction())
        internals.add(it1)

        val sm = StateMachine(action = DefaultStateAction(), states = states, initial = s1!!, internals = internals, transitions = transitions, name = "Test")


        val inEvents : MutableList<EventType> = ArrayList()
        inEvents.add(et1)
        inEvents.add(et2)
        inEvents.add(et3)
        val outEvents : MutableList<EventType> = ArrayList()
        p = Port("p", PortType.PROVIDED, inEvents, outEvents)
        val ports : MutableList<Port> = ArrayList()
        ports.add(p!!)

        c = Component("test", ports, sm);
    }

    // override keyword required to override the tearDown method
    After fun tearDown() {
        // tear down the test case
    }

    Test fun testTransitions() {

        //SM(s1 --> Comp(s1b -et1-> s2b[et2] -et3-> s1b) -et3-> s2[et2])
        c!!.start()
        assertEquals(c!!.behavior.current, comp)
        assertEquals(comp!!.current, s1b)
        p!!.receive(e1)//transition inside Comp
        assertEquals(c!!.behavior.current, comp)

        println(comp!!.current.name)

        assertEquals(comp!!.current, s2b)
        p!!.receive(e1)//nothing
        assertEquals(c!!.behavior.current, comp)
        assertEquals(comp!!.current, s2b)
        p!!.receive(e2)//internal transition inside Comp
        assertEquals(c!!.behavior.current, comp)
        assertEquals(comp!!.current, s2b)
        p!!.receive(e1)//nothing
        assertEquals(c!!.behavior.current, comp)
        assertEquals(comp!!.current, s2b)
        p!!.receive(e3)//transition inside Comp
        assertEquals(c!!.behavior.current, comp)
        assertEquals(comp!!.current, s1b)
        p!!.receive(e3)//We should go out of Comp
        assertEquals(c!!.behavior.current, s2)
    }
}