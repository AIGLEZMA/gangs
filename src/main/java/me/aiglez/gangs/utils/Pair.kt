package me.aiglez.gangs.utils

class Pair<A, B>(val first : A, val second : B) {

    companion object {
        @JvmStatic
        fun <Z, Q> of(first : Z, second : Q) : Pair<Z, Q> {
            return Pair(first, second)
        }
    }


}