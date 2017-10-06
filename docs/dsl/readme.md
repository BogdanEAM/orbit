# Orbit DSL

The Orbit DSL is a domain specific language for serializing structured
types that cross process boundaries in the Orbit framework.

Users define objects, grain interfaces and other types in `.opf`
files which are then compiled to Java for use at runtime.

The Orbit DSL has similar aims to
[ProtoBuf](https://developers.google.com/protocol-buffers/) in that it
aims to be a fast, efficient and simple way to define a well known
binary format across processes and allow the evolution of types in a
reliable way.
It aims to be close to ProtoBuf at both source and binary level while
allowing types specific to Orbit to be defined easily.

The Orbit DSL aims to avoid the problems of performance and
forwards/backwards compatibility in other formats such as Java
serialization and JSON.

```
package cloud.orbit.examples.dsl;

grain User {
    enum LoginResult {
        OK = 0,
        DENIED = 1
    }

    object LoginRequest {
        String username = 0;
        String password = 1;
    }

    LoginResult login(LoginRequest request = 0);
}
```
This is a simple example of a grain definition. See [examples](examples)
for more information.

