package org.sintef.kosm

abstract class Handler(val action : HandlerAction = NullHandlerAction, val event : EventType) : HandlerAction by action {

}

open class Transition(val source : State, val target : State, action : HandlerAction = NullHandlerAction, event : EventType) : Handler(action, event) {

}

class AutoTransition (source : State, target : State, action : HandlerAction = NullHandlerAction) : Transition(source, target, action, NullEventType) {

}

class InternalTransition(val state : State, action : HandlerAction = NullHandlerAction, event : EventType) : Handler(action, event) {

}