package org.sintef.kosm

import java.util.Date

open class EventType(val name : String)

object DefaultEventType : EventType("Default")

object NullEventType : EventType("Null")


open class Event(val eType : EventType, val timestamp : Long? = Date().getTime())

object NullEvent : Event(NullEventType)