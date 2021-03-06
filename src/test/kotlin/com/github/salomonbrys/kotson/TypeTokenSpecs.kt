package com.github.salomonbrys.kotson

import com.google.gson.Gson
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import java.lang.reflect.ParameterizedType
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class TypeTokenSpecs : Spek ({

    given("a non generic type") {
        on("typeToken") {
            it("should give the type's class") {
                assertEquals(String::class.java, typeToken<String>())
            }
        }
    }

    given("a one parameter Any generic type") {

        on("wildcard typeToken") {
            it("should give the type's class") {
                assertEquals(List::class.java, typeToken<List<*>>())
            }
        }

        on("specialized typeToken") {
            it("should give a ParameterizedType") {
                assertTrue(typeToken<List<String>>() is ParameterizedType)
            }
        }

    }

    given("a two parameter Any generic type") {

        on("full wildcard typeToken") {
            it("should give the type's class") {
                assertEquals(Map::class.java, typeToken<Map<*, *>>())
            }
        }

        on("full specialized typeToken") {
            it("should give a ParameterizedType") {
                assertTrue(typeToken<Map<String, String>>() is ParameterizedType)
            }
        }

        on("semi specialized typeToken") {
            it("T-*: should throw an exception") {
                assertFailsWith<IllegalArgumentException> {
                    typeToken<Map<String, *>>()
                }
            }
            it("*-T: should throw an exception") {
                assertFailsWith<IllegalArgumentException> {
                    typeToken<Map<*, String>>()
                }
            }
        }

    }

    given("a one (interface) parameter generic type") {

        on("wildcard typeToken") {
            it("should give the type's class") {
                assertEquals(SingleBase::class.java, typeToken<SingleBase<*>>())
            }
        }

        on("specialized typeToken") {
            it("should give a ParameterizedType") {
                assertTrue(typeToken<SingleBase<Value>>() is ParameterizedType)
            }
        }

    }

    given("a one (class) parameter generic type") {

        on("wildcard typeToken") {
            it("should give the type's class") {
                assertEquals(SingleValue::class.java, typeToken<SingleValue<*>>())
            }
        }

        on("base specialized typeToken") {
            it("should give the type's class too") {
                assertEquals(SingleValue::class.java, typeToken<SingleValue<Value>>())
            }
        }

        on("specialized typeToken") {
            it("should give a ParameterizedType") {
                assertTrue(typeToken<SingleValue<SubValue>>() is ParameterizedType)
            }
        }

    }

    given("a two (Any, interface) parameter generic type") {

        on("full wildcard typeToken") {
            it("should give the type's class") {
                assertEquals(AnyBaseTuple::class.java, typeToken<AnyBaseTuple<*, *>>())
            }
        }

        on("full specialized typeToken") {
            it("should give a ParameterizedType") {
                assertTrue(typeToken<AnyBaseTuple<String, Value>>() is ParameterizedType)
            }
        }

        on("semi specialized typeToken") {
            it("T-*: should give a ParameterizedType") {
                assertTrue(typeToken<AnyBaseTuple<String, *>>() is ParameterizedType)
            }
            it("*-T: should throw an exception") {
                assertFailsWith<IllegalArgumentException> {
                    typeToken<AnyBaseTuple<*, Value>>()
                }
            }
        }

    }

    given("a two (interface, interface) parameter generic type") {

        on("full wildcard typeToken") {
            it("should give the type's class") {
                assertEquals(BaseBaseTuple::class.java, typeToken<BaseBaseTuple<*, *>>())
            }
        }

        on("full specialized typeToken") {
            it("should give a ParameterizedType") {
                assertTrue(typeToken<BaseBaseTuple<Value, Value>>() is ParameterizedType)
            }
        }

        on("semi specialized typeToken") {
            it("T-*: should give a ParameterizedType") {
                assertTrue(typeToken<BaseBaseTuple<Value, *>>() is ParameterizedType)
            }
            it("*-T: should give a ParameterizedType") {
                assertTrue(typeToken<BaseBaseTuple<*, Value>>() is ParameterizedType)
            }
        }

    }

    given("a two (interface, class) parameter generic type") {

        on("full wildcard typeToken") {
            it("should give the type's class") {
                assertEquals(BaseValueTuple::class.java, typeToken<BaseValueTuple<*, *>>())
            }
        }

        on("full specialized typeToken") {
            it("should give a ParameterizedType") {
                assertTrue(typeToken<BaseValueTuple<SubValue, SubValue>>() is ParameterizedType)
            }
        }

        on("semi specialized typeToken") {
            it("T-*: should give a ParameterizedType") {
                assertTrue(typeToken<BaseValueTuple<SubValue, *>>() is ParameterizedType)
            }
            it("*-T: should give a ParameterizedType") {
                assertTrue(typeToken<BaseValueTuple<*, SubValue>>() is ParameterizedType)
            }
        }

    }

    given("a two (class, class) parameter generic type") {

        on("full wildcard typeToken") {
            it("should give the type's class") {
                assertEquals(ValueValueTuple::class.java, typeToken<ValueValueTuple<*, *>>())
            }
        }

        on("full specialized typeToken") {
            it("should give a ParameterizedType") {
                assertTrue(typeToken<ValueValueTuple<SubValue, SubValue>>() is ParameterizedType)
            }
        }

        on("semi specialized typeToken") {
            it("T-*: should give a ParameterizedType") {
                assertTrue(typeToken<ValueValueTuple<SubValue, *>>() is ParameterizedType)
            }
            it("*-T: should give a ParameterizedType") {
                assertTrue(typeToken<ValueValueTuple<*, SubValue>>() is ParameterizedType)
            }
        }

    }

    given("a nested generic parameter type with wildcards") {

        on("type-token-ization") {
            it("should have removed the wildcards") {
                val t = typeToken<List<List<Person>>>()
                t as ParameterizedType
                assertEquals(List::class.java, t.rawType)
                val p1 = t.actualTypeArguments[0]
                p1 as ParameterizedType
                assertEquals(List::class.java, p1.rawType)
                val p2 = p1.actualTypeArguments[0]
                assertEquals(Person::class.java, p2)
            }
        }

        on("deserialization") {
            it("should have understood the generic type") {
                assertEquals("Salomon", Gson().fromJson<List<List<Person>>>("[ [ { name: \"Salomon\", age: 30 } ] ]")[0][0].name)
            }
        }

    }

}) {

    interface Base

    open class Value : Base

    class SubValue : Value()


    class SingleBase<out T : Base>

    class SingleValue<out T : Value>

    class AnyBaseTuple<T : Any, out U : Base>

    class BaseBaseTuple<T : Base, out U : Base>

    class BaseValueTuple<T : Base, out U : Value>

    class ValueValueTuple<T : Value, out U : Value>

}
