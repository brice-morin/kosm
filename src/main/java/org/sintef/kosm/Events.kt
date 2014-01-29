package org.sintef.kosm

//import java.util.Date

open class EventType(name : String)

object NullEventType : EventType("Null")




open class Event(val eType : EventType, var port : Port? = null, val timestamp : Long? = 0/*Date().getTime()*/)

object NullEvent : Event(NullEventType)

//Things to generate from ThingML
//for all messages e.g. hello(who : String) create the proper EventType and Event
object HelloEventType : EventType(name = "hello")
class HelloEvent(val who : String) : Event(eType = HelloEventType)