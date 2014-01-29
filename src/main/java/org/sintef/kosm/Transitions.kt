package org.sintef.kosm

abstract class Handler(val action : HandlerAction, val event : EventType) {
    fun check(e : Event) = action.check(e)
    fun execute(e : Event) = action.execute(e)
}

open class Transition(val source : State, val target : State, action : HandlerAction, event : EventType) : Handler(action, event) {

}

class AutoTransition (source : State, target : State, action : HandlerAction) : Transition(source, target, action, NullEventType) {

}

class InternalTransition(val state : State, action : HandlerAction, event : EventType) : Handler(action, event) {

}