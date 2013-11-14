package org.sintef.kosm

import org.junit.Test
import kotlin.test.*
import org.junit.After
import org.junit.Before
import java.util.ArrayList

public class HistoryCompositeTest2 {

    var sm : StateMachine? = null
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
        s1 = State(action = DefaultStateAction(), name = "s1")
        s2 = State(action = DefaultStateAction(), name = "s2")
        val states : MutableList<StateT> = ArrayList()
        states.add(s1!!)
        states.add(s2!!)

        s1b = State(action = DefaultStateAction(), name = "s1b")
        s2b = State(action = DefaultStateAction(), name = "s2b")
        val states2 : MutableList<StateT> = ArrayList()
        states2.add(s1b!!)
        states2.add(s2b!!)

        val internals2 : MutableList<InternalTransition> = ArrayList()
        /*val it1b : InternalTransition = InternalTransition(state = s2b!!, event = et3, action = DefaultHandlerAction())
        internals2.add(it1b)*/

        val transitions2 : MutableList<Transition> = ArrayList()
        val t1b : Transition = Transition(source = s1b!!, target = s2b!!, action = DefaultHandlerAction(), event = et1)
        transitions2.add(t1b)
        val t2b : Transition = Transition(source = s2b!!, target = s1b!!, action = DefaultHandlerAction(), event = et2)
        transitions2.add(t2b)

        comp = CompositeState(action = DefaultStateAction(), states = states2, initial = s1b!!, internals = internals2, transitions = transitions2, name = "Composite Test", keepHistory = false)
        states.add(comp!!)

        val transitions : MutableList<Transition> = ArrayList()
        val t1 : Transition = AutoTransition(source = s1!!, target = comp!!, action = DefaultHandlerAction())
        transitions.add(t1)
        val t2 : Transition = Transition(source = comp!!, target = s2!!, action = DefaultHandlerAction(), event = et3)
        transitions.add(t2)
        val t3 : Transition = Transition(source = s2!!, target = comp!!, action = DefaultHandlerAction(), event = et2)
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

        //SM(s1 --> Comp(s1b -et1-> s2b[et2] -et2-> s1b) -et3-> s2[et2] -et2-> Comp)

        sm!!.onEntry()////this should trigger t1
        assertEquals(sm!!.current, comp)
        assertEquals(comp!!.current, s1b)
        sm!!.dispatch(e1)//transition inside Comp
        assertEquals(sm!!.current, comp)
        assertEquals(comp!!.current, s2b)
        sm!!.dispatch(e1)//nothing
        assertEquals(sm!!.current, comp)
        assertEquals(comp!!.current, s2b)
        sm!!.dispatch(e2)//transition inside Comp
        assertEquals(sm!!.current, comp)
        assertEquals(comp!!.current, s1b)
        sm!!.dispatch(e1)//transition inside Comp
        assertEquals(sm!!.current, comp)
        assertEquals(comp!!.current, s2b)
        sm!!.dispatch(e3)//go out of Comp
        assertEquals(sm!!.current, s2)
        assertEquals(comp!!.current, s2b)
        sm!!.dispatch(e2)//back to Comp
        assertEquals(sm!!.current, comp)
        assertEquals(comp!!.current, s1b)
    }
}