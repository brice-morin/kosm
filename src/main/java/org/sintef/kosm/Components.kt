package org.sintef.kosm

import java.util.ArrayList
import java.util.HashMap

//import java.util.logging.Logger

enum class PortType {REQUIRED; PROVIDED}

class Port(val name : String, val portType : PortType, val inEvents : List<EventType>, val outEvents : List<EventType>) {

    var component : Component? = null
    var connector : Connector? = null

    fun send(event : Event) {
        if (outEvents.containsItem(event.eType)) {
            connector?.handle(event, this)
        } else {
            //Logger.getLogger(this.javaClass.getName()).warning("Port " + this.name + " cannot handle event of type " + event.eType)
        }
    }

    fun receive(event : Event) {
        if (inEvents.containsItem(event.eType)) {
            event.port = this
            component?.receive(event)
        } else {
            //Logger.getLogger(this.javaClass.getName()).warning("Port " + this.name + " cannot handle event of type " + event.eType)
        }
    }
}

class Connector(val provided : Port, val required : Port) {

    {
        if(provided.portType != required.portType) {
            provided.connector = this
            required.connector = this
        } else {
            throw Exception("Connector should connect a provided port to a required port")
        }
    }

    fun handle(event : Event, port : Port) {
        if (port == provided)
            required.receive(event)
        else if (port == required)
            provided.receive(event)
        //else
            //Logger.getLogger(this.javaClass.getName()).warning("Connector cannot handle this event (" + event.eType.name + "), as it comes from a port (" + port.name + ") not connecte to this connector")
    }

}

open class Component(val name : String, val ports : Map<String, Port>, val behavior : StateMachine) {

  {
      for(port : Port in ports.values()) {//Bug in JS, see http://youtrack.jetbrains.com/issue/KT-4499
          port.component = this
      }

      behavior.setComponent(this)
  }

  fun receive(event : Event) {
      println("received: " + event)
      behavior.dispatch(event)
  }

  fun start() {
      behavior.onEntry()
  }

}

//Things to generate from ThingML

trait IStuff {
    fun foo()
    fun bar(foobar : String)
}

object MyThingBuilder {

    fun build(p : String, q : Int) : MyThing {

        val ports : MutableMap<String, Port> = HashMap()
        //for each port
        val inEvents : MutableList<EventType> = ArrayList()
        inEvents.add(HelloEventType)
        val outEvents : MutableList<EventType> = ArrayList()
        val port1 : Port = Port("p1", PortType.PROVIDED, outEvents, inEvents)
        ports.put(port1.name, port1)
        val port2 : Port = Port("p2", PortType.REQUIRED, inEvents, outEvents)
        ports.put(port2.name, port2)


        val states : MutableList<State> = ArrayList()
        //for each state (recursive for regions/composite)
        val s1 : State = AtomicState(action = DefaultStateAction(), name = "s1")
        states.add(s1)
        val s2 : State = AtomicState(action = DefaultStateAction(), name = "s2")
        states.add(s2)

        val transitions : MutableList<Transition> = ArrayList()
        val internals : MutableList<InternalTransition> = ArrayList()
        //for each transition inside the current region
        val t1 : Transition = AutoTransition(source = s1, target = s2, action = DefaultHandlerAction())
        transitions.add(t1)
        val t2 : Transition = Transition(source = s2, target = s1, action = DefaultHandlerAction(), event = HelloEventType)
        transitions.add(t2)

        val it1 : InternalTransition = InternalTransition(state = s2, event = HelloEventType, action = DefaultHandlerAction())
        internals.add(it1)

        val sm : StateMachine = StateMachine(action = DefaultStateAction(), states = states, initial = s1, internals = internals, transitions = transitions, name = "State Machine")

        return MyThing(ports, sm, p, q)
    }
}
class MyThing(ports : Map<String, Port>, behavior : StateMachine, /*(readonly/var) properties*/ val p : String, var q : Int) : Component("myThing", ports, behavior),  IStuff {

    override fun foo() {

    }
    override fun bar(foobar: String) {

    }

}

fun main(args : Array<String>) {
    val myThing = MyThingBuilder.build("abc", 0)
    val myThing2 = MyThingBuilder.build("xyz", 1)

    println(myThing.ports.get("p1")!!)
    println(myThing2.ports.get("p2")!!)

    val c1 = Connector(myThing.ports.get("p1")!!, myThing2.ports.get("p2")!!)

    myThing.start()
    myThing2.start()

    myThing.ports.get("p1")!!.send(HelloEvent("world"))
}