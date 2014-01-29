package org.sintef.kosm

import org.junit.Test
import kotlin.test.*
import org.junit.After
import org.junit.Before
import java.util.ArrayList

public class HelloTest {

    var c : Component? = null
    var p : Port? = null
    var sm : StateMachine? = null
    var s1 : State? = null
    var s2 : State? = null

    Before fun setUp() {
        s1 = AtomicState(action = DefaultStateAction(), name = "s1")
        s2 = AtomicState(action = DefaultStateAction(), name = "s2")
        val states : MutableList<State> = ArrayList()
        states.add(s1!!)
        states.add(s2!!)

        val transitions : MutableList<Transition> = ArrayList()
        val t1 : Transition = Transition(source = s1!!, target = s2!!, action = HelloTransitionAction, event = HelloEventType)
        transitions.add(t1)

        val internals : MutableList<InternalTransition> = ArrayList()

        sm = StateMachine(action = DefaultStateAction(), states = states, initial = s1!!, internals = internals, transitions = transitions, name = "Test")

        val inEvents : MutableList<EventType> = ArrayList()
        inEvents.add(HelloEventType)
        val outEvents : MutableList<EventType> = ArrayList()
        p = Port("hello", PortType.PROVIDED, inEvents, outEvents)
        val ports : MutableList<Port> = ArrayList()
        ports.add(p!!)

        c = Component("HelloWorld", ports, sm!!);
    }

    // override keyword required to override the tearDown method
    After fun tearDown() {
        // tear down the test case
    }

    Test fun testHello() {
        c!!.start()
        assertEquals(sm!!.current, s1)
        p!!.receive(HelloEvent("world"))
        assertEquals(sm!!.current, s2)
    }
}