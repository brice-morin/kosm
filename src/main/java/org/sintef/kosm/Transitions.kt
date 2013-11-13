package org.sintef.kosm

abstract class Handler(val action : HandlerAction = NullHandlerAction, val event : String? = null) : HandlerAction by action {//TODO: reify event as a class

}

class Transition(val source : State, val target : State, action : HandlerAction = NullHandlerAction, event : String? = null) : Handler(action, event) {

}

class InternalTransition(val state : State, action : HandlerAction = NullHandlerAction, event : String? = null) : Handler(action, event) {

}