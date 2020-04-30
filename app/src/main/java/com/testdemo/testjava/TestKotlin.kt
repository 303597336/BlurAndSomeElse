package com.testdemo.testjava

open class TestKotlin {
    fun getString(): Int {
        return this.hashCode()
    }

    //想被继承必须使用open，除了接口（及其方法）
    open fun printThing() {
        println("Test")
    }

    fun <R> myLet(sign: Int, block: (Int) -> R): R = block(sign)

    /*fun canNoParam() {//会优先调用这个函数而不是有默认值的带参函数
        println("canNoParam")
    }*/
    @JvmOverloads
    fun canNoParam(param: String = "yes", param2: Int, param3: Boolean) {
        println("canNoParam param = $param")
        printMy()
    }
}

class SunTest : TestKotlin {
    constructor(n: String)

    override fun printThing() {
        println("SunTest")
    }
}

//扩展方法
fun TestKotlin.addMethod(param: String) {
    println("addition receive param = $param")
}

//扩展变量
val SunTest.addition: String
    get() = "la-add"//扩展变量不能直接初始化，必须通过getter

fun hasPrefix(x: Any) = when (x) {
    is String -> x.startsWith("prefix")
    else -> false
}

fun foo() {
    var ints = intArrayOf(1, 4, 5, 2)
    ints.forEach {
        if (it == 5) return@forEach
        println(it)
    }
    println("fun forEach has not been forced to return")
}

fun getRunnable() : Runnable {
    return Runnable { TODO("not implemented") }
}

fun main(args: Array<String>) {
    var a = 1
    val s1 = "a is $a"
    a = 2
    val s2 = "${s1.replace("is", "was")}, but now is $a"
    println("$s2 - ${hasPrefix("prefixcc")}")

    foo()

    val testInstance = TestKotlin()
    println("Test's code = ${TestKotlin().getString()}\nTest's canNoParam invoke:")
    testInstance.canNoParam(param2 = 0, param3 = false)
    testInstance.canNoParam("", 0, true)

    var test = SunTest(s1)
    test.printThing()
    test.addMethod("addadd")
    println("扩展的变量：${test.addition}")
    println(test.myLet(111) { println("自己定义的Lambda表达式，参数为：$it"); return@myLet "no" })

    var testpp = 1
    println("-p = ${-testpp}, unary = ${testpp.unaryMinus()}")

    val testJava = TestJava()
    testJava.setOnClickListener {  }
    testJava.setOnClickListener { println(it) }
    var testJavaResult: String
    testJavaResult = testJava.getInstance(1)
    println("now the testJavaResult is $testJavaResult")
}