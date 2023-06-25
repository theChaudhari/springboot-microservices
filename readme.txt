- Circuit breaker is one of the pattern used by developer while developing microservices
- Circuit breaker is one of the pattern which helps to manage downstream service failure in a proper manner

# What is Circuit breaker pattern?
this pattern comes into the picture while communicating between services
for exampl we have two srvices : ServiceA and ServiceB
ServiceA is calling to ServiceB, if the ServiceB is down due to some infrastructure outrage. ServiceA is not getting a result and it will hang by throwing an exception and then another request comes and it also faces the some situtation

Life cycle of pattern state
there are 3 main states in circuit breaker pattern
1. closed
2. open
3. half open


CLOSED state
when both service which are interacting are up and running, circuit breaker is closed,
circuit breaker is counting the number of remote API calls continously

OPEN state
as soon as the percentage of failing remote api call is exceding the given threshold, circuit breaker changes its state to OPEN state, caling microservices will fail immediately and an exception will be returned that means flow is interupted

HALF open
after staying at open state for a given period of time, breaker automatically turns its state into HALF open state. in this state only limited number of remote API call are allowed to pass through, if the failing call count is greather than this limited numbers, breaker turn again into OPEN state otherwise it is closed



# Resilience4j
- it is lightweight, easy to use fault tolerant library inspired by netflix hystrix

it provide various features
- circuit breaker - fault tolerance
- rate limiter - block too many request
- time limiter - limit the time while calling remote operation
- retry mechanism - automatically retry for failed opeartion
- cache - store result of costly remote opeartion
- bulkhead - limit number of concurrent request
