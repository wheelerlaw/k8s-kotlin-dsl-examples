import com.fasterxml.jackson.databind.ObjectMapper
import com.fkorotkov.kubernetes.*
import com.fkorotkov.openshift.*
import io.fabric8.kubernetes.api.model.Container
import io.fabric8.kubernetes.api.model.ObjectMeta
import io.fabric8.kubernetes.api.model.PodSpec
import java.util.*

class Label(var key: String, var value: String) {
    constructor(): this("", "")
}

fun ObjectMeta.labels(block: MutableList<Label>.() -> Unit = {}) {
    val labels = LinkedList<Label>()
    labels.addAll(this.labels.map { Label(it.key, it.value) })
    labels.block()
    this.labels = labels.map { Pair(it.key, it.value) }.toMap()
}

fun MutableList<Label>.label(block: Label.() -> Unit = {}) {
    val label = Label()
    label.block()
    this.add(label)
}

//fun MutableList<Label>.label(block: () -> Pair<String, String>){
//
//}

fun MutableList<Label>.label(pair: Pair<String, String>) {
    val label = Label()
    label.key = pair.first
    label.value = pair.second
    this.add(label)
}

fun PodSpec.containers(block: MutableList<Container>.() -> Unit = {}) {
    val containers = ArrayList<Container>()
    containers.addAll(this.containers)
    containers.block()
    this.containers = containers
}

fun MutableList<Container>.container(block: Container.() -> Unit = {}) {
    val container = Container()
    container.block()
    this.add(container)
}

fun main(args: Array<String>) {
    val dc = newDeploymentConfig {
        metadata {
            labels = mapOf(
                "app" to "httpd",
                "test" to "helloWorld"
            )
            labels {
                label {
                    key = "test2"
                    value = "helloWorld2"
                }
                label {
                    "test3" to "helloWorld3"
                }

                label("test4" to "helloWorld4")
            }

            name = "httpd"
        }
        spec {
            replicas = 1
            strategy {
                resources { }
            }
            template {
                metadata {
                    name = "httpd"
                    labels = mapOf("app" to "httpd")
                }
                spec {
                    containers = listOf(
                        newContainer {
                            name = "httpd"
                            image = "hello-world"
                            resources { }
                        }
                    )
                    containers {
                        container {
                            name = "httpd"
                            image = "hello-world"
                        }
                        add(newContainer {
                            name = "httpd"
                            image = "hello-world2"
                            resources { }
                        })
                        add(newContainer {
                            name = "httpd"
                            image = "hello-world3"
                            resources { }
                        })
                    }
                }
            }
        }
    }

    val objectMapper = ObjectMapper()
    objectMapper.writeValue(System.out, dc)

}
