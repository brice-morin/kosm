package org.sintef.kosm

//import java.util.Date

open class EventType(val name : String)

object NullEventType : EventType("Null")


open class Event(val eType : EventType, var port : Port? = null, val timestamp : Long? = 0/*Date().getTime()*/)

object NullEvent : Event(NullEventType)