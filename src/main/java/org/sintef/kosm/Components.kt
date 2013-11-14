package org.sintef.kosm

import java.util.ArrayList
import java.util.logging.Logger

enum class PortType {REQUIRED; PROVIDED}

class Port(val name : String, val portType : PortType, val inEvents : List<EventType>, val outEvents : List<EventType>, val connector : Connector?) {

    var component : Component? = null

    fun send(event : Event) {
        if (outEvents.containsItem(event.eType)) {
            connector?.handle(event, this)
        } else {
            Logger.getLogger(this.javaClass.getName()).warning("Port " + this.name + " cannot handle event of type " + event.eType)
        }
    }

    fun receive(event : Event) {
        if (inEvents.containsItem(event.eType)) {
            component?.receive(event, this)
        } else {
            Logger.getLogger(this.javaClass.getName()).warning("Port " + this.name + " cannot handle event of type " + event.eType)
        }
    }

    fun setComponent(component : Component) {this.component = component}

}

class Connector(val provided : Port, val required : Port) {

    fun handle(event : Event, port : Port) {
        if (port == provided)
            required.receive(event)
        else if (port == required)
            provided.receive(event)
        else
            Logger.getLogger(this.javaClass.getName()).warning("Connector cannot handle this event (" + event.eType.name + "), as it comes from a port (" + port.name + ") not connecte to this connector")
    }

}

open class Component(val name : String, ports : List<Port>, val behavior : StateMachine) {
  val providedPort : MutableList<Port> = ArrayList()
  val requiredPort : MutableList<Port> = ArrayList();

  {
      for(port : Port in ports) {
          port.setComponent(this)
          if (port.portType == PortType.REQUIRED) {
              requiredPort.add(port)
          } else {
              providedPort.add(port)
          }
      }

      behavior._setComponent(this)

  }

  fun receive(event : Event, port : Port) {
      behavior.dispatch(event)
  }

}