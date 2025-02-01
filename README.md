# Grpc Stock Service
The gRPC server employs a variety of communication types, including Unary, Client Streaming, Server Streaming, and Bidirectional Streaming, to support versatile data interactions. WebFlux is leveraged for efficient data manipulation, persistence, and query handling, ensuring responsive and non-blocking operations. Additionally, the service is backed by Gatling for load testing, ensuring robust performance and scalability under varying traffic conditions.

### Mongo Docker
`docker run -d -p 27017:27017 mongo`

### Gatling
`gradle gatlingRun --simulation GSSSimulation`
