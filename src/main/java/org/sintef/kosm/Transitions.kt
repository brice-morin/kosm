package org.sintef.kosm

abstract class Handler(val action : HandlerAction = NullHandlerAction, val event : EventType) : HandlerAction /*by action*/ {
    override fun execute() = action.execute()
}

open class Transition(val source : StateT, val target : StateT, action : HandlerAction = NullHandlerAction, event : EventType) : Handler(action, event) {

}

class AutoTransition (source : StateT, target : StateT, action : HandlerAction = NullHandlerAction) : Transition(source, target, action, NullEventType) {

}

class InternalTransition(val state : StateT, action : HandlerAction = NullHandlerAction, event : EventType) : Handler(action, event) {

}