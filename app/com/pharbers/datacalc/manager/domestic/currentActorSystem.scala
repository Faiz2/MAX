package com.pharbers.datacalc.manager.domestic

import akka.actor.ActorSystem


object currentActorSystem {
    def apply() = ActorSystem("sys")
}