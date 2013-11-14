package org.sintef.kosm

import org.junit.Test
import kotlin.test.*
import org.junit.After
import org.junit.Before
import java.util.ArrayList

public class SimpleTransitionTest {

    var sm : StateMachine? = null
    var s1 : State? = null
    var s2 : State? = null

    val et1 : EventType = EventType("a")
    val et2 : EventType = EventType("b")
    val et3 : EventType = EventType("c")

    val e1 : Event = Event(et1)
    val e2 : Event = Event(et2)
    val e3 : Event = Event(et3)

    Before fun setUp() {
        s1 = State(action = DefaultStateAction(), name = "s1")
        s2 = State(action = DefaultStateAction(), name = "s2")
        val states : MutableList<State> = ArrayList()
        states.add(s1!!)
        states.add(s2!!)

        val transitions : MutableList<Transition> = ArrayList()
        val t1 : Transition = AutoTransition(source = s1!!, target = s2!!, action = DefaultHandlerAction())
        transitions.add(t1)
        val t2 : Transition = Transition(source = s2!!, target = s1!!, action = DefaultHandlerAction(), event = et3)
        transitions.add(t2)
        val t3 : Transition = Transition(source = s2!!, target = s1!!, action = DefaultHandlerAction(), event = et1)
        transitions.add(t3)

        val internals : MutableList<InternalTransition> = ArrayList()
        val it1 : InternalTransition = InternalTransition(state = s2!!, event = et1, action = DefaultHandlerAction())
        internals.add(it1)

        sm = StateMachine(action = DefaultStateAction(), states = states, initial = s1!!, internals = internals, transitions = transitions, name = "Test")
    }

    // override keyword required to override the tearDown method
    After fun tearDown() {
        // tear down the test case
    }

    Test fun testTransitions() {
        sm!!.onEntry()////this should trigger t1
        assertEquals(sm!!.current, s2)
        sm!!.dispatch(e1)//this should trigger it1
        assertEquals(sm!!.current, s2)
        sm!!.dispatch(e1)//this should trigger it1
        assertEquals(sm!!.current, s2)
        sm!!.dispatch(e2)//this should trigger nothing
        assertEquals(sm!!.current, s2)
        sm!!.dispatch(e1)//this should trigger it1
        assertEquals(sm!!.current, s2)
        sm!!.dispatch(e3)////this should trigger t2, and then t1 (auto-transition)
        assertEquals(sm!!.current, s2)
    }
}