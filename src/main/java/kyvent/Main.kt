package kyvent

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule

fun main(args : Array<String>) {
    //   test1()
    //  test2()
    jacksonTest()

}

fun test1() {
    println("---> single step")
    val cmd : CreateCustomerCmd = CreateCustomerCmd(CommandId(), CustomerId())
    val snapshot : Snapshot<Customer> = Snapshot(Customer(), Version(0))
    val uow = handleCustomerCommands(snapshot, cmd, applyEventOnCustomer)
    println(uow)
}

fun uow2(): CustomerUnitOfWork? {
    val cmd : CreateActivatedCustomerCmd = CreateActivatedCustomerCmd(CommandId(), CustomerId())
    val snapshot : Snapshot<Customer> = Snapshot(Customer(), Version(1))
    return handleCustomerCommands(snapshot, cmd, applyEventOnCustomer)
}

fun jacksonTest() {

    val mapper = ObjectMapper().registerModules(Jdk8Module(), KotlinModule(), JavaTimeModule())
    mapper.enable(SerializationFeature.INDENT_OUTPUT);
    mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    val uow = uow2()
    println("--> uow")
    println(uow)
    val uowAsJson = mapper.writeValueAsString(uow)
    println(uowAsJson)

    val jsonHelper = JsonHelper<CustomerUnitOfWork>(mapper)
    val uowFromJson = jsonHelper.fromJson(uowAsJson, CustomerUnitOfWork::class.java)
    println(uowFromJson)

    println(jsonHelper.fromJsonAt("/events", uowAsJson, List::class.java))

}
